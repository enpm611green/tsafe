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

package tsafe.server.database;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import tsafe.common_datastructures.Airway;
import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.FlightPlan;
import tsafe.common_datastructures.FlightTrack;
import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.Sid;
import tsafe.common_datastructures.Star;

/**
 * The database of air traffic control information. Because this database
 * manages runtime objects, it filters out data that it finds to be unneccessary
 * so as to avoid OutOfMemory errors.
 */
public class RuntimeDatabase extends DatabaseInterface {
	// In memory database tables
	private Map flightsInBounds = new HashMap();

	private Map flightsOutBounds = new HashMap();

	private Map fixesInBounds = new HashMap();

	private Map fixesOutBounds = new HashMap();

	private Map airwaysInBounds = new HashMap();

	private Map airwaysOutBounds = new HashMap();

	private Map sidsInBounds = new HashMap();

	private Map sidsOutBounds = new HashMap();

	private Map starsInBounds = new HashMap();

	private Map starsOutBounds = new HashMap();

	/**
	 * RuntimeDatabase constructor
	 */
	public RuntimeDatabase() {
	}

	// ****************************
	// ***** Managing Flights *****
	// ****************************

	/**
	 * Inserts flights to the database
	 */
	public synchronized void insertFlight(Flight f) {
		FlightPlan fp = f.getFlightPlan();
		flightsInBounds.put(f.getAircraftId(), f);

	}

	public synchronized void updateFlight(Flight f) {
		deleteFlight(f.getAircraftId());
		insertFlight(f);
	}

	public synchronized void deleteFlight(String aircraftId) {
		delete(flightsInBounds, flightsOutBounds, aircraftId);
	}

	public synchronized Flight selectFlight(String aircraftId) {
		Object selected = select(flightsInBounds, flightsOutBounds, aircraftId);
		return selected == null ? null : new Flight((Flight) selected);
	}

	public synchronized Collection selectFlightsInBounds(LatLonBounds bounds) {
		
		FlightTrack ft = null;
		FlightPlan fp = null;
		boolean routeInBounds = false;
		Set deepCopyFlights = new HashSet();

		Iterator flightIter = flightsInBounds.values().iterator();
						
		while (flightIter.hasNext()) {
			Flight f = (Flight) flightIter.next();
			ft = f.getFlightTrack();
			fp = f.getFlightPlan();
			routeInBounds = fp != null
					&& super.routeInBounds(fp.getRoute(), bounds);
			// If flight or route is in bounds add it to the list
			if (ft != null)
				if ((bounds.contains(ft.getLatitude(), ft.getLongitude()))
						|| (routeInBounds)) {
					deepCopyFlights.add(new Flight(f));
				}
		}

		return deepCopyFlights;
	}

	// **************************
	// ***** Managing Fixes *****
	// **************************

	public synchronized void insertFix(Fix fix) {
		insert(fixesInBounds, fixesOutBounds, fix.getId(), fix);
	}

	public synchronized void deleteFix(String fixId) {
		delete(fixesInBounds, fixesOutBounds, fixId);
	}

	public synchronized Fix selectFix(String fixId) {
		return (Fix) select(fixesInBounds, fixesOutBounds, fixId);
	}

	public synchronized Collection selectFixesInBounds() {
		return selectInBounds(fixesInBounds);
	}

	// ****************************
	// ***** Managing Airways *****
	// ****************************

	public synchronized void insertAirway(Airway awy) {
		insert(airwaysInBounds, airwaysOutBounds, awy.getId(), awy);
	}

	public synchronized void deleteAirway(String awyId) {
		delete(airwaysInBounds, airwaysOutBounds, awyId);
	}

	public synchronized Airway selectAirway(String airwayId) {
		return (Airway) select(airwaysInBounds, airwaysOutBounds, airwayId);
	}

	public synchronized Collection selectAirwaysInBounds() {
		return selectInBounds(airwaysInBounds);
	}

	// ****************************
	// ***** Managing Sids *****
	// ****************************

	public synchronized void insertSid(Sid sid) {
		insert(sidsInBounds, sidsOutBounds, sid.getId(), sid);
	}

	public synchronized void deleteSid(String sidId) {
		delete(sidsInBounds, sidsOutBounds, sidId);
	}

	public synchronized Sid selectSid(String sidId) {
		return (Sid) select(sidsInBounds, sidsOutBounds, sidId);
	}

	public synchronized Collection selectSidsInBounds() {
		return selectInBounds(sidsInBounds);
	}

	// ****************************
	// ***** Managing Stars *****
	// ****************************

	public synchronized void insertStar(Star star) {
		insert(starsInBounds, starsOutBounds, star.getId(), star);
	}

	public synchronized void deleteStar(String starId) {
		delete(starsInBounds, starsOutBounds, starId);
	}

	public synchronized Star selectStar(String starId) {
		return (Star) select(starsInBounds, starsOutBounds, starId);
	}

	public synchronized Collection selectStarsInBounds() {
		return selectInBounds(starsInBounds);
	}

	// ***************************
	// ***** Helpers Methods *****
	// ***************************

	private void insert(Map inMap, Map outMap, String id, Object data) {
		inMap.put(id, data);
	}

	private void delete(Map inMap, Map outMap, String id) {
		inMap.remove(id);
		outMap.remove(id);
	}

	private Object select(Map inMap, Map outMap, String id) {
		Object data = inMap.get(id);
		return data != null ? data : outMap.get(id);
	}

	private Collection selectInBounds(Map inBounds) {
		return Collections.unmodifiableCollection(inBounds.values());
	}
}