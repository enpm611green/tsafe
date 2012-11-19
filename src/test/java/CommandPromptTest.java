import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.Vector;

import org.junit.Test;

import tsafe.client.text_client.CommandPrompt;
import tsafe.client.text_client.TextParser;
import tsafe.common_datastructures.Flight;

public class CommandPromptTest {
  @Test
  public void testGetSelectedFlightsNone() {

    CommandPrompt cp = new CommandPrompt(null, null, null);
    TextParser tp = new TextParser("", new Vector<Flight>());

    try {
      Method method = cp.getClass().getDeclaredMethod("GetSelectFlights",
          new Class[] { TextParser.class });
      method.setAccessible(true);
      @SuppressWarnings("unchecked")
      Collection<Flight> c = (Collection<Flight>) method.invoke(cp, tp);
      assertTrue(c.size() == 0);
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
  }

}
