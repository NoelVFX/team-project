package interface_adapter.course_progress;

import use_case.view_course_progress.ViewCourseProgressInputBoundary;
import use_case.view_course_progress.ViewCourseProgressInputData;

/**
 * The controller for the View Course Progress Use Case.
 */
public class ViewCourseProgressController {

    private final ViewCourseProgressInputBoundary viewCourseProgressUseCaseInteractor;

    public ViewCourseProgressController(ViewCourseProgressInputBoundary viewCourseProgressUseCaseInteractor) {
        this.viewCourseProgressUseCaseInteractor = viewCourseProgressUseCaseInteractor;
    }

    /**
     * Executes the View Course Progress Use Case.
     * @param username the username of the user who is logged in
     * @param courseCode the code of the course to open
     */
    public void execute(String username, String courseCode) {
        final ViewCourseProgressInputData inputData = new ViewCourseProgressInputData(username, courseCode);
        viewCourseProgressUseCaseInteractor.execute(inputData);
    }

    /**
     * Executes the "switch to home view" Use Case.
     */
    public void switchToHomeView() {
        viewCourseProgressUseCaseInteractor.switchToHomeView();
    }
}
