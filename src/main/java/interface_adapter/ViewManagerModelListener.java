package interface_adapter;

/**
 * Listens for changes to the View Manager Model (the active view).
 */
public interface ViewManagerModelListener {

  /**
   * Update which view is currently being displayed.
   */
  void viewChanged();
}
