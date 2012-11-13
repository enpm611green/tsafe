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

package tsafe.server.computation;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;

import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.FlightPlan;
import tsafe.common_datastructures.FlightTrack;
import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.Trajectory;
import tsafe.common_datastructures.client_server_communication.ComputationResults;
import tsafe.common_datastructures.client_server_communication.UserParameters;
import tsafe.server.calculation.Calculator;
import tsafe.server.computation.data.RouteTrack;
import tsafe.server.computation.sub_computation.ConformanceMonitor;
import tsafe.server.computation.sub_computation.RouteTracker;
import tsafe.server.computation.sub_computation.TrajectorySynthesizer;

/**
 * Handles the control flow between the algorithmic units of TSAFE
 */
public class ComputationMediator {

	/**
	 * Engine components
	 */
	private RouteTracker routeTracker;

	private ConformanceMonitor confMonitor;

	private TrajectorySynthesizer trajSynth;

	private LatLonBounds bounds;

	private UserParameters parameters;

	/**
	 * Collection of flights over which algorithms calculate
	 */
	private Collection flights = new LinkedList();

	/**
	 * Collections of observers of the engine
	 */
	private Collection observers = new LinkedList();

	/**
	 * Private engine constructor
	 */
	public ComputationMediator(Calculator calculator) {
		this.routeTracker = new RouteTracker(this, calculator);
		this.confMonitor = new ConformanceMonitor(this, calculator);
		this.trajSynth = new TrajectorySynthesizer(this, calculator);
	}

	// *** Setters and getters ***

	/**
	 * @return Returns the bounds.
	 */
	public LatLonBounds getBounds() {
		return bounds;
	}

	/**
	 * @param bounds
	 *            The bounds to set.
	 */
	public void setBounds(LatLonBounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * @return Returns the parameters.
	 */
	public UserParameters getParameters() {
		return parameters;
	}

	/**
	 * @param parameters
	 *            The parameters to set.
	 */
	public void setParameters(UserParameters parameters) {
		this.parameters = parameters;
	}

	/**
	 * Set the flights over which the engine is to compute
	 */
	public void setFlights(Collection flights) {

		this.flights = flights;
	}

	//*********************************************

	// Experiment fields:
	private Set totalBlunders = new HashSet();

	private Set totalFlights = new HashSet();

	private boolean changed = true;

	/***************************************************************************
	 * Runs the TSAFE Engine Performs Conformance Monitoring and Trajectory
	 * Synthesis
	 * 
	 * @return
	 **************************************************************************/
	public ComputationResults computeFlights(LatLonBounds bounds,
			UserParameters parameters) {

		this.bounds = bounds;
		this.parameters = parameters;

		// Instantiate an empty collection of blunders,
		// an empty flight2traj map, and a copy of the current flights
		Collection blunders = new LinkedList();
		Map flight2TrajMap = new HashMap();

		// For each flight:
		// 1) If it has no flight plan, assign it a dr traj and continue
		// 2) If it has a flight plan, determine if it is blundering
		// 3) If it is, assign its dr trajectory as its predicted trajectory
		//    If it isn't, assign its route trajectory as its predicted trajectory
		Iterator flightIter = flights.iterator(); 

		while (flightIter.hasNext()) {
			Flight flight = (Flight) flightIter.next();
			FlightTrack ft = flight.getFlightTrack();
			FlightPlan fp = flight.getFlightPlan();

			// If the flight doesn't have a flight plan, assign it a dr
			// trajectory
			// Don't check its conformance, just continue to the next flight
			if (fp == null) {
				Trajectory drTraj = trajSynth.getDeadReckoningTrajectory(ft);
				flight2TrajMap.put(flight, drTraj);
				continue;
			}

			// Determine if flight is blundering by comparing its actual track
			// to its route track
			RouteTrack rt = routeTracker.findRouteTrack(ft, fp);

			boolean blundering = confMonitor.isBlundering(ft, rt);

			// If the flight is bludering, add it to the set of blunders
			// and assign a dead reckoning trajectory as its assigned trajectory
			if (blundering) {
				blunders.add(flight);

				Trajectory drTraj = trajSynth.getDeadReckoningTrajectory(ft);
				flight2TrajMap.put(flight, drTraj);
			}

			// If the flight is conforming, synthesize a route trajectory for
			// it,
			// assuming it's current track is its route track
			else {
				Trajectory rtTraj = trajSynth.getRouteTrajectory(rt, fp
						.getRoute());
				flight2TrajMap.put(flight, rtTraj);
			}
		}


		// Notify the observers of the results
		ComputationResults results = new ComputationResults(flights, blunders,
				flight2TrajMap);

		return results;
	}
}