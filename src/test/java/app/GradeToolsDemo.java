package app;

import entity.ConfidenceLevel;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import interface_adapter.course_progress.CourseProgressState;
import interface_adapter.course_progress.CourseProgressViewModel;
import interface_adapter.course_progress.CourseProgressViewModelListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import java.awt.Component;
import java.util.Arrays;

/**
 * A manual demo of use cases 5 (Calculate Grade), 6 (Set Target Grade) and 7 (Rank Subjects),
 * for trying them
 * out before they are added to the CourseProgressView. It wires the use case with AppBuilder, adds a sample course
 * and shows a stand-in progress page.
 * Run it from the IDE (it lives in the test sources so it never ships with the app).
 */
public final class GradeToolsDemo {

    private static final String USERNAME = "demo";
    private static final String COURSE_CODE = "CSC207";

    private GradeToolsDemo() {
    }

    /**
     * Starts the demo.
     * @param args unused
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GradeToolsDemo::show);
    }

    private static void show() {
        final AppBuilder appBuilder = new AppBuilder()
                .addCalculateGradeUseCase()
                .addSetTargetGradeUseCase()
                .addRankSubjectsUseCase();
        saveSampleCourses(appBuilder);

        // What View Course Progress (use case 3) will do when a course is opened.
        final CourseProgressViewModel viewModel = appBuilder.getCourseProgressViewModel();
        final CourseProgressState state = viewModel.getState();
        state.setUsername(USERNAME);
        state.setCourseCode(COURSE_CODE);
        state.setCourseName("Software Design");
        state.setTargetGrade("A-");
        state.setEstimatedGrade("N/A");
        state.setTargetMessage("Open the calculator to enter your marks.");

        final JLabel title = new JLabel(COURSE_CODE + " - Software Design");
        final JLabel estimated = new JLabel();
        final JButton calculate = new JButton("Calculate Grade");
        calculate.addActionListener(evt -> appBuilder.getGradeCalculatorDialog().open());
        final JButton ranking = new JButton("Subject Ranking");
        ranking.addActionListener(evt -> appBuilder.getSubjectRankingDialog().open(USERNAME));
        viewModel.addListener(new CourseProgressViewModelListener() {
            @Override
            public void stateChanged() {
                estimated.setText("Estimated grade: " + viewModel.getState().getEstimatedGrade());
            }

            @Override
            public void targetGradeChanged() {
                // Not used by this demo.
            }

            @Override
            public void loadFailed() {
                // The demo opens its course directly, so loading cannot fail.
            }
        });

        final JPanel page = new JPanel();
        page.setLayout(new BoxLayout(page, BoxLayout.Y_AXIS));
        page.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
        for (JComponent component : new JComponent[]{
            title, estimated, appBuilder.getTargetGradePanel(), calculate, ranking}) {
            component.setAlignmentX(Component.LEFT_ALIGNMENT);
            page.add(component);
        }
        viewModel.notifyStateChanged();

        // Reuse the app's main window so the calculator pop-up opens over it.
        final JFrame frame = appBuilder.build();
        frame.getContentPane().removeAll();
        frame.add(page);
        frame.setTitle("Grade tools demo");
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

    /** Enrols the demo user in a few courses from different subjects, with some topics rated. */
    private static void saveSampleCourses(AppBuilder appBuilder) {
        final CourseFactory factory = new CourseFactory();
        final Course csc207 = factory.create(COURSE_CODE, "Software Design", LetterGrade.A_MINUS,
                Arrays.asList("SOLID", "Clean Architecture", "Design Patterns"));
        csc207.setTopicConfidence("SOLID", ConfidenceLevel.YELLOW);
        final Course csc148 = factory.create("CSC148", "Intro to Computer Science", LetterGrade.A,
                Arrays.asList("Recursion", "Linked Lists"));
        csc148.setTopicConfidence("Recursion", ConfidenceLevel.GREEN);
        final Course mat137 = factory.create("MAT137", "Calculus", LetterGrade.B_PLUS,
                Arrays.asList("Limits", "Derivatives", "Integrals"));
        mat137.setTopicConfidence("Limits", ConfidenceLevel.GREEN);
        mat137.setTopicConfidence("Derivatives", ConfidenceLevel.GREEN);
        mat137.setTopicConfidence("Integrals", ConfidenceLevel.YELLOW);
        final Course sta247 = factory.create("STA247", "Probability", LetterGrade.B,
                Arrays.asList("Random Variables", "Distributions"));
        for (Course course : new Course[]{csc207, csc148, mat137, sta247}) {
            appBuilder.courseDataAccessObject.saveCourse(USERNAME, course);
        }
    }
}
