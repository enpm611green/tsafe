package tsafe.client.text_client;

import java.util.Collection;
import java.util.Iterator;

import tsafe.common_datastructures.Flight;

/**
 * This class Parses the text entered by the user within the command prompt to
 * determine if the entry is valid and what kind of parameters/flights are suppose
 * to be displayed.
 * 
 *
 */
public class TextParser {
	
	// Three options for a return type
	public enum ReturnType {Valid,KeyList,Error};
	
	// An enum representing all the key word options and user can choose
	public enum KeyWordType {KeyList,Select,ShowFlights,ShowFixes,ShowRoutes,ShowTrajectories,SetParameters,EnableParameters,DisableParaneters,Error};
	
	// The Value types for the Key Words
	public enum ValueType {All,Selected,WithPlans,Conforming,Blundering,None};
	
	// The Parameter options
	public enum ParameterType{threslat, thresver, thresang, thresspe, thresres, horiztim};
	
	//The message Prompted to the user
	private String message = ""; 
	
	//The selected flights (Only if user choose flights)
	private String [] selectedFlights;
	
	//The Return type
	private ReturnType returnType;
	
	//The KeyWord Type
	private KeyWordType keyWordType;
	
	//The Variable Type Choosen
	private ValueType valueType;
	
	// The Parameter type
	private ParameterType paramType;
	
	// The Parameter Value
	private double parameterValue;
	
	// ALl list of the possible flights a user can select
	private Collection flights;
	
	/**
	 * Constructor:  Passes in the text statement that the user entered. 
	 * The class than parses the entered statement and determines
	 * what the keywords and
	 * @param text	The Command Entered
	 * @param allFlights	The Collection of 'Flight' objects to be selected
	 */
	public TextParser(String text, Collection allFlights)
	{
		this.flights = allFlights;
		
		text = text.toLowerCase().trim();
		
		// An empty string is an invalid command
		if(text.isEmpty())
		{
			returnType = ReturnType.Error;
			message = "Invalid Entry";
		}
		else if(text.equals("list")) // When 'list' is entered we display the list of key words
		{
			returnType = ReturnType.KeyList;
			message = getKeyList();
		}
		else if(text.startsWith("select "))  // Select the flights
			parseSelect(text);
		else if(text.startsWith("show flights ")) // Parse Show Flights Option
			this.parseShowFlights(text);
		else if(text.startsWith("show fixes ")) // Parse Show Fixes Option
			this.parseShowFixes(text);
		else if(text.startsWith("show routes ")) // Parse Show Routes Option
			this.parseShowRoutes(text);
		else if(text.startsWith("show trajectories "))  // Parse Show Trajectories Option
			this.parseShowTrajectories(text);
		else if(text.startsWith("set parameter "))  // Parse Set Parameter Value
			parseSetParameters(text);
		else if(text.startsWith("enable parameter "))  // Parse Enable Parameter Value
			parseEnableDisableParam(text, true);
		else if(text.startsWith("disable parameter "))  // Parse Disable Parameter Value
			parseEnableDisableParam(text, false);
		else
		{
			// The list above was the only allowed key words all others result in an error
			returnType = ReturnType.Error;
			message = "Invalid Entry.  Type 'list' for a list of valid entries";
		}
		
	}
	
	/**
	 * Returns a string with the proper syntax of the key words that can be entered
	 * @return
	 */
	private String getKeyList()
	{
		return "Key Words:\n" +
		"Select <flightid> \nshow fixes [all /  none]\n" +
		"Show Flights [ all | selected | withplans | conforming | blundering | none ]\n" + 
		"Show Routes [ all | selected | conforming | blundering | none ]\n" +
		"Show Trajectories [ all | selected | withplans | conforming | blundering | none ]\n" +
		"Set Parameter <parameter name> <value>\n" +
		"Enable Parameter <parameter name>\n" +
		"Disable Parameter <parameter name>\n\n";
	}
	
