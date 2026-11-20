package interface_adapter.view_courses;

import use_case.view_courses.ViewCoursesInputBoundary;
import use_case.view_courses.ViewCoursesInputData;

/**
 * The controller for the View Enrolled Courses Use Case.
 */
public class ViewCoursesController {

    private final ViewCoursesInputBoundary viewCoursesUseCaseInteractor;

    public ViewCoursesController(ViewCoursesInputBoundary viewCoursesUseCaseInteractor) {
        this.viewCoursesUseCaseInteractor = viewCoursesUseCaseInteractor;
    }

    /**
     * Executes the View Enrolled Courses Use Case.
     * @param username the username of the user who is logged in
     */
    public void execute(String username) {
        viewCoursesUseCaseInteractor.execute(new ViewCoursesInputData(username));
    }
}
