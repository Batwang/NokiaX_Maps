package com.example.hereapi_example;

import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.Map.MapTransformListener;
import com.here.android.mapping.MapEventListener;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapState;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Toast;

public class SnapshotDemoActivity  extends Activity implements MapTransformListener  {

	/**
     * Note that this may be null if the Google Play services APK is not available.
     */
    private Map mMap;
    private boolean mTakingShot = false;
    private CheckBox mWaitForMapLoadCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.snapshot_demo);
        mWaitForMapLoadCheckBox = (CheckBox) findViewById(R.id.wait_for_map_load);

        createMapIfReady();
    }

    @Override
    protected void onResume() {
        super.onResume();
        createMapIfReady();
    }

    private void createMapIfReady() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
        	final SnapshotDemoActivity that = this;
        	 final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
 			// initialize the Map Fragment to create a map and
 			// attached to the fragment
 			mapFragment.init(new FragmentInitListener() {
 				@Override
 				public void onFragmentInitializationCompleted(InitError error) {
 					if (error == InitError.NONE) {
 						mMap = (Map) mapFragment.getMap();
 						mMap.addMapTransformListener(that);
 					};
 				}
 			});
        }
    }

    /**
     * Called when the snapshot button is clicked.
     */
    public void onScreenshot(View view) {
        takeSnapshot();
    }

    private void takeSnapshot() {
        if (mMap == null) {
            return;
        }

        final ImageView snapshotHolder = (ImageView) findViewById(R.id.snapshot_holder);

        // here maps does not have this functionality
        
   /*     final SnapshotReadyCallback callback = new SnapshotReadyCallback() {
            @Override
            public void onSnapshotReady(Bitmap snapshot) {
                // Callback is called from the main thread, so we can modify the ImageView safely.
                snapshotHolder.setImageBitmap(snapshot);
            }
        };
    */
        
        if (mWaitForMapLoadCheckBox.isChecked()) {
        	mTakingShot = true;
        } else {
        	Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
            //mMap.snapshot(callback);
        }
    }

    /**
     * Called when the clear button is clicked.
     */
    public void onClearScreenshot(View view) {
        ImageView snapshotHolder = (ImageView) findViewById(R.id.snapshot_holder);
        snapshotHolder.setImageDrawable(null);
    }

	@Override
	public void onMapTransformEnd(MapState mapState) {

		if(mTakingShot){
			Toast.makeText(this, R.string.not_implemented_with_here, Toast.LENGTH_SHORT).show();
			mTakingShot = false;
			//mMap.snapshot(callback);
		}
	}

	@Override
	public void onMapTransformStart() {}


}
