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

package tsafe.server.parser;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.List;
import java.util.StringTokenizer;
import java.util.Vector;

import tsafe.common_datastructures.Airway;
import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Route;
import tsafe.common_datastructures.Sid;
import tsafe.common_datastructures.Star;
import tsafe.server.database.DatabaseInterface;

/**
 * An interface to a class that parses a feed. Employs the Template design
 * pattern to read from the feed and update the database accordingly.
 */
abstract public class ParserInterface implements Runnable {

	/**
	 * The original feed source
	 */
	private Reader source;

	/**
	 * The buffered reader that wraps the feed source
	 */
	protected BufferedReader feedReader;

	/**
	 * The database the feed parser updates as it reads the feed
	 */
	protected final DatabaseInterface tsafeDB;

	/**
	 * This flag is true if a stop has been requested
	 */
	private boolean stopped, readerClosed;

	/**
	 * Constructs a feed parser to continually reader from the feedReader and
	 * update a DatabaseInterface
	 * 
	 * This implementation assigns the arguments to the feedReader and tsafeDB
	 * fields, respectively.
	 */
	public ParserInterface(Reader source, DatabaseInterface tsafeDB) {
		this.source = source;
		this.tsafeDB = tsafeDB;
		this.stopped = false;
		this.readerClosed = false;
	}

	/**
	 * Start parsing the feed on a new thread
	 */
	public final void startParsing() {
		this.feedReader = new BufferedReader(source);
		(new Thread(this, "Feed Parser")).start();
	}

	/**
	 * Stop parsing the feed
	 */
	public final void stopParsing() {
		synchronized (this) {
			stopped = true;
		}
		while (!readerClosed)
			;
	}

	/** Returns true if a stop has been requested */
	private final synchronized boolean isStopped() {
		return stopped;
	}

	/**
	 * Continually reads from the feed and updates the database
	 * 
	 * This implementation continues in a loop until a stop is requested, at
	 * which point it stops the parser. Each time through the loop, it calls
	 * executeUpdate, to perform a single update of the database.
	 */
	public final void run() {

		// Continuing updating until a stop is requested.
		while (!isStopped()) {
			try {
				boolean moreUpdates = executeUpdate();

				// If there is nothing more to parse, stop parsing
				if (!moreUpdates) {
					break;
				}
			} catch (IOException e) {
				System.out
						.println("Error communicating with ASDI feed . . .  trying to reconnect.");
				try {
					// if there was an error communicating with the feed, try to
					// reset source
					source.reset();
				} catch (IOException e2) {
					System.out.println("Could not reconnect to ASDI feed.");
					// if the feed could not be reset, stop parsing
					break;
				}
			}
		}

		// Stop the parser
		try {
			feedReader.close();
			readerClosed = true;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads from the feed and executes a single update on the database Returns
	 * true if there are more updates to be executed.
	 * 
	 * @throws IOException -
	 *             if there is an error reading from the feed
	 */
	public abstract boolean executeUpdate() throws IOException;

	/*
	 * Methods to read the static data and save it in the data base. They are
	 * only used once when launching the program.
	 */

	private void readFixes(Reader reader, DatabaseInterface tsafeDB)
			throws IOException {
		BufferedReader input = new BufferedReader(reader);
		String line = input.readLine();

		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			tsafeDB.insertFix(parseFix(st));
			line = input.readLine();
		}
	}

	private void readAirways(Reader reader, DatabaseInterface tsafeDB)
			throws IOException {
		BufferedReader input = new BufferedReader(reader);
		String line = input.readLine();

		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			Airway airway = new Airway(st.nextToken());
			parseRoute(st, airway, tsafeDB);
			tsafeDB.insertAirway(airway);
			line = input.readLine();
		}
	}

