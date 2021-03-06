package module6;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.HashMap;

import de.fhpotsdam.unfolding.UnfoldingMap;
import de.fhpotsdam.unfolding.data.Feature;
import de.fhpotsdam.unfolding.data.GeoJSONReader;
import de.fhpotsdam.unfolding.data.PointFeature;
import de.fhpotsdam.unfolding.geo.Location;
import de.fhpotsdam.unfolding.marker.AbstractShapeMarker;
import de.fhpotsdam.unfolding.marker.Marker;
import de.fhpotsdam.unfolding.marker.MultiMarker;
import de.fhpotsdam.unfolding.marker.SimplePolygonMarker;
import de.fhpotsdam.unfolding.providers.Google;
import de.fhpotsdam.unfolding.providers.MBTilesMapProvider;
import de.fhpotsdam.unfolding.utils.MapUtils;
import parsing.ParseFeed;
import processing.core.PApplet;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;

/** EarthquakeCityMap
 * An application with an interactive map displaying earthquake data.
 * Author: UC San Diego Intermediate Software Development MOOC team
 * @author Your name here
 * Date: July 17, 2015
 * */
public class EarthquakeCityMap extends PApplet {
	
	// We will use member variables, instead of local variables, to store the data
	// that the setUp and draw methods will need to access (as well as other methods)
	// You will use many of these variables, but the only one you should need to add
	// code to modify is countryQuakes, where you will store the number of earthquakes
	// per country.
	
	// You can ignore this.  It's to get rid of eclipse warnings
	private static final long serialVersionUID = 1L;

	// IF YOU ARE WORKING OFFILINE, change the value of this variable to true
	private static final boolean offline = false;
	
	/** This is where to find the local tiles, for working without an Internet connection */
	public static String mbTilesString = "blankLight-1-3.mbtiles";
	
	

	//feed with magnitude 2.5+ Earthquakes (past week)
	private String earthquakesURL = "https://earthquake.usgs.gov/earthquakes/feed/v1.0/summary/2.5_week.atom";
	
	// The files containing city names and info and country names and info
	private String cityFile = "city-data.json";
	private String countryFile = "countries.geo.json";
	
	// The map
	private UnfoldingMap map;
	
	// Markers for each city
	private List<Marker> cityMarkers;
	// Markers for each earthquake
	private List<Marker> quakeMarkers;

	// A List of country markers
	private List<Marker> countryMarkers;
	
	// NEW IN MODULE 5
	private CommonMarker lastSelected;
	private CommonMarker lastClicked;
	
	// For box selection
	private float boxTopLeftX, boxTopLeftY, boxBottomRightX, boxBottomRightY;
	private float boxTopLeftLon, boxTopLeftLat, boxBottomRightLon, boxBottomRightLat;
	private boolean boxSelected = false;
	private char key_recorded;
	private int mouseClickNum;
	private List<Marker> selectedQuakes;
	private boolean displaySelectedInfo;
	
