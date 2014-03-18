package com.example.hereapi_example;


import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapMarker;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

public class ProgrammaticDemoActivity extends Activity {

	 private static final String MAP_FRAGMENT_TAG = "map";
	    private Map mMap;
	    private MapFragment mMapFragment;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);

	        setUpMapIfNeeded();
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();

	        // In case Google Play services has since become available.
	        setUpMapIfNeeded();
	    }

	    private void setUpMapIfNeeded() {
	        // Do a null check to confirm that we have not already instantiated the map.
	        if (mMap == null) {
	        	// It isn't possible to set a fragment's id programmatically so we set a tag instead and
		        // search for it using that.
		        mMapFragment = (MapFragment) getFragmentManager().findFragmentByTag(MAP_FRAGMENT_TAG);

		        // We only create a fragment if it doesn't already exist.
		        if (mMapFragment == null) {
		            // To programmatically add the map, we first create a SupportMapFragment.
		            mMapFragment = new MapFragment();

		            // Then we add it using a FragmentTransaction.
		            android.app.FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		            fragmentTransaction.add(android.R.id.content, mMapFragment, MAP_FRAGMENT_TAG);
		            fragmentTransaction.commit();
		            
		            mMapFragment.init(getApplicationContext(), new FragmentInitListener(){

						@Override
						public void onFragmentInitializationCompleted(InitError error) {
							if(error == InitError.NONE){
								mMap = mMapFragment.getMap();
								setUpMap();
							}else{
								Toast.makeText(getBaseContext(),"onFragmentInitializationCompleted error: " + error, Toast.LENGTH_LONG).show();								
							}
						}
		            });
		        }
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
