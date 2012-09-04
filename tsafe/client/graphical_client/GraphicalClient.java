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

package tsafe.client.graphical_client;

import java.util.Collection;

import tsafe.client.ClientInterface;
import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.TSAFEProperties;
import tsafe.common_datastructures.client_server_communication.ComputationResults;
import tsafe.common_datastructures.client_server_communication.UserParameters;
import tsafe.server.ServerInterface;

/**
 * Main executable class
 */
public class GraphicalClient extends ClientInterface {

	/**
	 * The graphical main user interface
	 */
	private GraphicalWindow window;

	/**
	 * Construct a GraphicalClient
	 */
	public GraphicalClient(ServerInterface server) {
		super(server);
		bounds = TSAFEProperties.getLatLonBounds();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		this.window = new GraphicalWindow(this);
		this.window.startWindow();
	}

	/**
	 * Queries the server for new flight data.
	 * 
	 * @see tsafe.client.ClientInterface#getFlightData()
	 */
	public void updateClient(ComputationResults results) {
		this.window.updateWindow(results);
	}

	/**
	 * @return Returns the parameters.
	 */
	public UserParameters getParameters() {
		return parameters;
	}

	/**
	 * Interface method for the client for reading the fixes.
	 * 
	 * @return All fixes, stored in the database.
	 */
	public Collection getFixes() {
		return this.server.getFixes();
	}

	/**
	 * @return Returns the server.
	 */
	public ServerInterface getServer() {
		return server;
	}

	/**
	 * @param server
	 *            The server to set.
	 */
	public void setServer(ServerInterface server) {
		this.server = server;
	}

	/**
	 * @return Returns the bounds.
	 */
	public LatLonBounds getBounds() {
		return bounds;
	}

	public void testing() {
		ClientInterface client = new GraphicalClient(this.server);
		client.setBounds(bounds);
		client.run();
	}
}