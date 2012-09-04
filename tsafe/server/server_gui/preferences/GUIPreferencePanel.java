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

package tsafe.server.server_gui.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.server_gui.utils.LayoutUtils;

/**
 * Allows a user to specify various GUI preferences.
 */
class GUIPreferencePanel extends PreferencePanel {


    //
    // MEMBER VARIABLES
    //

    /**
     * The dialog that owns this panel.
     */
    private JDialog parentDialog;

    /**
     * If this checkbox is selected, the splash screen will be shown
     * when FIG is first started.
     */
    private JCheckBox showSplashCheckbox;

    /**
     * If this checkbox is selected, FIG will remember the last size
     * of each window.
     */
    private JCheckBox rememberWindowSizesCheckbox;

    /**
     * If this checkbox is selected, FIG will remember the last location
     * of each window.
     */
    private JCheckBox rememberWindowLocationsCheckbox;



    //
    // LAYOUT METHODS
    //

    //-------------------------------------------
    /**
     * Constructs a new panel to specify the GUI preferences.
     *
     * @param parentDialog      the dialog that owns this panel
     * @param commonButtonPanel the common buttons between all preference panels; assumes
     *                          this argument is not null
     */
    GUIPreferencePanel(JDialog parentDialog, JPanel commonButtonPanel) {
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
        MyEventListener mel = null;
        gridy = initSplashCheckbox(gridbag, mel, gridy);
        gridy = initWindowPreferencesCheckboxes(gridbag, mel, gridy);
        gridy = addCommonButtonPanel(gridbag, gridy, commonButtonPanel);


        // Load the current system preferences.
        loadSystemPreferences();
    }


    //-------------------------------------------
    /**
     * Set up the "Show Splash" checkbox.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initSplashCheckbox(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the label.
        JLabel label = new JLabel("Splash Screen:");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  2, 1,
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
        JTextArea instructions = new JTextArea("Toggle whether to show the splash screen " +
                                               "each time FIG starts.", 2, 40);
        instructions.setOpaque(false);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
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
        showSplashCheckbox = new JCheckBox("Show splash screen at startup");
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               LayoutUtils.CHECKBOX_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(showSplashCheckbox, c);
        add(showSplashCheckbox);
        gridy++;


        return gridy;
    }


    //-------------------------------------------
    /**
     * Set up the "Window Sizes" and "Window Locations" checkbox.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initWindowPreferencesCheckboxes(GridBagLayout gridbag, MyEventListener mel, int gridy) {
            
        // Set the label.
        JLabel label = new JLabel("Window Preferences:");
        label.setHorizontalAlignment(JLabel.LEFT);
        label.setVerticalAlignment(JLabel.BOTTOM);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  2, 1,
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
        JTextArea instructions = new JTextArea("Toggle whether to remember the last size and location " +
                                               "of each window.", 2, 40);
        instructions.setOpaque(false);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
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
        rememberWindowSizesCheckbox = new JCheckBox("Remember window sizes");
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               LayoutUtils.CHECKBOX_WEIGHTX, 
                                               LayoutUtils.PANEL_IB_WEIGHTY_BETWEEN,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.WEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(rememberWindowSizesCheckbox, c);
        add(rememberWindowSizesCheckbox);
        gridy++;


        // Create the checkbox.
        rememberWindowLocationsCheckbox = new JCheckBox("Remember window locations");
        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               1, 1,
                                               LayoutUtils.CHECKBOX_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW,
                                               GridBagConstraints.HORIZONTAL, 
                                               GridBagConstraints.NORTHWEST,
                                               new Insets(LayoutUtils.PANEL_IB_SPACE_BETWEEN,
                                                          LayoutUtils.PANEL_INDENT_1, 0, 0));
        gridbag.setConstraints(rememberWindowLocationsCheckbox, c);
        add(rememberWindowLocationsCheckbox);
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
                                               2, 1,
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
                                               2, 1,
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
 	// FIELD MAINTENANCE METHODS
	//

    //-------------------------------------------
    /**
     * Loads the current system preferences into this panel.
     */
    private void loadSystemPreferences() {
        showSplashCheckbox.setSelected(TSAFEProperties.getShowSplashScreenFlag());
        rememberWindowSizesCheckbox.setSelected(TSAFEProperties.getRememberWindowSizesFlag());
        rememberWindowLocationsCheckbox.setSelected(TSAFEProperties.getRememberWindowLocationsFlag());
    }



	//
 	// PREFERENCE_PANEL METHODS
	//

    //-------------------------------------------
    void setSystemPreferences() {
        TSAFEProperties.setShowSplashScreenFlag(showSplashCheckbox.isSelected());
        TSAFEProperties.setRememberWindowSizesFlag(rememberWindowSizesCheckbox.isSelected());
        TSAFEProperties.setRememberWindowLocationsFlag(rememberWindowLocationsCheckbox.isSelected());
    }


    //-------------------------------------------
    void savePreferencesAsDefault() {

        // Save a copy of the system preferences.
        boolean originalShowSplashScreenFlag = TSAFEProperties.getShowSplashScreenFlag();
        boolean originalRememberWindowSizesFlag = TSAFEProperties.getRememberWindowSizesFlag();
        boolean originalRememberWindowLocationsFlag = TSAFEProperties.getRememberWindowLocationsFlag();

        // Temporarily set the system preferences to reflect the
        // current preference choices and save the system prefrences
        // as the defaults.
        setSystemPreferences();
        TSAFEProperties.saveShowSplashScreenFlagAsDefault();
        TSAFEProperties.saveRememberWindowSizesFlagAsDefault();
        TSAFEProperties.saveRememberWindowLocationsFlagAsDefault();

        // Now restore the original system preferences.
        TSAFEProperties.setShowSplashScreenFlag(originalShowSplashScreenFlag);
        TSAFEProperties.setRememberWindowSizesFlag(originalRememberWindowSizesFlag);
        TSAFEProperties.setRememberWindowLocationsFlag(originalRememberWindowLocationsFlag);
    }


    //-------------------------------------------
    void restoreDefaultPreferences() {

        // Save a copy of the system preferences.
        boolean originalShowSplashScreenFlag = TSAFEProperties.getShowSplashScreenFlag();
        boolean originalRememberWindowSizesFlag = TSAFEProperties.getRememberWindowSizesFlag();
        boolean originalRememberWindowLocationsFlag = TSAFEProperties.getRememberWindowLocationsFlag();

        // Temporarily set the system preferences to their default
        // values and load them into this panel.
        TSAFEProperties.restoreDefaultShowSplashScreenFlag();
        TSAFEProperties.restoreDefaultRememberWindowSizesFlag();
        TSAFEProperties.restoreDefaultRememberWindowLocationsFlag();
        loadSystemPreferences();

        // Now restore the original system preferences.
        TSAFEProperties.setShowSplashScreenFlag(originalShowSplashScreenFlag);
        TSAFEProperties.setRememberWindowSizesFlag(originalRememberWindowSizesFlag);
        TSAFEProperties.setRememberWindowLocationsFlag(originalRememberWindowLocationsFlag);
    }




    //
    // INNER CLASSES
    //

    /**
     * Listens and responds to any events fired on behalf of the 
     * buttons or lists in this preference panel.
     */
    private class MyEventListener {}

}
