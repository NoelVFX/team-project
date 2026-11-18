package use_case.rank_subjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Output Data for the Rank Subjects Use Case: the user's subjects, from most to least confident.
 */
public class RankSubjectsOutputData {

    private final List<SubjectRank> subjects;

    /**
     * Creates the output data.
     * @param subjects the ranked subjects, from most to least confident
     */
    public RankSubjectsOutputData(List<SubjectRank> subjects) {
        this.subjects = Collections.unmodifiableList(new ArrayList<>(subjects));
    }

    public List<SubjectRank> getSubjects() {
        return subjects;
    }

    /**
     * One subject in the ranking.
     */
    public static class SubjectRank {
        private final int rank;
        private final String subjectCode;
        private final List<String> courseCodes;
        private final boolean hasTopics;
        private final double averageConfidence;
        private final int greenTopics;
        private final int yellowTopics;
        private final int redTopics;

        /**
         * Creates one entry of the ranking.
         * @param rank the position in the ranking, starting at 1; tied subjects share a rank
         * @param subjectCode the subject code, such as "MAT"
         * @param courseCodes the codes of the user's courses in this subject
         * @param hasTopics false if none of the courses has a topic
         * @param averageConfidence the average confidence from 0 to 1; 0 if there are no topics
         * @param greenTopics the number of GREEN topics
         * @param yellowTopics the number of YELLOW topics
         * @param redTopics the number of RED topics
         */
        public SubjectRank(int rank, String subjectCode, List<String> courseCodes,
                           boolean hasTopics, double averageConfidence,
                           int greenTopics, int yellowTopics, int redTopics) {
            this.rank = rank;
            this.subjectCode = subjectCode;
            this.courseCodes = Collections.unmodifiableList(new ArrayList<>(courseCodes));
            this.hasTopics = hasTopics;
            this.averageConfidence = averageConfidence;
            this.greenTopics = greenTopics;
            this.yellowTopics = yellowTopics;
            this.redTopics = redTopics;
        }

        public int getRank() {
            return rank;
        }

        public String getSubjectCode() {
            return subjectCode;
        }

        public List<String> getCourseCodes() {
            return courseCodes;
        }

        public boolean hasTopics() {
            return hasTopics;
        }

        public double getAverageConfidence() {
            return averageConfidence;
        }

        public int getGreenTopics() {
            return greenTopics;
        }

        public int getYellowTopics() {
            return yellowTopics;
        }

        public int getRedTopics() {
            return redTopics;
        }
    }
}
