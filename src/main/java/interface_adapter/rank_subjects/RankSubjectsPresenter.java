package interface_adapter.rank_subjects;

import use_case.rank_subjects.RankSubjectsOutputBoundary;
import use_case.rank_subjects.RankSubjectsOutputData;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Presenter for the Rank Subjects Use Case.
 */
public class RankSubjectsPresenter implements RankSubjectsOutputBoundary {

    private static final double PERCENT = 100;

    /** Friendly names for common subject codes. Unknown codes are shown on their own. */
    private static final Map<String, String> SUBJECT_NAMES = createSubjectNames();

    private final SubjectRankingViewModel subjectRankingViewModel;

    /**
     * Creates the presenter.
     * @param subjectRankingViewModel the view model of the subject ranking pop-up
     */
    public RankSubjectsPresenter(SubjectRankingViewModel subjectRankingViewModel) {
        this.subjectRankingViewModel = subjectRankingViewModel;
    }

    @Override
    public void prepareSuccessView(RankSubjectsOutputData response) {
        final List<SubjectRankingState.Row> rows = new ArrayList<>();
        for (RankSubjectsOutputData.SubjectRank subject : response.getSubjects()) {
            final String average;
            if (subject.hasTopics()) {
                average = Math.round(subject.getAverageConfidence() * PERCENT) + "%";
            }
            else {
                average = "No topics";
            }
            rows.add(new SubjectRankingState.Row(
                    String.valueOf(subject.getRank()),
                    subjectLabel(subject.getSubjectCode()),
                    String.join(", ", subject.getCourseCodes()),
                    average,
                    subject.getConfidenceLevel(),
                    subject.getGreenTopics() + " green, " + subject.getYellowTopics()
                            + " yellow, " + subject.getRedTopics() + " red"));
        }

        final SubjectRankingState state = subjectRankingViewModel.getState();
        state.setRows(rows);
        state.setRankingError(null);
        subjectRankingViewModel.notifyStateChanged();
    }

    @Override
    public void prepareFailView(String error) {
        final SubjectRankingState state = subjectRankingViewModel.getState();
        state.setRows(new ArrayList<>());
        state.setRankingError(error);
        subjectRankingViewModel.notifyStateChanged();
    }

    /**
     * Returns the subject code with its name, such as "MAT (Mathematics)".
     * @param subjectCode the subject code
     * @return the label to show
     */
    static String subjectLabel(String subjectCode) {
        final String name = SUBJECT_NAMES.get(subjectCode);
        if (name == null) {
            return subjectCode;
        }
        return subjectCode + " (" + name + ")";
    }

    private static Map<String, String> createSubjectNames() {
        final Map<String, String> names = new HashMap<>();
        names.put("ANT", "Anthropology");
        names.put("AST", "Astronomy");
        names.put("BIO", "Biology");
        names.put("CHM", "Chemistry");
        names.put("CSC", "Computer Science");
        names.put("ECO", "Economics");
        names.put("ENG", "English");
        names.put("GGR", "Geography");
        names.put("HIS", "History");
        names.put("LIN", "Linguistics");
        names.put("MAT", "Mathematics");
        names.put("PHL", "Philosophy");
        names.put("PHY", "Physics");
        names.put("POL", "Political Science");
        names.put("PSY", "Psychology");
        names.put("RSM", "Management");
        names.put("SOC", "Sociology");
        names.put("STA", "Statistics");
        return Collections.unmodifiableMap(names);
    }
}
