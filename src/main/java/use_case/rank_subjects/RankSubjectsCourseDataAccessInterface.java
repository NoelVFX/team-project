package use_case.rank_subjects;

import entity.Course;

import java.util.List;

/**
 * DAO interface for the Rank Subjects Use Case.
 */
public interface RankSubjectsCourseDataAccessInterface {

    /**
     * Returns every course the user is enrolled in.
     * @param username the owner of the courses
     * @return the user's courses; empty if the user has none
     */
    List<Course> getCourses(String username);
}
