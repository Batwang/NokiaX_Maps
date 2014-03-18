package com.example.hereapi_example;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapMarker;

import android.app.Activity;
import android.os.Bundle;

public class RetainMapDemoActivity extends Activity {
	Map mMap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo);

        MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);

        if (savedInstanceState == null) {
            // First incarnation of this activity.
            mapFragment.setRetainInstance(true);
        } else {
            // Reincarnated activity. The obtained map is the same map instance in the previous
            // activity life cycle. There is no need to reinitialize it.
            mMap = mapFragment.getMap();
        }
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
    	if(mMap == null){ 
 		   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
 			// initialize the Map Fragment to create a map and
 			// attached to the fragment
 			mapFragment.init(new FragmentInitListener() {
 				@Override
 				public void onFragmentInitializationCompleted(InitError error) {
 					if (error == InitError.NONE) {
 						mMap = (Map) mapFragment.getMap();
 						setUpMap();
 					}
 				}
 			});
    	}
    }

    private void setUpMap() {
    	 if(mMap != null){
  		   MapMarker markker = MapFactory.createMapMarker();
  		   markker.setCoordinate(mMap.getCenter());
  		   markker.setTitle("Marker");
  		   mMap.addMapObject(markker);
  	   }
    }
}
