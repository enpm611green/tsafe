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

package tsafe.server.server_gui.utils;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Toolkit;

/**
 * Utilities related to component layout.
 */
public class LayoutUtils {


    //
    // GENERAL PANEL LAYOUT CONSTANTS
    //

    // Borders
    public final static int PANEL_BORDERSIZE = 20;

    // Indents
    public final static int PANEL_INDENT_1 = 12;
    public final static int PANEL_INDENT_2 = 24;

    // IB = IntraBlock
    public final static int PANEL_IB_SPACE_BETWEEN = 10;
    public final static int PANEL_IB_SPACE_BETWEEN_RELATED = 5;
    public final static double PANEL_IB_WEIGHTY_BETWEEN = 0.0;

    // B = Block
    public final static int PANEL_B_SPACE_BETWEEN = 20;
    public final static int PANEL_B_SPACE_ABOVE_FINAL_BUTTONP = PANEL_B_SPACE_BETWEEN + 5;
    public final static int PANEL_B_SPACE_ABOVE_TOPMOST = 5;
    public final static int PANEL_B_SPACE_BELOW_BOTTOMMOST = 5;
    public final static double PANEL_B_WEIGHTY_TOPMOST = 0.1;
    public final static double PANEL_B_WEIGHTY_BOTTOMMOST = 0.2;
    public final static double PANEL_B_WEIGHTY_ABOVE = 0.2;
    public final static double PANEL_B_WEIGHTY_BELOW = 0.2;
    public final static double PANEL_B_WEIGHTY_FINAL_BUTTONP = 0.0;

    // SP = Separator
    public final static int PANEL_SP_SPACE_ABOVE = PANEL_B_SPACE_BETWEEN;
    public final static int PANEL_SP_SPACE_BELOW = PANEL_B_SPACE_BETWEEN + 5;
    public final static int PANEL_SP_SPACE_ABOVE_FINAL_BUTTONP = PANEL_B_SPACE_BETWEEN + 10;
    public final static int PANEL_SP_SPACE_BELOW_FINAL_BUTTONP = PANEL_B_SPACE_BETWEEN - 5;
    public final static double PANEL_SP_WEIGHTX = 1.0;
    public final static double PANEL_SP_WEIGHTY = 0.1;

 

    //
    // DIALOG LAYOUT CONSTANTS
    //

    // Borders
    public final static int DIALOG_BORDERSIZE = 20;

    // Indents
    public final static int DIALOG_INDENT_1 = 12;
    public final static int DIALOG_INDENT_2 = 24;

    // IB = IntraBlock
    public final static int DIALOG_IB_SPACE_BETWEEN = 10;
    public final static int DIALOG_IB_SPACE_BETWEEN_RELATED = 5;
    public final static double DIALOG_IB_WEIGHTY_BETWEEN = 0.0;

    // B = Block
    public final static int DIALOG_B_SPACE_BETWEEN = 20;
    public final static int DIALOG_B_SPACE_ABOVE_FINAL_BUTTONP = DIALOG_B_SPACE_BETWEEN;
    public final static int DIALOG_B_SPACE_ABOVE_TOPMOST = 5;
    public final static int DIALOG_B_SPACE_BELOW_BOTTOMMOST = 5;
    public final static double DIALOG_B_WEIGHTY_TOPMOST = 0.1;
    public final static double DIALOG_B_WEIGHTY_BOTTOMMOST = 0.2;
    public final static double DIALOG_B_WEIGHTY_ABOVE = 0.2;
    public final static double DIALOG_B_WEIGHTY_BELOW = 0.2;
    public final static double DIALOG_B_WEIGHTY_FINAL_BUTTONP = 0.0;

    // SP = Separator
    public final static int DIALOG_SP_SPACE_ABOVE = DIALOG_B_SPACE_BETWEEN;
    public final static int DIALOG_SP_SPACE_BELOW = DIALOG_B_SPACE_BETWEEN + 5;
    public final static int DIALOG_SP_SPACE_ABOVE_FINAL_BUTTONP = DIALOG_B_SPACE_BETWEEN + 10;
    public final static int DIALOG_SP_SPACE_BELOW_FINAL_BUTTONP = DIALOG_B_SPACE_BETWEEN - 5;
    public final static double DIALOG_SP_WEIGHTX = 1.0;
    public final static double DIALOG_SP_WEIGHTY = 0.1;

    

    //
    // BUTTON PANEL LAYOUT CONSTANTS
    //

    public final static int BUTTONP_BORDERSIZE = 5;
    public final static int BUTTONP_SPACE_HORIZONTAL = 8;
    public final static int BUTTONP_SPACE_VERTICAL = 5;
    public final static double BUTTONP_WEIGHTX = 0.0;
    public final static double BUTTONP_WEIGHTY = 0.0;



    //
    // TABLE LAYOUT CONSTANTS
    //

    public final static int TABLE_SINGLE_COLUMN_WIDTH = 300;
    public final static double TABLE_WEIGHTX = 0.8;
    public final static double TABLE_WEIGHTY = 0.8;



    //
    // OTHER COMPONENTS LAYOUT CONSTANTS
    //

    public final static double LABEL_WEIGHTX = 0.0;
    public final static double TEXTFIELD_FILE_WEIGHTX = 1.0;
    public final static double TEXTAREA_WEIGHTX = 0.1;
    public final static double CHECKBOX_WEIGHTX = 0.0;
    public final static Dimension EXPANDEDVIEWER_SIZE = new Dimension(300,100);





    //
    // METHODS
    //

    //-------------------------------------------
  	/**
     * Returns a GridBagConstraints object based on the information provided.
     *
     * @param gridx           the leftmost column of this component
     * @param gridy           the topmost row of this component
     * @param gridwidth       the number of columns this component spans
     * @param gridheight      the number of rows this component spans
     * @param weightx         the weighting factor hint used to allocate extra row space
     * @param weighty         the weighting factor hint used to allocate extra column space
     * @param fill            how the component should use up any extra space
     * @param anchor          where the component should be situated if there is extra space
     * @param externalPadding how much padding to apply around the component
     * @return a GridBagConstraints object based on the input constraints
     */
    public static GridBagConstraints makeGridBagConstraints(int gridx, int gridy, 
                                                     int gridwidth, int gridheight, 
                                                     double weightx, double weighty, 
                                                     int fill, int anchor,
                                                     Insets externalPadding) {
		GridBagConstraints c = new GridBagConstraints();
		c.gridx = gridx;
		c.gridy = gridy;
		c.gridwidth = gridwidth;
		c.gridheight = gridheight;
		c.weightx = weightx;
		c.weighty = weighty;
		c.fill = fill;
        c.anchor = anchor;
        c.insets = externalPadding;
		
		return c;
  	}


    //-------------------------------------------
    /**
     * Finds the location of the upper-left corner of a window such
     * that the window is centered in the screen.
     *
     * @param windowSize  the size of the window
     * @return  the point that centers the window, or (0,0) if the center
     *          cannot be found
     */
    public static Point centerWindow(Dimension windowSize) {
        Point location = new Point(0,0);

        if (windowSize != null) {

            // Get the width and height of the window.
            int windowHeight = (int) windowSize.getHeight();
            int windowWidth = (int) windowSize.getWidth();
            if ((windowHeight != -1) && (windowWidth != -1)) {
                
                // Get the screen dimensions.
                Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
                
                // Find the location that centers the window.
                int x = (screenDim.width - windowWidth) / 2;
                int y = (screenDim.height - windowHeight) / 2;
                location = new Point(x,y);
            }
        }
        
        
        return location;
    }

}
