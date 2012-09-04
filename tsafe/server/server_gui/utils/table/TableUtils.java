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

package tsafe.server.server_gui.utils.table;

import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;

import tsafe.server.server_gui.utils.LayoutUtils;

/**
 * Utilities related to JTables.
 */
public class TableUtils {


    //-------------------------------------------
    /**
     * Adds a mouse lisenter to the input table that listens for a double click 
     * on one of the table's cells and then displays the full contents of that
     * cell in a popup dialog.
     *
     * @param JTable   the table
     */
    public static void addExpandedCellViewer(final JTable table) {
        if (table != null) {
            table.addMouseListener(new MouseAdapter() {
                    public void mouseClicked(MouseEvent e) {
                        if (e.getClickCount() == 2) {
                            Point p = e.getPoint();
                            int row = table.rowAtPoint(p);
                            int column = table.columnAtPoint(p);
                            Object o = table.getValueAt(row, column);                            

                            JDialog dialog = new JDialog((Frame) null, "Expanded View", true);
                            JScrollPane contentPane = new JScrollPane(new JLabel(o.toString()));
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
        }
    }                               
    

    //-------------------------------------------
    /**
     * Removes all existing rows in the table model and adds the rows found
     * in the input data vector.  Doing this allows all the table settings
     * (i.e. column sizes, policies, etc.) to remain intact.
     *
     * @param model      the table model
     * @param dataVector the new table rows
     */
    public static void setDataVector(DefaultTableModel model, Vector dataVector) {

        // Make sure the input parameters are valid.
        if (model != null) {

            // Clone the input vector.  This handles the case where the input data vector
            // originated from a call to "model.getDataVector()".  That method exposes
            // the rep of the table model (i.e. returns the actual data vector).  If we
            // don't clone the vector, then removing all rows from the model in the next
            // step will cause the input data vector to become empty!
            if (dataVector == null) {
                dataVector = new Vector();
            }
            else {
                dataVector = (Vector) dataVector.clone();
            }

            // Remove all existing rows from the table model.
            for (int row = model.getRowCount() - 1; row >= 0; row--) {
                model.removeRow(row);
            }

            // Add new rows.
            for (int i = 0; i < dataVector.size(); i++) {
                Vector newRow = (Vector) dataVector.get(i);
                model.addRow(newRow);
            }
        }
    }


    //-------------------------------------------
    /**
     * Returns the value of the specified column for each row
     * in the data vector.
     *
     * @param dataVector  the data vector
     * @param column      the column of interest
     * @return            a List of Objects
     */
    public static java.util.List getValuesFromDataVector(Vector dataVector, int column) {
        java.util.List values = new Vector();

        // Make sure the input parameters are valid.
        if ((dataVector != null) && (dataVector.size() > 0)) {
            int columnCount = ((Vector) dataVector.get(0)).size();
            if ((column >= 0) && (column < columnCount)) {
                
                // Loop through each row vector and extract the approriate value.
                for (int row = 0; row < dataVector.size(); row++) {
                    Vector rowVector = (Vector) dataVector.get(row);
                    values.add(rowVector.get(column));
                }
            }
        }

        return values;
    }


    //-------------------------------------------
    /**
     * Returns the value of the specified column for each row
     * currently selected in the table.
     *
     * @param table   the table
     * @param column  the column of interest
     * @return        an array of Strings
     */
    public static String[] getSelectedValues(JTable table, int column) {
        String[] selectedValues = new String[0];

        // Make sure the input parameters are valid.
        if (table != null) {
            int columnCount = table.getColumnCount();            
            if ((column >= 0) && (column < columnCount)) {
                
                // Get the indices of all selected rows, then extract the
                // value at the specified column for each row.
                int[] selectedRows = table.getSelectedRows();
                selectedValues = new String[selectedRows.length];
                for (int i = 0; i < selectedRows.length; i++) {
                    selectedValues[i] = (String) table.getValueAt(selectedRows[i], column);
                }
            }
        }

        return selectedValues;
    }


    //-------------------------------------------
    /**
     * Selects rows which have one of the values in the input array in the
     * specified column.  If there are multiple rows with the same value, only
     * the last row in the table with that value is selected.
     *
     * @param table   the table
     * @param values  a list of values
     * @param column  the column of interest
     */
    public static void selectRows(JTable table, String[] values, int column) {

        // Make sure the input parameters are valid.
        if ((values != null) && (table != null)) {
            int columnCount = table.getColumnCount();            
            if ((column >= 0) && (column < columnCount)) {

                // For each row, create a mapping between the value in the specified 
                // column and the row's index.  Note that if there are multiple 
                // rows that have the same value, only the last row will be mapped.
                Map valueRowMapping = new HashMap();
                int rowCount = table.getRowCount();                
                for (int row = 0; row < rowCount; row++) {
                    valueRowMapping.put(table.getValueAt(row, column), new Integer(row));
                }

                // Now look up the indices of the rows mapped to by the values in the input
                // array and select them.
                table.clearSelection();
                for (int i = 0; i < values.length; i++) {
                    if ((values[i] != null) && (valueRowMapping.containsKey(values[i]))) {
                        int row = ((Integer) valueRowMapping.get(values[i])).intValue();
                        table.addRowSelectionInterval(row, row);
                    }
                }
            }
        }
    }


    //-------------------------------------------
    /**
     * For each value in the input array that does not appear in the specified column
     * of at least one of the table model's rows, a new row is added with that value
     * in the specified column and the empty string in the remaining columns.  Note
     * that null values in the input array are ignored.
     *
     * @param model   the table model
     * @param values  a list of values
     * @param column  the column of interest
     * @return        the number of new rows actually inserted
     */
    public static int addMissingRows(DefaultTableModel model, String[] values, int column) {
        int numInserted = 0;

        // Make sure the input parameters are valid.
        if ((values != null) && (model != null)) {
            int columnCount = model.getColumnCount();            
            if ((column >= 0) && (column < columnCount)) {

                // Make a list of all unique values in the specified column
                // for all rows in the table model.
                Set existingValues = new HashSet();
                int rowCount = model.getRowCount();                
                for (int row = 0; row < rowCount; row++) {
                    existingValues.add(model.getValueAt(row, column));
                }

                // Now, if a value in the input array does not already exist in the specified column
                // of some row in the table model, go ahead and add a new row with that value.
                for (int i = 0; i < values.length; i++) {
                    if ((values[i] != null) && (!existingValues.contains(values[i]))) {
                        Vector newRow = new Vector(columnCount);
                        for (int j = 0; j < columnCount; j++) {
                            if (j == column) {
                                newRow.add(values[i]);
                            }
                            else {
                                newRow.add("");
                            }                            
                        }
                        model.addRow(newRow);
                        existingValues.add(values[i]);
                        numInserted++;
                    }
                }
            }
        }

        return numInserted;
    }
   
}
