package tsafe.client.text_client;

import java.awt.Color;
import java.awt.Dimension;
import java.text.DecimalFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Vector;

import javax.swing.JEditorPane;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTextPane;

import tsafe.client.SelectedFlights;
import tsafe.client.ShowOptions;
import tsafe.client.ShowOptions.Options;
import tsafe.common_datastructures.Fix;
import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.FlightTrack;
import tsafe.common_datastructures.Point4D;
import tsafe.common_datastructures.Route;
import tsafe.common_datastructures.Trajectory;

/**
 * The Flight text feed, displays a textual representation of what is being displayed on the map.
 * <p>
 * The text that is displayed is being displayed in html so that we can change the colors of
 * each individual line.
 * <p>
 * We also decided to only display three decimal places for the lat and lon values. We are
 * doing this because we feel that it is not visually appealing to the user to have too many decimal places.
 * Changing the decimal places would be an easy fix.
 * 
 *
 */
public class FlightTextFeed extends JEditorPane{

    /**
     * Data to draw to the screen
     */
    private Collection flights  = new LinkedList();
    private Collection blunders = new LinkedList();
    private Map flight2TrajMap = new HashMap();
    
    //We want to show the coordinates in Three places, otherwise it's difficult to read
    final DecimalFormat threePlaces = new DecimalFormat("0.000");

    // The selected flights to be displayed, if that option is set
    private SelectedFlights selectedFlights;
    
    // The Show Options the user choose to display 
    private ShowOptions showOpts;
  
    /**
     * Constructor:  Initializes the show options and selected flights that
     * will help displaying in the feed
     * @param showOptions The Show Options chosen by the user
     * @param selFlights	The Selected Flights chosen by the user
     */
	public FlightTextFeed(ShowOptions showOptions, SelectedFlights selFlights)
	{
		// Sets the Selected FLights and Show Options
		this.selectedFlights = selFlights;
		this.showOpts = showOptions;
		
		//We just initializes the html string
		String str = 
			"<html><body>" +
			"<p> </p>" +
			"<p> </p>" +
			"<p > </p>" +
			"<p>  </p>" +
			"</body> </html>";
		
		// We use html so that we can customize the colors of individual lines
		this.setContentType("text/html");
		
		// Sets the string
		this.setText(str);
		
		// We don't want the user editing the flight feed
		this.setEditable(false);
		
		//Using a black background so that the white text will display
		this.setBackground(Color.black);

		this.setPreferredSize(new Dimension(700, 200));
	}
    
	/**
	 * Updates the Text Feed with the latest flights.  
	 */
    public void updateFlightText()
    {	
    	StringBuffer flightText = new StringBuffer().append("<html><body>");
        
        // Loop through the flights and display the appropriate flights/information
        Iterator flightIter = flights.iterator();
        while(flightIter.hasNext()) {
        	
        	Flight flight = (Flight)flightIter.next();
            boolean hasFlightPlan = flight.getFlightPlan() != null;
            boolean isBlundering = hasFlightPlan && blunders.contains(flight);
            boolean isConforming = hasFlightPlan && !isBlundering;
            boolean isSelected = selectedFlights.getSelectedFlights().contains(flight);
        	
            Options showFlights = this.showOpts.getShowFlightsOption();
            Options showRoutes = this.showOpts.getShowRoutesOption();
            Options showTrajectories = this.showOpts.getShowTrajectoriesOption();
            
            // Display the flight if . . .
            if ((showFlights == Options.ShowAll) ||
                (showFlights == Options.ShowWithPlan  && hasFlightPlan) ||
                (showFlights == Options.ShowConforming && isConforming) ||
                (showFlights == Options.ShowBlundering && isBlundering) ||
                (showFlights == Options.ShowSelected   && isSelected)) {
                
            	flightText.append("<p style=\"color:");
            	
                /** Set the color of the flight and draw it */
                /** Set the color of the flight and draw it */
                if      (!hasFlightPlan) flightText.append("yellow\"> ");
                else if (isBlundering)   flightText.append("red\"> ");
                else /*(isConforming)*/  flightText.append("white\"> ");
                
                flightText.append("Flight ").append(flight.getAircraftId()).append(" ");
    
                flightText.append(getPositionString(flight));
                
                // Draw the route if . . .
                if (hasFlightPlan &&
                    ((showRoutes == Options.ShowAll) ||
                     (showRoutes == Options.ShowConforming && isConforming) ||
                     (showRoutes == Options.ShowBlundering && isBlundering) ||
                     (showRoutes == Options.ShowSelected   && isSelected))) {
                	
                	flightText.append(getRouteString(flight.getFlightPlan().getRoute()));
                }
                
                flightText.append(getConformanceString(isConforming));

                // Draw the trajectory if . . .
                if ((showTrajectories == Options.ShowAll) ||
                    (showTrajectories == Options.ShowWithPlan  && hasFlightPlan) ||
                    (showTrajectories == Options.ShowConforming && isConforming)  ||
                    (showTrajectories == Options.ShowBlundering && isBlundering)  ||
                    (showTrajectories == Options.ShowSelected    && isSelected)) {
                     /** Set the color of the trajectory and draw it */
                    
                	flightText.append(getTrajString((Trajectory)flight2TrajMap.get(flight)));
                }
        	
                flightText.append(" </p>");
            }
        }
        
        flightText.append("</body> </html>");
                
        //Displays the flights within the text feed
        this.setText(flightText.toString());
    }
    
