package interface_adapter.logged_in;

/**
 * Listens for changes to the Logged In View Model.
 */
public interface LoggedInViewModelListener {

  /**
   * The logged-in state was updated.
   */
  void stateChanged();

  /**
   * A password change attempt occurred.
   */
  void passwordChanged();
}
