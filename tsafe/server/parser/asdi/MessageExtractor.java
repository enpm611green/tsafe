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
import java.util.Date;

import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.FlightPlan;
import tsafe.common_datastructures.FlightTrack;
import tsafe.common_datastructures.Route;
import tsafe.server.calculation.Calculator;
import tsafe.server.database.DatabaseInterface;

/**
 * Parses the ASDI messages
 */
class MessageExtractor {

	/**
	 * The database the message parser updates
	 */
	private DatabaseInterface tsafeDB;

	/**
	 * Used by the message parser for calculations
	 */
	private Calculator calc;

	/**
	 * Constuct a message parser that updates the given TSAFE database and uses
	 * the given heading calculator
	 */
	public MessageExtractor(DatabaseInterface tsafeDB, Calculator calc) {
		this.tsafeDB = tsafeDB;
		this.calc = calc;
	}

	/**
	 * This method realizes the messages update on the DatabaseInterface
	 */
	public void extractMessage(Message message) {
		String messageType = message.getType();
		// Flight track message
		if (messageType.equals("TZ")) {

			String aircraftId = NASFields.getAircraftId(message
					.getField(Message.FLIGHT_ID));

			double latitude = NASFields.getLatitude(message
					.getField(Message.TRACK_POSITION));
			double longitude = NASFields.getLongitude(message
					.getField(Message.TRACK_POSITION));
			double altitude = NASFields.getAltitude(message
					.getField(Message.ASSIGNED_ALTITUDE));
			double speed = NASFields.getGroundSpeed(message
					.getField(Message.SPEED));

			//try to select flight from the database
			Flight f = tsafeDB.selectFlight(aircraftId);

			// If flight not in the database, insert it
			if (f == null) {
				//** We say the heading is zero when we receive the first TZ/UZ
				// message.
				FlightTrack track = new FlightTrack(latitude, longitude,
						altitude, message.getTime(), speed, 0);
				tsafeDB.insertFlight(new Flight(aircraftId, track));
			}

			// If the flight is in the database, update it
			else {
				FlightTrack track = f.getFlightTrack();

				/**
				 * If this is the first TZ/UZ message, the heading is set to 0
				 * Otherwise, calculate the heading from previous position
				 */
				double heading = track == null ? 0 : calc.angleLL(track
						.getLatitude(), track.getLongitude(), latitude,
						longitude);

				f.setFlightTrack(new FlightTrack(latitude, longitude, altitude,
						message.getTime(), speed, heading));
				tsafeDB.updateFlight(f);
			}
		}

		// Update/boundary crossing message
		else if (messageType.equals("UZ")) {
			String aircraftId = NASFields.getAircraftId(message
					.getField(Message.FLIGHT_ID));
			Fix fix = NASFields.getCoordinationFix(message
					.getField(Message.COORDINATION_FIX));

			if (fix == null) {
				return;
			}

			double latitude = fix.getLatitude();
			double longitude = fix.getLongitude();
			double altitude = NASFields.getAltitude(message
					.getField(Message.ASSIGNED_ALTITUDE));
			double speed = NASFields.getGroundSpeed(message
					.getField(Message.SPEED));

			/**
			 * The coordination time is given in hours only. We assume the date
			 * to be the same as date on which the message was received. 
			 */
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date(message.getTime()));
			long time = NASFields.getCoordinationTime(cal.get(Calendar.YEAR),
					cal.get(Calendar.MONTH), cal.get(Calendar.DATE), message
							.getField(Message.COORDINATION_TIME));

			// try to select flight from the database
			Flight f = tsafeDB.selectFlight(aircraftId);

			// If flight not in the database, insert it
			if (f == null) {
				//** We say the heading is zero when we receive the first TZ/UZ
				// message.
				FlightTrack track = new FlightTrack(latitude, longitude,
						altitude, message.getTime(), speed, 0);
				//** Since we don't have enough information for a full flight
				// plan, we don't parse the route
				tsafeDB.insertFlight(new Flight(aircraftId, track));
			}

			// If the flight is in the database, update it
			else {
				Route route = NASFields.getRoute(message
						.getField(Message.ROUTE_DATA), tsafeDB, calc);
				FlightTrack track = f.getFlightTrack();


				/**
				 * If this is the first TZ/UZ message, the heading is set to 0
				 * Otherwise, calculate the heading from previous position
				 */
				double heading = track == null ? 0 : calc.angleLL(track
						.getLatitude(), track.getLongitude(), latitude,
						longitude);

				f.setFlightTrack(new FlightTrack(latitude, longitude, altitude,
						time, speed, heading));
				if (f.getFlightPlan() != null) {
					f.setFlightPlan(f.getFlightPlan().amendRoute(route));
				}
				tsafeDB.updateFlight(f);
			}
		}

