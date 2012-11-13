package tsafe.client.text_client;


import java.awt.Color;
import java.awt.Dimension;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Vector;


import javax.swing.JEditorPane;

import tsafe.client.SelectedFlights;
import tsafe.client.ShowOptions;
import tsafe.client.ShowOptions.Options;
import tsafe.client.text_client.TextParser.KeyWordType;
import tsafe.client.text_client.TextParser.ValueType;
import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.client_server_communication.UserParameters;

/**
 * The Command Prompt allows the user to enter text commands that interact with
 * the text feed and the graphical window.
 *
 *
 */
public class CommandPrompt extends JEditorPane implements KeyListener {
	
	// Is displayed in the command prompt 
	final String prompt = "Enter Flight Command: ";
	
	// Contains the flight information from the server
	private TextClient textClient;
	
	// The Parameters the user sets
	private UserParameters parameters;
	
	// The Display options the user chooses
	private ShowOptions showOpt;
	
	// All the flights loaded within the boundaries
	private Collection allFlights;
	
	/**
	 * The constructor:
	 * Stores the object for the user parameters, show options and text client
	 * <p>
	 * If the user parameters or show options are changes the other clients are also
	 * affected.
	 * @param userParam	The User Parameters set by the user within the different clients
	 * @param showOptions	The Display options
	 * @param txtClient	The Text Client, which contains the flight information from the server
	 */
	public CommandPrompt(UserParameters userParam, ShowOptions showOptions, TextClient txtClient)
	{						
		// Store all the objects entered
		this.parameters = userParam;
		this.showOpt = showOptions;
		this.allFlights = new LinkedList();
		this.textClient = txtClient;
				
		// Just making it look more colorful.  White on black can be diff on the eyes.
		this.setBackground(Color.green);
		
		// Display the prompt
		this.setText(prompt);
	
		this.addKeyListener(this);
		
		this.setPreferredSize(new Dimension(700, 200));
	}
	
    /** Handle the key typed event from the text field. */
    public void keyTyped(KeyEvent e) {
    	//Does nothing
    }
    
    /**
     * Handle the key pressed event from the text field.
     * <p>
     * We avoid the user from deleting the command prompt 'Enter Flight Info'
     * <p>
     * We also do not want the user typing in text within the command prompt, so we always
     * force the user to enter text at the end
     */
    public void keyPressed(KeyEvent e) {
       
    	// Check if the Caret is not at the end of the prompt
    	if(this.getCaretPosition() != this.getText().length())
    	{
    		// We move the caret to the end
    		this.setCaretPosition(this.getText().length());
    		
    		// We don't want the user to delete the prompt
    		if(e.getKeyCode() == 8)
    			e.consume();
    	}
    	
    	// We don't want the user to delete the prompt
    	else if(e.getKeyCode() == 8 && this.getText().endsWith(prompt))
    		e.consume();
    }
    
    /** 
     * Handle the key released event from the text field. 
     * <p>
     * When enter is clicked we parse the commands
     * */
    public void keyReleased(KeyEvent e) {
    	// We want to parse the user string when they hit enter
    	if(e.getKeyCode() == 10)
    	{
    		// Parses the Commands and sets the parameters, options ect
    		parseCommands();
    	}
    }
    
    /**
     * Sets all the flights
     * @param flights
     */
    public void setFlights(Collection flights)
    {
    	this.allFlights = flights;
    }
    
