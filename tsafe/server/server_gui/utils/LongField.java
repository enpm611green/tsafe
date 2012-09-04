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

import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.PlainDocument;

/**
 * A text field that only accepts valid longs.
 */
public class LongField extends JTextField {


    //
    // MEMBER VARIABLES
    //

    private Toolkit toolkit;
    private boolean nonNegativeOnly;



    //
    // METHODS
    //

    //-------------------------------------------
	public LongField(int columns) {
        this(columns, false);
    }


    //-------------------------------------------
	public LongField(int columns, boolean nonNegativeOnly) {
        super(columns);
     	toolkit = Toolkit.getDefaultToolkit();
        this.nonNegativeOnly = nonNegativeOnly;
 	}


    //-------------------------------------------
  	public long getValue() {
        long retVal = 0;

     	try {
            retVal = Long.parseLong(getText());
     	} 
        catch (NumberFormatException nfe) {
            // This should never happen because insertString allows
            // only properly formatted data to get in the field.
  			toolkit.beep();
     	}

     	return retVal;
 	}


    //-------------------------------------------
	public void setValue(long value) {
        if (nonNegativeOnly) {
            value = Math.abs(value);
        }

        setText("" + value);
 	}


	// ------------------------------------ //
  	protected Document createDefaultModel() {
        return new LongDocument();
  	}




    //
    // INNER CLASS
    //

 	protected class LongDocument extends PlainDocument {

        //-------------------------------------------
        public void insertString(int offs, String str, AttributeSet a) throws BadLocationException {
            char[] source = str.toCharArray();
            char[] result = new char[source.length];
            int j = 0;
            
            for (int i = 0; i < result.length; i++) {

                // Allow digits.
                if (Character.isDigit(source[i])) {
                    result[j++] = source[i];
                }

                // Allow negative signs (unless only positive integers are accepted).
                else if ((!nonNegativeOnly) && 
                         ((offs == 0) && (i == 0) && (source[i] == '-'))) {
                    result[j++] = source[i];
                }

                // Otherwise, beep for an "error".
                else {
                    toolkit.beep();
                }
            }

            super.insertString(offs, new String(result, 0, j), a);
        }
        
	} // inner class LongDocument
    
}
