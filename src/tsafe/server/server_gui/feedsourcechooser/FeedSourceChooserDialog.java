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
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Reader;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.ButtonModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.parser.asdi.ServerReader;
import tsafe.server.server_gui.utils.LayoutUtils;
import fig.io.FIGFileReader;

/**
 * A modal dialog that allows the user to create a new feed source or 
 * to optionally choose one from a list of default sources.
 */
public class FeedSourceChooserDialog  {


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

    /**
     * The list of default feed sources (or null if there are none).
     */
    private java.util.List defaultSources;

    /**
     * An optional feed source that the user can edit instead of creating a new
     * feed source from scratch.
     */
    private Reader editableSource;

    /**
     * True if the user can specify a content filter.
     */
    private boolean allowContentFilter;



    //
    // METHODS
    //

    //-------------------------------------------
    /**
     * Construct a new feed source chooser dialog.
     */
    public FeedSourceChooserDialog() {
        this(null, true);
    }


    //-------------------------------------------
    /**
     * Construct a new feed source chooser dialog.
     *
     * @param defaultSources  a list of default sources to choose from
     */
    public FeedSourceChooserDialog(java.util.List defaultSources) {
        this(defaultSources, true);
    }


    //-------------------------------------------
    /**
     * Construct a new feed source chooser dialog.
     *
     * @param defaultSources      a list of default sources to choose from
     * @param allowContentFilter  true if the user can specify a content filter
     *
     */
    public FeedSourceChooserDialog(java.util.List defaultSources, boolean allowContentFilter) {
        this(defaultSources, null, allowContentFilter);
    }


    //-------------------------------------------
    /**
     * Construct a new feed source chooser dialog.
     *
     * @param defaultSources      a list of default sources to choose from
     * @param editableSource      an optional feed source that the user can directly edit,
     *                            or null to not specify one
     * @param allowContentFilter  true if the user can specify a content filter
     *
     */
    public FeedSourceChooserDialog(java.util.List defaultSources,
                                   Reader editableSource, 
                                   boolean allowContentFilter) {

        // If there are no default sources, set the list to null for convenience.
        if ((defaultSources != null) && (defaultSources.isEmpty())) {
            defaultSources = null;
        }

        this.defaultSources = defaultSources;
        this.editableSource = editableSource;
        this.allowContentFilter = allowContentFilter;
    }


    //-------------------------------------------
    /**
     * Show the dialog.
     *
     * @param parent the parent frame
     * @param title  the title to display on the dialog
     */
    public void showDialog(JFrame parent, String title) {
        dialog = new JDialog(parent, title, true);
        contentPanel = new ContentPanel(dialog);
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
        
        // Since default sources and the edit source option can change the window size 
        // dramatically, load one of four window sizes and locations.
        String property = null;
        if ((defaultSources != null) && (editableSource != null)) {
            property = "FeedSourceChooserDialog_DefaultSources_EditSource";
        }
        else if (defaultSources != null) {
            property = "FeedSourceChooserDialog_DefaultSources";
        }
        else if (editableSource != null) {
            property = "FeedSourceChooserDialog_EditSource";
        }
        else {
            property = "FeedSourceChooserDialog";
        }


        Dimension size = TSAFEProperties.getWindowSize(property);
        if (size != null) {
            contentPanel.setPreferredSize(size);
        }
        dialog.pack();

        Point location = TSAFEProperties.getWindowLocation(property);
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

        // Since default sources and the edit source option can change the window size 
        // dramatically, save one of four window sizes and locations.
        String property = null;
        if ((defaultSources != null) && (editableSource != null)) {
            property = "FeedSourceChooserDialog_DefaultSources_EditSource";
        }
        else if (defaultSources != null) {
            property = "FeedSourceChooserDialog_DefaultSources";
        }
        else if (editableSource != null) {
            property = "FeedSourceChooserDialog_EditSource";
        }
        else {
            property = "FeedSourceChooserDialog";
        }


        TSAFEProperties.setWindowSize(property, contentPanel.getSize());
        TSAFEProperties.setWindowLocation(property, dialog.getLocation());
    }
    

