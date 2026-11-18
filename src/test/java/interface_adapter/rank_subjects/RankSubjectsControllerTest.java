package interface_adapter.rank_subjects;

import data_access.InMemoryCourseDataAccessObject;
import entity.CourseFactory;
import entity.LetterGrade;
import entity.StandardConfidenceScoring;
import org.junit.jupiter.api.Test;
import use_case.rank_subjects.RankSubjectsInteractor;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

/**
 * Runs the whole Rank Subjects engine: controller, interactor, presenter and view model.
 */
class RankSubjectsControllerTest {

    @Test
    void ranksTheGivenUsersSubjects() {
        final InMemoryCourseDataAccessObject repository = new InMemoryCourseDataAccessObject();
        repository.saveCourse("Paul", new CourseFactory().create("MAT137", "Calculus",
                LetterGrade.A, Collections.singletonList("Limits")));
        repository.saveCourse("Alice", new CourseFactory().create("CSC207", "Software Design",
                LetterGrade.A, Collections.singletonList("SOLID")));
        final SubjectRankingViewModel viewModel = new SubjectRankingViewModel();
        final RankSubjectsController controller = new RankSubjectsController(
                new RankSubjectsInteractor(repository, new RankSubjectsPresenter(viewModel),
                        new StandardConfidenceScoring()));

        controller.execute("Paul");

        assertNull(viewModel.getState().getRankingError());
        assertEquals(1, viewModel.getState().getRows().size());
        assertEquals("MAT (Mathematics)", viewModel.getState().getRows().get(0).getSubject());
        assertEquals("0%", viewModel.getState().getRows().get(0).getAverageConfidence());
        assertEquals("RED", viewModel.getState().getRows().get(0).getColour());
    }

    @Test
    void userWithoutCoursesSeesAnError() {
        final SubjectRankingViewModel viewModel = new SubjectRankingViewModel();
        new RankSubjectsController(new RankSubjectsInteractor(new InMemoryCourseDataAccessObject(),
                new RankSubjectsPresenter(viewModel), new StandardConfidenceScoring()))
                .execute("Nobody");

        assertEquals("You are not enrolled in any courses yet. Enrol in a course first.",
                viewModel.getState().getRankingError());
    }
}
