package interface_adapter.calculate_grade;

import data_access.InMemoryCourseDataAccessObject;
import entity.CourseFactory;
import entity.LetterGrade;
import interface_adapter.course_progress.AssessmentRow;
import org.junit.jupiter.api.Test;
import use_case.calculate_grade.CalculateGradeInteractor;
import use_case.calculate_grade.CalculateGradeOutputBoundary;
import use_case.calculate_grade.CalculateGradeOutputData;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Checks that the controller hands every row, unchanged, to the real interactor.
 */
class CalculateGradeControllerTest {

    private static final double DELTA = 1e-9;

    @Test
    void passesEveryRowToTheInteractor() {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        repository.saveCourse("Paul", new CourseFactory().create("CSC207", "Software Design",
                LetterGrade.A, Collections.singletonList("SOLID")));
        final CalculateGradeOutputData[] result = new CalculateGradeOutputData[1];
        final CalculateGradeOutputBoundary presenter = new CalculateGradeOutputBoundary() {
            @Override
            public void prepareSuccessView(CalculateGradeOutputData outputData) {
                result[0] = outputData;
            }

            @Override
            public void prepareFailView(String errorMessage) {
                fail("Use case failure is unexpected: " + errorMessage);
            }
        };
        final CalculateGradeController controller =
                new CalculateGradeController(new CalculateGradeInteractor(repository, presenter));

        controller.execute("Paul", "CSC207", Arrays.asList(
                new AssessmentRow("Midterm", "80", "20"),
                new AssessmentRow("Quiz", "90%", "5")));

        assertNotNull(result[0]);
        assertEquals(2, result[0].getAssessments().size());
        assertEquals("Midterm", result[0].getAssessments().get(0).getName());
        assertEquals(80, result[0].getAssessments().get(0).getMark(), DELTA);
        assertEquals(20, result[0].getAssessments().get(0).getWeight(), DELTA);
        assertEquals("Quiz", result[0].getAssessments().get(1).getName());
        assertEquals(90, result[0].getAssessments().get(1).getMark(), DELTA);
        assertEquals(5, result[0].getAssessments().get(1).getWeight(), DELTA);
    }
}
