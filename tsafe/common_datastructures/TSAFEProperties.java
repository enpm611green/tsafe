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

package tsafe.common_datastructures;

import java.awt.Dimension;
import java.awt.Point;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.Hashtable;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import java.util.Vector;

import fig.io.FIGFileContentFilter;

/**
 * The TSAFE application's properties.
 */
public class TSAFEProperties {


	//
	// CONSTANTS
	//

    /**
     * The location of the user's property file.
     */
    private final static String TSAFE_PROPERTIES_FILE = "tsafe.properties";

    /**
     * The location of TSAFE's default property file.
     */
    private final static String DEFAULT_TSAFE_PROPERTIES_FILE = "default_tsafe.properties";



    //
    // CLASS VARIABLES
    //

    /**
     * TSAFE's properties.
     */
    private static Properties props = null;
    static {
        restoreAllDefaults();
    }



	//
	// GLOBAL METHODS
	//      

    //-------------------------------------------
    /**
     * Restore all properties to their default values.
     */
    public static void restoreAllDefaults() {

        // First, reload the properties file.
        loadPropertiesFromFile();

        // Then, call each property's "restore" method here 
        // (don't bother reloading the properties file).
        restoreDefaultDataFiles(false);
        restoreDefaultFeedSource(false);
        restoreDefaultBackgroundImage(false);
        restoreDefaultLatitudeConstraints(false);
        restoreDefaultLongitudeConstraints(false);
        restoreDefaultShowSplashScreenFlag(false);
        restoreDefaultRememberWindowSizesFlag(false);
        restoreDefaultRememberWindowLocationsFlag(false);
        restoreDefaultContentFilter(false);
        restoreDefaultFeedDirectory(false);
        restoreDefaultFeedSources(false);
        restoreDefaultAirlineDataVector(false);
        restoreDefaultFacilityDataVector(false);
        restoreDefaultMessageTypeDataVector(false);
    }


    //-------------------------------------------
    /**
     * Save current values of all properties as the default values.
     */
    public static void saveAllAsDefaults() {
        // Call each property's "save" method here (don't
        // bother saving values to file yet).
        saveDataFilesAsDefault(false);
        saveFeedSourceAsDefault(false);
        saveBackgroundImageAsDefault(false);
        saveLatitudeConstraintsAsDefault(false);
        saveLongitudeConstraintsAsDefault(false);
        saveShowSplashScreenFlagAsDefault(false);
        saveRememberWindowSizesFlagAsDefault(false);
        saveRememberWindowLocationsFlagAsDefault(false);
        saveContentFilterAsDefault(false);
        saveFeedDirectoryAsDefault(false);
        saveFeedSourcesAsDefault(false);
        saveAirlineDataVectorAsDefault(false);
        saveFacilityDataVectorAsDefault(false);
        saveMessageTypeDataVectorAsDefault(false);

        // Now, save all property values to file.
        savePropertiesToFile();
    }


    //-------------------------------------------
    /**
     * Loads the properties from file.
     */
    private static void loadPropertiesFromFile() {

        // Load the default properties.  Load the file as a resource since it may
        // reside in a JAR file.
        Properties defaultProps = new Properties();
        try {
            InputStream defaultPropsFile = TSAFEResourceAnchor.getInputStream(DEFAULT_TSAFE_PROPERTIES_FILE);
            defaultProps.load(defaultPropsFile);
            defaultPropsFile.close();
        }
        catch (Exception e) {
            System.err.println("Could not load default properties file: " + e);
        }

        // Now load the user properties.
        props = new Properties(defaultProps);
        try {
            FileInputStream userPropsFile = new FileInputStream(TSAFE_PROPERTIES_FILE);
            props.load(userPropsFile);
            userPropsFile.close();        
        }
        catch (Exception e) {
            System.err.println("Could not load user properties file: " + e);
        }
    }


    //-------------------------------------------
    /**
     * Save current property values to file.
     */
    private static void savePropertiesToFile() {
        try {
            FileOutputStream propsFile = new FileOutputStream(TSAFE_PROPERTIES_FILE);
            props.store(propsFile, "TSAFE Properties File");
            propsFile.close();
        }
        catch (Exception e) {
            System.err.println("Could not write properties file: " + e);
        }
    }




    //
    // PROPERTY METHODS
    //


    // DATA FILES ////////////////////////////////////////////////////

    /**
     * The list of data files used by TSAFE.
     */
    private static String[] dataFiles;

