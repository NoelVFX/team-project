package use_case.view_courses;

/**
 * Input Boundary for the View Enrolled Courses Use Case.
 */
public interface ViewCoursesInputBoundary {

    /**
     * Loads the user's enrolled courses for the home page.
     * @param inputData the input data
     */
    void execute(ViewCoursesInputData inputData);
}
