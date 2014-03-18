package com.example.hereapi_example;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import com.here.android.common.Image;
import com.here.android.mapping.FragmentInitListener;
import com.here.android.mapping.InitError;
import com.here.android.mapping.LocalMesh;
import com.here.android.mapping.Map;
import com.here.android.mapping.MapAnimation;
import com.here.android.mapping.MapFactory;
import com.here.android.mapping.MapFragment;
import com.here.android.mapping.MapLocalModel;
import com.here.android.mapping.MapOverlayType;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class GroundOverlayDemoActivity extends Activity implements OnSeekBarChangeListener {

    private static final int TRANSPARENCY_MAX = 100;
    
    private static final double NEWARK_lat = 40.714086;
    private static final double NEWARK_lon = -74.228697;

    private Map mMap = null;
    private MapLocalModel mGroundOverlay = null;
    private LocalMesh m_mesh;
    private SeekBar mTransparencyBar;

    private float mTransparency = 0;
    private int mCurrentEntry;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ground_overlay_demo);

        mTransparencyBar = (SeekBar) findViewById(R.id.transparencySeekBar);
        mTransparencyBar.setMax(TRANSPARENCY_MAX);
        mTransparencyBar.setProgress(0);

        setUpMapIfNeeded();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();
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
						DoAddTheOverlay();
					}
				}
			});
     }
	}
	
	private void DoAddTheOverlay() {
		
		if(mMap == null){
			return;
		}
		
		if(mGroundOverlay != null){
			mMap.removeMapObject(mGroundOverlay);
			mGroundOverlay = null;
		}
		
		mMap.setCenter(MapFactory.createGeoCoordinate(NEWARK_lat, NEWARK_lon), MapAnimation.NONE,14,0,0);
		
		Image image = MapFactory.createImage();
		
		if(mCurrentEntry == 0){	
			image.setBitmap(setTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.newark_nj_1922), mTransparency));
		}else{
			image.setBitmap(setTransparency(BitmapFactory.decodeResource(getResources(), R.drawable.newark_prudential_sunny), mTransparency));
		}
		
		//Transparency ?
		
	    mGroundOverlay = MapFactory.createMapLocalModel();
	    mGroundOverlay.setTexture(image);
	    mGroundOverlay.setDynamicScalingEnabled(false);
	    
	    mGroundOverlay.setAnchor(MapFactory.createGeoCoordinate(NEWARK_lat, NEWARK_lon));
	    mGroundOverlay.setOverlayType(MapOverlayType.ROAD_OVERLAY);
	    
	    m_mesh = MapFactory.createLocalMesh();

        // 1. create mesh of two triangles to make up the rectangle of the image
        // the rectangle coordinates from top left clockwise  0..4, match up with the
        // indices

        IntBuffer vertexIndices = IntBuffer.allocate(6);

        vertexIndices.put(0);
        vertexIndices.put(1);
        vertexIndices.put(2);

        vertexIndices.put(2);
        vertexIndices.put(1);
        vertexIndices.put(3);

        m_mesh.setVertexIndices(vertexIndices);

        // 2. need to offset the anchor point and adjust to the Here coordinate system
        // where (0,0) is the center of the image (anchor)

        float w = image.getWidth();
        float h = image.getHeight();
        float u = 0;
        float v = 1;

        float anchor_east = (float) ((u - 0.5) * w); // meters east from the anchor
        float anchor_north = (float) ((0.5 - v) * h); // meters north from the anchor

        FloatBuffer vertices = FloatBuffer.allocate(4 * 3);

        vertices.put(-w/2 - anchor_east);
        vertices.put(h/2 - anchor_north);
        vertices.put(0);

        vertices.put(w/2 - anchor_east);
        vertices.put(h/2 - anchor_north);
        vertices.put(0);

        vertices.put(-w/2 - anchor_east);
        vertices.put(-h/2 - anchor_north);
        vertices.put(0);

        vertices.put(w/2 - anchor_east);
        vertices.put(-h/2 - anchor_north);
        vertices.put(0);

        m_mesh.setVertices(vertices);

        // 3. map the texture to the 4 vertices of the rectangle

        FloatBuffer uvCoordinates = FloatBuffer.allocate(8);

        float array[] = {0, 0, 1, 0, 0, 1, 1 , 1};
        uvCoordinates.put(array);

        m_mesh.setTextureCoordinates(uvCoordinates);

        mGroundOverlay.setMesh(m_mesh);
        mGroundOverlay.setYaw(mMap.getOrientation());
	    
        mMap.addMapObject(mGroundOverlay);
	    mTransparencyBar.setOnSeekBarChangeListener(this);
	}

	
	private Bitmap setTransparency(Bitmap bitmap, float transparency) {

		Bitmap m_bm = bitmap;

        Bitmap bm = Bitmap.createBitmap(m_bm.getWidth(), m_bm.getHeight(),Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bm);
        Paint paint = new Paint();
        int opacity = 255 - (int) (255 * transparency);
        paint.setAlpha(opacity);
        canvas.drawBitmap(m_bm, 0, 0, paint);

        return bm;
    }

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (mGroundOverlay != null) {
			mTransparency = ((float) progress / (float) TRANSPARENCY_MAX);
			DoAddTheOverlay();
        }
	}

	@Override
	public void onStartTrackingTouch(SeekBar arg0) {}
	@Override
	public void onStopTrackingTouch(SeekBar arg0) {}

	public void switchImage(View view) {
		if(mCurrentEntry == 0){
			mCurrentEntry = 1;
		}else{
			mCurrentEntry = 0;
		}
			
		DoAddTheOverlay();
    }
}
