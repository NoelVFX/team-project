package use_case.view_course_progress;

/**
 * The Input Data for the View Course Progress Use Case.
 */
public class ViewCourseProgressInputData {

    private final String username;
    private final String courseCode;

    public ViewCourseProgressInputData(String username, String courseCode) {
        this.username = username;
        this.courseCode = courseCode;
    }

    String getUsername() {
        return username;
    }

    String getCourseCode() {
        return courseCode;
    }
}
