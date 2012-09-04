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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.JToggleButton;

import tsafe.common_datastructures.client_server_communication.UserParameters;

/**
 * Singleton class Dialog box to set the engine's parameters
 */
class ParametersDialog extends JDialog implements ActionListener {

	/**
	 * UserParameters
	 */
	private UserParameters parameters;

	/**
	 * Tabbed Pane
	 */
	private JTabbedPane tabbedPane;

	/**
	 * Toggle buttons alerting as to whether certain parameters are on or off
	 */
	private JToggleButton cmLateralWeightButton = new JToggleButton();

	private JToggleButton cmVerticalWeightButton = new JToggleButton();

	private JToggleButton cmAngularWeightButton = new JToggleButton();

	private JToggleButton cmSpeedWeightButton = new JToggleButton();

	/**
	 * JTextFields holding all the parameter values
	 */
	private JTextField cmLateralThresholdField = new JTextField(10);

	private JTextField cmVerticalThresholdField = new JTextField(10);

	private JTextField cmAngularThresholdField = new JTextField(10);

	private JTextField cmSpeedThresholdField = new JTextField(10);

	private JTextField cmResidualThresholdField = new JTextField(10);

	private JTextField tsTimeHorizonField = new JTextField(10);

	/**
	 * Private constructor instantiated once
	 */
	public ParametersDialog(GraphicalWindow client, UserParameters parameters) {
		super(client, "Tsafe Preferences");
		this.parameters = parameters;

		// Make the tabs
		Component confMonitorTab = makeConfMonitorTab();
		Component trajSynthTab = makeTrajSynthTab();

		// Make the tabbed pane
		this.tabbedPane = new JTabbedPane();
		tabbedPane.addTab("Conformance Monitor", confMonitorTab);
		tabbedPane.addTab("Trajectory Synthesizer", trajSynthTab);

		// Layout the dialog
		super.setContentPane(tabbedPane);
		super.pack();
	}

	public void showConformanceMonitorParameters() {
		showParametersAtTab(0);
	}

	public void showTrajectorySynthesizerParameters() {
		showParametersAtTab(1);
	}

	private void showParametersAtTab(int tabIdx) {
		this.tabbedPane.setSelectedIndex(tabIdx);
		setTextFields();
		super.show();
	}

	/**
	 * Handles the button clicks in the panel
	 */
	public void actionPerformed(ActionEvent e) {
		// if OK was pressed
		if (e.getActionCommand().equals("OK")) {
			setEngineParameters();
		}

		// close the dialog
		this.dispose();
	}

	/**
	 * Sets the data in the text fields to match that in the parameters object
	 */
	private void setTextFields() {
		cmLateralWeightButton.setText(parameters.cmLateralWeightOn ? "On"
				: "Off");
		cmVerticalWeightButton.setText(parameters.cmVerticalWeightOn ? "On"
				: "Off");
		cmAngularWeightButton.setText(parameters.cmAngularWeightOn ? "On"
				: "Off");
		cmSpeedWeightButton.setText(parameters.cmSpeedWeightOn ? "On" : "Off");

		cmLateralThresholdField.setEnabled(parameters.cmLateralWeightOn);
		cmVerticalThresholdField.setEnabled(parameters.cmVerticalWeightOn);
		cmAngularThresholdField.setEnabled(parameters.cmAngularWeightOn);
		cmSpeedThresholdField.setEnabled(parameters.cmSpeedWeightOn);

		cmLateralThresholdField.setText(Double
				.toString(parameters.cmLateralThreshold));
		cmVerticalThresholdField.setText(Double
				.toString(parameters.cmVerticalThreshold));
		cmAngularThresholdField.setText(Double
				.toString(parameters.cmAngularThreshold));
		cmSpeedThresholdField.setText(Double
				.toString(parameters.cmSpeedThreshold));
		cmResidualThresholdField.setText(Double
				.toString(parameters.cmResidualThreshold));

		tsTimeHorizonField.setText(Long.toString(parameters.tsTimeHorizon));
	}

	/**
	 * Sets the data in the parameters object to match that in the text fields
	 */
	private void setEngineParameters() {
		parameters.cmLateralWeightOn = cmLateralWeightButton.getText().equals(
				"On");
		parameters.cmVerticalWeightOn = cmVerticalWeightButton.getText()
				.equals("On");
		parameters.cmAngularWeightOn = cmAngularWeightButton.getText().equals(
				"On");
		parameters.cmSpeedWeightOn = cmSpeedWeightButton.getText().equals("On");

		parameters.cmLateralThreshold = Double
				.parseDouble(cmLateralThresholdField.getText());
		parameters.cmVerticalThreshold = Double
				.parseDouble(cmVerticalThresholdField.getText());
		parameters.cmAngularThreshold = Double
				.parseDouble(cmAngularThresholdField.getText());
		parameters.cmSpeedThreshold = Double.parseDouble(cmSpeedThresholdField
				.getText());
		parameters.cmResidualThreshold = Double
				.parseDouble(cmResidualThresholdField.getText());

		parameters.tsTimeHorizon = Long.parseLong(tsTimeHorizonField.getText());
	}

