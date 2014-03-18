package com.example.hereapi_example;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;

import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class MyLocationDemoActivity  extends Activity 
implements LocationListener
//ConnectionCallbacks,
//OnConnectionFailedListener,
//OnMyLocationButtonClickListener 
{
	Map HereMap = null;

    private LocationManager mLocationClient;
    private TextView mMessageView;

    boolean isGPSEnabled = false;
    boolean isNetworkEnabled = false;
    Location lastLocation = null; 
    
    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 10; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1; // 1 minute
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.my_location_demo);
        mMessageView = (TextView) findViewById(R.id.message_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
     
        lastLocation = getLocation();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mLocationClient != null) {
            mLocationClient.removeUpdates(this);
            mLocationClient = null;
        }
    }
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
  		   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
  			// initialize the Map Fragment to create a map and
  			// attached to the fragment
  			mapFragment.init(new FragmentInitListener() {
  				@Override
  				public void onFragmentInitializationCompleted(InitError error) {
  					if (error == InitError.NONE) {
  						HereMap = (Map) mapFragment.getMap();

  						//mMap.setMyLocationEnabled(true);
  		                //mMap.setOnMyLocationButtonClickListener(this); 
  					}
  				}
  			});
    }

    public void showMyLocation(View view) {
        if (lastLocation != null ) {
            String msg = "Location = " + lastLocation;
            Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
        }
    }
    
    
    public Location getLocation() {
    	
    	Location location = null; 
    	
        try {
        	mLocationClient = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
 
            // getting enabled status
            isGPSEnabled = mLocationClient.isProviderEnabled(LocationManager.GPS_PROVIDER);
            isNetworkEnabled = mLocationClient.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
 
            if (!isGPSEnabled && !isNetworkEnabled) {
                Toast.makeText(this, "no network provider is enabled", Toast.LENGTH_LONG).show();
            } else {
                
                // if GPS Enabled get lat/long using GPS Services
                if (isGPSEnabled) {
                	mMessageView.setText("using GPS");
                	location = mLocationClient.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                	
                	mLocationClient.requestLocationUpdates(
                                LocationManager.GPS_PROVIDER,
                                MIN_TIME_BW_UPDATES,
                                MIN_DISTANCE_CHANGE_FOR_UPDATES, this);
                }else if (isNetworkEnabled) {
                	mMessageView.setText("using network positioning");
                	location = mLocationClient.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                	
                	mLocationClient.requestLocationUpdates(
                            LocationManager.NETWORK_PROVIDER,
                            MIN_TIME_BW_UPDATES,
                            MIN_DISTANCE_CHANGE_FOR_UPDATES, this);                        
                }
            }
 
        } catch (Exception e) {
            e.printStackTrace();
        }
 
        return location;
    }

	@Override
	public void onLocationChanged(Location location) {
		// TODO Auto-generated method stub
		lastLocation = location;
		mMessageView.setText("Location = " + location);
	
		if(HereMap != null){
			HereMap.setCenter(MapFactory.createGeoCoordinate(location.getLatitude(),location.getLongitude()), MapAnimation.LINEAR);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		mMessageView.setText("onProviderDisabled = " + provider);
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		mMessageView.setText("onProviderEnabled = " + provider);
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		mMessageView.setText("onStatusChanged for " + provider + " to " + status);
	}

}
