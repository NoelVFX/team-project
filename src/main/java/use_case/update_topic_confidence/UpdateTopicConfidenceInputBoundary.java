package use_case.update_topic_confidence;

/**
 * Input boundary for the Update Topic Confidence Use Case.
 */
public interface UpdateTopicConfidenceInputBoundary {

    /**
     * Updates a topic's confidence level.
     *
     * @param inputData the requested update
     */
    void execute(UpdateTopicConfidenceInputData inputData);
}
