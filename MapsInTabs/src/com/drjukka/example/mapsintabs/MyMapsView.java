package com.drjukka.example.mapsintabs;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.here.android.mapping.MapCompatibilityFragment;

public class MyMapsView extends Fragment  {

	// map embedded in the map fragment
		private Map map = null;

		// map fragment embedded in this activity
		private MapCompatibilityFragment mapFragment = null;

		@Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState); 
	    }

	    @Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
	        View v = inflater.inflate(R.layout.map_tab, container, false);
	  
	        mapFragment = (MapCompatibilityFragment) getFragmentManager().findFragmentById(R.id.map);
	        mapFragment.init(new FragmentInitListener() {
     			@Override
     			public void onFragmentInitializationCompleted(InitError error) {
     				if (error == InitError.NONE) {
     					// retrieve a reference of the map from the map fragment
     					map = mapFragment.getMap();
     					// Set the map center coordinate to the Vancouver region
     					map.setCenter(MapFactory.createGeoCoordinate(49.196261,-123.004773, 0.0), MapAnimation.NONE);
     					// Set the map zoom level to the average between min and max
     					// (with no animation)
     					map.setZoomLevel((map.getMaxZoomLevel() +
     							map.getMinZoomLevel()) / 2);
     				} else {
     					System.out.println("ERROR: Cannot initialize Map Fragment");
     				}
     			}
     		});
	        
	        return v;
	    }
}

