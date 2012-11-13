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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Reader;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.parser.asdi.ServerReader;
import tsafe.server.server_gui.utils.LayoutUtils;

/**
 * A modal dialog that allows the user to specify a server source.
 */
class ServerSourceDialog  {


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
     * @param parent the parent frame
     * @param title  the title to display on the dialog
     */
    void showDialog(Frame parent, String title) {
        showDialog(parent, title, null);
    }


    //-------------------------------------------
    /**
     * Show the dialog.
     *
     * @param parent          the parent frame
     * @param title           the title to display on the dialog
     * @param previousSource  the previously chosen source, or null to not specify one;
     *                        the user will be allowed to edit the source directly, instead
     *                        of creating a new source from scratch
     */
    void showDialog(Frame parent, String title, ServerReader previousSource) {
        dialog = new JDialog(parent, title, true);
        contentPanel = new ContentPanel(dialog, previousSource);
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
        Dimension size = TSAFEProperties.getWindowSize("ServerSourceDialog");
        if (size != null) {
            contentPanel.setPreferredSize(size);
        }
        dialog.pack();

        Point location = TSAFEProperties.getWindowLocation("ServerSourceDialog");
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
        TSAFEProperties.setWindowSize("ServerSourceDialog", contentPanel.getSize());
        TSAFEProperties.setWindowLocation("ServerSourceDialog", dialog.getLocation());
    }
    

    //-------------------------------------------
    /**
     * Return the feed source chosen by the user.
     *
     * @return  the feed source (null if none was chosen)
     */
    Reader getSource() {
        if (contentPanel != null) {
            return contentPanel.getSource();
        }
        else {
            return null;
        }
    }




    //
    // INNER CLASSES
    //

    /**
     * The panel containing the input fields to specify a server source.
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
         * The source's server.
         */
        private JTextField serverField;

        /**
         * The source's port.
         */
        private JTextField portField;



        //
        // METHODS
        //

        //-------------------------------------------
        /**
         * Constructs a new panel.
         *
         * @param dialog          the dialog that owns this panel
         * @param previousSource  the previously chosen source, or null to not specify one;
         *                        the user will be allowed to edit the source directly, instead
         *                        of creating a new source from scratch
         */
        private ContentPanel(JDialog dialog, ServerReader previousSource) {
            super();
            GridBagLayout gridbag = new GridBagLayout();
            setLayout(gridbag);
            setBorder(BorderFactory.createEmptyBorder(LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE));       
            this.parentDialog = dialog;
            

            // If the dialog closes, treat it as a cancellation.
            parentDialog.addWindowListener(new WindowAdapter() {
                    public void windowClosing(WindowEvent we) {
                        handleCancel();
                    }
                });


            // Init this panel's contents.  Note that since we are laying out
            // components using GridBagLayout, changes to the layout must be
            // propogated to each of the methods below.  Use "gridy" to 
            // represent the first available vertical gridpoint.
            int gridy = 0;
            gridy = initTextFieldsPanel(gridbag, this, gridy);
            gridy = initButtonPanel(gridbag, this, gridy);


            // Set default values from previous source if possible.
            if (previousSource != null) {
                serverField.setText(previousSource.getServer());
                portField.setText("" + previousSource.getPort());
            }
        }

        
        //-------------------------------------------
        /**
         * Set up the panel to get the server and port.
         *
         * @param gridbag  the gridbag layout object
         * @param al       the event listener
         * @param gridy    the first available vertical position in the layout
         * @return  the first available vertical position in the layout after this method
         *          has finished laying out all of its components
         */
        private int initTextFieldsPanel(GridBagLayout gridbag, ActionListener al, int gridy) {

            // Set up server field.
            JLabel label = new JLabel("Server:");
            label.setHorizontalAlignment(JLabel.RIGHT);
            label.setVerticalAlignment(JLabel.CENTER);
            GridBagConstraints c = 
                LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   0.0, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_TOPMOST,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.CENTER,
                                                   new Insets(LayoutUtils.DIALOG_B_SPACE_ABOVE_TOPMOST, 
                                                              0,0,0));
            gridbag.setConstraints(label, c);
            add(label);