		// Flight plan message
		else if (messageType.equals("FZ")) {
			// FZ messages are always received before the flight
			// takes off, so this flight will not be in the database
			String aircraftId = NASFields.getAircraftId(message
					.getField(Message.FLIGHT_ID));
			if (tsafeDB.selectFlight(aircraftId) != null) {
				tsafeDB.deleteFlight(aircraftId);
			}

			double assignedSpeed = NASFields.getGroundSpeed(message
					.getField(Message.SPEED));
			double assignedAltitude = NASFields.getAltitude(message
					.getField(Message.ASSIGNED_ALTITUDE));

			Route route = NASFields.getRoute(message
					.getField(Message.ROUTE_DATA), tsafeDB, calc);

			FlightPlan plan = new FlightPlan(assignedSpeed, assignedAltitude,
					route);

			Flight flight = new Flight(aircraftId, plan);
			tsafeDB.insertFlight(flight);
		}

		// Amendment message
		else if (messageType.equals("AF")) {
			// ** We ignore AF messages if they do not amend any field
			if (message.getField(Message.FIELD_REFERENCE) == null)
				return;

			// Get the aircraft id and return if the flight isn't in the db
			// ** We ignore AF messages for flights we do not know about or
			// ** flights that do not yet have flight plans
			String aircraftId = NASFields.getAircraftId(message
					.getField(Message.FLIGHT_ID));
			Flight flight = tsafeDB.selectFlight(aircraftId);

			if (flight == null || flight.getFlightPlan() == null)
				return;

			// Any of the fields set during an FZ message may be altered.
			switch (Integer.parseInt(message.getField(Message.FIELD_REFERENCE))) {

			// Flight id amendment message
			case Message.FLIGHT_ID:
				tsafeDB.deleteFlight(aircraftId);
				String newAircraftId = NASFields.getAircraftId(message
						.getField(Message.AMENDMENT_DATA));
				Flight flight2 = new Flight(newAircraftId, flight
						.getFlightTrack(), flight.getFlightPlan());
				tsafeDB.insertFlight(flight2);
				break;

			// Route amendment message
			case Message.ROUTE_DATA:
				Route route = NASFields.getRoute(message
						.getField(Message.AMENDMENT_DATA), tsafeDB, calc);
				flight.setFlightPlan(flight.getFlightPlan().amendRoute(route));
				tsafeDB.updateFlight(flight);
				break;

			// Aircraft data amendment message
			case Message.AIRCRAFT_DATA:
				// ** We ignore amendments to the aircraft data
				break;

			// Speed amendment message
			case Message.SPEED:
				double speed = NASFields.getGroundSpeed(message
						.getField(Message.AMENDMENT_DATA));
				flight.setFlightPlan(flight.getFlightPlan().amendAssignedSpeed(
						speed));
				tsafeDB.updateFlight(flight);
				break;

			// Assigned Altitude amendment message
			case Message.ASSIGNED_ALTITUDE:
				double altitude = NASFields.getAltitude(message
						.getField(Message.AMENDMENT_DATA));
				flight.setFlightPlan(flight.getFlightPlan()
						.amendAssignedAltitude(altitude));
				tsafeDB.updateFlight(flight);

			case Message.REQUESTED_ALTITUDE:
				// ** We ignore amendments to the requested altitude
				break;

			// Coordination fix amendment message
			case Message.COORDINATION_FIX:
				// ** We ignore amendments to the coordination fix
				break;

			// Coordination time amendment message
			case Message.COORDINATION_TIME:
				// ** We ignore amendments to the coordination time
				break;

			// Some unknown amendment, throw an exception
			default:
				throw new RuntimeException("Unknown amendment message: "
						+ message.getOriginalString());
			}
		}

		// Flight cancellation message
		else if (messageType.equals("RZ")) {
			tsafeDB.deleteFlight(NASFields.getAircraftId(message
					.getField(Message.FLIGHT_ID)));
		}

		// Arrival messages
		else if (messageType.equals("AZ")) {
			tsafeDB.deleteFlight(NASFields.getAircraftId(message
					.getField(Message.FLIGHT_ID)));
		}

		// Flight prediction message
		else if (messageType.equals("RT")) {
			// ** We ignore flight prediction messages
		}

		// Departure message
		else if (messageType.equals("DZ")) {
			// ** We ignore departure messages
		}

		// Oceanic position message
		else if (messageType.equals("TO")) {
			// ** We ignore oceanic position messages
		}

		// Heartbeat message
		else if (messageType.equals("HB")) {
			// ** We ignore heartbeat messages
		}

		// Uh Oh, unknown message
		else {
			throw new RuntimeException("Unknown message: "
					+ message.getOriginalString());
		}
	}
}