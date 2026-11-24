package de.rwu.wi.gui;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PerformanceGUITest {
    @Test
    void tableTracksSelectionResultsAndFailures() {
        PerformanceGUI.SortTableModel model = new PerformanceGUI.SortTableModel();
        assertEquals(PerformanceRunner.Algorithm.values().length, model.getRowCount());
        assertEquals(Boolean.class, model.getColumnClass(0));
        assertTrue(model.isCellEditable(0, 0));
        assertFalse(model.isCellEditable(0, 1));
        assertEquals(model.getRowCount(), model.selectedRows().size());

        model.setValueAt(false, 0, 0);
        assertFalse(model.selectedRows().contains(0));
        model.setResult(new PerformanceGUI.RowResult(1, new PerformanceRunner.Result(1_234_567_890, 1234567), null));
        assertEquals("1,234.568", model.getValueAt(1, 2));
        assertEquals("1,234,567", model.getValueAt(1, 3));
        assertEquals("Completed", model.getValueAt(1, 4));
        model.setResult(new PerformanceGUI.RowResult(2, null, "Sort failed"));
        assertEquals("Sort failed", model.getValueAt(2, 4));
        assertEquals("", model.getValueAt(2, 2));
        model.setResult(new PerformanceGUI.RowResult(3, null, "Aborted"));
        assertEquals("Aborted", model.getValueAt(3, 4));
        assertEquals("", model.getValueAt(3, 2));
        assertEquals("", model.getValueAt(3, 3));

        model.clearResults();
        assertEquals("", model.getValueAt(1, 2));
        assertEquals("", model.getValueAt(2, 4));
        assertEquals(false, model.getValueAt(0, 0));
    }
}
