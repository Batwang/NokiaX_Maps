package com.example.hereapi_example;

import java.util.List;
import java.util.Random;

import com.here.android.common.GeoCoordinate;
import com.here.android.common.ViewObject;
import com.here.android.common.ViewObject.ViewObjectType;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapMarkerDragListener;
import com.here.android.mapping.MapObject;
import com.here.android.mapping.MapObjectType;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

public class SaveStateDemoActivity  extends Activity {

	 /** Default marker position when the activity is first created. */
    private static final double DEFAULT_MARKER_POSITION_LAT = 48.858179;
    private static final double DEFAULT_MARKER_POSITION_LON = 2.294576;

    /** List of hues to use for the marker */
    private static final float[] MARKER_HUES = new float[]{
    	0.0f,
    	30.0f,
    	60.0f,
    	120.0f,
    	180.0f,
    	210.0f,
    	240.0f,
    	270.0f,
    	300.0f,
    	330.0f,
    };

    
    // Bundle keys.
    private static final String OTHER_OPTIONS = "options";
    private static final String MARKER_POSITION_LAT = "markerPositionlat";
    private static final String MARKER_POSITION_LON = "markerPositionlon";
    private static final String MARKER_INFO = "markerInfo";

    /**
     * Extra info about a marker.
     */
    static class MarkerInfo implements Parcelable {

        public static final Parcelable.Creator<MarkerInfo> CREATOR =
                new Parcelable.Creator<MarkerInfo>() {
                    @Override
                    public MarkerInfo createFromParcel(Parcel in) {
                        return new MarkerInfo(in);
                    }

                    @Override
                    public MarkerInfo[] newArray(int size) {
                        return new MarkerInfo[size];
                    }
                };

        float mHue;

        public MarkerInfo(float color) {
            mHue = color;
        }

        private MarkerInfo(Parcel in) {
            mHue = in.readFloat();
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeFloat(mHue);
        }
    }

    /**
     * Example of a custom {@code MapFragment} showing how the position of a marker and other custom
     * {@link Parcelable}s objects can be saved after rotation of the device.
     * <p>
     * Storing custom {@link Parcelable} objects directly in the {@link Bundle} provided by the
     * {@link #onActivityCreated(Bundle)} method will throw a {@code ClassNotFoundException}. This
     * is due to the fact that this Bundle is parceled (thus losing its ClassLoader attribute at
     * this moment) and unparceled later in a different ClassLoader.
     * <br>
     * A workaround to store these objects is to wrap the custom {@link Parcelable} objects in a new
     * {@link Bundle} object.
     * <p>
     * However, note that it is safe to store {@link Parcelable} objects from the Maps API (eg.
     * MarkerOptions, LatLng, etc.) directly in the Bundle provided by the
     * {@link #onActivityCreated(Bundle)} method.
     */
    public static class SaveStateMapFragment extends MapFragment
            implements MapMarkerDragListener,  MapGestureListener {

        private Map mMap;
        private double mMarkerPosition_lat;
        private double mMarkerPosition_lon;
        private MarkerInfo mMarkerInfo;
        private boolean mMoveCameraToMarker;
        MapMarker MapMarkker = null;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (savedInstanceState == null) {
                // Activity created for the first time.
            	mMarkerPosition_lat = DEFAULT_MARKER_POSITION_LAT;
            	mMarkerPosition_lon = DEFAULT_MARKER_POSITION_LON;
                mMarkerInfo = new MarkerInfo(120.0f);
                mMoveCameraToMarker = true;
            } else {
                // Extract the state of the MapFragment:
                // - Objects from the API (eg. LatLng, MarkerOptions, etc.) were stored directly in
                //   the savedInsanceState Bundle.
                // - Custom Parcelable objects were wrapped in another Bundle.

                mMarkerPosition_lat = savedInstanceState.getDouble(MARKER_POSITION_LAT);
                mMarkerPosition_lon = savedInstanceState.getDouble(MARKER_POSITION_LON);

                Bundle bundle = savedInstanceState.getBundle(OTHER_OPTIONS);
                mMarkerInfo = bundle.getParcelable(MARKER_INFO);

                mMoveCameraToMarker = false;
            }

            setUpMapIfNeeded();
        }

