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

package tsafe.common_datastructures.client_server_communication;

import java.util.Collection;
import java.util.Map;

/**
 * The results of the engines computations
 */
public class ComputationResults {

    /** Result fields */
    private Collection flights, blunders;
    private Map flight2TrajMap;

    /** Empty constructor */
    public ComputationResults(Collection flights, Collection blunders, Map flight2TrajMap) {
        this.flights = flights;
        this.blunders = blunders;
        this.flight2TrajMap = flight2TrajMap;
    }

    // GETTERS

    /** Return flights */
    public Collection getFlights() {
        return this.flights;
    }

    /** Return blunders */
    public Collection getBlunders() {
        return this.blunders;
    }

    /** Return flight trajectory map */
    public Map getFlight2TrajectoryMap() {
        return this.flight2TrajMap;
    }
}
