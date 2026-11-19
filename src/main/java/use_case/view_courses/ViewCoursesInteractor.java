package use_case.view_courses;

import entity.Course;
import entity.LetterGrade;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;

/**
 * The View Enrolled Courses Interactor: loads the user's courses with their
 * estimated and target grades for the home page.
 */
public class ViewCoursesInteractor implements ViewCoursesInputBoundary {

    private final ViewCoursesCourseDataAccessInterface courseDataAccessObject;
    private final ViewCoursesOutputBoundary viewCoursesPresenter;

    public ViewCoursesInteractor(ViewCoursesCourseDataAccessInterface courseDataAccessInterface,
                                 ViewCoursesOutputBoundary viewCoursesOutputBoundary) {
        this.courseDataAccessObject = courseDataAccessInterface;
        this.viewCoursesPresenter = viewCoursesOutputBoundary;
    }

    @Override
    public void execute(ViewCoursesInputData inputData) {
        final String username = inputData.getUsername();

        if (username == null || username.trim().isEmpty()) {
            viewCoursesPresenter.prepareFailView("No user is logged in.");
            return;
        }

        final List<ViewCoursesOutputData.CourseSummary> summaries = new ArrayList<>();
        for (Course course : courseDataAccessObject.getCourses(username)) {
            summaries.add(toSummary(course));
        }
        viewCoursesPresenter.prepareSuccessView(new ViewCoursesOutputData(username, summaries));
    }

    private static ViewCoursesOutputData.CourseSummary toSummary(Course course) {
        final OptionalDouble currentPercentage = course.getCurrentPercentage();
        final Optional<LetterGrade> estimatedGrade = course.getEstimatedGrade();

        return new ViewCoursesOutputData.CourseSummary(
                course.getCode(),
                course.getName(),
                course.getTargetGrade().getLabel(),
                currentPercentage.isPresent(),
                currentPercentage.orElse(0),
                estimatedGrade.isPresent() ? estimatedGrade.get().getLabel() : "",
                course.isTargetReached());
    }
}
