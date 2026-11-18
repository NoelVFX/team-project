package use_case.view_course_progress;

import entity.Course;
import entity.LetterGrade;
import entity.Topic;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * The View Course Progress Interactor.
 */
public class ViewCourseProgressInteractor implements ViewCourseProgressInputBoundary {

    private final ViewCourseProgressCourseDataAccessInterface courseDataAccessObject;
    private final ViewCourseProgressOutputBoundary courseProgressPresenter;

    public ViewCourseProgressInteractor(ViewCourseProgressCourseDataAccessInterface courseDataAccessInterface,
                                        ViewCourseProgressOutputBoundary viewCourseProgressOutputBoundary) {
        this.courseDataAccessObject = courseDataAccessInterface;
        this.courseProgressPresenter = viewCourseProgressOutputBoundary;
    }

    @Override
    public void execute(ViewCourseProgressInputData inputData) {
        final String username = inputData.getUsername();
        final String courseCode = inputData.getCourseCode() == null ? "" : inputData.getCourseCode().trim();

        if (courseCode.isEmpty()) {
            courseProgressPresenter.prepareFailView("Please choose a course.");
        }
        else if (!courseDataAccessObject.courseExists(username, courseCode)) {
            courseProgressPresenter.prepareFailView("You are not enrolled in " + courseCode + ".");
        }
        else {
            final Course course = courseDataAccessObject.getCourse(username, courseCode);
            courseProgressPresenter.prepareSuccessView(toOutputData(course));
        }
    }

    private ViewCourseProgressOutputData toOutputData(Course course) {
        final List<ViewCourseProgressOutputData.TopicData> topics = new ArrayList<>();
        for (Topic topic : course.getTopics()) {
            topics.add(new ViewCourseProgressOutputData.TopicData(
                    topic.getName(), topic.getConfidence().getName()));
        }

        final OptionalDouble currentPercentage = course.getCurrentPercentage();
        final Optional<LetterGrade> estimatedGrade = course.getEstimatedGrade();

        return new ViewCourseProgressOutputData(
                course.getCode(),
                course.getName(),
                course.getTargetGrade().getLabel(),
                currentPercentage.isPresent(),
                currentPercentage.orElse(0),
                estimatedGrade.isPresent() ? estimatedGrade.get().getLabel() : "",
                course.getCompletedWeight(),
                topics);
    }

    @Override
    public void switchToHomeView() {
        courseProgressPresenter.switchToHomeView();
    }
}
