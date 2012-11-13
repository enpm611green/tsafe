package tsafe.client;

import java.util.Collection;
import java.util.LinkedList;

/**
 * The Options on which types of flights, routes, fixes and Trajectories to
 * show within the map.
 * <p>
 * We needed to pull this functionality out of the graphical client package,
 * because the user will now have the functionality to change these options in the
 * command prompt also.
 * 
 */
public class ShowOptions {

	//Show Options
    public enum Options {ShowAll,ShowSelected,ShowWithPlan,ShowConforming,ShowBlundering,ShowNone};
    
    // Set the default values for the different options
    private Options showFixes = Options.ShowNone;
    private Options showFlights = Options.ShowAll;
    private Options showRoutes = Options.ShowAll;
    private Options showTrajectories = Options.ShowAll;
    
    /**
     * Sets the Show fixes options.  Only use ShowAll & ShowNone
     * <p>
     * Note: Changing these values should change the selected items in all clients.
     * @param opt
     */
    public void setShowFixesOption(Options opt)
    {
    	showFixes = opt;
    }
    /**
     * Gets the Shot Fixes Options including: ShowAll, ShowNone
     * @return
     */
    public Options getShowFixesOption()
    {
    	return showFixes;
    }
    /**
     * Sets the Show Flights options.  Only use ShowAll,ShowSelected,ShowWithPlan,ShowConforming,ShowBlundering,ShowNone
     * <p>
     * Note: Changing these values should change the selected items in all clients.
     * @param opt
     */
    public void setShowFlightsOption(Options opt)
    {
    	showFlights = opt;
    }
    /**
     * Gets the Show Flights Options including: ShowAll,ShowSelected,ShowWithPlan,ShowConforming,ShowBlundering,ShowNone
     * @return
     */
    public Options getShowFlightsOption()
    {
    	return showFlights;
    }
    /**
     * Sets the Show Routes options.  Only use ShowAll,ShowSelected,ShowConforming,ShowBlundering,ShowNone
     * <p>
     * Note: Changing these values should change the selected items in all clients.
     * @param opt
     */
    public void setShowRoutesOption(Options opt)
    {
    	showRoutes = opt;
    }
    /**
     * Gets the Show Routes Options including: ShowAll,ShowSelected,ShowConforming,ShowBlundering,ShowNone
     * @return
     */
    public Options getShowRoutesOption()
    {
    	return showRoutes;
    }
    /**
     * Sets the Show Trajectories options.  Only use ShowAll,ShowSelected,ShowWithPlan,ShowConforming,ShowBlundering,ShowNone
     * <p>
     * Note: Changing these values should change the selected items in all clients.
     * @param opt
     */
    public void setShowTrajectoriesOption(Options opt)
    {
    	showTrajectories = opt;
    }
    /**
     * Gets the Show Trajectories Options including: ShowAll,ShowSelected,ShowWithPlan,ShowConforming,ShowBlundering,ShowNone
     * @return
     */
    public Options getShowTrajectoriesOption()
    {
    	return showTrajectories;
    }
    
}
