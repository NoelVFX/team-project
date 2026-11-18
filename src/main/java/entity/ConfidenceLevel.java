package entity;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * How confident a student feels about a topic in a course.
 * The only instances are the constants declared in this class.
 */
public final class ConfidenceLevel {
    /** The student is confident with the topic. */
    public static final ConfidenceLevel GREEN = new ConfidenceLevel("GREEN", 1.0);
    /** The student is not so confident with the topic. */
    public static final ConfidenceLevel YELLOW = new ConfidenceLevel("YELLOW", 0.5);
    /** The topic needs more attention. */
    public static final ConfidenceLevel RED = new ConfidenceLevel("RED", 0.0);

    /** Every confidence level, from most to least confident. */
    private static final List<ConfidenceLevel> ALL_LEVELS =
            Collections.unmodifiableList(Arrays.asList(GREEN, YELLOW, RED));

    private final String name;
    private final double score;

    private ConfidenceLevel(String name, double score) {
        this.name = name;
        this.score = score;
    }

    /**
     * Returns the name of this confidence level, such as "GREEN".
     * @return the name of this confidence level
     */
    public String getName() {
        return name;
    }

    /**
     * Returns how confident this level is as a number, used to average confidence
     * over many topics: 1 for GREEN, 0.5 for YELLOW and 0 for RED.
     * @return the score of this confidence level, from 0 to 1
     */
    public double getScore() {
        return score;
    }

    /**
     * Returns every confidence level, from most to least confident.
     * @return a read-only list of all the confidence levels
     */
    public static List<ConfidenceLevel> values() {
        return ALL_LEVELS;
    }

    /**
     * Returns the confidence level with the given name, ignoring case and surrounding spaces.
     * @param name the name of a confidence level, such as "yellow"
     * @return the matching confidence level
     * @throws IllegalArgumentException if no confidence level has that name
     */
    public static ConfidenceLevel fromName(String name) {
        if (name != null) {
            for (ConfidenceLevel level : ALL_LEVELS) {
                if (level.name.equalsIgnoreCase(name.trim())) {
                    return level;
                }
            }
        }
        throw new IllegalArgumentException("Unknown confidence level: " + name);
    }

    @Override
    public String toString() {
        return name;
    }
}
