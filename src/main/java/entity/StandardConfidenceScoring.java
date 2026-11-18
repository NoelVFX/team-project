package entity;

/**
 * The default confidence scoring: a green topic is worth 1, yellow 0.5 and red 0.
 * An average of at least 2/3 counts as GREEN, at least 1/3 as YELLOW, and anything lower as RED.
 */
public class StandardConfidenceScoring implements ConfidenceScoringStrategy {

    private static final double GREEN_SCORE = 1.0;
    private static final double YELLOW_SCORE = 0.5;
    private static final double RED_SCORE = 0.0;
    private static final double GREEN_THRESHOLD = 2.0 / 3.0;
    private static final double YELLOW_THRESHOLD = 1.0 / 3.0;

    @Override
    public double score(ConfidenceLevel level) {
        final double result;
        if (level == ConfidenceLevel.GREEN) {
            result = GREEN_SCORE;
        }
        else if (level == ConfidenceLevel.YELLOW) {
            result = YELLOW_SCORE;
        }
        else {
            result = RED_SCORE;
        }
        return result;
    }

    @Override
    public ConfidenceLevel levelFor(double averageScore) {
        final ConfidenceLevel result;
        if (averageScore >= GREEN_THRESHOLD) {
            result = ConfidenceLevel.GREEN;
        }
        else if (averageScore >= YELLOW_THRESHOLD) {
            result = ConfidenceLevel.YELLOW;
        }
        else {
            result = ConfidenceLevel.RED;
        }
        return result;
    }
}
