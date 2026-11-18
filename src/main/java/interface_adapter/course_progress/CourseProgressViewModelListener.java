package interface_adapter.course_progress;

/**
 * Listens for changes to the Course Progress View Model.
 */
public interface CourseProgressViewModelListener {

    /**
     * The course progress state was updated with a course to display.
     */
    void stateChanged();

    /**
     * An attempt to load a course's progress failed.
     */
    void loadFailed();

    /**
     * An attempt to set the target grade finished, successfully or not.
     */
    void targetGradeChanged();
}
