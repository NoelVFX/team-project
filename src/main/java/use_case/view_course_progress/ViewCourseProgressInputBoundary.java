package use_case.view_course_progress;

/**
 * Input Boundary for actions which are related to viewing a course's learning progress.
 */
public interface ViewCourseProgressInputBoundary {

    /**
     * Executes the view course progress use case.
     * @param inputData the input data
     */
    void execute(ViewCourseProgressInputData inputData);

    /**
     * Executes the switch back to the home view use case.
     */
    void switchToHomeView();
}
