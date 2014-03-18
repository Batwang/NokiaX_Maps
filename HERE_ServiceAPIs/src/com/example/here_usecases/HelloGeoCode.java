package com.example.here_usecases;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapMarker;
import com.here.android.search.ErrorCode;
import com.here.android.search.Location;
import com.here.android.search.ResultListener;
import com.here.android.search.geocoder.GeocodeRequest;

public class HelloGeoCode extends Activity {

	 // map embedded in the map fragment
  private Map map = null;
  private com.here.android.search.Geocoder geocoder = null;
  private GeocodeRequest request = null;
  private MapMarker marker= null;
  
  // map fragment embedded in this activity
  private MapFragment mapFragment = null;

  @Override
  public void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);
      setContentView(R.layout.geocoding);

      // Search for the map fragment to finish setup by calling init().
      mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
      mapFragment.init(new FragmentInitListener() {
          @Override
          public void onFragmentInitializationCompleted(InitError error) {
              if (error == InitError.NONE) {
                  // retrieve a reference of the map from the map fragment
                  map = mapFragment.getMap();
                  geocoder = MapFactory.getGeocoder();
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
  		
  		if(marker != null){
  			map.removeMapObject(marker);
  			marker = null;
  		}

  		request = geocoder.createOneBoxRequest(map.getCenter(), null, ((TextView)findViewById(R.id.geostring)).getText().toString());
  		request.execute(new ResultListener<List<Location>>(){
			@Override
			public void onCompleted(List<Location> data, ErrorCode error) {
			
				if(error == ErrorCode.NONE ){		
					if(data != null && data.size() > 0){
						MapMarker AddMarker = MapFactory.createMapMarker();
						AddMarker.setCoordinate(data.get(0).getCoordinate());
						AddMarker.setTitle(data.get(0).getAddress().getText());
				        AddMarker.setDraggable(false);
				        
				        map.addMapObject(AddMarker);
				        map.setCenter(AddMarker.getCoordinate(), MapAnimation.NONE);
					}else{
						Toast.makeText(getApplicationContext(),"No results for Geo coding request", Toast.LENGTH_LONG).show();
					}
				}else{
					Toast.makeText(getApplicationContext(),"Geocode finished with error: " + error, Toast.LENGTH_LONG).show();
				}	
			}
  		});
  	}
  }
}