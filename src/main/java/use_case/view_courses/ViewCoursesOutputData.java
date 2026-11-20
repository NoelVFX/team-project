package use_case.view_courses;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Output Data for the View Enrolled Courses Use Case.
 */
public class ViewCoursesOutputData {

    private final String username;
    private final List<CourseSummary> courses;

    /**
     * Creates the output data.
     * @param username the user whose courses these are
     * @param courses one summary per enrolled course, in enrolment order
     */
    public ViewCoursesOutputData(String username, List<CourseSummary> courses) {
        this.username = username;
        this.courses = Collections.unmodifiableList(new ArrayList<>(courses));
    }

    public String getUsername() {
        return username;
    }

    public List<CourseSummary> getCourses() {
        return courses;
    }

    /**
     * One enrolled course, with its estimated and target grade.
     */
    public static class CourseSummary {

        private final String courseCode;
        private final String courseName;
        private final String targetGrade;
        private final boolean hasMarks;
        private final double currentPercentage;
        private final String estimatedGrade;
        private final boolean targetReached;

        /**
         * Creates the summary of one course.
         * @param courseCode the course code
         * @param courseName the course name
         * @param targetGrade the label of the target letter grade, such as "A-"
         * @param hasMarks whether any assessment marks have been entered
         * @param currentPercentage the grade so far as a percentage; 0 if there are no marks
         * @param estimatedGrade the label of the estimated letter grade; empty if there are no marks
         * @param targetReached whether the grade so far reaches the target grade
         */
        public CourseSummary(String courseCode, String courseName, String targetGrade,
                             boolean hasMarks, double currentPercentage, String estimatedGrade,
                             boolean targetReached) {
            this.courseCode = courseCode;
            this.courseName = courseName;
            this.targetGrade = targetGrade;
            this.hasMarks = hasMarks;
            this.currentPercentage = currentPercentage;
            this.estimatedGrade = estimatedGrade;
            this.targetReached = targetReached;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public String getTargetGrade() {
            return targetGrade;
        }

        public boolean hasMarks() {
            return hasMarks;
        }

        public double getCurrentPercentage() {
            return currentPercentage;
        }

        public String getEstimatedGrade() {
            return estimatedGrade;
        }

        public boolean isTargetReached() {
            return targetReached;
        }
    }
}
