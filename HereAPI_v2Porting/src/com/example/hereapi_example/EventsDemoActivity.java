package com.example.hereapi_example;

import java.util.List;

import com.here.android.common.ViewObject;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.Map;
import com.here.android.mapping.Map.MapTransformListener;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapGestureListener;
import com.here.android.mapping.MapState;

import android.app.Activity;
import android.graphics.PointF;
import android.os.Bundle;
import android.widget.TextView;

public class EventsDemoActivity extends Activity 
implements MapGestureListener, MapTransformListener{

	private Map mMap;
    private TextView mTapTextView;
    private TextView mCameraTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.events_demo);

        mTapTextView = (TextView) findViewById(R.id.tap_text);
        mCameraTextView = (TextView) findViewById(R.id.camera_text);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
    }

    private void setUpMapIfNeeded() {
    	if(mMap == null){ 
     	   final EventsDemoActivity that = this;
  		   final MapFragment mapFragment = (MapFragment)getFragmentManager().findFragmentById(R.id.map);
  			// initialize the Map Fragment to create a map and
  			// attached to the fragment
  			mapFragment.init(new FragmentInitListener() {
  				@Override
  				public void onFragmentInitializationCompleted(InitError error) {
  					if (error == InitError.NONE) {
  						mMap = (Map) mapFragment.getMap();
  						mapFragment.getMapGesture().addMapGestureListener(that);
  						mMap.addMapTransformListener(that);
  					}
  				}
  			});
         }
    }
    
    @Override
	public void onMapTransformEnd(MapState mapState) {
		if(mapState !=null){
			mCameraTextView.setText(mapState.getCenter().toString());
		}else if(mMap !=null){
			mCameraTextView.setText(mMap.getCenter().toString());
		}
	}

	@Override
	public void onMapTransformStart() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean onDoubleTapEvent(PointF arg0) {
		mTapTextView.setText("Double tapped, point=" + arg0);
		return false;
	}

	@Override
	public boolean onLongPressEvent(PointF p) {
		mTapTextView.setText("long press, point=" + p);
		return false;
	}

	@Override
	public void onLongPressRelease() {
		mTapTextView.setText("long press released");
		
	}
	
	@Override
	public boolean onTwoFingerTapEvent(PointF p) {
		mTapTextView.setText("2 finger tapped, point=" + p);
		return false;
	}
	
	@Override
	public boolean onTapEvent(PointF p) {
		mTapTextView.setText("tapped, point=" + p);
		return false;
	}

	@Override
	public boolean onMapObjectsSelected(List<ViewObject> objects) {
		if(objects != null){
			mTapTextView.setText("onMapObjectsSelected, count=" + objects.size());
		}
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void onMultiFingerManipulationEnd() {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public void onPinchLocked() {
		mTapTextView.setText("Pinch locked");
		
	}

	@Override
	public boolean onPinchZoomEvent(float scaleFactor, PointF p) {
		mTapTextView.setText("Pinch zoom, point=" + p + ", scale=" + scaleFactor);
		return false;
	}

	@Override
	public boolean onRotateEvent(float rotateAngle) {
		mTapTextView.setText("Rotatetd, angle=" + rotateAngle);
		return false;
	}

	@Override
	public void onRotateLocked() {
		mTapTextView.setText("Rotate locked");
	}

	

	@Override
	public boolean onTiltEvent(float angle) {
		mTapTextView.setText("Tilted, angle=" + angle);
		return false;
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

    
}
