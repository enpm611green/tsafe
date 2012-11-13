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
 * A flight's aircraft data and intent information: assigned speed, assigned altitude, and flight route
 * This is an immutable datatype
 */
public class FlightPlan {

    /**
     * Assigned speed and assigned altitude
     */
    private double speed, altitude;

    /**
     * Flight route
     */
    private Route route;

    /**
     * Construct a flight plan
     *
     *@throws NullPointerException if route is null
     */
    public FlightPlan(double speed, double altitude, Route route) {
        if (route == null) throw new NullPointerException("route is null");
        this.speed = speed;
        this.altitude = altitude;
        this.route = new Route(route);
    }

    /** Return the assigned speed */
    public double getAssignedSpeed() {
        return this.speed;
    }

    /** Return the assigned altitude */
    public double getAssignedAltitude() {
        return this.altitude;
    }
     
    /** Return a copy of the flight route */
    public Route getRoute() {
        return new Route(this.route);
    }

    /** Returns a flight plan with the amended speed */
    public FlightPlan amendAssignedSpeed(double newSpeed) {
        return new FlightPlan(newSpeed, this.altitude, this.route);
    }

    /** Returns a flight plan with the amended altitude */
    public FlightPlan amendAssignedAltitude(double newAltitude) {
        return new FlightPlan(this.speed, newAltitude, this.route);
    }

    /** Returns a flight plan with the amended route */
    public FlightPlan amendRoute(Route newRoute) {
        return new FlightPlan(this.speed, this.altitude,  new Route(newRoute));
    }
}

