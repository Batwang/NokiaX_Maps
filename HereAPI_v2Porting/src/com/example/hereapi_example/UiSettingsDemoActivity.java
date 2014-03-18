package com.example.hereapi_example;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGesture;
import com.here.android.mapping.PositionIndicator;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

public class UiSettingsDemoActivity extends Activity {

	 private Map mMap;
	 private MapGesture mUiSettings;

	 @Override
	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.ui_settings_demo);
	        setUpMapIfNeeded();
	    }

	    @Override
	    protected void onResume() {
	        super.onResume();
	        setUpMapIfNeeded();

	        if (mMap != null) {
	           // Keep the UI Settings state in sync with the checkboxes.
	           // mUiSettings.setZoomControlsEnabled(isChecked(R.id.zoom_buttons_toggle));
	           // mUiSettings.setCompassEnabled(isChecked(R.id.compass_toggle));
	           
	        	PositionIndicator posInd =mMap.getPositionIndicator();
	        	posInd.setVisible(isChecked(R.id.mylocationbutton_toggle));
	        	
	           // mMap.setMyLocationEnabled(isChecked(R.id.mylocationlayer_toggle));
	            mUiSettings.setPanningEnabled(isChecked(R.id.scroll_toggle));
	            mUiSettings.setPinchEnabled(isChecked(R.id.zoom_gestures_toggle));
	            mUiSettings.setTiltEnabled(isChecked(R.id.tilt_toggle));
	            mUiSettings.setRotateEnabled(isChecked(R.id.rotate_toggle));
	        
	       /*   setDoubleTapEnabled(boolean enabled)
	            setSingleTapEnabled(boolean enabled)
	            setLongPressEnabled(boolean enabled)
	            
	            setKineticFlickEnabled(boolean enabled)
	            setPanningEnabled(boolean enabled)
	            
	            setPinchEnabled(boolean enabled)
	            
	            setKineticRotationEnabled(boolean enabled)
	            setRotateEnabled(boolean enabled)
	            
	            setTiltEnabled(boolean enabled)
	            
	            setTwoFingerPanningEnabled(boolean enable)
	            setTwoFingerPinchRotateEnabled(boolean enable)
	            setTwoFingerTapEnabled(boolean enabled) */
	        
	        }
	    }

	    /**
	     * Returns whether the checkbox with the given id is checked.
	     */
	    private boolean isChecked(int id) {
	      return ((CheckBox) findViewById(id)).isChecked();
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
							mUiSettings = mapFragment.getMapGesture();
						}
					}
				});
	        }
	    }

	    /**
	     * Checks if the map is ready (which depends on whether the Google Play services APK is
	     * available. This should be called prior to calling any methods on GoogleMap.
	     */
	    private boolean checkReady() {
	        if (mMap == null) {
	            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
	            return false;
	        }
	        return true;
	    }

	    public void setZoomButtonsEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
	        // Enables/disables the zoom controls (+/- buttons in the bottom right of the map).
	       // mUiSettings.setZoomControlsEnabled(((CheckBox) v).isChecked());
	    }

	    public void setCompassEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
	        
	        // Enables/disables the compass (icon in the top left that indicates the orientation of the
	        // map).
	        //mUiSettings.setCompassEnabled(((CheckBox) v).isChecked());
	    }

	    public void setMyLocationButtonEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
	        
	        // Enables/disables the my location button (this DOES NOT enable/disable the my location
	        // dot/chevron on the map). The my location button will never appear if the my location
	        // layer is not enabled.
	        //mUiSettings.setMyLocationButtonEnabled(((CheckBox) v).isChecked());
	    }

	    public void setMyLocationLayerEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
	        // Enables/disables the my location layer (i.e., the dot/chevron on the map). If enabled, it
	        // will also cause the my location button to show (if it is enabled); if disabled, the my
	        // location button will never show.
	        //mMap.setMyLocationEnabled(((CheckBox) v).isChecked());
	    }

	    public void setScrollGesturesEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        // Enables/disables scroll gestures (i.e. panning the map).
	        mUiSettings.setPanningEnabled(((CheckBox) v).isChecked());
	    }

	    public void setZoomGesturesEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        // Enables/disables zoom gestures (i.e., double tap, pinch & stretch).
	        mUiSettings.setPinchEnabled(((CheckBox) v).isChecked());
	    }

	    public void setTiltGesturesEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        // Enables/disables tilt gestures.
	        mUiSettings.setTiltEnabled(((CheckBox) v).isChecked());
	    }

	    public void setRotateGesturesEnabled(View v) {
	        if (!checkReady()) {
	            return;
	        }
	        // Enables/disables rotate gestures.
	        mUiSettings.setRotateEnabled(((CheckBox) v).isChecked());
	    }
}
