package use_case.rank_subjects;

/**
 * The output boundary for the Rank Subjects Use Case.
 */
public interface RankSubjectsOutputBoundary {

    /**
     * Prepares the success view for the Rank Subjects Use Case.
     * @param outputData the output data
     */
    void prepareSuccessView(RankSubjectsOutputData outputData);

    /**
     * Prepares the failure view for the Rank Subjects Use Case.
     * @param errorMessage the explanation of the failure
     */
    void prepareFailView(String errorMessage);
}