	/**
	 * Parses the select statement entered.  We also check to make sure
	 * that all the flights entered are valid.
	 * @param input
	 */
	private void parseSelect(String input)
	{
		this.keyWordType = KeyWordType.Select;
		String ids = input.substring(6).trim();
		
		// Must contain at least one flight
		if(ids.isEmpty())
		{
			returnType = ReturnType.Error;
			message = "Invalid Select Statement.  Please use the following syntax: 'Select <flightid>' ";
		}
		else if(ids.equals("*"))  // Represents all flights
		{
			this.returnType = ReturnType.Valid;
			this.valueType = ValueType.All;
		}
		else
		{
			// Get all the flights entered.  
			String [] flights = ids.split(" ");
			
			// All flights must exist in order to select the flights
			boolean allFlightsExist = true;
			StringBuffer missingFlights = new StringBuffer();
			for(String flight: flights)
			{
				if(!flightExists(flight))
				{
					allFlightsExist = false;
					missingFlights.append("'").append(flight).append("' ");
				}
			}
			
			// If all flights exist set the Value, return type and all the selected flights
			if(allFlightsExist)
			{
				this.returnType = ReturnType.Valid;
				
				this.selectedFlights = flights;
				this.valueType = ValueType.Selected;
			}
			else // Some of the Flights attempted selected did not exist
			{
				this.returnType = ReturnType.Error;
				this.message = "Invalid Select Statement.\n" +
				"The following flights did not exist: " + missingFlights + " " +
				"\nPlease try again.\n\n";
			}
		}
	}
	
