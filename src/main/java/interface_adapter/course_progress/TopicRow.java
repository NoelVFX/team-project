package interface_adapter.course_progress;

/**
 * The text needed to display one topic on the course progress page.
 */
public class TopicRow {

    private final String name;
    private final String confidence;
    private final String description;

    /**
     * Creates a topic row.
     * @param name the name of the topic
     * @param confidence the name of the confidence level: "GREEN", "YELLOW" or "RED"
     * @param description what the confidence level means, such as "Confident"
     */
    public TopicRow(String name, String confidence, String description) {
        this.name = name;
        this.confidence = confidence;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getConfidence() {
        return confidence;
    }

    public String getDescription() {
        return description;
    }
}