	public void setup() {		
		// (1) Initializing canvas and map tiles
		size(900, 700, OPENGL);
		if (offline) {
		    map = new UnfoldingMap(this, 200, 50, 650, 600, new MBTilesMapProvider(mbTilesString));
		    earthquakesURL = "2.5_week.atom";  // The same feed, but saved August 7, 2015
		}
		else {
			map = new UnfoldingMap(this, 200, 50, 650, 600, new Google.GoogleMapProvider());
			// IF YOU WANT TO TEST WITH A LOCAL FILE, uncomment the next line
		    //earthquakesURL = "2.5_week.atom";
		}
		MapUtils.createDefaultEventDispatcher(this, map);
		
		// FOR TESTING: Set earthquakesURL to be one of the testing files by uncommenting
		// one of the lines below.  This will work whether you are online or offline
		//earthquakesURL = "test1.atom";
		// earthquakesURL = "test2.atom";
		
		// Uncomment this line to take the quiz
		// earthquakesURL = "quiz2.atom";
		
		
		// (2) Reading in earthquake data and geometric properties
	    //     STEP 1: load country features and markers
		List<Feature> countries = GeoJSONReader.loadData(this, countryFile);
		countryMarkers = MapUtils.createSimpleMarkers(countries);
		
		//     STEP 2: read in city data
		List<Feature> cities = GeoJSONReader.loadData(this, cityFile);
		cityMarkers = new ArrayList<Marker>();
		for(Feature city : cities) {
		  cityMarkers.add(new CityMarker(city));
		}
	    
		//     STEP 3: read in earthquake RSS feed
	    List<PointFeature> earthquakes = ParseFeed.parseEarthquake(this, earthquakesURL);
	    quakeMarkers = new ArrayList<Marker>();
	    
	    for(PointFeature feature : earthquakes) {
		  //check if LandQuake
		  if(isLand(feature)) {
		    quakeMarkers.add(new LandQuakeMarker(feature));
		  }
		  // OceanQuakes
		  else {
		    quakeMarkers.add(new OceanQuakeMarker(feature));
		  }
	    }

	    // could be used for debugging
	    printQuakes();
	    
	    /** test sortAndPrint()
	     * uncomment this if want to print the top largest events
	    
	    int numToPrint;
	    numToPrint = 10;
	    System.out.println("Print the top " + numToPrint + " largest Earthquakes");
	    sortAndPrint(numToPrint);
	     */
	    
	 		
	    // (3) Add markers to map
	    //     NOTE: Country markers are not added to the map.  They are used
	    //           for their geometric properties
	    map.addMarkers(quakeMarkers);
	    map.addMarkers(cityMarkers);
	    

	    
	    
	}  // End setup
	
	
	public void draw() {
		background(0);
		map.draw();
		if (boxSelected) {boxSelection();};
		addKey();
		addKeyMode();
		
	}
	
	
	// TODO: Add the method:
	//   private void sortAndPrint(int numToPrint)
	// and then call that method from setUp
	private void sortAndPrint(int numToPrint) {
		
		List<EarthquakeMarker> quakeList = new ArrayList<EarthquakeMarker>();
		
		for (Marker mk : quakeMarkers) {
			quakeList.add((EarthquakeMarker) mk);
		}
		
		
		Collections.sort(quakeList);
		int i = 0;
		int totalNum = quakeList.size();
		while (i < numToPrint && i < totalNum) {
			// print in reversed order
			System.out.println(i+1 + ": " + quakeList.get(totalNum-1-i).getTitle());
			i++;
		}
		
		//
		
		HashMap<String, Integer> magRecord = new HashMap<String, Integer>();
		
		for (EarthquakeMarker mk : quakeList) {
			
			String mag = Float.toString(mk.getMagnitude());
			
			if (magRecord.containsKey(mag)) {
				magRecord.put(mag, magRecord.get(mag)+1);
				
			}
			else {
				magRecord.put(mag, 1);
			}
			
		}
		/** The helper function to find the largest magnitude
		 * that has occured more than three times.
		 
		System.out.println(magRecord.size());
		
		float magMax = (float) 0.0;
		String magMaxStr = "";
		for (String mag : magRecord.keySet()) {
			if (magRecord.get(mag) >=  3) {
							
				if (Float.parseFloat(mag) > magMax) {
					magMax = Float.parseFloat(mag);
					magMaxStr = mag;
				}
			}
		}
		
		System.out.println("Target found:\n" + "M " + magMaxStr + " : " + magRecord.get(magMaxStr));
		*/
	}
	
	/** Event handler that gets called automatically when the 
	 * key is pressed.
	 */
	public void keyPressed() {
		System.out.println(key);
		key_recorded = key;
		// if (key_recorded != 's') {
		if (key_recorded == 'q') {
			mouseClickNum = 0;
			boxSelected = false;
			selectedQuakes.clear();
			displaySelectedInfo = false;
		}
		
	}
	/** Event handler that gets called automatically when the 
	 * mouse moves.
	 */
	@Override
	public void mouseMoved()
	{
		// clear the last selection
		if (lastSelected != null) {
			lastSelected.setSelected(false);
			lastSelected = null;
		
		}
		selectMarkerIfHover(quakeMarkers);
		selectMarkerIfHover(cityMarkers);
		//loop();
	}
	
	// If there is a marker selected 
	private void selectMarkerIfHover(List<Marker> markers)
	{
		// Abort if there's already a marker selected
		if (lastSelected != null) {
			return;
		}
		
		for (Marker m : markers) 
		{
			CommonMarker marker = (CommonMarker)m;
			if (marker.isInside(map,  mouseX, mouseY)) {
				lastSelected = marker;
				marker.setSelected(true);
				return;
			}
		}
	}
	
