/*
 TSAFE Prototype: A decision support tool for air traffic controllers
 Copyright (C) 2003  Gregory D. Dennis

 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */

package tsafe.server.server_gui.feedsourcechooser;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import tsafe.server.server_gui.utils.DoubleField;
import tsafe.server.server_gui.utils.LayoutUtils;
import tsafe.server.server_gui.utils.LongField;
import fig.io.FIGFileContentFilter;

/**
 * Allows a user to filter messages based on various conditions.
 */
class OtherFilterPanel extends FilterPanel {


    //
    // MEMBER VARIABLES
    //

    /**
     * The dialog that owns this panel.
     */
    private JDialog parentDialog;


    /**
     * The radio buttons used to choose the message delay.
     */
    private ButtonGroup delayRadioButtonGroup;

    /**
     * The no delay option radio button.
     */
    private JRadioButton noDelayRadioButton;


    /**
     * The constant delay option radio button.
     */
    private JRadioButton constantDelayRadioButton;

    /**
     * The amount of constant delay.
     */
    private LongField constantDelayLongField;

    /**
     * The beginning portion of the label belonging to the constant delay text field.
     * This variable is needed to allow the constant delay option to be "greyed out"
     * when not chosen.
     */
    private JLabel constantDelayLabel1;

    /**
     * The final portion of the label belonging to the constant delay text field.
     * This variable is needed to allow the constant delay option to be "greyed out"
     * when not chosen.
     */
    private JLabel constantDelayLabel2;


    /**
     * The recorded delay option radio button.
     */
    private JRadioButton recordedDelayRadioButton;

    /**
     * The amount by which the delay between messages should be scaled.
     */
    private DoubleField delayScalarDoubleField;

    /**
     * The beginning portion of the label belonging to the delay scalar text field.
     * This variable is needed to allow the delay scalar option to be "greyed out"
     * when not chosen.
     */
    private JLabel delayScalarLabel1;

    /**
     * The final portion of the label belonging to the constant delay scalar text field.
     * This variable is needed to allow the delay scalar option to be "greyed out"
     * when not chosen.
     */
    private JLabel delayScalarLabel2;


    /**
     * The filter badly formatted messages option.
     */
    private JCheckBox filterBadMessagesCheckbox;



    //
    // LAYOUT METHODS
    //

    //-------------------------------------------
    /**
     * Constructs a new panel to filter messages by airline.
     *
     * @param parentDialog      the dialog that owns this panel
     * @param commonButtonPanel the common buttons between all filtering panels; assumes
     *                          this argument is not null
     */
    OtherFilterPanel(JDialog parentDialog, JPanel commonButtonPanel) {
        super();
        GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        setBorder(BorderFactory.createEmptyBorder(LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE));       
        this.parentDialog = parentDialog;


        // Init this panel's contents.  Note that since we are laying out
        // components using GridBagLayout, changes to the layout must be
        // propogated to each of the methods below.  Use "gridy" to 
        // represent the first available vertical gridpoint.
        int gridy = 0;
        MyEventListener mel = new MyEventListener();
        gridy = initDelayOption(gridbag, mel, gridy);
        gridy = initFilterBadMessagesOption(gridbag, mel, gridy);
        gridy = addCommonButtonPanel(gridbag, gridy, commonButtonPanel);
    }


