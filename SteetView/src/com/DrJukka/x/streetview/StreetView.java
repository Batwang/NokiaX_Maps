package com.DrJukka.x.streetview;


import com.DrJukka.x.streetview.R;
import android.app.TabActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.OnTabChangeListener;
import android.widget.TabHost.TabSpec;
import android.widget.Toast;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.restricted.mapping.Map;
import com.here.android.restricted.streetlevel.StreetLevelFragment;

@SuppressWarnings("deprecation")
public class StreetView extends TabActivity  {
 
	// map embedded in the map fragment
	private Map map = null;
	private StreetLevelFragment streetLevelFragment = null;
	  
	// map fragment embedded in this activity
	private MapFragment mapFragment = null;
	  
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
    	setContentView(R.layout.activity_street_view);
    	
    	final TabHost tabHost = getTabHost(); 
    	tabHost.setup();

    	TabSpec spec1=tabHost.newTabSpec("Map");
    	spec1.setContent(R.id.tab1);
    	spec1.setIndicator("Map");

    	TabSpec spec2=tabHost.newTabSpec("Street");
    	spec2.setIndicator("Street");
    	spec2.setContent(R.id.tab2);


    	tabHost.addTab(spec1);
    	tabHost.addTab(spec2);   
    	
    	tabHost.setOnTabChangedListener(new OnTabChangeListener() {
            @Override
            public void onTabChanged(String arg0) {         

               Toast.makeText(getApplicationContext(),"Tab selected: " +  tabHost.getCurrentTab(), Toast.LENGTH_LONG).show();
            
               if(streetLevelFragment != null && map != null){
	               if(tabHost.getCurrentTab() == 1){
	            	   MoveStreetLevelImage();
	               }else{
	            	   moveTheMap();
	               }
               }
            }      
        });  
    
        // Search for the map fragment to finish setup by calling init().
        mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.init(new FragmentInitListener() {
            @Override
            public void onFragmentInitializationCompleted(InitError error) {
                if (error == InitError.NONE) {
                    // retrieve a reference of the map from the map fragment
                    map = (Map) mapFragment.getMap();
                    map.setCenter(MapFactory.createGeoCoordinate(48.8567, 2.3508), MapAnimation.NONE);
                  
                //    mapFragment.getMapGesture().addMapGestureListener(that);
                    mapFragment.getMapGesture().setTiltEnabled(false);
                    
                    map.setStreetLevelCoverageVisible(true);
                    
                    streetLevelFragment = (StreetLevelFragment) getFragmentManager().findFragmentById(R.id.streetlevelfragment);
                    streetLevelFragment.init(new FragmentInitListener() {
                  		@Override
                  		public void onFragmentInitializationCompleted(InitError error) {
                  			if (error == InitError.NONE) { 
                  				streetLevelFragment.getStreetLevelModel().setOverlayTransparency(0.5f);                				                				
                  				MoveStreetLevelImage();
                  			}else{
                  				Toast.makeText(getApplicationContext(),"Streetlevel init error: " + error, Toast.LENGTH_LONG).show();
                  			}
                  		}
                  	});
                } else {
              	  Toast.makeText(getApplicationContext(),"Map init error: " + error, Toast.LENGTH_LONG).show();
                }
            }
        });
    
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.street_view, menu);
        return super.onCreateOptionsMenu(menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.action_about:
          	  	//Toast.makeText(getApplicationContext(),"Show About", Toast.LENGTH_LONG).show();
          	  	AboutDialog about = new AboutDialog(this);
          	  	about.setTitle("About Streetview");
  				about.show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    
    public void MoveStreetLevelImage() {
  	  if(streetLevelFragment != null && map != null){
  		  com.here.android.restricted.streetlevel.StreetLevel level =streetLevelFragment.getStreetLevelModel().getStreetLevel(map.getCenter(), 100);
  		  if (level != null) {
  			// Render street level imagery
  			streetLevelFragment.getStreetLevelModel().moveTo( level, false, map.getOrientation(), 0, 1.0f);
  		  }
  	  }
    }
    
    public void moveTheMap() {
    	// TODO Auto-generated method stub
    	 if(streetLevelFragment != null && map != null){
    		 map.setCenter(streetLevelFragment.getStreetLevelModel().getPosition(), MapAnimation.LINEAR, map.getZoomLevel(), streetLevelFragment.getStreetLevelModel().getHeading(), 0);
    	 }
    }
}