package use_case.view_courses;

/**
 * The Input Data for the View Enrolled Courses Use Case.
 */
public class ViewCoursesInputData {

    private final String username;

    public ViewCoursesInputData(String username) {
        this.username = username;
    }

    String getUsername() {
        return username;
    }
}