    /**
     * Parses the commands and sets the User Parameters, Selected Flights or Show Options
     * depending on the key word entered
     */
    private void parseCommands()
    {	
		String text = this.getText();
		
		// We first want to get all the text entered after the command prompt 'Enter Flight Command'
		int lastIndex = text.lastIndexOf(prompt);
		String result = text.substring(lastIndex + prompt.length());
		result = result.replaceAll("\n", "");
		
		/* Then take the command and pass it into the text parser which sets the properties
		 * that is used to store the entered commands*/
		TextParser textParser = new TextParser(result,this.allFlights);
		
		// If the user does not enter a correct format command than an error occurs and we display the error
		// also the user may type 'keylist' which also returns a message displaying a list of key words
		if(textParser.getTextType() == TextParser.ReturnType.Error || textParser.getTextType() == TextParser.ReturnType.KeyList)
			text += "\n" + textParser.getMessage();
		else if(textParser.getTextType() == TextParser.ReturnType.Valid) //If it's valid we need to set values
		{
			// Switch through the key words and reset the appropriate objects
			switch(textParser.getKeyWordEntered())
			{
				case SetParameters:
					setParameters(textParser);
					break;
				case EnableParameters:
					enableDisableParameters(textParser, true);
					break;
				case DisableParaneters:
					enableDisableParameters(textParser, false);
					break;
				case ShowFixes:
					this.showOpt.setShowFixesOption(getShowOptions(textParser.getValueEntered()));
					break;
				case ShowFlights:
					this.showOpt.setShowFlightsOption(getShowOptions(textParser.getValueEntered()));
					break;
				case ShowRoutes:
					this.showOpt.setShowRoutesOption(getShowOptions(textParser.getValueEntered()));
					break;
				case ShowTrajectories:
					this.showOpt.setShowTrajectoriesOption(getShowOptions(textParser.getValueEntered()));
					break;
				case Select:
					this.textClient.updateSelectedFlights(GetSelectFlights(textParser));
					break;
			}
		}
		
		// After we store the entered information prompt the user to enter flight command again
		text += "\nEnter Flight Command: ";
		
		this.setText(text);
    }
    /**
     * Gets all the Flights that are selected within the command prompt
     * @param textParser
     * @return
     */
    private Collection GetSelectFlights(TextParser textParser)
    {
    	Collection selectedFlights = new Vector();
    	boolean getAll = false;
    	
    	// The text parser parses all the flights and stores the flights in an array
    	String [] selFlights = textParser.getFlights();
    	
    	// If the user enetered a command to select all flights than we want all flights.
    	if(textParser.getValueEntered() == ValueType.All)
    		getAll = true;
    	
    	// We want to loop through all the current flights
    	Iterator flightIterator = this.allFlights.iterator();
    	
    	while(flightIterator.hasNext())
    	{
    		Flight flight = (Flight) flightIterator.next();
    		boolean includeFlight = true;
    		if(!getAll)
    		{
    			//Check to see if flight is one of the selected flights
    			for(int index = 0; index < selFlights.length; index++)
    			{
    				String selFlight = selFlights[index];
    				
    				includeFlight = flight.getAircraftId().toLowerCase().equals(selFlight);
    				break;
    			}
    		}
    		
    		//If Flight is included or we are selecting all than include in the selected flight list
    		if(includeFlight)
    			selectedFlights.add(flight);
    	}
    	
    	return selectedFlights;
    }
    
    /**
     * Gets the Show Options that the user entered for the key word
     * <p>
     * Should have probably used one enum for both the show options, if
     * I had more time I would have fixed it, but this seems like
     * an alight way to handle it for now.
     * @param valueType The value the user entered
     * @return 
     */
    private ShowOptions.Options getShowOptions(TextParser.ValueType valueType)
    {
    	ShowOptions.Options option = Options.ShowNone;
    	
    	//Switch through the show type and set the show option to what the value type is
    	switch(valueType)
    	{
    		case All:
    			option = Options.ShowAll;
    			break;
    		case Blundering:
    			option = Options.ShowBlundering;
    			break;
    		case Conforming:
    			option = Options.ShowConforming;
    			break;
    		case Selected:
    			option = Options.ShowSelected;
    			break;
    		case WithPlans:
    			option = Options.ShowWithPlan;
    			break;
    		case None:
    			option = Options.ShowNone;
    			break;
    	}
    	
    	return option;
    }
    
    /**
     * Enables and Disables the parameters
     * @param textParser
     * @param enable
     */
    private void enableDisableParameters(TextParser textParser, boolean enable)
    {
    	//Switch through which parameter the user enables/disables and stores
    	// it within the User Parameter object
    	switch(textParser.getParameterType())
    	{
    		case threslat:
    			this.parameters.cmLateralWeightOn = enable;
    			break;
    		case thresang:
    			this.parameters.cmAngularWeightOn = enable;
    			break;
    		case thresspe:
    			this.parameters.cmSpeedWeightOn = enable;
    			break;
    		case thresver:
    			this.parameters.cmVerticalWeightOn = enable;
    			break;
    	}
    }
    /**
     * Set the parameters
     * @param textParser
     */
    private void setParameters(TextParser textParser)
    {
    	//Gets the value the user sets
    	double value = textParser.getParameterValue();
    	
    	//Switch through the Parameter type the user choose to change, and sets the value
    	// within the User Parameter
    	switch(textParser.getParameterType())
    	{
    		case threslat:
    			this.parameters.cmLateralThreshold = value;
    			break;
    		case thresres:
    			this.parameters.cmResidualThreshold = value;
    			break;
    		case thresang:
    			this.parameters.cmAngularThreshold = value;
    			break;
    		case thresspe:
    			this.parameters.cmSpeedThreshold = value;
    			break;
    		case thresver:
    			this.parameters.cmVerticalThreshold = value;
    			break;
    		case horiztim:
    			this.parameters.tsTimeHorizon = (int)value;
    			break;
    	}
    }
}
