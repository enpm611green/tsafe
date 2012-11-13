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

import javax.swing.JPanel;

import tsafe.common_datastructures.TSAFEProperties;
import fig.io.FIGFileContentFilter;

/**
 * A panel containing filtering options.
 */
abstract class FilterPanel extends JPanel {


    //-------------------------------------------
    /**
     * Sets the filtering options in this panel to reflect the specified content
     * filter.  If the content filter is null, sets the options to reflect the
     * system defaults.
     *
     * @param filter  the content filter
     */
    void setFilteringConditions(FIGFileContentFilter filter) {
        if (filter == null) {
            filter = TSAFEProperties.getContentFilter();
        }
    }


    //-------------------------------------------
    /**
     * Updates the specified content filter to reflect the filtering options chosen
     * by the user within this filter panel.
     *
     * @param filter  the content filter to update
     */
    abstract void updateContentFilter(FIGFileContentFilter filter);

}
