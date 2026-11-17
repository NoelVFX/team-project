package interface_adapter.course_progress;

import java.util.ArrayList;
import java.util.List;

/**
 * The state for the Course Progress View Model.
 */
public class CourseProgressState {

    private String courseCode = "";
    private String courseName = "";
    private String estimatedGrade = "";
    private String currentPercentage = "";
    private String targetGrade = "";
    private String completedWeight = "";
    private List<TopicRow> topics = new ArrayList<>();
    private String error;

    public String getCourseCode() {
        return courseCode;
    }

    public void setCourseCode(String courseCode) {
        this.courseCode = courseCode;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getEstimatedGrade() {
        return estimatedGrade;
    }

    public void setEstimatedGrade(String estimatedGrade) {
        this.estimatedGrade = estimatedGrade;
    }

    public String getCurrentPercentage() {
        return currentPercentage;
    }

    public void setCurrentPercentage(String currentPercentage) {
        this.currentPercentage = currentPercentage;
    }

    public String getTargetGrade() {
        return targetGrade;
    }

    public void setTargetGrade(String targetGrade) {
        this.targetGrade = targetGrade;
    }

    public String getCompletedWeight() {
        return completedWeight;
    }

    public void setCompletedWeight(String completedWeight) {
        this.completedWeight = completedWeight;
    }

    public List<TopicRow> getTopics() {
        return topics;
    }

    public void setTopics(List<TopicRow> topics) {
        this.topics = topics;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