	/**
	 * Checks if the flight entered is a valid flights
	 * @param strflight
	 * @return
	 */
	private boolean flightExists(String strflight)
	{
		boolean containsFlight = false;
		
		// Loops through all the possible flights and sees if the selected flight exists
		Iterator flightIterator = this.flights.iterator();
		while(flightIterator.hasNext())
		{
			Flight flight = (Flight)flightIterator.next();
			
			if(flight.getAircraftId().toLowerCase().equals(strflight)) {
				containsFlight = true;
				break;
			}
			
		}
		
		return containsFlight;
	}
	/**
	 * Parses the Show Flights statement and stores what
	 * the variable that was entered.  If the variable is
	 * not entered correctly user gets a friendly error message.
	 * @param input
	 */
	private void parseShowFlights(String input)
	{
		// Trim out the substring for 'Show Flights '
		String showFlights = input.substring(13).trim();
		
		// Sets that the return type is valid and the key word is Show Flights
		this.returnType = ReturnType.Valid;
		this.keyWordType = KeyWordType.ShowFlights;
		
		// Determine which value type was entered for show flights
		if(showFlights.equals("all"))
			this.valueType = ValueType.All;
		else if(showFlights.equals("selected"))
			this.valueType = ValueType.Selected;
		else if(showFlights.equals("withplans"))
			this.valueType = ValueType.WithPlans;
		else if(showFlights.equals("conforming"))
			this.valueType = ValueType.Conforming;
		else if(showFlights.equals("blundering"))
			this.valueType = ValueType.Blundering;
		else if(showFlights.equals("none"))
			this.valueType = ValueType.None;
		else
		{
			// If all the options above were not entered than the user did not choose a correct option
			returnType = ReturnType.Error;
			
			// Display the possible show options
			message = "Invalid Show Flights Statement.\n" +
					  "Please use the following syntax: 'show flights [ all | selected | withplans | conforming | blundering | none ]' ";
		}		
	}
	/**
	 * Parses the Show Fixes Command Statement.  Makes sure that the 
	 * variables entered are correct.
	 * @param input	The input command
	 */
	private void parseShowFixes(String input)
	{
		// Trim the substring for 'Show fixes'
		String showFixes = input.substring(11).trim();
		
		// Sets that the return type is valid and the key word is Show Fixes
		this.returnType = ReturnType.Valid;
		this.keyWordType = KeyWordType.ShowFixes;
		
		// Only 'All' or 'None' are the options for show fixes
		if(showFixes.equals("all"))
			this.valueType = ValueType.All;
		else if(showFixes.equals("none"))
			this.valueType = ValueType.None;
		else
		{
			returnType = ReturnType.Error;
			message = "Invalid Show Fixes Statement.\n" +
					  "Please use the following syntax: 'show fixes [all /  none]' ";
		}
	}
	/**
	 * Parses the Show Trajectories statement and makes sure that
	 * all the variables was entered correctly
	 * @param input
	 */
	private void parseShowTrajectories (String input)
	{
		// Trim the substring of "Show Trajectories "
		String showTraj = input.substring(18).trim();
		
		this.returnType = ReturnType.Valid;
		this.keyWordType = KeyWordType.ShowTrajectories;
		
		//The Show Trajectories Options, we set those values accordingly
		if(showTraj.equals("all"))
			this.valueType = ValueType.All;
		else if(showTraj.equals("selected"))
			this.valueType = ValueType.Selected;
		else if(showTraj.equals("withplans"))
			this.valueType = ValueType.WithPlans;
		else if(showTraj.equals("conforming"))
			this.valueType = ValueType.Conforming;
		else if(showTraj.equals("blundering"))
			this.valueType = ValueType.Blundering;
		else if(showTraj.equals("none"))
			this.valueType = ValueType.None;
		else
		{
			returnType = ReturnType.Error;
			message = "Invalid Show Trajectories  Statement.\n" +
					  "Please use the following syntax: 'show trajectories [ all | selected | withplans | conforming | blundering | none ]' ";
		}	
	}
	/**
	 * Parses the Show Routes statement and makes sure that
	 * all the variables was entered correctly
	 * @param input
	 */
	private void parseShowRoutes(String input)
	{
		// Trim the Show Routes Command
		String showRoutes = input.substring(12).trim();
		this.returnType = ReturnType.Valid;
		this.keyWordType = KeyWordType.ShowRoutes;
		
		// The show routes options and sets accordingly
		if(showRoutes.equals("all"))
			this.valueType = ValueType.All;
		else if(showRoutes.equals("selected"))
			this.valueType = ValueType.Selected;
		else if(showRoutes.equals("conforming"))
			this.valueType = ValueType.Conforming;
		else if(showRoutes.equals("blundering"))
			this.valueType = ValueType.Blundering;
		else if(showRoutes.equals("none"))
			this.valueType = ValueType.None;
		else
		{
			returnType = ReturnType.Error;
			message = "Invalid Show Routes Statement.\n" +
					  "Please use the following syntax: 'show routes [ all | selected | conforming | blundering | none ]' ";
		}	
	}
	/**
	 * Parses the Set Parameters statement to get the 
	 * Parameter and the value being entered
	 * @param input
	 */
	private void parseSetParameters(String input)
	{
		// Gets the substring for the parameter entered
		String setParam = input.substring(14).trim();
		
		this.returnType = ReturnType.Valid;
		this.keyWordType = KeyWordType.SetParameters;
		
		// Determine which parameter that is selected
		if(setParam.startsWith("threslat "))
			this.paramType = ParameterType.threslat;
		else if(setParam.startsWith("thresver "))
			this.paramType = ParameterType.thresver;
		else if(setParam.startsWith("thresang "))
			this.paramType = ParameterType.thresang;
		else if(setParam.startsWith("thresspe "))
			this.paramType = ParameterType.thresspe;
		else if(setParam.startsWith("thresres "))
			this.paramType = ParameterType.thresres;
		else if(setParam.startsWith("horiztim "))
		{
			// The horiztim must be an integer, therefore if user enters a decimal place it is not valid
			String horzValue = setParam.substring(9).trim();
			if(horzValue.contains("."))
			{
				returnType = ReturnType.Error;
				message = "Invalid Set Parameters Statement.\n" +
						  "horiztim must be an integer' ";
			}
			else
				this.paramType = ParameterType.horiztim;
		}
		else
		{
			returnType = ReturnType.Error;
			message = "Invalid Set Parameters Statement.\n" +
					  "Please use the following syntax: 'set parameter <parameter name> <value>' ";
		}
		
		//If the user entered the correct parameter we need to get the double
		//value associated with this parameter
		if(this.returnType == ReturnType.Valid)
		{
			String parmValueString = setParam.substring(9).trim();
			
			try
			{
				// It does not appear that the Parameter Dialog, does not do any error checking
				// on numbers, i.e neg values ect. therefore we are not
				this.parameterValue = Double.parseDouble(parmValueString);
			}
			catch(NumberFormatException ex)
			{
				returnType = ReturnType.Error;
				message = "Invalid Set Parameters Statement.\n" +
				  "The Value entered was not a number.  Please try again";
			}
		}
	}
	/**
	 * Parses the Enable and Disable parameter command to make
	 * sure the parameters can be enabled or disabled.
	 * @param input
	 * @param enable True if 'enable parameter' False if 'disable parameter'
	 */
	private void parseEnableDisableParam(String input, boolean enable)
	{
		// Gets the substring not including the 'Set Parameter' part
		String enableParameter = input.substring(17).trim();
		this.returnType = ReturnType.Valid;
		
		if(enable)
			this.keyWordType = KeyWordType.EnableParameters;
		else
			this.keyWordType = KeyWordType.DisableParaneters;
		
		// The Parameter types
		if(enableParameter.equals("threslat"))
			this.paramType = ParameterType.threslat;
		else if(enableParameter.equals("thresver"))
			this.paramType = ParameterType.thresver;
		else if(enableParameter.equals("thresang"))
			this.paramType = ParameterType.thresang;
		else if(enableParameter.equals("thresspe"))
			this.paramType = ParameterType.thresspe;
		else if(enableParameter.equals("thresres") || enableParameter.equals("horiztim"))
		{
			// Thresres and horiztim can not be enabled/disabed
			returnType = ReturnType.Error;
			
			if(enable)
			{
				message = "Invalid Enable Statement.\n" +
					      "thresres and horiztim cannot be enabled' ";
			}
			else
			{
				message = "Invalid Disable Statement.\n" +
						  "thresres and horiztim cannot be disabled' ";
			}
			
		}
		else
		{
			returnType = ReturnType.Error;
			
			if(enable)
			{
				message = "Invalid Enable Parameter Statement.\n" +
					  "Please use the following syntax: 'enable parameter <parameter name>' ";
			}
			else
			{
				message = "Invalid Disable Parameter Statement.\n" +
				  "Please use the following syntax: 'disable parameter <parameter name>' ";
			}
				
		}	
	}

