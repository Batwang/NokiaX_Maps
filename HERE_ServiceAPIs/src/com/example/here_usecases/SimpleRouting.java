package com.example.here_usecases;


import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.common.GeoBoundingBox;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapRoute;
import com.here.android.routing.RouteManager;
import com.here.android.routing.RouteManagerEventListener;
import com.here.android.routing.RouterError;
import com.here.android.routing.RoutingMode;
import com.here.android.routing.RoutingResult;
import com.here.android.routing.RoutingType;
import com.here.android.routing.TransportMode;
import com.here.android.routing.WaypointParameterList;


public class SimpleRouting extends Activity 
implements RouteManagerEventListener{

	 // map embedded in the map fragment
 private Map map = null;
 private RouteManager router = null;
 private MapMarker start_marker= null;
 private MapMarker end_marker= null;
 private MapRoute mapRoute = null;
 
 // map fragment embedded in this activity
 private MapFragment mapFragment = null;

 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.simple_routing);

     // Search for the map fragment to finish setup by calling init().
     mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
     mapFragment.init(new FragmentInitListener() {
         @Override
         public void onFragmentInitializationCompleted(InitError error) {
             if (error == InitError.NONE) {
                 // retrieve a reference of the map from the map fragment
                 map = mapFragment.getMap();
                 router = MapFactory.getRouteManager();
                 
                 mapFragment.getMapGesture().setRotateEnabled(false);
                 mapFragment.getMapGesture().setTiltEnabled(false);
                 
                 // helsinki 60.1708° N, 24.9375° E
                 start_marker = MapFactory.createMapMarker(210.0f);
                 start_marker.setCoordinate(MapFactory.createGeoCoordinate(60.1708, 24.9375));
                 start_marker.setTitle("Origin");
                 start_marker.setDraggable(true);
                 map.addMapObject(start_marker);
                 
                 //Tampere 61.5000° N, 23.7667° E
                 end_marker = MapFactory.createMapMarker(30.0f);
                 end_marker.setCoordinate(MapFactory.createGeoCoordinate(61.5000, 23.7667));
                 end_marker.setTitle("Destination");
                 end_marker.setDraggable(true);
                 map.addMapObject(end_marker);
                 
                 GeoBoundingBox firstbox =MapFactory.createGeoBoundingBox(start_marker.getCoordinate(), start_marker.getCoordinate());
                 map.zoomTo(firstbox.merge(MapFactory.createGeoBoundingBox(end_marker.getCoordinate(), end_marker.getCoordinate())), MapAnimation.NONE, 0);
                 
             } else {
           	  	Toast.makeText(getApplicationContext(),"Map init error: " + error, Toast.LENGTH_LONG).show();
             }
         }
     });
 }
 public void onGeoCode(View view) {
	 doTheRouting();
 }
	  		
 public void doTheRouting() {

	if(end_marker != null && start_marker != null && router != null ){
	
		if(mapRoute != null){
			map.removeMapObject(mapRoute);
			mapRoute = null;
		}
		
		onProgress(-1);
		
		// Add the way
        WaypointParameterList waypoints = MapFactory.createWaypointParameterList();
        waypoints.addCoordinate(start_marker.getCoordinate());
        waypoints.addCoordinate(end_marker.getCoordinate());

        // Routing mode
        RoutingMode mode = MapFactory.createRoutingMode();
        
        RadioGroup radioGroupId = (RadioGroup) findViewById(R.id.radioRoutMode);
        int selectedOption = radioGroupId.getCheckedRadioButtonId();
	  	if(selectedOption == R.id.radio_public){
	  		mode.setTransportMode(TransportMode.PUBLIC_TRANSPORT);
	  	}else if(selectedOption == R.id.radio_walk){
	  		mode.setTransportMode(TransportMode.PEDESTRIAN);
	  	}else{
	  		mode.setTransportMode(TransportMode.CAR);
	  	}	  	
	  	
	  	mode.setRoutingType(RoutingType.FASTEST);

		router.calculateRouteAsync(waypoints, mode, this);
	}
}

@Override
public void onCalculateRouteFinished(RouterError errorCode,List<RoutingResult> routeResults) {
	if (errorCode == RouterError.NONE)
	{
		if(routeResults.size() > 0){
			// create a map route object and place it on the map
	        mapRoute = MapFactory.createMapRoute(routeResults.get(0).getRoute());
	        map.addMapObject(mapRoute);
	
	        // Get the bounding box containing the route and zoom in (no animation)
	        GeoBoundingBox gbb = routeResults.get(0).getRoute().getBoundingBox();
	        map.zoomTo(gbb, MapAnimation.BOW, 0);	
		}else{
			Toast.makeText(getApplicationContext(),"No results for Routing found", Toast.LENGTH_LONG).show();
		}
	}else {
		Toast.makeText(getApplicationContext(),"Routing failed, error: " + errorCode, Toast.LENGTH_LONG).show();
	}
}

@Override
public void onProgress(int percentage) {
	// TODO Auto-generated method stub
	TextView proge =  (TextView)findViewById(R.id.routing_prog);
	proge.setText("done: " + percentage + " %");
}
}
