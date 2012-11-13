package tsafe.client;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Vector;

/**
 * This class allows us to communicate the selected flights with all the clients.
 * <p>
 * In some cases we change in selected flight in either the map or command promt and this
 * way we can keep track of all the selected items between interfaces.
 * 
 */
public class SelectedFlights {

	// The flights selected
    private Collection selectedFlights;
    
    /**
     * Constructor: Initalizes the selected flights collection
     */
    public SelectedFlights()
    {
    	this.selectedFlights = new Vector();
    }

    /**
     * Sets the selected flights
     * @param selFlights
     */
    public void setSelectedFlights(Collection selFlights)
    {
    	this.selectedFlights = selFlights;
    }
    
    /**
     * Gets the selected flights
     * @return
     */
    public Collection getSelectedFlights()
    {
    	return this.selectedFlights;
    }
    
}
