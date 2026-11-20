package interface_adapter.rank_subjects;

import org.junit.jupiter.api.Test;
import use_case.rank_subjects.RankSubjectsOutputData;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RankSubjectsPresenterTest {

    @Test
    void successFormatsRows() {
        final SubjectRankingViewModel viewModel = new SubjectRankingViewModel();
        viewModel.getState().setRankingError("old error");
        final int[] notifications = {0};
        viewModel.addListener(() -> notifications[0]++);

        final RankSubjectsOutputData output = new RankSubjectsOutputData(Arrays.asList(
                new RankSubjectsOutputData.SubjectRank(1, "MAT", Arrays.asList("MAT137", "MAT223"),
                        0.875, "GREEN", 3, 1, 0),
                new RankSubjectsOutputData.SubjectRank(2, "XYZ", Collections.singletonList("XYZ100"),
                        0.5, "YELLOW", 0, 2, 0),
                new RankSubjectsOutputData.SubjectRank(3, "STA", Collections.singletonList("STA247"),
                        0.25, "RED", 0, 1, 1),
                new RankSubjectsOutputData.SubjectRank(4, "ECO", Collections.singletonList("ECO101"),
                        0, "", 0, 0, 0)));
        new RankSubjectsPresenter(viewModel).prepareSuccessView(output);

        final List<SubjectRankingState.Row> rows = viewModel.getState().getRows();
        assertEquals(4, rows.size());
        assertEquals("1", rows.get(0).getRank());
        assertEquals("MAT (Mathematics)", rows.get(0).getSubject());
        assertEquals("MAT137, MAT223", rows.get(0).getCourses());
        assertEquals("88%", rows.get(0).getAverageConfidence());
        assertEquals("GREEN", rows.get(0).getColour());
        assertEquals("3 green, 1 yellow, 0 red", rows.get(0).getTopics());

        assertEquals("XYZ", rows.get(1).getSubject());
        assertEquals("YELLOW", rows.get(1).getColour());
        assertEquals("RED", rows.get(2).getColour());
        assertEquals("No topics", rows.get(3).getAverageConfidence());
        assertEquals("", rows.get(3).getColour());

        assertNull(viewModel.getState().getRankingError());
        assertEquals(1, notifications[0]);
    }

    @Test
    void failureClearsRowsAndShowsError() {
        final SubjectRankingViewModel viewModel = new SubjectRankingViewModel();
        viewModel.getState().setRows(Collections.singletonList(
                new SubjectRankingState.Row("1", "CSC", "CSC207", "50%", "YELLOW", "")));

        new RankSubjectsPresenter(viewModel).prepareFailView("No courses.");

        assertTrue(viewModel.getState().getRows().isEmpty());
        assertEquals("No courses.", viewModel.getState().getRankingError());
    }
}
