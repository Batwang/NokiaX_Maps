package com.example.here_usecases;

import java.util.List;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;
import com.here.android.common.ViewObject;
import com.here.android.common.ViewObject.ViewObjectType;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.restricted.mapping.Map;
import com.here.android.mapping.MapObjectType;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapObject;
import com.here.android.mapping.MapProxyObject;
import com.here.android.mapping.MapProxyObjectType;
import com.here.android.mapping.MapTransitLayer;
import com.here.android.mapping.MapTransitLayerMode;
import com.here.android.mapping.TransitAccessInfo;
import com.here.android.mapping.TransitAccessObject;
import com.here.android.mapping.TransitLineObject;
import com.here.android.mapping.TransitStopInfo;
import com.here.android.mapping.TransitStopObject;

public class TransitMap extends Activity 
implements  MapGestureListener, OnItemSelectedListener{

	 // map embedded in the map fragment
  private Map map = null;
  private MapTransitLayer layer = null;
  // map fragment embedded in this activity
  private MapFragment mapFragment = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.transit_map);
      
      Spinner spinner = (Spinner) findViewById(R.id.what_spinner);
      ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.transit_array, android.R.layout.simple_spinner_item);
      adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
      spinner.setAdapter(adapter);
      spinner.setOnItemSelectedListener(this);
            
      final TransitMap that = this;
      // Search for the map fragment to finish setup by calling init().
      mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      mapFragment.init(new FragmentInitListener() {
          @Override
          public void onFragmentInitializationCompleted(InitError error) {
              if (error == InitError.NONE) {
                  // retrieve a reference of the map from the map fragment
                  map = (Map) mapFragment.getMap();
                  mapFragment.getMapGesture().addMapGestureListener(that);
                  
                  layer = map.getMapTransitLayer();
                  
                  Spinner spinner = (Spinner) findViewById(R.id.what_spinner);
            	  	String selStr = spinner.getSelectedItem().toString();
            	  	if (selStr.equals(getString(R.string.EVERYTHING))) {
            	  		layer.setMode(MapTransitLayerMode.EVERYTHING);
            	  	}else if (selStr.equals(getString(R.string.STOPS_AND_ACCESSES))) {
            	  		layer.setMode(MapTransitLayerMode.STOPS_AND_ACCESSES);
            	  	}else{ //nothing
            	  		layer.setMode(MapTransitLayerMode.NOTHING);
            	  	}
                  
              } else {
           	   	Toast.makeText(getApplicationContext(),"Map init error: " + error, Toast.LENGTH_LONG).show();
              }
          }
      });
  }
  
	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		if(layer != null){
	    	Spinner spinner = (Spinner) findViewById(R.id.what_spinner);
	  	  	String selStr = spinner.getSelectedItem().toString();
	  	  	if (selStr.equals(getString(R.string.EVERYTHING))) {
	  	  		layer.setMode(MapTransitLayerMode.EVERYTHING);
	  	  	}else if (selStr.equals(getString(R.string.STOPS_AND_ACCESSES))) {
	  	  		layer.setMode(MapTransitLayerMode.STOPS_AND_ACCESSES);
	  	  	}else{ //nothing
	  	  		layer.setMode(MapTransitLayerMode.NOTHING);
	  	  	}
    	}
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		if(layer != null){
	    	Spinner spinner = (Spinner) findViewById(R.id.what_spinner);
	  	  	String selStr = spinner.getSelectedItem().toString();
	  	  	if (selStr.equals(getString(R.string.EVERYTHING))) {
	  	  		layer.setMode(MapTransitLayerMode.EVERYTHING);
	  	  	}else if (selStr.equals(getString(R.string.STOPS_AND_ACCESSES))) {
	  	  		layer.setMode(MapTransitLayerMode.STOPS_AND_ACCESSES);
	  	  	}else{ //nothing
	  	  		layer.setMode(MapTransitLayerMode.NOTHING);
	  	  	}
    	}
	}

	@Override
	public boolean onMapObjectsSelected(List<ViewObject> objects) {
		for (ViewObject viewObject : objects) {
			if (viewObject.getBaseType() == ViewObjectType.USER_OBJECT) {
				MapObject mapObject = (MapObject) viewObject;
				if (mapObject.getType() == MapObjectType.MARKER) {}
			}else if (viewObject.getBaseType() == ViewObjectType.PROXY_OBJECT) {
				String stufff = "";
				MapProxyObject proxyObj = (MapProxyObject) viewObject;
				if (proxyObj.getType() == MapProxyObjectType.TRANSIT_STOP) {
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
				}else{
					stufff = "something else " + proxyObj.getType();
				}
				
				Toast.makeText(getApplicationContext(),stufff, Toast.LENGTH_LONG).show();
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
