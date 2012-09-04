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

import java.util.Collections;
import java.util.Vector;

import javax.swing.table.DefaultTableModel;

/**
 * The default table model for a JSortTable.
 * <p>
 * This and some related classes in this package are either partially or 
 * completely based on original source code written by Claude Duguay
 * (Copyright (c) 2002).
 *
 */
public class DefaultSortTableModel extends DefaultTableModel implements SortTableModel {

    
    //
    // MEMBER VARIABLES
    //

    /**
     * True if this table is editable.
     */
    private boolean editable = true;



    // 
    // METHODS
    //

    //-------------------------------------------
    public DefaultSortTableModel() {
    }
  

    //-------------------------------------------
    public DefaultSortTableModel(int rows, int cols) {
        super(rows, cols);
    }
  

    //-------------------------------------------
    public DefaultSortTableModel(Object[][] data, Object[] names) {
        super(data, names);
    }
  

    //-------------------------------------------
    public DefaultSortTableModel(Object[] names, int rows) {
        super(names, rows);
    }
  

    //-------------------------------------------
    public DefaultSortTableModel(Vector names, int rows) {
        super(names, rows);
    }
  

    //-------------------------------------------
    public DefaultSortTableModel(Vector data, Vector names) {
        super(data, names);
    }
  

    //-------------------------------------------
    public boolean isSortable(int col) {
        return true;
    }
  

    //-------------------------------------------
    public void sortColumn(int col, boolean ascending) {
        Collections.sort(getDataVector(), new ColumnComparator(col, ascending));
    }


    //-------------------------------------------
    /**
     * Allows the user to dynamically toggle whether this table is editable.
     *
     * @param editable  true if this table is editable; false otherwise
     */
    public void setEditable(boolean editable) {
        this.editable = editable;
    }


    //-------------------------------------------
    public boolean isCellEditable(int row, int column) {
        return editable;
    }
            
}

