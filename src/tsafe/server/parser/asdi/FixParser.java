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

import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Point2D;
import tsafe.common_datastructures.PointXY;
import tsafe.server.calculation.Calculator;
import tsafe.server.database.DatabaseInterface;

/**
 * Parse named fixes, lat/lon fixes, and radial distance fixes
 */
class FixParser {

	private static final double METERS_PER_MILE = 1609.2655;

	private static final double RADIANS_PER_DEGREE = Math.PI / 180;

	/**
	 * Lookup a fix by name Return null is cannot find it
	 */
	public static Fix getFixNamed(String fixDescription,
			DatabaseInterface tsafeDB) {
		return tsafeDB.selectFix(fixDescription);
	}

	/**
	 * Parse a lat/lon fix return null is unable to parse
	 */
	public static Fix getFixLatLon(String fixDescription) {

		// Get the index of the '/', return null if there is none
		int slashIdx = fixDescription.indexOf('/');
		if (slashIdx == -1)
			return null;

		// Extract the latitude and longitude strings
		String latString = fixDescription.substring(0, slashIdx);
		String lonString = fixDescription.substring(slashIdx + 1);

		double lat, lon;
		int length, sign;
		char declination;

		/**
		 * Parse the latitude string
		 */
		length = latString.length();
		declination = latString.charAt(latString.length() - 1);

		// northern directions are positive
		if (declination == 'N') {

			sign = 1;
			latString = latString.substring(0, length - 1);
		}
		
		// southern directions are negative
		else if (declination == 'S') {
			sign = -1;
			latString = latString.substring(0, length - 1);
		}
		
		// if latString doesn't end in an 'N' or 'S', 'N' is implied
		else {
			sign = 1;
		}

		// Get the latitude in decimal form
		try {
			lat = sign * getDecimalCoordinate(latString);
		} catch (NumberFormatException e) {
			return null;
		}

		/**
		 * Parse the longitude string
		 */
		length = lonString.length();
		declination = lonString.charAt(lonString.length() - 1);

		// easterly directions are positive
		if (declination == 'E') {
			sign = 1;
			lonString = lonString.substring(0, length - 1);
		}

		// westerly directions are negative
		else if (declination == 'W') {
			sign = -1;
			lonString = lonString.substring(0, length - 1);
		}

		// if lonString doesn't end in an 'E' or 'W', 'W' is implied
		else {
			sign = -1;
		}

		// Get longitude in decimal form
		try {
			lon = sign * getDecimalCoordinate(lonString);
		} catch (NumberFormatException e) {
			return null;
		}

		/**
		 * Return the parsed fix
		 */
		return new Fix(fixDescription, lat, lon);
	}

	private static double getDecimalCoordinate(String degreesMinutes)
			throws NumberFormatException {
		int length = degreesMinutes.length();
		String degreesString = degreesMinutes.substring(0, length - 2);
		String minutesString = degreesMinutes.substring(length - 2, length);
		int degrees = Integer.parseInt(degreesString);
		int minutes = Integer.parseInt(minutesString);
		return degrees + (minutes / 60.0);
	}

	/**
	 * Parse a fix radial distance return null if unable to parse
	 */
	public static Fix getFixRadialDistance(String fixDescription,
			DatabaseInterface tsafeDB, Calculator calc) {

		// Must be at least 8 characters long to be a fix radial disance
		int len = fixDescription.length();
		if (len < 8)
			return null;

		// To extract from the fix description
		Fix relativeFix;
		double radians, meters;

		try {
			// Extract fix from description
			String fixName = fixDescription.substring(0, len - 6);
			relativeFix = getFixNamed(fixName, tsafeDB);
			if (relativeFix == null) {
				return null;
			}
		
			// Extract radial degrees from description
			// Zero on a lat/lon fix is due north. Our zero is due east
			String radialDesc = fixDescription.substring(len - 6, len - 3);
			int degrees = Integer.parseInt(radialDesc) + 90;
			if (degrees >= 360)
				degrees -= 360;
			radians = RADIANS_PER_DEGREE * degrees;

			// Extract distance in miles from description
			String distanceDesc = fixDescription.substring(len - 3);
			meters = METERS_PER_MILE * Integer.parseInt(distanceDesc);
		} catch (IndexOutOfBoundsException e) {
			return null;
		} catch (NumberFormatException e) {
			return null;
		}

		// Get the XY point of the relative fix,
		// and the x, y deviations from that fix
		PointXY fixPoint = calc.toXY(relativeFix);
		double xChange = Math.cos(radians) * meters;
		double yChange = Math.sin(radians) * meters;

		// Calculate the final lat/lon location
		Point2D location = calc.toLL(fixPoint.getX() + xChange, fixPoint.getY()
				+ yChange);
		return new Fix(fixDescription, location);
	}
}