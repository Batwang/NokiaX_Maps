package com.drjukka.example.mapsintabs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.here.android.mapping.FactoryInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapView;


public class MyOtherTabView extends Fragment  {

	private MapView mMapView;
    private Map mMap;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); 
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.other_tab, container, false);
        mMapView = (MapView) v.findViewById(R.id.map);
        setUpMapIfNeeded();   
        return v;
    }
    
    private void setUpMapIfNeeded() {
        if (mMap == null && mMapView != null) {
            mMap = (Map) mMapView.getMap();
            if (mMap != null) {
                setUpMap();
            }else{

            	 MapFactory.initFactory(getActivity().getApplicationContext(), new FactoryInitListener() {
					@Override
					public void onFactoryInitializationCompleted(InitError error) {
						// TODO Auto-generated method stub
						if(error == InitError.NONE){
							mMapView.setMap(MapFactory.createMap());
							mMap = (Map) mMapView.getMap();
							mMap.setCenter(MapFactory.createGeoCoordinate(48.8567, 2.3508), MapAnimation.NONE);
						}
					} 
            	 });	
            }
        }
    }
    
    private void setUpMap() {
   	 if(mMap != null){
   		   
        
 	   }
   }
}
