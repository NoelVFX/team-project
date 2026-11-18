package interface_adapter.course_progress;

    import java.util.ArrayList;
    import java.util.Collections;
    import java.util.List;

    /**
     * The state for the Course Progress View Model: the course currently open
     * on the progress page.
     *
     * All values are plain strings, ready to show.
     */
    public class CourseProgressState {

        private String username = "";
        private String courseCode = "";
        private String courseName = "";
        private String estimatedGrade = "";
        private String currentPercentage = "";
        private String targetGrade = "";
        private String targetMessage = "";
        private String targetGradeError;
        private String completedWeight = "";

        private List<TopicRow> topics = new ArrayList<>();
        private List<AssessmentRow> assessments = new ArrayList<>();

        private String error;

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }

        public String getCourseCode() {
            return courseCode;
        }

        public void setCourseCode(String courseCode) {
            this.courseCode = courseCode;
        }

        public String getCourseName() {
            return courseName;
        }

        public void setCourseName(String courseName) {
            this.courseName = courseName;
        }

        public String getEstimatedGrade() {
            return estimatedGrade;
        }

        public void setEstimatedGrade(String estimatedGrade) {
            this.estimatedGrade = estimatedGrade;
        }

        public String getCurrentPercentage() {
            return currentPercentage;
        }

        public void setCurrentPercentage(String currentPercentage) {
            this.currentPercentage = currentPercentage;
        }

        public String getTargetGrade() {
            return targetGrade;
        }

        public void setTargetGrade(String targetGrade) {
            this.targetGrade = targetGrade;
        }

        /**
         * Returns the sentence telling the student what they need
         * on the remaining course weight.
         *
         * @return the target message, or an empty string if there is none
         */
        public String getTargetMessage() {
            return targetMessage;
        }

        public void setTargetMessage(String targetMessage) {
            this.targetMessage = targetMessage;
        }

        /**
         * Returns why the last attempt to set the target grade failed.
         *
         * @return the error, or null if the last attempt succeeded
         */
        public String getTargetGradeError() {
            return targetGradeError;
        }

        public void setTargetGradeError(String targetGradeError) {
            this.targetGradeError = targetGradeError;
        }

        public String getCompletedWeight() {
            return completedWeight;
        }

        public void setCompletedWeight(String completedWeight) {
            this.completedWeight = completedWeight;
        }

        public List<TopicRow> getTopics() {
            return topics;
        }

        public void setTopics(List<TopicRow> topics) {
            this.topics = topics;
        }

        /**
         * Returns the saved assessments of the open course.
         * The grade calculator starts from these rows.
         *
         * @return a read-only list of saved assessments
         */
        public List<AssessmentRow> getAssessments() {
            return Collections.unmodifiableList(assessments);
        }

        public void setAssessments(List<AssessmentRow> assessments) {
            this.assessments = new ArrayList<>(assessments);
        }

        public String getError() {
            return error;
        }

        public void setError(String error) {
            this.error = error;
        }
    }