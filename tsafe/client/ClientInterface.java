package tsafe.client;

import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.client_server_communication.ComputationResults;
import tsafe.common_datastructures.client_server_communication.UserParameters;
import tsafe.server.ServerInterface;

/**
 * @author Christopher Ackermann
 * 
 * An Interface for clients that provides the methods, necessary in order to
 * communicate with the server component. All comunication to classes in the
 * client component must be done through this class.
 */
public abstract class ClientInterface extends Thread {

	//	*** Interface attributes **************************************
	/**
	 * Handle to the interface class of the server, necessary for the
	 * communication between client and server.
	 */
	protected ServerInterface server;

	/**
	 * The bounds for the area within the client is supposed to show the
	 * flights.
	 */
	protected LatLonBounds bounds;

	/**
	 * Stores the parameters such as thresholds that can be changed by the user
	 * and that are used for calculating the flights.
	 */
	protected UserParameters parameters;

	//****************************************************************

	public ClientInterface(ServerInterface server) {
		this.server = server;
		this.parameters = new UserParameters();
		bounds = new LatLonBounds(0,0,0,0);
	}

	/**
	 * Recieves the notification from the client after the timer goes off.
	 * 
	 * @see tsafe.client.ClientInterface#notifyClient()
	 */
	public void notifyClient() {
		// Get the new flight data after the notification.
		this.getFlightData();
	}

	/**
	 * Sets the bounds and starts the client.
	 */
	public void setBounds(LatLonBounds bounds) {
		this.bounds = bounds;
	}

	/**
	 * Queries the server for new flight data.
	 * 
	 * @see tsafe.client.ClientInterface#getFlightData()
	 */
	public void getFlightData() {

		ComputationResults results = this.server.getFlightData(this.bounds,
				this.parameters);
		updateClient(results);

	}

	public abstract void updateClient(ComputationResults results);

}