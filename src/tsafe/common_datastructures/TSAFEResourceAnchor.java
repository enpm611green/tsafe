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

package tsafe.common_datastructures;

import java.awt.Image;
import java.awt.Toolkit;
import java.io.InputStream;
import java.net.URL;

/**
 * The resource anchor can be used to load TSAFE-related resources.
 */
public class TSAFEResourceAnchor {


    //
    // CONSTANTS
    //

    /**
     * The location of all TSAFE-related resources.
     */
    private final static String RESOURCES_LOCATION = "_resources/";



	//
    // RESOURCE METHODS
	//

    //-------------------------------------------    
    /**
     * Gets the specified image resource.
     *
     * @param resourceName  the resource name
     * @return an Image for that resource
     */
    public static Image getImage(String resourceName) {
        URL imageUrl = TSAFEResourceAnchor.class.getResource(RESOURCES_LOCATION + resourceName);
        return Toolkit.getDefaultToolkit().getImage(imageUrl);
    }


    //-------------------------------------------
    /**
     * Gets the specified resource as an input stream.
     *
     * @param resourceName  the resource name
     * @return an InputStream for that resource
     */
    public static InputStream getInputStream(String resourceName) {
        return TSAFEResourceAnchor.class.getResourceAsStream(RESOURCES_LOCATION + resourceName);        
    }
  
}
