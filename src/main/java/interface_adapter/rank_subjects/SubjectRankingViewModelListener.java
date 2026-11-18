package interface_adapter.rank_subjects;

/**
 * Listens for changes to the Subject Ranking View Model.
 */
public interface SubjectRankingViewModelListener {

    /**
     * A ranking finished, successfully or not.
     */
    void stateChanged();
}
