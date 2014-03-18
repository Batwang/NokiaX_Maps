package com.example.hereapi_example;

import java.util.ArrayList;

import com.here.android.common.GeoCoordinate;
import com.here.android.common.GeoPolyline;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapPolyline;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class PolylineDemoActivity  extends Activity implements OnSeekBarChangeListener {

	private static final double MELBOURNE_lat = -37.81319;
	private static final double MELBOURNE_lon = 144.96298;
	private static final double SYDNEY_lat = -33.87365;
	private static final double SYDNEY_lon = 151.20689;
	private static final double ADELAIDE_lat = -34.92873;
	private static final double ADELAIDE_lon = 138.59995;
	private static final double PERTH_lat = -31.95285;
	private static final double PERTH_lon = 115.85734;

	private static final double LHR_lat = 51.471547;
	private static final double LHR_lon = -0.460052;
	private static final double LAX_lat = 33.936524;
	private static final double LAX_lon = -118.377686;
	private static final double JFK_lat = 40.641051;
	private static final double JFK_lon = -73.777485;
	private static final double AKL_lat = -37.006254;
	private static final double AKL_lon = 174.783018;

    private static final int WIDTH_MAX = 50;
    private static final int HUE_MAX = 360;
    private static final int ALPHA_MAX = 255;

    private Map mMap= null;
    private MapPolyline mMutablePolyline = null;
    private SeekBar mColorBar;
    private SeekBar mAlphaBar;
    private SeekBar mWidthBar;
	
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.polyline_demo);

        mColorBar = (SeekBar) findViewById(R.id.hueSeekBar);
        mColorBar.setMax(HUE_MAX);
        mColorBar.setProgress(0);

        mAlphaBar = (SeekBar) findViewById(R.id.alphaSeekBar);
        mAlphaBar.setMax(ALPHA_MAX);
        mAlphaBar.setProgress(255);

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

    	if(mMap == null){
    		return;
    	}
    	
    	// A simple polyline with the default options from Melbourne-Adelaide-Perth.        
    	ArrayList<GeoCoordinate> pollyList1 = new ArrayList<GeoCoordinate> (3);
    	pollyList1.add(MapFactory.createGeoCoordinate(MELBOURNE_lat, MELBOURNE_lon));
    	pollyList1.add(MapFactory.createGeoCoordinate(ADELAIDE_lat, ADELAIDE_lon));
    	pollyList1.add(MapFactory.createGeoCoordinate(PERTH_lat, PERTH_lon));
    	
    	mMap.addMapObject(MapFactory.createMapPolyline(MapFactory.createGeoPolyline(pollyList1)));
    	
    	// A geodesic polyline that goes around the world.
    	ArrayList<GeoCoordinate> pollyList2 = new ArrayList<GeoCoordinate> (5);
    	pollyList2.add(MapFactory.createGeoCoordinate(LHR_lat, LHR_lon));
    	pollyList2.add(MapFactory.createGeoCoordinate(AKL_lat, AKL_lon));
    	pollyList2.add(MapFactory.createGeoCoordinate(LAX_lat, LAX_lon));
    	pollyList2.add(MapFactory.createGeoCoordinate(JFK_lat, JFK_lon));
    	pollyList2.add(MapFactory.createGeoCoordinate(LHR_lat, LHR_lon));
    	
    	MapPolyline tmpPolly = MapFactory.createMapPolyline(MapFactory.createGeoPolyline(pollyList2));
    	tmpPolly.setLineWidth(5);
    	tmpPolly.setLineColor(Color.BLUE);
    	tmpPolly.setGeodesicEnabled(true);
    	
    	mMap.addMapObject(tmpPolly);

        // Rectangle centered at Sydney.  This polyline will be mutable.
    	int radius = 5;
    	ArrayList<GeoCoordinate> pollyList3 = new ArrayList<GeoCoordinate> (5);
    	pollyList3.add(MapFactory.createGeoCoordinate(SYDNEY_lat + radius, SYDNEY_lon + radius));
    	pollyList3.add(MapFactory.createGeoCoordinate(SYDNEY_lat + radius, SYDNEY_lon - radius));
    	pollyList3.add(MapFactory.createGeoCoordinate(SYDNEY_lat - radius, SYDNEY_lon - radius));
    	pollyList3.add(MapFactory.createGeoCoordinate(SYDNEY_lat - radius, SYDNEY_lon + radius));
    	pollyList3.add(MapFactory.createGeoCoordinate(SYDNEY_lat + radius, SYDNEY_lon + radius));
    	
        int color = Color.HSVToColor(mAlphaBar.getProgress(), new float[] {mColorBar.getProgress(), 1, 1});
        
        GeoPolyline ZomPolly = MapFactory.createGeoPolyline(pollyList3);
        
        mMutablePolyline = MapFactory.createMapPolyline(ZomPolly);
    	tmpPolly.setLineWidth(mWidthBar.getProgress());
    	tmpPolly.setLineColor(color);

    	mMap.addMapObject(mMutablePolyline);

        mColorBar.setOnSeekBarChangeListener(this);
        mAlphaBar.setOnSeekBarChangeListener(this);
        mWidthBar.setOnSeekBarChangeListener(this);

        // Move the map so that it is centered on the mutable polyline.
        mMap.zoomTo(ZomPolly.getBoundingBox(), MapAnimation.NONE, 0);
    }
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (mMutablePolyline == null) {
            return;
        }

        if (seekBar == mColorBar) {
            mMutablePolyline.setLineColor(Color.HSVToColor(
                    Color.alpha(mMutablePolyline.getLineColor()), new float[] {progress, 1, 1}));
        } else if (seekBar == mAlphaBar) {
            float[] prevHSV = new float[3];
            Color.colorToHSV(mMutablePolyline.getLineColor(), prevHSV);
            mMutablePolyline.setLineColor(Color.HSVToColor(progress, prevHSV));
        } else if (seekBar == mWidthBar) {
            mMutablePolyline.setLineWidth(progress);
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
