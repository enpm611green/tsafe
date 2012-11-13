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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.server_gui.feedsourcechooser.FeedSourceChooserDialog;
import tsafe.server.server_gui.utils.LayoutUtils;
import tsafe.server.server_gui.utils.table.TableUtils;

/**
 * The panel containing the data sources options.
 */
class DataSourcesConfigPanel extends ConfigPanel {


    //
    // MEMBER VARIABLES
    //

    /**
     * The parent frame.
     */
    private JFrame parent;

    /**
     * The text field displaying the fixes file.
     */
    private JTextField fixesTextField;

    /**
     * The text field displaying the airports file.
     */
    private JTextField airportsTextField;

    /**
     * The text field displaying the navaids file.
     */
    private JTextField navaidsTextField;

    /**
     * The text field displaying the airways file.
     */
    private JTextField airwaysTextField;

    /**
     * The text field displaying the sids file.
     */
    private JTextField sidsTextField;

    /**
     * The text field displaying the stars file.
     */
    private JTextField starsTextField;

    /**
     * The text field displaying the feed source.
     */
    private JTextField sourceTextField;

    /**
     * The feed source.
     */
    private Reader feedSource;
    
    
    
	//
 	// LAYOUT METHODS
	//

    //-------------------------------------------
    /**
     * Constructs a new panel to specify various data sources.
     *
     * @param parent            the parent frame
     * @param commonButtonPanel the common buttons between all config panels; assumes
     *                          this argument is not null
     */
    DataSourcesConfigPanel(JFrame parent, JPanel commonButtonPanel) {
		super();
		GridBagLayout gridbag = new GridBagLayout();
        setLayout(gridbag);
        setBorder(BorderFactory.createEmptyBorder(LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE,
                                                  LayoutUtils.PANEL_BORDERSIZE));       
        this.parent = parent;
        this.feedSource = null;


        // Init this panel's contents.  Note that since we are laying out
        // components using GridBagLayout, changes to the layout must be
        // propogated to each of the methods below.  Use "gridy" to 
        // represent the first available vertical gridpoint.
        int gridy = 0;
        MyEventListener mel = new MyEventListener();
        gridy = initDataFilesOption(gridbag, mel, gridy);
        gridy = initFeedSourceOption(gridbag, mel, gridy);
        gridy = addCommonButtonPanel(gridbag, gridy, commonButtonPanel);


        // Load the default configuration.
        loadDefaultConfigurationProperties();
    }


