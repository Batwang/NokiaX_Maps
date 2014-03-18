package com.example.hereapi_example;

import android.app.Activity;
import android.os.Bundle;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapScheme;


public class OptionsDemoActivity  extends Activity {

	Map HereMap = null;
	 
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	    setContentView(R.layout.options_demo);
	    setUpMapIfNeeded();
	   }
	        
	@Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }
	        
	 private void setUpMapIfNeeded() {        
         if(HereMap == null){ 
  		   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
  			// initialize the Map Fragment to create a map and
  			// attached to the fragment
  			mapFragment.init(new FragmentInitListener() {
  				@Override
  				public void onFragmentInitializationCompleted(InitError error) {
  					if (error == InitError.NONE) {
  						HereMap = (Map) mapFragment.getMap();
  						setUpMap();
  					}else {
  	                    
  	                	
  	                }
  				}
  			});
         }
      }
     
     private void setUpMap() {
  	   if(HereMap != null){		   
  		   /*
			map:cameraBearing="112.5"
			map:cameraTargetLat="-33.796923"
			map:cameraTargetLng="150.922433"
			map:cameraTilt="30"
			map:cameraZoom="13"
  		    */
  	   	HereMap.setCenter(MapFactory.createGeoCoordinate(-33.796923, 150.922433), MapAnimation.NONE,13.0,112.5f,30.0f);
  	   	 
  	   		/*
			map:mapType="normal"
			*/
  	   	HereMap.setMapScheme(MapScheme.NORMAL_DAY);
  	   		
  	   		/*
			map:uiCompass="false"
			map:uiRotateGestures="true"
			map:uiScrollGestures="false"
			map:uiTiltGestures="true"
			map:uiZoomControls="false"
			map:uiZoomGestures="true"/>
  	   		 */
  	   }
     }
  }	        
	      