    /**
     * The list of data files used by TSAFE.
     */
    private final static String PROP_DATA_FILES = "dataFiles";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultDataFiles() {
        restoreDefaultDataFiles(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultDataFiles(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedFiles = props.getProperty(PROP_DATA_FILES);
        if ((serializedFiles == null) || (serializedFiles.equals(""))) {
            dataFiles = new String[0];
        }

        // De-serialize the list of data files.
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedFiles));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                dataFiles = (String[]) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize data files: " + e);
                dataFiles = new String[0];
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveDataFilesAsDefault() {
        saveDataFilesAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveDataFilesAsDefault(boolean saveToFile) {        

        // Serialize the list of data files into string format.
        String serializedFiles = "";
        try {
            ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
            oos.writeObject(dataFiles);
            oos.flush();
            serializedFiles = new String(convertBytesToString(serializedStream.toByteArray()));
            oos.close();
        }
        catch (Exception e) {
            System.err.println("Could not serialize data files: " + e);
            serializedFiles = "";
        }


        props.setProperty(PROP_DATA_FILES, serializedFiles);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the list of data files.
     *
     * @return  an array containing the data file names
     */
    public static String[] getDataFiles() {
        return ((String[]) dataFiles.clone());
    }

    //-------------------------------------------
    /**
     * Set the list of data files.
     *
     * @param newDataFiles  an array of new data file names
     */
    public static void setDataFiles(String[] newDataFiles) {
        dataFiles = newDataFiles;
        // Translate a null array into the empty array!
        if (dataFiles == null) {
            dataFiles = new String[0];
        }
    }   



    // FEED SOURCE ///////////////////////////////////////////////////

    /**
     * The chosen feed source in the configuration.
     */
    private static Reader feedSource;

    /**
     * The chosen feed source in the configuration.
     */
    private final static String PROP_FEED_SOURCE = "feedSource";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultFeedSource() {
        restoreDefaultFeedSource(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultFeedSource(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedSource = props.getProperty(PROP_FEED_SOURCE);        
        if ((serializedSource == null) || (serializedSource.equals(""))) {
            feedSource = null;
        }

        // De-serialize the feed source.
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedSource));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                feedSource = (Reader) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize feed source: " + e);
                feedSource = null;
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveFeedSourceAsDefault() {
        saveFeedSourceAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveFeedSourceAsDefault(boolean saveToFile) {        

        // Serialize the feed source into string format.
        String serializedSource = "";
        if (feedSource != null) {
            try {
                ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
                oos.writeObject(feedSource);
                oos.flush();
                serializedSource = new String(convertBytesToString(serializedStream.toByteArray()));
                oos.close();
            }
            catch (Exception e) {
                System.err.println("Could not serialize feed source: " + e);
                serializedSource = "";
            }
        }


        props.setProperty(PROP_FEED_SOURCE, serializedSource);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the chosen feed source.
     *
     * @return  the feed source
     */
    public static Reader getFeedSource() {
        return feedSource;
    }

    //-------------------------------------------
    /**
     * Set the chosen feed source.
     *
     * @param newFeedSource  the new feed source Reader
     */
    public static void setFeedSource(Reader newFeedSource) {
        feedSource = newFeedSource;
    }   



    // BACKGROUND IMAGE //////////////////////////////////////////////

    /**
     * The background map image filename.
     */
    private static String backgroundImage;

    /**
     * The background map image filename.
     */
    private final static String PROP_BACKGROUND_IMAGE = "backgroundImage";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultBackgroundImage() {
        restoreDefaultBackgroundImage(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultBackgroundImage(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        backgroundImage = props.getProperty(PROP_BACKGROUND_IMAGE, "");
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveBackgroundImageAsDefault() {
        saveBackgroundImageAsDefault(true);
    }
    
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveBackgroundImageAsDefault(boolean saveToFile) {        
        if (backgroundImage == null) {
            backgroundImage = "";
        }
        props.setProperty(PROP_BACKGROUND_IMAGE, backgroundImage);

        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the background image filename.
     *
     * @return  the background image filename
     */
    public static String getBackgroundImage() {
        return backgroundImage;
    }

    //-------------------------------------------
    /**
     * Set the background image filename.
     *
     * @param newImage  the new background image filename
     */
    public static void setBackgroundImage(String newImage) {        
        backgroundImage = newImage;
    }



    // LATITUDE CONSTRAINTS //////////////////////////////////////////

    /**
     * The latitude constraints.
     */
    private static String[] latConstraints;

    /**
     * The latitude constraints.
     */
    private final static String PROP_LATITUDE_CONSTRAINTS = "latConstraints";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultLatitudeConstraints() {
        restoreDefaultLatitudeConstraints(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultLatitudeConstraints(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedConstraints = props.getProperty(PROP_LATITUDE_CONSTRAINTS);
        if ((serializedConstraints == null) || (serializedConstraints.equals(""))) {
            latConstraints = new String[0];
        }

        // De-serialize the list of constraints.
        else {
            try {
                ByteArrayInputStream serializedStream = 
                    new ByteArrayInputStream(convertStringToBytes(serializedConstraints));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                latConstraints = (String[]) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize latitude constraints: " + e);
                latConstraints = new String[0];
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveLatitudeConstraintsAsDefault() {
        saveLatitudeConstraintsAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveLatitudeConstraintsAsDefault(boolean saveToFile) {        

        // Serialize the list of constraints into string format.
        String serializedConstraints = "";
        try {
            ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
            oos.writeObject(latConstraints);
            oos.flush();
            serializedConstraints = new String(convertBytesToString(serializedStream.toByteArray()));
            oos.close();
        }
        catch (Exception e) {
            System.err.println("Could not serialize latitude constraints: " + e);
            serializedConstraints = "";
        }


        props.setProperty(PROP_LATITUDE_CONSTRAINTS, serializedConstraints);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the list of latitude constraints.
     *
     * @return  an array containing the latitude constraints
     */
    public static String[] getLatitudeConstraints() {
        return ((String[]) latConstraints.clone());
    }

    //-------------------------------------------
    /**
     * Set the list of latitude constraints.
     *
     * @param newLatitudeConstraints  an array of new latitude constraints
     */
    public static void setLatitudeConstraints(String[] newLatitudeConstraints) {
        latConstraints = newLatitudeConstraints;
        // Translate a null array into the empty array!
        if (latConstraints == null) {
            latConstraints = new String[0];
        }
    }
    
    public static LatLonBounds getLatLonBounds(){
    	double minLat = 0;
    	double minLon = 0;
    	double maxLat = 0;
    	double maxLon = 0; 
    	
    	try {
			int minLatDegrees = Integer.parseInt(latConstraints[0]);
			int minLatMinutes = Integer.parseInt(latConstraints[1]);
			int minLatSign = latConstraints[2].charAt(0) == 'N' ? 1 : -1;
			int maxLatDegrees = Integer.parseInt(latConstraints[3]);
			int maxLatMinutes = Integer.parseInt(latConstraints[4]);
			int maxLatSign = latConstraints[5].charAt(0) == 'N' ? 1 : -1;

			int minLonDegrees = Integer.parseInt(lonConstraints[0]);
			int minLonMinutes = Integer.parseInt(lonConstraints[1]);
			int minLonSign = lonConstraints[2].charAt(0) == 'E' ? 1 : -1;
			int maxLonDegrees = Integer.parseInt(lonConstraints[3]);
			int maxLonMinutes = Integer.parseInt(lonConstraints[4]);
			int maxLonSign = lonConstraints[5].charAt(0) == 'E' ? 1 : -1;

			minLat = minLatSign * (minLatDegrees + (minLatMinutes / 60.0));
			minLon = minLonSign * (minLonDegrees + (minLonMinutes / 60.0));
			maxLat = maxLatSign * (maxLatDegrees + (maxLatMinutes / 60.0));
			maxLon = maxLonSign * (maxLonDegrees + (maxLonMinutes / 60.0));

		} catch (NumberFormatException e) {
		}

		return new LatLonBounds(minLat, minLon, maxLat, maxLon);
    }



    // LONGITUDE CONSTRAINTS /////////////////////////////////////////

    /**
     * The longitude constraints.
     */
    private static String[] lonConstraints;

    /**
     * The longitude constraints.
     */
    private final static String PROP_LONGITUDE_CONSTRAINTS = "lonConstraints";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultLongitudeConstraints() {
        restoreDefaultLongitudeConstraints(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultLongitudeConstraints(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedConstraints = props.getProperty(PROP_LONGITUDE_CONSTRAINTS);
        if ((serializedConstraints == null) || (serializedConstraints.equals(""))) {
            lonConstraints = new String[0];
        }

        // De-serialize the list of constraints.
        else {
            try {
                ByteArrayInputStream serializedStream = 
                    new ByteArrayInputStream(convertStringToBytes(serializedConstraints));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                lonConstraints = (String[]) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize longitude constraints: " + e);
                lonConstraints = new String[0];
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveLongitudeConstraintsAsDefault() {
        saveLongitudeConstraintsAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveLongitudeConstraintsAsDefault(boolean saveToFile) {        
        // Serialize the list of constraints into string format.
        String serializedConstraints = "";
        try {
            ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
            oos.writeObject(lonConstraints);
            oos.flush();
            serializedConstraints = new String(convertBytesToString(serializedStream.toByteArray()));
            oos.close();
        }
        catch (Exception e) {
            System.err.println("Could not serialize longitude constraints: " + e);
            serializedConstraints = "";
        }


        props.setProperty(PROP_LONGITUDE_CONSTRAINTS, serializedConstraints);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the list of longitude constraints.
     *
     * @return  an array containing the longitude constraints
     */
    public static String[] getLongitudeConstraints() {
        return ((String[]) lonConstraints.clone());
    }

    //-------------------------------------------
    /**
     * Set the list of longitude constraints.
     *
     * @param newLongitudeConstraints  an array of new longitude constraints
     */
    public static void setLongitudeConstraints(String[] newLongitudeConstraints) {
        lonConstraints = newLongitudeConstraints;

        // Translate a null array into the empty array!
        if (lonConstraints == null) {
            lonConstraints = new String[0];
        }
    }


    // SHOW SPLASH SCREEN FLAG ///////////////////////////////////////

    /**
     * True if the splash screen should be shown at startup.
     */
    private static boolean showSplashScreen;

    /**
     * Flags whether the splash screen should be shown at startup.
     */
    private final static String PROP_SHOW_SPLASHSCREEN = "showSplashScreen";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultShowSplashScreenFlag() {
        restoreDefaultShowSplashScreenFlag(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultShowSplashScreenFlag(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }
       
        showSplashScreen = Boolean.valueOf(props.getProperty(PROP_SHOW_SPLASHSCREEN, "true")).booleanValue();
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveShowSplashScreenFlagAsDefault() {
        saveShowSplashScreenFlagAsDefault(true);
    }
    
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveShowSplashScreenFlagAsDefault(boolean saveToFile) {        
        props.setProperty(PROP_SHOW_SPLASHSCREEN, (new Boolean(showSplashScreen)).toString());

        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Returns true if the splash screen should be shown at startup.
     *
     * @return  true if the splash screen should be shown at startup
     */
    public static boolean getShowSplashScreenFlag() {
        return showSplashScreen;
    }

    //-------------------------------------------
    /**
     * Sets whether the splash screen should be shown at startup.
     *
     * @param show  true if the splash screen should be shown at startup
     */
    public static void setShowSplashScreenFlag(boolean show) {
        showSplashScreen = show;
    }



    // REMEMBER WINDOW SIZES FLAG ////////////////////////////////////

    /**
     * True if TSAFE should remember the last size of each window.
     */
    private static boolean rememberWindowSizes;

    /**
     * Flags whether TSAFE should remember the last size of each window.
     */
    private final static String PROP_REMEMBER_WINDOW_SIZES = "rememberWindowSizes";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultRememberWindowSizesFlag() {
        restoreDefaultRememberWindowSizesFlag(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultRememberWindowSizesFlag(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }
       
        rememberWindowSizes = 
            Boolean.valueOf(props.getProperty(PROP_REMEMBER_WINDOW_SIZES, "true")).booleanValue();
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveRememberWindowSizesFlagAsDefault() {
        saveRememberWindowSizesFlagAsDefault(true);
    }
    
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveRememberWindowSizesFlagAsDefault(boolean saveToFile) {        
        props.setProperty(PROP_REMEMBER_WINDOW_SIZES, (new Boolean(rememberWindowSizes)).toString());

        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Returns true if TSAFE should remember the last size of each window.
     *
     * @return   true if TSAFE should remember the last size of each window
     */
    public static boolean getRememberWindowSizesFlag() {
        return rememberWindowSizes;
    }

    //-------------------------------------------
    /**
     * Sets whether TSAFE should remember the last size of each window.
     *
     * @param remember  true if TSAFE should remember the last size of each window
     */
    public static void setRememberWindowSizesFlag(boolean remember) {
        rememberWindowSizes = remember;
    }



    // REMEMBER WINDOW LOCATIONS FLAG ////////////////////////////////

    /**
     * True if TSAFE should remember the last location of each window.
     */
    private static boolean rememberWindowLocations;

    /**
     * Flags whether TSAFE should remember the last location of each window.
     */
    private final static String PROP_REMEMBER_WINDOW_LOCATIONS = "rememberWindowLocations";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultRememberWindowLocationsFlag() {
        restoreDefaultRememberWindowLocationsFlag(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultRememberWindowLocationsFlag(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }
       
        rememberWindowLocations = 
            Boolean.valueOf(props.getProperty(PROP_REMEMBER_WINDOW_LOCATIONS, "true")).booleanValue();
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveRememberWindowLocationsFlagAsDefault() {
        saveRememberWindowLocationsFlagAsDefault(true);
    }
    
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveRememberWindowLocationsFlagAsDefault(boolean saveToFile) {        
        props.setProperty(PROP_REMEMBER_WINDOW_LOCATIONS, (new Boolean(rememberWindowLocations)).toString());

        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Returns true if TSAFE should remember the last location of each window.
     *
     * @return   true if TSAFE should remember the last location of each window
     */
    public static boolean getRememberWindowLocationsFlag() {
        return rememberWindowLocations;
    }

    //-------------------------------------------
    /**
     * Sets whether TSAFE should remember the last location of each window.
     *
     * @param remember  true if TSAFE should remember the last location of each window
     */
    public static void setRememberWindowLocationsFlag(boolean remember) {
        rememberWindowLocations = remember;
    }



    // CONTENT FILTER ////////////////////////////////////////////////

    /**
     * The default content filter for FIG files.
     */
    private static FIGFileContentFilter contentFilter;

    /**
     * The default content filter for FIG files.
     */
    private final static String PROP_CONTENTFILTER = "contentFilter";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultContentFilter() {
        restoreDefaultContentFilter(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultContentFilter(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedFilter = props.getProperty(PROP_CONTENTFILTER);        
        if ((serializedFilter == null) || (serializedFilter.equals(""))) {
            contentFilter = new FIGFileContentFilter();
        }

        // De-serialize the content filter.
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedFilter));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                contentFilter = (FIGFileContentFilter) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize the content filter: " + e);
                contentFilter = new FIGFileContentFilter();
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveContentFilterAsDefault() {
        saveContentFilterAsDefault(true);
    }
    
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveContentFilterAsDefault(boolean saveToFile) {        

        // Serialize the content filter into string format.
        String serializedFilter = "";
        try {
            ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
            oos.writeObject(contentFilter);
            oos.flush();
            serializedFilter = new String(convertBytesToString(serializedStream.toByteArray()));
            oos.close();
        }
        catch (Exception e) {
            System.err.println("Could not serialize the content filter: " + e);
            serializedFilter = "";            
        }


        props.setProperty(PROP_CONTENTFILTER, serializedFilter);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the content filter.
     *
     * @return  the content filter
     */
    public static FIGFileContentFilter getContentFilter() {
        FIGFileContentFilter clonedFilter = cloneFilter(contentFilter);
        if (clonedFilter == null) {
            clonedFilter = new FIGFileContentFilter();
        }

        return clonedFilter;
    }

    //-------------------------------------------
    /**
     * Set the content filter.
     *
     * @param newFilter  the new content filter
     */
    public static void setContentFilter(FIGFileContentFilter newFilter) {
        contentFilter = cloneFilter(newFilter);
    }

    //-------------------------------------------
    /**
     * Clones the content filter.
     *
     * @param filter  a FIGFileContentFilter
     * @return a clone of the FIGFileContentFilter, or null if the input filter
     *         cannot be cloned
     */
    private static FIGFileContentFilter cloneFilter(FIGFileContentFilter filter) {
        FIGFileContentFilter clonedFilter = null;
        
        if (filter != null) {
            clonedFilter = (FIGFileContentFilter) filter.clone();
        }

        return clonedFilter;
    }



    // FEED DIRECTORY ////////////////////////////////////////////////

    /**
     * The default location of recorded feeds.
     */
    private static File feedDirectory;

    /**
     * The default location of recorded feeds.
     */
    private final static String PROP_FEED_DIR = "feedDir";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultFeedDirectory() {
        restoreDefaultFeedDirectory(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultFeedDirectory(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        feedDirectory = new File(props.getProperty(PROP_FEED_DIR, ""));
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveFeedDirectoryAsDefault() {
        saveFeedDirectoryAsDefault(true);
    }
    
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveFeedDirectoryAsDefault(boolean saveToFile) {        
        if (feedDirectory == null) {
            props.setProperty(PROP_FEED_DIR, "");
        }
        else {           
            props.setProperty(PROP_FEED_DIR, feedDirectory.getPath());
        }

        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the feed directory.
     *
     * @return  the feed directory
     */
    public static File getFeedDirectory() {
        return feedDirectory;
    }

    //-------------------------------------------
    /**
     * Set the feed directory.
     *
     * @param newDir  the new feed directory
     */
    public static void setFeedDirectory(File newDir) {        
        feedDirectory = newDir;
    }



    // FEED SOURCES //////////////////////////////////////////////////

    /**
     * The data vector of feed sources (a Vector of Vectors containing single Reader objects,
     * in the same format as a Vector returned from DefaultTableModel.getDataVector).
     */
    private static Vector feedSources;

    /**
     * The data vector of feed sources.
     */
    private final static String PROP_FEED_SOURCES_VECTOR = "feedSourcesVector";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultFeedSources() {
        restoreDefaultFeedSources(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultFeedSources(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedSources = props.getProperty(PROP_FEED_SOURCES_VECTOR);        
        if ((serializedSources == null) || (serializedSources.equals(""))) {
            feedSources = new Vector();
        }

        // De-serialize the list of feed sources.
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedSources));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                feedSources = (Vector) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize feed sources: " + e);
                feedSources = new Vector();
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveFeedSourcesAsDefault() {
        saveFeedSourcesAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveFeedSourcesAsDefault(boolean saveToFile) {        
        // Serialize the list of feed sources into string format.
        String serializedSources = "";
        try {
            ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
            oos.writeObject(feedSources);
            oos.flush();
            serializedSources = new String(convertBytesToString(serializedStream.toByteArray()));
            oos.close();
        }
        catch (Exception e) {
            System.err.println("Could not serialize feed sources: " + e);
            serializedSources = "";            
        }


        props.setProperty(PROP_FEED_SOURCES_VECTOR, serializedSources);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the list of feed sources.
     *
     * @return  the data vector of feed sources
     */
    public static Vector getFeedSources() {
        return feedSources;
    }

    //-------------------------------------------
    /**
     * Set the list of feed sources.
     *
     * @param newFeedSources  a data vector of feed sources
     */
    public static void setFeedSources(Vector newFeedSources) {
        feedSources = newFeedSources;

        // Translate a null list into the empty list!
        if (feedSources == null) {
            feedSources = new Vector();
        }
    }   



    // AIRLINE DATA VECTOR ///////////////////////////////////////////

    /**
     * The data vector of airlines (a Vector of Vectors containing two String objects
     * representing an ICAO airline abbreviation and a description, in the same format
     * as a Vector returned from DefaultTableModel.getDataVector).
     */
    private static Vector airlineDataVector;

    /**
     * The data vector of airlines.
     */
    private final static String PROP_AIRLINE_DATA_VECTOR = "airlineDataVector";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultAirlineDataVector() {
        restoreDefaultAirlineDataVector(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultAirlineDataVector(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedVector = props.getProperty(PROP_AIRLINE_DATA_VECTOR);
        if ((serializedVector == null) || (serializedVector.equals(""))) {
            airlineDataVector = new Vector();
        }

        // De-serialize the airline table.
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedVector));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                airlineDataVector = (Vector) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize airline table: " + e);
                airlineDataVector = new Vector();
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveAirlineDataVectorAsDefault() {
        saveAirlineDataVectorAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveAirlineDataVectorAsDefault(boolean saveToFile) {        
        
        // Serialize the table of airlines into string format.
        String serializedVector = "";
        if (airlineDataVector != null) {
            try {
                ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
                oos.writeObject(airlineDataVector);
                oos.flush();
                serializedVector = new String(convertBytesToString(serializedStream.toByteArray()));
                oos.close();
            }
            catch (Exception e) {
                System.err.println("Could not serialize airline table: " + e);
                serializedVector = "";            
            }            
        }


        props.setProperty(PROP_AIRLINE_DATA_VECTOR, serializedVector);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the airline data vector.
     *
     * @return  a Vector of Vectors object containing the list of airlines
     */
    public static Vector getAirlineDataVector() {
        return airlineDataVector;
    }

    //-------------------------------------------
    /**
     * Set the airline data vector.
     *
     * @param newAirlineDataVector  the airline data vector
     */
    public static void setAirlineDataVector(Vector newAirlineDataVector) {
        airlineDataVector = newAirlineDataVector;
    }   



    // FACILITY DATA VECTOR ///////////////////////////////////////////

    /**
     * The data vector of facilities (a Vector of Vectors containing two String objects
     * representing an ETMS facility identifiers and a description, in the same format
     * as a Vector returned from DefaultTableModel.getDataVector).
     */
    private static Vector facilityDataVector;

    /**
     * The data vector of facilities.
     */
    private final static String PROP_FACILITY_DATA_VECTOR = "facilityDataVector";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultFacilityDataVector() {
        restoreDefaultFacilityDataVector(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultFacilityDataVector(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        String serializedVector = props.getProperty(PROP_FACILITY_DATA_VECTOR);
        if ((serializedVector == null) || (serializedVector.equals(""))) {
            facilityDataVector = new Vector();
        }

        // De-serialize the facility table.
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedVector));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                facilityDataVector = (Vector) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize facility table: " + e);
                facilityDataVector = new Vector();
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveFacilityDataVectorAsDefault() {
        saveFacilityDataVectorAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveFacilityDataVectorAsDefault(boolean saveToFile) {        
        
        // Serialize the table of facilities into string format.
        String serializedVector = "";
        if (facilityDataVector != null) {
            try {
                ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
                oos.writeObject(facilityDataVector);
                oos.flush();
                serializedVector = new String(convertBytesToString(serializedStream.toByteArray()));
                oos.close();
            }
            catch (Exception e) {
                System.err.println("Could not serialize facility table: " + e);
                serializedVector = "";            
            }            
        }


        props.setProperty(PROP_FACILITY_DATA_VECTOR, serializedVector);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the facility data vector.
     *
     * @return  a Vector of Vectors object containing the list of facilities
     */
    public static Vector getFacilityDataVector() {
        return facilityDataVector;
    }

    //-------------------------------------------
    /**
     * Set the facility data vector.
     *
     * @param newFacilityDataVector  the facility data vector
     */
    public static void setFacilityDataVector(Vector newFacilityDataVector) {
        facilityDataVector = newFacilityDataVector;
    } 



    // MESSAGE TYPE DATA VECTOR //////////////////////////////////////

    /**
     * The data vector of message types (a Vector of Vectors containing two String objects
     * representing an ETMS or NAS message type and a description, in the same format
     * as a Vector returned from DefaultTableModel.getDataVector).
     */
    private static Vector messageTypeDataVector;

    /**
     * The data vector of message types.
     */
    private final static String PROP_MESSAGE_TYPE_DATA_VECTOR = "messageTypeDataVector";


    //-------------------------------------------
    /**
     * Restores this value from file.
     */
    public static void restoreDefaultMessageTypeDataVector() {
        restoreDefaultMessageTypeDataVector(true);
    }

    //-------------------------------------------
    /**
     * Restores this value from the current list of properties.
     *
     * @param restoreFromFile  true if the current properties should
     *                         be reloaded from file
     */
    private static void restoreDefaultMessageTypeDataVector(boolean restoreFromFile) {
        if (restoreFromFile) {            
            loadPropertiesFromFile();
        }

        
        String serializedVector = props.getProperty(PROP_MESSAGE_TYPE_DATA_VECTOR);
        if ((serializedVector == null) || (serializedVector.equals(""))) {
            messageTypeDataVector = new Vector();
        }

        // De-serialize the type table.
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedVector));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                messageTypeDataVector = (Vector) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize message type table: " + e);
                messageTypeDataVector = new Vector();
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves this value as the default value in the properties file.
     */
    public static void saveMessageTypeDataVectorAsDefault() {
        saveMessageTypeDataVectorAsDefault(true);
    }
        
    //-------------------------------------------
    /**
     * Saves this value as the default value in the current list of properties.
     * This new value can be erased if the properties are reloaded from file
     * before the modified value is written to file.
     *
     * @param saveToFile  true if the current properties should
     *                    be written to file
     */
    private static void saveMessageTypeDataVectorAsDefault(boolean saveToFile) {        
        
        // Serialize the table of types into string format.
        String serializedVector = "";
        if (messageTypeDataVector != null) {
            try {
                ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
                oos.writeObject(messageTypeDataVector);
                oos.flush();
                serializedVector = new String(convertBytesToString(serializedStream.toByteArray()));
                oos.close();
            }
            catch (Exception e) {
                System.err.println("Could not serialize message type table: " + e);
                serializedVector = "";            
            }            
        }


        props.setProperty(PROP_MESSAGE_TYPE_DATA_VECTOR, serializedVector);
        
        if (saveToFile) {
            savePropertiesToFile();
        }
    }

    //-------------------------------------------
    /**
     * Return the message type data vector.
     *
     * @return  a Vector of Vectors object containing the list of types
     */
    public static Vector getMessageTypeDataVector() {
        return messageTypeDataVector;
    }

    //-------------------------------------------
    /**
     * Set the message type data vector.
     *
     * @param newMessageTypeDataVector  the type data vector
     */
    public static void setMessageTypeDataVector(Vector newMessageTypeDataVector) {
        messageTypeDataVector = newMessageTypeDataVector;
    }   



    // WINDOW PREFERENCES ////////////////////////////////////////////

    /**
     * Maps windows to window sizes (String -> Dimension).
     */
    private static Map windowSizes = null;

    /**
     * The window sizes map.
     */
    private final static String PROP_WINDOW_SIZES = "windowSizes";

    /**
     * Maps windows to window locations (String -> Point).
     */
    private static Map windowLocations = null;

    /**
     * The window locations map.
     */
    private final static String PROP_WINDOW_LOCATIONS = "windowLocations";


    //-------------------------------------------
    /**
     * Gets the size of the specified window.
     *
     * @param windowId a unique window ID
     * @return the preferred size of the window as a Dimension object
     */
    public static Dimension getWindowSize(String windowId) {
        if (windowSizes == null) {
            loadWindowPreferences();
        }

        if (getRememberWindowSizesFlag()) {
            return ((Dimension) windowSizes.get(windowId));
        }

        return null;
    }

    //-------------------------------------------
    /**
     * Sets the size of the specified window.
     *
     * @param windowId a unique window ID
     * @param size     the size of the window
     */
    public static void setWindowSize(String windowId, Dimension size) {
        if (windowSizes == null) {
            loadWindowPreferences();
        }

        windowSizes.put(windowId, size);
    }


    //-------------------------------------------
    /**
     * Gets the location of the specified window.
     *
     * @param windowId a unique window ID
     * @return the preferred location of the window as a Point object
     */
    public static Point getWindowLocation(String windowId) {
        if (windowLocations == null) {
            loadWindowPreferences();
        }

        if (getRememberWindowLocationsFlag()) {
            return ((Point) windowLocations.get(windowId));
        }

        return null;
    }

    //-------------------------------------------
    /**
     * Sets the location of the specified window.
     *
     * @param windowId a unique window ID
     * @param location the location of the window
     */
    public static void setWindowLocation(String windowId, Point location) {
        if (windowLocations == null) {
            loadWindowPreferences();
        }

        windowLocations.put(windowId, location);
    }

    //-------------------------------------------
    /**
     * Loads all window preferences from the user properties file.
     */
    private static void loadWindowPreferences() {
        
        // Load the windows sizes.
        String serializedMap = props.getProperty(PROP_WINDOW_SIZES);
        if ((serializedMap == null) || (serializedMap.equals(""))) {
            windowSizes = new Hashtable();
        }
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedMap));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                windowSizes = (Map) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize window sizes map: " + e);
                windowSizes = new Hashtable();
            }
        }


        // Load the windows locations.
        serializedMap = props.getProperty(PROP_WINDOW_LOCATIONS);
        if ((serializedMap == null) || (serializedMap.equals(""))) {
            windowLocations = new Hashtable();
        }
        else {
            try {
                ByteArrayInputStream serializedStream = new ByteArrayInputStream(convertStringToBytes(serializedMap));
                ObjectInputStream ois = new ObjectInputStream(serializedStream);
                windowLocations = (Map) ois.readObject();
                ois.close();
            }
            catch (Exception e) {
                System.err.println("Could not deserialize window locations map: " + e);
                windowLocations = new Hashtable();
            }
        }
    }

    //-------------------------------------------
    /**
     * Saves all window preferences to the user properties file.
     */
    public static void saveWindowPreferences() {

        // Serialize the window sizes map.
        String serializedMap = "";
        if (windowSizes != null) {
            try {
                ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
                oos.writeObject(windowSizes);
                oos.flush();
                serializedMap = new String(convertBytesToString(serializedStream.toByteArray()));
                oos.close();
            }
            catch (Exception e) {
                System.err.println("Could not serialize window sizes map: " + e);
                serializedMap = "";            
            }            
        }
        props.setProperty(PROP_WINDOW_SIZES, serializedMap);


        // Serialize the window locations map.
        serializedMap = "";
        if (windowLocations != null) {
            try {
                ByteArrayOutputStream serializedStream = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(serializedStream);
                oos.writeObject(windowLocations);
                oos.flush();
                serializedMap = new String(convertBytesToString(serializedStream.toByteArray()));
                oos.close();
            }
            catch (Exception e) {
                System.err.println("Could not serialize window locations map: " + e);
                serializedMap = "";            
            }            
        }
        props.setProperty(PROP_WINDOW_LOCATIONS, serializedMap);


        // Finally, save preferences to disk.
        savePropertiesToFile();
    }




    //
    // UTILITY METHODS
    //

    //-------------------------------------------
    /**
     * Converts an array of bytes into a single string.
     *
     * @param bytes  an array of bytes
     * @return  a string representation of those bytes
     */
    private static String convertBytesToString(byte[] bytes) {
        if (bytes == null) {
            return null;
        }
        else if (bytes.length == 0) {
            return "";
        }

        StringBuffer bytesString = new StringBuffer(Byte.toString(bytes[0]));
        for (int i = 1; i < bytes.length; i++) {
            bytesString.append(";" + Byte.toString(bytes[i]));
        }

        return bytesString.toString();
    }


    //-------------------------------------------
    /**
     * Converts the string of bytes (as created by the {@link #convertBytesToString(bytes[])}
     * method) back into an array of bytes.
     *
     * @param bytesString  a String of bytes
     * @return  the corresponding byte array
     */
    private static byte[] convertStringToBytes(String bytesString) {
        if (bytesString == null) {
            return null;
        }

        StringTokenizer byteTokens = new StringTokenizer(bytesString, ";");
        byte[] bytes = new byte[byteTokens.countTokens()];
        
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = Byte.valueOf(byteTokens.nextToken()).byteValue();
        }

        return bytes;
    }

}
