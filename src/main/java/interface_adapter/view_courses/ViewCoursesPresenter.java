package interface_adapter.view_courses;

import interface_adapter.logged_in.LoggedInState;
import interface_adapter.logged_in.LoggedInViewModel;
import use_case.view_courses.ViewCoursesOutputBoundary;
import use_case.view_courses.ViewCoursesOutputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * The Presenter for the View Enrolled Courses Use Case. It fills the home page's course list.
 */
public class ViewCoursesPresenter implements ViewCoursesOutputBoundary {

    static final String NO_COURSES_MESSAGE = "You are not enrolled in any courses yet. Press Enrol to add one.";
    static final String NO_MARKS = "No marks yet";
    static final String ON_TRACK = "On track";
    static final String BELOW_TARGET = "Below target";

    // TODO: use HomeViewModel once HomeView replaces LoggedInView.
    private final LoggedInViewModel homeViewModel;

    public ViewCoursesPresenter(LoggedInViewModel homeViewModel) {
        this.homeViewModel = homeViewModel;
    }

    @Override
    public void prepareSuccessView(ViewCoursesOutputData outputData) {
        final List<CourseRow> rows = new ArrayList<>();
        for (ViewCoursesOutputData.CourseSummary course : outputData.getCourses()) {
            rows.add(toRow(course));
        }

        final LoggedInState state = homeViewModel.getState();
        state.setCourses(rows);
        if (rows.isEmpty()) {
            state.setCourseListMessage(NO_COURSES_MESSAGE);
        }
        else {
            state.setCourseListMessage(rows.size() == 1 ? "1 enrolled course"
                    : rows.size() + " enrolled courses");
        }
        homeViewModel.notifyStateChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        final LoggedInState state = homeViewModel.getState();
        state.setCourses(Collections.<CourseRow>emptyList());
        state.setCourseListMessage(errorMessage);
        homeViewModel.notifyStateChanged();
    }

    private static CourseRow toRow(ViewCoursesOutputData.CourseSummary course) {
        final String estimatedGrade;
        final String status;
        if (course.hasMarks()) {
            estimatedGrade = course.getEstimatedGrade() + " ("
                    + String.format(Locale.ROOT, "%.1f%%", course.getCurrentPercentage()) + ")";
            status = course.isTargetReached() ? ON_TRACK : BELOW_TARGET;
        }
        else {
            estimatedGrade = "N/A";
            status = NO_MARKS;
        }
        return new CourseRow(course.getCourseCode(), course.getCourseName(), estimatedGrade,
                course.getTargetGrade(), status);
    }
}
