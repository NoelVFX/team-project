package use_case.set_target_grade;

import data_access.InMemoryCourseDataAccessObject;
import entity.Assessment;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class SetTargetGradeInteractorTest {

    private static final double DELTA = 1e-6;

    private InMemoryCourseDataAccessObject repositoryWithCourse(Assessment... assessments) {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        final Course course = new CourseFactory().create("CSC207", "Software Design", LetterGrade.B,
                Collections.singletonList("SOLID"));
        course.setAssessments(Arrays.asList(assessments));
        repository.saveCourse("Paul", course);
        return repository;
    }

    private static SetTargetGradeOutputBoundary expectFailure(String expectedError) {
        return new SetTargetGradeOutputBoundary() {
            @Override
            public void prepareSuccessView(SetTargetGradeOutputData outputData) {
                fail("Use case success is unexpected.");
            }

            @Override
            public void prepareFailView(String errorMessage) {
                assertEquals(expectedError, errorMessage);
            }
        };
    }

    /** Runs the interactor and returns the output data, failing the test on a fail view. */
    private static SetTargetGradeOutputData runSuccess(InMemoryCourseDataAccessObject repository,
                                                       String targetGrade) {
        final SetTargetGradeOutputData[] result = new SetTargetGradeOutputData[1];
        final SetTargetGradeOutputBoundary presenter = new SetTargetGradeOutputBoundary() {
            @Override
            public void prepareSuccessView(SetTargetGradeOutputData outputData) {
                result[0] = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected: " + errorMessage);
            }
        };
        new SetTargetGradeInteractor(repository, presenter)
                .execute(new SetTargetGradeInputData("Paul", "CSC207", targetGrade));
        assertNotNull(result[0], "Success view was not prepared.");
        return result[0];
    }

    @Test
    void successTest() {
        final InMemoryCourseDataAccessObject repository =
                repositoryWithCourse(new Assessment("Midterm", 80, 40));

        final SetTargetGradeOutputData output = runSuccess(repository, " a- ");

        assertEquals("CSC207", output.getCourseCode());
        assertEquals("A-", output.getTargetGrade());
        assertEquals(80, output.getTargetMinPercentage());
        assertEquals(60, output.getRemainingWeight(), DELTA);
        assertFalse(output.isCourseCompleted());
        // (80 * 100 - 80 * 40) / 60 = 80
        assertEquals(80, output.getRequiredAverage(), DELTA);
        assertEquals(LetterGrade.A_MINUS, repository.getCourse("Paul", "CSC207").getTargetGrade());
    }

    @Test
    void noAssessmentsNeedsTargetMinimumTest() {
        final SetTargetGradeOutputData output = runSuccess(repositoryWithCourse(), "A+");
        assertEquals("", output.getEstimatedGrade());
        assertFalse(output.isTargetReached());
        assertEquals(100, output.getRemainingWeight(), DELTA);
        assertEquals(90, output.getRequiredAverage(), DELTA);
    }

    @Test
    void targetOutOfReachTest() {
        final SetTargetGradeOutputData output =
                runSuccess(repositoryWithCourse(new Assessment("Midterm", 40, 50)), "A+");
        // (90 * 100 - 40 * 50) / 50 = 140
        assertEquals(140, output.getRequiredAverage(), DELTA);
    }

    @Test
    void targetAlreadySecuredTest() {
        final SetTargetGradeOutputData output =
                runSuccess(repositoryWithCourse(new Assessment("Midterm", 100, 80)), "C");
        assertTrue(output.getRequiredAverage() <= 0);
        assertTrue(output.isTargetReached());
    }

    @Test
    void courseCompletedTest() {
        final InMemoryCourseDataAccessObject repository =
                repositoryWithCourse(new Assessment("Everything", 75, 100));
        final SetTargetGradeOutputData output = runSuccess(repository, "A");
        assertTrue(output.isCourseCompleted());
        assertEquals(0, output.getRemainingWeight(), DELTA);
        assertEquals(75, output.getCurrentPercentage(), DELTA);
        assertEquals("B", output.getEstimatedGrade());
        assertFalse(output.isTargetReached());
        assertEquals(LetterGrade.A, repository.getCourse("Paul", "CSC207").getTargetGrade());
    }

    @Test
    void failureUnknownGradeTest() {
        final InMemoryCourseDataAccessObject repository = repositoryWithCourse();
        new SetTargetGradeInteractor(repository, expectFailure("Unknown letter grade: E."))
                .execute(new SetTargetGradeInputData("Paul", "CSC207", "E"));
        assertEquals(LetterGrade.B, repository.getCourse("Paul", "CSC207").getTargetGrade());
    }

    @Test
    void failureCourseNotFoundTest() {
        new SetTargetGradeInteractor(repositoryWithCourse(), expectFailure("You are not enrolled in MAT137."))
                .execute(new SetTargetGradeInputData("Paul", "MAT137", "A"));
    }

    @Test
    void failureOtherUsersCourseTest() {
        new SetTargetGradeInteractor(repositoryWithCourse(), expectFailure("You are not enrolled in CSC207."))
                .execute(new SetTargetGradeInputData("Alice", "CSC207", "A"));
    }
}
