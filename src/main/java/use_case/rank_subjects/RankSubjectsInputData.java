package use_case.rank_subjects;

/**
 * The Input Data for the Rank Subjects Use Case.
 */
public class RankSubjectsInputData {

    private final String username;

    /**
     * Creates the input data.
     * @param username the user whose subjects are ranked
     */
    public RankSubjectsInputData(String username) {
        this.username = username;
    }

    String getUsername() {
        return username;
    }
}
