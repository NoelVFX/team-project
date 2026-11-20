package view;

import org.junit.jupiter.api.Test;

import javax.swing.JButton;
import java.awt.Component;
import java.awt.Container;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class CourseProgressViewTest {

    @Test
    void calculateGradeButtonOpensCalculatorAction() {
        final CourseProgressView courseProgressView =
                new CourseProgressView(new interface_adapter.course_progress.CourseProgressViewModel());
        final AtomicBoolean opened = new AtomicBoolean(false);
        courseProgressView.setOpenGradeCalculatorAction(() -> opened.set(true));

        final JButton calculateGrade = findButton(courseProgressView, "Calculate Grade");
        assertNotNull(calculateGrade);
        calculateGrade.doClick();

        assertTrue(opened.get());
    }

    private JButton findButton(Container container, String text) {
        for (Component component : container.getComponents()) {
            if (component instanceof JButton && text.equals(((JButton) component).getText())) {
                return (JButton) component;
            }
            if (component instanceof Container) {
                final JButton button = findButton((Container) component, text);
                if (button != null) {
                    return button;
                }
            }
        }
        return null;
    }
}
