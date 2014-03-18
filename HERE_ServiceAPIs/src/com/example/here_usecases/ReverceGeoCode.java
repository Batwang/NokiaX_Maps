package com.example.here_usecases;

import java.util.List;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;
import com.here.android.common.ViewObject;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;
import com.here.android.search.Address;
import com.here.android.search.ErrorCode;
import com.here.android.search.ResultListener;
import com.here.android.search.geocoder.ReverseGeocodeRequest;

public class ReverceGeoCode   extends Activity 
implements ResultListener<Address>, MapGestureListener{

	 // map embedded in the map fragment
  private Map map = null;
  private com.here.android.search.Geocoder geocoder = null;
  private ReverseGeocodeRequest request = null;
  
  private MapMarker mapMarker = null;
  
  // map fragment embedded in this activity
  private MapFragment mapFragment = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.revercegeo);
      
      final ReverceGeoCode that = this;
      // Search for the map fragment to finish setup by calling init().
      mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      mapFragment.init(new FragmentInitListener() {
          @Override
          public void onFragmentInitializationCompleted(InitError error) {
              if (error == InitError.NONE) {
                  // retrieve a reference of the map from the map fragment
                  map = mapFragment.getMap();
                  geocoder = MapFactory.getGeocoder();
                  mapFragment.getMapGesture().addMapGestureListener(that);
              } else {
           	   	  Toast.makeText(getApplicationContext(),"Map init error: " + error, Toast.LENGTH_LONG).show();
              }
          }
      });
  }

	@Override
	public void onPanEnd() {
		if (geocoder != null && map != null ) {
	  		
	  		if(request != null){
	  			request.cancel();
	  			request = null;
	  		}
	  		
	  		if(mapMarker != null){
	  			mapMarker.hideInfoBubble();
	  			map.removeMapObject(mapMarker);
	  			mapMarker = null;
	  		}

	  		mapMarker = MapFactory.createMapMarker();
	  		mapMarker.setCoordinate(map.getCenter());
	  		mapMarker.setDraggable(false);
	  		map.addMapObject(mapMarker);
	        
	  		request = geocoder.createReverseGeocodeRequest(map.getCenter());	  				
	  		request.execute(this);
	  	}
	}
	
	@Override
	public void onCompleted(Address data, ErrorCode error) {
		if(map == null){
			return;
		}
		   
		if(error == ErrorCode.NONE && data != null){		
			mapMarker.setTitle(data.getText());
			mapMarker.showInfoBubble();
		}else{
			Toast.makeText(getApplicationContext(),"Geocode finished with error: " + error, Toast.LENGTH_LONG).show();
		}	
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

	@Override
	public boolean onMapObjectsSelected(List<ViewObject> objects) {
		// TODO Auto-generated method stub
		return false;
	}

	
}