	private void readSids(Reader reader, DatabaseInterface tsafeDB)
			throws IOException {
		BufferedReader input = new BufferedReader(reader);
		String line = input.readLine();

		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			String id = st.nextToken();
			Route basicRoute = parseRoute(st, new Route(), tsafeDB);
			Sid sid = new Sid(id, basicRoute);

			line = input.readLine();
			while (line != null && Character.isWhitespace(line.charAt(0))) {
				st = new StringTokenizer(line);
				Route transition = parseRoute(st, new Route(), tsafeDB);
				sid.addTransition(transition);
				line = input.readLine();
			}

			tsafeDB.insertSid(sid);
		}
	}

	private void readStars(Reader reader, DatabaseInterface tsafeDB)
			throws IOException {
		BufferedReader input = new BufferedReader(reader);
		String line = input.readLine();

		while (line != null) {
			StringTokenizer st = new StringTokenizer(line);
			String id = st.nextToken();
			Route basicRoute = parseRoute(st, new Route(), tsafeDB);
			Star star = new Star(id, basicRoute);

			line = input.readLine();
			while (line != null && Character.isWhitespace(line.charAt(0))) {
				st = new StringTokenizer(line);
				Route transition = parseRoute(st, new Route(), tsafeDB);
				star.addTransition(transition);
				line = input.readLine();
			}

			tsafeDB.insertStar(star);
		}
	}

	private Route parseRoute(StringTokenizer st, Route r,
			DatabaseInterface tsafeDB) {
		while (st.hasMoreTokens()) {
			// intern if possible
			Fix fix = parseFix(st);
			Fix interned = tsafeDB.selectFix(fix.getId());
			if (interned != null)
				fix = interned;
			r.addFix(fix);
		}
		return r;
	}

	private Fix parseFix(StringTokenizer st) {
		String fixId = st.nextToken();
		double lat = parseLatitude(st.nextToken());
		double lon = parseLongitude(st.nextToken());
		return new Fix(fixId, lat, lon);
	}

	private double parseLatitude(String latitude) {
		int degrees = Integer.parseInt(latitude.substring(0, 2));
		int minutes = Integer.parseInt(latitude.substring(3, 5));
		double seconds = Double.parseDouble(latitude.substring(6, 12));
		double coord = degrees + (minutes / 60.0) + (seconds / 3600.0);

		char declination = latitude.charAt(12);
		return declination == 'N' ? coord : -coord;
	}

	private double parseLongitude(String longitude) {
		int degrees = Integer.parseInt(longitude.substring(0, 3));
		int minutes = Integer.parseInt(longitude.substring(4, 6));
		double seconds = Double.parseDouble(longitude.substring(7, 13));
		double coord = degrees + (minutes / 60.0) + (seconds / 3600.0);

		char declination = longitude.charAt(13);
		return declination == 'E' ? coord : -coord;
	}

	/**
	 * Calls each method of this class in order to read the files with the
	 * static data.
	 * 
	 * @param dataFiles
	 *            String array contains the path name of each file that stores
	 *            the static data.
	 * @return List of error messages.
	 */
	public final List readStaticData(String[] dataFiles) {
		java.util.List errorMessages = new Vector();

		// Read fixes into database.
		try {
			Reader reader = new FileReader(dataFiles[0]);
			this.readFixes(reader, tsafeDB);
		} catch (java.io.IOException e) {
			errorMessages.add("Unable to read fix file.");
			return errorMessages;
		}

		// Read airports into database.
		try {
			Reader reader = new FileReader(dataFiles[1]);
			this.readFixes(reader, tsafeDB);
		} catch (java.io.IOException e) {	
			errorMessages.add("Unable to read airport file.");
			return errorMessages;
		}

		// Read navaids into database.
		try {
			Reader reader = new FileReader(dataFiles[2]);
			this.readFixes(reader, tsafeDB);
		} catch (java.io.IOException e) {	
			errorMessages.add("Unable to read navaid file.");
			return errorMessages;
		}

		// Read airways into database.
		try {
			Reader reader = new FileReader(dataFiles[3]);
			this.readAirways(reader, tsafeDB);
		} catch (java.io.IOException e) {
			errorMessages.add("Unable to read airway file.");
			return errorMessages;
		}

		// Read airways into database.
		try {
			Reader reader = new FileReader(dataFiles[4]);
			this.readSids(reader, tsafeDB);
		} catch (java.io.IOException e) {
			errorMessages.add("Unable to read sid file.");
			return errorMessages;
		}

		// Read stars into database.
		try {
			Reader reader = new FileReader(dataFiles[5]);
			this.readStars(reader, tsafeDB);
		} catch (java.io.IOException e) {
			errorMessages.add("Unable to read star file.");
			return errorMessages;
		}
		return errorMessages;
	}
}

