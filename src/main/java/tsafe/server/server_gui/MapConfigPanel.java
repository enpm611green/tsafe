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

package tsafe.server.server_gui;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.server_gui.utils.LayoutUtils;

/**
 * The panel containing the map and location options.
 */
class MapConfigPanel extends ConfigPanel {


    //
    // MEMBER VARIABLES
    //

    /**
     * The parent frame.
     */
    private JFrame parent;


    /**
     * Text fields to extract configurations from
     */
    private JTextField bgImageFileField;

    private JTextField minLatDegreesField;
    private JTextField minLatMinutesField;
    private JTextField maxLatDegreesField;
    private JTextField maxLatMinutesField;

    private JTextField minLonDegreesField;
    private JTextField minLonMinutesField;
    private JTextField maxLonDegreesField;
    private JTextField maxLonMinutesField;


    /**
     * Toggle buttons for north/south or east/west directions
     */
    private JToggleButton minLatDirButton; 
    private JToggleButton maxLatDirButton;

    private JToggleButton minLonDirButton;
    private JToggleButton maxLonDirButton;
    
    
    
	//
 	// LAYOUT METHODS
	//

    //-------------------------------------------
    /**
     * Constructs a new panel to specify various map and location properties.
     *
     * @param parent            the parent frame
     * @param commonButtonPanel the common buttons between all config panels; assumes
     *                          this argument is not null
     */
    MapConfigPanel(JFrame parent, JPanel commonButtonPanel) {
		super();
		GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        setBorder(BorderFactory.createEmptyBorder(LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE));       
        this.parent = parent;


        // Init this panel's contents.  Note that since we are laying out
        // components using GridBagLayout, changes to the layout must be
        // propogated to each of the methods below.  Use "gridy" to 
        // represent the first available vertical gridpoint.
        int gridy = 0;
        MyEventListener mel = new MyEventListener();
        gridy = initBackgroundImageOption(gridbag, mel, gridy);
        gridy = initLatitudeOption(gridbag, mel, gridy);
        gridy = initLongitudeOption(gridbag, mel, gridy);
        gridy = addCommonButtonPanel(gridbag, gridy, commonButtonPanel);


        // Load the default configuration.
        loadDefaultConfigurationProperties();
    }


