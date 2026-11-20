package interface_adapter.course_progress;

import interface_adapter.ViewManagerModel;
import use_case.view_course_progress.ViewCourseProgressOutputBoundary;
import use_case.view_course_progress.ViewCourseProgressOutputData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The Presenter for the View Course Progress Use Case.
 */
public class ViewCourseProgressPresenter implements ViewCourseProgressOutputBoundary {

    private final ViewManagerModel viewManagerModel;
    private final CourseProgressViewModel courseProgressViewModel;
    private final String homeViewName;

    /**
     * Creates the presenter.
     * @param viewManagerModel the model used to switch between views
     * @param courseProgressViewModel the view model of the course progress page
     * @param homeViewName the name of the view to return to from the course progress page
     */
    public ViewCourseProgressPresenter(ViewManagerModel viewManagerModel,
                                       CourseProgressViewModel courseProgressViewModel,
                                       String homeViewName) {
        this.viewManagerModel = viewManagerModel;
        this.courseProgressViewModel = courseProgressViewModel;
        this.homeViewName = homeViewName;
    }

    @Override
    public void prepareSuccessView(ViewCourseProgressOutputData outputData) {
        final CourseProgressState state = new CourseProgressState();
        state.setUsername(outputData.getUsername());
        state.setCourseCode(outputData.getCourseCode());
        state.setCourseName(outputData.getCourseName());
        state.setTargetGrade(outputData.getTargetGrade());
        state.setCompletedWeight(formatPercentage(outputData.getCompletedWeight()) + " of the course graded");

        if (outputData.hasMarks()) {
            state.setEstimatedGrade(outputData.getEstimatedGrade());
            state.setCurrentPercentage(formatPercentage(outputData.getCurrentPercentage()));
        }
        else {
            state.setEstimatedGrade("N/A");
            state.setCurrentPercentage("No marks entered yet");
        }

        final List<TopicRow> topics = new ArrayList<>();
        for (ViewCourseProgressOutputData.TopicData topic : outputData.getTopics()) {
            topics.add(new TopicRow(topic.getName(), topic.getConfidence(),
                    describeConfidence(topic.getConfidence())));
        }
        state.setTopics(topics);

        courseProgressViewModel.setState(state);
        courseProgressViewModel.notifyStateChanged();

        viewManagerModel.setState(CourseProgressViewModel.VIEW_NAME);
        viewManagerModel.notifyViewChanged();
    }

    @Override
    public void prepareFailView(String errorMessage) {
        courseProgressViewModel.getState().setError(errorMessage);
        courseProgressViewModel.notifyLoadFailed();
    }

    @Override
    public void switchToHomeView() {
        viewManagerModel.setState(homeViewName);
        viewManagerModel.notifyViewChanged();
    }

    private String formatPercentage(double percentage) {
        return String.format(Locale.ROOT, "%.1f%%", percentage);
    }

    private String describeConfidence(String confidence) {
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
