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

import java.io.File;

import javax.swing.JFileChooser;

import fig.io.FIGFile;

/**
 * A file chooser that allows only FIG files to be selected.
 */
public class FIGFileChooser extends JFileChooser {
    
    
    //
    // METHODS
    //
    
    //-------------------------------------------
    /**
     * Creates a new FIG file chooser.
     */
    public FIGFileChooser() {
        this(null);
    }


    //-------------------------------------------
    /**
     * Creates a new FIG file chooser.
     *
     * @param currDirectory  the initial directory
     */
    public FIGFileChooser(File currDirectory) {
        super(currDirectory);

        addChoosableFileFilter(new FIGFileFilter());
        setAcceptAllFileFilterUsed(false);
        setFileSelectionMode(JFileChooser.FILES_ONLY);
    }

    

    //
    // UTILITY METHODS
    //
    
    //-------------------------------------------
    /**
     * Return the extension portion of the file's name.
     */
    public static String getExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int dotIndex = filename.lastIndexOf('.');
            if ((dotIndex > 0) && (dotIndex < filename.length() - 1)) {
                return filename.substring(dotIndex + 1).toLowerCase();
            };
        }
        
        return null;
    }


    //-------------------------------------------
    /**
     * Adds a ".fig" extension to the file if it doesn't already exist.
     */
    public static File addFIGExtension(File f) {
        File returnFile = f;

        if ((f != null) && (!f.isDirectory())) {
            String extension = getExtension(f);

            if (extension == null) {
                String filename = f.getPath();
                returnFile = new File(filename + "." + FIGFile.FIG_EXTENSION);
            }
            else if (!extension.equals(FIGFile.FIG_EXTENSION)) {
                String filename = f.getPath() + "." + FIGFile.FIG_EXTENSION;
                returnFile = new File(filename);
            }
        }
        
        return returnFile;
    }




    //
    // INNER CLASSES
    //

    /**
     * Filters out all files except FIG files.
     */
    public class FIGFileFilter extends javax.swing.filechooser.FileFilter {
       

        //
        // FILEFILTER METHODS
        //

        //-------------------------------------------
        public boolean accept(File f) {
            if (f != null) {
                if (f.isDirectory()) {
                    return true;
                }
                
                String extension = getExtension(f);
                if ((extension != null) && (extension.equals(FIGFile.FIG_EXTENSION))) {
                    return true;
                }
            }

            return false;
        }


        //-------------------------------------------
        public String getDescription() {
            return "FIG files (*." + FIGFile.FIG_EXTENSION + ")";
        }
        
    } // inner class FIGFileFilter

}
