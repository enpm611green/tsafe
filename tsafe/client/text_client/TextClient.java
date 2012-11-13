package tsafe.client.text_client;

import java.util.Collection;

import tsafe.client.ClientInterface;
import tsafe.client.SelectedFlights;
import tsafe.client.ShowOptions;
import tsafe.client.graphical_client.GraphicalWindow;
import tsafe.common_datastructures.TSAFEProperties;
import tsafe.common_datastructures.client_server_communication.ComputationResults;
import tsafe.common_datastructures.client_server_communication.UserParameters;
import tsafe.server.ServerInterface;

/**
 * Launches the Text Window and interacts with the server
 * 
 */
public class TextClient extends ClientInterface {

	// The Text Window, which has a command prompt and a text flight feed
	private TextWindow textWindow;
	
	/**
	 * The Constructor:  Initializes the ClientInterface properties
	 * @param server	The Server Interface which contains the flights
	 * @param userParams	The User Properties set by the user
	 * @param showOpt	The Show Options set by the user
	 * @param selFlights	The User Selected Flights
	 */
	public TextClient(ServerInterface server, UserParameters userParams, ShowOptions showOpt, SelectedFlights selFlights) {
		super(server,userParams,showOpt, selFlights);
		bounds = TSAFEProperties.getLatLonBounds();
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Runnable#run()
	 */
	public void run() {
		this.textWindow = new TextWindow(this);
		this.textWindow.startWindow();
	}

	@Override
	public void updateClient(ComputationResults results) {
		// Updates the flights displayed within the Flight text feed
		this.textWindow.updateTextWindow(results);
		
	}
	
	/**
	 * @return Returns the parameters.
	 */
	public UserParameters getParameters() {
		return parameters;
	}
	
	/**
	 * Gets the Show Options
	 * @return
	 */
	public ShowOptions getShowOptions(){
		return this.showOptions;
	}
	
	/**
	 * Gets the Selected Flights
	 * @return
	 */
	public SelectedFlights getSelFlights(){
		return this.selectedFlights;
	}
	
	/**
	 * Updates the selected flights
	 * @param selFlights
	 */
	public void updateSelectedFlights(Collection selFlights){
		this.selectedFlights.setSelectedFlights(selFlights);
	}

}