    /**
     * Create a string for the flight position
     * @param conforming
     * @return
     */
    private String getConformanceString(boolean conforming)
    {
    	String str = "";
    	
    	if(conforming)
    		str = ", Conformance: YES";
    	else
    		str = ", Conformance: NO";
    	return str;
    }
    /**
     * Creates a String for the Current Flight Position to be displayed in the feed
     * @param flight
     * @return
     */
    private String getPositionString(Flight flight)
    {
    	StringBuffer position = new StringBuffer();
    	
    	FlightTrack flightTrack = flight.getFlightTrack();
    	if(flightTrack != null)
    	{
    		//Only displaying three places.  Makes it easier for the user to read
    		String lat = threePlaces.format(flightTrack.getLatitude());
    		String lon = threePlaces.format(flightTrack.getLongitude());
    		
	    	position.append("Position: ").append(lat).append(" ").append(lon).append(" ");
    	}
    	
    	return position.toString();
    	
    }
    /**
     * Creates a String for the Route to be displayed in the feed
     * @param route
     * @return
     */
    private String getRouteString(Route route)
    {
    	StringBuffer routeBuffer = new StringBuffer();
    	routeBuffer.append(", Route: (");
    	boolean first = true;
    	
    	// Loop through the route places and append each place to the string
    	Iterator routeIterator = route.fixIterator();
    	while(routeIterator.hasNext())
    	{
    		Fix fix = (Fix)routeIterator.next();
    		String lat = threePlaces.format(fix.getLatitude());
    		String lon = threePlaces.format(fix.getLongitude());
    		
    		if(!first){
    			routeBuffer.append(", ");
    		}
    		routeBuffer.append(lat).append(" ").append(lon);
    		
    		first = false;
    	}
    	
    	routeBuffer.append(")");
    	
    	return routeBuffer.toString();
    }
    /**
     * Creates a String for the Trajectory to be displayed in the feed
     * @param traj
     * @return
     */
    private String getTrajString(Trajectory traj)
    {
    	StringBuffer trajBuffer = new StringBuffer();
    	trajBuffer.append(", Trajectory: (");
    	boolean first = true;
    	
    	// Loops through the Points within the trajectory and append them in the trajectory string
    	Iterator pntIterator = traj.pointIterator();
    	while(pntIterator.hasNext())
    	{
    		Point4D point4d = (Point4D)pntIterator.next();

    		// Get Lat and Lon values and only display three places
    		String lat = threePlaces.format(point4d.getLatitude());
    		String lon = threePlaces.format(point4d.getLongitude());
    		
    		if(!first){
    			trajBuffer.append(", ");
    		}
    		trajBuffer.append(lat).append(" ").append(lon);
    		
    		first = false;
    	}
    	
    	trajBuffer.append(")");
    	
    	return trajBuffer.toString();
    }
    
    /**
     * Sets the flights that are currently available
     * @param allFlights
     */
    public void setFlights(Collection allFlights)
    {
    	this.flights = allFlights;
    }
    /**
     * Sets the blunders currently available
     * @param fBlunders
     */
    public void setBlunders(Collection fBlunders)
    {
    	this.blunders = fBlunders;
    }
    
    /** Sets the flight trajectory map in the pane */
    public void setFlightTrajectoryMap(Map flight2TrajMap) {
        this.flight2TrajMap = new HashMap(flight2TrajMap);
    }
}