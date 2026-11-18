package interface_adapter.rank_subjects;

import java.util.ArrayList;
import java.util.List;

/**
 * The View Model for the Subject Ranking pop-up.
 */
public class SubjectRankingViewModel {

    private final List<SubjectRankingViewModelListener> listeners = new ArrayList<>();

    private SubjectRankingState state = new SubjectRankingState();

    public SubjectRankingState getState() {
        return this.state;
    }

    public void setState(SubjectRankingState state) {
        this.state = state;
    }

    /**
     * Registers a listener to be told when the state changes.
     * @param listener the listener to add
     */
    public void addListener(SubjectRankingViewModelListener listener) {
        listeners.add(listener);
    }

    /**
     * Tells every listener that the state has changed.
     */
    public void notifyStateChanged() {
        for (SubjectRankingViewModelListener listener : listeners) {
            listener.stateChanged();
        }
    }
}
