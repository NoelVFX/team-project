package use_case.update_topic_confidence;

/**
 * Output boundary for the Update Topic Confidence Use Case.
 */
public interface UpdateTopicConfidenceOutputBoundary {

    /**
     * Prepares the success view.
     *
     * @param outputData the updated topic data
     */
    void prepareSuccessView(UpdateTopicConfidenceOutputData outputData);

    /**
     * Prepares the failure view.
     *
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