    //-------------------------------------------
    /**
     * Set up the background image chooser.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initBackgroundImageOption(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Background Image:");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  4, 1,
                                                                  LayoutUtils.LABEL_WEIGHTX, 
                                                                  LayoutUtils.PANEL_B_WEIGHTY_TOPMOST,
                                                                  GridBagConstraints.HORIZONTAL, 
                                                                  GridBagConstraints.SOUTHWEST,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_ABOVE_TOPMOST,
                                                                             0,0,0));
        gridbag.setConstraints(label, c);
        add(label);
        gridy++;

        
        // Set the image chooser.
        bgImageFileField = new JTextField(20);
        bgImageFileField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               3, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(bgImageFileField, c);
        add(bgImageFileField);
        

        JButton button = new JButton("Choose");
		button.setToolTipText("Choose the background image");
		button.setActionCommand("Choose Background");
  		button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.NORTHEAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          0, LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(button, c);
        add(button);
        gridy++;


        return gridy;
    }


    //-------------------------------------------
    /**
     * Set up the text fields that specify the latitude constraints.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initLatitudeOption(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Latitude:");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  4, 1,
                                                                  LayoutUtils.LABEL_WEIGHTX, 
                                                                  LayoutUtils.PANEL_B_WEIGHTY_ABOVE,
                                                                  GridBagConstraints.HORIZONTAL, 
                                                                  GridBagConstraints.SOUTHWEST,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_BETWEEN,
                                                                             0,0,0));
        gridbag.setConstraints(label, c);
        add(label);
        gridy++;

        
        // Set the minimum latitude picker.
        label = new JLabel("Minimum:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        minLatDegreesField = new JTextField(3);
        minLatMinutesField = new JTextField(3);
        minLatDirButton = new JToggleButton("N");
        minLatDirButton.addActionListener(new DirectionButtonListener(minLatDirButton, 'N', 'S'));

        JPanel panel = new JPanel();
        panel.add(minLatDegreesField);
        panel.add(new JLabel("degrees"));
        panel.add(minLatMinutesField);
        panel.add(new JLabel("minutes"));
        panel.add(minLatDirButton);

        c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                               3, 1,
                                               0.9, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(panel, c);
        add(panel);
        gridy++;


        // Set the maximum latitude picker.
        label = new JLabel("Maximum:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHEAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        maxLatDegreesField = new JTextField(3);
        maxLatMinutesField = new JTextField(3);
        maxLatDirButton = new JToggleButton("N");
        maxLatDirButton.addActionListener(new DirectionButtonListener(maxLatDirButton, 'N', 'S'));

        panel = new JPanel();
        panel.add(maxLatDegreesField);
        panel.add(new JLabel("degrees"));
        panel.add(maxLatMinutesField);
        panel.add(new JLabel("minutes"));
        panel.add(maxLatDirButton);

        c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                               3, 1,
                                               0.9, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(panel, c);
        add(panel);
        gridy++;


        return gridy;
    }


    //-------------------------------------------
    /**
     * Set up the text fields that specify the longitude constraints.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initLongitudeOption(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Longitude:");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  4, 1,
                                                                  LayoutUtils.LABEL_WEIGHTX, 
                                                                  LayoutUtils.PANEL_B_WEIGHTY_ABOVE,
                                                                  GridBagConstraints.HORIZONTAL, 
                                                                  GridBagConstraints.SOUTHWEST,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_BETWEEN,
                                                                             0,0,0));
        gridbag.setConstraints(label, c);
        add(label);
        gridy++;

        
        // Set the minimum longitude picker.
        label = new JLabel("Minimum:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        minLonDegreesField = new JTextField(3);
        minLonMinutesField = new JTextField(3);
        minLonDirButton = new JToggleButton("E");
        minLonDirButton.addActionListener(new DirectionButtonListener(minLonDirButton, 'E', 'W'));

        JPanel panel = new JPanel();
        panel.add(minLonDegreesField);
        panel.add(new JLabel("degrees"));
        panel.add(minLonMinutesField);
        panel.add(new JLabel("minutes"));
        panel.add(minLonDirButton);

        c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                               3, 1,
                                               0.9, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(panel, c);
        add(panel);
        gridy++;


        // Set the maximum longitude picker.
        label = new JLabel("Maximum:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHEAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        maxLonDegreesField = new JTextField(3);
        maxLonMinutesField = new JTextField(3);
        maxLonDirButton = new JToggleButton("E");
        maxLonDirButton.addActionListener(new DirectionButtonListener(maxLonDirButton, 'E', 'W'));

        panel = new JPanel();
        panel.add(maxLonDegreesField);
        panel.add(new JLabel("degrees"));
        panel.add(maxLonMinutesField);
        panel.add(new JLabel("minutes"));
        panel.add(maxLonDirButton);

        c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                               3, 1,
                                               0.9, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(panel, c);
        add(panel);
        gridy++;


        return gridy;
    }


    //-------------------------------------------
    /**
     * Add the common buttons to this preference panel.
     *
     * @param gridbag           the gridbag layout object
     * @param gridy             the first available vertical position in the layout
     * @param commonButtonPanel the common buttons between all preference panels
     * @return  the first available vertical position in the layout after this method
     *          has finished laying out all of its components
     */
    private int addCommonButtonPanel(GridBagLayout gridbag, int gridy, JPanel commonButtonPanel) {
        
        // Create a visual separator.
        JSeparator separator = new JSeparator();
        GridBagConstraints c = 
            LayoutUtils.makeGridBagConstraints(0, gridy,
                                               4, 1,
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
                                               4, 1,
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
    // CONFIG PANEL METHODS
    //

    //-------------------------------------------
    void loadDefaultConfigurationProperties() {

        // Load the background image.
        TSAFEProperties.restoreDefaultBackgroundImage();
        bgImageFileField.setText(TSAFEProperties.getBackgroundImage());


        // Load the latitude constraints.
        TSAFEProperties.restoreDefaultLatitudeConstraints();
        String[] constraints = TSAFEProperties.getLatitudeConstraints();
        if ((constraints == null) || (constraints.length != 6)) {
            constraints = new String[] {"", "", "N", "", "", "N"};
        }
        minLatDegreesField.setText(constraints[0]);
        minLatMinutesField.setText(constraints[1]);
        minLatDirButton.setText(constraints[2]);
        if (constraints[2].equals("S")) {
            minLatDirButton.setSelected(true);
        }
        maxLatDegreesField.setText(constraints[3]);
        maxLatMinutesField.setText(constraints[4]);
        maxLatDirButton.setText(constraints[5]);
        if (constraints[5].equals("S")) {
            maxLatDirButton.setSelected(true);
        }


        // Load the longitude constraints.
        TSAFEProperties.restoreDefaultLongitudeConstraints();
        constraints = TSAFEProperties.getLongitudeConstraints();
        if ((constraints == null) || (constraints.length != 6)) {
            constraints = new String[] {"", "", "E", "", "", "E"};
        }
        minLonDegreesField.setText(constraints[0]);
        minLonMinutesField.setText(constraints[1]);
        minLonDirButton.setText(constraints[2]);
        if (constraints[2].equals("W")) {
            minLonDirButton.setSelected(true);
        }
        maxLonDegreesField.setText(constraints[3]);
        maxLonMinutesField.setText(constraints[4]);
        maxLonDirButton.setText(constraints[5]);
        if (constraints[5].equals("W")) {
            maxLonDirButton.setSelected(true);
        }
    }
 

    //-------------------------------------------
    void setConfigurationProperties() {

        // Set the background image.
        TSAFEProperties.setBackgroundImage(bgImageFileField.getText());


        // Set the latitude constraints.
        String[] constraints = new String[6];
        constraints[0] = minLatDegreesField.getText();
        constraints[1] = minLatMinutesField.getText();
        constraints[2] = minLatDirButton.getText();
        constraints[3] = maxLatDegreesField.getText();
        constraints[4] = maxLatMinutesField.getText();
        constraints[5] = maxLatDirButton.getText();
        TSAFEProperties.setLatitudeConstraints(constraints);


        // Set the longitude constraints.
        constraints = new String[6];
        constraints[0] = minLonDegreesField.getText();
        constraints[1] = minLonMinutesField.getText();
        constraints[2] = minLonDirButton.getText();
        constraints[3] = maxLonDegreesField.getText();
        constraints[4] = maxLonMinutesField.getText();
        constraints[5] = maxLonDirButton.getText();
        TSAFEProperties.setLongitudeConstraints(constraints);
    }


    //-------------------------------------------
    void saveConfigurationAsDefault() {
        setConfigurationProperties();
        TSAFEProperties.saveBackgroundImageAsDefault();
        TSAFEProperties.saveLatitudeConstraintsAsDefault();
        TSAFEProperties.saveLongitudeConstraintsAsDefault();
    }


    //-------------------------------------------
    java.util.List validateInputParameters() {
        java.util.List errorMessages = new Vector();


        // Validate that an image file was specified.
        if (bgImageFileField.getText().length() == 0) {
            errorMessages.add("Must select a background image file.");
        }


        // Validate lat/lon bounds.
        try {
            int minLatDegrees = Integer.parseInt(minLatDegreesField.getText());
            int minLatMinutes = Integer.parseInt(minLatMinutesField.getText());
            int minLonDegrees = Integer.parseInt(minLonDegreesField.getText());
            int minLonMinutes = Integer.parseInt(minLonMinutesField.getText());
            int maxLatDegrees = Integer.parseInt(maxLatDegreesField.getText());
            int maxLatMinutes = Integer.parseInt(maxLatMinutesField.getText());
            int maxLonDegrees = Integer.parseInt(maxLonDegreesField.getText());
            int maxLonMinutes = Integer.parseInt(maxLonMinutesField.getText());

            int minLatSign = minLatDirButton.getText().charAt(0) == 'N' ? 1 : -1;
            int minLonSign = minLonDirButton.getText().charAt(0) == 'E' ? 1 : -1;
            int maxLatSign = maxLatDirButton.getText().charAt(0) == 'N' ? 1 : -1;
            int maxLonSign = maxLonDirButton.getText().charAt(0) == 'E' ? 1 : -1;


            double minLat = minLatSign * (minLatDegrees + (minLatMinutes / 60.0));
            double minLon = minLonSign * (minLonDegrees + (minLonMinutes / 60.0));
            double maxLat = maxLatSign * (maxLatDegrees + (maxLatMinutes / 60.0));
            double maxLon = maxLonSign * (maxLonDegrees + (maxLonMinutes / 60.0));

            if (maxLat <= minLat || maxLon <= minLon) {
                errorMessages.add("Lat/lon maximums must be greater than minimums.");
            }

            if (minLat < -90 || minLat > 90 || maxLat < -90 || maxLat > 90) {
                errorMessages.add("Latitudes must be between -90.0 and 90.0.");
            }

            if (minLon < -180 || maxLon > 180 || minLon < -180 || maxLon > 180) {
                errorMessages.add("Longitudes must be between -180.0 and 180.0.");
            }
        } 
        catch (NumberFormatException e) {
            errorMessages.add("Lat/lon numbers must be integers.");
        }

        return errorMessages;
    }




    //
    // INNER CLASSES
    //

    /**
     * Listens and responds to any events fired on behalf of the 
     * buttons in this panel.
     */
    private class MyEventListener implements ActionListener {


        //
        // METHODS
        //

        //-------------------------------------------
        /**
         * Responds to any events thrown by this panel's buttons.
         *
         * @param event the ActionEvent
         */
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();


            // Choose Button

            if (command.equals("Choose Background")) {
                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    bgImageFileField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }


            // Default

            else {  
                System.err.println("Unrecognized command: " + command);
            }
        }

    } // inner class MyEventListener




    /**
     * Toggles the direction on the toggle buttons.
     */
    private static class DirectionButtonListener implements ActionListener {

        //
        // MEMBER VARIABLES
        //

        private JToggleButton button;
        private char dir1;
        private char dir2;



        //
        // METHODS
        //
 
        //-------------------------------------------       
        /**
         * Constructs a new toggle button listener for directional buttons.
         *
         * @param button  the toggle button
         * @param dir1    the first valid direction
         * @param dir2    the second valid direction
         */
        public DirectionButtonListener(JToggleButton button, char dir1, char dir2) {
            this.button = button;
            this.dir1 = dir1;
            this.dir2 = dir2;
        }


        //-------------------------------------------
        public void actionPerformed(ActionEvent e) {
            button.setText("" + (button.getText().charAt(0) == dir1 ? dir2 : dir1));
        }

    } // inner class DirectionButtonListener


}
