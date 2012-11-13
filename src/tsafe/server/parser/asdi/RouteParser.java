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

package tsafe.server.parser.asdi;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import tsafe.common_datastructures.Airway;
import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Route;
import tsafe.common_datastructures.Sid;
import tsafe.common_datastructures.Star;
import tsafe.server.calculation.Calculator;
import tsafe.server.database.DatabaseInterface;

/**
 * Parses routes
 */
class RouteParser {

	private static final String NEW_LINE = System.getProperty("line.separator");

	private static final double METERS_PER_MILE = 1609.2655;

	private static final double RADIANS_PER_DEGREE = Math.PI / 180;

	public static Route parseRoute(String routeDescription,
			DatabaseInterface tsafeDB, Calculator calc) {

		Route route = new Route();
		List routeElements = new ArrayList(routeDescription.length() / 4 /* approximation */);

		// Segment route description into route elements
		StringTokenizer st = new StringTokenizer(routeDescription, ".*");
		while (st.hasMoreTokens()) {
			routeElements.add(st.nextToken());
		}

		// Iterate through all the route elements
		for (int i = 0; i < routeElements.size(); i++) {

			String routeElem = (String) routeElements.get(i);

			// If it is the tailoring indicator '/', skip it
			if (routeElem.length() == 1) {
				continue;
			}

			// Is it an adapted fix?
			boolean isFirstOrLast = (i == 0) || (i == routeElements.size() - 1);
			Fix adaptedFix = parseAdaptedFix(routeElem, isFirstOrLast, tsafeDB,
					calc);
			if (adaptedFix != null) {
				route.addFix(adaptedFix);
				continue;
			}

			// Is this an airway?
			Airway airway = tsafeDB.selectAirway(routeElem);
			if (airway != null) {
				// Going to find the preceeding and succeeding fixes
				// Preceding fix = fix before the airway in the route
				// Succeding fix = fix after the airway in the route

				// If the route is empty, i make the preceding the fix, the
				// first fix in the airway
				// this is a rare case that occurs only when the parser failed
				// on the preceding fixes
				// if you have a better solution, let me know
				Fix precFix = route.isEmpty() ? airway.firstFix() : route
						.lastFix();
				Fix succFix = getSuccFix(routeElements, i, tsafeDB, calc);

				// Airway isn't followed by a known fix
				if (succFix == null) {
					// See if it's followed by a known airway
					String succRouteElem = (String) routeElements.get(i + 1);
					Airway succAirway = tsafeDB.selectAirway(succRouteElem);
					if (succAirway == null) {
						return route;
					}

					// If found succeeding airway, take cross fix as succ fix
					succFix = getCrossAirwaysFix(airway, succAirway, calc);

				}
				// If a succeeding fix was found, must advance counter, so it is
				// not parsed twice
				else {
					i++;
				}

				// Add all the airway points between precFix and succFix to the
				// route
				addAirwayPoints(airway, precFix, succFix, route, calc);

				// Add the succeeding point and advance the counter;
				route.addFix(succFix);
				continue;
			}

			// If this is the second or second to last element,
			// Check if this a sis/star route. If so, skip it.
			if (isSidStar(routeElem)) {
				Sid sid = tsafeDB.selectSid(routeElem);
				if (sid != null) {
					Fix succFix = getSuccFix(routeElements, i, tsafeDB, calc);
					if (succFix == null) {
						continue;
					}

					Route sidRoute = sid.routeTo(succFix);
					if (sidRoute == null) {
						//System.out.println("Couldn't find route in sid " +
						// sid.getId() + " to " + succFix);
						continue;
					}

					addRouteToRoute(sidRoute, route, true);
					i++;// so the succ fix isn't parsed again
					continue;
				}

				Star star = tsafeDB.selectStar(routeElem);
				if (star != null) {
					// In the rare case that a star is the first element
					// we are able to parse, just skip it.
					if (route.isEmpty()) {
						continue;
					}

					Fix precFix = route.lastFix();
					Route starRoute = star.routeFrom(precFix);
					if (starRoute == null) {
						continue;
					}

					addRouteToRoute(starRoute, route, false);
					continue;
				}

				//System.out.println("Couldn't find sid/star " + routeElem);
				continue;
			}

			// If it is a navaid of the form aaaddd or aaadddR, skip it.
			if ((routeElem.length() == 6
					&& Character.isDigit(routeElem.charAt(3))
					&& Character.isDigit(routeElem.charAt(4)) && Character
					.isDigit(routeElem.charAt(5)))
					|| (routeElem.length() == 7
							&& Character.isDigit(routeElem.charAt(3))
							&& Character.isDigit(routeElem.charAt(4))
							&& Character.isDigit(routeElem.charAt(5)) && routeElem
							.charAt(6) == 'R')) {
				continue;
			}

			// If it's one of these wierd ones that ends in trans, skip it
			if (routeElem.endsWith("TRANS") || routeElem.endsWith("TRAN")
					|| routeElem.endsWith("TRNS") || routeElem.endsWith("TRN")) {
				continue;
			}

			// Is this the infamous unidentified fix? The skip it.
			if (routeElem.equals("XXX")) {
				continue;
			}
		}

		// Check for any duplicate route points
		//Fix duplicate = (Fix)findDuplicate(route.fixList());
		//if (duplicate != null) {
		return route;
	}

