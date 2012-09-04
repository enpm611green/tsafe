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

/**
 * A modal dialog that allows the user to specify FIG's preferences.
 */
public class PreferencesDialog  {


    //
    // CONSTANTS
    //

    /**
     * The default preferences tab to display.
     */
    public final static int TAB_DEFAULT = 0;

    /**
     * The GUI preferences tab.
     */
    public final static int TAB_GUI = 0;

    /**
     * The feed sources preferences tab.
     */
    public final static int TAB_FEED_SOURCES = 1;

    /**
     * The content filter preferences tab.
     */
    public final static int TAB_CONTENT_FILTER = 2;

    /**
     * The airlines preferences tab.
     */
    public final static int TAB_AIRLINES = 3;

    /**
     * The facilities preferences tab.
     */
    public final static int TAB_FACILITIES = 4;

    /**
     * The message types preferences tab.
     */
    public final static int TAB_MESSAGE_TYPES = 5;

    

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
     * Show the dialog with the default tab as active.
     *
     * @param parent  the parent frame
     * @param title   the title to display on the dialog
     */
    public void showDialog(Frame parent, String title) {
        showDialog(parent, title, TAB_DEFAULT);
    }


    //-------------------------------------------
    /**
     * Show the dialog with the specified tab as active.
     *
     * @param parent     the parent frame
     * @param title      the title to display on the dialog
     * @param activeTab  the tab that should be active (if an invalid tab is specified,
     *                   the default tab is made active)
     */
    public void showDialog(Frame parent, String title, int activeTab) {
        dialog = new JDialog(parent, title, true);
        if ((activeTab < 0) || (activeTab > 5)) {
            activeTab = TAB_DEFAULT;
        }
        contentPanel = new ContentPanel(dialog, activeTab);
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
        Dimension size = TSAFEProperties.getWindowSize("PreferencesDialog");
        if (size != null) {
            contentPanel.setPreferredSize(size);
        }
        dialog.pack();

        Point location = TSAFEProperties.getWindowLocation("PreferencesDialog");
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
        TSAFEProperties.setWindowSize("PreferencesDialog", contentPanel.getSize());
        TSAFEProperties.setWindowLocation("PreferencesDialog", dialog.getLocation());
    }




    //
    // INNER CLASSES
    //

    /**
     * The panel containing the tabbed pane used to specify FIG's preferences.
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
         * The preference panels that make up this dialog.
         */
        private java.util.List preferencePanels;



        //
        // LAYOUT METHODS
        //
        
        //-------------------------------------------
        /**
         * Constructs a new panel.
         *
         * @param dialog     the dialog that owns this panel
         * @param activeTab  the tab that should be active (if an invalid tab is specified,
         *                   the default tab is made active)
         */
        private ContentPanel(JDialog dialog, int activeTab) {
            super(new GridLayout());       
            this.parentDialog = dialog;


            // If the dialog closes, treat it as a cancellation.
            parentDialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        handleCancel();
                    }
                });


            // Create a tabbed pane containing the various preference panels.
            // Add a common button panel to each preference panel.
            JTabbedPane tPane = new JTabbedPane();
            PreferencePanel preferencePanel;
            preferencePanels = new Vector();


            preferencePanel = new GUIPreferencePanel(parentDialog, createCommonButtonPanel(this));
            preferencePanels.add(preferencePanel);
            tPane.addTab("GUI", preferencePanel);


            preferencePanel = new FeedSourcePreferencePanel(parentDialog, createCommonButtonPanel(this));
            preferencePanels.add(preferencePanel);
            tPane.addTab("Feed Sources", preferencePanel);


            preferencePanel = new ContentFilterPreferencePanel(parentDialog, createCommonButtonPanel(this));
            preferencePanels.add(preferencePanel);
            tPane.addTab("Default Filter", preferencePanel);


            preferencePanel = new AirlinePreferencePanel(parentDialog, createCommonButtonPanel(this));
            preferencePanels.add(preferencePanel);
            tPane.addTab("Airlines", preferencePanel);


            preferencePanel = new FacilityPreferencePanel(parentDialog, createCommonButtonPanel(this));
            preferencePanels.add(preferencePanel);
            tPane.addTab("Facilities", preferencePanel);


            preferencePanel = new MessageTypePreferencePanel(parentDialog, createCommonButtonPanel(this));
            preferencePanels.add(preferencePanel);
            tPane.addTab("Message Types", preferencePanel);
            

            add(tPane);
            tPane.setSelectedIndex(activeTab);
        }

        
        //-------------------------------------------
        /**
         * Set up the OK/Save/Restore/Cancel buttons.
         *
         * @param al   the event listener
         * @return     a new JPanel containing the three buttons
         */
        private JPanel createCommonButtonPanel(ActionListener al) {

            // Create the button panel.
            JPanel buttonPanel = new JPanel(new GridLayout(1, 4,
                                                           LayoutUtils.BUTTONP_SPACE_HORIZONTAL,
                                                           LayoutUtils.BUTTONP_SPACE_VERTICAL));

            JButton button = new JButton("OK");
            button.setToolTipText("Accept preferences for current session");
            button.setActionCommand("OK");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Save");
            button.setToolTipText("Save preferences for future sessions");
            button.setActionCommand("Save");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Restore");
            button.setToolTipText("Restore saved preferences");
            button.setActionCommand("Restore");
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
         * Handles all cancellation requests by the user.
         */
        private void handleCancel() {
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
            

            // OK button.

            if (command.equals("OK")) {
                Iterator panels = preferencePanels.iterator();
                while (panels.hasNext()) {
                    PreferencePanel panel = (PreferencePanel) panels.next();
                    panel.setSystemPreferences();
                }

                saveWindowPreferences();
                parentDialog.dispose();
            }
            

            // Save button.

            else if (command.equals("Save")) {
                Iterator panels = preferencePanels.iterator();
                while (panels.hasNext()) {
                    PreferencePanel panel = (PreferencePanel) panels.next();
                    panel.savePreferencesAsDefault();
                }
            }


            // Restore button.
            
            else if (command.equals("Restore")) {
                Iterator panels = preferencePanels.iterator();
                while (panels.hasNext()) {
                    PreferencePanel panel = (PreferencePanel) panels.next();
                    panel.restoreDefaultPreferences();
                }
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
