package use_case.rank_subjects;

import entity.ConfidenceLevel;
import entity.ConfidenceScoringStrategy;
import entity.Course;
import entity.Subject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;

/**
 * The Rank Subjects Interactor. It groups the user's courses by subject (for example,
 * every MAT course), and ranks the subjects by the average confidence of their topics,
 * from most to least confident. How confidence is scored is decided by the
 * {@link ConfidenceScoringStrategy} it is given.
 */
public class RankSubjectsInteractor implements RankSubjectsInputBoundary {

    /** Two averages closer than this are treated as a tie. */
    private static final double EPSILON = 1e-9;

    private final RankSubjectsCourseDataAccessInterface courseDataAccessObject;
    private final RankSubjectsOutputBoundary rankSubjectsPresenter;
    private final ConfidenceScoringStrategy scoring;

    /**
     * Creates the interactor.
     * @param courseDataAccessInterface where the user's courses are loaded from
     * @param rankSubjectsOutputBoundary the presenter to report the ranking to
     * @param scoring how topic confidence is turned into scores and levels
     */
    public RankSubjectsInteractor(RankSubjectsCourseDataAccessInterface courseDataAccessInterface,
                                  RankSubjectsOutputBoundary rankSubjectsOutputBoundary,
                                  ConfidenceScoringStrategy scoring) {
        this.courseDataAccessObject = courseDataAccessInterface;
        this.rankSubjectsPresenter = rankSubjectsOutputBoundary;
        this.scoring = scoring;
    }

    @Override
    public void execute(RankSubjectsInputData inputData) {
        final List<Course> courses = courseDataAccessObject.getCourses(inputData.getUsername());
        if (courses == null || courses.isEmpty()) {
            rankSubjectsPresenter.prepareFailView(
                    "You are not enrolled in any courses yet. Enrol in a course first.");
            return;
        }

        final List<Subject> subjects = sortByConfidence(groupBySubject(courses), scoring);

        final List<RankSubjectsOutputData.SubjectRank> ranking = new ArrayList<>();
        int rank = 0;
        OptionalDouble previous = null;
        for (int i = 0; i < subjects.size(); i++) {
            final Subject subject = subjects.get(i);
            final OptionalDouble average = subject.getAverageConfidence(scoring);
            // Tied subjects share a rank; the next subject skips the shared places (1, 2, 2, 4).
            if (previous == null || !sameAverage(previous, average)) {
                rank = i + 1;
            }
            previous = average;
            ranking.add(toSubjectRank(rank, subject, scoring));
        }
        rankSubjectsPresenter.prepareSuccessView(new RankSubjectsOutputData(ranking));
    }

    /**
     * Groups the courses by subject code, with the subjects in alphabetical order.
     */
    private static List<Subject> groupBySubject(List<Course> courses) {
        final Map<String, Subject> subjectsByCode = new TreeMap<>();
        for (Course course : courses) {
            final String code = course.getSubjectCode();
            subjectsByCode.computeIfAbsent(code, Subject::new).addCourse(course);
        }
        return new ArrayList<>(subjectsByCode.values());
    }

    /**
     * Sorts the subjects from most to least confident. Subjects without topics go last.
     * The sort is stable, so tied subjects stay in alphabetical order.
     */
    private static List<Subject> sortByConfidence(List<Subject> subjects,
                                                  ConfidenceScoringStrategy scoring) {
        final List<Subject> sorted = new ArrayList<>(subjects);
        sorted.sort(Comparator.comparingDouble(
                (Subject subject) -> subject.getAverageConfidence(scoring).orElse(-1)).reversed());
        return sorted;
    }

    private static boolean sameAverage(OptionalDouble first, OptionalDouble second) {
        if (!first.isPresent() || !second.isPresent()) {
            return first.isPresent() == second.isPresent();
        }
        return Math.abs(first.getAsDouble() - second.getAsDouble()) < EPSILON;
    }

    private static RankSubjectsOutputData.SubjectRank toSubjectRank(
            int rank, Subject subject, ConfidenceScoringStrategy scoring) {
        final List<String> courseCodes = new ArrayList<>();
        for (Course course : subject.getCourses()) {
            courseCodes.add(course.getCode());
        }
        final OptionalDouble average = subject.getAverageConfidence(scoring);
        final Optional<ConfidenceLevel> level = subject.getOverallConfidence(scoring);
        return new RankSubjectsOutputData.SubjectRank(
                rank,
                subject.getCode(),
                courseCodes,
                average.orElse(0),
                level.map(ConfidenceLevel::getName).orElse(""),
                subject.countTopics(ConfidenceLevel.GREEN),
                subject.countTopics(ConfidenceLevel.YELLOW),
                subject.countTopics(ConfidenceLevel.RED));
    }
}
