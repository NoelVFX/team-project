package interface_adapter.course_progress;

public interface CourseProgressViewModelListener {

    /**
     * The course progress state was updated with a course to display.
     */
    void stateChanged();

    /**
     * An attempt to load a course's progress failed.
     */
    void loadFailed();
}
