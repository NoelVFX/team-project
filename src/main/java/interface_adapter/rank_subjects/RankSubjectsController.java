package interface_adapter.rank_subjects;

import use_case.rank_subjects.RankSubjectsInputBoundary;
import use_case.rank_subjects.RankSubjectsInputData;

/**
 * The controller for the Rank Subjects Use Case.
 */
public class RankSubjectsController {

    private final RankSubjectsInputBoundary rankSubjectsUseCaseInteractor;

    public RankSubjectsController(RankSubjectsInputBoundary rankSubjectsUseCaseInteractor) {
        this.rankSubjectsUseCaseInteractor = rankSubjectsUseCaseInteractor;
    }

    /**
     * Executes the Rank Subjects Use Case.
     * @param username the user whose subjects are ranked
     */
    public void execute(String username) {
        rankSubjectsUseCaseInteractor.execute(new RankSubjectsInputData(username));
    }
}
