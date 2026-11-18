package view;

import interface_adapter.course_progress.CourseProgressState;
import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.CourseProgressViewModelListener;
import interface_adapter.set_target_grade.SetTargetGradeController;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.Component;
import java.awt.FlowLayout;

/**
 * The part of the course progress page where the student edits the target grade
 * and sees the average they need on the rest of the course.
 * It is meant to be added to the CourseProgressView.
 */
public class TargetGradePanel extends JPanel implements CourseProgressViewModelListener {

    private static final int ROW_GAP = 5;

    private final CourseProgressViewModel courseProgressViewModel;
    private SetTargetGradeController setTargetGradeController;

    private final JComboBox<String> gradeSelector =
            new JComboBox<>(CourseProgressViewModel.GRADE_OPTIONS.toArray(new String[0]));
    private final JLabel targetMessage = new JLabel(" ");

    public TargetGradePanel(CourseProgressViewModel courseProgressViewModel) {
        this.courseProgressViewModel = courseProgressViewModel;
        this.courseProgressViewModel.addListener(this);

        final JButton setTarget = new JButton("Set Target");
        setTarget.addActionListener(evt -> {
            final CourseProgressState state = courseProgressViewModel.getState();
            setTargetGradeController.execute(state.getUsername(), state.getCourseCode(),
                    (String) gradeSelector.getSelectedItem());
        });

        final JPanel selectorRow = new JPanel(new FlowLayout(FlowLayout.LEFT, ROW_GAP, ROW_GAP));
        selectorRow.add(new JLabel("Target grade:"));
        selectorRow.add(gradeSelector);
        selectorRow.add(setTarget);

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        selectorRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        targetMessage.setAlignmentX(Component.LEFT_ALIGNMENT);
        // Line the message up with the selector row, which FlowLayout indents by its gap.
        targetMessage.setBorder(BorderFactory.createEmptyBorder(0, ROW_GAP, 0, 0));
        add(selectorRow);
        add(targetMessage);
    }

    public void setSetTargetGradeController(SetTargetGradeController setTargetGradeController) {
        this.setTargetGradeController = setTargetGradeController;
    }

    /**
     * The course progress state was updated.
     */
    @Override
    public void stateChanged() {
        final CourseProgressState state = courseProgressViewModel.getState();
        if (!state.getTargetGrade().isEmpty()) {
            gradeSelector.setSelectedItem(state.getTargetGrade());
        }
        targetMessage.setText(state.getTargetMessage().isEmpty() ? " " : state.getTargetMessage());
    }

    /**
     * An attempt to load a course failed. The progress page reports it, so nothing to do here.
     */
    @Override
    public void loadFailed() {
        // Intentionally empty.
    }

    /**
     * An attempt to set the target grade finished, successfully or not.
     */
    @Override
    public void targetGradeChanged() {
        final CourseProgressState state = courseProgressViewModel.getState();
        if (state.getTargetGradeError() != null) {
            JOptionPane.showMessageDialog(this, state.getTargetGradeError());
        }
    }
}