        @Override
        public void onResume() {
            super.onResume();
            setUpMapIfNeeded();
        }

        @Override
        public void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);

            // All Parcelable objects of the API  (eg. LatLng, MarkerOptions, etc.) can be set
            // directly in the given Bundle.
            outState.putDouble(MARKER_POSITION_LAT, mMarkerPosition_lat);
            outState.putDouble(MARKER_POSITION_LON, mMarkerPosition_lon);
            
            // All other custom Parcelable objects must be wrapped in another Bundle. Indeed,
            // failing to do so would throw a ClassNotFoundException. This is due to the fact that
            // this Bundle is being parceled (losing its ClassLoader at this time) and unparceled
            // later in a different ClassLoader.
            Bundle bundle = new Bundle();
            bundle.putParcelable(MARKER_INFO, mMarkerInfo);
            outState.putBundle(OTHER_OPTIONS, bundle);
        }

        private void setUpMapIfNeeded() {
            // Do a null check to confirm that we have not already instantiated the map.
            if (mMap == null) {
            	final SaveStateMapFragment that = this;
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
        		MapMarkker = MapFactory.createMapMarker(mMarkerInfo.mHue);
        		MapMarkker.setCoordinate(mMap.getCenter());
        		MapMarkker.setTitle("Marker");
        		MapMarkker.setDraggable(true);
     		  
     		   mMap.addMapObject(MapMarkker);
     	 
     		   if (mMoveCameraToMarker) {
     			   mMap.setCenter(MapMarkker.getCoordinate(), MapAnimation.NONE);
     		   }
        	}
        }

        
    	@Override
		public void onMarkerDrag(MapMarker marker) {}

		@Override
		public void onMarkerDragEnd(MapMarker marker) {
			mMarkerPosition_lat = marker.getCoordinate().getLatitude();
			mMarkerPosition_lon = marker.getCoordinate().getLongitude();
		}

		@Override
		public void onMarkerDragStart(MapMarker marker) {}
		

		@Override
		public boolean onMapObjectsSelected(List<ViewObject> objects) {
			for (ViewObject viewObject : objects) {
				if (viewObject.getBaseType() == ViewObjectType.USER_OBJECT) {
					MapObject mapObject = (MapObject) viewObject;
	                if (mapObject.getType() == MapObjectType.MARKER) {
	                	MapMarker marker = (MapMarker) mapObject;

	                	float newHue = MARKER_HUES[new Random().nextInt(MARKER_HUES.length)];
	                	mMarkerInfo.mHue = newHue;
	                	
	                	
	                	
	                	MapMarker NewMarkker = MapFactory.createMapMarker(newHue);
	                	NewMarkker.setCoordinate(MapMarkker.getCoordinate());
	                	NewMarkker.setTitle(MapMarkker.getTitle());
	                	NewMarkker.setDraggable(true);
	          		  
	                	mMap.removeMapObject(MapMarkker);
	                	MapMarkker = null;
	          		   	mMap.addMapObject(NewMarkker);
	          		   	MapMarkker = NewMarkker;
	          				  
	                	/*MapMarker NewMarkker = MapFactory.createMapMarker(newHue);
	                	NewMarkker.setCoordinate(marker.getCoordinate());
	                	NewMarkker.setTitle(marker.getTitle());
	                	NewMarkker.setDraggable(true);
	          		  
	                	mMap.removeMapObject(marker);
	          		   	mMap.addMapObject(NewMarkker);*/
	                }
				}
			}	
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(PointF p) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onLongPressEvent(PointF p) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onLongPressRelease() {
			// TODO Auto-generated method stub
			
		}

		
		@Override
		public void onMultiFingerManipulationEnd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onMultiFingerManipulationStart() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPanEnd() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPanStart() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onPinchLocked() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onRotateEvent(float rotateAngle) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void onRotateLocked() {
			// TODO Auto-generated method stub
			
		}

		@Override
		public boolean onTapEvent(PointF p) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onTiltEvent(float angle) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean onTwoFingerTapEvent(PointF p) {
			// TODO Auto-generated method stub
			return false;
		}

	
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.save_state_demo);
    }

}
