package use_case.set_target_grade;

import entity.Course;
import entity.LetterGrade;

import java.util.OptionalDouble;

/**
 * The Set Target Grade Interactor. It changes a course's target grade and reports
 * the average the student needs on the rest of the course to reach it.
 */
public class SetTargetGradeInteractor implements SetTargetGradeInputBoundary {

    private final SetTargetGradeCourseDataAccessInterface courseDataAccessObject;
    private final SetTargetGradeOutputBoundary setTargetGradePresenter;

    /**
     * Creates the interactor.
     * @param courseDataAccessInterface where the course is loaded from and saved to
     * @param setTargetGradeOutputBoundary the presenter to report the result to
     */
    public SetTargetGradeInteractor(
            SetTargetGradeCourseDataAccessInterface courseDataAccessInterface,
            SetTargetGradeOutputBoundary setTargetGradeOutputBoundary) {
        this.courseDataAccessObject = courseDataAccessInterface;
        this.setTargetGradePresenter = setTargetGradeOutputBoundary;
    }

    @Override
    public void execute(SetTargetGradeInputData inputData) {
        final Course course = courseDataAccessObject.getCourse(
                inputData.getUsername(), inputData.getCourseCode());
        if (course == null) {
            setTargetGradePresenter.prepareFailView(
                    "You are not enrolled in " + inputData.getCourseCode() + ".");
            return;
        }

        final LetterGrade targetGrade;
        try {
            targetGrade = LetterGrade.fromLabel(inputData.getTargetGrade());
        }
        catch (IllegalArgumentException ex) {
            setTargetGradePresenter.prepareFailView(ex.getMessage() + ".");
            return;
        }

        course.setTargetGrade(targetGrade);
        courseDataAccessObject.saveCourse(inputData.getUsername(), course);

        final OptionalDouble requiredAverage = course.getRequiredAverageForTarget();
        final OptionalDouble percentage = course.getCurrentPercentage();
        final SetTargetGradeOutputData outputData = new SetTargetGradeOutputData(
                course.getCode(),
                targetGrade.getLabel(),
                targetGrade.getMinPercentage(),
                course.getRemainingWeight(),
                !requiredAverage.isPresent(),
                requiredAverage.orElse(0),
                percentage.orElse(0),
                course.getEstimatedGrade().map(grade -> grade.getLabel()).orElse(""),
                course.isTargetReached());
        setTargetGradePresenter.prepareSuccessView(outputData);
    }
}
