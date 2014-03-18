package com.example.here_usecases;

import java.util.List;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.common.GeoCoordinate;
import com.here.android.common.ViewObject;
import com.here.android.common.ViewObject.ViewObjectType;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapContainer;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapObject;
import com.here.android.mapping.MapObjectType;
import com.here.android.search.ErrorCode;
import com.here.android.search.Location;
import com.here.android.search.ResultListener;
import com.here.android.search.geocoder.GeocodeRequest;

public class GeoCodeAddress extends Activity 
implements ResultListener<List<Location>> , MapGestureListener{

	 // map embedded in the map fragment
  private Map map = null;
  private com.here.android.search.Geocoder geocoder = null;
  private GeocodeRequest request = null;
  
  private MapContainer MapMarkers = null;
  
  // map fragment embedded in this activity
  private MapFragment mapFragment = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.geocodeaddress);
      
      final GeoCodeAddress that = this;
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

  /**
   * Called when the geobut button is clicked.
   */
  public void onGeoCode(View view) {
  	if (geocoder != null && map != null ) {
  		
  		if(request != null){
  			request.cancel();
  			request = null;
  		}
  		
  		if(MapMarkers != null){
  			map.removeMapObject(MapMarkers);
  			MapMarkers = null;
  		}
  		
  		com.here.android.search.Address src_address = MapFactory.createAddress();
  		src_address.setHouseNumber(getString(((TextView)findViewById(R.id.geoHouseNo))));
  		src_address.setStreet(getString(((TextView)findViewById(R.id.geoStreet))));
  		src_address.setPostalCode(getString(((TextView)findViewById(R.id.geoPostal))));
  		src_address.setCity(getString(((TextView)findViewById(R.id.geoCity))));
  		src_address.setCountryCode(getString(((TextView)findViewById(R.id.geoCountry))));
	
  		request = geocoder.createAddressRequest(src_address);
  		request.execute(this);
  	}
  }
  
  public String getString(TextView view) {
	
	  String ret = new String("");
	  
	  if(view != null){
		  String val = view.getText().toString();
		  if(val != null){
			  ret = val;
		  }
	  }
	  
	  return ret;
  }
  
  @Override
  public void onCompleted(List<Location> data, ErrorCode error) {
	   if(map == null){
		   return;
	   }
	   
	   if(error == ErrorCode.NONE && data != null){		
			
			MapMarkers = MapFactory.createMapContainer();
			map.addMapObject(MapMarkers);
			
			for(int i=0; i < data.size(); i++){
				Location dataItem = data.get(i);
				MapMarker AddMarker = MapFactory.createMapMarker((i * 360 / data.size()));
	        	
				AddMarker.setCoordinate(dataItem.getCoordinate());
				AddMarker.setTitle(dataItem.getAddress().getText());
		        AddMarker.setDraggable(false);
		        MapMarkers.addMapObject(AddMarker);
			}
			
			ZoomToMarkers();
			
		}else{
			Toast.makeText(getApplicationContext(),"Geocode finished with error: " + error, Toast.LENGTH_LONG).show();
		}	
	}
  
	private void ZoomToMarkers(){
	   
	   if(MapMarkers != null && map != null ){ 	       
   	List<MapObject> addedMarkers = MapMarkers.getAllMapObjects();
       
   	boolean gotRect = false;
       double north = 0;
       double west = 0;
       double south = 0;
       double east = 0;
       
       if(addedMarkers.size() == 1){

    	   if(addedMarkers.get(0).getType() == MapObjectType.MARKER){
	            	MapMarker MarkerElement = (MapMarker)addedMarkers.get(0);
	            	if(MarkerElement != null){
	            		map.setCenter(MarkerElement.getCoordinate(), MapAnimation.BOW);
	            	}
    	   }
       }else{
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
	           		map.zoomTo(MapFactory.createGeoBoundingBox(topLeft, bottomRight), MapAnimation.NONE, 0);
	           }
       }
   }
}

	@Override
	public boolean onMapObjectsSelected(List<ViewObject> objects) {
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
	
	private MapMarker FindObject(MapMarker marker) {
		MapMarker ret = null;
   	
   	List<MapObject> addedMarkers = MapMarkers.getAllMapObjects();
   	for (int i = 0; i < addedMarkers.size(); i++)
       {
   		if (addedMarkers.get(i).hashCode() == marker.hashCode()) {
   			ret = (MapMarker) addedMarkers.get(i);
       	}
       }

	    return ret;
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
