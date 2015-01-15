package smc.helpmap;

import java.util.ArrayList;

import smc.helpmap.MapLocationListener.locListener;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class MapActivity extends ActionBarActivity implements locListener {
	private LatLng SMC;
    private GoogleMap mMap;
    private MapLocationListener mll;
    private Marker markerMe;
    /** °O¿ý­y¸ñ */
    private ArrayList<LatLng> traceOfMe;
    private TextView mLong;
    private TextView mLat;
    private TextView mProvider;
    private String mProviders[] = new String[]{"GPS", "Network"};
    private int mSel = 0;
    private Activity mActivity;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_map);
		SMC = new LatLng(24.998379, 121.487960);
		RelativeLayout rel = (RelativeLayout)findViewById(R.id.infoLayout);
		mMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.map)).getMap();
		mLong = (TextView)this.findViewById(R.id.longitude);
		mLat = (TextView)this.findViewById(R.id.latitude);
		mProvider = (TextView)this.findViewById(R.id.provider);
		mActivity = this;
		rel.bringToFront();

        
		mll = new MapLocationListener(this, this);
        
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			new AlertDialog.Builder(this).setTitle("Choise Provider [" + mProviders[mSel] + "]")
			.setSingleChoiceItems(mProviders, mSel, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mSel = which;
				}
				
			}).setPositiveButton(android.R.string.ok, new OnClickListener() {

				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					mll.removeLocation();
					mll.setProvider(mSel);
					mll.updateLocation();
				}
				
			}).setNegativeButton(android.R.string.cancel, null)
			.show();
			
			return true;
		}
		else if (id == R.id.action_refresh) {
			if (mll.isEnable())
				mll.updateLocation();
			return true;
		}
		else if (id == R.id.action_clean) {
			cleanTrack();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if (mll.isEnable())
			mll.updateLocation();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (!mll.isEnable())
			mll.removeLocation();
	}

	/* (non-Javadoc)
	 * @see smc.helpmap.MapLocationListener.locListener#updateLocation()
	 */
	@Override
	public void updateLocation() {
		// TODO Auto-generated method stub
		if (mll != null) {
	        showMarkerMe(mll.getLat(), mll.getLong());        
	        cameraFocusOnMe(mll.getLat(), mll.getLong());
	        trackToMe(mll.getLat(), mll.getLong());
		}
	}
	
	private void showMarkerMe(double lat, double lng) {
		 if (markerMe != null) {
			 markerMe.remove();
		 }

		 MarkerOptions markerOpt = new MarkerOptions();
		 markerOpt.position(new LatLng(lat, lng));
		 markerOpt.title("I am here");		 
		 markerOpt.draggable(true);
		 markerMe = mMap.addMarker(markerOpt);
		 mLong.setText("" + lng);
		 mLat.setText("" + lat);
		 mProvider.setText(mll.getProvider());
		 //Toast.makeText(this, "lat:" + lat + ",lng:" + lng, Toast.LENGTH_SHORT).show();
	}
	
	private void cameraFocusOnMe(double lat, double lng) {
		 CameraPosition camPosition = new CameraPosition.Builder()
		    .target(new LatLng(lat, lng))
		    .zoom(16)
		    .build();

		 mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camPosition));
	}
	
	private void trackToMe(double lat, double lng) {
		if (traceOfMe == null) {
			traceOfMe = new ArrayList<LatLng>();
		}
		traceOfMe.add(new LatLng(lat, lng));

		PolylineOptions polylineOpt = new PolylineOptions();
		 
		for (LatLng latlng : traceOfMe) {
			polylineOpt.add(latlng);
		}

		polylineOpt.color(Color.RED);

		Polyline line = mMap.addPolyline(polylineOpt);
		line.setWidth(10);
	}
	
	private void cleanTrack() {
		mMap.clear();		
		traceOfMe = null;
		updateLocation();		
	}
	
}
