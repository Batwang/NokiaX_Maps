package com.example.hereapi_example;

import java.util.ArrayList;

import com.here.android.common.GeoCoordinate;
import com.here.android.common.GeoPolygon;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapPolygon;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PolygonDemoActivity  extends Activity implements OnSeekBarChangeListener{

	private static final double SYDNEY_lat = -33.87365;
    private static final double SYDNEY_lon = 151.20689;
    
    private Map mMap= null;
    
    private static final int WIDTH_MAX = 50;
    private static final int HUE_MAX = 360;
    private static final int ALPHA_MAX = 255;

    private MapPolygon mMutablePolygon = null;

    private SeekBar mColorBar;
    private SeekBar mAlphaBar;
    private SeekBar mWidthBar;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polygon_demo);

        mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
        mColorBar.setMax(HUE_MAX);
        mColorBar.setProgress(0);

        mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
        mAlphaBar.setMax(ALPHA_MAX);
        mAlphaBar.setProgress(127);

        mWidthBar = (SeekBar) findViewById(R.id.widthSeekBar);
        mWidthBar.setMax(WIDTH_MAX);
        mWidthBar.setProgress(10);

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
        // Create a rectangle with two rectangular holes.
    	if(mMap == null){
    		return;
    	}
    	
    	ArrayList<GeoPolygon> holesList = new ArrayList<GeoPolygon> (2);
    	holesList.add(createRectangle(-22, 128, 1, 1));
    	holesList.add(createRectangle(-18, 133, 0.5, 1.5));
    	
    	GeoPolygon polly = createRectangle(-20, 130, 5, 5);
    	
    	MapPolygon tmpPolyn = MapFactory.createMapPolygon(polly, holesList);
    	
    	tmpPolyn.setFillColor(Color.CYAN);
    	tmpPolyn.setLineColor(Color.BLUE);
    	tmpPolyn.setLineWidth(5);

    	mMap.addMapObject(tmpPolyn);
    	
        // Create a rectangle centered at Sydney.
    	GeoPolygon options = createRectangle(SYDNEY_lat, SYDNEY_lon, 5, 8);
    	
        int fillColor = Color.HSVToColor(mAlphaBar.getProgress(), new float[] {mColorBar.getProgress(), 1, 1});
       
        mMutablePolygon = MapFactory.createMapPolygon(options);
        mMutablePolygon.setFillColor(fillColor);
        mMutablePolygon.setLineColor(Color.BLACK);
        mMutablePolygon.setLineWidth(mWidthBar.getProgress());

    	mMap.addMapObject(mMutablePolygon);
    	
        mColorBar.setOnSeekBarChangeListener(this);
        mAlphaBar.setOnSeekBarChangeListener(this);
        mWidthBar.setOnSeekBarChangeListener(this);

     //   Toast.makeText(this, "zomto: " + options.getBoundingBox().getTopLeft() + ", " + options.getBoundingBox().getBottomRight(), Toast.LENGTH_LONG).show();
        
        // Move the map so that it is centered on the mutable polygon.
        mMap.zoomTo(options.getBoundingBox(), MapAnimation.NONE, 0);
   
  //      mMap.setCenter(options.getBoundingBox().getTopLeft(), MapAnimation.NONE);
    }
    
    /**
     * Creates a List of LatLngs that form a rectangle with the given dimensions.
     */
    private GeoPolygon createRectangle(double lat, double lon, double halfWidth, double halfHeight) {
    	// Create the GeoPolygon required by MapPolygon.
        GeoPolygon geoPolygon = null;
        try {
            ArrayList<GeoCoordinate> pointList = new ArrayList<GeoCoordinate> (5);
            pointList.add(MapFactory.createGeoCoordinate((lat - halfHeight), (lon - halfWidth)));
            pointList.add(MapFactory.createGeoCoordinate((lat - halfHeight), (lon + halfWidth)));
            pointList.add(MapFactory.createGeoCoordinate((lat + halfHeight), (lon + halfWidth)));
            pointList.add(MapFactory.createGeoCoordinate((lat + halfHeight), (lon - halfWidth)));
            pointList.add(MapFactory.createGeoCoordinate((lat - halfHeight), (lon - halfWidth)));
            
            geoPolygon = MapFactory.createGeoPolygon(pointList);
       } catch (Exception ex) {
                // nothing to be done
       }
        
        return geoPolygon;		
    }
    

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		 if (mMutablePolygon == null) {
	            return;
	        }

	        if (seekBar == mColorBar) {
	            mMutablePolygon.setFillColor(Color.HSVToColor(Color.alpha(mMutablePolygon.getFillColor()), new float[] {progress, 1, 1}));
	        } else if (seekBar == mAlphaBar) {
	            int prevColor = mMutablePolygon.getFillColor();
	            mMutablePolygon.setFillColor(Color.argb(progress, Color.red(prevColor), Color.green(prevColor),Color.blue(prevColor)));
	        } else if (seekBar == mWidthBar) {
	            mMutablePolygon.setLineWidth(progress);
	        }	
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
		// TODO Auto-generated method stub
		
	}
	
}
