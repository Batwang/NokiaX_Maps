package com.example.hereapi_example;


import java.util.List;
import java.util.Random;

import com.here.android.common.GeoCoordinate;
import com.here.android.common.Image;
import com.here.android.common.ViewObject;
import com.here.android.common.ViewObject.ViewObjectType;
import com.here.android.mapping.Map.InfoBubbleAdapter;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapContainer;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapMarkerDragListener;
import com.here.android.mapping.MapObject;
import com.here.android.mapping.MapObjectType;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

public class MarkerDemoActivity extends Activity 
implements OnSeekBarChangeListener, MapGestureListener,MapMarkerDragListener{

	private static final double BRISBANE_lat = -27.47093;
	private static final double BRISBANE_lon = 153.0235;
    private static final double MELBOURNE_lat = -37.81319;
    private static final double MELBOURNE_lon = 144.96298;
    private static final double SYDNEY_lat = -33.87365;
    private static final double SYDNEY_lon = 151.20689;
    private static final double ADELAIDE_lat = -34.92873;
    private static final double ADELAIDE_lon = 138.59995;
    private static final double PERTH_lat = -31.952854;
    private static final double PERTH_lon = 115.857342;
    
    /** Demonstrates customizing the info window and/or its contents. */
    class CustomInfoWindowAdapter implements InfoBubbleAdapter {
        private final RadioGroup mOptions;

        // These a both viewgroups containing an ImageView with id "badge" and two TextViews with id
        // "title" and "snippet".
        private final View mWindow;
        private final View mContents;

        CustomInfoWindowAdapter() {
            mWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
            mContents = getLayoutInflater().inflate(R.layout.custom_info_contents, null);
            mOptions = (RadioGroup) findViewById(R.id.custom_info_window_options);
        }

       

        private void render(MapMarker marker, View view) {
            int badge;
            // Use the equals() method on a Marker to check for equals.  Do not use ==.
            if (marker.equals(mBrisbane)) {
                badge = R.drawable.badge_qld;
            } else if (marker.equals(mAdelaide)) {
                badge = R.drawable.badge_sa;
            } else if (marker.equals(mSydney)) {
                badge = R.drawable.badge_nsw;
            } else if (marker.equals(mMelbourne)) {
                badge = R.drawable.badge_victoria;
            } else if (marker.equals(mPerth)) {
                badge = R.drawable.badge_wa;
            } else {
                // Passing 0 to setImageResource will clear the image view.
                badge = 0;
            }
            ((ImageView) view.findViewById(R.id.badge)).setImageResource(badge);

            String title = marker.getTitle();
            TextView titleUi = ((TextView) view.findViewById(R.id.title));
            if (title != null) {
                // Spannable string allows us to edit the formatting of the text.
                SpannableString titleText = new SpannableString(title);
                titleText.setSpan(new ForegroundColorSpan(Color.RED), 0, titleText.length(), 0);
                titleUi.setText(titleText);
            } else {
                titleUi.setText("");
            }

            String snippet = marker.getDescription();
            TextView snippetUi = ((TextView) view.findViewById(R.id.snippet));
            if (snippet != null && snippet.length() > 12) {
                SpannableString snippetText = new SpannableString(snippet);
                snippetText.setSpan(new ForegroundColorSpan(Color.MAGENTA), 0, 10, 0);
                snippetText.setSpan(new ForegroundColorSpan(Color.BLUE), 12, snippet.length(), 0);
                snippetUi.setText(snippetText);
            } else {
                snippetUi.setText("");
            }
        }

		@Override
		public View getInfoBubble(MapMarker marker) {
			if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_window) {
                // This means that getInfoContents will be called.
                return null;
            }
            render(marker, mWindow);
            return mWindow;
		}

		@Override
		public View getInfoBubbleContents(MapMarker marker) {
			if (mOptions.getCheckedRadioButtonId() != R.id.custom_info_contents) {
                // This means that the default info contents will be used.
                return null;
            }
            render(marker, mContents);
            return mContents;
		}
    

    }
    
    Map HereMap = null;
    MapContainer MapMarkers = null;
    MapContainer RBMarkers = null;
    
    private MapMarker mPerth;
    private MapMarker mSydney;
    private MapMarker mBrisbane;
    private MapMarker mAdelaide;
    private MapMarker mMelbourne;

    private TextView mTopText;
    private SeekBar mRotationBar;
 //   private CheckBox mFlatBox;

    private final Random mRandom = new Random();
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.marker_demo);

        mTopText = (TextView) findViewById(R.id.top_text);

        mRotationBar = (SeekBar) findViewById(R.id.rotationSeekBar);
        mRotationBar.setMax(360);
        mRotationBar.setOnSeekBarChangeListener(this);

 //       mFlatBox = (CheckBox) findViewById(R.id.flat);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
    
    
    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (HereMap == null) {
        	final MarkerDemoActivity that = this;
        	
        	final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
			// initialize the Map Fragment to create a map and
			// attached to the fragment
			mapFragment.init(new FragmentInitListener() {
				@Override
				public void onFragmentInitializationCompleted(InitError error) {
					if (error == InitError.NONE) {
						HereMap = (Map) mapFragment.getMap();
						
						mapFragment.getMapGesture().addMapGestureListener(that);
 						mapFragment.setMapMarkerDragListener(that);
 						
 						//setOnInfoWindowClickListener(this);
 				        
						setUpMap();
					}else {
						
					}
				}
			});
        }
    }

    private void setUpMap() {
    	
    	if (HereMap == null) {
    		return;
    	}
        // Hide the zoom controls as the button panel will cover it.
    	//HereMap.getUiSettings().setZoomControlsEnabled(false);

        // Add lots of markers to the map.
        addMarkersToMap();

        // Setting an info window adapter allows us to change the both the contents and look of the
        // info window.
     //   mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter());

        // Set listeners for marker events.  See the bottom of this class for their behavior.
        // are set earlier already

        // Pan to see all markers in view.
        if(MapMarkers != null){ 	       
        	List<MapObject> addedMarkers = MapMarkers.getAllMapObjects();
            
        	boolean gotRect = false;
            double north = 0;
            double west = 0;
            double south = 0;
            double east = 0;
            
            
            for (int i = 0; i < addedMarkers.size(); i++)
            {
            	
            	if(addedMarkers.get(i).getType() == MapObjectType.MARKER){
            	
	            	MapMarker MarkerElement = (MapMarker)addedMarkers.get(i);
	            	if(MarkerElement != null){
		                if (!gotRect)
		                {
		                    gotRect = true;
		                    north = south = MarkerElement.getCoordinate().getLatitude();
		                    west = east = MarkerElement.getCoordinate().getLongitude();
		                }
		                else
		                {
		                    if (north < MarkerElement.getCoordinate().getLatitude()) north = MarkerElement.getCoordinate().getLatitude();
		                    if (west > MarkerElement.getCoordinate().getLongitude()) west = MarkerElement.getCoordinate().getLongitude();
		                    if (south > MarkerElement.getCoordinate().getLatitude()) south = MarkerElement.getCoordinate().getLatitude();
		                    if (east < MarkerElement.getCoordinate().getLongitude()) east = MarkerElement.getCoordinate().getLongitude();
		                }
	            	}
            	}
            }
            
            if(gotRect){
        
            	GeoCoordinate topLeft = MapFactory.createGeoCoordinate(north, west);
            	GeoCoordinate bottomRight= MapFactory.createGeoCoordinate(south, east);
            	
            	HereMap.zoomTo(MapFactory.createGeoBoundingBox(topLeft, bottomRight), MapAnimation.NONE, 0);
            }
        }
    }
    
    private void addMarkersToMap() {
        if(MapMarkers != null){ 
        	return;
        }
        
        
        MapMarkers = MapFactory.createMapContainer();
    	
        mBrisbane = MapFactory.createMapMarker(210.0f);
        mBrisbane.setCoordinate(MapFactory.createGeoCoordinate(BRISBANE_lat, BRISBANE_lon));
        mBrisbane.setTitle("Brisbane");
        mBrisbane.setDescription("Population: 2,074,200");
        mBrisbane.setDraggable(true);
        MapMarkers.addMapObject(mBrisbane);
    
        // Uses a custom icon with the info window popping out of the center of the icon.
        mSydney = MapFactory.createMapMarker();
        mSydney.setCoordinate(MapFactory.createGeoCoordinate(SYDNEY_lat, SYDNEY_lon));
        
        Image imgSydney = MapFactory.createImage();
        Bitmap bitSydney = BitmapFactory.decodeResource(getBaseContext().getResources(), R.drawable.arrow);
        imgSydney.setBitmap(bitSydney);
        mSydney.setIcon(imgSydney);
        
        // .infoWindowAnchor(0.5f, 0.5f));
        mSydney.setTitle("Sydney");
        mSydney.setDescription("Population: 4,627,300");
        mSydney.setDraggable(true);
        MapMarkers.addMapObject(mSydney);
        
        // Creates a draggable marker. Long press to drag.
        mMelbourne = MapFactory.createMapMarker();
        mMelbourne.setCoordinate(MapFactory.createGeoCoordinate(MELBOURNE_lat, MELBOURNE_lon));
        mMelbourne.setTitle("Melbourne");
        mMelbourne.setDescription("Population: 4,137,400");
        mMelbourne.setDraggable(true);
        MapMarkers.addMapObject(mMelbourne);
        
        
        // A few more markers for good measure.
        mPerth = MapFactory.createMapMarker();
        mPerth.setCoordinate(MapFactory.createGeoCoordinate(PERTH_lat, PERTH_lon));
        mPerth.setTitle("Perth");
        mPerth.setDescription("Population: 1,738,800");
        mPerth.setDraggable(true);
        MapMarkers.addMapObject(mPerth);
        
        mAdelaide = MapFactory.createMapMarker();
        mAdelaide.setCoordinate(MapFactory.createGeoCoordinate(ADELAIDE_lat, ADELAIDE_lon));
        mAdelaide.setTitle("Adelaide");
        mAdelaide.setDescription("Population: 1,213,000");
        mAdelaide.setDraggable(true);
        MapMarkers.addMapObject(mAdelaide);
        
        HereMap.addMapObject(MapMarkers);
        
        RBMarkers = MapFactory.createMapContainer();

 //       float rotation = mRotationBar.getProgress();
 //       boolean flat = mFlatBox.isChecked();

        int numMarkersInRainbow = 12;
        for (int i = 0; i < numMarkersInRainbow; i++) {
            
        	double  RBLat = -30 + 10 * Math.sin(i * Math.PI / (numMarkersInRainbow - 1));
        	double  RBLon = 135 - 10 * Math.cos(i * Math.PI / (numMarkersInRainbow - 1));
        	
        	MapMarker ReinbowMarker = MapFactory.createMapMarker((i * 360 / numMarkersInRainbow));
        	ReinbowMarker.setCoordinate(MapFactory.createGeoCoordinate(RBLat, RBLon));
        	ReinbowMarker.setTitle("Marker " + i);
        	
        	RBMarkers.addMapObject(ReinbowMarker);

        	//.flat(flat)
            //.rotation(rotation)));
        }
        HereMap.addMapObject(RBMarkers);
    }

    private boolean checkReady() {
        if (HereMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    /** Called when the Clear button is clicked. */
    public void onClearMap(View view) {
        if (!checkReady()) {
            return;
        }
        
        if(RBMarkers != null){
        	HereMap.removeMapObject(RBMarkers);
        	RBMarkers = null;
        }
   
        if(MapMarkers != null){
        	HereMap.removeMapObject(MapMarkers);
        	MapMarkers = null;
        }
    }
    
    /** Called when the Reset button is clicked. */
    public void onResetMap(View view) {
        if (!checkReady()) {
            return;
        }
        
        // Clear the map because we don't want duplicates of the markers.
        if(RBMarkers != null){
        	HereMap.removeMapObject(RBMarkers);
        	RBMarkers = null;
        }
        
        if(MapMarkers != null){
        	HereMap.removeMapObject(MapMarkers);
        	MapMarkers = null;
        }
        setUpMap();
    }
    
    /** Called when the Reset button is clicked. */
    public void onToggleFlat(View view) {
        if (!checkReady()) {
            return;
        }
    /*    boolean flat = mFlatBox.isChecked();
        for (Marker marker : mMarkerRainbow) {
            marker.setFlat(flat);
        }*/
    }
    
    public MapMarker FindObject(MapMarker marker){
    
    	MapMarker ret = null;
    	
    	List<MapObject> addedMarkers = MapMarkers.getAllMapObjects();
    	for (int i = 0; i < addedMarkers.size(); i++)
        {
    		if (addedMarkers.get(i).hashCode() == marker.hashCode()) {
    			ret = (MapMarker) addedMarkers.get(i);
        	}
        }
    	if(ret == null){
    		List<MapObject> ReinBMarkers =  RBMarkers.getAllMapObjects();
    		for (int i = 0; i < ReinBMarkers.size(); i++)
            {
    			if (ReinBMarkers.get(i).hashCode() == marker.hashCode()) {
        			ret = (MapMarker) ReinBMarkers.get(i);
            	}
            }
    	}
	    return ret;
    }
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (!checkReady()) {
            return;
        }
      /*  float rotation = seekBar.getProgress();
        for (Marker marker : mMarkerRainbow) {
            marker.setRotation(rotation);
        }*/
	}

	@Override
	public boolean onTapEvent(PointF p) {
		if(HereMap != null){
			List<ViewObject> objs =	HereMap.getSelectedObjects(p);
					
			for (ViewObject viewObject : objs) {
				if (viewObject.getBaseType() == ViewObjectType.USER_OBJECT) {
					MapObject mapObject = (MapObject) viewObject;
	                    if (mapObject.getType() == MapObjectType.MARKER) {
	                        MapMarker marker = (MapMarker) mapObject;
										
							if (mPerth.hashCode() == marker.hashCode()) {

								// This causes the marker at Perth to bounce into position when it is clicked.
					            final android.os.Handler handler = new Handler();
					            final long start = SystemClock.uptimeMillis();
					            final long duration = 1500;
				
					            final BounceInterpolator interpolator = new BounceInterpolator();
				
					            handler.post(new Runnable() {
					                @Override
					                public void run() {
					                    long elapsed = SystemClock.uptimeMillis() - start;
					                    float t = Math.max(1 - interpolator
					                            .getInterpolation((float) elapsed / duration), 0);
					                    
					                    PointF NewAnc = new PointF(0.5f,(1.0f + 2 * t));
					                    mPerth.setAnchorPoint(NewAnc);
				
					                    if (t > 0.0) {
					                        // Post again 16ms later.
					                        handler.postDelayed(this, 16);
					                    }
					                }
					            });
					        } else if (mAdelaide.hashCode() == marker.hashCode()) {
					            // This causes the marker at Adelaide to change color and alpha.
					        	
					        	if(MapMarkers != null){
					        		MapMarkers.removeMapObject(mAdelaide);
					        		mAdelaide.hideInfoBubble();
					        		mAdelaide = null;
					        		
					        		mAdelaide = MapFactory.createMapMarker(mRandom.nextFloat() * 360);
					        		mAdelaide.setTransparency(mRandom.nextFloat());
					        		mAdelaide.setCoordinate(MapFactory.createGeoCoordinate(ADELAIDE_lat, ADELAIDE_lon));
					                mAdelaide.setTitle("Adelaide");
					                mAdelaide.setDescription("Population: 1,213,000");
					                mAdelaide.setDraggable(true);
					        		MapMarkers.addMapObject(mAdelaide);
					        	}
					        }
	                    }
				}
			}
		}
		return false;
	}
	
	
	

	
	@Override
	public boolean onMapObjectsSelected(List<ViewObject> objects) {
		// TODO Auto-generated method stub
		
		for (ViewObject viewObject : objects) {
			if (viewObject.getBaseType() == ViewObjectType.USER_OBJECT) {
				MapObject mapObject = (MapObject) viewObject;
                if (mapObject.getType() == MapObjectType.MARKER) {
                	MapMarker marker = FindObject((MapMarker) mapObject);
                	if(marker != null){
                		marker.showInfoBubble();
                	}
                	return false;
                }
			}
		}	
	
		return false;
	}
	
	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {// Do nothing.
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {// Do nothing.	
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
	public boolean onTiltEvent(float angle) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onTwoFingerTapEvent(PointF p) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMarkerDrag(MapMarker marker) {
		mTopText.setText("onMarkerDrag.  Current Position: " + marker.getCoordinate());
	}

	@Override
	public void onMarkerDragEnd(MapMarker marker) {
		 mTopText.setText("onMarkerDragEnd");
	}

	@Override
	public void onMarkerDragStart(MapMarker marker) {
		mTopText.setText("onMarkerDragStart");
	}
}
