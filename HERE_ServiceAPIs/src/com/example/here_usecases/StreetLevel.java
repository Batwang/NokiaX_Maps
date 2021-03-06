package com.example.here_usecases;


import java.util.List;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.Toast;

import com.here.android.common.ViewObject;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;

import com.here.android.restricted.mapping.Map;
import com.here.android.restricted.streetlevel.StreetLevelEventListener;
import com.here.android.restricted.streetlevel.StreetLevelFragment;
import com.here.android.restricted.streetlevel.StreetLevelSelectedObject;


public class StreetLevel extends Activity 
implements MapGestureListener, StreetLevelEventListener {

	 // map embedded in the map fragment
  private Map map = null;
  private StreetLevelFragment streetLevelFragment = null;
  
  // map fragment embedded in this activity
  private MapFragment mapFragment = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.streetlevel);

      final StreetLevel that = this;
      // Search for the map fragment to finish setup by calling init().
      mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      mapFragment.init(new FragmentInitListener() {
          @Override
          public void onFragmentInitializationCompleted(InitError error) {
              if (error == InitError.NONE) {
                  // retrieve a reference of the map from the map fragment
                  map = (Map) mapFragment.getMap();
                  map.setCenter(MapFactory.createGeoCoordinate(48.8567, 2.3508), MapAnimation.NONE);
                
                  mapFragment.getMapGesture().addMapGestureListener(that);
                  mapFragment.getMapGesture().setTiltEnabled(false);
                  
                  map.setStreetLevelCoverageVisible(true);
                  
                  streetLevelFragment = (StreetLevelFragment) getFragmentManager().findFragmentById(R.id.streetlevelfragment);
                  streetLevelFragment.init(new FragmentInitListener() {
                		@Override
                		public void onFragmentInitializationCompleted(InitError error) {
                			if (error == InitError.NONE) {
                				streetLevelFragment.addStreetLevelEventListener(that); 
                				streetLevelFragment.getStreetLevelModel().setOverlayTransparency(0.5f);                				                				
                				MoveStreetLevelImage();

                			}else{
                				Toast.makeText(getApplicationContext(),"Streetlevel init error: " + error, Toast.LENGTH_LONG).show();
                			}
                		}
                	});
              } else {
            	  Toast.makeText(getApplicationContext(),"Map init error: " + error, Toast.LENGTH_LONG).show();
              }
          }
      });
  }

  public void MoveStreetLevelImage() {

	  if(streetLevelFragment != null && map != null){
		  com.here.android.restricted.streetlevel.StreetLevel level =streetLevelFragment.getStreetLevelModel().getStreetLevel(map.getCenter(), 100);
		  if (level != null) {
			// Render street level imagery
			streetLevelFragment.getStreetLevelModel().moveTo( level, false, map.getOrientation(), 0, 1.0f);
		  }
	  }
  }
  
  @Override
  public void onPanEnd() {
	  MoveStreetLevelImage();
  }
  
  @Override
  public boolean onRotateEvent(float rotateAngle) {
	  MoveStreetLevelImage();
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
public boolean onMapObjectsSelected(List<ViewObject> objects) {
	// TODO Auto-generated method stub
	return false;
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

public void moveTheMapAlso() {
	// TODO Auto-generated method stub
	 if(streetLevelFragment != null && map != null){
		 map.setCenter(streetLevelFragment.getStreetLevelModel().getPosition(), MapAnimation.LINEAR, map.getZoomLevel(), streetLevelFragment.getStreetLevelModel().getHeading(), 0);
	 }
}

@Override
public void onAnimationEnd() {
	moveTheMapAlso();
}

@Override
public void onAnimationStart() {
	moveTheMapAlso();
}

@Override
public boolean onCompassSelected() {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean onDoubleTap(PointF p) {
	moveTheMapAlso();
	return false;
}

@Override
public boolean onObjectsSelected(List<StreetLevelSelectedObject> selectedObjects) {
	// TODO Auto-generated method stub
	return false;
}

@Override
public boolean onPinchZoom(float scaleFactor) {
	moveTheMapAlso();
	return false;
}

@Override
public boolean onRotate(PointF from, PointF to) {
	moveTheMapAlso();
	return false;
}

@Override
public boolean onTap(PointF p) {
	moveTheMapAlso();
	return false;
}

  
}