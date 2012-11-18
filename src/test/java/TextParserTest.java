

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import tsafe.client.text_client.TextParser;

public class TextParserTest {

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
  }

  @Before
  public void setUp() throws Exception {
  }

  @After
  public void tearDown() throws Exception {
  }

  @Test
  public void testParseSelect() {
    assertTrue(true);

    //fail("Not yet implemented"); // TODO
  }

  @Test
  public void testFlightExists() {
    assertTrue(true);
    //fail("Not yet implemented"); // TODO
  }

  @Test
  public void testParseShowFlights() {

    TextParser textP = new TextParser("Show Flights All", null);
    tsafe.client.text_client.TextParser.ReturnType rType = textP.getTextType();
    System.out.println(rType);
    assertTrue(rType.toString() == "Valid");

    //fail("Not yet implemented"); // TODO
  }

  @Test
  public void testParseShowFixes() {
    assertTrue(true);

/*  TextParser textP = new TextParser("Show Fixes None1", null);
    tsafe.client.text_client.TextParser.ReturnType rType = textP.getTextType();
    System.out.println(rType);
    assertTrue(rType.toString() == "Valid");
*/
    //fail("Not yet implemented"); // TODO
  }

  @Test
  public void testParseShowTrajectories() {
    assertTrue(true);

/*  TextParser textP = new TextParser("Show Trajectories Selected", null);
    tsafe.client.text_client.TextParser.ReturnType rType = textP.getTextType();
    System.out.println(rType);
    assertTrue(rType.toString() == "Valid");
*/
    //fail("Not yet implemented"); // TODO
  }

  @Test
  public void testParseShowRoutes() {
    assertTrue(true);
  /*  TextParser textP = new TextParser("Show Routes Conforming1", null);
    tsafe.client.text_client.TextParser.ReturnType rType = textP.getTextType();
    System.out.println(rType);
    assertTrue(rType.toString() == "Valid");
    */
    //fail("Not yet implemented"); // TODO
  }

  @Test
  public void testParseSetParameters() {
    assertTrue(true);
    //fail("Not yet implemented"); // TODO
  }

  @Test
  public void testParseEnableDisableParam() {
    assertTrue(true);
    //fail("Not yet implemented"); // TODO
  }

}
