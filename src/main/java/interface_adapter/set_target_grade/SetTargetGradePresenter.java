package interface_adapter.set_target_grade;

import interface_adapter.course_progress.CourseProgressState;
import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.TargetMessageFormatter;
import use_case.set_target_grade.SetTargetGradeOutputBoundary;
import use_case.set_target_grade.SetTargetGradeOutputData;

/**
 * The Presenter for the Set Target Grade Use Case.
 */
public class SetTargetGradePresenter implements SetTargetGradeOutputBoundary {

    private final CourseProgressViewModel courseProgressViewModel;

    public SetTargetGradePresenter(CourseProgressViewModel courseProgressViewModel) {
        this.courseProgressViewModel = courseProgressViewModel;
    }

    @Override
    public void prepareSuccessView(SetTargetGradeOutputData response) {
        final CourseProgressState state = courseProgressViewModel.getState();
        state.setTargetGrade(response.getTargetGrade());
        state.setTargetMessage(TargetMessageFormatter.format(
                response.getTargetGrade(),
                response.isCourseCompleted(),
                response.isTargetReached(),
                response.getCurrentPercentage(),
                response.getEstimatedGrade(),
                response.getRequiredAverage(),
                response.getRemainingWeight()));
        state.setTargetGradeError(null);
        courseProgressViewModel.notifyStateChanged();
        courseProgressViewModel.notifyTargetGradeChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final CourseProgressState state = courseProgressViewModel.getState();
        state.setTargetGradeError(error);
        courseProgressViewModel.notifyTargetGradeChanged();
    }
}
