/*
 * Created on Aug 26, 2004
 *
 * To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package tsafe.client.graphical_client;

import java.awt.Dimension;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tsafe.client.SelectedFlights;
import tsafe.client.ShowOptions;
import tsafe.client.ShowOptions.Options;
import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.TSAFEProperties;
import tsafe.common_datastructures.client_server_communication.ComputationResults;

/**
 *  
 * To change the template for this generated type comment go to Window -
 * Preferences - Java - Code Style - Code Templates
 */
public class GraphicalWindow extends JFrame implements ListSelectionListener,
		ActionListener {

	/**
	 * Parameters dialog box
	 */
	private ParametersDialog paramsDialog;

	/**
	 * Map on which flight data, fixes, etc are drawn
	 */
	private FlightMap flightMap;

	/**
	 * The Options on what type of flights, tract, routes ect to display
	 */
	private ShowOptions showOpt;
	
	
	/**
	 * List of flights to select from
	 */
	private FlightList flightList;
	
	/**
	 * The flights that are selected
	 */
	private SelectedFlights selectedFlights;

	/**
	 * Manages the communication to the server
	 */
	private GraphicalClient client;
	
	private TsafeMenu tSafeMenu; 

	/**
	 * Location of split pane divider
	 */
	private static final int SPLIT_DIVIDER_LOCATION = 100;


	/**
	 *  
	 */
	public GraphicalWindow(GraphicalClient client) {

		this.client = client;

		//		 Create a parameters dialog box
		this.paramsDialog = new ParametersDialog(this, this.client
				.getParameters());
		this.showOpt = this.client.getShowOptions();
				
		this.selectedFlights = client.getSelectedFlights();
		
		//Added a title so the user can tell the difference between the two windows
		this.setTitle("Tsafe Map Window");
		
		// Create the background image.
		Image bgImage = Toolkit.getDefaultToolkit().getImage(
				TSAFEProperties.getBackgroundImage());

		// Create a flight map.  We removed getFlightMap function b/c we no longer are using static properties
		this.flightMap = new FlightMap(bgImage, this.client.getBounds(), 
				this.client.getFixes(),this.showOpt,this.selectedFlights );


		// Create a list of flights and add the client as a listener
		this.flightList = new FlightList();
		flightList.addListSelectionListener(this);

		this.tSafeMenu = makeTsafeMenu(this);
		// Build the content pane
		setJMenuBar(this.tSafeMenu);
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new JScrollPane(flightList), flightMap);
		splitPane.setDividerLocation(SPLIT_DIVIDER_LOCATION);
		Dimension mapDim = flightMap.getPreferredSize();
		splitPane.setPreferredSize(new Dimension(
				(int) (mapDim.getWidth() + SPLIT_DIVIDER_LOCATION),
				(int) mapDim.getHeight()));
		super.setContentPane(splitPane);
		
		//Quit everything when the window is closed
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				System.exit(0);
			}
		});

	}

	/**
	 * Starts parsing the feed and displays the window
	 * 
	 * @overrides Window.show()
	 */
	public void show() {
		super.show();
	}

	/**
	 * Called by TsafeMenu menu when recognizes menu events
	 */
	void showMenuChanged(String submenu, String submenuItem) {
		Options showOption = this.getOptionFromString(submenuItem);

		if (submenu.equals(TsafeMenu.FIXES_TEXT))
			this.showOpt.setShowFixesOption(showOption);

		else if (submenu.equals(TsafeMenu.FLIGHTS_TEXT))
			this.showOpt.setShowFlightsOption(showOption);

		else if (submenu.equals(TsafeMenu.ROUTES_TEXT))
			this.showOpt.setShowRoutesOption(showOption);

		else if (submenu.equals(TsafeMenu.TRAJS_TEXT))
			this.showOpt.setShowTrajectoriesOption(showOption);

		flightMap.updateNeeded();
		repaint();
	}

	/**
	 * List selection event handler When the selected item has changed,
	 */
	public void valueChanged(ListSelectionEvent e) {
		Collection selFlights = flightList.getSelectedFlights();
		if(!selFlights.isEmpty() && !this.selectedFlights.getSelectedFlights().equals(selFlights))
		{
			this.selectedFlights.setSelectedFlights(selFlights);
			
			flightMap.updateNeeded();
			repaint();
		}
	}

	public void actionPerformed(ActionEvent e) {
		// Conformance Monitor parameters was selected
		if (e.getActionCommand().equals(TsafeMenu.CONF_MONITOR_TEXT))
			this.paramsDialog.showConformanceMonitorParameters();

		// Trajectory Synthesizer parameters was selected
		else
			/* if (e.getActionCommand().equals(TRAJ_SYNTH_TEXT)) */
			this.paramsDialog.showTrajectorySynthesizerParameters();
	}

	private void refreshTsafeMenu()
	{
		String showFixesText = showOptionToText(this.showOpt.getShowFixesOption());
		String showFlightsText = showOptionToText(this.showOpt.getShowFlightsOption());
		String showRoutesText = showOptionToText(this.showOpt.getShowRoutesOption());
		String showTrajectoriesText = showOptionToText(this.showOpt.getShowTrajectoriesOption());
		
		this.tSafeMenu.updateMenuItems(showFixesText, showFlightsText, showRoutesText, showTrajectoriesText);
	}

	private TsafeMenu makeTsafeMenu(GraphicalWindow client) {
		
		String showFixesText = showOptionToText(this.showOpt.getShowFixesOption());
		String showFlightsText = showOptionToText(this.showOpt.getShowFlightsOption());
		String showRoutesText = showOptionToText(this.showOpt.getShowRoutesOption());
		String showTrajectoriesText = showOptionToText(this.showOpt.getShowTrajectoriesOption());
		
		return new TsafeMenu(client, showFixesText,
				showFlightsText, showRoutesText,
				showTrajectoriesText);
	}

	private String showOptionToText(ShowOptions.Options showOption) {
		
		switch (showOption) {
		case ShowAll:
			return TsafeMenu.ALL_TEXT;
		case ShowSelected:
			return TsafeMenu.SELECTED_TEXT;
		case ShowWithPlan:
			return TsafeMenu.WITH_PLAN_TEXT;
		case ShowConforming:
			return TsafeMenu.CONFORMING_TEXT;
		case ShowBlundering:
			return TsafeMenu.BLUNDERING_TEXT;
		case ShowNone:
			return TsafeMenu.NONE_TEXT;
		}

		throw new RuntimeException("Invalid Show Option");
	}

	private static ShowOptions.Options getOptionFromString(String showText) {
		
		if (showText.equals(TsafeMenu.ALL_TEXT))
			return ShowOptions.Options.ShowAll;
		if (showText.equals(TsafeMenu.SELECTED_TEXT))
			return ShowOptions.Options.ShowSelected;
		if (showText.equals(TsafeMenu.WITH_PLAN_TEXT))
			return ShowOptions.Options.ShowWithPlan;
		if (showText.equals(TsafeMenu.CONFORMING_TEXT))
			return ShowOptions.Options.ShowConforming;
		if (showText.equals(TsafeMenu.BLUNDERING_TEXT))
			return ShowOptions.Options.ShowBlundering;
		if (showText.equals(TsafeMenu.NONE_TEXT))
			return ShowOptions.Options.ShowNone;

		throw new RuntimeException("Invalid Show Text");
	}

	public void updateWindow(ComputationResults results) {

		/* We pass in the current flights to re-populate the list,
		   we also pass in the selected flights so that the flights that are selected
		   can be updated, even if they were selected using a different client */
		flightList.setFlights(results.getFlights(),this.selectedFlights.getSelectedFlights());

		// Update the flight map
		synchronized (flightMap) {
			flightMap.setFlights(results.getFlights());
			flightMap.setBlunders(results.getBlunders());
			flightMap.setFlightTrajectoryMap(results.getFlight2TrajectoryMap());
		}

		/* We need to update the selected flights and the tsafe menu, if the
		   user used the command prompt to change options or selected flights */
		refreshTsafeMenu();
		
		flightMap.updateNeeded();
		repaint();
	}

	public void startWindow() {
		this.pack();
		this.show();
	}
}