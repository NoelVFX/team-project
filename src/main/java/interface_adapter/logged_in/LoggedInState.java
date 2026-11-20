package interface_adapter.logged_in;

import interface_adapter.view_courses.CourseRow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The State information representing the logged-in user.
 */
public class LoggedInState {
    private String username = "";

    private String password = "";
    private String passwordError;

    // The home page's course list, filled by the View Enrolled Courses use case.
    private List<CourseRow> courses = new ArrayList<>();
    private String courseListMessage = "";

    public LoggedInState(LoggedInState copy) {
        username = copy.username;
        password = copy.password;
        passwordError = copy.passwordError;
        courses = new ArrayList<>(copy.courses);
        courseListMessage = copy.courseListMessage;
    }

    // Because of the previous copy constructor, the default constructor must be explicit.
    public LoggedInState() {

    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public void setPasswordError(String passwordError) {
        this.passwordError = passwordError;
    }

    public String getPasswordError() {
        return passwordError;
    }

    /**
     * Returns the rows of the home page's course list.
     * @return a read-only list of the user's courses, in enrolment order
     */
    public List<CourseRow> getCourses() {
        return Collections.unmodifiableList(courses);
    }

    public void setCourses(List<CourseRow> courses) {
        this.courses = new ArrayList<>(courses);
    }

    public String getCourseListMessage() {
        return courseListMessage;
    }

    public void setCourseListMessage(String courseListMessage) {
        this.courseListMessage = courseListMessage;
    }
}
