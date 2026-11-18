package use_case.set_target_grade;

/**
 * The Input Data for the Set Target Grade Use Case.
 */
public class SetTargetGradeInputData {

    private final String username;
    private final String courseCode;
    private final String targetGrade;

    /**
     * Creates the input data.
     * @param username the user whose course is being changed
     * @param courseCode the code of the course
     * @param targetGrade the label of the new target grade, such as "A-"
     */
    public SetTargetGradeInputData(String username, String courseCode, String targetGrade) {
        this.username = username;
        this.courseCode = courseCode;
        this.targetGrade = targetGrade;
    }

    String getUsername() {
        return username;
    }

    String getCourseCode() {
        return courseCode;
    }

    String getTargetGrade() {
        return targetGrade;
    }
}
