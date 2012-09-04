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

import java.util.Calendar;

import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Route;
import tsafe.server.calculation.Calculator;
import tsafe.server.database.DatabaseInterface;

/**
 * Includes static methods for unpacking NAS messages.
 */
class NASFields {

    // Conversion constants
    private static final int MILLISECONDS_PER_HOUR = 60 * 60 * 1000;
    private static final double METERS_PER_NAUTICAL_MILE = 1852.0;
    private static final double FEET_PER_METER = 3.2808;
    private static final double KNOTS_PER_MACH = 644.62216;

    // FIELD 02 - Flight Identification

    /**
     * @return given the flight ID of a flight, this function returns the corresponding aircraft ID.
     */
    public static String getAircraftId(String flightId) {
        int slashPos = flightId.indexOf("/");
        if (slashPos == -1) return flightId;
        else return flightId.substring(0,slashPos);
    }

    /**
     * @return given the flight ID of a flight, this function returns the corresponding computer ID,
     *  if this ID is provided. Otherwise, it returns <code> null </code>
     */
    public static String getComputerId(String flightId) {
        int slashPos = flightId.indexOf("/");
        if (slashPos == -1) return null;
        else return flightId.substring(slashPos + 1);
    }

    // FIELD 03 - Aircraft Data

    /**
     * @return given the aircraft data, this function returns the number of aircraft, if this number is provided.
     */
    public static String getAircraftNumber(String aircraftData) {
        if ((aircraftData.charAt(0)=='H') || (Character.isDigit(aircraftData.charAt(0)))) {
            // if the aircraft number is provided
            int slashPos=aircraftData.indexOf("/");
            return aircraftData.substring(0,slashPos);
        }
        else return null;
    }

    /**
     * @return given the aircraft data, this function returns the type of aircraft
     */
    public static String getAircraftType(String aircraftData) {
        if ((aircraftData.charAt(0)=='H') || (Character.isDigit(aircraftData.charAt(0)))) {
            // if the aircraft number is provided
            int slashPos1 = aircraftData.indexOf("/");
            int slashPos2 = aircraftData.indexOf("/", 1 + slashPos1);
            return aircraftData.substring(1 + slashPos1, slashPos2);
        }
        else {
            int slashPos = aircraftData.indexOf("/");
            return aircraftData.substring(0,slashPos);
        }
    }

    /**
     * @return given the aircraft data, this function returns the airborne equipment qualifier, if this information is provided. Otherwise, it returns the character "-".
     */
    public static char getAirborneEquipment(String aircraftData) {
        if (aircraftData.charAt(aircraftData.length() - 2)=='/')
                // if the airborne equipment qualifier is provided
                return aircraftData.charAt(aircraftData.length()-1);
        else return '-';
    }
    
    /**
     * FIELD 05 - Ground Speed
     * Return value in meters / millisecond
     *
     * Speed may be presented in one of the following formats:
     *   a. dd(d)(d) - true air speed in Knots
     *   b. Mddd - mach speed
     */
    public static double getGroundSpeed(String speedDescription) {
    			  
        double knots;

        try {
            // if it's a mach speed, case b
            if (speedDescription.charAt(0) == 'M') {    	
         
            	double machFraction = Integer.parseInt(speedDescription.substring(1)) / 100.0;
                knots = (double)KNOTS_PER_MACH * machFraction;
            }
            // else if it's case case a
            else {
                knots = Integer.parseInt(speedDescription);
            }
        }
        catch (NumberFormatException e) {
            knots = 0;
        }

        return METERS_PER_NAUTICAL_MILE * knots /*(nautical miles per hour)*/ / MILLISECONDS_PER_HOUR;
    }
    
    /**
     * FIELD 06 - Coordination Fix
     * Returns the coordinates of the given coordination fix.
     * A coordination fix is always a lat/lon fix.
     *
     * @returns if there is a fix that corresponds to the given name, then
     *          the method returns the coordinates of that fix; otherwise, it
     *          returns <code>null</code>.
     */
    public static Fix getCoordinationFix (String fixDescription) {
        return FixParser.getFixLatLon(fixDescription);
    }
    
    /**
     * FIELD 07 - Coordination Time
     * Return value in milliseconds
     */    
    public static long getCoordinationTime(int year, int month, int date, String timeDescription) {
        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(year, month, date);
        String hourDesc = timeDescription.substring(1);
        int hour = Integer.parseInt(hourDesc);
        cal.set(Calendar.HOUR_OF_DAY, hour);
        return cal.getTime().getTime();
    }

    /**
     * FIELD 08 - Assigned Altitude
     * Return value in Meters
     *
     * Altitude may be presented in one of the following formats:
     *  a. ddd - altitude
     *  b. OTP/ddd - VFR-on-top plus an altitude
     *  c. dddT - Interim altitude
     *  d. dddBddd - a block of altitudes
     *  e. dddC - Reported Mode C altitudes when it is not within
     *            Altitude Conformance Limit (ALCT) feet of the assigned altitude
     */
     public static double getAltitude(String altitudeDescription) {
         double footHundredths;

		
         try {
             switch (altitudeDescription.length()) {
                 case 2: // this has come up before
                 case 3: // in the case of a
                     footHundredths = Integer.parseInt(altitudeDescription);
                     break;
                 case 4: // in the case of c or e
                     footHundredths = Integer.parseInt(altitudeDescription.substring(0, 3));
                     break;
                 case 7: // in the case of b or d
                         // in the case of b
                     if (altitudeDescription.charAt(3) == '/') {
                         footHundredths = Integer.parseInt(altitudeDescription.substring(4, 7));
                     }
                     // in the case of d
                     else {
                         int block1 = Integer.parseInt(altitudeDescription.substring(0, 3));
                         int block2 = Integer.parseInt(altitudeDescription.substring(4, 7)); 
                     
                         // for lack of a better solution, i take the average
						 footHundredths = (double)(block1 + block2) / 2.0;			 
                     }
                     break;
                 default:
                     footHundredths = 0;
                     break;
             }
         } catch (NumberFormatException e) {
             footHundredths = 0;
         }
                      
         return footHundredths * 100.0 / FEET_PER_METER;
    }

    /**
     * FIELD 10 - Route Data
     * Returns the route returned by the route parser
     */
    public static Route getRoute(String routeDescription, DatabaseInterface tsafeDB, Calculator calc) {
        return RouteParser.parseRoute(routeDescription, tsafeDB, calc);
    }
    

    // FIELD 23 - Track Position Components

    /**
     * @return given a latitude/longitude coordinate, it returns the corresponding latitude. For convenience, we express latitutes in minutes. Also, a northern latitude is positive and a southern latitude is negative.
     */
    public static double getLatitude(String position)
    {
        int degrees = Integer.parseInt(position.substring(0,2));
        int minutes = Integer.parseInt(position.substring(2,4));
        double latitude = degrees + (minutes / 60.0);

        // south latitudes are represented as negative numbers
        return (position.charAt(4) == 'N') ? latitude : -latitude;
    }

    /**
     * @return given a latitude/longitude coordinate, it returns the corresponding longitude. For convenience, we express longitudes in minutes. Also, an eastern longitude is positive and a western longitude is negative.
     */
    public static double getLongitude(String position) {
        int degrees = Integer.parseInt(position.substring(6,9));
        int minutes = Integer.parseInt(position.substring(9,11));
        double longitude = degrees + (minutes / 60.0);

        // west latitudes are represented as negative numbers
		return (position.charAt(11) == 'E') ? longitude : -longitude;
    }
}
