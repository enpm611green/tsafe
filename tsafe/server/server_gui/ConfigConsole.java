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

package tsafe.server.server_gui;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Iterator;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.UIManager;

import tsafe.common_datastructures.LatLonBounds;
import tsafe.common_datastructures.TSAFEProperties;
import tsafe.common_datastructures.TSAFEResourceAnchor;
import tsafe.server.ServerMediator;
import tsafe.server.server_gui.preferences.PreferencesDialog;
import tsafe.server.server_gui.utils.LayoutUtils;

/**
 * This is the configuration window for TSAFE.
 */
public class ConfigConsole extends JFrame{

	//
	// CONSTANTS
	//

	/**
	 * The icon representing TSAFE (for the upper-left corner of the frame).
	 */
	private final static String TSAFE_ICON = "images/TSAFE_icon.gif";

	//
	// MEMBER VARIABLES
	//

	/**
	 * The config panels that make up this console.
	 */
	private java.util.List configPanels;

	private LatLonBounds bounds;

	private ServerMediator server;

	/**
	 * The TSAFE client to launch.
	 */
	//	private GraphicalClient client;
	//
	// METHODS
	//
	//-------------------------------------------
	/**
	 * Create a new config window.
	 */
	public ConfigConsole(ServerMediator server) {
		super("TSAFE Configuration");
		this.server = server;

		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			System.err.println("Unable to load system's look and feel");
		}

		setContentPane(makeContentPane());
		setIconImage(TSAFEResourceAnchor.getImage(TSAFE_ICON));
		initMenuBar();
		//setDefaultCloseOperation(EXIT_ON_CLOSE);
		

