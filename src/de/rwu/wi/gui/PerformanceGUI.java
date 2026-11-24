package de.rwu.wi.gui;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingWorker;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.ExecutionException;

public class PerformanceGUI extends JFrame {
    private final JTextField sizeField = new JTextField("1000", 10);
    private final JComboBox<PerformanceRunner.DataType> dataType =
            new JComboBox<>(PerformanceRunner.DataType.values());
    private final JButton startButton = new JButton("Start");
    private final JButton abortButton = new JButton("Abort current");
    private final JLabel status = new JLabel("Select algorithms and start a run.");
    private final SortTableModel model = new SortTableModel();
    private final JTable table = new JTable(model);
    private final PerformanceChart runtimeChart = PerformanceChart.runtime();
    private final PerformanceChart comparisonChart = PerformanceChart.comparisons();
    private final ActiveSort activeSort = new ActiveSort();

    public PerformanceGUI() {
        super("Sorting performance");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        JMenuBar menuBar = new JMenuBar();
        JMenu helpMenu = new JMenu("Help");
        JMenuItem sizeGuidelines = new JMenuItem("List size guidelines");
        sizeGuidelines.addActionListener(event -> JOptionPane.showMessageDialog(this,
                "MainPerformance runs array bubble sort for up to 100,000 elements\n"
                        + "and linked-list bubble sort for up to 50,000 elements.\n\n"
                        + "Both bubble sorts are quadratic and may take a long time even\n"
                        + "below these limits. For larger inputs, uncheck them or use Abort current.\n"
                        + "Linked-list merge sort is O(n log n) and has no such cutoff.",
                "List size guidelines", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(sizeGuidelines);
        JMenuItem aboutItem = new JMenuItem("About");
        aboutItem.addActionListener(event -> JOptionPane.showMessageDialog(this,
                "Sorting Performance\n\nCopyright (c) 2026 Thomas Bayer\n"
                        + "Licensed under the MIT License. See the LICENSE file for details.",
                "About", JOptionPane.INFORMATION_MESSAGE));
        helpMenu.add(aboutItem);
        menuBar.add(helpMenu);
        setJMenuBar(menuBar);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEADING));
        controls.add(new JLabel("List size:"));
        controls.add(sizeField);
        controls.add(new JLabel("Data type:"));
        controls.add(dataType);
        controls.add(startButton);
        abortButton.setEnabled(false);
        controls.add(abortButton);

        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getColumnModel().getColumn(0).setMaxWidth(70);
        DefaultTableCellRenderer numberRenderer = new DefaultTableCellRenderer();
        numberRenderer.setHorizontalAlignment(JLabel.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(numberRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(numberRenderer);
        table.setFillsViewportHeight(true);

        JPanel content = new JPanel(new BorderLayout(8, 8));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        content.add(controls, BorderLayout.NORTH);
        JPanel charts = new JPanel(new GridLayout(1, 2, 8, 0));
        charts.add(new JScrollPane(runtimeChart));
        charts.add(new JScrollPane(comparisonChart));
        JSplitPane split = new JSplitPane(JSplitPane.VERTICAL_SPLIT, new JScrollPane(table), charts);
        split.setResizeWeight(0.5);
        content.add(split, BorderLayout.CENTER);
        content.add(status, BorderLayout.SOUTH);
        setContentPane(content);
        setSize(1050, 700);
        split.setDividerLocation(0.5);
        setLocationRelativeTo(null);
        startButton.addActionListener(event -> start());
        abortButton.addActionListener(event -> {
            if (activeSort.abort()) {
                status.setText("Aborting current sorter...");
            } else {
                status.setText("No sorter is currently running.");
            }
        });
    }

    private void start() {
        if (table.isEditing() && !table.getCellEditor().stopCellEditing()) {
            return;
        }
        int size;
        try {
            size = Integer.parseInt(sizeField.getText().trim());
            if (size < 1) {
                throw new NumberFormatException();
            }
        } catch (NumberFormatException exception) {
            JOptionPane.showMessageDialog(this, "Enter a positive integer list size.",
                    "Invalid list size", JOptionPane.ERROR_MESSAGE);
            return;
        }
        List<Integer> selectedRows = model.selectedRows();
        if (selectedRows.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Select at least one sorting algorithm.",
                    "No algorithms selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        PerformanceRunner.DataType type = (PerformanceRunner.DataType) dataType.getSelectedItem();
        model.clearResults();
        runtimeChart.clear();
        comparisonChart.clear();
        setRunning(true);
        status.setText("Preparing input...");
        new SwingWorker<RunSummary, RowResult>() {
            @Override
            protected RunSummary doInBackground() {
                List<? extends Comparable<?>> data = PerformanceRunner.createData(size, type, new Random());
                boolean failed = false;
                boolean anyAborted = false;
                for (int row : selectedRows) {
                    PerformanceRunner.Algorithm algorithm = model.algorithmAt(row);
                    SortCancellation cancellation = activeSort.begin();
                    publish(new RowResult(row, null, "Running"));
                    PerformanceRunner.Result result = null;
                    String error = null;
                    try {
                        result = SorterThread.run(
                                () -> PerformanceRunner.run(algorithm, data, type, cancellation),
                                cancellation);
                    } catch (RuntimeException | StackOverflowError exception) {
                        error = exception.toString();
                    }
                    boolean aborted = activeSort.finish(cancellation);
                    if (aborted) {
                        anyAborted = true;
                        publish(new RowResult(row, null, "Aborted"));
                    } else if (error != null) {
                        failed = true;
                        publish(new RowResult(row, null, error));
                    } else {
                        publish(new RowResult(row, result, null));
                    }
                }
                return new RunSummary(failed, anyAborted);
            }

            @Override
            protected void process(List<RowResult> results) {
                for (RowResult result : results) {
                    model.setResult(result);
                    String label = model.algorithmAt(result.row()).toString();
                    runtimeChart.setEntry(result.row(), label, result.result());
                    comparisonChart.setEntry(result.row(), label, result.result());
                    status.setText(("Running".equals(result.error()) ? "Running " : "Finished ")
                            + model.algorithmAt(result.row()) + ".");
                }
            }

            @Override
            protected void done() {
                setRunning(false);
                try {
                    RunSummary summary = get();
                    status.setText(summary.failed()
                            ? "Run completed with errors; see the Status column."
                            : summary.aborted()
                                    ? "Run completed with aborted sorters; see the Status column."
                                    : "Run completed. Times include comparison counting.");
                } catch (InterruptedException exception) {
                    Thread.currentThread().interrupt();
                    status.setText("Run interrupted.");
                } catch (ExecutionException exception) {
                    Throwable cause = exception.getCause();
                    status.setText("Run failed: " + cause);
                    JOptionPane.showMessageDialog(PerformanceGUI.this, cause.toString(),
                            "Run failed", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    private void setRunning(boolean running) {
        startButton.setEnabled(!running);
        abortButton.setEnabled(running);
        sizeField.setEnabled(!running);
        dataType.setEnabled(!running);
        table.setEnabled(!running);
    }

    record RowResult(int row, PerformanceRunner.Result result, String error) {}
    private record RunSummary(boolean failed, boolean aborted) {}

    static class SortTableModel extends AbstractTableModel {
        private final PerformanceRunner.Algorithm[] algorithms = PerformanceRunner.Algorithm.values();
        private final boolean[] selected = new boolean[algorithms.length];
        private final String[] times = new String[algorithms.length];
        private final String[] counts = new String[algorithms.length];
        private final String[] statuses = new String[algorithms.length];
        private final String[] columns = {"Run", "Algorithm", "Time (ms)", "Comparisons", "Status"};

        SortTableModel() {
            java.util.Arrays.fill(selected, true);
            clearResults();
        }

        PerformanceRunner.Algorithm algorithmAt(int row) {
            return algorithms[row];
        }

        List<Integer> selectedRows() {
            List<Integer> result = new ArrayList<>();
            for (int i = 0; i < selected.length; i++) {
                if (selected[i]) {
                    result.add(i);
                }
            }
            return result;
        }

        void clearResults() {
            java.util.Arrays.fill(times, "");
            java.util.Arrays.fill(counts, "");
            java.util.Arrays.fill(statuses, "");
            fireTableDataChanged();
        }

        void setResult(RowResult rowResult) {
            int row = rowResult.row();
            if (rowResult.error() != null) {
                statuses[row] = rowResult.error();
            } else {
                times[row] = String.format(Locale.US, "%,.3f", rowResult.result().elapsedNanos() / 1_000_000.0);
                counts[row] = String.format(Locale.US, "%,d", rowResult.result().comparisons());
                statuses[row] = "Completed";
            }
            fireTableRowsUpdated(row, row);
        }

        @Override public int getRowCount() { return algorithms.length; }
        @Override public int getColumnCount() { return columns.length; }
        @Override public String getColumnName(int column) { return columns[column]; }
        @Override public Class<?> getColumnClass(int column) { return column == 0 ? Boolean.class : String.class; }
        @Override public boolean isCellEditable(int row, int column) { return column == 0; }

        @Override
        public Object getValueAt(int row, int column) {
            return switch (column) {
                case 0 -> selected[row];
                case 1 -> algorithms[row].toString();
                case 2 -> times[row];
                case 3 -> counts[row];
                case 4 -> statuses[row];
                default -> throw new IndexOutOfBoundsException("Column: " + column);
            };
        }

        @Override
        public void setValueAt(Object value, int row, int column) {
            if (column == 0) {
                selected[row] = Boolean.TRUE.equals(value);
                fireTableCellUpdated(row, column);
            }
        }
    }
}
