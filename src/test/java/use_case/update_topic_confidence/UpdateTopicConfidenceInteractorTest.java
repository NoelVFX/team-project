package use_case.update_topic_confidence;

import data_access.InMemoryCourseDataAccessObject;
import entity.ConfidenceLevel;
import entity.Course;
import entity.CourseFactory;
import entity.LetterGrade;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class UpdateTopicConfidenceInteractorTest {

    private static final String USERNAME = "Mia";

    private static class RecordingPresenter implements UpdateTopicConfidenceOutputBoundary {
        private UpdateTopicConfidenceOutputData success;
        private String error;

        @Override
        public void prepareSuccessView(UpdateTopicConfidenceOutputData outputData) {
            success = outputData;
        }

        @Override
        public void prepareFailView(String errorMessage) {
            error = errorMessage;
        }
    }

    private Course course() {
        return new CourseFactory().create(
                "CSC207",
                "Software Design",
                LetterGrade.A_MINUS,
                Arrays.asList("SOLID", "Clean Architecture"));
    }

    @Test
    void updatesTopicConfidenceAndSavesCourse() {
        final InMemoryCourseDataAccessObject dao = new InMemoryCourseDataAccessObject();
        dao.saveCourse(USERNAME, course());
        final RecordingPresenter presenter = new RecordingPresenter();
        final UpdateTopicConfidenceInputBoundary interactor =
                new UpdateTopicConfidenceInteractor(dao, presenter);

        interactor.execute(new UpdateTopicConfidenceInputData(
                USERNAME, " csc207 ", "Clean Architecture", "green"));

        assertNotNull(presenter.success);
        assertNull(presenter.error);
        assertEquals(USERNAME, presenter.success.getUsername());
        assertEquals("CSC207", presenter.success.getCourseCode());
        assertEquals("Clean Architecture", presenter.success.getTopicName());
        assertEquals("GREEN", presenter.success.getConfidence());
        assertEquals(ConfidenceLevel.GREEN,
                dao.getCourse(USERNAME, "CSC207").getTopics().get(1).getConfidence());
    }

    @Test
    void rejectsUnknownCourse() {
        final InMemoryCourseDataAccessObject dao = new InMemoryCourseDataAccessObject();
        final RecordingPresenter presenter = new RecordingPresenter();
        final UpdateTopicConfidenceInputBoundary interactor =
                new UpdateTopicConfidenceInteractor(dao, presenter);

        interactor.execute(new UpdateTopicConfidenceInputData(
                USERNAME, "CSC207", "SOLID", "GREEN"));

        assertNull(presenter.success);
        assertEquals("You are not enrolled in CSC207.", presenter.error);
    }

    @Test
    void rejectsUnknownTopic() {
        final InMemoryCourseDataAccessObject dao = new InMemoryCourseDataAccessObject();
        dao.saveCourse(USERNAME, course());
        final RecordingPresenter presenter = new RecordingPresenter();
        final UpdateTopicConfidenceInputBoundary interactor =
                new UpdateTopicConfidenceInteractor(dao, presenter);

        interactor.execute(new UpdateTopicConfidenceInputData(
                USERNAME, "CSC207", "Databases", "GREEN"));

        assertNull(presenter.success);
        assertEquals("No such topic: Databases.", presenter.error);
    }

    @Test
    void rejectsUnknownConfidenceLevel() {
        final InMemoryCourseDataAccessObject dao = new InMemoryCourseDataAccessObject();
        dao.saveCourse(USERNAME, course());
        final RecordingPresenter presenter = new RecordingPresenter();
        final UpdateTopicConfidenceInputBoundary interactor =
                new UpdateTopicConfidenceInteractor(dao, presenter);

        interactor.execute(new UpdateTopicConfidenceInputData(
                USERNAME, "CSC207", "SOLID", "BLUE"));

        assertNull(presenter.success);
        assertEquals("Unknown confidence level: BLUE.", presenter.error);
    }
}
