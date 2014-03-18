package com.example.hereapi_example;

import java.util.List;

import com.here.android.common.GeoCoordinate;
import com.here.android.common.ViewObject;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapCircle;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.location.Location;
import android.os.Bundle;

interface  LocationChangedListener{
	void onLocationChanged(Location location);
}

public class LocationSourceDemoActivity  extends Activity
implements LocationChangedListener{


	private static class LongPressLocationSource implements MapGestureListener{
        private LocationChangedListener mListener = null;
        private Map mMap = null;
        /**
         * Flag to keep track of the activity's lifecycle. This is not strictly necessary in this
         * case because onMapLongPress events don't occur while the activity containing the map is
         * paused but is included to demonstrate best practices (e.g., if a background service were
         * to be used).
         */
        private boolean mPaused;

        public LongPressLocationSource(LocationChangedListener listener,Map map) {
            mListener = listener;
            mMap = map;
        }


        public void onPause() {
            mPaused = true;
        }

        public void onResume() {
            mPaused = false;
        }

        
        @Override
		public boolean onLongPressEvent(PointF p) {
			
        	if (mListener != null && !mPaused) {
        		Location location = new Location("LongPressLocationProvider");
                GeoCoordinate point = MapFactory.createGeoCoordinate(mMap.pixelToGeo(p));
          
                location.setLatitude(point.getLatitude());
                location.setLongitude(point.getLongitude());
                location.setAccuracy(100);
                mListener.onLocationChanged(location);
            }
        	
        	return false;
		}
		@Override
		public boolean onDoubleTapEvent(PointF p) {
			return false;
		}
		
		@Override
		public void onLongPressRelease() {}

		@Override
		public boolean onMapObjectsSelected(List<ViewObject> objects) {
			return false;
		}

		@Override
		public void onMultiFingerManipulationEnd() {}

		@Override
		public void onMultiFingerManipulationStart() {}

		@Override
		public void onPanEnd() {}

		@Override
		public void onPanStart() {}

		@Override
		public void onPinchLocked() {}

		@Override
		public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
			return false;
		}

		@Override
		public boolean onRotateEvent(float rotateAngle) {
			return false;
		}

		@Override
		public void onRotateLocked() {}

		@Override
		public boolean onTapEvent(PointF p) {
			return false;
		}

		@Override
		public boolean onTiltEvent(float angle) {
			return false;
		}

		@Override
		public boolean onTwoFingerTapEvent(PointF p) {
			return false;
		}
    }

	private LongPressLocationSource mLocationSource = null;
	Map  mMap = null;
	private MapMarker centerMarker = null;
	private MapCircle circle = null;
	
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

	        if(mLocationSource != null){
	        	mLocationSource.onResume();
	        }
	    }

	    private void setUpMapIfNeeded() {
	    	
	 		  final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
	 			
	 		  final LocationSourceDemoActivity that = this;
	 		   // initialize the Map Fragment to create a map and
	 			// attached to the fragment
	 			mapFragment.init(new FragmentInitListener() {
	 				@Override
	 				public void onFragmentInitializationCompleted(InitError error) {
	 					if (error == InitError.NONE) {
	 						mMap = (Map) mapFragment.getMap();
	 						mLocationSource = new LongPressLocationSource(that,mMap);
	 						mapFragment.getMapGesture().addMapGestureListener(mLocationSource);
	 						
	 						//mMap.setMyLocationEnabled(true);
	 					}
	 				}
	 			});
	    	}

	    @Override
	    protected void onPause() {
	        super.onPause();
	        if(mLocationSource != null){
	        	mLocationSource.onPause();
	        }
	    }

		@Override
		public void onLocationChanged(Location location) {
			if(location == null){
				
			}else if(centerMarker != null && circle != null){
				circle.setRadius(location.getAccuracy());
				circle.setCenter(MapFactory.createGeoCoordinate(location.getLatitude(),location.getLongitude()));
				centerMarker.setCoordinate(circle.getCenter());
			}else{
				circle = MapFactory.createMapCircle(location.getAccuracy(),MapFactory.createGeoCoordinate(location.getLatitude(),location.getLongitude()));
	            circle.setLineWidth(2);
	            circle.setLineColor(Color.BLACK);
	            circle.setFillColor(Color.argb(50, 0xFF, 0x00,0x00));
	            mMap.addMapObject(circle);
            
	            centerMarker = MapFactory.createMapMarker();
	            centerMarker.setCoordinate(circle.getCenter());
	            centerMarker.setTitle("centerMarker");
	            centerMarker.setDraggable(true);
	            mMap.addMapObject(centerMarker);
			}
		}
}
