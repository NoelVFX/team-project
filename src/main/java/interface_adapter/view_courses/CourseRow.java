package interface_adapter.view_courses;

/**
 * The text needed to display one enrolled course in the home page's course list.
 */
public class CourseRow {

    private final String courseCode;
    private final String courseName;
    private final String estimatedGrade;
    private final String targetGrade;
    private final String status;

    /**
     * Creates a course row.
     * @param courseCode the course code, used to open the course's progress page
     * @param courseName the course name
     * @param estimatedGrade the estimated letter grade, or "N/A" if there are no marks
     * @param targetGrade the target letter grade
     * @param status how the estimate compares to the target, such as "On track"
     */
    public CourseRow(String courseCode, String courseName, String estimatedGrade,
                     String targetGrade, String status) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.estimatedGrade = estimatedGrade;
        this.targetGrade = targetGrade;
        this.status = status;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public String getEstimatedGrade() {
        return estimatedGrade;
    }

    public String getTargetGrade() {
        return targetGrade;
    }

    public String getStatus() {
        return status;
    }

    /**
     * Returns the one-line text shown in the home page's course list.
     * @return for example "CSC207 - Software Design | Estimated: B | Target: A- | Below target"
     */
    @Override
    public String toString() {
        return courseCode + " - " + courseName
                + " | Estimated: " + estimatedGrade
                + " | Target: " + targetGrade
                + " | " + status;
    }
}
