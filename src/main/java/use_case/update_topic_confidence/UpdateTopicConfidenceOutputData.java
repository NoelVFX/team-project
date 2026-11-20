package use_case.update_topic_confidence;

/**
 * Output data for a successful Update Topic Confidence Use Case.
 */
public class UpdateTopicConfidenceOutputData {

    private final String username;
    private final String courseCode;
    private final String topicName;
    private final String confidence;

    public UpdateTopicConfidenceOutputData(String username, String courseCode,
                                           String topicName, String confidence) {
        this.username = username;
        this.courseCode = courseCode;
        this.topicName = topicName;
        this.confidence = confidence;
    }

    public String getUsername() {
        return username;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTopicName() {
        return topicName;
    }

    public String getConfidence() {
        return confidence;
    }
}
