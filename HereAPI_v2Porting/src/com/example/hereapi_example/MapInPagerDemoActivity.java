package com.example.hereapi_example;

import com.here.android.mapping.FactoryInitListener;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapCompatibilityFragment;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapMarker;
import com.here.android.mapping.MapView;
import com.here.android.restricted.mapping.RestrictedMapFactory;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

public class MapInPagerDemoActivity  extends FragmentActivity {

	 private MyAdapter mAdapter;
	 private ViewPager mPager;

	 private MapView mMapView;
	 private Map mMap; 
	    
	    /** Called when the activity is first created. */
	    @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.map_in_pager_demo);
	        
	      
	        mAdapter = new MyAdapter(getSupportFragmentManager());

	        mPager = (ViewPager) findViewById(R.id.pager);
	        mPager.setAdapter(mAdapter);

	        // This is required to avoid a black flash when the map is loaded.  The flash is due
	        // to the use of a SurfaceView as the underlying view of the map.
	        mPager.requestTransparentRegion(mPager);
	     
	        mMapView = (MapView) findViewById(R.id.map);
	    
	        setUpMapIfNeeded();
	    }
	    
	    private void setUpMapIfNeeded() {
	        if (mMap == null && mMapView != null) {
	            mMap = mMapView.getMap();
	            if (mMap != null) {
	                setUpMap();
	            }else{
	            	 MapFactory.initFactory(getApplicationContext(), new FactoryInitListener() {

						@Override
						public void onFactoryInitializationCompleted(InitError error) {
							// TODO Auto-generated method stub
							if(error == InitError.NONE){
								mMapView.setMap(RestrictedMapFactory.createMap());
								mMap = mMapView.getMap();
								setUpMap();
							}
						} 
	            	 });	
	            }
	        }
	    }
	    
	    private void setUpMap() {
	    	 if(mMap != null){
	  		   MapMarker markker = MapFactory.createMapMarker();
	  		   markker.setCoordinate(mMap.getCenter());
	  		   markker.setTitle("Marker");
	  		   mMap.addMapObject(markker);
	  		   
	  		   mMap.setCenter(markker.getCoordinate(),MapAnimation.NONE);
	  	   }
	    }

	    /** A simple fragment that displays a TextView. */
	    public static class TextFragment extends Fragment{
	      @Override
	      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
	        return inflater.inflate(R.layout.text_fragment, container, false);
	      }
	    }
	    
	    public static class MyMapFragment extends Fragment{
	    	 
	    	@Override
		      public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle bundle) {
		        return inflater.inflate(R.layout.map_fragment, container, false);
		      }
		    }

	    /** A simple FragmentPagerAdapter that returns two TextFragment and a SupportMapFragment. */
	    public static class MyAdapter extends FragmentPagerAdapter {
	
	    	public MyAdapter(FragmentManager fragmentManager) {
	    		super(fragmentManager);
	    	}

	        @Override
	        public int getCount() {
	            return 3;
	        }
            	
           @Override
	        public Fragment getItem(int position) {
	            switch (position) {
	            case 0:
	            case 1:
	                return new TextFragment();
	            case 2:
	                return new MyMapFragment();
	            default:
	                return null;
	            }
	        }

	
	    }
}
