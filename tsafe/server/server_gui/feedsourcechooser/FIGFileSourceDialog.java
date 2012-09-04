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
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.table.DefaultTableModel;

import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.server_gui.utils.LayoutUtils;
import tsafe.server.server_gui.utils.table.TableUtils;
import fig.io.FIGFile;
import fig.io.FIGFileContentFilter;
import fig.io.FIGFileReader;

/**
 * A modal dialog that allows the user to specify a FIG file source.
 */
public class FIGFileSourceDialog  {


    //
    // CONSTANTS
    //
    
    /**
     * The FIG file table's column names.
     */
    private final static Vector FILE_TABLE_COLUMNS;
    static {
        FILE_TABLE_COLUMNS = new Vector();
        FILE_TABLE_COLUMNS.add("Place in correct order");
    }
    
    /**
     * The index of the "File" table column.
     */
    private final static int COLUMN_FILES = 0;
    
    /**
     * The width of the "File" table column.
     */
    private final static int COLUMN_WIDTH_FILES = LayoutUtils.TABLE_SINGLE_COLUMN_WIDTH;
    
    
    
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
     * True if the user can specify a content filter.
     */
    private boolean allowContentFilter;



    //
    // METHODS
    //

    //-------------------------------------------
    /**
     * Constructs a new FIG file source dialog.
     */
    public FIGFileSourceDialog() {
        this(true);
    }


    //-------------------------------------------
    /**
     * Constructs a new FIG file source dialog.
     *
     * @param allowContentFilter   flags whether the user will be allowed to
     *                             specify a content filter
     */
    public FIGFileSourceDialog(boolean allowContentFilter) {
        this.allowContentFilter = allowContentFilter;
    }

    
    //-------------------------------------------
    /**
     * Show the dialog.
     *
     * @param parent the parent frame
     * @param title  the title to display on the dialog
     */
    public void showDialog(Frame parent, String title) {
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
    public void showDialog(Frame parent, String title, FIGFileReader previousSource) {
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
        Dimension size = TSAFEProperties.getWindowSize("FIGFileSourceDialog");
        if (size != null) {
            contentPanel.setPreferredSize(size);
        }
        dialog.pack();

        Point location = TSAFEProperties.getWindowLocation("FIGFileSourceDialog");
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
        TSAFEProperties.setWindowSize("FIGFileSourceDialog", contentPanel.getSize());
        TSAFEProperties.setWindowLocation("FIGFileSourceDialog", dialog.getLocation());
    }
    

    //-------------------------------------------
    /**
     * Return the feed source chosen by the user.
     *
     * @return  the feed source (null if none was chosen)
     */
    public FIGFileReader getSource() {
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
     * The panel containing the input fields to choose a FIG file feed source.
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
         * The text field displaying the content filter.
         */
        private JTextField filterTextField;

        /**
         * The filter to use on this FIG file.
         */
        private FIGFileContentFilter contentFilter;
        
        /**
         * The table containing the list of FIG files.
         */
        private JTable fileTable;
        
        /**
         * The FIG file table's model.
         */
        private DefaultTableModel fileTableModel;

        /**
         * The FIG file chooser dialog.
         */
        private FIGFileChooser fileChooser;



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
        private ContentPanel(JDialog dialog, FIGFileReader previousSource) {
            super();
            GridBagLayout gridbag = new GridBagLayout();
            setLayout(gridbag);
            setBorder(BorderFactory.createEmptyBorder(LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE,
                                                      LayoutUtils.DIALOG_BORDERSIZE));       
            this.parentDialog = dialog;
            this.fileChooser = new FIGFileChooser(TSAFEProperties.getFeedDirectory());
            

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
            gridy = initFileTable(gridbag, this, gridy);
            if (allowContentFilter) {
                gridy = initFilterPanel(gridbag, this, gridy);
            }
            gridy = initButtonPanel(gridbag, this, gridy);


            // Set initial values.
            setContentFilter(TSAFEProperties.getContentFilter());


            // Set default values based on the previous source if possible.
            if (previousSource != null) {

                // Convert the list of FIG files to Files.
                FIGFile[] figFiles = previousSource.getFiles();
                if (figFiles != null) {
                    File[] files = new File[figFiles.length];
                    for (int i = 0; i < files.length; i++) {
                        files[i] = figFiles[i].getFile();
                    }
                    
                    try {
                        addFiles(files);
                    }
                    catch (Exception e) {
                        System.err.println("Couldn't edit previous FIG file source due to exception: " + e);
                        JOptionPane.showMessageDialog((JFrame) parentDialog.getOwner(),
                                                      "Could not recover FIG files list from feed source!",
                                                      "Bad or Missing File List",
                                                      JOptionPane.ERROR_MESSAGE);
                    }
                }

                setContentFilter(previousSource.getContentFilter());
            }
        }

        
        //-------------------------------------------
        /**
         * Set up the panel to choose FIG filenames.
         *
         * @param gridbag  the gridbag layout object
         * @param al       the event listener
         * @param gridy    the first available vertical position in the layout
         * @return  the first available vertical position in the layout after this method
         *          has finished laying out all of its components
         */
        private int initFileTable(GridBagLayout gridbag, ActionListener al, int gridy) {

            // Set the label.
            JLabel label = new JLabel("Specify FIG filenames:");
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setVerticalAlignment(JLabel.BOTTOM);
            GridBagConstraints c = 
                LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   2, 1,
                                                   LayoutUtils.LABEL_WEIGHTX, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_TOPMOST,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.SOUTHWEST,
                                                   new Insets(LayoutUtils.DIALOG_B_SPACE_ABOVE_TOPMOST,
                                                              0,0,0));
            gridbag.setConstraints(label, c);
            add(label);
            gridy++;


            // Create the table.
            fileTableModel = new DefaultTableModel(new Vector(), FILE_TABLE_COLUMNS) {
                    public boolean isCellEditable(int row, int column) {
                        return false;
                    }
                };
            fileTable = new JTable(fileTableModel);
            fileTable.setToolTipText("Double click on a file for an expanded view");

            TableUtils.addExpandedCellViewer(fileTable);
            fileTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
            fileTable.setShowGrid(false);
            fileTable.getColumnModel().getColumn(COLUMN_FILES).setPreferredWidth(COLUMN_WIDTH_FILES);
            fileTable.setPreferredScrollableViewportSize(new Dimension(COLUMN_WIDTH_FILES,
                                                                       5 * fileTable.getRowHeight()));
            
            JScrollPane tableScrollPane = new JScrollPane(fileTable);
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   2, 1,
                                                   LayoutUtils.TABLE_WEIGHTX, 
                                                   LayoutUtils.TABLE_WEIGHTY,
                                                   GridBagConstraints.BOTH, 
                                                   GridBagConstraints.CENTER,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
                                                              LayoutUtils.DIALOG_INDENT_1,
                                                              0,0));
            gridbag.setConstraints(tableScrollPane, c);
            add(tableScrollPane);
            gridy++;
        

