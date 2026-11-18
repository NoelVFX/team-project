package use_case.set_target_grade;

/**
 * Input Boundary for the Set Target Grade Use Case.
 */
public interface SetTargetGradeInputBoundary {

    /**
     * Executes the set target grade use case.
     * @param setTargetGradeInputData the input data
     */
    void execute(SetTargetGradeInputData setTargetGradeInputData);
}
