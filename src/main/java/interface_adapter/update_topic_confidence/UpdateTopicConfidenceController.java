package interface_adapter.update_topic_confidence;

import use_case.update_topic_confidence.UpdateTopicConfidenceInputBoundary;
import use_case.update_topic_confidence.UpdateTopicConfidenceInputData;

/**
 * Controller for the Update Topic Confidence Use Case.
 */
public class UpdateTopicConfidenceController {

    private final UpdateTopicConfidenceInputBoundary updateTopicConfidenceInteractor;

    public UpdateTopicConfidenceController(
            UpdateTopicConfidenceInputBoundary updateTopicConfidenceInteractor) {
        this.updateTopicConfidenceInteractor = updateTopicConfidenceInteractor;
    }

    /**
     * Updates a topic's confidence for a course.
     *
     * @param username the logged-in user
     * @param courseCode the course code
     * @param topicName the topic name
     * @param confidence the selected confidence name
     */
    public void execute(String username, String courseCode,
                        String topicName, String confidence) {
        updateTopicConfidenceInteractor.execute(new UpdateTopicConfidenceInputData(
                username, courseCode, topicName, confidence));
    }
}
