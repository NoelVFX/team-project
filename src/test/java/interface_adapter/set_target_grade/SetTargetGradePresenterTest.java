package interface_adapter.set_target_grade;

import data_access.InMemoryCourseDataAccessObject;
import entity.Assessment;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import interface_adapter.course_progress.CourseProgressState;
import interface_adapter.course_progress.CourseProgressViewModel;
import org.junit.jupiter.api.Test;
import use_case.set_target_grade.SetTargetGradeInteractor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Runs the whole Set Target Grade engine: controller, interactor, presenter and view model.
 */
class SetTargetGradePresenterTest {

    private CourseProgressViewModel viewModel;
    private SetTargetGradeController controller;

    private void build() {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        final Course course = new CourseFactory().create("CSC207", "Software Design", LetterGrade.B,
                Collections.singletonList("SOLID"));
        course.setAssessments(Collections.singletonList(new Assessment("Midterm", 80, 40)));
        repository.saveCourse("Paul", course);

        viewModel = new CourseProgressViewModel();
        controller = new SetTargetGradeController(
                new SetTargetGradeInteractor(repository, new SetTargetGradePresenter(viewModel)));
    }

    @Test
    void successUpdatesTargetAndMessage() {
        build();
        viewModel.getState().setTargetGradeError("old error");

        controller.execute("Paul", "CSC207", "A-");

        final CourseProgressState state = viewModel.getState();
        assertEquals("A-", state.getTargetGrade());
        assertEquals("You need an average of 80% on the remaining 60% to reach A-.", state.getTargetMessage());
        assertNull(state.getTargetGradeError());
    }

    @Test
    void failureKeepsOldTarget() {
        build();
        viewModel.getState().setTargetGrade("B");

        controller.execute("Paul", "CSC207", "Z");

        final CourseProgressState state = viewModel.getState();
        assertEquals("B", state.getTargetGrade());
        assertEquals("Unknown letter grade: Z.", state.getTargetGradeError());
    }
}
