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
import java.util.Collection;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.TSAFEProperties;
import tsafe.common_datastructures.client_server_communication.ComputationResults;

/**
 * @author cackermann
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
	 * List of flights to select from
	 */
	private FlightList flightList;

	/**
	 * Manages the communication to the server
	 */
	private GraphicalClient client;

	/**
	 * Location of split pane divider
	 */
	private static final int SPLIT_DIVIDER_LOCATION = 100;

	/**
	 * Show Menu Initial Selections for graphical client.
	 */
	private static final int INITIAL_SHOW_FIXES_OPTION = FlightMap.SHOW_NONE;

	private static final int INITIAL_SHOW_FLIGHTS_OPTION = FlightMap.SHOW_ALL;

	private static final int INITIAL_SHOW_ROUTES_OPTION = FlightMap.SHOW_ALL;

	private static final int INITIAL_SHOW_TRAJS_OPTION = FlightMap.SHOW_WITH_PLAN;

	private static final String INITIAL_SHOW_FIXES_TEXT = showOptionToText(INITIAL_SHOW_FIXES_OPTION);

	private static final String INITIAL_SHOW_FLIGHTS_TEXT = showOptionToText(INITIAL_SHOW_FLIGHTS_OPTION);

	private static final String INITIAL_SHOW_ROUTES_TEXT = showOptionToText(INITIAL_SHOW_ROUTES_OPTION);

	private static final String INITIAL_SHOW_TRAJS_TEXT = showOptionToText(INITIAL_SHOW_TRAJS_OPTION);

	/**
	 *  
	 */
	public GraphicalWindow(GraphicalClient client) {

		this.client = client;

		//		 Create a parameters dialog box
		this.paramsDialog = new ParametersDialog(this, this.client
				.getParameters());

		// Create the background image.
		Image bgImage = Toolkit.getDefaultToolkit().getImage(
				TSAFEProperties.getBackgroundImage());

		// Create a flight map
		this.flightMap = makeFlightMap(bgImage, this.client.getBounds(),
				this.client.getFixes());

		// Create a list of flights and add the client as a listener
		this.flightList = new FlightList();
		flightList.addListSelectionListener(this);

		// Build the content pane
		setJMenuBar(makeTsafeMenu(this));
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
		int showOption = showTextToOption(submenuItem);

		if (submenu.equals(TsafeMenu.FIXES_TEXT))
			flightMap.setShowFixes(showOption);

		else if (submenu.equals(TsafeMenu.FLIGHTS_TEXT))
			flightMap.setShowFlights(showOption);

		else if (submenu.equals(TsafeMenu.ROUTES_TEXT))
			flightMap.setShowRoutes(showOption);

		else if (submenu.equals(TsafeMenu.TRAJS_TEXT))
			flightMap.setShowTrajectories(showOption);

		flightMap.updateNeeded();
		repaint();
	}

	/**
	 * List selection event handler When the selected item has changed,
	 */
	public void valueChanged(ListSelectionEvent e) {
		Collection selectedFlights = flightList.getSelectedFlights();
		flightMap.setSelectedFlights(selectedFlights);
		flightMap.updateNeeded();
		repaint();
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

	private void refreshWindow() {
		// Parameters changed, update flight map
		flightMap.updateNeeded();
		flightMap.repaint();
	}

	//	 PRIVATE HELPER METHODS

	private static FlightMap makeFlightMap(Image mapImage, LatLonBounds bounds,
			Collection fixes) {
		FlightMap flightMap = new FlightMap(mapImage, bounds, fixes);
		flightMap.setShowFixes(INITIAL_SHOW_FIXES_OPTION);
		flightMap.setShowFlights(INITIAL_SHOW_FLIGHTS_OPTION);
		flightMap.setShowRoutes(INITIAL_SHOW_ROUTES_OPTION);
		flightMap.setShowTrajectories(INITIAL_SHOW_TRAJS_OPTION);
		return flightMap;
	}

	private static TsafeMenu makeTsafeMenu(GraphicalWindow client) {
		return new TsafeMenu(client, INITIAL_SHOW_FIXES_TEXT,
				INITIAL_SHOW_FLIGHTS_TEXT, INITIAL_SHOW_ROUTES_TEXT,
				INITIAL_SHOW_TRAJS_TEXT);
	}

	private static String showOptionToText(int showOption) {
		switch (showOption) {
		case FlightMap.SHOW_ALL:
			return TsafeMenu.ALL_TEXT;
		case FlightMap.SHOW_SELECTED:
			return TsafeMenu.SELECTED_TEXT;
		case FlightMap.SHOW_WITH_PLAN:
			return TsafeMenu.WITH_PLAN_TEXT;
		case FlightMap.SHOW_CONFORMING:
			return TsafeMenu.CONFORMING_TEXT;
		case FlightMap.SHOW_BLUNDERING:
			return TsafeMenu.BLUNDERING_TEXT;
		case FlightMap.SHOW_NONE:
			return TsafeMenu.NONE_TEXT;
		}

		throw new RuntimeException("Invalid Show Option");
	}

	private static int showTextToOption(String showText) {
		if (showText.equals(TsafeMenu.ALL_TEXT))
			return FlightMap.SHOW_ALL;
		if (showText.equals(TsafeMenu.SELECTED_TEXT))
			return FlightMap.SHOW_SELECTED;
		if (showText.equals(TsafeMenu.WITH_PLAN_TEXT))
			return FlightMap.SHOW_WITH_PLAN;
		if (showText.equals(TsafeMenu.CONFORMING_TEXT))
			return FlightMap.SHOW_CONFORMING;
		if (showText.equals(TsafeMenu.BLUNDERING_TEXT))
			return FlightMap.SHOW_BLUNDERING;
		if (showText.equals(TsafeMenu.NONE_TEXT))
			return FlightMap.SHOW_NONE;

		throw new RuntimeException("Invalid Show Text");
	}

	public void updateWindow(ComputationResults results) {

		flightList.setFlights(results.getFlights());

		// Update the flight map
		synchronized (flightMap) {
			flightMap.setFlights(results.getFlights());
			flightMap.setBlunders(results.getBlunders());
			flightMap.setFlightTrajectoryMap(results.getFlight2TrajectoryMap());
		}

		flightMap.updateNeeded();
		repaint();
	}

	public void startWindow() {
		this.pack();
		this.show();
	}
}