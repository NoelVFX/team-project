package entity;

/**
 * A rule for turning topic confidence into numbers (Strategy pattern). It decides how much
 * each confidence level is worth, and which level an average of those scores counts as.
 * Swapping the strategy changes how subjects are ranked without changing the ranking itself.
 */
public interface ConfidenceScoringStrategy {

    /**
     * Returns how much a topic at the given confidence level is worth.
     * @param level the confidence level of a topic
     * @return the score, from 0 (no confidence) to 1 (fully confident)
     */
    double score(ConfidenceLevel level);

    /**
     * Returns the confidence level that an average score counts as.
     * @param averageScore an average of scores returned by {@link #score}, from 0 to 1
     * @return the matching confidence level
     */
    ConfidenceLevel levelFor(double averageScore);
}
