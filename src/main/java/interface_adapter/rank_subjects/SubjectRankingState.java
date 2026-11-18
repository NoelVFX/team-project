package interface_adapter.rank_subjects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The state for the Subject Ranking View Model. All values are plain strings, ready to show.
 */
public class SubjectRankingState {
    private List<Row> rows = new ArrayList<>();
    private String rankingError;

    /**
     * Returns the ranked subjects, from most to least confident.
     * @return a read-only list of rows
     */
    public List<Row> getRows() {
        return Collections.unmodifiableList(rows);
    }

    public void setRows(List<Row> rows) {
        this.rows = new ArrayList<>(rows);
    }

    /**
     * Returns why the ranking could not be made.
     * @return the error, or null if the last ranking succeeded
     */
    public String getRankingError() {
        return rankingError;
    }

    public void setRankingError(String rankingError) {
        this.rankingError = rankingError;
    }

    /**
     * One subject in the ranking, as text.
     */
    public static class Row {
        private final String rank;
        private final String subject;
        private final String courses;
        private final String averageConfidence;
        private final String colour;
        private final String topics;

        /**
         * Creates a row.
         * @param rank the position, such as "1"
         * @param subject the subject, such as "MAT (Mathematics)"
         * @param courses the course codes, such as "MAT137, MAT223"
         * @param averageConfidence the average confidence, such as "75%" or "No topics"
         * @param colour "GREEN", "YELLOW" or "RED" for the average, or "" if there are no topics
         * @param topics how many topics have each level, such as "2 green, 1 yellow, 0 red"
         */
        public Row(String rank, String subject, String courses, String averageConfidence,
                   String colour, String topics) {
            this.rank = rank;
            this.subject = subject;
            this.courses = courses;
            this.averageConfidence = averageConfidence;
            this.colour = colour;
            this.topics = topics;
        }

        public String getRank() {
            return rank;
        }

        public String getSubject() {
            return subject;
        }

        public String getCourses() {
            return courses;
        }

        public String getAverageConfidence() {
            return averageConfidence;
        }

        public String getColour() {
            return colour;
        }

        public String getTopics() {
            return topics;
        }
    }
}
