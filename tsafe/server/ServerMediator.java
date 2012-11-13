package tsafe.server;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Reader;
import java.util.Collection;
import java.util.List;
import java.util.Vector;

import javax.swing.Timer;

import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.TSAFEProperties;
import tsafe.common_datastructures.client_server_communication.ComputationResults;
import tsafe.common_datastructures.client_server_communication.UserParameters;
import tsafe.server.calculation.Calculator;
import tsafe.server.computation.ComputationMediator;
import tsafe.server.database.DatabaseInterface;
import tsafe.server.database.RuntimeDatabase;
import tsafe.server.parser.ParserInterface;
import tsafe.server.parser.asdi.ASDIParser;
import tsafe.server.server_gui.ConfigConsole;

/**
 * 
 * 
 * Manages the communication between the logical components of the server
 * component. It also provides an interface for the communication with the
 * client component.
 */
public class ServerMediator implements ActionListener {

	/**
	 * The object of the ServerInterface is needed in order to communicate with
	 * the clients.
	 */
	ServerInterface serverInterface;

	/**
	 * Timer that triggers a repaint call
	 */
	private Timer timer;

	/**
	 * Initial time between successive repaints
	 */
	private static final int REPAINT_STEP = 3000;

	/**
	 * Handle to the computation component
	 */
	private ComputationMediator computation;

	/**
	 * Handle to the computation component
	 */
	private DatabaseInterface database;

	public ServerMediator(ServerInterface serverInterface) {

		this.serverInterface = serverInterface;

		// Create a timer to periodically repaint the flight map
		this.timer = new Timer(REPAINT_STEP, this);

		//this.launchTsafe();
		// Shows the configuration console.
		ConfigConsole console = new ConfigConsole(this);
	}

	/**
	 * Creates instances of the interface classes of each component in the
	 * server.
	 * 
	 * @return A list of errors that occured while reading the properties file.
	 */
	private List launchTsafe() {

		List errorMessages = new Vector();
		
		//Make the database.
		this.database = new RuntimeDatabase();


		// Make the Engine Calculator.
		Calculator calculator = new Calculator();

		// Retrieve the data files
		String[] dataFiles = TSAFEProperties.getDataFiles();

		// Make the feed reader.
		Reader feedReader = TSAFEProperties.getFeedSource();

		// Make the feed parser.
		ParserInterface feedParser = new ASDIParser(feedReader, this.database,
				calculator);

		feedParser.readStaticData(dataFiles);

		// Make the Tsafe Engine.
		this.computation = new ComputationMediator(calculator);

		// Start parsing the dynamic feed source
		feedParser.startParsing();

		// Start the timer that runs the program
		timer.start();
		return errorMessages;
	}

	/**
	 * Timer event handler When timer goes off, notify the clients
	 */
	public void actionPerformed(ActionEvent e) {
		// The timer has gone off
		if (e.getActionCommand() == null) {
			this.serverInterface.notifyObservers();
		}
	}

	/**
	 * Manages the flight data of a client.
	 */
	public ComputationResults proceedFlights(LatLonBounds bounds,
			UserParameters parameters) {

		// Query the database for flight in bounds, parse the flight list to the
		// computation component and start it
		this.computation
				.setFlights(this.database.selectFlightsInBounds(bounds));
		return this.computation.computeFlights(bounds, parameters);
	}

	public Collection getFixes() {
		return this.database.selectFixesInBounds();
	}

	/*
	 * Method necessary for the communication to the client while launchin
	 * Tsafe.
	 */
	public void startTsafe(LatLonBounds bounds) {
		this.launchTsafe();
		this.serverInterface.displayClient(bounds);
		timer.start();
	}
}