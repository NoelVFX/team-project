package interface_adapter.signup;

/**
 * Listens for changes to the Signup View Model.
 */
public interface SignupViewModelListener {

  /**
   * The signup state was updated.
   */
  void stateChanged();

}