		// Before the parent frame closes, save all window preferences.
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent we) {
				saveWindowPreferences();
				TSAFEProperties.saveWindowPreferences();
				System.exit(0);
			}
		});

		loadWindowPreferences();
		this.show();
	}

	//-------------------------------------------
	/**
	 * Load this window's preferences.
	 */
	private void loadWindowPreferences() {

		Dimension size = TSAFEProperties.getWindowSize("ConfigConsole");
		if (size != null) {
			((JPanel) getContentPane()).setPreferredSize(size);
		}
		pack();

		Point location = TSAFEProperties.getWindowLocation("ConfigConsole");
		if (location != null) {
			setLocation(location);
		} else {
			setLocation(LayoutUtils.centerWindow(getSize()));
		}
	}

	//-------------------------------------------
	/**
	 * Save this window's preferences.
	 */
	private void saveWindowPreferences() {

		TSAFEProperties.setWindowSize("ConfigConsole", getContentPane()
				.getSize());
		TSAFEProperties.setWindowLocation("ConfigConsole", getLocation());
	}

	//-------------------------------------------
	/**
	 * Creates this frame's content panel.
	 * 
	 * @return a JPanel
	 */
	private JPanel makeContentPane() {

		// Create a tabbed pane containing the various config panels.
		// Add a common button panel to each config panel.
		JTabbedPane tPane = new JTabbedPane();
		ConfigPanel configPanel;
		configPanels = new Vector();
		CommonButtonPanelListener listener = new CommonButtonPanelListener(this);

		configPanel = new DataSourcesConfigPanel(this,
				createCommonButtonPanel(listener));
		configPanels.add(configPanel);
		tPane.addTab("Data Sources", configPanel);

		configPanel = new MapConfigPanel(this,
				createCommonButtonPanel(listener));
		configPanels.add(configPanel);
		tPane.addTab("Map", configPanel);

		JPanel panel = new JPanel(new GridLayout());
		panel.add(tPane);

		return panel;
	}

	//-------------------------------------------
	/**
	 * Set up the Launch button.
	 * 
	 * @param al
	 *            the event listener
	 * @return a new JPanel containing the three buttons
	 */
	private JPanel createCommonButtonPanel(ActionListener al) {

		// Create the button panel.
		JPanel buttonPanel = new JPanel(new GridLayout(1, 3,
				LayoutUtils.BUTTONP_SPACE_HORIZONTAL,
				LayoutUtils.BUTTONP_SPACE_VERTICAL));

		JButton button = new JButton("Launch TSAFE");
		button.setToolTipText("Accept configuration and launch TSAFE");
		button.setActionCommand("Launch");
		button.addActionListener(al);
		buttonPanel.add(button);

		button = new JButton("Save Configuration");
		button.setToolTipText("Save the configuration for future sessions");
		button.setActionCommand("Save");
		button.addActionListener(al);
		buttonPanel.add(button);

		button = new JButton("Restore Configuration");
		button.setToolTipText("Restore a previously saved configurations");
		button.setActionCommand("Restore");
		button.addActionListener(al);
		buttonPanel.add(button);

		return buttonPanel;
	}

	//-------------------------------------------
	/**
	 * Initialize the menu bar.
	 */
	private void initMenuBar() {
		JMenuBar menubar = new JMenuBar();
		setJMenuBar(menubar);

		MenuBarListener mbl = new MenuBarListener(this);

		// Create "File" menu
		JMenu menu = new JMenu("File");
		menubar.add(menu);

		JMenuItem menuItem = new JMenuItem("Exit");
		menuItem.setActionCommand("Exit");
		menuItem.addActionListener(mbl);
		menu.add(menuItem);

		// Create "Options" menu
		menu = new JMenu("Options");
		menubar.add(menu);

		menuItem = new JMenuItem("GUI");
		menuItem.setActionCommand("GUI");
		menuItem.addActionListener(mbl);
		menu.add(menuItem);

		menuItem = new JMenuItem("Feed Sources");
		menuItem.setActionCommand("Feed Sources");
		menuItem.addActionListener(mbl);
		menu.add(menuItem);

		// Create Options.Content_Filter submenu
		JMenu submenu = new JMenu("Content Filter");

		menuItem = new JMenuItem("Default Filter");
		menuItem.setActionCommand("Default Filter");
		menuItem.addActionListener(mbl);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Airlines");
		menuItem.setActionCommand("Airlines");
		menuItem.addActionListener(mbl);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Facilities");
		menuItem.setActionCommand("Facilities");
		menuItem.addActionListener(mbl);
		submenu.add(menuItem);

		menuItem = new JMenuItem("Message Types");
		menuItem.setActionCommand("Message Types");
		menuItem.addActionListener(mbl);
		submenu.add(menuItem);

		menu.add(submenu);

		// Create "Help" menu
		menu = new JMenu("Help");
		menubar.add(menu);

		menuItem = new JMenuItem("About");
		menuItem.setActionCommand("About");
		menuItem.addActionListener(mbl);
		menu.add(menuItem);
	}

	//-------------------------------------------
	/**
	 * Prepares TSAFE for launch. If any errors occur during preparation,
	 * returns a List of error strings; if there are no errors, returns an empty
	 * list.
	 * 
	 * @return a List of error strings
	 */
	private java.util.List prepareForLaunch() {
		java.util.List errorMessages = new Vector();
		//		 Make the bounds of the map area.
		double minLat = 0;
		double minLon = 0;
		double maxLat = 0;
		double maxLon = 0;

		try {
			String[] constraints = TSAFEProperties.getLatitudeConstraints();
			int minLatDegrees = Integer.parseInt(constraints[0]);
			int minLatMinutes = Integer.parseInt(constraints[1]);
			int minLatSign = constraints[2].charAt(0) == 'N' ? 1 : -1;
			int maxLatDegrees = Integer.parseInt(constraints[3]);
			int maxLatMinutes = Integer.parseInt(constraints[4]);
			int maxLatSign = constraints[5].charAt(0) == 'N' ? 1 : -1;

			constraints = TSAFEProperties.getLongitudeConstraints();
			int minLonDegrees = Integer.parseInt(constraints[0]);
			int minLonMinutes = Integer.parseInt(constraints[1]);
			int minLonSign = constraints[2].charAt(0) == 'E' ? 1 : -1;
			int maxLonDegrees = Integer.parseInt(constraints[3]);
			int maxLonMinutes = Integer.parseInt(constraints[4]);
			int maxLonSign = constraints[5].charAt(0) == 'E' ? 1 : -1;

			minLat = minLatSign * (minLatDegrees + (minLatMinutes / 60.0));
			minLon = minLonSign * (minLonDegrees + (minLonMinutes / 60.0));
			maxLat = maxLatSign * (maxLatDegrees + (maxLatMinutes / 60.0));
			maxLon = maxLonSign * (maxLonDegrees + (maxLonMinutes / 60.0));

		} catch (NumberFormatException e) {
		}

		this.bounds = new LatLonBounds(minLat, minLon, maxLat, maxLon);
		// Create the background image.
		Image bgImage = Toolkit.getDefaultToolkit().getImage(
				TSAFEProperties.getBackgroundImage());

		return errorMessages;
	}

	//
	// INNER CLASSES
	//

	/**
	 * Listens and responds to any events fired on behalf of the common button
	 * panel.
	 */
	private class CommonButtonPanelListener implements ActionListener {

		//
		// MEMBER VARIABLES
		//

		/**
		 * The parent frame.
		 */
		private JFrame parent;

		//
		// METHODS
		//

		//-------------------------------------------
		/**
		 * Creates a new listener.
		 * 
		 * @param parent
		 *            the parent frame
		 */
		private CommonButtonPanelListener(JFrame parent) {
			this.parent = parent;
		}

		//-------------------------------------------
		/**
		 * Responds to any events thrown by the buttons.
		 * 
		 * @param event
		 *            the ActionEvent
		 */
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();

			// Save button.

			if (command.equals("Save")) {
				Iterator panels = configPanels.iterator();
				while (panels.hasNext()) {
					ConfigPanel panel = (ConfigPanel) panels.next();
					panel.saveConfigurationAsDefault();
				}
			}

			// Restore button.

			else if (command.equals("Restore")) {
				System.exit(0);
				/*Iterator panels = configPanels.iterator();
				while (panels.hasNext()) {
					ConfigPanel panel = (ConfigPanel) panels.next();
					panel.loadDefaultConfigurationProperties();
				}*/
			}

			// Launch button.

			else if (command.equals("Launch")) {

				// Validate all input parameters.
				java.util.List errorMessages = new Vector();
				Iterator panels = configPanels.iterator();
				while (panels.hasNext()) {
					ConfigPanel panel = (ConfigPanel) panels.next();
					errorMessages.addAll(panel.validateInputParameters());
				}
				if (errorMessages.size() > 0) {
					displayErrors(errorMessages);
					return;
				}

				// Prepare to launch TSAFE.
				panels = configPanels.iterator();
				while (panels.hasNext()) {
					ConfigPanel panel = (ConfigPanel) panels.next();
					panel.setConfigurationProperties();
				}
				errorMessages = prepareForLaunch();
				if (errorMessages.size() > 0) {
					displayErrors(errorMessages);
					return;
				}

				// Save the window preferences to disk.
				saveWindowPreferences();
				TSAFEProperties.saveWindowPreferences();

				// Now launch TSAFE!
				parent.dispose();
				server.startTsafe(bounds);
			}
			else {
				System.err.println("Unrecognized command: " + command);
			}
		}

		//-------------------------------------------
		/**
		 * Displays the error messages if any.
		 * 
		 * @param errorMessages
		 *            a list of error messages
		 */
		private void displayErrors(java.util.List errorMessages) {
			if ((errorMessages == null) || (errorMessages.size() == 0)) {
				return;
			}

			StringBuffer messageBuffer = new StringBuffer();
			Iterator messages = errorMessages.iterator();

			while (messages.hasNext()) {
				messageBuffer.append((String) messages.next());
				messageBuffer.append("      "
						+ System.getProperty("line.separator"));
			}

			JOptionPane.showMessageDialog(parent, messageBuffer.toString(),
					"Input Data Error", JOptionPane.ERROR_MESSAGE);
		}

	} // inner class CommonButtonPanelListener

	/**
	 * Listens and responds to any events fired on behalf of the main menu bar.
	 */
	private class MenuBarListener implements ActionListener {

		//
		// MEMBER VARIABLES
		//

		/**
		 * The parent frame.
		 */
		private JFrame parent;

		//
		// METHODS
		//

		//-------------------------------------------
		/**
		 * Creates a new menu bar listener.
		 * 
		 * @param parent
		 *            the frame that owns the menubar
		 */
		private MenuBarListener(JFrame parent) {
			this.parent = parent;
		}

		//-------------------------------------------
		/**
		 * Responds to any events thrown by the menu bar.
		 * 
		 * @param event
		 *            the ActionEvent
		 */
		public void actionPerformed(ActionEvent event) {
			String command = event.getActionCommand();

			// "File" menu items

			if (command.equals("Exit")) {
				saveWindowPreferences();
				TSAFEProperties.saveWindowPreferences();
				System.exit(0);
			}

			// "Options" menu items

			else if (command.equals("GUI")) {
				PreferencesDialog dialog = new PreferencesDialog();
				dialog.showDialog(parent, "TSAFE Preferences",
						PreferencesDialog.TAB_GUI);
			}

			else if (command.equals("Feed Sources")) {
				PreferencesDialog dialog = new PreferencesDialog();
				dialog.showDialog(parent, "TSAFE Preferences",
						PreferencesDialog.TAB_FEED_SOURCES);
			}

			else if (command.equals("Default Filter")) {
				PreferencesDialog dialog = new PreferencesDialog();
				dialog.showDialog(parent, "TSAFE Preferences",
						PreferencesDialog.TAB_CONTENT_FILTER);
			}

			else if (command.equals("Airlines")) {
				PreferencesDialog dialog = new PreferencesDialog();
				dialog.showDialog(parent, "TSAFE Preferences",
						PreferencesDialog.TAB_AIRLINES);
			}

			else if (command.equals("Facilities")) {
				PreferencesDialog dialog = new PreferencesDialog();
				dialog.showDialog(parent, "TSAFE Preferences",
						PreferencesDialog.TAB_FACILITIES);
			}

			else if (command.equals("Message Types")) {
				PreferencesDialog dialog = new PreferencesDialog();
				dialog.showDialog(parent, "TSAFE Preferences",
						PreferencesDialog.TAB_MESSAGE_TYPES);
			}

			// "Help" menu items

			else if (command.equals("About")) {
				AboutDialog.showDialog(parent, "About TSAFE");
			}
		}

	} // inner class MenuBarListener

	
}