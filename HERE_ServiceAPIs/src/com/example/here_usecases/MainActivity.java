package com.example.here_usecases;

import android.os.Bundle;
import android.app.Activity;
import android.app.ListActivity;
import android.content.Context;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

public class MainActivity extends ListActivity {

	/**A simple POJO that holds the details about the demo that are used by the List Adapter.*/
    private static class DemoDetails {
        /**The resource id of the title of the demo.*/
        private final int titleId;
        /**The resources id of the description of the demo.*/
        private final int descriptionId;

        /**The demo activity's class.*/
        private final Class<? extends Activity> activityClass;

        public DemoDetails(int titleId, int descriptionId, Class<? extends Activity> activityClass) {
            super();
            this.titleId = titleId;
            this.descriptionId = descriptionId;
            this.activityClass = activityClass;
        }
    }
    
    /**
     * A custom array adapter that shows a {@link FeatureView} containing details about the demo.
     */
    private static class CustomArrayAdapter extends ArrayAdapter<DemoDetails> {

        /**@param demos An array containing the details of the demos to be displayed.         */
        public CustomArrayAdapter(Context context, DemoDetails[] demos) {
            super(context, R.layout.feature, R.id.title, demos);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            FeatureView featureView;
            if (convertView instanceof FeatureView) {
                featureView = (FeatureView) convertView;
            } else {
                featureView = new FeatureView(getContext());
            }

            DemoDetails demo = getItem(position);

            featureView.setTitleId(demo.titleId);
            featureView.setDescriptionId(demo.descriptionId);

            return featureView;
        }
    }
    
    private static final DemoDetails[] demos = {
    	new DemoDetails(R.string.hellogeo_title, R.string.hellogeo_description, HelloGeoCode.class),
    	new DemoDetails(R.string.geocode_title, R.string.geocode_description, GeoCode.class),
    	new DemoDetails(R.string.geocodeadd_title, R.string.geocodeadd_description, GeoCodeAddress.class),
      	new DemoDetails(R.string.revgeo_title, R.string.revgeo_description, ReverceGeoCode.class),
      	new DemoDetails(R.string.searchmap_title, R.string.searchmap_description, SearchMap.class),
      	new DemoDetails(R.string.exploremap_title, R.string.exploremap_description, ExploreMap.class),
      	new DemoDetails(R.string.exploreradius_title, R.string.exploreradius_description, ExploreRadius.class),
      	new DemoDetails(R.string.whatshere_title, R.string.whatshere_description, Whatshere.class),
      	new DemoDetails(R.string.simplerouting_title, R.string.simplerouting_description, SimpleRouting.class),
      	new DemoDetails(R.string.textsuggestion_title, R.string.textsuggestion_description, Text_suggestions.class),
      	new DemoDetails(R.string.mypos_title, R.string.mypos_description, MyPosition.class),
      	new DemoDetails(R.string.streetlev_title, R.string.streetlev_description, StreetLevel.class),
      	new DemoDetails(R.string.venues_title, R.string.venues_description, Venues.class),
      	new DemoDetails(R.string.mapextras_title, R.string.mapextras_description, MapExtras.class),
      	new DemoDetails(R.string.maptransit_title, R.string.maptransit_description, TransitMap.class),
    };
    
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        ListAdapter adapter = new CustomArrayAdapter(this, demos);
        setListAdapter(adapter);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        DemoDetails demo = (DemoDetails) getListAdapter().getItem(position);
        startActivity(new Intent(this, demo.activityClass));
    }
    
}
