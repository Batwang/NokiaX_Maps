package com.example.hereapi_example;


import com.here.android.common.GeoCoordinate;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.Map.MapTransformListener;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapState;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;

public class CameraDemoActivity extends Activity implements MapTransformListener {

    //private static final int SCROLL_BY_PX = 100;
    
    MapState BONDI = null;
    MapState SYDNEY= null;
   		
    
    Map HereMap = null;
    
    private CompoundButton mAnimateToggle;
    private CompoundButton mCustomDurationToggle;
    private SeekBar mCustomDurationBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera_demo);

       mAnimateToggle = (CompoundButton) findViewById(R.id.animate);
        mCustomDurationToggle = (CompoundButton) findViewById(R.id.duration_toggle);
        mCustomDurationBar = (SeekBar) findViewById(R.id.duration_bar);

        updateEnabledState();
        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateEnabledState();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
    	if(HereMap == null){ 
    	   final MapTransformListener that = this;
 		   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
 			// initialize the Map Fragment to create a map and
 			// attached to the fragment
 			mapFragment.init(new FragmentInitListener() {
 				@Override
 				public void onFragmentInitializationCompleted(InitError error) {
 					if (error == InitError.NONE) {
 						HereMap = (Map) mapFragment.getMap();
 						HereMap.setZoomLevel(10);
 						HereMap.setCenter(MapFactory.createGeoCoordinate(-33.87365,151.20689), MapAnimation.NONE);
 						HereMap.addMapTransformListener(that);
 						
 						BONDI = new MapState(50,300,15.5f,MapFactory.createGeoCoordinate(-33.891614, 151.276417));
 					    SYDNEY= new MapState(0,25,15.5f,MapFactory.createGeoCoordinate(-33.87365, 151.20689));
 					}
 				}
 			});
        }
    }
    
    /**
     * When the map is not ready the CameraUpdateFactory cannot be used. This should be called on
     * all entry points that call methods on the Google Maps API.
     */
    private boolean checkReady() {
        if (HereMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /**
     * Called when the Go To Bondi button is clicked.
     */
    public void onGoToBondi(View view) {
    	if (HereMap != null && BONDI!= null ) {
    		HereMap.setCenter(BONDI.getCenter(), GetAnimationMode(),BONDI.getZoomLevel(),BONDI.getOrientation(),BONDI.getTilt()); 
    	}
    }

    /**
     * Called when the Animate To Sydney button is clicked.
     */
    public void onGoToSydney(View view) {
    	if (HereMap != null && SYDNEY!= null ) {
    		HereMap.setCenter(SYDNEY.getCenter(), GetAnimationMode(),SYDNEY.getZoomLevel(),SYDNEY.getOrientation(),SYDNEY.getTilt());
    	}
    }

	@Override
	public void onMapTransformEnd(MapState mapState) {
		if (HereMap != null && SYDNEY!= null ) {

			if(SYDNEY.getCenter().distanceTo(mapState.getCenter()) == 0){
//			&& (SYDNEY.getZoomLevel() == mapState.getZoomLevel())
	//		&& (SYDNEY.getOrientation() == mapState.getOrientation())
		//	&& (SYDNEY.getTilt() == mapState.getTilt())){
				Toast.makeText(getBaseContext(), "Animation to Sydney complete", Toast.LENGTH_SHORT).show();
			}
		}
	}

	@Override
	public void onMapTransformStart() {
		// TODO Auto-generated method stub
		
	}
    
	   /**
     * Called when the stop button is clicked.
     */
    public void onStopAnimation(View view) {
    	Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
    	}

    
    /**
     * Called when the zoom in button (the one with the +) is clicked.
     */
	public void doZoom(double ZoomChange) {
		// TODO Auto-generated method stub
		if (checkReady()) {
			double newZoom = HereMap.getZoomLevel() + ZoomChange;
			if((newZoom >= HereMap.getMinZoomLevel())
			&& (newZoom <= HereMap.getMaxZoomLevel())){
				HereMap.setZoomLevel(newZoom);
			}
        }
	}
	
    public void onZoomIn(View view) {
    	doZoom(-1);
    }
    public void onZoomOut(View view) {
    	doZoom(1);
    }

    /**
     * Called when the left arrow button is clicked. This causes the camera to move to the left
     */
    public void onScrollLeft(View view) {
    	if (HereMap != null){
    		GeoCoordinate centr = HereMap.getCenter();
    		PointF centPoint = HereMap.geoToPixel(centr);
    		centPoint.x = (centPoint.x - (HereMap.getWidth()/10));
    		HereMap.setCenter(HereMap.pixelToGeo(centPoint), MapAnimation.LINEAR);
    	}
    }

    /**
     * Called when the right arrow button is clicked. This causes the camera to move to the right.
     */
    public void onScrollRight(View view) {
    	if (HereMap != null){
    		GeoCoordinate centr = HereMap.getCenter();
    		PointF centPoint = HereMap.geoToPixel(centr);
    		centPoint.x = (centPoint.x + (HereMap.getWidth()/10));
    		HereMap.setCenter(HereMap.pixelToGeo(centPoint), MapAnimation.LINEAR);
    	}
    }

    /**
     * Called when the up arrow button is clicked. The causes the camera to move up.
     */
    public void onScrollUp(View view) {
    	if (HereMap != null){
    		GeoCoordinate centr = HereMap.getCenter();
    		PointF centPoint = HereMap.geoToPixel(centr);
    		centPoint.y = (centPoint.y - (HereMap.getHeight()/10));
    		HereMap.setCenter(HereMap.pixelToGeo(centPoint), MapAnimation.LINEAR);
    	}
    }

    /**
     * Called when the down arrow button is clicked. This causes the camera to move down.
     */
    public void onScrollDown(View view) {
    	if (HereMap != null){
    		GeoCoordinate centr = HereMap.getCenter();
    		PointF centPoint = HereMap.geoToPixel(centr);
    		centPoint.y = (centPoint.y + (HereMap.getHeight()/10));
    		HereMap.setCenter(HereMap.pixelToGeo(centPoint), MapAnimation.LINEAR);
    	}
    }

    
    /**
     * Called when the animate button is toggled
     */
    public void onToggleAnimate(View view) {
        updateEnabledState();
    }

    /**
     * Called when the custom duration checkbox is toggled
     */
    public void onToggleCustomDuration(View view) {
    	Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
    }

    /**
     * Update the enabled state of the custom duration controls.
     */
    private void updateEnabledState() {
        mCustomDurationToggle.setEnabled(mAnimateToggle.isChecked());
        mCustomDurationBar.setEnabled(mAnimateToggle.isChecked() && mCustomDurationToggle.isChecked());
    }

    /**
     * 
     */
    private MapAnimation GetAnimationMode() {
        if (mAnimateToggle.isChecked()) {
            return MapAnimation.LINEAR;
        } else {
        	return MapAnimation.NONE;
        }
    }
}