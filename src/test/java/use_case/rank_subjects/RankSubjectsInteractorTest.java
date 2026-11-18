package use_case.rank_subjects;

import data_access.InMemoryCourseDataAccessObject;
import entity.ConfidenceLevel;
import entity.Course;
import entity.ConfidenceScoringStrategy;
import entity.LetterGrade;
import entity.StandardConfidenceScoring;
import entity.Topic;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RankSubjectsInteractorTest {

    private static final double DELTA = 1e-9;

    private static Course course(String code, ConfidenceLevel... levels) {
        final Course course = new Course(code, "Some course", LetterGrade.B);
        for (int i = 0; i < levels.length; i++) {
            course.addTopic(new Topic("Topic " + i, levels[i]));
        }
        return course;
    }

    /** Runs the interactor for Paul and returns the ranking, failing the test on a fail view. */
    private static List<RankSubjectsOutputData.SubjectRank> rank(InMemoryCourseDataAccessObject repository) {
        return rank(repository, new StandardConfidenceScoring());
    }

    /** Runs the interactor with the given scoring strategy and returns the ranking. */
    private static List<RankSubjectsOutputData.SubjectRank> rank(InMemoryCourseDataAccessObject repository,
                                                                ConfidenceScoringStrategy scoring) {
        final RankSubjectsOutputData[] result = new RankSubjectsOutputData[1];
        final RankSubjectsOutputBoundary presenter = new RankSubjectsOutputBoundary() {
            @Override
            public void prepareSuccessView(RankSubjectsOutputData outputData) {
                result[0] = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected: " + errorMessage);
            }
        };
        new RankSubjectsInteractor(repository, presenter, scoring)
                .execute(new RankSubjectsInputData("Paul"));
        assertNotNull(result[0], "Success view was not prepared.");
        return result[0].getSubjects();
    }

    @Test
    void successTest() {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        repository.saveCourse("Paul", course("CSC207", ConfidenceLevel.YELLOW, ConfidenceLevel.RED));
        repository.saveCourse("Paul", course("MAT137", ConfidenceLevel.GREEN, ConfidenceLevel.GREEN));
        repository.saveCourse("Paul", course("STA247", ConfidenceLevel.RED));
        repository.saveCourse("Paul", course("mat223", ConfidenceLevel.GREEN, ConfidenceLevel.YELLOW));
        repository.saveCourse("Paul", course("CSC148", ConfidenceLevel.GREEN));

        final List<RankSubjectsOutputData.SubjectRank> ranking = rank(repository);

        assertEquals(3, ranking.size());
        // MAT: (1 + 1 + 1 + 0.5) / 4 = 0.875
        final RankSubjectsOutputData.SubjectRank first = ranking.get(0);
        assertEquals(1, first.getRank());
        assertEquals("MAT", first.getSubjectCode());
        assertEquals(Arrays.asList("MAT137", "mat223"), first.getCourseCodes());
        assertTrue(first.hasTopics());
        assertEquals(0.875, first.getAverageConfidence(), DELTA);
        assertEquals("GREEN", first.getConfidenceLevel());
        assertEquals(3, first.getGreenTopics());
        assertEquals(1, first.getYellowTopics());
        assertEquals(0, first.getRedTopics());
        // CSC: (0.5 + 0 + 1) / 3 = 0.5
        assertEquals("CSC", ranking.get(1).getSubjectCode());
        assertEquals(2, ranking.get(1).getRank());
        assertEquals(0.5, ranking.get(1).getAverageConfidence(), DELTA);
        assertEquals("YELLOW", ranking.get(1).getConfidenceLevel());
        // STA: 0
        assertEquals("STA", ranking.get(2).getSubjectCode());
        assertEquals(3, ranking.get(2).getRank());
        assertEquals(0, ranking.get(2).getAverageConfidence(), DELTA);
        assertEquals("RED", ranking.get(2).getConfidenceLevel());
    }

    @Test
    void tiedSubjectsShareARankTest() {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        repository.saveCourse("Paul", course("PHY131", ConfidenceLevel.GREEN));
        repository.saveCourse("Paul", course("MAT137", ConfidenceLevel.YELLOW));
        repository.saveCourse("Paul", course("CSC207", ConfidenceLevel.GREEN, ConfidenceLevel.RED));
        repository.saveCourse("Paul", course("STA247", ConfidenceLevel.RED));

        final List<RankSubjectsOutputData.SubjectRank> ranking = rank(repository);

        assertEquals("PHY", ranking.get(0).getSubjectCode());
        assertEquals(1, ranking.get(0).getRank());
        // CSC and MAT both average 0.5: same rank, alphabetical order.
        assertEquals("CSC", ranking.get(1).getSubjectCode());
        assertEquals(2, ranking.get(1).getRank());
        assertEquals("MAT", ranking.get(2).getSubjectCode());
        assertEquals(2, ranking.get(2).getRank());
        // The next subject skips the shared place.
        assertEquals("STA", ranking.get(3).getSubjectCode());
        assertEquals(4, ranking.get(3).getRank());
    }

    @Test
    void subjectsWithoutTopicsGoLastTest() {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        repository.saveCourse("Paul", course("ECO101"));
        repository.saveCourse("Paul", course("BIO130"));
        repository.saveCourse("Paul", course("CSC207", ConfidenceLevel.RED));

        final List<RankSubjectsOutputData.SubjectRank> ranking = rank(repository);

        assertEquals("CSC", ranking.get(0).getSubjectCode());
        assertEquals(1, ranking.get(0).getRank());
        assertEquals("BIO", ranking.get(1).getSubjectCode());
        assertFalse(ranking.get(1).hasTopics());
        assertEquals(2, ranking.get(1).getRank());
        assertEquals("ECO", ranking.get(2).getSubjectCode());
        assertEquals(2, ranking.get(2).getRank());
    }

    @Test
    void otherUsersCoursesAreIgnoredTest() {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        repository.saveCourse("Paul", course("CSC207", ConfidenceLevel.GREEN));
        repository.saveCourse("Alice", course("MAT137", ConfidenceLevel.GREEN));

        final List<RankSubjectsOutputData.SubjectRank> ranking = rank(repository);

        assertEquals(1, ranking.size());
        assertEquals("CSC", ranking.get(0).getSubjectCode());
    }

    @Test
    void anotherScoringStrategyChangesTheRankingTest() {
        // A stricter strategy where yellow is worth nothing: only green topics count.
        final ConfidenceScoringStrategy greenOnly = new ConfidenceScoringStrategy() {
            @Override
            public double score(ConfidenceLevel level) {
                return level == ConfidenceLevel.GREEN ? 1 : 0;
            }

            @Override
            public ConfidenceLevel levelFor(double averageScore) {
                return averageScore >= 0.5 ? ConfidenceLevel.GREEN : ConfidenceLevel.RED;
            }
        };
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        repository.saveCourse("Paul", course("MAT137", ConfidenceLevel.YELLOW, ConfidenceLevel.YELLOW));
        repository.saveCourse("Paul", course("CSC207", ConfidenceLevel.GREEN, ConfidenceLevel.RED, ConfidenceLevel.RED));

        // Standard scoring: MAT 0.5 beats CSC 0.33.
        assertEquals("MAT", rank(repository).get(0).getSubjectCode());
        // Green-only scoring: CSC 0.33 beats MAT 0, with no change to the interactor.
        final List<RankSubjectsOutputData.SubjectRank> ranking = rank(repository, greenOnly);
        assertEquals("CSC", ranking.get(0).getSubjectCode());
        assertEquals("RED", ranking.get(0).getConfidenceLevel());
        assertEquals(0, ranking.get(1).getAverageConfidence(), DELTA);
    }

    @Test
    void failureNoCoursesTest() {
        final RankSubjectsOutputBoundary presenter = new RankSubjectsOutputBoundary() {
            @Override
            public void prepareSuccessView(RankSubjectsOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals("You are not enrolled in any courses yet. Enrol in a course first.",
                        errorMessage);
            }
        };
        new RankSubjectsInteractor(new InMemoryCourseDataAccessObject(), presenter,
                new StandardConfidenceScoring())
                .execute(new RankSubjectsInputData("Paul"));
    }
}
