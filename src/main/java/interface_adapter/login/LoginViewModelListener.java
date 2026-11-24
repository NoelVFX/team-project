package interface_adapter.login;

/**
 * Listens for changes to the Login View Model.
 */
public interface LoginViewModelListener {

  /**
   * The login state was updated.
   */
  void stateChanged();

}
