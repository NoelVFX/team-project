package use_case.rank_subjects;

import entity.ConfidenceLevel;
import entity.Course;
import entity.Subject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.TreeMap;

/**
 * The Rank Subjects Interactor. It groups the user's courses by subject (for example,
 * every MAT course), and ranks the subjects by the average confidence of their topics,
 * from most to least confident.
 */
public class RankSubjectsInteractor implements RankSubjectsInputBoundary {

    /** Two averages closer than this are treated as a tie. */
    private static final double EPSILON = 1e-9;

    private final RankSubjectsCourseDataAccessInterface courseDataAccessObject;
    private final RankSubjectsOutputBoundary rankSubjectsPresenter;

    public RankSubjectsInteractor(RankSubjectsCourseDataAccessInterface courseDataAccessInterface,
                                  RankSubjectsOutputBoundary rankSubjectsOutputBoundary) {
        this.courseDataAccessObject = courseDataAccessInterface;
        this.rankSubjectsPresenter = rankSubjectsOutputBoundary;
    }

    @Override
    public void execute(RankSubjectsInputData inputData) {
        final List<Course> courses = courseDataAccessObject.getCourses(inputData.getUsername());
        if (courses == null || courses.isEmpty()) {
            rankSubjectsPresenter.prepareFailView(
                    "You are not enrolled in any courses yet. Enrol in a course first.");
            return;
        }

        final List<Subject> subjects = sortByConfidence(groupBySubject(courses));

        final List<RankSubjectsOutputData.SubjectRank> ranking = new ArrayList<>();
        int rank = 0;
        OptionalDouble previous = null;
        for (int i = 0; i < subjects.size(); i++) {
            final Subject subject = subjects.get(i);
            final OptionalDouble average = subject.getAverageConfidence();
            // Tied subjects share a rank; the next subject skips the shared places (1, 2, 2, 4).
            if (previous == null || !sameAverage(previous, average)) {
                rank = i + 1;
            }
            previous = average;
            ranking.add(toSubjectRank(rank, subject));
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
    private static List<Subject> sortByConfidence(List<Subject> subjects) {
        final List<Subject> sorted = new ArrayList<>(subjects);
        sorted.sort(Comparator.comparingDouble(
                (Subject subject) -> subject.getAverageConfidence().orElse(-1)).reversed());
        return sorted;
    }

    private static boolean sameAverage(OptionalDouble first, OptionalDouble second) {
        if (!first.isPresent() || !second.isPresent()) {
            return first.isPresent() == second.isPresent();
        }
        return Math.abs(first.getAsDouble() - second.getAsDouble()) < EPSILON;
    }

    private static RankSubjectsOutputData.SubjectRank toSubjectRank(int rank, Subject subject) {
        final List<String> courseCodes = new ArrayList<>();
        for (Course course : subject.getCourses()) {
            courseCodes.add(course.getCode());
        }
        final OptionalDouble average = subject.getAverageConfidence();
        return new RankSubjectsOutputData.SubjectRank(
                rank,
                subject.getCode(),
                courseCodes,
                average.isPresent(),
                average.orElse(0),
                subject.countTopics(ConfidenceLevel.GREEN),
                subject.countTopics(ConfidenceLevel.YELLOW),
                subject.countTopics(ConfidenceLevel.RED));
    }
}