	/**
	 * The Return type, can be three options.
	 * Valid: Meaning that everything was entered correctly
	 * Error: Meaning an error happened and you need to prompt user with the message
	 * KeyWords:  Meaning the user wants a list of keywords which is stored in the message
	 * @return
	 */
	public  ReturnType getTextType()
	{
		return returnType;
	}
	/**
	 * An Enum with the keyword that the user choose
	 * @return
	 */
	public KeyWordType getKeyWordEntered()
	{
		return this.keyWordType;
	}
	/**
	 * The Value type that the user entered that is associated with the keyword
	 * @return
	 */
	public ValueType getValueEntered()
	{
		return this.valueType;
	}
	/**
	 * The selected flights by the user.  This is only populated if the
	 * KeyWord is Flights and the ValueType entered is selected.
	 * @return
	 */
	public String[] getFlights()
	{
		return this.selectedFlights;
	}
	/**
	 * The Error Message or the KeyWord list.  Only is populated if an error
	 * Occurred or user typed 'list'
	 * @return
	 */
	public String getMessage()
	{
		return message;
	}
	
	/**
	 * Returns the parameter type that was entered into the text prompt
	 * @return
	 */
	public ParameterType getParameterType()
	{
		return this.paramType;
	}
	
	/**
	 * If the set parameter command was being called, the value that the user was setting.
	 * @return
	 */
	public double getParameterValue()
	{
		return this.parameterValue;
	}

}
