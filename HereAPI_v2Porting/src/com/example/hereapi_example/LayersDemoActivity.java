package com.example.hereapi_example;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.MapScheme;
import com.here.android.restricted.mapping.Map;
import com.here.android.mapping.MapFragment;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.Toast;

public class LayersDemoActivity extends Activity implements OnItemSelectedListener {

    private Map mMap;

    private CheckBox mTrafficCheckbox;
//    private CheckBox mMyLocationCheckbox;
    private CheckBox mBuildingsCheckbox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layers_demo);

        Spinner spinner = (Spinner) findViewById(R.id.layers_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.layers_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        mTrafficCheckbox = (CheckBox) findViewById(R.id.traffic);
       // mMyLocationCheckbox = (CheckBox) findViewById(R.id.my_location);
        mBuildingsCheckbox = (CheckBox) findViewById(R.id.buildings);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
        if (mMap != null) {
            updateTraffic();
            updateMyLocation();
            updateBuildings();
        }
    }

    private void setUpMapIfNeeded() {
    	if(mMap == null){ 

  		   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
  			// initialize the Map Fragment to create a map and
  			// attached to the fragment
  			mapFragment.init(new FragmentInitListener() {
  				@Override
  				public void onFragmentInitializationCompleted(InitError error) {
  					if (error == InitError.NONE) {
  						mMap = (Map) mapFragment.getMap();
  					}
  				}
  			});
         }
    }

    private boolean checkReady() {
        if (mMap == null) {
            Toast.makeText(this, R.string.map_not_ready, Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
    
    /**
     * Called when the Traffic checkbox is clicked.
     */
    public void onTrafficToggled(View view) {
        updateTraffic();
    }

    private void updateTraffic() {
        if (!checkReady()) {
            return;
        }
        mMap.setTrafficInfoVisible(mTrafficCheckbox.isChecked());
    }

    /**
     * Called when the MyLocation checkbox is clicked.
     */
    public void onMyLocationToggled(View view) {
        updateMyLocation();
    }

    private void updateMyLocation() {
    	Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the Buildings checkbox is clicked.
     */
    public void onBuildingsToggled(View view) {
        updateBuildings();
    }

    private void updateBuildings() {
        if (!checkReady()) {
            return;
        }
        mMap.setVenueMapTilesVisible(mBuildingsCheckbox.isChecked());
        mMap.setLandmarksVisible(mBuildingsCheckbox.isChecked());
    }
    
	@Override
	 public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        setLayer((String) parent.getItemAtPosition(position));
    }

	private void setLayer(String layerName) {
        if (!checkReady()) {
            return;
        }
        if (layerName.equals(getString(R.string.normal))) {
            mMap.setMapScheme(MapScheme.NORMAL_DAY);
        } else if (layerName.equals(getString(R.string.normal_night))) {
            mMap.setMapScheme(MapScheme.NORMAL_NIGHT);
        } else if (layerName.equals(getString(R.string.hybrid))) {
            mMap.setMapScheme(MapScheme.HYBRID_DAY);
        } else if (layerName.equals(getString(R.string.hybrid_transit))) {
            mMap.setMapScheme(MapScheme.HYBRID_DAY_TRANSIT);
        } else if (layerName.equals(getString(R.string.satellite))) {
            mMap.setMapScheme(MapScheme.SATELLITE_DAY);
        } else if (layerName.equals(getString(R.string.satellite_night))) {
            mMap.setMapScheme(MapScheme.SATELLITE_NIGHT);
        } else if (layerName.equals(getString(R.string.transit))) {
            mMap.setMapScheme(MapScheme.NORMAL_DAY_TRANSIT);
        } else if (layerName.equals(getString(R.string.transit_night))) {
            mMap.setMapScheme(MapScheme.NORMAL_NIGHT_TRANSIT);
        } else if (layerName.equals(getString(R.string.terrain))) {
            mMap.setMapScheme(MapScheme.TERRAIN_DAY);
        }  else {
            Log.i("LDA", "Error setting layer with name " + layerName);
        }
    }
	
	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub	
	}
}
