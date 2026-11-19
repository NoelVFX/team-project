package use_case.view_courses;

/**
 * The output boundary for the View Enrolled Courses Use Case.
 */
public interface ViewCoursesOutputBoundary {

    /**
     * Prepares the home page's course list.
     * @param outputData the user's courses; the list may be empty
     */
    void prepareSuccessView(ViewCoursesOutputData outputData);

    /**
     * Prepares the failure view, for example when no user is logged in.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
