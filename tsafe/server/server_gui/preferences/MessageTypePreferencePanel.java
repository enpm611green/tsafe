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
 * Allows a user to specify the system's list of message types.
 */
class MessageTypePreferencePanel extends PreferencePanel {


    //
    // CONSTANTS
    //

    /**
     * The message type table's column names.
     */
    private final static Vector TYPE_TABLE_COLUMNS;
    static {
        TYPE_TABLE_COLUMNS = new Vector();
        TYPE_TABLE_COLUMNS.add("Type");
        TYPE_TABLE_COLUMNS.add("Description");
    }

    /**
     * The index of the "Type" table column.
     */
    private final static int COLUMN_MESSAGE_TYPE = 0;

    /**
     * The width of the "Type" table column.
     */
    private final static int COLUMN_WIDTH_MESSAGE_TYPE = 100;

    /**
     * The index of the "Description" table column.
     */
    private final static int COLUMN_DESCRIPTION = 1;

    /**
     * The width of the "Description" table column.
     */
    private final static int COLUMN_WIDTH_DESCRIPTION = 300;



    //
    // MEMBER VARIABLES
    //

    /**
     * The dialog that owns this panel.
     */
    private JDialog parentDialog;

    /**
     * The table of types.
     */
    private JSortTable typeTable;

    /**
     * The type table's model.
     */
    private DefaultSortTableModel typeTableModel;



    //
    // LAYOUT METHODS
    //

    //-------------------------------------------
    /**
     * Constructs a new panel to specify the list of message types.
     *
     * @param parentDialog      the dialog that owns this panel
     * @param commonButtonPanel the common buttons between all preference panels; assumes
     *                          this argument is not null
     */
    MessageTypePreferencePanel(JDialog parentDialog, JPanel commonButtonPanel) {
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
        gridy = initTypesTable(gridbag, mel, gridy);
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
        JTextArea instructions = new JTextArea("This panel lets you specify the list of message types you can " +
                                               "choose from when filtering messages from a FIG file " +
                                               "based on type.", 5, 40);
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
     * Set up the types table.
     *
     * @param gridbag  the gridbag layout object
     * @param mel      the general event listener
     * @param gridy    the first available vertical position in the layout
     */
    private int initTypesTable(GridBagLayout gridbag, MyEventListener mel, int gridy) {
            
        // Create the table.
        typeTableModel = new DefaultSortTableModel(new Vector(), TYPE_TABLE_COLUMNS);
        typeTableModel.setEditable(true);
        typeTable = new JSortTable(typeTableModel);

        typeTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        typeTable.getColumnModel().getColumn(COLUMN_MESSAGE_TYPE).setPreferredWidth(COLUMN_WIDTH_MESSAGE_TYPE);
        typeTable.getColumnModel().getColumn(COLUMN_DESCRIPTION).setPreferredWidth(COLUMN_WIDTH_DESCRIPTION);
        typeTable.setPreferredScrollableViewportSize(new Dimension(COLUMN_WIDTH_MESSAGE_TYPE + 
                                                                   COLUMN_WIDTH_DESCRIPTION, 
                                                                   10 * typeTable.getRowHeight()));

        JScrollPane tableScrollPane = new JScrollPane(typeTable);
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
        
        JButton button = new JButton("Add Type");
        button.setToolTipText("Add a blank row to the table");
        button.setActionCommand("Add Type");
        button.addActionListener(mel);
        buttonPanel.add(button);
        
        button = new JButton("Remove Types");
        button.setToolTipText("Remove selected rows from the table");
        button.setActionCommand("Remove Types");
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
     * Set the information in the type table's model.  If the specified data 
     * vector is null, the table model is set to the empty table model.
     *
     * @param newTypes  data vector of types
     */
    private void setTypeTableDataVector(Vector newTypes) {
        if (newTypes == null) {
            newTypes = new Vector();
        }
                
        // Set the new model information while preserving the model settings.
        TableUtils.setDataVector(typeTableModel, newTypes);
    }


    //-------------------------------------------
    /**
     * Loads the current system preferences into this panel.
     */
    private void loadSystemPreferences() {
        setTypeTableDataVector(TSAFEProperties.getMessageTypeDataVector());
    }



	//
 	// PREFERENCE_PANEL METHODS
	//

    //-------------------------------------------
    void setSystemPreferences() {
        TSAFEProperties.setMessageTypeDataVector(typeTableModel.getDataVector());
    }


    //-------------------------------------------
    void savePreferencesAsDefault() {

        // Save a copy of the system preferences.
        Vector originalMessageTypes = TSAFEProperties.getMessageTypeDataVector();

        // Temporarily set the system preferences to reflect the
        // current preference choices and save the system prefrences
        // as the defaults.
        setSystemPreferences();
        TSAFEProperties.saveMessageTypeDataVectorAsDefault();

        // Now restore the original system preferences.
        TSAFEProperties.setMessageTypeDataVector(originalMessageTypes);
    }


    //-------------------------------------------
    void restoreDefaultPreferences() {

        // Save a copy of the system preferences.
        Vector originalMessageTypes = TSAFEProperties.getMessageTypeDataVector();

        // Temporarily set the system preferences to their default
        // values and load them into this panel.
        TSAFEProperties.restoreDefaultMessageTypeDataVector();
        loadSystemPreferences();

        // Now restore the original system preferences.
        TSAFEProperties.setMessageTypeDataVector(originalMessageTypes);
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

            
            // "Add Type" Button
            
            if (command.equals("Add Type")) {
                typeTableModel.addRow((Vector) null);
            }
            
            
            // "Remove Types" Button
            
            else if (command.equals("Remove Types")) {
                int[] selectedRows = typeTable.getSelectedRows();
                
                // Remove the row with the highest row index first so
                // we do not run into "index shifting" errors.
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    typeTableModel.removeRow(selectedRows[i]);
                }
            }
            
            
            // Default

            else {  
                System.err.println("Unrecognized command: " + command);
            }
        }

    } // inner class MyEventListener

}
