package use_case.update_topic_confidence;

/**
 * Input data for the Update Topic Confidence Use Case.
 */
public class UpdateTopicConfidenceInputData {

    private final String username;
    private final String courseCode;
    private final String topicName;
    private final String confidence;

    public UpdateTopicConfidenceInputData(String username, String courseCode,
                                          String topicName, String confidence) {
        this.username = username;
        this.courseCode = courseCode;
        this.topicName = topicName;
        this.confidence = confidence;
    }

    String getUsername() {
        return username;
    }

    String getCourseCode() {
        return courseCode;
    }

    String getTopicName() {
        return topicName;
    }

    String getConfidence() {
        return confidence;
    }
}
