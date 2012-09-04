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

import java.util.Comparator;
import java.util.Vector;

/**
 * Compares two Vectors based on the value at a specified index position.
 * <p>
 * This and some related classes in this package are either partially or 
 * completely based on original source code written by Claude Duguay
 * (Copyright (c) 2002).
 *
 */
public class ColumnComparator implements Comparator {

    //
    // MEMBER VARIABLES
    //

    protected int index;
    protected boolean ascending;
  


    //
    // METHODS
    //

    //-------------------------------------------
    public ColumnComparator(int index, boolean ascending) {
        this.index = index;
        this.ascending = ascending;
    }
  

    //-------------------------------------------
    public int compare(Object one, Object two) {
        if (one instanceof Vector && two instanceof Vector) {
            Vector vOne = (Vector)one;
            Vector vTwo = (Vector)two;
            Object oOne = vOne.elementAt(index);
            Object oTwo = vTwo.elementAt(index);
            if (oOne instanceof Comparable && oTwo instanceof Comparable) {
                Comparable cOne = (Comparable) oOne;
                Comparable cTwo = (Comparable) oTwo;
                if (ascending) {
                    return cOne.compareTo(cTwo);
                }
                else {
                    return cTwo.compareTo(cOne);
                }
            }
        }
        
        return 1;
    }

}

