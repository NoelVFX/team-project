package use_case.calculate_grade;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Output Data for the Calculate Grade Use Case. It is an immutable data carrier, so its
 * constructor takes every value at once.
 */
public class CalculateGradeOutputData {

    private final String courseCode;
    private final boolean hasMarks;
    private final double currentPercentage;
    private final String estimatedGrade;
    private final double completedWeight;
    private final double remainingWeight;
    private final String targetGrade;
    private final boolean courseCompleted;
    private final boolean targetReached;
    private final double requiredAverage;
    private final List<AssessmentData> assessments;

    /**
     * Creates the output data.
     * @param courseCode the code of the course
     * @param hasMarks false if the course now has no saved assessments
     * @param currentPercentage the weighted grade so far, from 0 to 100; 0 if there are no marks
     * @param estimatedGrade the label of the letter grade so far, or "" if there are no marks
     * @param completedWeight the total weight of the saved assessments, from 0 to 100
     * @param remainingWeight how much of the course grade is still to come, from 0 to 100
     * @param targetGrade the label of the course's target grade
     * @param courseCompleted true if the saved weights add up to 100
     * @param targetReached true if the grade so far is at least the target grade
     * @param requiredAverage the average needed on the remaining weight to reach the target;
     *                        meaningless if the course is completed
     * @param assessments the saved assessments
     */
    public CalculateGradeOutputData(String courseCode, boolean hasMarks, double currentPercentage,
                                    String estimatedGrade, double completedWeight,
                                    double remainingWeight, String targetGrade,
                                    boolean courseCompleted, boolean targetReached,
                                    double requiredAverage, List<AssessmentData> assessments) {
        this.courseCode = courseCode;
        this.hasMarks = hasMarks;
        this.currentPercentage = currentPercentage;
        this.estimatedGrade = estimatedGrade;
        this.completedWeight = completedWeight;
        this.remainingWeight = remainingWeight;
        this.targetGrade = targetGrade;
        this.courseCompleted = courseCompleted;
        this.targetReached = targetReached;
        this.requiredAverage = requiredAverage;
        this.assessments = Collections.unmodifiableList(new ArrayList<>(assessments));
    }

    public String getCourseCode() {
        return courseCode;
    }

    /**
     * Returns whether the course has any saved assessments.
     * @return false if every mark was cleared
     */
    public boolean hasMarks() {
        return hasMarks;
    }

    public double getCurrentPercentage() {
        return currentPercentage;
    }

    public String getEstimatedGrade() {
        return estimatedGrade;
    }

    public double getCompletedWeight() {
        return completedWeight;
    }

    public double getRemainingWeight() {
        return remainingWeight;
    }

    public String getTargetGrade() {
        return targetGrade;
    }

    public boolean isCourseCompleted() {
        return courseCompleted;
    }

    public boolean isTargetReached() {
        return targetReached;
    }

    public double getRequiredAverage() {
        return requiredAverage;
    }

    public List<AssessmentData> getAssessments() {
        return assessments;
    }

    /**
     * One saved assessment: its name, mark and weight.
     */
    public static class AssessmentData {
        private final String name;
        private final double mark;
        private final double weight;

        /**
         * Creates the data for one assessment.
         * @param name the name of the assessment
         * @param mark the mark earned, as a percentage
         * @param weight how much the assessment is worth, as a percentage of the course grade
         */
        public AssessmentData(String name, double mark, double weight) {
            this.name = name;
            this.mark = mark;
            this.weight = weight;
        }

        public String getName() {
            return name;
        }

        public double getMark() {
            return mark;
        }

        public double getWeight() {
            return weight;
        }
    }
}
