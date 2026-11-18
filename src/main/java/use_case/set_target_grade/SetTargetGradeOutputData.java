package use_case.set_target_grade;

/**
 * Output Data for the Set Target Grade Use Case.
 */
public class SetTargetGradeOutputData {

    private final String courseCode;
    private final String targetGrade;
    private final int targetMinPercentage;
    private final double remainingWeight;
    private final boolean courseCompleted;
    private final double requiredAverage;
    private final double currentPercentage;
    private final String estimatedGrade;
    private final boolean targetReached;

    /**
     * Creates the output data.
     * @param courseCode the code of the course
     * @param targetGrade the label of the new target grade
     * @param targetMinPercentage the lowest percentage that earns the target grade
     * @param remainingWeight how much of the course grade is still to come, from 0 to 100
     * @param courseCompleted true if every assessment of the course has been entered
     * @param requiredAverage the average needed on the remaining weight to reach the target;
     *                        above 100 if out of reach, 0 or less if already secured,
     *                        and meaningless if the course is completed
     * @param currentPercentage the weighted grade so far; 0 if there are no marks
     * @param estimatedGrade the label of the letter grade so far, or "" if there are no marks
     * @param targetReached true if the grade so far is at least the target grade
     */
    public SetTargetGradeOutputData(String courseCode, String targetGrade, int targetMinPercentage,
                                    double remainingWeight, boolean courseCompleted,
                                    double requiredAverage, double currentPercentage,
                                    String estimatedGrade, boolean targetReached) {
        this.courseCode = courseCode;
        this.targetGrade = targetGrade;
        this.targetMinPercentage = targetMinPercentage;
        this.remainingWeight = remainingWeight;
        this.courseCompleted = courseCompleted;
        this.requiredAverage = requiredAverage;
        this.currentPercentage = currentPercentage;
        this.estimatedGrade = estimatedGrade;
        this.targetReached = targetReached;
    }

    public String getCourseCode() {
        return courseCode;
    }

    public String getTargetGrade() {
        return targetGrade;
    }

    public int getTargetMinPercentage() {
        return targetMinPercentage;
    }

    public double getRemainingWeight() {
        return remainingWeight;
    }

    public boolean isCourseCompleted() {
        return courseCompleted;
    }

    public double getRequiredAverage() {
        return requiredAverage;
    }

    public double getCurrentPercentage() {
        return currentPercentage;
    }

    public String getEstimatedGrade() {
        return estimatedGrade;
    }

    public boolean isTargetReached() {
        return targetReached;
    }
}
