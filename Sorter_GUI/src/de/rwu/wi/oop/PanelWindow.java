package de.rwu.wi.oop;

import javax.swing.*;
import java.awt.*;

public class PanelWindow extends JFrame {

    private JPanel mainPane; // List<ResultPanel> panels = new LinkedList<>();
    private ResultPanel bubbleSortPanel;
    private ResultPanel bubbleSortLinkedListPanel;
    private ResultPanel mergeSortPanel;
    public static final int DEFAULT_DIMENSION = 3;


    public PanelWindow() {
        super("Sortieren");
        initGUI(DEFAULT_DIMENSION);
    }

    public PanelWindow(int dimension) {
        // Frame title
        super("Sortieren");
        initGUI(dimension);
    }

    // -- Functionality ---------------------------------------------------------------------------

    // -- Initialization --------------------------------------------------------------------------

    public void initGUI(int dimension) {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        bubbleSortPanel = new ResultPanel("BubbleSort");
        bubbleSortLinkedListPanel = new ResultPanel("BubbleSort LL");

        mainPane = new JPanel();
        mainPane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 1;
        mainPane.add(bubbleSortPanel, c);
        c = new GridBagConstraints();
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 3;
        c.gridheight = 1;
        mainPane.add(bubbleSortLinkedListPanel, c);

        this.setContentPane(mainPane);

    }

    /**
     * Code generated From GUI Editor do not modify
     */

    private void initComponents() {
        pack();
    }

    public ResultPanel getBubbleSortPanel() {
        return bubbleSortPanel;
    }



}
