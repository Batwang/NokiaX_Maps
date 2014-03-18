package com.example.hereapi_example;

import java.util.ArrayList;
import java.util.List;

import com.here.android.common.GeoBoundingBox;
import com.here.android.common.GeoCoordinate;
import com.here.android.common.ViewObject;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapCircle;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapMarkerDragListener;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class CircleDemoActivity extends Activity
implements OnSeekBarChangeListener, MapMarkerDragListener,  MapGestureListener //OnMarkerDragListener, OnMapLongClickListener
{	
    private static final double DEFAULT_RADIUS = 1000000;
    public static final double RADIUS_OF_EARTH_METERS = 6371009;

    private static final int WIDTH_MAX = 50;
    private static final int HUE_MAX = 360;
    private static final int ALPHA_MAX = 255;

    private SeekBar mColorBar;
    private SeekBar mAlphaBar;
    private SeekBar mWidthBar;
    private int mStrokeColor = Color.BLACK;
    private int mFillColor = Color.YELLOW;
    
    private static final double SYDNEY_lat = -33.87365;
    private static final double SYDNEY_lon = 151.20689;
    
    private List<DraggableCircle> mCircles = new ArrayList<DraggableCircle>(1);
    private Map mMap= null;

    
    private class DraggableCircle {
        private MapMarker centerMarker = null;
        private MapMarker radiusMarker = null;
        private MapCircle circle = null;
        private double radius;
        
        public DraggableCircle(GeoCoordinate center, double radius) {
            this.radius = radius;
            
	            circle = MapFactory.createMapCircle(radius, center);
	            circle.setLineWidth(mWidthBar.getProgress());
	            circle.setLineColor(mStrokeColor);
	            circle.setFillColor(mFillColor);
	            mMap.addMapObject(circle);
            
	            centerMarker = MapFactory.createMapMarker();
	            centerMarker.setCoordinate(center);
	            centerMarker.setTitle("centerMarker");
	            centerMarker.setDraggable(true);
	            mMap.addMapObject(centerMarker);
	         	
	            radiusMarker = MapFactory.createMapMarker(150.0f);
	            radiusMarker.setCoordinate(toRadiusLatLng(center, radius));
	            radiusMarker.setTitle("radiusMarker");
	            radiusMarker.setDraggable(true);
	            mMap.addMapObject(radiusMarker);
	
	
        }
        public GeoCoordinate getCenter(){
        	GeoCoordinate ret = null;
        	if(centerMarker != null){
        		ret = centerMarker.getCoordinate();
        	}
        	return ret;
        }
        
        public boolean onMarkerMoved(MapMarker marker) {
        	if(centerMarker != null){ 
	            if (centerMarker.equals(marker)) {
	                circle.setCenter(marker.getCoordinate());
	                radiusMarker.setCoordinate(toRadiusLatLng(marker.getCoordinate(), radius));
	                return true;
	            }else if (radiusMarker.equals(marker)) {
	                 radius = toRadiusMeters(centerMarker.getCoordinate(), radiusMarker.getCoordinate());
	                 circle.setRadius(radius);
	                 return true;
	            }
        	}
        	return false;
        }
        public void onStyleChange() {
        	if(circle != null){ 
        		circle.setLineWidth(mWidthBar.getProgress());
        		circle.setFillColor(mFillColor);
        		circle.setLineColor(mStrokeColor);
        	}
        }
    }
	
    
    /** Generate LatLng of radius marker */
    private static GeoCoordinate toRadiusLatLng(GeoCoordinate center, double radius) {
        double radiusAngle = Math.toDegrees(radius / RADIUS_OF_EARTH_METERS) / Math.cos(Math.toRadians(center.getLatitude()));
        return MapFactory.createGeoCoordinate(center.getLatitude(), center.getLongitude() + radiusAngle);
    }
    
    private static double toRadiusMeters(GeoCoordinate center, GeoCoordinate radius) {
        return center.distanceTo(radius);
    }
    
    
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (seekBar == mColorBar) {
            mFillColor = Color.HSVToColor(Color.alpha(mFillColor), new float[] {progress, 1, 1});
        } else if (seekBar == mAlphaBar) {
            mFillColor = Color.argb(progress, Color.red(mFillColor), Color.green(mFillColor),
                  Color.blue(mFillColor));
        }

        for (DraggableCircle draggableCircle : mCircles) {
            draggableCircle.onStyleChange();
        }
		
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {// Don't do anything here.
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {// Don't do anything here.
	}
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.circle_demo);

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
    	   final CircleDemoActivity that = this;
 		   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
 			// initialize the Map Fragment to create a map and
 			// attached to the fragment
 			mapFragment.init(new FragmentInitListener() {
 				@Override
 				public void onFragmentInitializationCompleted(InitError error) {
 					if (error == InitError.NONE) {
 						mMap = (Map) mapFragment.getMap();
 						mapFragment.getMapGesture().addMapGestureListener(that);
 						mapFragment.setMapMarkerDragListener(that); 
 						
 						setUpMap();
 					}
 				}
 			});
        }
    }
    
    private void setUpMap() {
    	if(mMap != null){ 
	        mColorBar.setOnSeekBarChangeListener(this);
	        mAlphaBar.setOnSeekBarChangeListener(this);
	        mWidthBar.setOnSeekBarChangeListener(this);

	        // callbacks handled via Gestures, listeneer added earlier.
	        mFillColor = Color.HSVToColor(mAlphaBar.getProgress(), new float[] {mColorBar.getProgress(), 1, 1});
	        mStrokeColor = Color.BLACK;
	
	        DraggableCircle circle = new DraggableCircle(MapFactory.createGeoCoordinate(SYDNEY_lat, SYDNEY_lon), DEFAULT_RADIUS);
	        mCircles.add(circle);
	     // Move the map so that it is centered on the initial circle
	        mMap.setCenter(circle.getCenter(), MapAnimation.NONE,4.0f,0,0);
      
	         
    	}
    }

    @Override
	public void onMarkerDrag(MapMarker marker) {onMarkerMoved(marker);}
	@Override
	public void onMarkerDragEnd(MapMarker marker) {onMarkerMoved(marker);}
	@Override
	public void onMarkerDragStart(MapMarker marker) {onMarkerMoved(marker);}

	private void onMarkerMoved(MapMarker marker) {
		if(mMap != null){ 
	        for (DraggableCircle draggableCircle : mCircles) {
	            if (draggableCircle.onMarkerMoved(marker)) {
	                break;
	            }
	        }
		}
    }

	@Override
	public boolean onLongPressEvent(PointF point) {
		if(mMap != null){
			// We know the center, let's place the outline at a point 3/4 along the view.
			GeoBoundingBox bbox = mMap.getBoundingBox();
	   
			double raddius = (bbox.getTopLeft().distanceTo(bbox.getBottomRight()) / 4);

	        // ok create it
	        DraggableCircle circle = new DraggableCircle(mMap.pixelToGeo(point), raddius);
	        mCircles.add(circle);
			return true;
		}
		return false;
	}
	@Override
	public void onLongPressRelease() {}
	@Override
	public boolean onDoubleTapEvent(PointF arg0) {return false;}
	@Override
	public boolean onMapObjectsSelected(List<ViewObject> arg0) {return false;}
	@Override
	public void onMultiFingerManipulationEnd() {}
	@Override
	public void onMultiFingerManipulationStart() {}
	@Override
	public void onPanEnd() {}
	@Override
	public void onPanStart() {	}
	@Override
	public void onPinchLocked() {}
	@Override
	public boolean onPinchZoomEvent(float arg0, PointF arg1) {return false;}
	@Override
	public boolean onRotateEvent(float arg0) {return false;}
	@Override
	public void onRotateLocked() {}
	@Override
	public boolean onTapEvent(PointF arg0) {return false;}
	@Override
	public boolean onTiltEvent(float arg0) {return false;}
	@Override
	public boolean onTwoFingerTapEvent(PointF arg0) {return false;}

	
}
