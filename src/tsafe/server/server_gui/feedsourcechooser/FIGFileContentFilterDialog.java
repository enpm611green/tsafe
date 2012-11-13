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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.server_gui.utils.LayoutUtils;
import fig.io.FIGFileContentFilter;

/**
 * A modal dialog that allows the user to specify a FIG file content filter.
 */
public class FIGFileContentFilterDialog  {


    //
    // MEMBER VARIABLES
    //

    /**
     * The actual dialog.
     */
    private JDialog dialog;

    /**
     * The dialog's main panel.
     */
    private ContentPanel contentPanel;



    //
    // METHODS
    //

    //-------------------------------------------
    /**
     * Show the dialog.
     *
     * @param parent         the parent frame
     * @param title          the title to display on the dialog
     * @param defaultFilter  the default filtering conditions, or null to 
     *                       use the system defaults
     */
    public void showDialog(Frame parent, String title, FIGFileContentFilter defaultFilter) {
        dialog = new JDialog(parent, title, true);
        contentPanel = new ContentPanel(dialog, defaultFilter);
        dialog.setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        dialog.setContentPane(contentPanel);
        loadWindowPreferences();
        dialog.show();        
    }
    
    
    //-------------------------------------------
    /**
     * Load this window's preferences.
     */
    private void loadWindowPreferences() {
        Dimension size = TSAFEProperties.getWindowSize("FIGFileContentFilterDialog");
        if (size != null) {
            contentPanel.setPreferredSize(size);
        }
        dialog.pack();

        Point location = TSAFEProperties.getWindowLocation("FIGFileContentFilterDialog");
        if (location != null) {
            dialog.setLocation(location);
        }
        else {
            dialog.setLocation(LayoutUtils.centerWindow(dialog.getSize()));
        }
    }
    
    
    //-------------------------------------------
    /**
     * Save this window's preferences.
     */
    private void saveWindowPreferences() {
        TSAFEProperties.setWindowSize("FIGFileContentFilterDialog", contentPanel.getSize());
        TSAFEProperties.setWindowLocation("FIGFileContentFilterDialog", dialog.getLocation());
    }
    

    //-------------------------------------------
    /**
     * Return the content filter chosen by the user.
     *
     * @return  the content filter
     */
    public FIGFileContentFilter getContentFilter() {
        return contentPanel.getContentFilter();
    }




    //
    // INNER CLASSES
    //

    /**
     * The panel containing the tabbed pane used to specify the filtering options.
     */
    private class ContentPanel extends JPanel implements ActionListener {       
       
 
        //
        // MEMBER VARIABLES
        //

        /**
         * The dialog that owns this panel.
         */
        private JDialog parentDialog;
        
        /**
         * The content filter.
         */
        private FIGFileContentFilter contentFilter;

        /**
         * The filter panels that make up this dialog.
         */
        private java.util.List filterPanels;



        //
        // LAYOUT METHODS
        //
        
        //-------------------------------------------
        /**
         * Constructs a new panel.
         *
         * @param dialog        the dialog that owns this panel
         * @param defaultFilter  the default filtering conditions, or null to 
         *                       use the system defaults
         */
        private ContentPanel(JDialog dialog, FIGFileContentFilter defaultFilter) {
            super(new GridLayout());       
            this.parentDialog = dialog;


            // Clone the filter so that we don't accidentally modify the original.
            if (defaultFilter != null) {
                defaultFilter = (FIGFileContentFilter) defaultFilter.clone();
            }
            if (defaultFilter == null) {
                defaultFilter = TSAFEProperties.getContentFilter();
            }


            // If the dialog closes, treat it as a cancellation.
            parentDialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        handleCancel();
                    }
                });


            // Create a tabbed pane containing the various filter panels.
            // Add a common button panel to each filter panel.
            JTabbedPane tPane = new JTabbedPane();
            filterPanels = new Vector();

            FilterPanel filterPanel = new AirlineFilterPanel(parentDialog, createCommonButtonPanel(this));
            filterPanels.add(filterPanel);
            tPane.addTab("Airlines", filterPanel);

            filterPanel = new FacilityFilterPanel(parentDialog, createCommonButtonPanel(this));
            filterPanels.add(filterPanel);
            tPane.addTab("Facilities", filterPanel);

            filterPanel = new MessageTypeFilterPanel(parentDialog, createCommonButtonPanel(this));
            filterPanels.add(filterPanel);
            tPane.addTab("Message Types", filterPanel);

            filterPanel = new OtherFilterPanel(parentDialog, createCommonButtonPanel(this));
            filterPanels.add(filterPanel);
            tPane.addTab("Other", filterPanel);
            
            add(tPane);


            setContentFilter(defaultFilter);
        }

        
        //-------------------------------------------
        /**
         * Set up the Accept/Reset/Cancel buttons.
         *
         * @param al       the event listener
         * @return         a new JPanel containing the three buttons
         */
        private JPanel createCommonButtonPanel(ActionListener al) {

            // Create the button panel.
            JPanel buttonPanel = new JPanel(new GridLayout(1, 3,
                                                           LayoutUtils.BUTTONP_SPACE_HORIZONTAL,
                                                           LayoutUtils.BUTTONP_SPACE_VERTICAL));

            JButton button = new JButton("Accept");
            button.setToolTipText("Accept filter options");
            button.setActionCommand("Accept");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Reset");
            button.setToolTipText("Reset filter options to their default values");
            button.setActionCommand("Reset");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Cancel");
            button.setToolTipText("Cancel");
            button.setActionCommand("Cancel");
            button.addActionListener(al);
            buttonPanel.add(button);


            return buttonPanel;
        }



        //
        // OTHER METHODS
        //

        //-------------------------------------------
        /**
         * Sets the filtering options in all of the filter panels to reflect the
         * conditions in the input filter.
         *
         * @param filter   the content filter
         */
        private void setContentFilter(FIGFileContentFilter filter) {
            contentFilter = filter;

            Iterator panels = filterPanels.iterator();
            while (panels.hasNext()) {
                FilterPanel panel = (FilterPanel) panels.next();
                panel.setFilteringConditions(contentFilter);
            }
        }


        //-------------------------------------------
        /**
         * Return the new content filter added by the user.
         *
         * @return  the new content filter
         */
        private FIGFileContentFilter getContentFilter() {
            return contentFilter;
        }


        //-------------------------------------------
        /**
         * Handles all cancellation requests by the user.
         */
        private void handleCancel() {
            contentFilter = null;
            saveWindowPreferences();
            parentDialog.dispose();
        }


        //-------------------------------------------
        /**
         * Responds to any events thrown by the panel's buttons.
         *
         * @param event the ActionEvent
         */
        public void actionPerformed(ActionEvent event) {
            String command = event.getActionCommand();
            

            // Accept button.

            if (command.equals("Accept")) {
                Iterator panels = filterPanels.iterator();
                while (panels.hasNext()) {
                    FilterPanel panel = (FilterPanel) panels.next();
                    panel.updateContentFilter(contentFilter);
                }

                saveWindowPreferences();
                parentDialog.dispose();
            }
            

            // Reset button.

            else if (command.equals("Reset")) {
                setContentFilter(TSAFEProperties.getContentFilter());
            }


            // Cancel button.

            else if (command.equals("Cancel")) {
                handleCancel();
            }


            // Default
            
            else {  
                System.err.println("Unrecognized command: " + command);
            }
        }

    } // inner class ContentPanel

}
