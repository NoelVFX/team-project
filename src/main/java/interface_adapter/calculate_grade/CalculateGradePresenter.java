package interface_adapter.calculate_grade;

import interface_adapter.course_progress.AssessmentRow;
import interface_adapter.course_progress.CourseProgressState;
import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.TargetMessageFormatter;
import use_case.calculate_grade.CalculateGradeOutputBoundary;
import use_case.calculate_grade.CalculateGradeOutputData;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * The Presenter for the Calculate Grade Use Case. It shows the result in the calculator
 * pop-up and updates the course progress page: estimated grade, current percentage,
 * graded weight, target message and saved assessments.
 */
public class CalculateGradePresenter implements CalculateGradeOutputBoundary {

    /** Shown as the estimated grade when no marks have been entered. */
    public static final String NO_GRADE = "N/A";
    /** Shown as the current percentage when no marks have been entered. */
    public static final String NO_MARKS = "No marks entered yet";

    private final GradeCalculatorViewModel gradeCalculatorViewModel;
    private final CourseProgressViewModel courseProgressViewModel;

    /**
     * Creates the presenter.
     * @param gradeCalculatorViewModel the view model of the calculator pop-up
     * @param courseProgressViewModel the view model of the course progress page
     */
    public CalculateGradePresenter(GradeCalculatorViewModel gradeCalculatorViewModel,
                                   CourseProgressViewModel courseProgressViewModel) {
        this.gradeCalculatorViewModel = gradeCalculatorViewModel;
        this.courseProgressViewModel = courseProgressViewModel;
    }

    @Override
    public void prepareSuccessView(CalculateGradeOutputData response) {
        // Update the progress page first, so the calculator can read the saved rows from it.
        updateProgressPage(response);

        final GradeCalculatorState calculatorState = gradeCalculatorViewModel.getState();
        if (response.hasMarks()) {
            calculatorState.setResultMessage("Current grade: "
                    + TargetMessageFormatter.formatNumber(response.getCurrentPercentage()) + "% ("
                    + response.getEstimatedGrade() + "), based on "
                    + TargetMessageFormatter.formatNumber(response.getCompletedWeight())
                    + "% of the course.");
        }
        else {
            calculatorState.setResultMessage("All saved marks were cleared.");
        }
        calculatorState.setCalculateError(null);
        gradeCalculatorViewModel.notifyStateChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final GradeCalculatorState calculatorState = gradeCalculatorViewModel.getState();
        calculatorState.setCalculateError(error);
        gradeCalculatorViewModel.notifyStateChanged();
    }

    private void updateProgressPage(CalculateGradeOutputData response) {
        final CourseProgressState progressState = courseProgressViewModel.getState();
        progressState.setAssessments(toRows(response.getAssessments()));
        if (response.hasMarks()) {
            progressState.setEstimatedGrade(response.getEstimatedGrade());
            progressState.setCurrentPercentage(formatPercentage(response.getCurrentPercentage()));
        }
        else {
            progressState.setEstimatedGrade(NO_GRADE);
            progressState.setCurrentPercentage(NO_MARKS);
        }
        progressState.setCompletedWeight(
                formatPercentage(response.getCompletedWeight()) + " of the course graded");
        progressState.setTargetMessage(TargetMessageFormatter.format(
                response.getTargetGrade(),
                response.isCourseCompleted(),
                response.isTargetReached(),
                response.getCurrentPercentage(),
                response.getEstimatedGrade(),
                response.getRequiredAverage(),
                response.getRemainingWeight()));
        courseProgressViewModel.notifyStateChanged();
    }

    private static List<AssessmentRow> toRows(
            List<CalculateGradeOutputData.AssessmentData> assessments) {
        final List<AssessmentRow> rows = new ArrayList<>();
        for (CalculateGradeOutputData.AssessmentData assessment : assessments) {
            rows.add(new AssessmentRow(
                    assessment.getName(),
                    TargetMessageFormatter.formatNumber(assessment.getMark()),
                    TargetMessageFormatter.formatNumber(assessment.getWeight())));
        }
        return rows;
    }

    /**
     * Formats a percentage the same way the View Course Progress presenter does, such as "83.6%",
     * so the progress page looks the same whichever use case updated it last.
     */
    private static String formatPercentage(double percentage) {
        return String.format(Locale.ROOT, "%.1f%%", percentage);
    }
}
