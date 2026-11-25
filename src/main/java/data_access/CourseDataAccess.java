package data_access;

import use_case.calculate_grade.CalculateGradeCourseDataAccessInterface;
import use_case.enrol_course.EnrolCourseDataAccessInterface;
import use_case.rank_subjects.RankSubjectsCourseDataAccessInterface;
import use_case.set_target_grade.SetTargetGradeCourseDataAccessInterface;
import use_case.update_topic_confidence.UpdateTopicConfidenceCourseDataAccessInterface;
import use_case.view_course_progress.ViewCourseProgressCourseDataAccessInterface;
import use_case.view_courses.ViewCoursesCourseDataAccessInterface;

/**
 * Everything a course data access object offers: the union of the small course data access
 * interfaces that each use case depends on.
 *
 * <p>Use cases never see this interface; each one still depends only on its own small interface
 * (Interface Segregation). It exists so that {@link CachingCourseDataAccessObject} can wrap any
 * course data access object, whether it stores courses in MongoDB or in memory.</p>
 */
public interface CourseDataAccess extends
        ViewCourseProgressCourseDataAccessInterface,
        EnrolCourseDataAccessInterface,
        CalculateGradeCourseDataAccessInterface,
        SetTargetGradeCourseDataAccessInterface,
        UpdateTopicConfidenceCourseDataAccessInterface,
        RankSubjectsCourseDataAccessInterface,
        ViewCoursesCourseDataAccessInterface {
}
