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

package tsafe.server.server_gui;

import javax.swing.ImageIcon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;

import tsafe.common_datastructures.TSAFEResourceAnchor;
import tsafe.server.server_gui.utils.LayoutUtils;

/**
 * Shows "About TSAFE" information in a dialog.
 */
class AboutDialog {


	//
	// CONSTANTS
	//

    /**
     * The "About TSAFE" image.
     */
    private final static String ABOUT_TSAFE_IMAGE = "images/aboutTSAFE.jpg";



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
    static void showDialog(JFrame parent, String title) {
        JDialog dialog = new JDialog(parent, title, true);

        // Load the "About TSAFE" image.
        ImageIcon image = new ImageIcon(TSAFEResourceAnchor.getImage(ABOUT_TSAFE_IMAGE));
        dialog.getContentPane().add(new JLabel(image));
        dialog.pack();

        // Center the dialog on the screen.
        dialog.setLocation(LayoutUtils.centerWindow(dialog.getSize()));

        // Show the dialog.
        dialog.show();        
    }

} 
