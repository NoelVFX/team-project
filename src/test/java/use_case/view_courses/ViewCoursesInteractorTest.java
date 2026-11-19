package use_case.view_courses;

import data_access.InMemoryCourseDataAccessObject;
import entity.Assessment;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

class ViewCoursesInteractorTest {

    private static final double DELTA = 1e-6;

    /** A presenter that fails the test whichever method is called; tests override what they expect. */
    private static class UnexpectedPresenter implements ViewCoursesOutputBoundary {
        @Override
        public void prepareSuccessView(ViewCoursesOutputData outputData) {
            fail("Use case success is unexpected.");
        }

        @Override
        public void prepareFailView(String errorMessage) {
            fail("Use case failure is unexpected.");
        }
    }

    private InMemoryCourseDataAccessObject repositoryWithCourses() {
        final CourseFactory factory = new CourseFactory();
        final Course csc207 = factory.create("CSC207", "Software Design", LetterGrade.A_MINUS,
                Arrays.asList("SOLID", "Clean Architecture"));
        csc207.setAssessments(Arrays.asList(
                new Assessment("Midterm", 70, 20),
                new Assessment("Assignment", 90, 10)));
        final Course mat137 = factory.create("MAT137", "Calculus", LetterGrade.B,
                Collections.singletonList("Limits"));
        mat137.setAssessments(Collections.singletonList(new Assessment("Problem Set 1", 85, 10)));
        final Course sta247 = factory.create("STA247", "Probability", LetterGrade.A,
                Collections.singletonList("Random Variables"));

        final InMemoryCourseDataAccessObject courseRepository = new InMemoryCourseDataAccessObject();
        courseRepository.saveCourse("Paul", csc207);
        courseRepository.saveCourse("Paul", mat137);
        courseRepository.saveCourse("Paul", sta247);
        courseRepository.saveCourse("Alice", factory.create("ECO101", "Economics", LetterGrade.B,
                Collections.singletonList("Supply")));
        return courseRepository;
    }

    @Test
    void successListsCoursesInEnrolmentOrderTest() {
        final boolean[] called = {false};

        final ViewCoursesOutputBoundary successPresenter = new UnexpectedPresenter() {
            @Override
            public void prepareSuccessView(ViewCoursesOutputData outputData) {
                called[0] = true;
                assertEquals("Paul", outputData.getUsername());
                final List<ViewCoursesOutputData.CourseSummary> courses = outputData.getCourses();
                assertEquals(3, courses.size());

                final ViewCoursesOutputData.CourseSummary csc207 = courses.get(0);
                assertEquals("CSC207", csc207.getCourseCode());
                assertEquals("Software Design", csc207.getCourseName());
                assertEquals("A-", csc207.getTargetGrade());
                assertTrue(csc207.hasMarks());
                // (70 * 20 + 90 * 10) / 30
                assertEquals(76.666666, csc207.getCurrentPercentage(), DELTA);
                assertEquals("B", csc207.getEstimatedGrade());
                assertFalse(csc207.isTargetReached());

                final ViewCoursesOutputData.CourseSummary mat137 = courses.get(1);
                assertEquals("MAT137", mat137.getCourseCode());
                assertEquals("A", mat137.getEstimatedGrade());
                assertTrue(mat137.isTargetReached());

                final ViewCoursesOutputData.CourseSummary sta247 = courses.get(2);
                assertEquals("STA247", sta247.getCourseCode());
                assertFalse(sta247.hasMarks());
                assertEquals("", sta247.getEstimatedGrade());
                assertFalse(sta247.isTargetReached());
            }
        };

        final ViewCoursesInputBoundary interactor =
                new ViewCoursesInteractor(repositoryWithCourses(), successPresenter);
        interactor.execute(new ViewCoursesInputData("Paul"));
        assertTrue(called[0]);
    }

    @Test
    void successOnlyShowsOwnCoursesTest() {
        final boolean[] called = {false};

        final ViewCoursesOutputBoundary successPresenter = new UnexpectedPresenter() {
            @Override
            public void prepareSuccessView(ViewCoursesOutputData outputData) {
                called[0] = true;
                assertEquals(1, outputData.getCourses().size());
                assertEquals("ECO101", outputData.getCourses().get(0).getCourseCode());
            }
        };

        final ViewCoursesInputBoundary interactor =
                new ViewCoursesInteractor(repositoryWithCourses(), successPresenter);
        interactor.execute(new ViewCoursesInputData("Alice"));
        assertTrue(called[0]);
    }

    @Test
    void successNoCoursesTest() {
        final boolean[] called = {false};

        final ViewCoursesOutputBoundary successPresenter = new UnexpectedPresenter() {
            @Override
            public void prepareSuccessView(ViewCoursesOutputData outputData) {
                called[0] = true;
                assertTrue(outputData.getCourses().isEmpty());
            }
        };

        final ViewCoursesInputBoundary interactor =
                new ViewCoursesInteractor(new InMemoryCourseDataAccessObject(), successPresenter);
        interactor.execute(new ViewCoursesInputData("Paul"));
        assertTrue(called[0]);
    }

    @Test
    void failureNoUserTest() {
        final boolean[] called = {false};

        final ViewCoursesOutputBoundary failurePresenter = new UnexpectedPresenter() {
            @Override
            public void prepareFailView(String errorMessage) {
                called[0] = true;
                assertEquals("No user is logged in.", errorMessage);
            }
        };

        final ViewCoursesInputBoundary interactor =
                new ViewCoursesInteractor(repositoryWithCourses(), failurePresenter);
        interactor.execute(new ViewCoursesInputData("  "));
        assertTrue(called[0]);
    }
}
