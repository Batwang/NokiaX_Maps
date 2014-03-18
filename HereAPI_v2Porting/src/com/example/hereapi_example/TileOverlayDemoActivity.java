package com.example.hereapi_example;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapOverlayType;

import com.here.android.restricted.mapping.Map;
import com.here.android.restricted.mapping.MapRasterTileSource;
import com.here.android.restricted.mapping.UrlMapRasterTileSourceBase;

import android.app.Activity;
import android.os.Bundle;

public class TileOverlayDemoActivity  extends Activity {
	
    private Map mMap = null;
    private LiveMapRasterTileSource mTileSource = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
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
						setUpMap();
					}
				}
			});
        }
    }

    private void setUpMap() {
    	if (mMap != null) {
	    	// we do not have option on not having tiles as scheme with HERE
	        //mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
	
	    	mTileSource = new LiveMapRasterTileSource();
	
			mMap.addRasterTileSource(mTileSource);
    	}
    }

    
    public class LiveMapRasterTileSource extends UrlMapRasterTileSourceBase {

    	/** This returns moon tiles. */
        private static final String MOON_MAP_URL_FORMAT =
                "http://mw1.google.com/mw-planetary/lunar/lunarmaps_v1/clem_bw/%d/%d/%d.jpg";

    	public LiveMapRasterTileSource() {
    		// We want the tiles placed over everything else
    		setOverlayType(MapOverlayType.FOREGROUND_OVERLAY);
    		// We don't want the map visible beneath the tiles
    		setTransparency(Transparency.TRANSPARENCY_OFF);
    		// We don't want the tiles visible between these zoom levels
    		hideAtZoomRange(12, 20);
    		// Do not cache tiles
    		setCachingEnabled(false);
    	}

    	// Implementation of UrlMapRasterTileSourceBase
    	public String getUrl(int x, int y, int zoomLevel) {

    		// The moon tile coordinate system is reversed.  This is not normal.
            int reversedY = (1 << zoomLevel) - y - 1;
            String url = String.format(Locale.US, MOON_MAP_URL_FORMAT, zoomLevel, x, reversedY);
           
            return url;
    	}
    }
}
