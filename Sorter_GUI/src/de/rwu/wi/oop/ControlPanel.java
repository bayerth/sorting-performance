package de.rwu.wi.oop;

import de.rwu.wi.samples.ConverterRangeModel;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.text.NumberFormat;

public class ControlPanel extends JPanel implements PropertyChangeListener {

    JFormattedTextField textField;
    JSlider slider;
    MainNewPanel controller;
    String title;
    NumberFormat numberFormat;

    final static boolean MULTICOLORED = false;
    final static int MAX = 10000;

    int nrOfElements;

    ControlPanel(MainNewPanel myController, String myTitle, ConverterRangeModel myModel) {
        if(MULTICOLORED) {
            setOpaque(true);
            setBackground(new Color(0, 255, 255));
        }
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(myTitle),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)));

        //Save arguments in instance variables.
        controller = myController;
        title = myTitle;
        //sliderModel = myModel;

        //Create the text field format, and then the text field.
        numberFormat = NumberFormat.getNumberInstance();
        numberFormat.setMaximumFractionDigits(2);
        NumberFormatter formatter = new NumberFormatter(numberFormat);
        formatter.setAllowsInvalid(false);
        formatter.setCommitsOnValidEdit(true);//seems to be a no-op --
        //aha -- it changes the value property but doesn't cause the result to
        //be parsed (that happens on focus loss/return, I think).
        //
        textField = new JFormattedTextField(formatter);
        textField.setColumns(10);
        //textField.setValue(new Double(sliderModel.getDoubleValue()));
        textField.addPropertyChangeListener(this);

        //Add the slider.
        slider = new JSlider(JSlider.HORIZONTAL);
        initSlider();

        //Make the text field/slider group a fixed size
        //to make stacked ConversionPanels nicely aligned.
        JPanel unitGroup = new JPanel() {
            public Dimension getMinimumSize() {
                return getPreferredSize();
            }

            public Dimension getPreferredSize() {
                return new Dimension(150, super.getPreferredSize().height);
            }

            public Dimension getMaximumSize() {
                return getPreferredSize();
            }
        };
        unitGroup.setLayout(new BoxLayout(unitGroup, BoxLayout.PAGE_AXIS));
        if(MULTICOLORED) {
            unitGroup.setOpaque(true);
            unitGroup.setBackground(new Color(0, 0, 255));
        }
        unitGroup.setBorder(BorderFactory.createEmptyBorder(
                0, 0, 0, 5));
        unitGroup.add(textField);
        unitGroup.add(slider);

        //Create a subpanel so the combo box isn't too tall
        //and is sufficiently wide.
        JPanel chooserPanel = new JPanel();
        chooserPanel.setLayout(new BoxLayout(chooserPanel,                                             BoxLayout.PAGE_AXIS));
        if(MULTICOLORED) {
            chooserPanel.setOpaque(true);
            chooserPanel.setBackground(new Color(255, 0, 255));
        }
        //chooserPanel.add(unitChooser);
        chooserPanel.add(Box.createHorizontalStrut(100));

        //Put everything together.
        setLayout(new BoxLayout(this, BoxLayout.LINE_AXIS));
        add(unitGroup);
        add(chooserPanel);
        unitGroup.setAlignmentY(TOP_ALIGNMENT);
        chooserPanel.setAlignmentY(TOP_ALIGNMENT);
    }

    //Don't allow this panel to get taller than its preferred size.
    //BoxLayout pays attention to maximum size, though most layout
    //managers don't.
    public Dimension getMaximumSize() {
        return new Dimension(Integer.MAX_VALUE,
                             getPreferredSize().height);
    }

    /**
     * Returns the multiplier (units/meter) for the currently
     * selected unit of measurement.
     */


    /**
     * Updates the text field when the main data model is updated.
     */

    /**
     * Detects when the value of the text field (not necessarily the same
     * number as you'd get from getText) changes.
     */
    public void propertyChange(PropertyChangeEvent e) {
        if("value".equals(e.getPropertyName())) {
            Number value = (Number) e.getNewValue();
            // write to slider!
        }
    }
    private void initSlider() {
        slider.setMinimum(3);
        slider.setMaximum(9);
        slider.setValue(4);
        this.nrOfElements=10000;
        ControlPanel.this.textField.setValue(nrOfElements);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.setPaintTicks(true);
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        if (slider.getChangeListeners().length == 0) {
            slider.addChangeListener(e -> {
                //System.out.println("value = " + ((JSlider) e.getSource()).getValue());
                JSlider source = (JSlider) e.getSource();
                if (source.getValueIsAdjusting()) { // change induced by sliding (not by setValue())
                    int value = ((JSlider) e.getSource()).getValue();
                    ControlPanel.this.nrOfElements = (int) Math.pow(10, value);
                    ControlPanel.this.textField.setValue(nrOfElements);
                }
            });
        }
    }
}