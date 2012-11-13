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

package tsafe.server.computation.sub_computation;

import java.util.Iterator;

import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.FlightTrack;
import tsafe.common_datastructures.Point2D;
import tsafe.common_datastructures.Point4D;
import tsafe.common_datastructures.PointXY;
import tsafe.common_datastructures.Route;
import tsafe.common_datastructures.Trajectory;
import tsafe.server.calculation.Calculator;
import tsafe.server.computation.ComputationMediator;
import tsafe.server.computation.data.RouteTrack;

/**
 * Computes nominal and dead reckoning trajectories of flights
 */
public class TrajectorySynthesizer extends ComputationColleagues {

	/**
	 * Calculator for distances and lat/long to x,y conversion
	 */
	private Calculator calculator;

	/** Sole constructor */
	public TrajectorySynthesizer(ComputationMediator mediator,
			Calculator calculator) {
		super(mediator);
		this.calculator = calculator;
	}

	/**
	 * @return Dead reckoning trajectory of flight: <br>
	 *         Assume flight stays "on course" with its current heading and
	 *         speed
	 */
	public Trajectory getDeadReckoningTrajectory(FlightTrack ft) {
		Point4D start = ft.asPoint4D();
		Point4D end = deadReckon(ft,
				this.mediator.getParameters().tsTimeHorizon);

		Trajectory drTraj = new Trajectory();
		drTraj.addPoint(start);
		drTraj.addPoint(end);
		return drTraj;
	}

	/**
	 * Dead reckons from the flight track for a given amount of time Returns the
	 * end point of this dead reckoning.
	 */
	private Point4D deadReckon(FlightTrack ft, long time) {
		double distance = ft.getSpeed() * time;
		double xChange = Math.cos(ft.getHeading()) * distance;
		double yChange = Math.sin(ft.getHeading()) * distance;

		PointXY startXY = calculator.toXY(ft.getLatitude(), ft.getLongitude(),
				this.mediator.getBounds());
		Point2D end = calculator.toLL(startXY.getX() + xChange, startXY.getY()
				+ yChange, this.mediator.getBounds());
		return new Point4D(end.getLatitude(), end.getLongitude(), ft
				.getAltitude(), ft.getTime() + time);
	}

	/**
	 * @return Route trajectory of flight: <br>
	 *         Assumes flight adheres strictly to its route
	 */
	public Trajectory getRouteTrajectory(RouteTrack rt, Route r) {
		// Start trajectory at route track point
		Trajectory routeTraj = new Trajectory();
		Point4D currPoint = rt.asPoint4D();
		routeTraj.addPoint(currPoint);

		// Move cursor to the next fix in the route
		Iterator fixIter = r.fixIterator();
		if (!fixIter.hasNext()) {
			System.out.println("Trajectory Synthesizer: ROUTE HAS NO POINTS.");
			return getDeadReckoningTrajectory(rt);
		}
		Fix nextFix = rt.getNextFix();
		Fix f = (Fix) fixIter.next();
		while (!f.equals(nextFix)) {
			f = (Fix) fixIter.next();
		}

		// Find the distance and time to the next fix
		long timeElapsed = 0;
		double dist = calculator.distanceLL(rt.getLatitude(),
				rt.getLongitude(), nextFix, this.mediator.getBounds());
		long timeToNextFix = (long) (dist / rt.getSpeed());

		// If the time to the next fix is within the time horizon,
		// add this next fix location and expected time to the trajectory
		if (timeToNextFix < this.mediator.getParameters().tsTimeHorizon) {
			currPoint = new Point4D(nextFix.getLatitude(), nextFix
					.getLongitude(), rt.getAltitude(), rt.getTime()
					+ timeToNextFix);
			routeTraj.addPoint(currPoint);
			timeElapsed = timeToNextFix;

			// While there are more fixes on the route,
			// try to add them to the trajectory as well.
			while (fixIter.hasNext()) {
				nextFix = (Fix) fixIter.next();
				dist = calculator.distanceLL(currPoint.getLatitude(), currPoint
						.getLongitude(), nextFix, this.mediator.getBounds());
				timeToNextFix = (long) (dist / rt.getSpeed());

				// If there is not enough time to reach the next fix,
				// break out of the loop
				if (timeToNextFix > this.mediator.getParameters().tsTimeHorizon
						- timeElapsed)
					break;

				currPoint = new Point4D(nextFix.getLatitude(), nextFix
						.getLongitude(), rt.getAltitude(), rt.getTime()
						+ timeElapsed + timeToNextFix);
				routeTraj.addPoint(currPoint);
				timeElapsed += timeToNextFix;
			}
		}

		// If the loop was exited because there was not enough time to reach the
		// next fix,
		// and not because the route had actually ended, then dead reckon for
		// the remaining time.
		if (fixIter.hasNext()) {
			double heading = calculator.angleLL(currPoint.getLatitude(),
					currPoint.getLongitude(), nextFix, this.mediator
							.getBounds());
			FlightTrack lastTrack = new FlightTrack(currPoint.getLatitude(),
					currPoint.getLongitude(), rt.getAltitude(), currPoint
							.getTime(), rt.getSpeed(), heading);
			Point4D end = deadReckon(lastTrack,
					this.mediator.getParameters().tsTimeHorizon - timeElapsed);
			routeTraj.addPoint(end);
		}

		return routeTraj;
	}
}