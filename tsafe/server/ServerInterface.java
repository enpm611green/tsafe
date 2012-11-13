package tsafe.server;

import java.util.Collection;
import java.util.Iterator;
import java.util.Vector;

import tsafe.client.ClientInterface;
import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.client_server_communication.ComputationResults;
import tsafe.common_datastructures.client_server_communication.UserParameters;

/**
 *
 * 
 * Interface for the communication with the clients. It provides the methods to
 * meet the requirements of a subject in an Observer Pattern.
 */
public class ServerInterface {

	/**
	 * List of all clients that have subscribed to the subject.
	 */
	private Vector clients;

	/**
	 * Handle to the mediator object.
	 */
	private ServerMediator mediator;

	/**
	 * Constructor: Creates a new Vector object for the clients.
	 *  
	 */
	public ServerInterface() {
		clients = new Vector();
		this.mediator = new ServerMediator(this);
	}

	/**
	 * Attach the new client to the list of observers.
	 * 
	 * @param newClient
	 *            Object of
	 */
	public void attachObserver(ClientInterface newClient) {
		this.clients.add(newClient);
	}

	/**
	 * Deletes the client object from the list of the observer.
	 * 
	 * @param newClient
	 *            Object of the client interface.
	 */
	public void detachObserver(ClientInterface newClient) {
		this.clients.remove(newClient);
	}

	/**
	 * Notify the clients for an update.
	 */
	public void notifyObservers() {

		Iterator clientIterator = this.clients.iterator();

		while (clientIterator.hasNext()) {
			ClientInterface client = (ClientInterface) clientIterator.next();
			client.notifyClient();
		}
	}

	/**
	 * The method will be used from the clients in order to query for the
	 * current flight data.
	 * 
	 * @return Results of the calculation.
	 */
	public synchronized ComputationResults getFlightData(LatLonBounds bounds,
			UserParameters parameters) {

		return this.mediator.proceedFlights(bounds, parameters);
	}

	/**
	 * Interface method for the client for reading the fixes.
	 * 
	 * @return All fixes, stored in the database.
	 */
	public Collection getFixes() {
		return this.mediator.getFixes();
	}

	/**
	 * Starts the ServerMediator for launching Tsafe.
	 */
	public void launchTsafe() {

	};

	/*
	 * Method necessary for the communication to the client while launchin
	 * Tsafe.
	 */

	public void displayClient(LatLonBounds bounds) {

		Iterator clientIterator = this.clients.iterator();

		while (clientIterator.hasNext()) {
			ClientInterface client = (ClientInterface) clientIterator.next();
			client.setBounds(bounds);
			client.run();
		}
	}
}