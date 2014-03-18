package com.example.here_usecases;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.here.android.common.GeoBoundingBox;
import com.here.android.common.GeoCoordinate;
import com.here.android.common.ViewObject;
import com.here.android.common.ViewObject.ViewObjectType;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapCircle;
import com.here.android.mapping.MapContainer;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapMarkerDragListener;
import com.here.android.mapping.MapObject;
import com.here.android.mapping.MapObjectType;
import com.here.android.search.CategoryFilter;
import com.here.android.search.ErrorCode;
import com.here.android.search.Places;
import com.here.android.search.ResultListener;
import com.here.android.search.places.DiscoveryRequest;
import com.here.android.search.places.DiscoveryResult;
import com.here.android.search.places.DiscoveryResultPage;
import com.here.android.search.places.GlobalCategory;
import com.here.android.search.places.Place;
import com.here.android.search.places.PlaceLink;
import com.here.android.search.places.PlaceRequest;
import com.here.android.search.places.DiscoveryResult.ResultType;

public class ExploreRadius extends Activity 
implements  MapGestureListener, MapMarkerDragListener, ResultListener<DiscoveryResultPage>{

	
	private class DraggableCircle {
        private MapMarker centerMarker = null;
        private MapMarker radiusMarker = null;
        private MapCircle circle = null;
        private double radius;
        
        public DraggableCircle(GeoCoordinate center, double radius) {
            this.radius = radius;
            
	            circle = MapFactory.createMapCircle(radius, center);
	            circle.setLineWidth(2);
	            circle.setLineColor(Color.BLACK);
	            circle.setFillColor(Color.HSVToColor(55, new float[] {100, 1, 1}));
	            map.addMapObject(circle);

	            centerMarker = MapFactory.createMapMarker(0.0f);
	            centerMarker.setCoordinate(center);
	            centerMarker.setTitle("centerMarker");
	            centerMarker.setDraggable(true);
	            map.addMapObject(centerMarker);
	         	
	            radiusMarker = MapFactory.createMapMarker(0.0f);
	            radiusMarker.setCoordinate(toRadiusLatLng(center, radius));
	            radiusMarker.setTitle("radiusMarker");
	            radiusMarker.setDraggable(true);
	            map.addMapObject(radiusMarker);
	
	
        }
        public GeoCoordinate getCenter(){
        	GeoCoordinate ret = null;
        	if(centerMarker != null){
        		ret = centerMarker.getCoordinate();
        	}
        	return ret;
        }
        
        public double getRadius(){
        	return this.radius;
        }
        
        public boolean onMarkerMoved(MapMarker marker) {
        	if(centerMarker != null){ 
	            if (centerMarker.equals(marker)) {
	                circle.setCenter(marker.getCoordinate());
	                radiusMarker.setCoordinate(toRadiusLatLng(marker.getCoordinate(), radius));
	                return true;
	            }else if (radiusMarker.equals(marker)) {
	            	 this.radius  = toRadiusMeters(centerMarker.getCoordinate(), radiusMarker.getCoordinate());
	                 circle.setRadius(this.radius);
	                 return true;
	            }
        	}
        	return false;
        }
        
        /** Generate LatLng of radius marker */
        private GeoCoordinate toRadiusLatLng(GeoCoordinate center, double radius) {
            double radiusAngle = Math.toDegrees(radius / 6371009) / Math.cos(Math.toRadians(center.getLatitude()));
            return MapFactory.createGeoCoordinate(center.getLatitude(), center.getLongitude() + radiusAngle);
        }
        
        private double toRadiusMeters(GeoCoordinate center, GeoCoordinate radius) {
            return center.distanceTo(radius);
        }
    }
	
	 // map embedded in the map fragment
 private Map map = null;
 private Places map_places = null;
 private DiscoveryRequest request = null;
 
 private DraggableCircle m_circle = null;
 
 private MapContainer MapMarkers = null;
 private List<PlaceLink> places = null;
 // map fragment embedded in this activity
 private MapFragment mapFragment = null;

 @Override
 public void onCreate(Bundle savedInstanceState) {
     super.onCreate(savedInstanceState);
     setContentView(R.layout.exploreradius);
     
     Spinner spinner = (Spinner) findViewById(R.id.cat_spinner);
     ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
             this, R.array.cat_array, android.R.layout.simple_spinner_item);
     adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
     spinner.setAdapter(adapter);
     
     final ExploreRadius that = this;
     // Search for the map fragment to finish setup by calling init().
     mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
     mapFragment.init(new FragmentInitListener() {
         @Override
         public void onFragmentInitializationCompleted(InitError error) {
             if (error == InitError.NONE) {
                 // retrieve a reference of the map from the map fragment
                 map = mapFragment.getMap();
                
                 mapFragment.getMapGesture().setRotateEnabled(false);
                 mapFragment.getMapGesture().setTiltEnabled(false);
                 
                 map_places = MapFactory.getPlaces();
                 mapFragment.getMapGesture().addMapGestureListener(that);
                 mapFragment.setMapMarkerDragListener(that); 
                 
                 m_circle = new DraggableCircle(map.getCenter(), 1000);
     	         map.setCenter(m_circle.getCenter(), MapAnimation.NONE,12.0f,0,0);
                 
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
 	if (map_places != null && map != null ) {
 		
 		if(request != null){
 			request.cancel();
 			request = null;
 		}
 		
 		if(MapMarkers != null){
 			map.removeMapObject(MapMarkers);
 			MapMarkers = null;
 			places = null;
 		}
 	
 		
 		request = map_places.createExploreRequest(m_circle.getCenter(), (int)m_circle.getRadius(), getCategoryFilter());
 		request.execute(this);
 	}
 }
 
 public CategoryFilter getCategoryFilter() {
	  
	  CategoryFilter filter = MapFactory.createCategoryFilter();
	  
	  Spinner spinner = (Spinner) findViewById(R.id.cat_spinner);
	  String selStr = spinner.getSelectedItem().toString();
	  if (selStr.equals(getString(R.string.cat_acc))) {
		  filter.add(GlobalCategory.ACCOMMODATION);
	  }else if (selStr.equals(getString(R.string.cat_adm))) {
		  filter.add(GlobalCategory.ADMINISTRATIVE_AREAS_BUILDINGS);
	  }else if (selStr.equals(getString(R.string.cat_bus))) {
		  filter.add(GlobalCategory.BUSINESS_SERVICES);
	  }else if (selStr.equals(getString(R.string.cat_edr))) {
		  filter.add(GlobalCategory.EAT_DRINK);
	  }else if (selStr.equals(getString(R.string.cat_fac))) {
		  filter.add(GlobalCategory.FACILITIES);
	  }else if (selStr.equals(getString(R.string.cat_goo))) {
		  filter.add(GlobalCategory.GOING_OUT);
	  }else if (selStr.equals(getString(R.string.cat_leo))) {
		  filter.add(GlobalCategory.LEISURE_OUTDOOR);
	  }else if (selStr.equals(getString(R.string.cat_nag))) {
		  filter.add(GlobalCategory.NATURAL_GEOGRAPHICAL);
	  }else if (selStr.equals(getString(R.string.cat_sho))) {
		  filter.add(GlobalCategory.SHOPPING);
	  }else if (selStr.equals(getString(R.string.cat_sim))) {
		  filter.add(GlobalCategory.SIGHTS_MUSEUMS);
	  }else if (selStr.equals(getString(R.string.cat_tra))) {
		  filter.add(GlobalCategory.TRANSPORT);
	  }else {
		  // return null, is same as all.
	  }

	  return filter;
 }
 
	@Override
	public void onCompleted(DiscoveryResultPage data, ErrorCode error) {

	   if(map == null){
		   return;
	   }
	   
	   if(error == ErrorCode.NONE && data != null){		
		   List<DiscoveryResult<?>> items = data.getItems();
          if (items != null) {
       	   MapMarkers = MapFactory.createMapContainer();
   		   map.addMapObject(MapMarkers);
   			
   		   places = new ArrayList<PlaceLink>(30);
   		   
   		   GeoBoundingBox stuffBox = MapFactory.createGeoBoundingBox(map.getCenter(), map.getCenter());
   		   int count = 0;
   		   int others = 0;
              for (DiscoveryResult<?> item : items) {
                  ResultType type = item.getResultType();

                  if (type == ResultType.DISCOVERY) {
               	   //DiscoveryLink discItem = (DiscoveryLink)item;
               	   others++;
                  } else if (type == ResultType.PLACE) {
               	   if(count < 30){ 
               		    PlaceLink placeItem = (PlaceLink)item;
               		    places.add(placeItem);
               		    
               		    	count++;
               	   			MapMarker AddMarker = MapFactory.createMapMarker((count * 360 / 30));
      	        	
               	   			AddMarker.setCoordinate(placeItem.getPosition());
		       				AddMarker.setTitle(placeItem.getId()); 
		       				AddMarker.setDescription(placeItem.getName());
		       				AddMarker.setDraggable(false);
		       		        MapMarkers.addMapObject(AddMarker);
	                	   
		       		        stuffBox = stuffBox.merge(MapFactory.createGeoBoundingBox(placeItem.getPosition(),placeItem.getPosition()));
		       		     
		       		        
	       		        }
                  }else{
               	   others++;
               	   // UNKNOWN 
                  }
              }
              Toast.makeText(getApplicationContext(),"Found " + count + " places and " + others + " others.", Toast.LENGTH_LONG).show();
              map.zoomTo(stuffBox, MapAnimation.BOW, 0);
          }else{
       	   Toast.makeText(getApplicationContext(),"Search did not find any items", Toast.LENGTH_LONG).show();
          }
			
		}else{
			Toast.makeText(getApplicationContext(),"Geocode finished with error: " + error, Toast.LENGTH_LONG).show();
		}	
	}

	@Override
	public boolean onMapObjectsSelected(List<ViewObject> objects) {
		for (ViewObject viewObject : objects) {
			if (viewObject.getBaseType() == ViewObjectType.USER_OBJECT) {
				MapObject mapObject = (MapObject) viewObject;
              if (mapObject.getType() == MapObjectType.MARKER) {
              	final MapMarker marker = FindObject((MapMarker) mapObject,true);
              	if(marker != null){
              		marker.showInfoBubble();
              		PlaceLink place = FindPlace(marker);
              		if(place != null){
              			PlaceRequest reg = place.getDetailsRequest();
              			if(reg != null){
              				reg.execute(new ResultListener<Place>(){
	    					@Override
	    					public void onCompleted(Place data, ErrorCode error) {
	    						// TODO Auto-generated method stub
	    						marker.hideInfoBubble();
	    						if(error == ErrorCode.NONE){
	    							Toast.makeText(getApplicationContext(),"Got details for: " + data.getName(), Toast.LENGTH_LONG).show();
	    						}else{
	    							Toast.makeText(getApplicationContext(),"get details finished with error: " + error, Toast.LENGTH_SHORT).show();
	    						}
	    					}
              			});
              			}
              		}
              	}
              	return false;
              }
			}
		}	
	
		return false;
	}
	
	private PlaceLink FindPlace(MapMarker marker) {
		PlaceLink ret = null;
  	
		for (int i = 0; i < places.size(); i++){
			if (places.get(i).getId().contentEquals(marker.getTitle())) {
				ret = places.get(i);
			}
		}
	    return ret;
	}
	
	private MapMarker FindObject(MapMarker marker, boolean bring_to_top) {
		MapMarker ret = null;
		int highest = 0;
		List<MapObject> addedMarkers = MapMarkers.getAllMapObjects();
		for (int i = 0; i < addedMarkers.size(); i++){
			if(highest < (addedMarkers.get(i).getZIndex())){
				highest = (addedMarkers.get(i).getZIndex());
			}
			if (addedMarkers.get(i).hashCode() == marker.hashCode()) {
				ret = (MapMarker) addedMarkers.get(i);
			}
		}
		
		if(bring_to_top && ret != null){
			ret.setZIndex((highest + 1));
		}
	    return ret;
	}

	
	@Override
	public void onMarkerDrag(MapMarker marker) {onMarkerMoved(marker);}
	@Override
	public void onMarkerDragEnd(MapMarker marker) {onMarkerMoved(marker);}
	@Override
	public void onMarkerDragStart(MapMarker marker) {onMarkerMoved(marker);}

	private void onMarkerMoved(MapMarker marker) {
		if(m_circle != null){ 
			m_circle.onMarkerMoved(marker);		
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
