package interface_adapter.update_topic_confidence;

import interface_adapter.course_progress.CourseProgressState;
import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.CourseProgressViewModelListener;
import interface_adapter.course_progress.TopicRow;
import org.junit.jupiter.api.Test;
import use_case.update_topic_confidence.UpdateTopicConfidenceOutputData;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class UpdateTopicConfidencePresenterTest {

    @Test
    void successRefreshesChangedTopicInProgressState() {
        final CourseProgressViewModel viewModel = new CourseProgressViewModel();
        final CourseProgressState state = viewModel.getState();
        state.setTopics(Arrays.asList(
                new TopicRow("SOLID", "RED", "Needs more attention"),
                new TopicRow("Clean Architecture", "YELLOW", "Not so confident")));
        final boolean[] notified = {false};
        viewModel.addListener(new CourseProgressViewModelListener() {
            @Override
            public void stateChanged() {
                notified[0] = true;
            }

            @Override
            public void loadFailed() {
            }

            @Override
            public void targetGradeChanged() {
            }
        });

        final UpdateTopicConfidencePresenter presenter =
                new UpdateTopicConfidencePresenter(viewModel);
        presenter.prepareSuccessView(new UpdateTopicConfidenceOutputData(
                "Mia", "CSC207", "Clean Architecture", "GREEN"));

        assertEquals("GREEN", viewModel.getState().getTopics().get(1).getConfidence());
        assertEquals("Confident", viewModel.getState().getTopics().get(1).getDescription());
        assertEquals("RED", viewModel.getState().getTopics().get(0).getConfidence());
        assertTrue(notified[0]);
    }

    @Test
    void failureNotifiesProgressViewWithError() {
        final CourseProgressViewModel viewModel = new CourseProgressViewModel();
        final String[] error = {null};
        viewModel.addListener(new CourseProgressViewModelListener() {
            @Override
            public void stateChanged() {
            }

            @Override
            public void loadFailed() {
                error[0] = viewModel.getState().getError();
            }

            @Override
            public void targetGradeChanged() {
            }
        });

        final UpdateTopicConfidencePresenter presenter =
                new UpdateTopicConfidencePresenter(viewModel);
        presenter.prepareFailView("No such topic: Databases.");

        assertEquals("No such topic: Databases.", error[0]);
    }
}
