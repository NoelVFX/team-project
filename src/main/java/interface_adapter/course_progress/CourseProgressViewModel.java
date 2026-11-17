package interface_adapter.course_progress;

import java.util.ArrayList;
import java.util.List;

/**
 * The View Model for the Course Progress View.
 */
public class CourseProgressViewModel {

    public static final String VIEW_NAME = "course progress";

    private final List<CourseProgressViewModelListener> listeners = new ArrayList<>();
    private CourseProgressState state = new CourseProgressState();

    public CourseProgressState getState() {
        return this.state;
    }

    public void setState(CourseProgressState state) {
        this.state = state;
    }

    public void addListener(CourseProgressViewModelListener listener) {
        listeners.add(listener);
    }

    public void notifyStateChanged() {
        for (CourseProgressViewModelListener listener : listeners) {
            listener.stateChanged();
        }
    }

    public void notifyLoadFailed() {
        for (CourseProgressViewModelListener listener : listeners) {
            listener.loadFailed();
        }
    }
}
