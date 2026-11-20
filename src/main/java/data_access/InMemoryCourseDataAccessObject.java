 package data_access;

    import entity.Course;
    import use_case.calculate_grade.CalculateGradeCourseDataAccessInterface;
    import use_case.enrol_course.EnrolCourseDataAccessInterface;
    import use_case.rank_subjects.RankSubjectsCourseDataAccessInterface;
    import use_case.set_target_grade.SetTargetGradeCourseDataAccessInterface;
    import use_case.update_topic_confidence.UpdateTopicConfidenceCourseDataAccessInterface;
    import use_case.view_course_progress.ViewCourseProgressCourseDataAccessInterface;
    import use_case.view_courses.ViewCoursesCourseDataAccessInterface;

    import java.util.ArrayList;
    import java.util.HashMap;
    import java.util.LinkedHashMap;
    import java.util.List;
    import java.util.Locale;
    import java.util.Map;

    /**
     * In-memory implementation of the DAO for storing each user's courses.
     * This implementation does NOT persist data between runs.
     */
    public class InMemoryCourseDataAccessObject implements
            ViewCourseProgressCourseDataAccessInterface,
            EnrolCourseDataAccessInterface,
            CalculateGradeCourseDataAccessInterface,
            SetTargetGradeCourseDataAccessInterface,
            UpdateTopicConfidenceCourseDataAccessInterface,
            RankSubjectsCourseDataAccessInterface {
            RankSubjectsCourseDataAccessInterface,
            ViewCoursesCourseDataAccessInterface {

        /** Maps a username to that user's courses by upper-case course code. */
        private final Map<String, Map<String, Course>> coursesByUser = new HashMap<>();

        /**
         * Compatibility helper for the view-progress branch.
         */
        public void save(String username, Course course) {
            saveCourse(username, course);
        }

        @Override
        public boolean existsByCode(String username, String courseCode) {
            return courseExists(username, courseCode);
        }

        @Override
        public boolean courseExists(String username, String courseCode) {
            return getCourse(username, courseCode) != null;
        }

        @Override
        public void saveCourse(String username, Course course) {
            coursesByUser
                    .computeIfAbsent(username, user -> new LinkedHashMap<>())
                    .put(key(course.getCode()), course);
        }

        @Override
        public Course getCourse(String username, String courseCode) {
            final Map<String, Course> courses = coursesByUser.get(username);

            if (courses == null || courseCode == null) {
                return null;
            }

            return courses.get(key(courseCode));
        }

        /**
         * Returns all of the user's courses in enrolment order.
         */
        @Override
        public List<Course> getCourses(String username) {
            final Map<String, Course> courses = coursesByUser.get(username);

            if (courses == null) {
                return new ArrayList<>();
            }

            return new ArrayList<>(courses.values());
        }

        private static String key(String courseCode) {
            return courseCode.trim().toUpperCase(Locale.ROOT);
        }
    }