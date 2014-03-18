package com.example.here_usecases;

import java.util.List;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Toast;
import com.here.android.common.ViewObject;
import com.here.android.common.ViewObject.ViewObjectType;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;

import com.here.android.restricted.mapping.Map;
import com.here.android.restricted.mapping.TrafficEvent;
import com.here.android.restricted.mapping.TrafficEventObject;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapProxyObject;
import com.here.android.mapping.MapProxyObjectType;
import com.here.android.mapping.TransitAccessInfo;
import com.here.android.mapping.TransitAccessObject;
import com.here.android.mapping.TransitLineObject;
import com.here.android.mapping.TransitStopInfo;
import com.here.android.mapping.TransitStopObject;



public class MapExtras  extends Activity 
implements MapGestureListener, OnSeekBarChangeListener{

	 // map embedded in the map fragment
  private Map map = null;
  
  // map fragment embedded in this activity
  private MapFragment mapFragment = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.mapextras);
      
      final MapExtras that = this;
       
      // Search for the map fragment to finish setup by calling init().
      mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      mapFragment.init(new FragmentInitListener() {
          @Override
          public void onFragmentInitializationCompleted(InitError error) {
              if (error == InitError.NONE) {
                  // retrieve a reference of the map from the map fragment
                  map = (Map) mapFragment.getMap();
                  mapFragment.getMapGesture().addMapGestureListener(that);
                  
                  SeekBar sekBar = ((SeekBar) findViewById(R.id.tilt_bar));
                  sekBar.setMax((int) map.getMaxTilt());
                  sekBar.setProgress((int) map.getTilt());
                  sekBar.setOnSeekBarChangeListener(that);
                  
                  ((CheckBox) findViewById(R.id.lm_toggle)).setChecked(map.isLandmarksVisible());
                  ((CheckBox) findViewById(R.id.tr_toggle)).setChecked(map.isTrafficInfoVisible());
                  ((CheckBox) findViewById(R.id.ex_toggle)).setChecked(map.isExtrudedBuildingsVisible());
                  ((CheckBox) findViewById(R.id.vn_toggle)).setChecked(map.isVenueMapTilesVisible());
                  ((CheckBox) findViewById(R.id.st_toggle)).setChecked(map.isStreetLevelCoverageVisible());
                  ((CheckBox) findViewById(R.id.eb_toggle)).setChecked(map.isMapEmbeddedPOIsVisible());
                  
              } else {
            	  	Toast.makeText(getApplicationContext(),"Map init error: " + error, Toast.LENGTH_LONG).show();
              }
          }
      });
  }
  
  
  public void onToggleEmbedded(View view) {
	  CheckBox cbox = (CheckBox) findViewById(R.id.eb_toggle);
	  map.setMapEmbeddedPOIsVisible(cbox.isChecked());
  }
  public void onToggleLandmarks(View view) {
	  CheckBox cbox = (CheckBox) findViewById(R.id.lm_toggle);
	  map.setLandmarksVisible(cbox.isChecked());
  }
  
  public void onToggleTraffic(View view) {
	  CheckBox cbox = (CheckBox) findViewById(R.id.tr_toggle);
	  map.setTrafficInfoVisible(cbox.isChecked());
  }
  public void onToggleExtruded(View view) {
	  CheckBox cbox = (CheckBox) findViewById(R.id.ex_toggle);
	  map.setExtrudedBuildingsVisible(cbox.isChecked());
  }
  public void onToggleVenues(View view) {
	  CheckBox cbox = (CheckBox) findViewById(R.id.vn_toggle);
	  map.setVenueMapTilesVisible(cbox.isChecked());
  }
  public void onToggleStreetLevel(View view) {
	  CheckBox cbox = (CheckBox) findViewById(R.id.st_toggle);
	  map.setStreetLevelCoverageVisible(cbox.isChecked());
  }
  
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {
		if(map != null){
			map.setTilt(progress);
		}
	}
	@Override
	public boolean onTiltEvent(float angle) {
		SeekBar sekBar = ((SeekBar) findViewById(R.id.tilt_bar));
		sekBar.setProgress((int) angle);
		return false;
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {}


	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {}
	
	@Override
	public boolean onMapObjectsSelected(List<ViewObject> objects) {
		for (ViewObject viewObject : objects) {
			if (viewObject.getBaseType() == ViewObjectType.USER_OBJECT) {
				//MapObject mapObject = (MapObject) viewObject;
               
				
			}else if (viewObject.getBaseType() == ViewObjectType.PROXY_OBJECT) {
				MapProxyObject proxyObj = (MapProxyObject) viewObject;
				
				String stufff = "";
				
				if (proxyObj.getType() == MapProxyObjectType.TRAFFIC_EVENT) 
				{
					TrafficEventObject trafficEventObj =(TrafficEventObject) proxyObj;
					TrafficEvent trafficEvent =trafficEventObj.getTrafficEvent();
					
					stufff = "TrafficEventObject " + trafficEvent.getEventText();
				}
				else if (proxyObj.getType() == MapProxyObjectType.TRANSIT_STOP) 
				{
					TransitStopObject transitStopObj = (TransitStopObject) proxyObj;
					TransitStopInfo transitStopInfo = transitStopObj.getTransitStopInfo();
					
					stufff = "TransitStopObject " + transitStopInfo.getOfficialName() + " at " + transitStopObj.getCoordinate().toString();
				}else if (proxyObj.getType() == MapProxyObjectType.TRANSIT_ACCESS) {
					TransitAccessObject transitAccessObj = (TransitAccessObject) proxyObj;
					TransitAccessInfo info = transitAccessObj.getTransitAccessInfo();
					
					stufff = "TransitAccessObject " + info.toString();
				}else if (proxyObj.getType() == MapProxyObjectType.TRANSIT_LINE) {
					TransitLineObject transitLineObj = (TransitLineObject) proxyObj;
					
					stufff = "TransitLineObject " + transitLineObj.getLineId();
				}
				if(stufff.length() > 0){
					Toast.makeText(getApplicationContext(),stufff,Toast.LENGTH_LONG).show();
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
	public boolean onTwoFingerTapEvent(PointF p) {
		// TODO Auto-generated method stub
		return false;
	}



}