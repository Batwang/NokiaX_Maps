package com.example.hereapi_example;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;

import android.app.Activity;
import android.os.Bundle;

public class MultiMapDemoActivity extends Activity {

	Map HereMap1 = null;
	Map HereMap2 = null;
	Map HereMap3 = null;
	Map HereMap4 = null;
	
	protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.multimap_demo);
    
        setUpMapIfNeeded();
	}
	
	private void setUpMapIfNeeded() {  

	       if(HereMap1 == null){ 
			   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map1);
				// initialize the Map Fragment to create a map and
				// attached to the fragment
				mapFragment.init(new FragmentInitListener() {
					@Override
					public void onFragmentInitializationCompleted(InitError error) {
						if (error == InitError.NONE) {
							HereMap1 = (Map) mapFragment.getMap();					
							HereMap1.setCenter(MapFactory.createGeoCoordinate(40.72,-74.00), MapAnimation.NONE,8.0f,0,0);
						}else {
		                    
		                	String erro_note ="ERROR: initialize 1: " + error;
		                	System.out.println(erro_note);
		              }
					}
				});
	       }
	       
	       if(HereMap2 == null){ 
			   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map2);
				// initialize the Map Fragment to create a map and
				// attached to the fragment
				mapFragment.init(new FragmentInitListener() {
					@Override
					public void onFragmentInitializationCompleted(InitError error) {
						if (error == InitError.NONE) {
							HereMap2 = (Map) mapFragment.getMap();					
							HereMap2.setCenter(MapFactory.createGeoCoordinate(51.51,-0.12), MapAnimation.NONE,8.0f,0,0);
						}else {
		                    
		                	String erro_note ="ERROR: initialize 1: " + error;
		                	System.out.println(erro_note);
		              }
					}
				});
	       }
	    		      
	       if(HereMap3 == null){ 
			   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map3);
				// initialize the Map Fragment to create a map and
				// attached to the fragment
				mapFragment.init(new FragmentInitListener() {
					@Override
					public void onFragmentInitializationCompleted(InitError error) {
						if (error == InitError.NONE) {
							HereMap3 = (Map) mapFragment.getMap();					
							HereMap3.setCenter(MapFactory.createGeoCoordinate(48.85,2.35), MapAnimation.NONE,8.0f,0,0);
						}else {
		                    
		                	String erro_note ="ERROR: initialize 1: " + error;
		                	System.out.println(erro_note);
		              }
					}
				});
	       }
	       
	       if(HereMap4 == null){ 
			   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map4);
				// initialize the Map Fragment to create a map and
				// attached to the fragment
				mapFragment.init(new FragmentInitListener() {
					@Override
					public void onFragmentInitializationCompleted(InitError error) {
						if (error == InitError.NONE) {
							HereMap4 = (Map) mapFragment.getMap();					
							HereMap4.setCenter(MapFactory.createGeoCoordinate(35.69,139.69), MapAnimation.NONE,8.0f,0,0);
						}else {
		                    
		                	String erro_note ="ERROR: initialize 1: " + error;
		                	System.out.println(erro_note);
		              }
					}
				});
	       }
	}
}
