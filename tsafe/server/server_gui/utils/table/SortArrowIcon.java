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

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;

import javax.swing.Icon;

/**
 * The sort arrow icon used to denote sort direction.
 * <p>
 * This and some related classes in this package are either partially or 
 * completely based on original source code written by Claude Duguay
 * (Copyright (c) 2002).
 *
 */
public class SortArrowIcon implements Icon {


    //
    // CONSTANTS
    //

    public static final int NONE = 0;
    public static final int DECENDING = 1;
    public static final int ASCENDING = 2;



    //
    // MEMBER VARIABLES
    //

    protected int direction;
    protected int width = 8;
    protected int height = 8;



    //
    // CONSTRUCTORS
    //

    //-------------------------------------------  
    public SortArrowIcon(int direction) {
        this.direction = direction;
    }
  

    //-------------------------------------------  
    public int getIconWidth() {
        return width;
    }
  

    //-------------------------------------------  
    public int getIconHeight() {
        return height;
    }
  

    //-------------------------------------------  
    public void paintIcon(Component c, Graphics g, int x, int y) {
        Color bg = c.getBackground();
        Color light = bg.brighter();
        Color shade = bg.darker();
  
        int w = width;
        int h = height;
        int m = w / 2;
        if (direction == ASCENDING)
            {
                g.setColor(shade);
                g.drawLine(x, y, x + w, y);
                g.drawLine(x, y, x + m, y + h);
                g.setColor(light);
                g.drawLine(x + w, y, x + m, y + h);
            }
        if (direction == DECENDING)
            {
                g.setColor(shade);
                g.drawLine(x + m, y, x, y + h);
                g.setColor(light);
                g.drawLine(x, y + h, x + w, y + h);
                g.drawLine(x + m, y, x + w, y + h);
            }
    }
}

