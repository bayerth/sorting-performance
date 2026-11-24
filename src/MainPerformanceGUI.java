import de.rwu.wi.gui.PerformanceGUI;

import javax.swing.SwingUtilities;

public class MainPerformanceGUI {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new PerformanceGUI().setVisible(true));
    }
}