	/** The event handler for mouse clicks
	 * It will display an earthquake and its threat circle of cities
	 * Or if a city is clicked, it will display all the earthquakes 
	 * where the city is in the threat circle
	 */
	@Override
	public void mouseClicked()
	{
		if (key_recorded == 's') { // enter selection mode
			if (mouseClickNum < 2) { // check if we have two selected point
				if (mouseClickNum == 0) { // collect the first point
					mouseClickNum = 1;
					boxTopLeftX = mouseX;
					boxTopLeftY = mouseY;
					System.out.println("the first point is selected");
				}
				else if (mouseClickNum == 1) { // collect the second point
					mouseClickNum = 2;
					boxBottomRightX = mouseX;
					boxBottomRightY = mouseY;
					boxSelected = true;
					System.out.println("the second point is selected");
					
					int totalSelected = selectQuakes();
					// selectedQuakeInfo = true;
					if (totalSelected > 0) {
						displaySelectedInfo = true;
					}
				}
			}
			
			if (boxSelected == true && mouseButton == RIGHT) {
				JSONArray selectedQuakesList = new JSONArray();
				// save the selected quakes info into csv
				for (Marker mk : selectedQuakes) {
					// covert to json
					JSONObject jsonObj = new JSONObject();
					// info to store
					String[] infoList = {"title", "magnitude",
							"depth", "location", "isOnLand", "age",};
					for (String info : infoList) {
						
						if (info.equals("location")) {
							jsonObj.put(info, mk.getLocation().toString());
						}
						else if (info.equals("isOnLand")) {
							EarthquakeMarker quakeMk = (EarthquakeMarker) mk;
							jsonObj.put(info, quakeMk.isOnLand());
						}
						else {
							jsonObj.put(info, mk.getProperty(info));
						}
					}
					selectedQuakesList.add(jsonObj);
					// String jsonText = jsonObj.toString();
					// System.out.println(jsonText);
				}
				//Write JSON file
				String fileSaveLoc = "../data/selected_earthquakes.json";
		        try (FileWriter file = new FileWriter(fileSaveLoc)) {
		 
		            file.write(selectedQuakesList.toJSONString());
		            file.flush();
		            //
		            System.out.println("Selected Earthquake saved in " + fileSaveLoc);
		 
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			
			}
			
//			if (mouseButton == RIGHT && boxSelected) {
//				int totalSelected = selectQuakes();
//				// selectedQuakeInfo = true;
//				if (totalSelected > 0) {
//					displaySelectedInfo = true;
//				}
//			}
			
			return;
		}
		
		if (lastClicked != null) {
			unhideMarkers();
			lastClicked = null;
		}
		else if (lastClicked == null) 
		{
			checkEarthquakesForClick();
			if (lastClicked == null) {
				checkCitiesForClick();
			}
		}
	}
	
	// Helper method that will check if a city marker was clicked on
	// and respond appropriately
	private void checkCitiesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker marker : cityMarkers) {
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = (CommonMarker)marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : cityMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : quakeMarkers) {
					EarthquakeMarker quakeMarker = (EarthquakeMarker)mhide;
					if (quakeMarker.getDistanceTo(marker.getLocation()) 
							> quakeMarker.threatCircle()) {
						quakeMarker.setHidden(true);
					}
				}
				return;
			}
		}		
	}
	
	// Helper method that will check if an earthquake marker was clicked on
	// and respond appropriately
	private void checkEarthquakesForClick()
	{
		if (lastClicked != null) return;
		// Loop over the earthquake markers to see if one of them is selected
		for (Marker m : quakeMarkers) {
			EarthquakeMarker marker = (EarthquakeMarker)m;
			if (!marker.isHidden() && marker.isInside(map, mouseX, mouseY)) {
				lastClicked = marker;
				// Hide all the other earthquakes and hide
				for (Marker mhide : quakeMarkers) {
					if (mhide != lastClicked) {
						mhide.setHidden(true);
					}
				}
				for (Marker mhide : cityMarkers) {
					if (mhide.getDistanceTo(marker.getLocation()) 
							> marker.threatCircle()) {
						mhide.setHidden(true);
					}
				}
				return;
			}
		}
	}
	
	// loop over and unhide all markers
	private void unhideMarkers() {
		for(Marker marker : quakeMarkers) {
			marker.setHidden(false);
		}
			
		for(Marker marker : cityMarkers) {
			marker.setHidden(false);
		}
	}
	
	// helper method to draw key in GUI
	private void addKey() {	
		// Remember you can use Processing's graphics methods here
		fill(255, 250, 240);
		
		int xbase = 25;
		int ybase = 50;
		
		rect(xbase, ybase, 150, 250);
		
		fill(0);
		textAlign(LEFT, CENTER);
		textSize(12);
		text("Earthquake Key", xbase+25, ybase+25);
		
		fill(150, 30, 30);
		int tri_xbase = xbase + 35;
		int tri_ybase = ybase + 50;
		triangle(tri_xbase, tri_ybase-CityMarker.TRI_SIZE, tri_xbase-CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE, tri_xbase+CityMarker.TRI_SIZE, 
				tri_ybase+CityMarker.TRI_SIZE);

		fill(0, 0, 0);
		textAlign(LEFT, CENTER);
		text("City Marker", tri_xbase + 15, tri_ybase);
		
		text("Land Quake", xbase+50, ybase+70);
		text("Ocean Quake", xbase+50, ybase+90);
		text("Size ~ Magnitude", xbase+25, ybase+110);
		
		fill(255, 255, 255);
		ellipse(xbase+35, 
				ybase+70, 
				10, 
				10);
		rect(xbase+35-5, ybase+90-5, 10, 10);
		
		fill(color(255, 255, 0));
		ellipse(xbase+35, ybase+140, 12, 12);
		fill(color(0, 0, 255));
		ellipse(xbase+35, ybase+160, 12, 12);
		fill(color(255, 0, 0));
		ellipse(xbase+35, ybase+180, 12, 12);
		
		textAlign(LEFT, CENTER);
		fill(0, 0, 0);
		text("Shallow", xbase+50, ybase+140);
		text("Intermediate", xbase+50, ybase+160);
		text("Deep", xbase+50, ybase+180);

		text("Past hour", xbase+50, ybase+200);
		
		fill(255, 255, 255);
		int centerx = xbase+35;
		int centery = ybase+200;
		ellipse(centerx, centery, 12, 12);

		strokeWeight(2);
		line(centerx-8, centery-8, centerx+8, centery+8);
		line(centerx-8, centery+8, centerx+8, centery-8);
		
		
	}
	// helper functions for my extension
	private int selectQuakes() {
		// TODO: use map.getLocation(float x, float y)
		// to convert the box boundary to geo boundary
		// so we can select the earthquakes inside the box
		Location geoPoint1 = map.getLocation(boxTopLeftX, boxTopLeftY);
		boxTopLeftLon = geoPoint1.getLon();
		boxTopLeftLat = geoPoint1.getLat();
		
		Location geoPoint3 = map.getLocation(boxBottomRightX, boxBottomRightY);
		boxBottomRightLon = geoPoint3.getLon();
		boxBottomRightLat = geoPoint3.getLat();
		
		Location geoPoint2 = new Location(boxTopLeftLat, boxBottomRightLon);
		Location geoPoint4 = new Location(boxBottomRightLat, boxTopLeftLon);
		
		//.isInsideByLocation
		
		List<Location> listLoc = Arrays.asList(geoPoint1, geoPoint2, geoPoint3, geoPoint4);
		AbstractShapeMarker boxGeo = new SimplePolygonMarker(listLoc);
		
		//
		selectedQuakes = new ArrayList<Marker>();
		//
		for (Marker mk : quakeMarkers) {
			if (boxGeo.isInsideByLocation(mk.getLocation())) {
				selectedQuakes.add(mk);
			}
		}
		
		return selectedQuakes.size();
		
	}
	
	private void boxSelection() {
		line(boxTopLeftX, boxTopLeftY, boxBottomRightX, boxTopLeftY);
		line(boxBottomRightX, boxTopLeftY, boxBottomRightX, boxBottomRightY);
		line(boxBottomRightX, boxBottomRightY, boxTopLeftX, boxBottomRightY);
		line(boxTopLeftX, boxBottomRightY, boxTopLeftX, boxTopLeftY);

	}
	
	private void addKeyMode() {
		if (key_recorded == 's' || boxSelected) {
			
			fill(255, 255, 255);
			
			int xbase = 25;
			int ybase = 320;
			
			rect(xbase, ybase, 150, 200);
			
			fill(0);
			textAlign(LEFT, CENTER);
			textSize(15);
			text("Selection Mode:", xbase+10, ybase+20);
			textSize(12);
			text("Click the mouse twice", xbase+20, ybase+45);
			text("to select two points;", xbase+20, ybase+65);
			//
			text("To save selected", xbase+10, ybase+95);
			text("earthquakes info: ", xbase+10, ybase+115);
			text("Right Click", xbase+20, ybase+135);
			//
			text("To exit: Press Key q", xbase+10, ybase+165);
			//text("other than 's'.", xbase+20, ybase+185);
			
			// display some basic info of the selected earthquakes
			
			if (displaySelectedInfo) {
				fill(250, 250, 230, (float) 200.0);
				
				int xInfo = 600;
				int yInfo = 70;
				
				rect(xInfo, yInfo, 200, 160);
				
				fill(0);
				textAlign(LEFT, CENTER);
				textSize(12);
				text("NO. of Events Selected: ", xInfo+10, yInfo+20);
				text(Integer.toString(selectedQuakes.size()), xInfo+10, yInfo+40);
				//
				text("Latitude Range: ", xInfo+10, yInfo+60);
				// String.format ("%,.2f", number)
				String latRange = String.format("%,.2f", boxBottomRightLat) + " --- " + String.format("%,.2f", boxTopLeftLat);
				text(latRange, xInfo+10, yInfo+80);
				String lonRange = String.format("%,.2f", boxTopLeftLon) + " --- " + String.format("%,.2f", boxBottomRightLon);
				text("Longitude Range: ", xInfo+10, yInfo+100);
				text(lonRange, xInfo+10, yInfo+120);
				
				
			}
		}
	}
	
	// Checks whether this quake occurred on land.  If it did, it sets the 
	// "country" property of its PointFeature to the country where it occurred
	// and returns true.  Notice that the helper method isInCountry will
	// set this "country" property already.  Otherwise it returns false.
	private boolean isLand(PointFeature earthquake) {
		
		// IMPLEMENT THIS: loop over all countries to check if location is in any of them
		// If it is, add 1 to the entry in countryQuakes corresponding to this country.
		for (Marker country : countryMarkers) {
			if (isInCountry(earthquake, country)) {
				return true;
			}
		}
		
		// not inside any country
		return false;
	}
	
	// prints countries with number of earthquakes
	// You will want to loop through the country markers or country features
	// (either will work) and then for each country, loop through
	// the quakes to count how many occurred in that country.
	// Recall that the country markers have a "name" property, 
	// And LandQuakeMarkers have a "country" property set.
	private void printQuakes() {
		int totalWaterQuakes = quakeMarkers.size();
		for (Marker country : countryMarkers) {
			String countryName = country.getStringProperty("name");
			int numQuakes = 0;
			for (Marker marker : quakeMarkers)
			{
				EarthquakeMarker eqMarker = (EarthquakeMarker)marker;
				if (eqMarker.isOnLand()) {
					if (countryName.equals(eqMarker.getStringProperty("country"))) {
						numQuakes++;
					}
				}
			}
			if (numQuakes > 0) {
				totalWaterQuakes -= numQuakes;
				System.out.println(countryName + ": " + numQuakes);
			}
		}
		System.out.println("OCEAN QUAKES: " + totalWaterQuakes);
	}
	
	
	
	// helper method to test whether a given earthquake is in a given country
	// This will also add the country property to the properties of the earthquake feature if 
	// it's in one of the countries.
	// You should not have to modify this code
	private boolean isInCountry(PointFeature earthquake, Marker country) {
		// getting location of feature
		Location checkLoc = earthquake.getLocation();

		// some countries represented it as MultiMarker
		// looping over SimplePolygonMarkers which make them up to use isInsideByLoc
		if(country.getClass() == MultiMarker.class) {
				
			// looping over markers making up MultiMarker
			for(Marker marker : ((MultiMarker)country).getMarkers()) {
					
				// checking if inside
				if(((AbstractShapeMarker)marker).isInsideByLocation(checkLoc)) {
					earthquake.addProperty("country", country.getProperty("name"));
						
					// return if is inside one
					return true;
				}
			}
		}
			
		// check if inside country represented by SimplePolygonMarker
		else if(((AbstractShapeMarker)country).isInsideByLocation(checkLoc)) {
			earthquake.addProperty("country", country.getProperty("name"));
			
			return true;
		}
		return false;
	}

}