    //-------------------------------------------
    /**
     * Set up the buttons to specify delay.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     * @return  the first available vertical position in the layout after this method
     *          has finished laying out all of its components
     */
    private int initDelayOption(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Delay:");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  6, 1,
                                                                  LayoutUtils.LABEL_WEIGHTX, 
                                                                  LayoutUtils.PANEL_B_WEIGHTY_TOPMOST,
                                                                  GridBagConstraints.HORIZONTAL, 
                                                                  GridBagConstraints.SOUTHWEST,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_ABOVE_TOPMOST, 
                                                                             0,0,0));
        gridbag.setConstraints(label, c);
        add(label);
        gridy++;


        // Set the instructions.
        JTextArea instructions = new JTextArea("Choose whether to include no delays, constant delay, or the " +
                                               "original recorded delays between messages during playback.", 2, 40);
        instructions.setOpaque(false);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               6, 1,
                                               LayoutUtils.TEXTAREA_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.BOTH, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(instructions, c);
        add(instructions);
        gridy++;


        // Create the radio buttons.
        noDelayRadioButton = new JRadioButton("No delay");
        noDelayRadioButton.setActionCommand("No delay");
        noDelayRadioButton.addActionListener(mel);
        noDelayRadioButton.setHorizontalAlignment(JRadioButton.LEFT);
        noDelayRadioButton.setHorizontalTextPosition(JRadioButton.RIGHT);
        delayRadioButtonGroup = new ButtonGroup();
        delayRadioButtonGroup.add(noDelayRadioButton);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               0.0,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(noDelayRadioButton, c);
        add(noDelayRadioButton);
        gridy++;
        
        
        constantDelayRadioButton = new JRadioButton("Constant delay");
        constantDelayRadioButton.setActionCommand("Constant delay");
        constantDelayRadioButton.addActionListener(mel);
        constantDelayRadioButton.setHorizontalAlignment(JRadioButton.LEFT);
        constantDelayRadioButton.setHorizontalTextPosition(JRadioButton.RIGHT);
        delayRadioButtonGroup.add(constantDelayRadioButton);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               0.0,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(constantDelayRadioButton, c);
        add(constantDelayRadioButton);
        
        constantDelayLabel1 = new JLabel("(Milliseconds: ");
        constantDelayLabel1.setEnabled(false);
        constantDelayLabel1.setHorizontalAlignment(JLabel.LEFT);
        constantDelayLabel1.setVerticalAlignment(JLabel.BOTTOM);
        c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                               1, 1,
                                               0.0, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(constantDelayLabel1, c);
        add(constantDelayLabel1);

        constantDelayLongField = new LongField(5, true);
        constantDelayLongField.setEnabled(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               0.0, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          0, 0, 0));
        gridbag.setConstraints(constantDelayLongField, c);
        add(constantDelayLongField);

        constantDelayLabel2 = new JLabel(" )");
        constantDelayLabel2.setEnabled(false);
        constantDelayLabel2.setHorizontalAlignment(JLabel.LEFT);
        constantDelayLabel2.setVerticalAlignment(JLabel.BOTTOM);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               0.0, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          0, 0, 0));
        gridbag.setConstraints(constantDelayLabel2, c);
        add(constantDelayLabel2);
        gridy++;
        

        recordedDelayRadioButton = new JRadioButton("Recorded delay");
        recordedDelayRadioButton.setActionCommand("Recorded delay");
        recordedDelayRadioButton.addActionListener(mel);
        recordedDelayRadioButton.setHorizontalAlignment(JRadioButton.LEFT);
        recordedDelayRadioButton.setHorizontalTextPosition(JRadioButton.RIGHT);
        delayRadioButtonGroup.add(recordedDelayRadioButton);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               0.0,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(recordedDelayRadioButton, c);
        add(recordedDelayRadioButton);


        delayScalarLabel1 = new JLabel("(Scale Factor: ");
        delayScalarLabel1.setEnabled(false);
        delayScalarLabel1.setHorizontalAlignment(JLabel.LEFT);
        delayScalarLabel1.setVerticalAlignment(JLabel.BOTTOM);
        c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                               1, 1,
                                               0.0, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(delayScalarLabel1, c);
        add(delayScalarLabel1);

        delayScalarDoubleField = new DoubleField(5, true);
        delayScalarDoubleField.setEnabled(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               0.0, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          0, 0, 0));
        gridbag.setConstraints(delayScalarDoubleField, c);
        add(delayScalarDoubleField);

        delayScalarLabel2 = new JLabel(" )");
        delayScalarLabel2.setEnabled(false);
        delayScalarLabel2.setHorizontalAlignment(JLabel.LEFT);
        delayScalarLabel2.setVerticalAlignment(JLabel.BOTTOM);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               0.0, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          0, 0, 0));
        gridbag.setConstraints(delayScalarLabel2, c);
        add(delayScalarLabel2);
        gridy++;
 

        // Set the "Recorded Delay" radio button as the default.
        recordedDelayRadioButton.doClick();

        
        return gridy;
    }


    //-------------------------------------------
    /**
     * Set up the checkbox to toggle the filtering of badly formatted messages.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     * @return  the first available vertical position in the layout after this method
     *          has finished laying out all of its components
     */
    private int initFilterBadMessagesOption(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Bad Messages:");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  6, 1,
                                                                  LayoutUtils.LABEL_WEIGHTX, 
                                                                  LayoutUtils.PANEL_B_WEIGHTY_ABOVE,
                                                                  GridBagConstraints.HORIZONTAL, 
                                                                  GridBagConstraints.SOUTHWEST,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_BETWEEN, 
                                                                             0,0,0));
        gridbag.setConstraints(label, c);
        add(label);
        gridy++;


        // Set the instructions.
        JTextArea instructions = new JTextArea("Toggle whether to filter badly formatted messages " +
                                               "during playback.", 2, 40);
        instructions.setOpaque(false);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               6, 1,
                                               LayoutUtils.TEXTAREA_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.BOTH, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(instructions, c);
        add(instructions);
        gridy++;


        // Create the checkbox.
        filterBadMessagesCheckbox = new JCheckBox("Filter badly formatted messages");
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               0.0,
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(filterBadMessagesCheckbox, c);
        add(filterBadMessagesCheckbox);
        gridy++;
 
        
        return gridy;
    }


    //-------------------------------------------
    /**
     * Add the common buttons to this filtering panel.
     *
     * @param gridbag           the gridbag layout object
     * @param gridy             the first available vertical position in the layout
     * @param commonButtonPanel the common buttons between all filtering panels
     * @return  the first available vertical position in the layout after this method
     *          has finished laying out all of its components
     */
    private int addCommonButtonPanel(GridBagLayout gridbag, int gridy, JPanel commonButtonPanel) {
        
        // Create a visual separator.
        JSeparator separator = new JSeparator();
        GridBagConstraints c = 
            LayoutUtils.makeGridBagConstraints(0, gridy,
                                               6, 1,
                                               LayoutUtils.PANEL_SP_WEIGHTX, 
                                               LayoutUtils.PANEL_SP_WEIGHTY,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.SOUTH,
                                               new Insets(LayoutUtils.PANEL_SP_SPACE_ABOVE_FINAL_BUTTONP,
                                                          0,0,0));
        gridbag.setConstraints(separator, c);
        add(separator);
        gridy++;
        

        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               6, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_FINAL_BUTTONP, 
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.NORTH,
                                               new Insets(LayoutUtils.BUTTONP_BORDERSIZE +
                                                          LayoutUtils.PANEL_SP_SPACE_BELOW_FINAL_BUTTONP,
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          LayoutUtils.BUTTONP_BORDERSIZE +
                                                          LayoutUtils.PANEL_B_SPACE_BELOW_BOTTOMMOST,
                                                          LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(commonButtonPanel, c);
        add(commonButtonPanel);        
        gridy++;

           
        return gridy;
    }



	//
 	// FILTER_PANEL METHODS
	//

    //-------------------------------------------
    void setFilteringConditions(FIGFileContentFilter filter) {        
        super.setFilteringConditions(filter);

        // Set delay options.
        long constantDelay = filter.getConstantDelay();
        double delayScalar = filter.getDelayScalar();
        delayScalarDoubleField.setValue(delayScalar);

        if ((constantDelay == 0) || (delayScalar == 0)) {
            noDelayRadioButton.doClick();
        }
        else if (constantDelay > 0) {
            constantDelayLongField.setValue(constantDelay);
            constantDelayRadioButton.doClick();
        }
        else {
            recordedDelayRadioButton.doClick();
        }


        // Set bad message filter option.
        filterBadMessagesCheckbox.setSelected(filter.getFilterBadMessages());
    }


    //-------------------------------------------
    void updateContentFilter(FIGFileContentFilter filter) {
        if (filter != null) {

            // Update delay options.
            ButtonModel bModel = delayRadioButtonGroup.getSelection();
            String actionCommand = bModel.getActionCommand();

            double scalar = 1.0;
            try {
                scalar = delayScalarDoubleField.getValue();
            }
            catch (NumberFormatException nfe) {
                scalar = 1.0;
            }
            filter.setDelayScalar(scalar);

            long constantDelay = -1;
            try {
                constantDelay = constantDelayLongField.getValue();
            }
            catch (NumberFormatException nfe) {
                constantDelay = -1;
            }
            filter.setConstantDelay(constantDelay);

            if (actionCommand.equals("No delay")) {
                filter.setConstantDelay(-1);
                filter.setDelayScalar(0);
            }
            if (actionCommand.equals("Constant delay")) {
                filter.setDelayScalar(1.0);
            }
            else if (actionCommand.equals("Recorded delay")) {
                filter.setConstantDelay(-1);
            }


            // Update bad message filter option.
            filter.setFilterBadMessages(filterBadMessagesCheckbox.isSelected());
        }        
    }




    //
    // INNER CLASSES
    //

    /**
     * Listens and responds to any events fired on behalf of the 
     * buttons or lists in this filter panel.
     */
    private class MyEventListener implements ActionListener {
        
        //
        // METHODS
        //
        
        //-------------------------------------------
        /**
         * Responds to any events thrown by the panel's buttons.
         *
         * @param event the ActionEvent
         */
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();


            // No Delay radio button

            if (command.equals("No delay")) {
                constantDelayLongField.setEnabled(false);
                constantDelayLabel1.setEnabled(false);
                constantDelayLabel2.setEnabled(false);
                delayScalarDoubleField.setEnabled(false);
                delayScalarLabel1.setEnabled(false);
                delayScalarLabel2.setEnabled(false);
            }


            // Constant Delay radio button

            else if (command.equals("Constant delay")) {
                constantDelayLongField.setEnabled(true);
                constantDelayLabel1.setEnabled(true);
                constantDelayLabel2.setEnabled(true);
                delayScalarDoubleField.setEnabled(false);
                delayScalarLabel1.setEnabled(false);
                delayScalarLabel2.setEnabled(false);
            }


            // Recorded Delay radio button

            else if (command.equals("Recorded delay")) {
                constantDelayLongField.setEnabled(false);
                constantDelayLabel1.setEnabled(false);
                constantDelayLabel2.setEnabled(false);
                delayScalarDoubleField.setEnabled(true);
                delayScalarLabel1.setEnabled(true);
                delayScalarLabel2.setEnabled(true);
            }


            // Default

            else {
                System.err.println("Unrecognized command: " + command);
            }
        }

    } // inner class MyEventListener

}
