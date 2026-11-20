package use_case.calculate_grade;

import entity.Assessment;
import entity.Course;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.OptionalDouble;
import java.util.Set;

/**
 * The Calculate Grade Interactor. It validates the marks and weights entered in the
 * grade calculator, saves them as the course's assessments and reports the new grade.
 * Submitting no rows clears the course's saved assessments.
 */
public class CalculateGradeInteractor implements CalculateGradeInputBoundary {

    private final CalculateGradeCourseDataAccessInterface courseDataAccessObject;
    private final CalculateGradeOutputBoundary calculateGradePresenter;

    /**
     * Creates the interactor.
     * @param courseDataAccessInterface where the course is loaded from and saved to
     * @param calculateGradeOutputBoundary the presenter to report the result to
     */
    public CalculateGradeInteractor(
            CalculateGradeCourseDataAccessInterface courseDataAccessInterface,
            CalculateGradeOutputBoundary calculateGradeOutputBoundary) {
        this.courseDataAccessObject = courseDataAccessInterface;
        this.calculateGradePresenter = calculateGradeOutputBoundary;
    }

    @Override
    public void execute(CalculateGradeInputData inputData) {
        final Course course = courseDataAccessObject.getCourse(
                inputData.getUsername(), inputData.getCourseCode());
        if (course == null) {
            calculateGradePresenter.prepareFailView(
                    "You are not enrolled in " + inputData.getCourseCode() + ".");
            return;
        }
        if (inputData.getRows().isEmpty() && course.getAssessments().isEmpty()) {
            calculateGradePresenter.prepareFailView("Enter at least one assessment.");
            return;
        }

        try {
            course.setAssessments(toAssessments(inputData.getRows()));
        }
        catch (IllegalArgumentException ex) {
            calculateGradePresenter.prepareFailView(ex.getMessage() + ".");
            return;
        }
        courseDataAccessObject.saveCourse(inputData.getUsername(), course);

        calculateGradePresenter.prepareSuccessView(toOutputData(course));
    }

    /**
     * Turns the rows typed into the calculator into assessments.
     * @throws IllegalArgumentException naming the first invalid row, if any row is invalid
     *                                  or two rows have the same name
     */
    private static List<Assessment> toAssessments(
            List<CalculateGradeInputData.AssessmentRow> rows) {
        final List<Assessment> assessments = new ArrayList<>();
        final Set<String> seenNames = new HashSet<>();
        int rowNumber = 0;
        for (CalculateGradeInputData.AssessmentRow row : rows) {
            rowNumber++;
            final Assessment assessment;
            try {
                assessment = toAssessment(row);
            }
            catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Row " + rowNumber + ": " + ex.getMessage(), ex);
            }
            if (!seenNames.add(assessment.getName().toLowerCase(Locale.ROOT))) {
                throw new IllegalArgumentException("Row " + rowNumber + ": \""
                        + assessment.getName() + "\" is entered more than once");
            }
            assessments.add(assessment);
        }
        return assessments;
    }

    private static Assessment toAssessment(CalculateGradeInputData.AssessmentRow row) {
        final double mark = parseNumber(row.getMark(), "Mark");
        final double weight = parseNumber(row.getWeight(), "Weight");
        return new Assessment(row.getName(), mark, weight);
    }

    private static double parseNumber(String text, String fieldName) {
        String trimmed = text == null ? "" : text.trim();
        // Accept "80%" as well as "80".
        if (trimmed.endsWith("%")) {
            trimmed = trimmed.substring(0, trimmed.length() - 1).trim();
        }
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException(fieldName + " cannot be empty");
        }
        try {
            return Double.parseDouble(trimmed);
        }
        catch (NumberFormatException ex) {
            throw new IllegalArgumentException(fieldName + " must be a number", ex);
        }
    }

    private static CalculateGradeOutputData toOutputData(Course course) {
        final List<CalculateGradeOutputData.AssessmentData> assessments = new ArrayList<>();
        for (Assessment assessment : course.getAssessments()) {
            assessments.add(new CalculateGradeOutputData.AssessmentData(
                    assessment.getName(), assessment.getMark(), assessment.getWeight()));
        }
        final OptionalDouble percentage = course.getCurrentPercentage();
        final OptionalDouble requiredAverage = course.getRequiredAverageForTarget();
        return new CalculateGradeOutputData(
                course.getCode(),
                percentage.isPresent(),
                percentage.orElse(0),
                course.getEstimatedGrade().map(grade -> grade.getLabel()).orElse(""),
                course.getCompletedWeight(),
                course.getRemainingWeight(),
                course.getTargetGrade().getLabel(),
                !requiredAverage.isPresent(),
                course.isTargetReached(),
                requiredAverage.orElse(0),
                assessments);
    }
}
