package interface_adapter.view_courses;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import interface_adapter.logged_in.LoggedInViewModelListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.view_courses.ViewCoursesOutputData;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ViewCoursesPresenterTest {

    private LoggedInViewModel homeViewModel;
    private ViewCoursesPresenter presenter;
    private int notifications;

    @BeforeEach
    void setUp() {
        homeViewModel = new LoggedInViewModel();
        notifications = 0;
        homeViewModel.addListener(new LoggedInViewModelListener() {
            @Override
            public void stateChanged() {
                notifications++;
            }

            @Override
            public void passwordChanged() {
                // not used by this use case
            }
        });
        presenter = new ViewCoursesPresenter(homeViewModel);
    }

    @Test
    void successFormatsRows() {
        presenter.prepareSuccessView(new ViewCoursesOutputData("Paul", Arrays.asList(
                new ViewCoursesOutputData.CourseSummary("CSC207", "Software Design", "A-",
                        true, 76.666666, "B", false),
                new ViewCoursesOutputData.CourseSummary("MAT137", "Calculus", "B",
                        true, 85, "A", true),
                new ViewCoursesOutputData.CourseSummary("STA247", "Probability", "A",
                        false, 0, "", false))));

        final LoggedInState state = homeViewModel.getState();
        assertEquals("3 enrolled courses", state.getCourseListMessage());
        assertEquals(3, state.getCourses().size());

        final CourseRow csc207 = state.getCourses().get(0);
        assertEquals("CSC207", csc207.getCourseCode());
        assertEquals("Software Design", csc207.getCourseName());
        assertEquals("B (76.7%)", csc207.getEstimatedGrade());
        assertEquals("A-", csc207.getTargetGrade());
        assertEquals("Below target", csc207.getStatus());
        assertEquals("CSC207 - Software Design | Estimated: B (76.7%) | Target: A- | Below target",
                csc207.toString());

        assertEquals("On track", state.getCourses().get(1).getStatus());
        assertEquals("N/A", state.getCourses().get(2).getEstimatedGrade());
        assertEquals("No marks yet", state.getCourses().get(2).getStatus());
        assertEquals(1, notifications);
    }

    @Test
    void successSingleCourseMessage() {
        presenter.prepareSuccessView(new ViewCoursesOutputData("Paul", Collections.singletonList(
                new ViewCoursesOutputData.CourseSummary("CSC207", "Software Design", "A-",
                        false, 0, "", false))));
        assertEquals("1 enrolled course", homeViewModel.getState().getCourseListMessage());
    }

    @Test
    void successNoCoursesShowsHint() {
        presenter.prepareSuccessView(new ViewCoursesOutputData("Paul",
                Collections.<ViewCoursesOutputData.CourseSummary>emptyList()));

        final LoggedInState state = homeViewModel.getState();
        assertTrue(state.getCourses().isEmpty());
        assertEquals(ViewCoursesPresenter.NO_COURSES_MESSAGE, state.getCourseListMessage());
        assertEquals(1, notifications);
    }

    @Test
    void failureClearsListAndShowsError() {
        homeViewModel.getState().setCourses(Collections.singletonList(
                new CourseRow("CSC207", "Software Design", "N/A", "A-", "No marks yet")));

        presenter.prepareFailView("No user is logged in.");

        final LoggedInState state = homeViewModel.getState();
        assertTrue(state.getCourses().isEmpty());
        assertEquals("No user is logged in.", state.getCourseListMessage());
        assertEquals(1, notifications);
    }
}
