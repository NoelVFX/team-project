package interface_adapter.enrol_course;

/**
 * Listens for changes to the Enrol View Model.
 */
public interface EnrolViewModelListener {

    /**
     * The enrol state was updated after an enrol attempt.
     */
    void stateChanged();
}

