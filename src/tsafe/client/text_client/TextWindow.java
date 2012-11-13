package tsafe.client.text_client;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;

import tsafe.client.ShowOptions;
import tsafe.common_datastructures.client_server_communication.ComputationResults;
import tsafe.common_datastructures.client_server_communication.UserParameters;

/**
 * The Text Window is a JFrame with a command prompt and a text feed.
 * <p>
 * The command prompt allows user to enter the commands that interact with the text
 * feed and map.
 * <p>
 * The text feed displays the flight information
 * 
 */
public class TextWindow  extends JFrame {

	//A JList containing al the flight Information
	private FlightTextFeed flightTextFeed;
	
	//The Command Prompt
	private CommandPrompt commandPrompt;
	
	// The User Parameters sected by the user
	private UserParameters parameters;
	
	// The Show options choosen by the user
	private ShowOptions showOpt;
	
	// The Text Client which contains the flights from the server
	private TextClient textClient;
	
	/**
	 * The Constructor:  Initializes the properties
	 * @param client
	 */
	public TextWindow(TextClient client)
	{
		textClient = client;
		parameters = textClient.getParameters();
		showOpt = textClient.getShowOptions();
		
		//We add a flight text feed and a command prompt to the Dialog
		flightTextFeed = new FlightTextFeed(this.showOpt,this.textClient.getSelFlights());
		commandPrompt = new CommandPrompt(parameters,showOpt,this.textClient);
		this.setPreferredSize(new Dimension(700, 400));
		this.setLocation(0, 410);
		
		//We have a req that the Text Window frame should not close the frame, so we deactivate it.
		this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		
		// Since we have two windows now we should set tables for the boxes
		this.setTitle("Tsafe Text Window");
		
		// A label to notifiy the user that this box contains flight info
		JLabel label = new JLabel();
		label.setText("Flight Information");

		this.setLayout(new BoxLayout(this.getContentPane(), BoxLayout.Y_AXIS));
		
		this.add(label);
		
		//The Text Feed that is scrollable
		JScrollPane infoScrollPane = new JScrollPane(flightTextFeed);
				
		//The Command Prompt which is also scrollable
		JScrollPane scrollPane = new JScrollPane(commandPrompt);

		//This is allows the user to adjust the sizes of each panel
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,
				infoScrollPane, scrollPane);
		splitPane.setOneTouchExpandable(true);
		splitPane.setDividerLocation(150);
	
		this.add(splitPane);
	}
	
	/**
	 * We run the updateTextWindow, when ever the server fires off an update to the clients
	 * @param results
	 */
	public void updateTextWindow(ComputationResults results)
	{
		// Update the flight map
		synchronized (flightTextFeed) {
			flightTextFeed.setFlights(results.getFlights());
			flightTextFeed.setBlunders(results.getBlunders());
			flightTextFeed.setFlightTrajectoryMap(results.getFlight2TrajectoryMap());
		}
		
		// Updates the Flights and parameters within the text feed
		flightTextFeed.updateFlightText();
		
		synchronized (this.commandPrompt) {
			commandPrompt.setFlights(results.getFlights());
		}
	}
	
	public void startWindow() {
		this.pack();
		this.setVisible(true);
	}
}


