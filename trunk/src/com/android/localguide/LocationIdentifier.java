package com.android.localguide;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

public class LocationIdentifier{
	
	public interface LocationIdentifierCallBack{
		public void gotLocation(Location location);
		public void settingsDisabled();
	}
	
	Context mContext;
	private LocationManager locationManager;
	LocationIdentifierCallBack mParent;
	ProgressDialog dialog;
	boolean isGPSenabled=false;
	boolean isNetworkenabled=false;
	boolean isSearching = false;
	Timer timer;

	public LocationIdentifier(Context context,LocationIdentifierCallBack parent)
	{
		mContext = context;
		mParent = parent;
	}
	public boolean settingsEnabled() {
		if(locationManager == null)
			locationManager = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);
		
		//exceptions will be thr own if provider is not permitted.
		try{
			isGPSenabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch(Exception ex){
			throw new RuntimeException(ex);
		}

		try{
			isNetworkenabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch(Exception ex){
			throw new RuntimeException(ex);
		}

		if(!isGPSenabled && !isNetworkenabled)
		{
			return false;
		}else {
			return true;
		}
	}
	public void getLocation()
	{
		isSearching= true;
		
		if(locationManager == null)
			locationManager = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);

		//exceptions will be thrown if provider is not permitted.
		try{
			isGPSenabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
		}
		catch(Exception ex){
			throw new RuntimeException(ex);
		}

		try{
			isNetworkenabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		}
		catch(Exception ex){
			throw new RuntimeException(ex);
		}

		if(!isGPSenabled && !isNetworkenabled)
		{
			mParent.settingsDisabled();
		}
		else
		{
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
			timer=new Timer();
			timer.schedule(new GetLastLocation(), 60000);
		}
	}
	

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			System.out.println("Locaiton manager locationListenerNetwork ********** called ");
			timer.cancel(); 
			mParent.gotLocation(location);
			isSearching = false;
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerGps);
		}
		public void onProviderDisabled(String provider) {

		}
		public void onProviderEnabled(String provider) {

		}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	LocationListener locationListenerGps = new LocationListener() {
		public void onLocationChanged(Location location) {
			System.out.println("Locaiton manager locationListenerGps ********** called ");
			timer.cancel();
			mParent.gotLocation(location);
			isSearching = false;
			locationManager.removeUpdates(this);
			locationManager.removeUpdates(locationListenerNetwork);
		}
		public void onProviderDisabled(String provider) {

		}
		public void onProviderEnabled(String provider) {

		}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};

	public boolean isSearchingLocation()
	{
		return isSearching;
	}

	class GetLastLocation extends TimerTask {
	@Override
	public void run() {
		System.out.println("Locaiton manager GetLastLocation timertask ********** ");
		locationManager.removeUpdates(locationListenerGps);
		locationManager.removeUpdates(locationListenerNetwork);
		Location networkLocation=null, gpsLocation=null;

		if(isGPSenabled)
			gpsLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

		if(isNetworkenabled)
			networkLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

		isSearching = false;

		//if there are both values use the latest one
		if(gpsLocation!=null && networkLocation!=null){
			if(gpsLocation.getTime()>networkLocation.getTime())
				mParent.gotLocation(gpsLocation);
			else
				mParent.gotLocation(networkLocation);
			return;
		}

		if(gpsLocation!=null){
			mParent.gotLocation(gpsLocation);
			return;
		}
		else if(networkLocation!=null){
			mParent.gotLocation(networkLocation);
			return;
		}
		
		mParent.gotLocation(null);
		
	    }
	}
    

}
