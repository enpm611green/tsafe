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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.server_gui.feedsourcechooser.FIGFileContentFilterDialog;
import tsafe.server.server_gui.utils.LayoutUtils;
import fig.io.FIGFileContentFilter;

/**
 * Allows a user to specify a default content filter.
 */
class ContentFilterPreferencePanel extends PreferencePanel {


    //
    // MEMBER VARIABLES
    //

    /**
     * The dialog that owns this panel.
     */
    private JDialog parentDialog;

    /**
     * The text field displaying the content filter.
     */
    private JTextField filterTextField;
    
    /**
     * The filter to use on this FIG file.
     */
    private FIGFileContentFilter contentFilter;



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
    ContentFilterPreferencePanel(JDialog parentDialog, JPanel commonButtonPanel) {
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
        gridy = initInstructions(gridbag, mel, gridy);
        gridy = initFilterPanel(gridbag, mel, gridy);
        gridy = addCommonButtonPanel(gridbag, gridy, commonButtonPanel);


        // Load the current system preferences.
        loadSystemPreferences();
    }


    //-------------------------------------------
    /**
     * Set up the instructions.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     * @return  the first available vertical position in the layout after this method
     *          has finished laying out all of its components
     */
    private int initInstructions(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the instructions.
        JTextArea instructions = new JTextArea("This panel lets you specify the default content filter " +
                                               "to use when reading from a FIG file.", 5, 40);
        instructions.setOpaque(false);
        instructions.setEditable(false);
        instructions.setLineWrap(true);
        instructions.setWrapStyleWord(true);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  2, 1,
                                                                  LayoutUtils.TEXTAREA_WEIGHTX, 
                                                                  LayoutUtils.PANEL_B_WEIGHTY_TOPMOST,
                                                                  GridBagConstraints.BOTH, 
                                                                  GridBagConstraints.WEST,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_ABOVE_TOPMOST, 
                                                                             0,0,0));
        gridbag.setConstraints(instructions, c);
        add(instructions);
        gridy++;

           
        return gridy;
    }


    //-------------------------------------------
    /**
     * Set up the panel to specify a content filter.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     * @return  the first available vertical position in the layout after this method
     *          has finished laying out all of its components
     */
    private int initFilterPanel(GridBagLayout gridbag, MyEventListener mel, int gridy) {

        // Set the filter chooser.
        filterTextField = new JTextField(15);
        filterTextField.setEditable(false);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  1, 1,
                                                                  LayoutUtils.TEXTFIELD_FILE_WEIGHTX,
                                                                  LayoutUtils.PANEL_B_WEIGHTY_ABOVE,
                                                                  GridBagConstraints.HORIZONTAL, 
                                                                  GridBagConstraints.NORTHWEST,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_BETWEEN, 
                                                                             0,0,0));
        gridbag.setConstraints(filterTextField, c);
        add(filterTextField);
            
        JButton button = new JButton("Change");
        button.setToolTipText("Change the content filter");
        button.setActionCommand("Change");
        button.addActionListener(mel);
        c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                               1, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX,
                                               LayoutUtils.PANEL_B_WEIGHTY_ABOVE,
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.NORTHEAST,
                                               new Insets(LayoutUtils.PANEL_B_SPACE_BETWEEN,
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          0, 
                                                          LayoutUtils.BUTTONP_BORDERSIZE));
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
     * Set the content filter.  If the specified filter is null, the 
     * filter is set to filter nothing.
     *
     * @param newFilter  the new filter
     */
    private void setContentFilter(FIGFileContentFilter newFilter) {
        contentFilter = newFilter;
        filterTextField.setText(newFilter.toString());
    }
    

    //-------------------------------------------
    /**
     * Loads the current system preferences into this panel.
     */
    private void loadSystemPreferences() {
        setContentFilter(TSAFEProperties.getContentFilter());
    }



	//
 	// PREFERENCE_PANEL METHODS
	//

    //-------------------------------------------
    void setSystemPreferences() {
        TSAFEProperties.setContentFilter(contentFilter);
    }


    //-------------------------------------------
    void savePreferencesAsDefault() {

        // Save a copy of the system preferences.
        FIGFileContentFilter originalFilter = TSAFEProperties.getContentFilter();

        // Temporarily set the system preferences to reflect the
        // current preference choices and save the system prefrences
        // as the defaults.
        setSystemPreferences();
        TSAFEProperties.saveContentFilterAsDefault();

        // Now restore the original system preferences.
        TSAFEProperties.setContentFilter(originalFilter);
    }


    //-------------------------------------------
    void restoreDefaultPreferences() {

        // Save a copy of the system preferences.
        FIGFileContentFilter originalFilter = TSAFEProperties.getContentFilter();

        // Temporarily set the system preferences to their default
        // values and load them into this panel.
        TSAFEProperties.restoreDefaultContentFilter();
        loadSystemPreferences();

        // Now restore the original system preferences.
        TSAFEProperties.setContentFilter(originalFilter);
    }




    //
    // INNER CLASSES
    //

    /**
     * Listens and responds to any events fired on behalf of the 
     * buttons or lists in this preference panel.
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

            
            // Change button

            if (command.equals("Change")) {
                FIGFileContentFilterDialog filterDialog = new FIGFileContentFilterDialog();
                filterDialog.showDialog((JFrame) parentDialog.getOwner(), 
                                        "Specify FIG File Content Filter", 
                                        contentFilter);
                FIGFileContentFilter filter = filterDialog.getContentFilter();
                if (filter != null) {
                    setContentFilter(filter);
                }
            }
            
            
            // Default

            else { 
                System.err.println("Unrecognized command: " + command);
            }
        }

    } // inner class MyEventListener

}
