package interface_adapter.course_progress;

import interface_adapter.ViewManagerModel;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import use_case.view_course_progress.ViewCourseProgressOutputData;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ViewCourseProgressPresenterTest {

    private ViewManagerModel viewManagerModel;
    private CourseProgressViewModel courseProgressViewModel;
    private ViewCourseProgressPresenter presenter;

    @BeforeEach
    void setUp() {
        viewManagerModel = new ViewManagerModel();
        courseProgressViewModel = new CourseProgressViewModel();
        presenter = new ViewCourseProgressPresenter(viewManagerModel, courseProgressViewModel, "home");
    }

    @Test
    void successFormatsStateAndSwitchesView() {
        presenter.prepareSuccessView(new ViewCourseProgressOutputData(
                "CSC207", "Software Design", "A-", true, 76.666666, "B", 30,
                Arrays.asList(
                        new ViewCourseProgressOutputData.TopicData("SOLID", "GREEN"),
                        new ViewCourseProgressOutputData.TopicData("Generics", "YELLOW"),
                        new ViewCourseProgressOutputData.TopicData("Clean Architecture", "RED"))));

        final CourseProgressState state = courseProgressViewModel.getState();
        assertEquals("CSC207", state.getCourseCode());
        assertEquals("Software Design", state.getCourseName());
        assertEquals("A-", state.getTargetGrade());
        assertEquals("B", state.getEstimatedGrade());
        assertEquals("76.7%", state.getCurrentPercentage());
        assertEquals("30.0% of the course graded", state.getCompletedWeight());
        assertEquals("Confident", state.getTopics().get(0).getDescription());
        assertEquals("Not so confident", state.getTopics().get(1).getDescription());
        assertEquals("Needs more attention", state.getTopics().get(2).getDescription());
        assertEquals("RED", state.getTopics().get(2).getConfidence());
        assertEquals(CourseProgressViewModel.VIEW_NAME, viewManagerModel.getCurrentViewName());
    }

    @Test
    void successWithoutMarksShowsPlaceholder() {
        presenter.prepareSuccessView(new ViewCourseProgressOutputData(
                "CSC207", "Software Design", "A-", false, 0, "", 0,
                Collections.<ViewCourseProgressOutputData.TopicData>emptyList()));

        final CourseProgressState state = courseProgressViewModel.getState();
        assertEquals("N/A", state.getEstimatedGrade());
        assertEquals("No marks entered yet", state.getCurrentPercentage());
    }

    @Test
    void failureSetsErrorAndStaysOnCurrentView() {
        final String[] shownError = {null};
        courseProgressViewModel.addListener(new CourseProgressViewModelListener() {
            @Override
            public void stateChanged() {
            }

            @Override
            public void loadFailed() {
                shownError[0] = courseProgressViewModel.getState().getError();
            }
        });

        presenter.prepareFailView("You are not enrolled in MAT137.");

        assertEquals("You are not enrolled in MAT137.", shownError[0]);
        assertEquals("", viewManagerModel.getCurrentViewName());
    }

    @Test
    void switchToHomeView() {
        presenter.switchToHomeView();
        assertEquals("home", viewManagerModel.getCurrentViewName());
    }
}
