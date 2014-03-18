package com.example.hereapi_example;

import com.here.android.common.GeoBoundingBox;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapMarker;

import android.app.Activity;
import android.graphics.Interpolator;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.OvershootInterpolator;

public class VisibleRegionDemoActivity extends Activity {

	private Map mMap;

    private static final double SOH_lat = -33.85704;
    private static final double SOH_lon = 151.21522;
    private static final double SFO_lat = 37.614631;
    private static final double SFO_lon = -122.385153;
    private static final double AUS_br_lat = -44;
    private static final double AUS_br_lon = 154;
    private static final double AUS_tl_lat = -10;
    private static final double AUS_tl_lon = 113;
    
    /** Keep track of current values for padding, so we can animate from them. */
    int currentLeft = 150;
    int currentTop = 0;
    int currentRight = 0;
    int currentBottom = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.visible_region_demo);
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    /**
     * Sets up the map if it is possible to do so (i.e., the Google Play services APK is correctly
     * installed) and the map has not already been instantiated.. This will ensure that we only ever
     * call {@link #setUpMap()} once when {@link #mMap} is not null.
     * <p>
     * If it isn't installed {@link SupportMapFragment} (and
     * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt for the user to
     * install/update the Google Play services APK on their device.
     * <p>
     * A user can return to this FragmentActivity after following the prompt and correctly
     * installing/updating/enabling the Google Play services. Since the FragmentActivity may not
     * have been completely destroyed during this process (it is likely that it would only be
     * stopped or paused), {@link #onCreate(Bundle)} may not be called again so we should call this
     * method in {@link #onResume()} to guarantee that it will be called.
     */
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
			// initialize the Map Fragment to create a map and
			// attached to the fragment
			mapFragment.init(new FragmentInitListener() {
				@Override
				public void onFragmentInitializationCompleted(InitError error) {
					if (error == InitError.NONE) {
						mMap = (Map) mapFragment.getMap();
						View mapView = mapFragment.getView();
						
						// turn MyLocation on and move to a place with indoor (SFO airport)
		                //mMap.setMyLocationEnabled(true);
						mapView.setPadding(currentLeft, currentTop, currentRight, currentBottom);
		                
						mMap.setCenter(MapFactory.createGeoCoordinate(SFO_lat,SFO_lon), MapAnimation.NONE,18,0,0);
		                
		                // Add a marker to the Opera House
		                MapMarker markker = MapFactory.createMapMarker();
		     		   	markker.setCoordinate(mMap.getCenter());
		     		   	markker.setTitle("Sydney Opera House");
		     		   	mMap.addMapObject(markker);
					}
				}
			});
        }
    }


    public void moveToOperaHouse(View view) {
       mMap.setCenter(MapFactory.createGeoCoordinate(SOH_lat,SOH_lon), MapAnimation.BOW,16,0,0);
   }

    public void moveToSFO(View view) {
    	mMap.setCenter(MapFactory.createGeoCoordinate(SFO_lat,SFO_lon), MapAnimation.BOW,18,0,0);
    }

    public void moveToAUS(View view) {
    	GeoBoundingBox bboxx = MapFactory.createGeoBoundingBox(MapFactory.createGeoCoordinate(AUS_tl_lat,AUS_tl_lon), MapFactory.createGeoCoordinate(AUS_br_lat,AUS_br_lon));
    	mMap.zoomTo(bboxx, MapAnimation.BOW, 0);
    }

    public void setNoPadding(View view) {
        animatePadding(150, 0, 0, 0);
    }

    public void setMorePadding(View view) {
    	final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
    	
        View mapView = mapFragment.getView();
        
        int left = 150;
        int top = 0;
        int right = mapView.getWidth() / 3;
        int bottom = mapView.getHeight() / 4;
        animatePadding(left, top, right, bottom);
    }

    public void animatePadding(final int toLeft, final int toTop, final int toRight, final int toBottom) {

        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        final long duration = 1000;

        final OvershootInterpolator interpolator = new OvershootInterpolator();

        final int startLeft = currentLeft;
        final int startTop = currentTop;
        final int startRight = currentRight;
        final int startBottom = currentBottom;

        currentLeft = toLeft;
        currentTop = toTop;
        currentRight = toRight;
        currentBottom = toBottom;

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed / duration);

                int left = (int) (startLeft + ((toLeft - startLeft) * t));
                int top = (int) (startTop + ((toTop - startTop) * t));
                int right = (int) (startRight + ((toRight - startRight) * t));
                int bottom = (int) (startBottom + ((toBottom - startBottom) * t));

                final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
            	
                View mapView = mapFragment.getView();
                mapView.setPadding(left, top, right, bottom);

                if (elapsed < duration) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                }
            }
        });
    }
    
}
