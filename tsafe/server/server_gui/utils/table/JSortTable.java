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

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Vector;

import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;

/**
 * A sortable JTable.
 * <p>
 * This and some related classes in this package are either partially or 
 * completely based on original source code written by Claude Duguay
 * (Copyright (c) 2002).
 *
 */
public class JSortTable extends JTable implements MouseListener {


    //
    // MEMBER VARIABLES
    //

    protected int sortedColumnIndex = -1;
    protected boolean sortedColumnAscending = true;
  


    //
    // METHODS
    //

    //-------------------------------------------
    public JSortTable() {
        this(new DefaultSortTableModel());
    }
  

    //-------------------------------------------
    public JSortTable(int rows, int cols) {
        this(new DefaultSortTableModel(rows, cols));
    }
  

    //-------------------------------------------
    public JSortTable(Object[][] data, Object[] names) {
        this(new DefaultSortTableModel(data, names));
    }
  

    //-------------------------------------------
    public JSortTable(Vector data, Vector names) {
        this(new DefaultSortTableModel(data, names));
    }
  

    //-------------------------------------------
    public JSortTable(SortTableModel model) {
        super(model);
        initSortHeader();
    }


    //-------------------------------------------
    public JSortTable(SortTableModel model,
                      TableColumnModel colModel) {
        super(model, colModel);
        initSortHeader();
    }


    //-------------------------------------------
    public JSortTable(SortTableModel model,
                      TableColumnModel colModel,
                      ListSelectionModel selModel) {
        super(model, colModel, selModel);
        initSortHeader();
    }


    //-------------------------------------------
    protected void initSortHeader() {
        JTableHeader header = getTableHeader();
        header.setDefaultRenderer(new SortHeaderRenderer());
        header.addMouseListener(this);
    }


    //-------------------------------------------
    public int getSortedColumnIndex() {
        return sortedColumnIndex;
    }
  

    //-------------------------------------------
    public boolean isSortedColumnAscending() {
        return sortedColumnAscending;
    }
  

    //-------------------------------------------
    public void mouseReleased(MouseEvent event) {
        TableColumnModel colModel = getColumnModel();
        int index = colModel.getColumnIndexAtX(event.getX());
        int modelIndex = colModel.getColumn(index).getModelIndex();
    
        SortTableModel model = (SortTableModel)getModel();
        if (model.isSortable(modelIndex))
            {
                // toggle ascension, if already sorted
                if (sortedColumnIndex == index)
                    {
                        sortedColumnAscending = !sortedColumnAscending;
                    }
                sortedColumnIndex = index;
    
                model.sortColumn(modelIndex, sortedColumnAscending);
            }
    }
  

    public void mousePressed(MouseEvent event) {}
    public void mouseClicked(MouseEvent event) {}
    public void mouseEntered(MouseEvent event) {}
    public void mouseExited(MouseEvent event) {}


    //-------------------------------------------
    /**
     * Returns a FixedTableCellRenderer.
     *
     * @param row a value of type 'int'
     * @param column a value of type 'int'
     * @return a value of type 'TableCellRenderer'
     */
    public TableCellRenderer getCellRenderer(int row, int column) {
        return new FixedTableCellRenderer();
    }

}

