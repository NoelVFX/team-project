package use_case.update_topic_confidence;

import entity.ConfidenceLevel;
import entity.Course;

/**
 * The Update Topic Confidence Interactor.
 */
public class UpdateTopicConfidenceInteractor implements UpdateTopicConfidenceInputBoundary {

    private final UpdateTopicConfidenceCourseDataAccessInterface courseDataAccessObject;
    private final UpdateTopicConfidenceOutputBoundary presenter;

    public UpdateTopicConfidenceInteractor(
            UpdateTopicConfidenceCourseDataAccessInterface courseDataAccessObject,
            UpdateTopicConfidenceOutputBoundary presenter) {
        this.courseDataAccessObject = courseDataAccessObject;
        this.presenter = presenter;
    }

    @Override
    public void execute(UpdateTopicConfidenceInputData inputData) {
        final String username = normalise(inputData.getUsername());
        final String courseCode = normalise(inputData.getCourseCode());
        final String topicName = normalise(inputData.getTopicName());
        final String confidenceName = normalise(inputData.getConfidence());

        if (username.isEmpty()) {
            presenter.prepareFailView("You must be logged in to update a topic.");
            return;
        }
        if (courseCode.isEmpty()) {
            presenter.prepareFailView("Please choose a course.");
            return;
        }
        if (topicName.isEmpty()) {
            presenter.prepareFailView("Please choose a topic.");
            return;
        }

        final Course course = courseDataAccessObject.getCourse(username, courseCode);
        if (course == null) {
            presenter.prepareFailView("You are not enrolled in " + courseCode + ".");
            return;
        }

        final ConfidenceLevel confidence;
        try {
            confidence = ConfidenceLevel.fromName(confidenceName);
        }
        catch (IllegalArgumentException exception) {
            presenter.prepareFailView(exception.getMessage() + ".");
            return;
        }

        try {
            course.setTopicConfidence(topicName, confidence);
        }
        catch (IllegalArgumentException exception) {
            presenter.prepareFailView(exception.getMessage() + ".");
            return;
        }

        courseDataAccessObject.saveCourse(username, course);
        presenter.prepareSuccessView(new UpdateTopicConfidenceOutputData(
                username, course.getCode(), topicName, confidence.getName()));
    }

    private static String normalise(String value) {
        return value == null ? "" : value.trim();
    }
}
