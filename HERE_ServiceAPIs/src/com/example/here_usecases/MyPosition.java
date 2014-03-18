package com.example.here_usecases;


import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.common.GeoPosition;
import com.here.android.common.LocationMethod;
import com.here.android.common.LocationStatus;
import com.here.android.common.PositionListener;
import com.here.android.common.PositioningManager;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapMarker;

public class MyPosition extends Activity implements PositionListener {

	 // map embedded in the map fragment
 private Map map = null;
 private PositioningManager positioning = null;
 
 private MapMarker marker= null;
 
 // map fragment embedded in this activity
 private MapFragment mapFragment = null;

 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.my_position);

     final MyPosition that = this;
     // Search for the map fragment to finish setup by calling init().
     mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
     mapFragment.init(new FragmentInitListener() {
         @Override
         public void onFragmentInitializationCompleted(InitError error) {
             if (error == InitError.NONE) {
                 // retrieve a reference of the map from the map fragment
                 map = mapFragment.getMap();
                 positioning = MapFactory.getPositioningManager();
                 positioning.addPositionListener(that);
                 doMoveMarker();
             } else {
            	 Toast.makeText(getApplicationContext(),"Map init error: " + error, Toast.LENGTH_LONG).show();
             }
         }
     });
 }

 /**
  * Called when the geobut button is clicked.
  */
 public void onGeoCode(View view) {
 	if (positioning != null && map != null ) {
 		
 		Button p1_button = (Button)findViewById(R.id.posssbut);
 		if(positioning.isActive()){
 			positioning.stop();
 			p1_button.setText(R.string.Start);
 		}else{
 			positioning.start(LocationMethod.GPS_NETWORK);
 			p1_button.setText(R.string.Stop);
 		}
 	}
 }

@Override
public void onPositionFixChanged(LocationMethod method, LocationStatus status) {
	doMoveMarker();
}

@Override
public void onPositionUpdated(LocationMethod method, GeoPosition position) {
	doMoveMarker();
}

public void doMoveMarker() {
	if(map == null){
		return;
	}
	
	if(positioning == null){
		return;
	}
	
	if(marker != null){
		marker.hideInfoBubble();
		map.removeMapObject(marker);
		marker = null;
	}
	
	TextView posMeth =  (TextView)findViewById(R.id.pos_method);
	if(positioning.getLocationMethod() == LocationMethod.GPS){
		posMeth.setText(R.string.GPS);
	}else if(positioning.getLocationMethod() == LocationMethod.GPS_NETWORK){
		posMeth.setText(R.string.GPS_NETWORK);
	}else if(positioning.getLocationMethod() == LocationMethod.NETWORK){
		posMeth.setText(R.string.NETWORK);
	}else{
		posMeth.setText(R.string.NONE);
	}
	
	TextView posStat =  (TextView)findViewById(R.id.pos_status);
	LocationStatus  sta = positioning.getLocationStatus(LocationMethod.GPS_NETWORK);
	if(sta == LocationStatus.AVAILABLE){
		posStat.setText(R.string.AVAILABLE);
	}else if(sta == LocationStatus.OUT_OF_SERVICE){
		posStat.setText(R.string.OUT_OF_SERVICE);
	}else if(sta == LocationStatus.TEMPORARILY_UNAVAILABLE){
		posStat.setText(R.string.TEMPORARILY_UNAVAILABLE);
	}else{
		posStat.setText(R.string.NONE);
	}
	
	GeoPosition pos = positioning.getPosition();
	
	if(pos != null && positioning.hasValidPosition()){
		
		marker = MapFactory.createMapMarker(120.0f);//green
		marker.setCoordinate(pos.getCoordinate());
		
		String tititle = "Heading: " + pos.getHeading() + ", speed: " + pos.getSpeed();
		String descrip = "Lat: " + pos.getCoordinate().getLatitude() + "Acc: " + pos.getLatitudeAccuracy();
		descrip = descrip + "\nLon: " + pos.getCoordinate().getLongitude() + "Acc: " + pos.getLongitudeAccuracy();
		descrip = descrip + "\nAlt: " + pos.getCoordinate().getAltitude() + "Acc: " + pos.getAltitudeAccuracy();
		descrip = descrip + "\nTime: " + pos.getTimestamp();
		marker.setTitle(tititle);
		marker.setDescription(descrip);
	}else{
		marker = MapFactory.createMapMarker(330.0f);//rose
		marker.setCoordinate(map.getCenter());
		marker.setTitle("invalid position !");
	}

    map.addMapObject(marker);
    map.setCenter(marker.getCoordinate(), MapAnimation.BOW);
    marker.showInfoBubble();
}
}