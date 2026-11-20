package interface_adapter.update_topic_confidence;

import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.TopicRow;
import use_case.update_topic_confidence.UpdateTopicConfidenceOutputBoundary;
import use_case.update_topic_confidence.UpdateTopicConfidenceOutputData;

import java.util.ArrayList;
import java.util.List;

/**
 * Presenter for the Update Topic Confidence Use Case.
 */
public class UpdateTopicConfidencePresenter implements UpdateTopicConfidenceOutputBoundary {

    private final CourseProgressViewModel courseProgressViewModel;

    public UpdateTopicConfidencePresenter(CourseProgressViewModel courseProgressViewModel) {
        this.courseProgressViewModel = courseProgressViewModel;
    }

    @Override
    public void prepareSuccessView(UpdateTopicConfidenceOutputData outputData) {
        final List<TopicRow> updatedTopics = new ArrayList<>();
        for (TopicRow topic
                : courseProgressViewModel.getState().getTopics()) {
            if (topic.getName().equalsIgnoreCase(outputData.getTopicName())) {
                updatedTopics.add(new TopicRow(
                        topic.getName(),
                        outputData.getConfidence(),
                        describeConfidence(outputData.getConfidence())));
            }
            else {
                updatedTopics.add(topic);
            }
        }

        courseProgressViewModel.getState().setTopics(updatedTopics);
        courseProgressViewModel.getState().setError(null);
        courseProgressViewModel.notifyStateChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        courseProgressViewModel.getState().setError(errorMessage);
        courseProgressViewModel.notifyLoadFailed();
    }

    private static String describeConfidence(String confidence) {
        final String description;
        if ("GREEN".equals(confidence)) {
            description = "Confident";
        }
        else if ("YELLOW".equals(confidence)) {
            description = "Not so confident";
        }
        else {
            description = "Needs more attention";
        }
        return description;
    }
}