    //-------------------------------------------
    /**
     * Set up the text fields that specify the various data files.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initDataFilesOption(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Data Files:");
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


        //
        // Set the fixes file chooser.
        //
        label = new JLabel("Fixes:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        fixesTextField = new JTextField(20);
        fixesTextField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(fixesTextField, c);
        add(fixesTextField);        

        JButton button = new JButton("Browse");
		button.setToolTipText("Choose the fix file");
		button.setActionCommand("Fix File");
  		button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          0, LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(button, c);
        add(button);
        gridy++;


        //
        // Set the airports file chooser.
        //
        label = new JLabel("Airports:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        airportsTextField = new JTextField(20);
        airportsTextField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(airportsTextField, c);
        add(airportsTextField);        

        button = new JButton("Browse");
		button.setToolTipText("Choose the airport file");
		button.setActionCommand("Airport File");
  		button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          0, LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(button, c);
        add(button);
        gridy++;


        //
        // Set the navaids file chooser.
        //
        label = new JLabel("Navaids:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        navaidsTextField = new JTextField(20);
        navaidsTextField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(navaidsTextField, c);
        add(navaidsTextField);        

        button = new JButton("Browse");
		button.setToolTipText("Choose the navaid file");
		button.setActionCommand("Navaid File");
  		button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          0, LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(button, c);
        add(button);
        gridy++;


        //
        // Set the airways file chooser.
        //
        label = new JLabel("Airways:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));

        gridbag.setConstraints(label, c);
        add(label);

        airwaysTextField = new JTextField(20);
        airwaysTextField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(airwaysTextField, c);
        add(airwaysTextField);        

        button = new JButton("Browse");
		button.setToolTipText("Choose the airway file");
		button.setActionCommand("Airway File");
  		button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          0, LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(button, c);
        add(button);
        gridy++;


        //
        // Set the sids file chooser
        //
        label = new JLabel("Sids:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        sidsTextField = new JTextField(20);
        sidsTextField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(sidsTextField, c);
        add(sidsTextField);        

        button = new JButton("Browse");
        button.setToolTipText("Choose the sid file");
        button.setActionCommand("Sid File");
        button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          0, LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(button, c);
        add(button);
        gridy++;


        //
        // Set the stars file chooser.
        //
        label = new JLabel("Stars:");
        label.setHorizontalAlignment(JLabel.RIGHT);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
                                               LayoutUtils.LABEL_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.EAST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(label, c);
        add(label);

        starsTextField = new JTextField(20);
        starsTextField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(2, gridy,
                                               1, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
        gridbag.setConstraints(starsTextField, c);
        add(starsTextField);        

        button = new JButton("Browse");
        button.setToolTipText("Choose the star file");
        button.setActionCommand("Star File");
        button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(3, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.EAST,
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
     * Set up the feed source chooser option.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initFeedSourceOption(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Feed Source:");
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

        
        // Set the source chooser.
        sourceTextField = new JTextField(20);
        sourceTextField.setEditable(false);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               3, 1,
                                               LayoutUtils.TEXTFIELD_FILE_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN, 
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(sourceTextField, c);
        add(sourceTextField);
        

        JButton button = new JButton("Choose");
		button.setToolTipText("Choose the feed source");
		button.setActionCommand("Choose Source");
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
                
        // Load the data files.
        TSAFEProperties.restoreDefaultDataFiles();
        String[] dataFiles = TSAFEProperties.getDataFiles();
        if ((dataFiles == null) || (dataFiles.length != 6)) {
            dataFiles = new String[] {"", "", "", "", "", ""};
        }
        
//        fixesTextField.setText("datafiles/fixes.txt");
//        airportsTextField.setText("datafiles/airports.txt");
//        navaidsTextField.setText("datafiles/navaids.txt");
//        airwaysTextField.setText("datafiles/airways.txt");
//        sidsTextField.setText("datafiles/sids.txt");
//        starsTextField.setText("datafiles/stars.txt");
        
        
        
        fixesTextField.setText(dataFiles[0]);
        airportsTextField.setText(dataFiles[1]);
        navaidsTextField.setText(dataFiles[2]);
        airwaysTextField.setText(dataFiles[3]);
        sidsTextField.setText(dataFiles[4]);
        starsTextField.setText(dataFiles[5]);


        // Load the feed source.
        TSAFEProperties.restoreDefaultFeedSource();
        setFeedSource(TSAFEProperties.getFeedSource());
    }


    //-------------------------------------------
    void setConfigurationProperties() {
        
        // Set the data files.
        String[] dataFiles = new String[6];
        dataFiles[0] = fixesTextField.getText();
        dataFiles[1] = airportsTextField.getText();
        dataFiles[2] = navaidsTextField.getText();
        dataFiles[3] = airwaysTextField.getText();
        dataFiles[4] = sidsTextField.getText();
        dataFiles[5] = starsTextField.getText();
        TSAFEProperties.setDataFiles(dataFiles);


        // Set the feed source.
        TSAFEProperties.setFeedSource(feedSource);
    }


    //-------------------------------------------
    void saveConfigurationAsDefault() {
        setConfigurationProperties();
        TSAFEProperties.saveDataFilesAsDefault();
        TSAFEProperties.saveFeedSourceAsDefault();
    }


    //-------------------------------------------
    java.util.List validateInputParameters() {
        java.util.List errorMessages = new Vector();


        // Validate Fix data file.
        if (fixesTextField.getText().length() == 0) {
            errorMessages.add("Must select a fix file.");
        }
        else {
            try {
                Reader reader = new FileReader(fixesTextField.getText());
            } 
            catch (FileNotFoundException e) {
                errorMessages.add("Selected fix file not found.");
            }
        }


        // Validate Airport data file.
        if (airportsTextField.getText().length() == 0) {
            errorMessages.add("Must select an airport file.");
        }
        else {
            try {
                Reader reader = new FileReader(airportsTextField.getText());
            } 
            catch (FileNotFoundException e) {
                errorMessages.add("Selected airport file not found.");
            }
        }


        // Validate Navaid data file.
        if (navaidsTextField.getText().length() == 0) {
            errorMessages.add("Must select an navaid file.");
        }
        else {
            try {
                Reader reader = new FileReader(navaidsTextField.getText());
            } 
            catch (FileNotFoundException e) {
                errorMessages.add("Selected navaid file not found.");
            }
        }


        // Validate Airway data file.
        if (airwaysTextField.getText().length() == 0) {
            errorMessages.add("Must select an airway file.");
        }
        else {
            try {
                Reader reader = new FileReader(airwaysTextField.getText());
            } 
            catch (FileNotFoundException e) {
                errorMessages.add("Selected airway file not found.");
            }
        }

        // Validate Sid data file.
        if (sidsTextField.getText().length() == 0) {
            errorMessages.add("Must select a sid file.");
        }
        else {
            try {
                Reader reader = new FileReader(sidsTextField.getText());
            } 
            catch (FileNotFoundException e) {
                errorMessages.add("Selected sid file not found.");
            }
        }

        // Validate Star data file.
        if (starsTextField.getText().length() == 0) {
            errorMessages.add("Must select a star file.");
        }
        else {
            try {
                Reader reader = new FileReader(starsTextField.getText());
            } 
            catch (FileNotFoundException e) {
                errorMessages.add("Selected star file not found.");
            }
        }


        // Validate feed source.
        if (feedSource == null) {
            errorMessages.add("Must specify a valid feed source.");
        }


        return errorMessages;
    }



	//
 	// FIELD MAINTENANCE METHODS
	//

    //-------------------------------------------
    /**
     * Set the feed source.  If the specified source is
     * null, the source is set to null.
     *
     * @param newSource   the new source
     */
    private void setFeedSource(Reader newSource) {
        feedSource = newSource;

        if (feedSource == null) {
            sourceTextField.setText("");            
        }
        else {
            sourceTextField.setText(feedSource.toString());
        }
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


            // Feed Source Button

            // Pop up a dialog that allows the user to select a feed source.
            if (command.equals("Choose Source")) {
                FeedSourceChooserDialog chooseSourceDialog = 
                    new FeedSourceChooserDialog(TableUtils.getValuesFromDataVector(TSAFEProperties.getFeedSources(),0),
                                                feedSource, true);
                chooseSourceDialog.showDialog(parent, "Select a Feed Source");
                Reader newSource = chooseSourceDialog.getSource();
                if (newSource != null) {
                    setFeedSource(newSource);
                }
            }


            // Fix File Browse button

            else if (command.equals("Fix File")) {
                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    fixesTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }


            // Airport File Browse button

            else if (command.equals("Airport File")) {
                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    airportsTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }


            // Navaid File Browse button

            else if (command.equals("Navaid File")) {
                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    navaidsTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }


            // Airway File Browse button

            else if (command.equals("Airway File")) {
                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    airwaysTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }

            // Sid File Browse button

            else if (command.equals("Sid File")) {
                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    sidsTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }

            // Airway File Browse button

            else if (command.equals("Star File")) {
                int returnVal = fileChooser.showOpenDialog(parent);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    starsTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                }
            }


            // Default

            else {  
                System.err.println("Unrecognized command: " + command);
            }
        }

    } // inner class MyEventListener


}