            // Create the button panel.
            JPanel buttonPanel = new JPanel(new GridLayout(1, 2,
                                                           LayoutUtils.BUTTONP_SPACE_HORIZONTAL, 
                                                           LayoutUtils.BUTTONP_SPACE_VERTICAL));

            JButton button = new JButton("Add Files");
            button.setToolTipText("Add FIG files to the list");
            button.setActionCommand("Add Files");
            button.addActionListener(al);
            buttonPanel.add(button);

            button = new JButton("Delete Files");
            button.setToolTipText("Delete the selected files from the list");
            button.setActionCommand("Delete Files");
            button.addActionListener(al);
            buttonPanel.add(button);

            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   2, 1,
                                                   LayoutUtils.BUTTONP_WEIGHTX, 
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BELOW, 
                                                   GridBagConstraints.NONE, 
                                                   GridBagConstraints.NORTH,
                                                   new Insets(LayoutUtils.BUTTONP_BORDERSIZE +
                                                              LayoutUtils.DIALOG_IB_SPACE_BETWEEN_RELATED,
                                                              LayoutUtils.BUTTONP_BORDERSIZE + 
                                                              LayoutUtils.DIALOG_INDENT_1,
                                                              LayoutUtils.BUTTONP_BORDERSIZE,
                                                              LayoutUtils.BUTTONP_BORDERSIZE));
            gridbag.setConstraints(buttonPanel, c);
            add(buttonPanel);
            gridy++;


            return gridy;
        }
        
        
        //-------------------------------------------
        /**
         * Set up the panel to specify a content filter.
         *
         * @param gridbag  the gridbag layout object
         * @param al       the event listener
         * @param gridy    the first available vertical position in the layout
         * @return  the first available vertical position in the layout after this method
         *          has finished laying out all of its components
         */
        private int initFilterPanel(GridBagLayout gridbag, ActionListener al, int gridy) {

            // Set the label.
            JLabel label = new JLabel("Content Filter:");
            label.setHorizontalAlignment(JLabel.LEFT);
            label.setVerticalAlignment(JLabel.BOTTOM);
            GridBagConstraints c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                                      2, 1,
                                                                      LayoutUtils.LABEL_WEIGHTX, 
                                                                      LayoutUtils.DIALOG_B_WEIGHTY_ABOVE,
                                                                      GridBagConstraints.HORIZONTAL, 
                                                                      GridBagConstraints.SOUTHWEST,
                                                                      new Insets(LayoutUtils.DIALOG_B_SPACE_BETWEEN, 
                                                                                 0,0,0));
            gridbag.setConstraints(label, c);
            add(label);
            gridy++;


            // Set the filter chooser.
            filterTextField = new JTextField(15);
            filterTextField.setEditable(false);
            c = LayoutUtils.makeGridBagConstraints(0, gridy,
                                                   1, 1,
                                                   LayoutUtils.TEXTFIELD_FILE_WEIGHTX,
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BELOW,
                                                   GridBagConstraints.HORIZONTAL, 
                                                   GridBagConstraints.NORTHWEST,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
                                                              LayoutUtils.DIALOG_INDENT_1, 0, 0));
            gridbag.setConstraints(filterTextField, c);
            add(filterTextField);
            
            JButton button = new JButton("Change");
            button.setToolTipText("Change the content filter");
            button.setActionCommand("Change");
            button.addActionListener(al);
            c = LayoutUtils.makeGridBagConstraints(1, gridy,
                                                   1, 1,
                                                   LayoutUtils.BUTTONP_WEIGHTX,
                                                   LayoutUtils.DIALOG_B_WEIGHTY_BELOW,
                                                   GridBagConstraints.NONE, 
                                                   GridBagConstraints.NORTHEAST,
                                                   new Insets(LayoutUtils.DIALOG_IB_SPACE_BETWEEN, 
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
        private FIGFileReader getSource() {
            FIGFileReader source = null;
            
            java.util.List filesList = TableUtils.getValuesFromDataVector(fileTableModel.getDataVector(), COLUMN_FILES);
            if (filesList.size() > 0) {
                try {
                    File[] files = (File[]) (new ArrayList(filesList)).toArray(new File[filesList.size()]);
                    source = new FIGFileReader(files, contentFilter);
                }
                catch (Exception e) {
                    source = null;
                    System.err.println("Unexpected exception: " + e);
                }
            }
            
            return source;
        }


        //-------------------------------------------
        /**
         * Set the content filter.  If the specified filter is null, the 
         * filter is set to filter nothing.
         *
         * @param newFilter  the new filter
         */
        private void setContentFilter(FIGFileContentFilter newFilter) {
            contentFilter = newFilter;

            if (allowContentFilter) {
                filterTextField.setText(newFilter.toString());
            }
        }


        //-------------------------------------------
        /**
         * Adds the specified files to the file table.
         *
         * @param files  the list of files
         * @exception IOException  thrown if one of the specified files is not a valid FIG file
         */
        private void addFiles(File[] files) throws IOException {
            validateFiles(files);
            if (files != null) {
                for (int i = 0; i < files.length; i++) {
                    Vector newRow = new Vector();
                    newRow.add(files[i]);
                    fileTableModel.addRow(newRow);
                }
            }
        }


        //-------------------------------------------
        /**
         * Validates that each file in the input array is a valid FIG file.
         *
         * @param files  the list of files
         * @exception IOException  thrown if one of the specified files is not a valid FIG file
         */
        private void validateFiles(File[] files) throws IOException {
            if ((files != null) && (files.length > 0)) {

                // DON'T DO THIS!  If they choose a file without the FIG extension,
                // treat it as a bad file instead of assuming the user wanted the
                // FIG file.

                // Add the FIG extension to each file if it doesn't exist.
                //for (int i = 0; i < files.length; i++) {
                //    files[i] = FIGFileChooser.addFIGExtension(files[i]);
                //}

                
                // Try creating a FIGFileReader with it.
                new FIGFileReader(files, contentFilter);
            }
        }
                
        
        //-------------------------------------------
        /**
         * Verifies that all input parameters are valid.
         *
         * @return  true if all input parameters are valid; false otherwise
         */
        private boolean validateInputParameters() {
            
            // Verify that at least one filename has been specified.             
            java.util.List files = TableUtils.getValuesFromDataVector(fileTableModel.getDataVector(), COLUMN_FILES);
            if (files.size() == 0) {
                JOptionPane.showMessageDialog((JFrame) parentDialog.getOwner(),
                                              "You must specify a valid FIG file!",
                                              "Missing Filename",
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
            TableUtils.setDataVector(fileTableModel, null);
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


            // Add Files button

            else if (command.equals("Add Files")) {
                fileChooser.setMultiSelectionEnabled(true);
                fileChooser.setDialogTitle("Choose a File");
                fileChooser.rescanCurrentDirectory();

                // Make the user keep choosing until he chooses a valid file or cancels the operation.
                boolean done = false;
                while (!done) {
                    int returnVal = fileChooser.showDialog((JFrame) parentDialog.getOwner(), "Choose");
                    
                    if (returnVal == JFileChooser.APPROVE_OPTION) {
                        File[] selectedFiles = fileChooser.getSelectedFiles();

                        try {
                            addFiles(selectedFiles);
                            done = true;
                        }
                        catch (Exception e) {
                            // Pop up a warning if the chosen files were bad.
                            System.err.println(e.toString());
                            JOptionPane.showMessageDialog((JFrame) parentDialog.getOwner(),
                                                          "You must choose valid filenames!",
                                                          "Bad Filenames",
                                                          JOptionPane.ERROR_MESSAGE);
                            continue;
                        }
                    }
                    else {
                        done = true;
                    }
                }
            }


            // Delete Files button

            else if (command.equals("Delete Files")) {                
                int[] selectedRows = fileTable.getSelectedRows();
                
                // Remove the row with the highest row index first so
                // we do not run into "index shifting" errors.
                for (int i = selectedRows.length - 1; i >= 0; i--) {
                    fileTableModel.removeRow(selectedRows[i]);
                }
            }


            // Change button

            else if (command.equals("Change")) {
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
                    
    } // inner class ContentPanel

}
