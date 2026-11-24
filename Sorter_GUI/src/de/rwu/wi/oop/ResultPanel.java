package de.rwu.wi.oop;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.NumberFormat;

public class ResultPanel<T> extends JPanel {

    private TextField nameTF;
    private JFormattedTextField timeFTF;
    private JFormattedTextField compFTF;
    NumberFormat numberFormat;

    // --------------------------------------------------------------------------------------------
    // API
    // --------------------------------------------------------------------------------------------

    public ResultPanel(String name) {
        //super(new GridLayout(1,3));
        super(new GridBagLayout());
        init(name);
    }

    public void init(String name) {
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(0);
        NumberFormatter formatter = new NumberFormatter(numberFormat);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);//seems to be a no-op --
        //aha -- it changes the value property but doesn't cause the result to
        //be parsed (that happens on focus loss/return, I think).

        nameTF = new TextField(name);

        compFTF = new JFormattedTextField(formatter);
        compFTF.setColumns(12);
        compFTF.setValue(22100000);
        timeFTF = new JFormattedTextField(formatter);
        timeFTF.setColumns(12);
        timeFTF.setValue(123456789);

        GridBagConstraints c = new GridBagConstraints();

        //c.weightx = 0.5;

        //c.fill = GridBagConstraints.HORIZONTAL;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 3;
        c.gridheight = 1;
        super.add(nameTF, c);
        c = new GridBagConstraints();
        c.gridx = 3;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(compFTF, c);
        c = new GridBagConstraints();
        c.gridx = 4;
        c.gridy = 0;
        c.gridwidth = 1;
        c.gridheight = 1;
        this.add(timeFTF, c);
    }

    public void draw() {

        // Notify the panel to update
        repaint();
        revalidate();
    }

}
