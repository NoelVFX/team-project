package use_case.view_course_progress;

/**
 * The output boundary for the View Course Progress Use Case.
 */
public interface ViewCourseProgressOutputBoundary {

    /**
     * Prepares the success view for the View Course Progress Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(ViewCourseProgressOutputData outputData);

    /**
     * Prepares the failure view for the View Course Progress Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);

    /**
     * Switches back to the home view.
     */
    void switchToHomeView();
}
