package view;

import interface_adapter.course_progress.CourseProgressState;
import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.CourseProgressViewModelListener;
import interface_adapter.course_progress.TopicRow;
import interface_adapter.course_progress.ViewCourseProgressController;

import javax.swing.*;
import java.awt.*;

/**
 * The View showing the learning progress of one course: the estimated and
 * target grades, and the student's confidence in each topic.
 */
public class CourseProgressView extends JPanel implements CourseProgressViewModelListener {

    private static final Color GREEN = new Color(46, 160, 67);
    private static final Color YELLOW = new Color(227, 179, 65);
    private static final Color RED = new Color(218, 54, 51);
    private static final int PADDING = 12;
    private static final int SWATCH_SIZE = 14;
    private static final Dimension TOPIC_LIST_SIZE = new Dimension(380, 180);

    private final CourseProgressViewModel courseProgressViewModel;
    private ViewCourseProgressController viewCourseProgressController;

    private final JLabel title = new JLabel("Course Progress");
    private final JLabel estimatedGrade = new JLabel();
    private final JLabel targetGrade = new JLabel();
    private final JLabel completedWeight = new JLabel();
    private final JPanel topicList = new JPanel();
    private final JButton back = new JButton("Back");

    public CourseProgressView(CourseProgressViewModel courseProgressViewModel) {
        this.courseProgressViewModel = courseProgressViewModel;
        this.courseProgressViewModel.addListener(this);

        title.setFont(title.getFont().deriveFont(Font.BOLD, 18f));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        final JPanel grades = new JPanel(new GridLayout(0, 2, PADDING, 4));
        grades.setBorder(BorderFactory.createTitledBorder("Grades"));
        grades.add(new JLabel("Estimated grade:"));
        grades.add(estimatedGrade);
        grades.add(new JLabel("Target grade:"));
        grades.add(targetGrade);
        grades.add(new JLabel("Progress:"));
        grades.add(completedWeight);

        topicList.setLayout(new BoxLayout(topicList, BoxLayout.Y_AXIS));
        final JScrollPane topicScroll = new JScrollPane(topicList);
        topicScroll.setBorder(BorderFactory.createTitledBorder("Topics"));
        topicScroll.setPreferredSize(TOPIC_LIST_SIZE);

        final JPanel buttons = new JPanel();
        buttons.add(back);
        back.addActionListener(evt -> viewCourseProgressController.switchToHomeView());

        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        this.add(title);
        this.add(Box.createVerticalStrut(PADDING));
        this.add(grades);
        this.add(Box.createVerticalStrut(PADDING));
        this.add(topicScroll);
        this.add(buttons);
    }

    public void setViewCourseProgressController(ViewCourseProgressController viewCourseProgressController) {
        this.viewCourseProgressController = viewCourseProgressController;
    }

    /**
     * The course progress state was updated with a course to display.
     */
    @Override
    public void stateChanged() {
        final CourseProgressState state = courseProgressViewModel.getState();
        title.setText(state.getCourseCode() + ": " + state.getCourseName());
        estimatedGrade.setText(state.getEstimatedGrade() + " (" + state.getCurrentPercentage() + ")");
        targetGrade.setText(state.getTargetGrade());
        completedWeight.setText(state.getCompletedWeight());

        topicList.removeAll();
        if (state.getTopics().isEmpty()) {
            topicList.add(new JLabel("No topics have been added to this course."));
        }
        for (TopicRow topic : state.getTopics()) {
            topicList.add(createTopicRow(topic));
        }
        topicList.revalidate();
        topicList.repaint();
    }

    /**
     * An attempt to load a course's progress failed.
     */
    @Override
    public void loadFailed() {
        JOptionPane.showMessageDialog(this, courseProgressViewModel.getState().getError());
    }

    /**
     * An attempt to set the target grade finished. Nothing to do here: the new target and
     * message arrive through stateChanged, and the target grade panel (use case 6) shows errors.
     */
    @Override
    public void targetGradeChanged() {
        // Intentionally empty.
    }

    private JPanel createTopicRow(TopicRow topic) {
        final JPanel swatch = new JPanel();
        swatch.setBackground(toColor(topic.getConfidence()));
        swatch.setPreferredSize(new Dimension(SWATCH_SIZE, SWATCH_SIZE));

        final JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
        row.add(swatch);
        row.add(new JLabel(topic.getName()));
        row.add(new JLabel("(" + topic.getDescription() + ")"));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, row.getPreferredSize().height));
        return row;
    }

    private Color toColor(String confidence) {
        final Color color;
        if ("GREEN".equals(confidence)) {
            color = GREEN;
        }
        else if ("YELLOW".equals(confidence)) {
            color = YELLOW;
        }
        else {
            color = RED;
        }
        return color;
    }
}
