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

package tsafe.main;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.io.File;

import tsafe.Feed;
import tsafe.client.ClientInterface;
import tsafe.client.graphical_client.GraphicalClient;
import tsafe.common_datastructures.TSAFEProperties;
import tsafe.server.ServerInterface;
import tsafe.server.server_gui.SplashScreen;
import tsafe.server.server_gui.utils.WaitCursorEventQueue;

/**
 * The TSAFE configuration console.
 */
public class Main {

	// Default values
	private static int PAUSE_DEFAULT = 3000;
	private static String FILENAME_DEFAULT = "feeds/test1.txt";

	public static void main(String[] args) {

		File input;
		int pause;

		if (args.length >= 2) {
			try {
				input = new File(args[0]);
				pause = Integer.parseInt(args[1]);
			} catch (NumberFormatException e) {
				// Use default values on exception
				input = new File(FILENAME_DEFAULT);
				pause = PAUSE_DEFAULT;
			}
		} else {
			input = new File(FILENAME_DEFAULT);
			pause = PAUSE_DEFAULT;
		}

		// Start the server feed
		Thread t = new Feed(input, pause);
		t.start();

		// Start Tsafe
		Main m = new Main();
		m.start();
	}

	public void start() {

		ServerInterface server;

		long hideSplashTime = 0;

		// CASE #1: Startup using splash screen.
		if (TSAFEProperties.getShowSplashScreenFlag()) {

			// Show the splash screen (for a minimum of 3.3 seconds).
			hideSplashTime = System.currentTimeMillis() + 3300;
			SplashScreen.show();
		}

		// Start the server.
		server = new ServerInterface();

		// Create the clients
		ClientInterface client = new GraphicalClient(server);
		server.attachObserver(client);

		if (TSAFEProperties.getShowSplashScreenFlag()) {
			// Wait for time to expire.
			while (System.currentTimeMillis() < hideSplashTime) {
			}

			// Show the console.

			SplashScreen.hide();
		}

		// Install the Event Queue decorator to automatically change the cursor
		// to an hourglass if a GUI-event takes too much time (exceeds 5
		// seconds).
		EventQueue waitQueue = new WaitCursorEventQueue(500);
		Toolkit.getDefaultToolkit().getSystemEventQueue().push(waitQueue);
	}

}