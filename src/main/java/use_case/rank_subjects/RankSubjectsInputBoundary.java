package use_case.rank_subjects;

/**
 * Input Boundary for the Rank Subjects Use Case.
 */
public interface RankSubjectsInputBoundary {

    /**
     * Executes the rank subjects use case.
     * @param rankSubjectsInputData the input data
     */
    void execute(RankSubjectsInputData rankSubjectsInputData);
}
