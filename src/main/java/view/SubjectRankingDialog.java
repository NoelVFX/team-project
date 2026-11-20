package view;

import interface_adapter.rank_subjects.RankSubjectsController;
import interface_adapter.rank_subjects.SubjectRankingState;
import interface_adapter.rank_subjects.SubjectRankingViewModel;
import interface_adapter.rank_subjects.SubjectRankingViewModelListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.WindowConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

/**
 * The pop-up that ranks the student's subjects (such as CSC or MAT) by their average
 * confidence, from most to least confident.
 */
public class SubjectRankingDialog extends JDialog implements SubjectRankingViewModelListener {

    private static final String[] COLUMNS =
        {"Rank", "Subject", "Courses", "Average confidence", "Topics"};
    private static final int[] COLUMN_WIDTHS = {40, 170, 150, 120, 170};
    private static final int AVERAGE_COLUMN = 3;
    private static final int TABLE_WIDTH = 650;
    private static final int TABLE_HEIGHT = 200;
    private static final int PADDING = 10;
    private static final int GAP = 8;

    private static final Color GREEN = new Color(200, 235, 200);
    private static final Color YELLOW = new Color(255, 240, 170);
    private static final Color RED = new Color(255, 205, 205);

    private final SubjectRankingViewModel subjectRankingViewModel;
    private RankSubjectsController rankSubjectsController;

    private final DefaultTableModel tableModel = new DefaultTableModel(COLUMNS, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };
    private final JTable table = new JTable(tableModel);
    private final JLabel messageLabel = new JLabel(" ");
    private final List<String> rowColours = new ArrayList<>();

    /**
     * Creates the subject ranking pop-up.
     * @param owner the main window, so the pop-up opens over it; may be null
     * @param subjectRankingViewModel the view model of this pop-up
     */
    public SubjectRankingDialog(Frame owner, SubjectRankingViewModel subjectRankingViewModel) {
        super(owner, "Subject Confidence Ranking", true);
        this.subjectRankingViewModel = subjectRankingViewModel;
        this.subjectRankingViewModel.addListener(this);

        table.setFillsViewportHeight(true);
        table.setRowSelectionAllowed(false);
        table.getTableHeader().setReorderingAllowed(false);
        for (int column = 0; column < COLUMN_WIDTHS.length; column++) {
            table.getColumnModel().getColumn(column).setPreferredWidth(COLUMN_WIDTHS[column]);
        }
        table.getColumnModel().getColumn(AVERAGE_COLUMN).setCellRenderer(new ColourRenderer());
        final JScrollPane tableScroll = new JScrollPane(table);
        tableScroll.setPreferredSize(new Dimension(TABLE_WIDTH, TABLE_HEIGHT));

        final JButton close = new JButton("Close");
        close.addActionListener(evt -> setVisible(false));
        final JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttons.add(close);

        final JPanel bottom = new JPanel(new BorderLayout());
        bottom.add(messageLabel, BorderLayout.NORTH);
        bottom.add(buttons, BorderLayout.SOUTH);

        final JPanel content = new JPanel(new BorderLayout(0, GAP));
        content.setBorder(BorderFactory.createEmptyBorder(PADDING, PADDING, PADDING, PADDING));
        content.add(new JLabel("Your subjects, from most to least confident. "
                + "Green topics count as 100%, yellow as 50% and red as 0%."), BorderLayout.NORTH);
        content.add(tableScroll, BorderLayout.CENTER);
        content.add(bottom, BorderLayout.SOUTH);

        setContentPane(content);
        setDefaultCloseOperation(WindowConstants.HIDE_ON_CLOSE);
        pack();
    }

    /**
     * Sets the controller that runs the Rank Subjects Use Case.
     * @param rankSubjectsController the controller
     */
    public void setRankSubjectsController(RankSubjectsController rankSubjectsController) {
        this.rankSubjectsController = rankSubjectsController;
    }

    /**
     * Ranks the given user's subjects and shows the result.
     * @param username the logged-in user
     */
    public void open(String username) {
        rankSubjectsController.execute(username);
        setLocationRelativeTo(getOwner());
        setVisible(true);
    }

    /**
     * A ranking finished, successfully or not.
     */
    @Override
    public void stateChanged() {
        final SubjectRankingState state = subjectRankingViewModel.getState();
        tableModel.setRowCount(0);
        rowColours.clear();
        for (SubjectRankingState.Row row : state.getRows()) {
            tableModel.addRow(new Object[]{row.getRank(), row.getSubject(), row.getCourses(),
                row.getAverageConfidence(), row.getTopics()});
            rowColours.add(row.getColour());
        }
        if (state.getRankingError() == null) {
            messageLabel.setText(" ");
        }
        else {
            messageLabel.setText(state.getRankingError());
        }
    }

    /**
     * Paints the average confidence cell in the colour of its confidence level.
     */
    private final class ColourRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable cellTable, Object value,
                                                       boolean isSelected, boolean hasFocus,
                                                       int row, int column) {
            final Component cell = super.getTableCellRendererComponent(
                    cellTable, value, isSelected, hasFocus, row, column);
            setHorizontalAlignment(SwingConstants.CENTER);
            final String colour;
            if (row < rowColours.size()) {
                colour = rowColours.get(row);
            }
            else {
                colour = "";
            }
            if ("GREEN".equals(colour)) {
                cell.setBackground(GREEN);
            }
            else if ("YELLOW".equals(colour)) {
                cell.setBackground(YELLOW);
            }
            else if ("RED".equals(colour)) {
                cell.setBackground(RED);
            }
            else {
                cell.setBackground(cellTable.getBackground());
            }
            cell.setForeground(Color.BLACK);
            return cell;
        }
    }
}