	private Component makeConfMonitorTab() {
		JPanel labelPanel = new JPanel(new GridLayout(5, 1));
		labelPanel.add(new JLabel("Lateral Threshold:   "));
		labelPanel.add(new JLabel("Vertical Theshold:  "));
		labelPanel.add(new JLabel("Angular Threshold:   "));
		labelPanel.add(new JLabel("Speed Threshold:     "));
		labelPanel.add(new JLabel("Residual Threshold:      "));

		JPanel valuesPanel = new JPanel(new GridLayout(5, 1));
		valuesPanel.add(cmLateralThresholdField);
		valuesPanel.add(cmVerticalThresholdField);
		valuesPanel.add(cmAngularThresholdField);
		valuesPanel.add(cmSpeedThresholdField);
		valuesPanel.add(cmResidualThresholdField);

		cmLateralWeightButton.addActionListener(new WeightButtonListener(
				cmLateralWeightButton, cmLateralThresholdField));
		cmVerticalWeightButton.addActionListener(new WeightButtonListener(
				cmVerticalWeightButton, cmVerticalThresholdField));
		cmAngularWeightButton.addActionListener(new WeightButtonListener(
				cmAngularWeightButton, cmAngularThresholdField));
		cmSpeedWeightButton.addActionListener(new WeightButtonListener(
				cmSpeedWeightButton, cmSpeedThresholdField));

		JPanel topLeftPanel = new JPanel(new BorderLayout());
		topLeftPanel.add(labelPanel, BorderLayout.WEST);
		topLeftPanel.add(valuesPanel, BorderLayout.CENTER);

		JPanel unitsPanel = new JPanel(new GridLayout(5, 1));
		unitsPanel.add(new JLabel("  meters  "));
		unitsPanel.add(new JLabel("  meters  "));
		unitsPanel.add(new JLabel("  radians "));
		unitsPanel.add(new JLabel("  meters / second  "));
		unitsPanel.add(new JLabel());

		JPanel onOffPanel = new JPanel(new GridLayout(5, 1));
		onOffPanel.add(cmLateralWeightButton);
		onOffPanel.add(cmVerticalWeightButton);
		onOffPanel.add(cmAngularWeightButton);
		onOffPanel.add(cmSpeedWeightButton);

		JPanel topRightPanel = new JPanel(new BorderLayout());
		topRightPanel.add(unitsPanel, BorderLayout.CENTER);
		topRightPanel.add(onOffPanel, BorderLayout.EAST);

		JPanel confMonitorTab = new JPanel(new BorderLayout());
		confMonitorTab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		confMonitorTab.add(topLeftPanel, BorderLayout.CENTER);
		confMonitorTab.add(topRightPanel, BorderLayout.EAST);
		confMonitorTab.add(makeButtonPanel(), BorderLayout.SOUTH);
		return confMonitorTab;
	}

	private Component makeTrajSynthTab() {
		JPanel timePanel = new JPanel();
		timePanel.add(new JLabel("Time Horizon: "));
		timePanel.add(this.tsTimeHorizonField);
		timePanel.add(new JLabel("milliseconds"));

		JPanel trajSynthTab = new JPanel(new BorderLayout());
		trajSynthTab.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		trajSynthTab.add(timePanel, BorderLayout.CENTER);
		trajSynthTab.add(makeButtonPanel(), BorderLayout.SOUTH);
		return trajSynthTab;
	}

	private JPanel makeButtonPanel() {
		JButton okButton = new JButton("OK");
		JButton cancelButton = new JButton("Cancel");
		okButton.addActionListener(this);
		cancelButton.addActionListener(this);

		JPanel buttonPanel = new JPanel();
		buttonPanel.add(okButton);
		buttonPanel.add(cancelButton);
		return buttonPanel;
	}

	private static class WeightButtonListener implements ActionListener {
		private JToggleButton button;

		private JTextField field;

		public WeightButtonListener(JToggleButton button, JTextField field) {
			this.button = button;
			this.field = field;
		}

		public void actionPerformed(ActionEvent e) {
			if (button.getText().equals("On")) {
				button.setText("Off");
				field.setEnabled(false);
			} else {
				button.setText("On");
				field.setEnabled(true);
			}
		}
	}
}