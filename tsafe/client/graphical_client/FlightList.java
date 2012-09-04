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

package tsafe.client.graphical_client;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;

import javax.swing.JList;

import tsafe.common_datastructures.Flight;

/**
 * Shows all the flights in an alphabetized flight list
 */
class FlightList extends JList {

    /**
     * Flight comparator
     */
    private static final Comparator FLIGHT_COMPARATOR = new FlightComparator();
    
    /**
     * Constructs a flight list
     */
    public FlightList() {
        super();
    }

    /**
     * Sets the flights to be shown in the list
     */
    public void setFlights(Collection flights) {
        // Remember all the flights that were selected beforehand
        Object[] selected = super.getSelectedValues();

        // Sort the flights and set the data of the JList
        Object[] newFlights = flights.toArray();
        Arrays.sort(newFlights, FLIGHT_COMPARATOR);
        super.setListData(newFlights);

        // Find the indices int the new list of all the
        // previously selected flights if they are in the new list
        Collection newSelected = new LinkedList();
        for (int i = 0; i < selected.length; i++) {
            int flightIdx = Arrays.binarySearch(newFlights, selected[i], FLIGHT_COMPARATOR);
            if (flightIdx >= 0) newSelected.add(new Integer(flightIdx));
        }

        // Select all these flights
        int[] selectedIndices = new int[newSelected.size()];
        Iterator idxIter = newSelected.iterator();
        for (int i = 0; idxIter.hasNext(); i++) {
            selectedIndices[i] = ((Integer)idxIter.next()).intValue();
        }
        super.setSelectedIndices(selectedIndices);
    }

    /**
     * Returns the selected flights
     */
    public Collection getSelectedFlights() {
        return Arrays.asList(super.getSelectedValues());
    }

    /**
     * Comparator used for sorting flights by their ids
     */
    private static class FlightComparator implements Comparator {
        public int compare(Object o1, Object o2) {
            String flightId1 = ((Flight)o1).getAircraftId();
            String flightId2 = ((Flight)o2).getAircraftId();
            return flightId1.compareTo(flightId2);
        }
    }
}