	private static Fix getSuccFix(List routeElements, int currentIndex,
			DatabaseInterface tsafeDB, Calculator calc) {
		String succRouteElem = (String) routeElements.get(currentIndex + 1);
		boolean isSuccLast = (currentIndex + 1) == (routeElements.size() - 1);
		return parseAdaptedFix(succRouteElem, isSuccLast, tsafeDB, calc);
	}

	/**
	 * Check if the element is an adapted fix, or an adapted fix appended with a
	 * time
	 */
	private static Fix parseAdaptedFix(String routeElem, boolean isFirstOrLast,
			DatabaseInterface tsafeDB, Calculator calc) {
		// Find a '/' character in the route element
		int slashIdx = routeElem.lastIndexOf('/');

		// Is there a slash?
		// If so, it could be a lat/lon fix or have a time or delay appended to
		// it
		if (slashIdx != -1) {
			// Is it a lat/lon fix?
			Fix fixLatLon = FixParser.getFixLatLon(routeElem);
			if (fixLatLon != null) {
				return fixLatLon;
			}

			// Not a lat/lon fix. Must have a time appended to it, so cut it off
			routeElem = routeElem.substring(0, slashIdx);
			slashIdx = routeElem.indexOf('/');
		}

		if (slashIdx == -1) {
			// If this is the first or last route element,
			// check if it is an iata airport first, then check if it is a fix
			// or navaid
			// Otherwise check if it is a fix or navaid first, airport second
			if (isFirstOrLast) {
				// Is it an airport?
				Fix fixAirport = FixParser
						.getFixNamed('K' + routeElem, tsafeDB);
				if (fixAirport != null)
					return fixAirport;

				// Is it a named fix?
				Fix fixNamed = FixParser.getFixNamed(routeElem, tsafeDB);
				if (fixNamed != null)
					return fixNamed;
			} else {
				// Is it a named fix?
				Fix fixNamed = FixParser.getFixNamed(routeElem, tsafeDB);
				if (fixNamed != null)
					return fixNamed;

				// Is it an airport?
				Fix fixAirport = FixParser
						.getFixNamed('K' + routeElem, tsafeDB);
				if (fixAirport != null)
					return fixAirport;
			}

			// Is it a fix radial disance?
			Fix fixRadialDistance = FixParser.getFixRadialDistance(routeElem,
					tsafeDB, calc);
			if (fixRadialDistance != null)
				return fixRadialDistance;
		} else {
			// Is it a lat/lon fix
			Fix fixLatLon = FixParser.getFixLatLon(routeElem);
			if (fixLatLon != null)
				return fixLatLon;
		}

		// Must not be a fix
		return null;
	}