            serverField = new JTextField(15);
            c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                                   1, 1,
                                                   0.8, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_TOPMOST,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.CENTER,
                                                   new Insets(LayoutUtils.DIALOG_B_SPACE_ABOVE_TOPMOST, 
                                                              LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
            gridbag.setConstraints(serverField, c);
            add(serverField);
            gridy++;


            // Set up port field.
            label = new JLabel("Port:");
            label.setHorizontalAlignment(JLabel.RIGHT);
            label.setVerticalAlignment(JLabel.CENTER);
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   0.0, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BELOW,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.CENTER,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
                                                              0,0,0));
            gridbag.setConstraints(label, c);
            add(label);
            
            portField = new JTextField(15);
            c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                                   1, 1,
                                                   0.8, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BELOW,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.CENTER,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
                                                              LayoutUtils.BUTTONP_BORDERSIZE, 0, 0));
            gridbag.setConstraints(portField, c);
            add(portField);
            gridy++;


            return gridy;
        }        
        
        
        //-------------------------------------------
        /**
         * Set up the OK/Cancel buttons.
         *
         * @param gridbag  the gridbag layout object
         * @param al       the event listener
         * @param gridy    the first available vertical position in the layout
         * @return  the first available vertical position in the layout after this method
         *          has finished laying out all of its components
         */
        private int initButtonPanel(GridBagLayout gridbag, ActionListener al, int gridy) {

            // Create the button panel.
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2,
                                                           LayoutUtils.BUTTONP_SPACE_HORIZONTAL, 
                                                           LayoutUtils.BUTTONP_SPACE_VERTICAL));

            JButton button = new JButton("OK");
            button.setToolTipText("OK");
            button.setActionCommand("OK");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Cancel");
            button.setToolTipText("Cancel");
            button.setActionCommand("Cancel");
            button.addActionListener(al);
            buttonPanel.add(button);

            GridBagConstraints c = 
                LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   2, 1,
                                                   LayoutUtils.BUTTONP_WEIGHTX, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BOTTOMMOST, 
                                                   GridBagConstraints.NONE, 
                                                   GridBagConstraints.SOUTH,
                                                   new Insets(LayoutUtils.BUTTONP_BORDERSIZE +
                                                              LayoutUtils.DIALOG_B_SPACE_ABOVE_FINAL_BUTTONP,
                                                              LayoutUtils.BUTTONP_BORDERSIZE,
                                                              LayoutUtils.BUTTONP_BORDERSIZE +
                                                              LayoutUtils.DIALOG_B_SPACE_BELOW_BOTTOMMOST,
                                                              LayoutUtils.BUTTONP_BORDERSIZE));
            gridbag.setConstraints(buttonPanel, c);
            add(buttonPanel);        
            gridy++;

 
            return gridy;
        }


        //-------------------------------------------
        /**
         * Return the new feed source added by the user.
         *
         * @return  the new feed source (null if none was specified)
         */
        private Reader getSource() {
            Reader source = null;
            String server = serverField.getText();
            String portString = portField.getText();
            
            if ((!server.equals("")) || (!portString.equals(""))) {
                try {
                    int port = (Integer.valueOf(portString)).intValue();
                    source = new ServerReader(server, port);
                }
                catch (NumberFormatException nfe) {
                    source = null;
                    System.err.println("Unexpected exception: " + nfe);
                }
                catch (Exception uhe) {
                    source = null;
                    System.err.println("Unexpected exception: " + uhe);
                }                    
            }

            return source;
        }


        //-------------------------------------------
        /**
         * Verifies that all input parameters are valid.
         *
         * @return  true if all input parameters are valid; false otherwise
         */
        private boolean validateInputParameters() {
            String server = serverField.getText();
            String portString = portField.getText();

            // Validate port number if one exists.
            if (!portString.equals("")) {
                try {
                    int port = (Integer.valueOf(portString)).intValue();
                }
                catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog((JFrame) parentDialog.getOwner(),
                                                  "Bad port number!",
                                                  "Invalid Port",
                                                  JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }

            // Make sure there is a port number.
            else {
                JOptionPane.showMessageDialog((JFrame) parentDialog.getOwner(),
                                              "You must specify a port number!",
                                              "Missing Port",
                                              JOptionPane.ERROR_MESSAGE);
                return false;
            }

            return true;
        }


        //-------------------------------------------
        /**
         * Handles all cancellation requests by the user.
         */
        private void handleCancel() {
            serverField.setText("");
            portField.setText("");
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
            

            // Handle OK/Cancel buttons.

            if (command.equals("OK")) {
                if (validateInputParameters()) {
                    saveWindowPreferences();
                    parentDialog.dispose();
                }       
            }
            
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
