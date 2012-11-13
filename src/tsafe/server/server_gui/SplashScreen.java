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

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Window;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import tsafe.common_datastructures.TSAFEResourceAnchor;
import tsafe.server.server_gui.utils.LayoutUtils;

/**
 * A centered splash screen.
 */
public class SplashScreen {


    //
    // MEMBER VARIABLES
    //

    /**
     * The frame that holds the splash window.
     */
    private Frame frame;
    
    /**
     * The splash window.
     */
    private Window window;



    //
    // METHODS
    //

    //-------------------------------------------
    /**
     * Constructs a new splash screen with the specified image.
     *
     * @param image an image
     */
    private SplashScreen(Image image) {

        // Create a frameless window.
        frame = new Frame();
        frame.addNotify();            
        window = new Window(frame);            
        window.addNotify();
        JLabel imageViewer = new JLabel(new ImageIcon(image));
        window.add(imageViewer);

        // Center the splash screen and set the window size.
        Dimension imageDim = new Dimension(image.getWidth(null), image.getHeight(null));            
        window.setLocation(LayoutUtils.centerWindow(imageDim));
        window.setSize(imageDim);
        imageViewer.setSize(imageDim);
    }

        
    //-------------------------------------------
    /**
     * Shows the splash screen.
     */
    private void makeVisible() {
        window.setVisible(true);
    }


    //-------------------------------------------
    /**
     * Disposes of this splash screen.
     */
    private void dispose() {
        window.removeNotify();
        frame.removeNotify();
        window.dispose();
        frame.dispose();
    }


    //-------------------------------------------
    public void finalize() {
        dispose();
    }



    //
    // STATIC METHODS
    //

    /**
     * The TSAFE splash screen image.
     */
    private final static String TSAFE_SPLASHSCREEN_IMAGE = "images/tsafe.gif";

    /**
     * The currnt splash screen.
     */
    private static SplashScreen splashScreen;


    //-------------------------------------------
    /**
     * Show the TSAFE splash screen.
     *
     * @param parent the parent frame
     * @param title  the title to display on the dialog
     */
    public static void show() {        
        splashScreen = new SplashScreen(TSAFEResourceAnchor.getImage(TSAFE_SPLASHSCREEN_IMAGE));
        splashScreen.makeVisible();
    }


    //-------------------------------------------
    /**
     * Hides and disposes of the TSAFE splash screen.
     */
    public static void hide() {
        if (splashScreen != null) {
            splashScreen.dispose();
        }

        splashScreen = null;
    }

} 