	private static void addAirwayPoints(Airway airway, Fix precFix,
			Fix succFix, Route route, Calculator calc) {
		List fixList = airway.fixList();

		// Find the "on fix" and the "off fix",
		// the fixes at which the flight will get on and off the airway,
		// respectively
		Fix onFix, offFix;

		// If the prec fix is on the airway, make it the on fix
		if (fixList.contains(precFix)) {
			onFix = precFix;
		}
		// If it isnt, find the closest fix on the airway to the prec fix
		// then add it to the route and make it the on fix
		else {
			onFix = getClosestAirwayFix(precFix, airway, calc);

			// If the on fix found is the same as the succ fix, return
			if (onFix.equals(succFix)) {
				return;
			}
			// If it isn't, add the new onFix to the route
			else {
				route.addFix(onFix);
			}
		}

		// If the succ fix is on the airway, make it the off fix
		if (fixList.contains(succFix)) {
			offFix = succFix;
		}
		// If it isnt, find the closest fix on the airway to the succ fix
		// and make it the off fix and add it to the route later
		else {
			offFix = getClosestAirwayFix(succFix, airway, calc);

			// If the off fix found is the same as the on fix, return
			if (offFix.equals(onFix)) {
				return;
			}
		}

		// Get the indices of the on and off fixes
		int onIdx = fixList.indexOf(onFix);
		int offIdx = fixList.indexOf(offFix);

		// Add all the points between the indices to the route
		try {
			addFixSublistToRoute(fixList, onIdx, offIdx, route);
		} catch (NoSuchElementException e) {
			System.out.println("Fix list: " + fixList);
			System.out.println("On idx: " + onIdx);
			System.out.println("On fix: " + onFix);
			System.out.println("Off idx: " + offIdx);
			System.out.println("Off fix: " + offFix);
			System.out.println("Route: " + route);
			throw e;
		}

		// If the off fix is not the succ fix, add the off fix
		if (!offFix.equals(succFix)) {
			route.addFix(offFix);
		}
	}

	// Adds all the fixes from onIdx to offIdx, exclusive at both ends
	private static void addFixSublistToRoute(List fixList, int onIdx,
			int offIdx, Route route) {
		// If the on and off indices are the same, just return
		if (onIdx == offIdx)
			return;

		// else if we're following the list forward
		else if (onIdx < offIdx) {
			ListIterator fixIter = fixList.listIterator(onIdx + 1);
			Fix fix = (Fix) fixIter.next();

			for (int i = onIdx + 1; i < offIdx; i++) {
				route.addFix(fix);
				fix = (Fix) fixIter.next();
			}
		}

		// else if we're following the list backwards
		else {
			ListIterator fixIter = fixList.listIterator(onIdx);
			Fix fix = (Fix) fixIter.previous();

			for (int i = onIdx - 1; i > offIdx; i--) {
				route.addFix(fix);
				fix = (Fix) fixIter.previous();
			}
		}
	}

	private static void addRouteToRoute(Route from, Route to, boolean addFirst) {
		Iterator fixIter = from.fixIterator();

		if (fixIter.hasNext()) {
			Fix first = (Fix) fixIter.next();
			if (addFirst)
				to.addFix(first);
		}

		while (fixIter.hasNext()) {
			to.addFix((Fix) fixIter.next());
		}
	}

	/**
	 * Returns the closest fix on the airway to the given fix
	 */
	private static Fix getClosestAirwayFix(Fix fix, Airway airway,
			Calculator calc) {
		
		Iterator fixIter = airway.fixIterator();
		double minDistance = Double.MAX_VALUE;
		Fix closestFix = null;

		while (fixIter.hasNext()) {
			Fix airwayFix = (Fix) fixIter.next();
			double distance = calc.distanceLL(fix, airwayFix);
			if (distance < minDistance) {
				closestFix = airwayFix;
				minDistance = distance;
			}
		}

		return closestFix;
	}

	/**
	 * Returns the closest fix on a1 to a2
	 */
	private static Fix getCrossAirwaysFix(Airway a1, Airway a2, Calculator calc) {

		Iterator fixIter1 = a1.fixIterator();
		double minDistance = Double.MAX_VALUE;
		Fix closestFix = null;

		while (fixIter1.hasNext()) {
			Fix a1Fix = (Fix) fixIter1.next();
			Iterator fixIter2 = a2.fixIterator();

			while (fixIter2.hasNext()) {
				Fix a2Fix = (Fix) fixIter2.next();
				// If they share a fix, just return it
				if (a1Fix.equals(a2Fix))
					return a1Fix;

				// Otherwise, continue to find the closest cross fix
				double distance = calc.distanceLL(a1Fix, a2Fix);
				if (distance < minDistance) {
					closestFix = a1Fix;
					minDistance = distance;
				}
			}
		}

		return closestFix;
	}

	/**
	 * Returns true is this is a sid/star route; false otherwise
	 */
	private static boolean isSidStar(String routeElem) {
		// Sid/star must be at least 3 characters long
		if (routeElem.length() < 3)
			return false;

		// Last character must be a digit
		char lastChar = routeElem.charAt(routeElem.length() - 1);
		if (!Character.isDigit(lastChar))
			return false;

		// Every character but the last must NOT be a digit
		for (int i = 0; i < routeElem.length() - 1; i++) {
			char currChar = routeElem.charAt(i);
			if (Character.isDigit(currChar))
				return false;
		}

		return true;
	}

}