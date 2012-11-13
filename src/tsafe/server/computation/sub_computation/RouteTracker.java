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
import tsafe.common_datastructures.FlightPlan;
import tsafe.common_datastructures.FlightTrack;
import tsafe.common_datastructures.Point2D;
import tsafe.common_datastructures.PointXY;
import tsafe.server.calculation.Calculator;
import tsafe.server.computation.ComputationMediator;
import tsafe.server.computation.data.RouteTrack;

/**
 * Finds a flights position along its route
 */
public class RouteTracker extends ComputationColleagues {

	/** Calculator for distances and lat/long to x,y conversion */
	private Calculator calculator;

	/** Sole Constructor */
	public RouteTracker(ComputationMediator mediator, Calculator calculator) {
		super(mediator);
		this.calculator = calculator;
	}

	/**
	 * Finds the point along the route that is closest to the flight track We
	 * assume that every route has at least 2 fixes.
	 */
	public RouteTrack findRouteTrack(FlightTrack ft, FlightPlan fp) {
		Point2D flightPoint = new Point2D(ft.getLatitude(), ft.getLongitude());

		
		// We will find the route position on each route segment
		// and take the route position that is closest to the flight
		Iterator fixIter = fp.getRoute().fixIterator();

		// If route does not have any fixess
		// return flights current position
		if (!fixIter.hasNext()) {
			return new RouteTrack(null, null, ft);
		}
		Fix prevFix = (Fix) fixIter.next();

		// If route does not has exactly one fix,
		// make that fix the prev and next
		if (!fixIter.hasNext()) {
			return new RouteTrack(prevFix, prevFix, ft);
		}

		// Minimums to keep track of
		double minDeviation = Double.MAX_VALUE;
		Fix minPrevFix = null, minNextFix = null;
		Point2D minSnapPoint = null;

		while (fixIter.hasNext()) {
			Fix nextFix = (Fix) fixIter.next();
			Point2D snapPoint = snapPointToRouteSegment(flightPoint, prevFix,
					nextFix, false);
			double snapDistance = calculator.distanceLL(snapPoint, flightPoint,
					this.mediator.getBounds());

			// found a new minimum
			if (snapDistance < minDeviation) {
				minPrevFix = prevFix;
				minNextFix = nextFix;
				minSnapPoint = snapPoint;
				minDeviation = snapDistance;
			}

			prevFix = nextFix;
		}

		PointXY flightXY = calculator.toXY(flightPoint, this.mediator
				.getBounds());
		PointXY prevXY = calculator.toXY(minPrevFix, this.mediator.getBounds());
		PointXY nextXY = calculator.toXY(minNextFix, this.mediator.getBounds());

		return new RouteTrack(minPrevFix, minNextFix, minSnapPoint
				.getLatitude(), minSnapPoint.getLongitude(), fp
				.getAssignedAltitude(), ft.getTime(), fp.getAssignedSpeed(),
				calculator.angleLL(minPrevFix, minNextFix, this.mediator
						.getBounds()));
	}

	/**
	 * Finds closest point on straight line between l1 and l2 to p
	 */
	private Point2D snapPointToRouteSegment(Point2D latLonPoint, Point2D fix1,
			Point2D fix2, boolean print) {
		PointXY p = calculator.toXY(latLonPoint, this.mediator.getBounds());
		PointXY l1 = calculator.toXY(fix1, this.mediator.getBounds());
		PointXY l2 = calculator.toXY(fix2, this.mediator.getBounds());

		// Find linear equation for line segment (y = mx + b)
		double m = (l2.getY() - l1.getY()) / (l2.getX() - l1.getX());
		double b = l1.getY() - (m * l1.getX());

		// Find linear equation for perpendicular line
		double mP = -1 / m;
		double bP = p.getY() - (mP * p.getX());

		// Find where line segment and perpendicular intersect
		double x = (b - bP) / (mP - m);
		double y = (m * x) + b;
		PointXY intersectXY = new PointXY(x, y);

		// Check if this point does indeed lie on the line segment.
		// If so, it must be the closest point on the line segment to p
		if (((l1.getX() < x && x < l2.getX()) || (l2.getX() < x && x < l1
				.getX()))
				&& ((l1.getY() < y && y < l2.getY()) || (l2.getY() < y && y < l1
						.getY()))) {
			return calculator.toLL(intersectXY, this.mediator.getBounds());
		}

		// If this intersection point does not lie on the line segment,
		// the closest point on the line segment to p must be an end point
		// Find the minimum distance to an end point
		double dist1 = calculator.distanceXY(p, l1);
		double dist2 = calculator.distanceXY(p, l2);
		PointXY minXY = dist1 < dist2 ? l1 : l2;
		return calculator.toLL(minXY, this.mediator.getBounds());
	}
}