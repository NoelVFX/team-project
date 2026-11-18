package use_case.set_target_grade;

/**
 * The output boundary for the Set Target Grade Use Case.
 */
public interface SetTargetGradeOutputBoundary {

    /**
     * Prepares the success view for the Set Target Grade Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(SetTargetGradeOutputData outputData);

    /**
     * Prepares the failure view for the Set Target Grade Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