    //-------------------------------------------
    /**
     * Return the feed source chosen by the user.
     *
     * @return  the feed source (null if none was chosen)
     */
    public Reader getSource() {
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
     * The panel containing the input fields to choose a feed source.
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
         * The combo box containing the list of default sources.
         */
        private JComboBox defaultSourcesCombo;

        /**
         * The radio buttons used to choose the feed source type.
         */
        private ButtonGroup sourceTypeRadioButtonGroup;

        /**
         * The chosen feed source.
         */
        private Reader source;



        //
        // METHODS
        //

        //-------------------------------------------
        /**
         * Constructs a new panel.
         *
         * @param dialog  the dialog that owns this panel
         */
        private ContentPanel(JDialog dialog) {
            super();
            GridBagLayout gridbag = new GridBagLayout();
            setLayout(gridbag);
            setBorder(BorderFactory.createEmptyBorder(LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE));       
            this.parentDialog = dialog;
            this.source = null;
            

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
            if (defaultSources != null) {
                gridy = initDefaultSourcesPanel(gridbag, this, gridy);            
            }
            if (editableSource != null) {
                gridy = initEditSourcePanel(gridbag, this, gridy);
            }
            gridy = initNewSourcePanel(gridbag, this, gridy);
        }

        
        //-------------------------------------------
        /**
         * Set up the panel to choose a default source.
         *
         * @param gridbag  the gridbag layout object
         * @param al       the event listener
         * @param gridy    the first available vertical position in the layout
         * @return  the first available vertical position in the layout after this method
         *          has finished laying out all of its components
         */
        private int initDefaultSourcesPanel(GridBagLayout gridbag, ActionListener al, int gridy) {

            // Set the label.
            JLabel label = new JLabel("Choose From List:");
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setVerticalAlignment(JLabel.BOTTOM);
            GridBagConstraints c = 
                LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.LABEL_WEIGHTX, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_TOPMOST,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.SOUTHWEST,
                                                   new Insets(LayoutUtils.DIALOG_B_SPACE_ABOVE_TOPMOST,
                                                              0,0,0));
            gridbag.setConstraints(label, c);
            add(label);
            gridy++;


            // Create the combo box.
            defaultSourcesCombo = new JComboBox(new Vector(defaultSources));
            defaultSourcesCombo.setEditable(false);
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.TABLE_WEIGHTX, 
                                                   LayoutUtils.DIALOG_IB_WEIGHTY_BETWEEN,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.CENTER,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
                                                              LayoutUtils.DIALOG_INDENT_1, 0, 0));
            gridbag.setConstraints(defaultSourcesCombo, c);
            add(defaultSourcesCombo);
            gridy++;
        

            // Create the button panel.
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2,
                                                           LayoutUtils.BUTTONP_SPACE_HORIZONTAL, 
                                                           LayoutUtils.BUTTONP_SPACE_VERTICAL));

            JButton button = new JButton("Choose");
            button.setToolTipText("Choose this source");
            button.setActionCommand("Choose Default");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Cancel");
            button.setToolTipText("Cancel");
            button.setActionCommand("Cancel");
            button.addActionListener(al);
            buttonPanel.add(button);

            // Leave out the left and right border inserts for the button panel so that the
            // "Next" and "Cancel" buttons of the component below line up with the radio buttons.
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.BUTTONP_WEIGHTX, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BELOW, 
                                                   GridBagConstraints.NONE, 
                                                   GridBagConstraints.NORTH,
                                                   new Insets(LayoutUtils.BUTTONP_BORDERSIZE +
                                                              LayoutUtils.DIALOG_IB_SPACE_BETWEEN_RELATED,
                                                              //LayoutUtils.BUTTONP_BORDERSIZE + 
                                                              LayoutUtils.DIALOG_INDENT_1,
                                                              LayoutUtils.BUTTONP_BORDERSIZE,
                                                              //LayoutUtils.BUTTONP_BORDERSIZE +
                                                              0));
            gridbag.setConstraints(buttonPanel, c);
            add(buttonPanel);        
            gridy++;


            return gridy;
        }


        //-------------------------------------------
        /**
         * Set up the panel to edit an existing source.
         *
         * @param gridbag  the gridbag layout object
         * @param al       the event listener
         * @param gridy    the first available vertical position in the layout
         * @return  the first available vertical position in the layout after this method
         *          has finished laying out all of its components
         */
        private int initEditSourcePanel(GridBagLayout gridbag, ActionListener al, int gridy) {

            // Set the label.
            JLabel label = new JLabel("Edit Source:");
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setVerticalAlignment(JLabel.BOTTOM);
            GridBagConstraints c;            
            if (defaultSources != null) {
                // This is not the first "block" in the dialog.
                c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                       1, 1,
                                                       LayoutUtils.LABEL_WEIGHTX, 
                                                       LayoutUtils.DIALOG_B_WEIGHTY_ABOVE,
                                                       GridBagConstraints.HORIZONTAL, 
                                                       GridBagConstraints.SOUTHWEST,
                                                       new Insets(LayoutUtils.DIALOG_B_SPACE_BETWEEN, 
                                                                  0,0,0));
            }
            else {
                // This is the first "block" in the dialog.
                c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                       1, 1,
                                                       LayoutUtils.LABEL_WEIGHTX, 
                                                       LayoutUtils.DIALOG_B_WEIGHTY_TOPMOST,
                                                       GridBagConstraints.HORIZONTAL, 
                                                       GridBagConstraints.SOUTHWEST,
                                                       new Insets(LayoutUtils.DIALOG_B_SPACE_ABOVE_TOPMOST, 
                                                                  0,0,0));
            }
            gridbag.setConstraints(label, c);
            add(label);
            gridy++;


            // Set the feed source label.
            label = new JLabel(editableSource.toString());
            label.setToolTipText("Double click for expanded view");
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setVerticalAlignment(JLabel.BOTTOM);
            label.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            JDialog dialog = new JDialog(parentDialog, "Expanded View", true);
                            JScrollPane contentPane = 
                                new JScrollPane(new JLabel("Feed Source: " + editableSource.toString()));
                            contentPane.setBorder(BorderFactory.createEmptyBorder(LayoutUtils.DIALOG_BORDERSIZE,
                                                                                  LayoutUtils.DIALOG_BORDERSIZE,
                                                                                  LayoutUtils.DIALOG_BORDERSIZE,
                                                                                  LayoutUtils.DIALOG_BORDERSIZE));
                            contentPane.setPreferredSize(LayoutUtils.EXPANDEDVIEWER_SIZE);
                            dialog.setContentPane(contentPane);
                            dialog.pack();
                            dialog.show();                            
                        }
                    }
                });
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.LABEL_WEIGHTX, 
                                                   LayoutUtils.DIALOG_IB_WEIGHTY_BETWEEN,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.WEST,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
                                                              LayoutUtils.DIALOG_INDENT_1, 0, 0));
            gridbag.setConstraints(label, c);
            add(label);
            gridy++;


            // Create the button panel.
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2,
                                                           LayoutUtils.BUTTONP_SPACE_HORIZONTAL, 
                                                           LayoutUtils.BUTTONP_SPACE_VERTICAL));

            JButton button = new JButton("Edit");
            button.setToolTipText("Edit feed source");
            button.setActionCommand("Edit");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Cancel");
            button.setToolTipText("Cancel");
            button.setActionCommand("Cancel");
            button.addActionListener(al);
            buttonPanel.add(button);

            // Leave out the left and right border inserts for the button panel so that the
            // "Next" and "Cancel" buttons of the component below line up with the radio buttons.
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.BUTTONP_WEIGHTX, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BELOW, 
                                                   GridBagConstraints.NONE, 
                                                   GridBagConstraints.NORTH,
                                                   new Insets(LayoutUtils.BUTTONP_BORDERSIZE +
                                                              LayoutUtils.DIALOG_IB_SPACE_BETWEEN_RELATED,
                                                              //LayoutUtils.BUTTONP_BORDERSIZE + 
                                                              LayoutUtils.DIALOG_INDENT_1,
                                                              LayoutUtils.BUTTONP_BORDERSIZE,
                                                              //LayoutUtils.BUTTONP_BORDERSIZE +
                                                              0));
            gridbag.setConstraints(buttonPanel, c);
            add(buttonPanel);        
            gridy++;
            gridy++;


            return gridy;
        }


        //-------------------------------------------
        /**
         * Set up the panel to choose a new source.
         *
         * @param gridbag  the gridbag layout object
         * @param al       the event listener
         * @param gridy    the first available vertical position in the layout
         * @return  the first available vertical position in the layout after this method
         *          has finished laying out all of its components
         */
        private int initNewSourcePanel(GridBagLayout gridbag, ActionListener al, int gridy) {

            // Set the label.
            JLabel label = new JLabel("Choose New Source:");
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setVerticalAlignment(JLabel.BOTTOM);
            GridBagConstraints c;            
            if ((defaultSources != null) || (editableSource != null)) {
                // This is not the first "block" in the dialog.
                c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                       1, 1,
                                                       LayoutUtils.LABEL_WEIGHTX, 
                                                       LayoutUtils.DIALOG_B_WEIGHTY_ABOVE,
                                                       GridBagConstraints.HORIZONTAL, 
                                                       GridBagConstraints.SOUTHWEST,
                                                       new Insets(LayoutUtils.DIALOG_B_SPACE_BETWEEN, 
                                                                  0,0,0));
            }
            else {
                // This is the first "block" in the dialog.
                c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                       1, 1,
                                                       LayoutUtils.LABEL_WEIGHTX, 
                                                       LayoutUtils.DIALOG_B_WEIGHTY_TOPMOST,
                                                       GridBagConstraints.HORIZONTAL, 
                                                       GridBagConstraints.SOUTHWEST,
                                                       new Insets(LayoutUtils.DIALOG_B_SPACE_ABOVE_TOPMOST, 
                                                                  0,0,0));
            }
            gridbag.setConstraints(label, c);
            add(label);
            gridy++;


            // Create the server source radio button.
            JRadioButton rButton = new JRadioButton("Server source");
            rButton.setSelected(true);
            rButton.setActionCommand("Server source");
            rButton.setHorizontalAlignment(JRadioButton.LEFT);
            rButton.setHorizontalTextPosition(JRadioButton.RIGHT);
            sourceTypeRadioButtonGroup = new ButtonGroup();
            sourceTypeRadioButtonGroup.add(rButton);
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.TABLE_WEIGHTX, 
                                                   LayoutUtils.DIALOG_IB_WEIGHTY_BETWEEN,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.WEST,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
                                                              LayoutUtils.DIALOG_INDENT_1, 0, 0));
            gridbag.setConstraints(rButton, c);
            add(rButton);
            gridy++;

            
            // Create the FIG file source radio button.
            rButton = new JRadioButton("FIG file source");
            rButton.setActionCommand("FIG file source");
            rButton.setHorizontalAlignment(JRadioButton.LEFT);
            rButton.setHorizontalTextPosition(JRadioButton.RIGHT);
            sourceTypeRadioButtonGroup.add(rButton);
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.TABLE_WEIGHTX, 
                                                   LayoutUtils.DIALOG_IB_WEIGHTY_BETWEEN,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.WEST,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN_RELATED, 
                                                              LayoutUtils.DIALOG_INDENT_1, 0, 0));
            gridbag.setConstraints(rButton, c);
            add(rButton);
            gridy++;
        

            // Create the button panel.
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2,
                                                           LayoutUtils.BUTTONP_SPACE_HORIZONTAL, 
                                                           LayoutUtils.BUTTONP_SPACE_VERTICAL));

            JButton button = new JButton("Next");
            button.setToolTipText("Go to next step");
            button.setActionCommand("Next");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Cancel");
            button.setToolTipText("Cancel");
            button.setActionCommand("Cancel");
            button.addActionListener(al);
            buttonPanel.add(button);

            if ((defaultSources != null) || (editableSource != null)) {
                // Leave out the left and right border inserts for the button panel so that the
                // "Next" and "Cancel" buttons line up with the radio buttons.
                c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                       1, 1,
                                                       LayoutUtils.BUTTONP_WEIGHTX, 
                                                       LayoutUtils.DIALOG_B_WEIGHTY_BOTTOMMOST, 
                                                       GridBagConstraints.NONE, 
                                                       GridBagConstraints.NORTH,
                                                       new Insets(LayoutUtils.BUTTONP_BORDERSIZE +
                                                                  LayoutUtils.DIALOG_IB_SPACE_BETWEEN_RELATED,
                                                                  //LayoutUtils.BUTTONP_BORDERSIZE + 
                                                                  LayoutUtils.DIALOG_INDENT_1,
                                                                  LayoutUtils.BUTTONP_BORDERSIZE +
                                                                  LayoutUtils.DIALOG_B_SPACE_BELOW_BOTTOMMOST,
                                                                  //LayoutUtils.BUTTONP_BORDERSIZE +
                                                                  0));
            }
            else {
                // Treat the button panel as the dialog's final button panel.
                c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                       1, 1,
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
            }
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
            return source;
        }


        //-------------------------------------------
        /**
         * Handles all cancellation requests by the user.
         */
        private void handleCancel() {
            source = null;
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
            

            // Cancel button.
            
            if (command.equals("Cancel")) {
                handleCancel();
            }

            
            // Choose Default Source button

            else if (command.equals("Choose Default")) {
                if (defaultSourcesCombo != null) {
                    source = (Reader) defaultSourcesCombo.getSelectedItem();
                    saveWindowPreferences();
                    parentDialog.dispose();
                }
            }

            
            // Edit Source button

            else if (command.equals("Edit")) {
                if (editableSource != null) {
                    Reader returnedSource = null;

                    // Let the user edit a server source.
                    if (editableSource instanceof ServerReader) {
                        ServerSourceDialog serverDialog = new ServerSourceDialog();
                        serverDialog.showDialog((JFrame) parentDialog.getOwner(), 
                                                "Specify Server Source", 
                                                (ServerReader) editableSource);
                        returnedSource = serverDialog.getSource();
                    }

                    // Let the user edit a FIG file source.
                    else if (editableSource instanceof FIGFileReader) {
                        FIGFileSourceDialog figFileDialog = new FIGFileSourceDialog(allowContentFilter);
                        figFileDialog.showDialog((JFrame) parentDialog.getOwner(), 
                                                 "Specify FIG File Source", 
                                                 (FIGFileReader) editableSource);
                        returnedSource = figFileDialog.getSource();
                    }


                    // If user successfully specified a source, close all dialogs!
                    if (returnedSource != null) {
                        source = returnedSource;
                        saveWindowPreferences();
                        parentDialog.dispose();
                    }                    
                }
            }


            // Next button

            else if (command.equals("Next")) {
                Reader returnedSource = null;
                ButtonModel bModel = sourceTypeRadioButtonGroup.getSelection();


                // Let the user choose a FIG file source.
                if (bModel.getActionCommand().equals("FIG file source")) {
                    FIGFileSourceDialog figFileDialog = new FIGFileSourceDialog(allowContentFilter);
                    figFileDialog.showDialog((JFrame) parentDialog.getOwner(), "Specify FIG File Source");
                    returnedSource = figFileDialog.getSource();
                }


                // Or, let the user specify a server source.
                else if (bModel.getActionCommand().equals("Server source")) {
                    ServerSourceDialog serverDialog = new ServerSourceDialog();
                    serverDialog.showDialog((JFrame) parentDialog.getOwner(), "Specify Server Source");
                    returnedSource = serverDialog.getSource();
                }


                // If user successfully specified a source, close all dialogs!
                if (returnedSource != null) {
                    source = returnedSource;
                    saveWindowPreferences();
                    parentDialog.dispose();
                }
            }


            // Default

            else {                
                System.err.println("Unrecognized command: " + command);
            }
        }
                    
    } // inner class ContentPanel

}
