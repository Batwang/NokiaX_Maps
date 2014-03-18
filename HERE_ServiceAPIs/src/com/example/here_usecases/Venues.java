package com.example.here_usecases;


import java.util.List;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.here.android.common.GeoBoundingBox;
import com.here.android.common.GeoCoordinate;
import com.here.android.common.ViewObject;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapGestureListener;
import com.here.android.restricted.mapping.Map;
import com.here.android.restricted.venuemaps.Venue;
import com.here.android.restricted.venuemaps.VenueInfo;
import com.here.android.restricted.venuemaps.VenueMapListener;
import com.here.android.restricted.venuemaps.VenueMaps;
import com.here.android.restricted.venuemaps.VenueRetrievalResult;

import com.here.android.mapping.MapFragment;


public class Venues extends Activity 
implements VenueMapListener, MapGestureListener {

	 // map embedded in the map fragment
  private Map map = null;
  private VenueMaps venueMaps  = null;
  boolean venue_data_loaded = false;
  List<VenueInfo> venueInfoList = null;
  // map fragment embedded in this activity
  private MapFragment mapFragment = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.venues);
      
      final Venues that = this;
      // Search for the map fragment to finish setup by calling init().
      mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      mapFragment.init(new FragmentInitListener() {
          @Override
          public void onFragmentInitializationCompleted(InitError error) {
              if (error == InitError.NONE) {
                  // retrieve a reference of the map from the map fragment
                  	map = (Map) mapFragment.getMap();
                  	map.setZoomLevel(17.0);
          			map.setVenueMapTilesVisible(true);
          			map.setLandmarksVisible(false);
          			
          			mapFragment.getMapGesture().addMapGestureListener(that);
          			
          			venueMaps = VenueMaps.getVenueMaps(getApplicationContext());
          			if (venueMaps.coverageInformationDownloaded()) {
          				that.venue_data_loaded = true;
          			} else {	
          				that.venue_data_loaded = false;
          				venueMaps.refreshVenues(that);
          			}
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
  	if (map != null && venueMaps != null) {
  		
  		venueInfoList = venueMaps.getVenuesIn(map.getBoundingBox());
  	
  		Toast.makeText(getApplicationContext(),"Got venues: " + venueInfoList.size(), Toast.LENGTH_LONG).show();
        
  		GeoBoundingBox boxx = null;
  		
  		for(int i=0; i < venueInfoList.size(); i++){
  			VenueInfo tmp = venueInfoList.get(i);
  			
  			if(boxx == null){
  				boxx = tmp.getBoundingBox();
  			}else{
  				boxx = boxx.merge(tmp.getBoundingBox());
  			}
  		}
  		map.zoomTo(boxx, MapAnimation.LINEAR, 0);
  	}
  }

@Override
public void onCoverageDownloaded(boolean sucess) {
	this.venue_data_loaded = sucess;
	if(sucess){
		
	}else{
	 	Toast.makeText(getApplicationContext(),"Venue data loading failed: ", Toast.LENGTH_LONG).show();
	}
}

@Override
public boolean onTapEvent(PointF p) {

	if(venueInfoList != null && venueMaps != null && map != null){

		GeoCoordinate selpoint = map.pixelToGeo(p);
		for(int i=0; i < venueInfoList.size(); i++){
			VenueInfo tmp = venueInfoList.get(i);
			
			if(tmp.getBoundingBox().contains(selpoint)){
				
				String stringstuff = "";
				VenueRetrievalResult result = venueMaps.getVenue(tmp, this);
				if (result == VenueRetrievalResult.REQUIRES_DOWNLOAD) {
					venueMaps.downloadVenue(tmp, this);
					stringstuff = "Downloading the venue";
				} else if (result == VenueRetrievalResult.AVAILABLE_DB) {
					stringstuff = "Parsing the venue";
				} else if (result == VenueRetrievalResult.BUSY_PARSING_THIS_VENUE) {
					stringstuff = "Already doing Parsing";
				} else if (result == VenueRetrievalResult.AVAILABLE_CACHE) {
					stringstuff = "Already available";
				}
				
				Toast.makeText(getApplicationContext(),"Venue cliked: " + tmp.getName() + ", " + stringstuff, Toast.LENGTH_LONG).show();
				
				return false;
			}
		}
	}
	
	return false;
}


@Override
public void onVenueDataDownloaded(VenueInfo venue, boolean success) {
	if (success) {
		Toast.makeText(getApplicationContext(),"Venue: " + venue.getName() + " downloaded.", Toast.LENGTH_SHORT).show();
		venueMaps.getVenue(venue, this);
	} else {
		Toast.makeText(getApplicationContext(),"Venue load failed " + venue.getName(), Toast.LENGTH_LONG).show();
		
	}
	
}

@Override
public void onVenueGet(Venue venue) {
	
	Toast.makeText(getApplicationContext(),"Venue gotten " + venue.getContent().getName(), Toast.LENGTH_LONG).show();
	
}

@Override
public boolean onDoubleTapEvent(PointF p) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean onLongPressEvent(PointF p) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void onLongPressRelease() {
	// TODO Auto-generated method stub
	
}

@Override
public boolean onMapObjectsSelected(List<ViewObject> objects) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void onMultiFingerManipulationEnd() {
	// TODO Auto-generated method stub
	
}

@Override
public void onMultiFingerManipulationStart() {
	// TODO Auto-generated method stub
	
}

@Override
public void onPanEnd() {
	// TODO Auto-generated method stub
	
}

@Override
public void onPanStart() {
	// TODO Auto-generated method stub
	
}

@Override
public void onPinchLocked() {
	// TODO Auto-generated method stub
	
}

@Override
public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean onRotateEvent(float rotateAngle) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public void onRotateLocked() {
	// TODO Auto-generated method stub
	
}



@Override
public boolean onTiltEvent(float angle) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean onTwoFingerTapEvent(PointF p) {
	// TODO Auto-generated method stub
	return false;
}
}