/**
 * <p>Title: </p>
 *
 * <p>Description: </p>
 *
 * <p>Copyright: Copyright (c) 2013</p>
 *
 * <p>Company: </p>
 *
 * @author billw
 * @version 1.0.5
 */
package smc.helpmap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;



/**
 * @author billw
 *
 */
public class MapLocationListener implements LocationListener {

	private Context mContext;
	private LocationManager mLms;
	private String bestProvider = LocationManager.GPS_PROVIDER;	//最佳資訊提供者
	private boolean getService = false;
	private double mLongitude;
	private double mLatitude;
	private Activity mActivity;
	private int mProvider = 0; //0:GPS 1:Network
	
	public MapLocationListener(Context context, Activity act) {
		mContext = context;
		mLongitude = 0.0;
		mLatitude = 0.0;
		mActivity = act;
		_init();
	}
	
	public MapLocationListener(Context context, Activity act, int provider) {
		mContext = context;
		mLongitude = 0.0;
		mLatitude = 0.0;
		mActivity = act;
		mProvider = provider;
		_init();
	}

	private void _init() {
		//取得系統定位服務
		LocationManager status = (LocationManager) (mContext.getSystemService(Context.LOCATION_SERVICE));
		if (status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			//如果GPS或網路定位開啟，呼叫locationServiceInitial()更新位置
			getService = true;	//確認開啟定位服務
			locationServiceInitial();
		} else {
			Toast.makeText(mContext, "請開啟定位服務", Toast.LENGTH_LONG).show();
			getService = false;	//確認開啟定位服務
			((Activity)mContext).startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));	//開啟設定頁面
		}
	}
	
	private void getLocation(Location location) {	//將定位資訊顯示在畫面中
		if (location != null) {
			mLongitude = location.getLongitude();	//取得經度
			mLatitude = location.getLatitude();	//取得緯度
			((MapActivity)mActivity).updateLocation();
		}
		else {
			Toast.makeText(mContext, "無法定位座標", Toast.LENGTH_LONG).show();
		}
	}
	

	private void locationServiceInitial() {
		mLms = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);	//取得系統定位服務

		//1.選擇最佳提供器
		Criteria criteria = new Criteria();
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		criteria.setAltitudeRequired(false);
		criteria.setBearingRequired(false);
		criteria.setCostAllowed(true);
		criteria.setPowerRequirement(Criteria.POWER_LOW);
		  
		bestProvider = mLms.getBestProvider(criteria, true);
		setProvider(mProvider); 
		
	}
	
	public void updateLocation() {
		if (getService)
			mLms.requestLocationUpdates(bestProvider, 1000, 5, this);
	}
		
	public void removeLocation() {
		if (getService) {
			mLms.removeUpdates(this);	//離開頁面時停止更新
		}
	}
	
	public double getLong() {
		return this.mLongitude;
	}
	
	public double getLat() {
		return this.mLatitude;
	}
	
	public String getProvider() {
		return bestProvider;
	}
	
	public void setProvider(int provider) {
		if (provider == 0 && mLms.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
			bestProvider = LocationManager.GPS_PROVIDER;
		}

		if (provider == 1 && mLms.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
			bestProvider = LocationManager.NETWORK_PROVIDER;
		}
		
		Location location = mLms.getLastKnownLocation(bestProvider);
		getLocation(location);

	}
	
	public boolean isEnable() {
		return this.getService;
	}
	
	public interface locListener {
		public void updateLocation();
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String)
	 */
	@Override
	public void onProviderDisabled(String arg0) {
		// TODO Auto-generated method stub
	}
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String)
	 */
	@Override
	public void onProviderEnabled(String arg0) {
		// TODO Auto-generated method stub
		
	}
	/* (non-Javadoc)
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int, android.os.Bundle)
	 */
	@Override
	public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
	 */
	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		this.getLocation(arg0);
	}
	


}
