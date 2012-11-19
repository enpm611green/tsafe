import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Vector;

import org.junit.Test;

import tsafe.client.text_client.CommandPrompt;
import tsafe.client.text_client.TextParser;
import tsafe.common_datastructures.Flight;
import tsafe.common_datastructures.FlightTrack;

public class CommandPromptTest {
  @SuppressWarnings("unchecked")
  private Collection<Flight> SetUpCommandPrompt(Collection<Flight> flights) {
    CommandPrompt cp = new CommandPrompt(null, null, null);

    cp.setFlights(flights);
    TextParser tp = new TextParser("select id", flights);

    try {
      Method method = cp.getClass().getDeclaredMethod("GetSelectFlights",
          new Class[] { TextParser.class });
      method.setAccessible(true);
      
      return (Vector<Flight>) method.invoke(cp, tp);
    } catch (NoSuchMethodException e) {
      e.printStackTrace();
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (InvocationTargetException e) {
      e.printStackTrace();
    }
    return null;
  }
  
  @Test
  public void testGetSelectedFlightsNone() {
    Collection<Flight> fl = SetUpCommandPrompt(new Vector<Flight>());
    
    assertTrue(fl.size() == 0);
  }

  @Test
  public void testGetSelectedFlightsOne() {
    Vector<Flight> flights  = new Vector<Flight>();
    flights.add(new Flight("id", new FlightTrack(1, 1, 1, 1, 1, 1)));
    
    Collection<Flight> fl = SetUpCommandPrompt(flights);
    
    assertTrue(fl.size() == 1);
    assertTrue(fl.toArray()[0].equals(flights.get(0)));
  }
}
