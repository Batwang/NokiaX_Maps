package com.example.hereapi_example;

import com.here.android.mapping.FactoryInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapView;
import com.here.android.restricted.mapping.RestrictedMapFactory;

import android.app.Activity;
import android.os.Bundle;

public class RawMapViewDemoActivity extends Activity {

	 private MapView mMapView;
	    private Map mMap;

	    @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.raw_mapview_demo);

	        mMapView = (MapView) findViewById(R.id.map);
	        //mMapView.onCreate(savedInstanceState);

	        setUpMapIfNeeded();
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	        mMapView.onResume();

	        setUpMapIfNeeded();
	    }

	    private void setUpMapIfNeeded() {
	        if (mMap == null && mMapView != null) {
	            mMap = mMapView.getMap();
	            if (mMap != null) {
	                setUpMap();
	            }else{
	            	 MapFactory.initFactory(getApplicationContext(), new FactoryInitListener() {

						@Override
						public void onFactoryInitializationCompleted(InitError error) {
							// TODO Auto-generated method stub
							if(error == InitError.NONE){
								mMapView.setMap(RestrictedMapFactory.createMap());
								mMap = mMapView.getMap();
								setUpMap();
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
	  		   
	  		   mMap.setCenter(markker.getCoordinate(),MapAnimation.NONE);
	  	   }
	    }

	    @Override
	    protected void onPause() {
	        mMapView.onPause();
	        super.onPause();
	    }

	    @Override
	    protected void onDestroy() {
	       // mMapView.onDestroy();
	        super.onDestroy();
	    }

	    @Override
	    public void onLowMemory() {
	        super.onLowMemory();
	    //    mMapView.onLowMemory();
	    }

	    @Override
	    public void onSaveInstanceState(Bundle outState) {
	        super.onSaveInstanceState(outState);
	    //    mMapView.onSaveInstanceState(outState);
	    }
}
