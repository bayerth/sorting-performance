package de.rwu.wi.oop;

public class MainPanel {
    public static void main(String[] args) {
// Start GUI
        int size = 10000;
        if (args.length == 1) {
            try {
                size = Integer.parseInt(args[0]);
            } catch (Exception e) {
                help();
                System.exit(-1);
            }
        }
        PanelWindow window = new PanelWindow(size);
        window.setVisible(true);
    }

    private static void help() {
        System.out.println("Arguments: none -> 3x3 Board, a single positive number n : n x n board");
    }
}
