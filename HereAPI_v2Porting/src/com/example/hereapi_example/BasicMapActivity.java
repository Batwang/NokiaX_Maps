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

public class BasicMapActivity extends Activity {

	Map HereMap = null;
	
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.basic_demo); 
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
	                    
	                	String erro_note ="ERROR: Cannot initialize Map Fragment: ";
	                	
	                	switch(error){
	                		case DEVICE_NOT_SUPPORTED:
	                			erro_note = erro_note + "DEVICE_NOT_SUPPORTED";
	                			break;
	                		case DISK_CACHE_LOCKED:
	                			erro_note = erro_note + "DISK_CACHE_LOCKED";
	                			break;
	                		case FILE_RW_ERROR:
	                			erro_note = erro_note + "FILE_RW_ERROR";
	                			break;
	                		case MISSING_APP_CREDENTIAL:
	                			erro_note = erro_note + "MISSING_APP_CREDENTIAL";
	                			break;
	                		case MODEL_NOT_SUPPORTED:
	                			erro_note = erro_note + "MODEL_NOT_SUPPORTED";
	                			break;
	                		case USAGE_EXPIRED:
	                			erro_note = erro_note + " USAGE_EXPIRED";
	                			break;
	                		case BUSY:
	                			erro_note =erro_note + "BUSY";
	                			break;
	                		case UNKNOWN:
	                			erro_note =erro_note +  "UNKNOWN";
	                			break;
	                		default:
	                			erro_note = erro_note + "Something else";
	                			break;
	                	}
	                
	                
	                	System.out.println(erro_note);
	                	
	                	Toast.makeText(getBaseContext(),erro_note, Toast.LENGTH_LONG).show();
	                }
				}
			});
       }
    }
   
   private void setUpMap() {
	   if(HereMap != null){
		   MapMarker markker = MapFactory.createMapMarker();
		   markker.setCoordinate(HereMap.getCenter());
		   markker.setTitle("Marker");
		   HereMap.addMapObject(markker);
	   }
   }
}