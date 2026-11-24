package use_case.view_course_progress;

import data_access.InMemoryCourseDataAccessObject;
import entity.Assessment;
import entity.ConfidenceLevel;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ViewCourseProgressInteractorTest {

    private static final double DELTA = 1e-6;

    /** A presenter that fails the test whichever method is called; tests override what they expect. */
    private static class UnexpectedPresenter implements ViewCourseProgressOutputBoundary {
        @Override
        public void prepareSuccessView(ViewCourseProgressOutputData outputData) {
            fail("Use case success is unexpected.");
        }

        @Override
        public void prepareFailView(String errorMessage) {
            fail("Use case failure is unexpected.");
        }

        @Override
        public void switchToHomeView() {
            fail("Switching to the home view is unexpected.");
        }
    }

    private InMemoryCourseDataAccessObject repositoryWithCourse() {
        final Course course = new CourseFactory().create("CSC207", "Software Design", LetterGrade.A_MINUS,
                Arrays.asList("SOLID", "Clean Architecture"));
        course.setTopicConfidence("SOLID", ConfidenceLevel.GREEN);
        final InMemoryCourseDataAccessObject courseRepository = new InMemoryCourseDataAccessObject();
        courseRepository.save("Paul", course);
        return courseRepository;
    }

    @Test
    void successWithMarksTest() {
        final InMemoryCourseDataAccessObject courseRepository = repositoryWithCourse();
        courseRepository.getCourse("Paul", "CSC207").setAssessments(Arrays.asList(
                new Assessment("Midterm", 70, 20),
                new Assessment("Assignment", 90, 10)));
        final boolean[] called = {false};

        final ViewCourseProgressOutputBoundary successPresenter = new UnexpectedPresenter() {
            @Override
            public void prepareSuccessView(ViewCourseProgressOutputData outputData) {
                called[0] = true;
                assertEquals("CSC207", outputData.getCourseCode());
                assertEquals("Software Design", outputData.getCourseName());
                assertEquals("A-", outputData.getTargetGrade());
                assertTrue(outputData.hasMarks());
                // (70 * 20 + 90 * 10) / 30
                assertEquals(76.666666, outputData.getCurrentPercentage(), DELTA);
                assertEquals("B", outputData.getEstimatedGrade());
                assertEquals(30, outputData.getCompletedWeight(), DELTA);
                assertEquals(2, outputData.getTopics().size());
                assertEquals("SOLID", outputData.getTopics().get(0).getName());
                assertEquals("GREEN", outputData.getTopics().get(0).getConfidence());
                assertEquals("Clean Architecture", outputData.getTopics().get(1).getName());
                assertEquals("RED", outputData.getTopics().get(1).getConfidence());
                // The saved marks are passed on, so the grade calculator can start from them.
                assertEquals("Paul", outputData.getUsername());
                assertEquals(2, outputData.getAssessments().size());
                assertEquals("Midterm", outputData.getAssessments().get(0).getName());
                assertEquals(70, outputData.getAssessments().get(0).getMark(), DELTA);
                assertEquals(20, outputData.getAssessments().get(0).getWeight(), DELTA);
            }
        };

        final ViewCourseProgressInputBoundary interactor =
                new ViewCourseProgressInteractor(courseRepository, successPresenter);
        // the course code is matched ignoring case and surrounding spaces
        interactor.execute(new ViewCourseProgressInputData("Paul", " csc207 "));
        assertTrue(called[0]);
    }

    @Test
    void successWithoutMarksTest() {
        final boolean[] called = {false};

        final ViewCourseProgressOutputBoundary successPresenter = new UnexpectedPresenter() {
            @Override
            public void prepareSuccessView(ViewCourseProgressOutputData outputData) {
                called[0] = true;
                assertFalse(outputData.hasMarks());
                assertEquals("", outputData.getEstimatedGrade());
                assertEquals(0, outputData.getCompletedWeight(), DELTA);
            }
        };

        final ViewCourseProgressInputBoundary interactor =
                new ViewCourseProgressInteractor(repositoryWithCourse(), successPresenter);
        interactor.execute(new ViewCourseProgressInputData("Paul", "CSC207"));
        assertTrue(called[0]);
    }

    @Test
    void failureNotEnrolledTest() {
        final boolean[] called = {false};

        final ViewCourseProgressOutputBoundary failurePresenter = new UnexpectedPresenter() {
            @Override
            public void prepareFailView(String errorMessage) {
                called[0] = true;
                assertEquals("You are not enrolled in MAT137.", errorMessage);
            }
        };

        final ViewCourseProgressInputBoundary interactor =
                new ViewCourseProgressInteractor(repositoryWithCourse(), failurePresenter);
        interactor.execute(new ViewCourseProgressInputData("Paul", "MAT137"));
        assertTrue(called[0]);
    }

    @Test
    void failureOtherUsersCourseTest() {
        final boolean[] called = {false};

        final ViewCourseProgressOutputBoundary failurePresenter = new UnexpectedPresenter() {
            @Override
            public void prepareFailView(String errorMessage) {
                called[0] = true;
                assertEquals("You are not enrolled in CSC207.", errorMessage);
            }
        };

        final ViewCourseProgressInputBoundary interactor =
                new ViewCourseProgressInteractor(repositoryWithCourse(), failurePresenter);
        interactor.execute(new ViewCourseProgressInputData("Alice", "CSC207"));
        assertTrue(called[0]);
    }

    @Test
    void failureEmptyCourseCodeTest() {
        final boolean[] called = {false};

        final ViewCourseProgressOutputBoundary failurePresenter = new UnexpectedPresenter() {
            @Override
            public void prepareFailView(String errorMessage) {
                called[0] = true;
                assertEquals("Please choose a course.", errorMessage);
            }
        };

        final ViewCourseProgressInputBoundary interactor =
                new ViewCourseProgressInteractor(repositoryWithCourse(), failurePresenter);
        interactor.execute(new ViewCourseProgressInputData("Paul", "  "));
        assertTrue(called[0]);
    }

    @Test
    void switchToHomeViewTest() {
        final boolean[] called = {false};

        final ViewCourseProgressOutputBoundary presenter = new UnexpectedPresenter() {
            @Override
            public void switchToHomeView() {
                called[0] = true;
            }
        };

        final ViewCourseProgressInputBoundary interactor =
                new ViewCourseProgressInteractor(repositoryWithCourse(), presenter);
        interactor.switchToHomeView();
        assertTrue(called[0]);
    }
}
