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


/**
 * Represents an airway, such as a jet or victor route
 */
public class Airway extends Route {

    /**
     * This airway's id
     */
    private String id;

    /**
     * Creates a new airway route with the given id.
     */
    public Airway(String id) {
        super();
        this.id = id;
    }

    /**
     * Return this airway's id
     */
    public String getId() {
        return this.id;
    }

    /**
     * Return a hash code for this object
     */
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Return true is o is an Airway and has the same id as this
     */
    public boolean equals(Object o) {
        return o != null &&
               o instanceof Airway &&
               ((Airway)o).id.equals(this.id);
    }
}
