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

import java.util.List;

import javax.swing.JFileChooser;
import javax.swing.JPanel;

/**
 * A panel containing configuration options.
 */
abstract class ConfigPanel extends JPanel {

    
    //
    // MEMBER VARIABLES
    //
    
    /**
     * File chooser dialog box
     */
    protected static JFileChooser fileChooser = new JFileChooser();
    
    
    
    //
    // METHODS
    //

    //-------------------------------------------
    /**
     * Load the default configuration properties stored on disk.
     */
    abstract void loadDefaultConfigurationProperties();


    //-------------------------------------------
    /**
     * Set the configuration options in TSAFE's properties, but do
     * not yet save these settings for future sessions.
     */
    abstract void setConfigurationProperties();


    //-------------------------------------------
    /**
     * Saves the chosen config options as the default configuration for
     * future sessions.
     */
    abstract void saveConfigurationAsDefault();


    //-------------------------------------------
    /**
     * Validates that the input parameters in this config panel are correct.  If there
     * are any errors, returns a list of error Strings; otherwise, if there are no
     * errors returns an empty List.
     */
    abstract List validateInputParameters();

}
