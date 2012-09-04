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

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.server_gui.utils.LayoutUtils;
import tsafe.server.server_gui.utils.table.DefaultSortTableModel;
import tsafe.server.server_gui.utils.table.JSortTable;
import tsafe.server.server_gui.utils.table.TableUtils;

/**
 * Allows a user to specify the system's list of facilities.
 */
class FacilityPreferencePanel extends PreferencePanel {


    //
    // CONSTANTS
    //

    /**
     * The facility table's column names.
     */
    private final static Vector FACILITY_TABLE_COLUMNS;
    static {
        FACILITY_TABLE_COLUMNS = new Vector();
        FACILITY_TABLE_COLUMNS.add("Facility Code");
        FACILITY_TABLE_COLUMNS.add("Description");
    }

    /**
     * The index of the "Facility" table column.
     */
    private final static int COLUMN_FACILITY = 0;

    /**
     * The width of the "Facility" table column.
     */
    private final static int COLUMN_WIDTH_FACILITY = 125;

    /**
     * The index of the "Description" table column.
     */
    private final static int COLUMN_DESCRIPTION = 1;

    /**
     * The width of the "Description" table column.
     */
    private final static int COLUMN_WIDTH_DESCRIPTION = 275;



    //
    // MEMBER VARIABLES
    //

    /**
     * The dialog that owns this panel.
     */
    private JDialog parentDialog;

    /**
     * The table of facilities.
     */
    private JSortTable facilityTable;

    /**
     * The facility table's model.
     */
    private DefaultSortTableModel facilityTableModel;



    //
    // LAYOUT METHODS
    //

    //-------------------------------------------
    /**
     * Constructs a new panel to specify the list of facilities.
     *
     * @param parentDialog      the dialog that owns this panel
     * @param commonButtonPanel the common buttons between all preference panels; assumes
     *                          this argument is not null
     */
    FacilityPreferencePanel(JDialog parentDialog, JPanel commonButtonPanel) {
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
        gridy = initFacilitiesTable(gridbag, mel, gridy);
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
        JTextArea instructions = new JTextArea("This panel lets you specify the list of facilities you can " +
                                               "choose from when filtering messages from a FIG file based " +
                                               "on facility.", 5, 40);
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
     * Set up the facilities table.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initFacilitiesTable(GridBagLayout gridbag, MyEventListener mel, int gridy) {
            
        // Create the table.
        facilityTableModel = new DefaultSortTableModel(new Vector(), FACILITY_TABLE_COLUMNS);
        facilityTableModel.setEditable(true);
        facilityTable = new JSortTable(facilityTableModel);

        facilityTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        facilityTable.getColumnModel().getColumn(COLUMN_FACILITY).setPreferredWidth(COLUMN_WIDTH_FACILITY);
        facilityTable.getColumnModel().getColumn(COLUMN_DESCRIPTION).setPreferredWidth(COLUMN_WIDTH_DESCRIPTION);
        facilityTable.setPreferredScrollableViewportSize(new Dimension(COLUMN_WIDTH_FACILITY + 
                                                                       COLUMN_WIDTH_DESCRIPTION, 
                                                                       10 * facilityTable.getRowHeight()));

        JScrollPane tableScrollPane = new JScrollPane(facilityTable);
        GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                  2, 1,
                                                                  LayoutUtils.TABLE_WEIGHTX, 
                                                                  LayoutUtils.TABLE_WEIGHTY,
                                                                  GridBagConstraints.BOTH, 
                                                                  GridBagConstraints.CENTER,
                                                                  new Insets(LayoutUtils.PANEL_B_SPACE_BETWEEN,
                                                                             0,0,0));
        gridbag.setConstraints(tableScrollPane, c);
        add(tableScrollPane);
        gridy++;
 

        // Create the button panel.
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2,
                                                       LayoutUtils.BUTTONP_SPACE_HORIZONTAL, 
                                                       LayoutUtils.BUTTONP_SPACE_VERTICAL));
        
        JButton button = new JButton("Add Facility");
        button.setToolTipText("Add a blank row to the table");
        button.setActionCommand("Add Facility");
        button.addActionListener(mel);
        buttonPanel.add(button);
        
        button = new JButton("Remove Facilities");
        button.setToolTipText("Remove selected rows from the table");
        button.setActionCommand("Remove Facilities");
        button.addActionListener(mel);
        buttonPanel.add(button);

        c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                               2, 1,
                                               LayoutUtils.BUTTONP_WEIGHTX, 
                                               LayoutUtils.PANEL_B_WEIGHTY_BELOW, 
                                               GridBagConstraints.NONE, 
                                               GridBagConstraints.NORTH,
                                               new Insets(LayoutUtils.BUTTONP_BORDERSIZE +
                                                          LayoutUtils.PANEL_IB_SPACE_BETWEEN_RELATED,
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          LayoutUtils.BUTTONP_BORDERSIZE,
                                                          LayoutUtils.BUTTONP_BORDERSIZE));
        gridbag.setConstraints(buttonPanel, c);
        add(buttonPanel);        
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
     * Set the information in the facility table's model.  If the specified data 
     * vector is null, the table model is set to the empty table model.
     *
     * @param newFacilities  data vector of facilities
     */
    private void setFacilityTableDataVector(Vector newFacilities) {
        if (newFacilities == null) {
            newFacilities = new Vector();
        }
                
        // Set the new model information while preserving the model settings.
        TableUtils.setDataVector(facilityTableModel, newFacilities);
    }


    //-------------------------------------------
    /**
     * Loads the current system preferences into this panel.
     */
    private void loadSystemPreferences() {
        setFacilityTableDataVector(TSAFEProperties.getFacilityDataVector());
    }



	//
 	// PREFERENCE_PANEL METHODS
	//

    //-------------------------------------------
    void setSystemPreferences() {
        TSAFEProperties.setFacilityDataVector(facilityTableModel.getDataVector());
    }


    //-------------------------------------------
    void savePreferencesAsDefault() {

        // Save a copy of the system preferences.
        Vector originalFacilities = TSAFEProperties.getFacilityDataVector();

        // Temporarily set the system preferences to reflect the
        // current preference choices and save the system prefrences
        // as the defaults.
        setSystemPreferences();
        TSAFEProperties.saveFacilityDataVectorAsDefault();

        // Now restore the original system preferences.
        TSAFEProperties.setFacilityDataVector(originalFacilities);
    }


    //-------------------------------------------
    void restoreDefaultPreferences() {

        // Save a copy of the system preferences.
        Vector originalFacilities = TSAFEProperties.getFacilityDataVector();

        // Temporarily set the system preferences to their default
        // values and load them into this panel.
        TSAFEProperties.restoreDefaultFacilityDataVector();
        loadSystemPreferences();

        // Now restore the original system preferences.
        TSAFEProperties.setFacilityDataVector(originalFacilities);
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

            
            // "Add Facility" Button
            
            if (command.equals("Add Facility")) {
                facilityTableModel.addRow((Vector) null);
            }
            
            
            // "Remove Facilities" Button
            
            else if (command.equals("Remove Facilities")) {
                int[] selectedRows = facilityTable.getSelectedRows();
                
                // Remove the row with the highest row index first so
                // we do not run into "index shifting" errors.
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    facilityTableModel.removeRow(selectedRows[i]);
                }
            }
            
            
            // Default

            else { 
                System.err.println("Unrecognized command: " + command);
            }
        }

    } // inner class MyEventListener

}
