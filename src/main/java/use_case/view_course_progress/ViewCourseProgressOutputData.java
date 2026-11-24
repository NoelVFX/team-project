package use_case.view_course_progress;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Output Data for the View Course Progress Use Case.
 */
public class ViewCourseProgressOutputData {

    private final String username;
    private final String courseCode;
    private final String courseName;
    private final String targetGrade;
    private final boolean hasMarks;
    private final double currentPercentage;
    private final String estimatedGrade;
    private final double completedWeight;
    private final List<TopicData> topics;
    private final List<AssessmentData> assessments;

    /**
     * Creates the output data for a course.
     * @param courseCode the course code
     * @param courseName the course name
     * @param targetGrade the label of the target letter grade, such as "A-"
     * @param hasMarks whether any assessment marks have been entered for the course
     * @param currentPercentage the grade so far as a percentage; 0 if there are no marks
     * @param estimatedGrade the label of the estimated letter grade; empty if there are no marks
     * @param completedWeight how much of the course grade has been completed, from 0 to 100
     * @param topics the topics of the course, in order
     */
    public ViewCourseProgressOutputData(String courseCode, String courseName, String targetGrade,
                                        boolean hasMarks, double currentPercentage,
                                        String estimatedGrade, double completedWeight,
                                        List<TopicData> topics) {
        this("", courseCode, courseName, targetGrade, hasMarks, currentPercentage,
                estimatedGrade, completedWeight, topics);
    }

    /**
     * Creates output data while preserving the logged-in user for later actions on the page.
     * @param username the logged-in user
     * @param courseCode the course code
     * @param courseName the course name
     * @param targetGrade the label of the target letter grade, such as "A-"
     * @param hasMarks whether any assessment marks have been entered for the course
     * @param currentPercentage the grade so far as a percentage; 0 if there are no marks
     * @param estimatedGrade the label of the estimated letter grade; empty if there are no marks
     * @param completedWeight how much of the course grade has been completed, from 0 to 100
     * @param topics the topics of the course, in order
     */
    public ViewCourseProgressOutputData(String username, String courseCode, String courseName,
                                        String targetGrade, boolean hasMarks,
                                        double currentPercentage, String estimatedGrade,
                                        double completedWeight, List<TopicData> topics) {
        this(username, courseCode, courseName, targetGrade, hasMarks, currentPercentage,
                estimatedGrade, completedWeight, topics, Collections.emptyList());
    }

    /**
     * Creates output data that also carries the course's saved assessments, so the grade
     * calculator can start from them.
     * @param username the logged-in user
     * @param courseCode the course code
     * @param courseName the course name
     * @param targetGrade the label of the target letter grade, such as "A-"
     * @param hasMarks whether any assessment marks have been entered for the course
     * @param currentPercentage the grade so far as a percentage; 0 if there are no marks
     * @param estimatedGrade the label of the estimated letter grade; empty if there are no marks
     * @param completedWeight how much of the course grade has been completed, from 0 to 100
     * @param topics the topics of the course, in order
     * @param assessments the saved assessments of the course, in order
     */
    public ViewCourseProgressOutputData(String username, String courseCode, String courseName,
                                        String targetGrade, boolean hasMarks,
                                        double currentPercentage, String estimatedGrade,
                                        double completedWeight, List<TopicData> topics,
                                        List<AssessmentData> assessments) {
        this.username = username;
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.targetGrade = targetGrade;
        this.hasMarks = hasMarks;
        this.currentPercentage = currentPercentage;
        this.estimatedGrade = estimatedGrade;
        this.completedWeight = completedWeight;
        this.topics = Collections.unmodifiableList(new ArrayList<>(topics));
        this.assessments = Collections.unmodifiableList(new ArrayList<>(assessments));
    }

    public String getUsername() {
        return username;
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

    public double getCompletedWeight() {
        return completedWeight;
    }

    public List<TopicData> getTopics() {
        return topics;
    }

    public List<AssessmentData> getAssessments() {
        return assessments;
    }

    /**
     * One saved assessment of the course: its name, mark and weight.
     */
    public static class AssessmentData {

        private final String name;
        private final double mark;
        private final double weight;

        /**
         * Creates the output data for an assessment.
         * @param name the name of the assessment
         * @param mark the mark earned, as a percentage
         * @param weight how much the assessment is worth, as a percentage of the course grade
         */
        public AssessmentData(String name, double mark, double weight) {
            this.name = name;
            this.mark = mark;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public double getMark() {
            return mark;
        }

        public double getWeight() {
            return weight;
        }
    }

    /**
     * One topic of the course and the student's confidence in it.
     */
    public static class TopicData {

        private final String name;
        private final String confidence;

        /**
         * Creates the output data for a topic.
         * @param name the name of the topic
         * @param confidence the name of the confidence level, such as "GREEN"
         */
        public TopicData(String name, String confidence) {
            this.name = name;
            this.confidence = confidence;
        }

        public String getName() {
            return name;
        }

        public String getConfidence() {
            return confidence;
        }
    }
}
