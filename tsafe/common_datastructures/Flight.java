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
 * A flight: an id, a flight track, and a flight plan
 */
public class Flight {

    // Flight id assumes flights have unique ids
    private String aircraftId;
    private FlightTrack track;
    private FlightPlan plan;

    /**
     * Constructs a flight with no flight plan
     *
     * @throws NullPointerException is aircraftId is null
     */
    public Flight(String aircraftId, FlightTrack track) {
        this(aircraftId, track, null);
    }

    /**
     * Constructs a flight with no flight track
     *
     * @throws NullPointerException is aircraftId is null
     */
    public Flight(String aircraftId, FlightPlan plan) {
        this(aircraftId, null, plan);
    }
    
    /**
     * Constructs a flight with a flight track and a flight plan
     *
     * @throws NullPointerException is aircraftId is null
     */
    public Flight(String aircraftId, FlightTrack track, FlightPlan plan) {
        if (aircraftId == null) throw new NullPointerException("aircraftId is null");
        this.aircraftId = aircraftId;
        this.track = track;
        this.plan = plan;
    }

    /**
     * Constructs a flight that is a copy of another
     *
     * @throws NullPointerException if f is null
     */
    public Flight(Flight f) {
        this(f.getAircraftId(), f.getFlightTrack(), f.getFlightPlan());
    }

    /** Returns the aircraft id */
    public String getAircraftId() {
        return aircraftId;
    }

    /**
     * Returns the flight track.
     * Returns null if the flight has no track
     */
    public FlightTrack getFlightTrack() {
        return track;
    }

    /**
     * Returns the flight plan.
     * Returns null if the flight has no plan
     */
    public FlightPlan getFlightPlan() {
        return plan;
    }

    /**
     * Sets the flight track
     */
    public void setFlightTrack(FlightTrack track) {
        this.track = track;
    }

    /**
     * Sets the flight plan
     */
    public void setFlightPlan(FlightPlan plan) {
        this.plan = plan;
    }

    /**
     * Returns a String representation of the flight
     */
    public String toString() {
        return aircraftId;
    }
    
    /**
     * Returns a hash code for the flight
     */
    public int hashCode() {
        return aircraftId.hashCode();
    }

    /**
     * Returns true if o is a Flight and has the same aircraft id as this
     */
    public boolean equals(Object o) {
        return o != null           &&
               o instanceof Flight &&
               ((Flight)o).aircraftId.equals(this.aircraftId);
    }
}
