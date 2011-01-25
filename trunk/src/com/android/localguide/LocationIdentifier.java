package com.android.localguide;

import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class LocationIdentifier{

	Context mContext;
	private LocationManager locationManager;
	WelcomeScreen mParent;
	ProgressDialog dialog;
    boolean isGPSenabled=false;
    boolean isNetworkenabled=false;
    Timer timer;
	public LocationIdentifier(Context context,WelcomeScreen parent)
	{
	mContext = context;
	mParent = parent;
	}
	
	public void getLocation()
	{
	
		if(locationManager == null)
	   locationManager = (LocationManager)mContext.getSystemService(mContext.LOCATION_SERVICE);

	       //exceptions will be thrown if provider is not permitted.
        try{
        	isGPSenabled=locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        }
        catch(Exception ex){}
        try{
        	isNetworkenabled=locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        	}
        catch(Exception ex){}
        
        if(!isGPSenabled && !isNetworkenabled)
        {
        	mParent.gotLocation(null);
        }
        else
        {
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
		//locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
	       timer=new Timer();
	      //  timer.schedule(new GetLastLocation(), 20000);
        }
	}
	

	LocationListener locationListenerNetwork = new LocationListener() {
		public void onLocationChanged(Location location) {
			timer.cancel();
            mParent.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerGps);
		}
		public void onProviderDisabled(String provider) {
			System.out.println("NETWORK ************** provider Disabled");
		}
		public void onProviderEnabled(String provider) {
			System.out.println("NETWORK ************** provider Enabled");
		}
		public void onStatusChanged(String provider, int status, Bundle extras) {}
	};
	
	LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
        	System.out.println(" ********** GPS Locatoin is :::"+location.toString());
        	Toast.makeText(mContext, location.toString(), 4000).show();
        	timer.cancel();
            mParent.gotLocation(location);
            locationManager.removeUpdates(this);
            locationManager.removeUpdates(locationListenerNetwork);
        }
        public void onProviderDisabled(String provider) {
        	System.out.println("GPS ************** provider Disabled");
        }
        public void onProviderEnabled(String provider) {
        	System.out.println("GPS************** provider Enabled");
        }
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };
    
    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
        	locationManager.removeUpdates(locationListenerGps);
        	locationManager.removeUpdates(locationListenerNetwork);
        	Toast.makeText(mContext, "Timer expired removing listeners", 4000).show();
             Location networkLocation=null, gpsLocation=null;
             if(isGPSenabled)
            	 gpsLocation=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
             if(isNetworkenabled)
            	 networkLocation=locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

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
             if(networkLocation!=null){
            	 mParent.gotLocation(networkLocation);
                 return;
             }
             mParent.gotLocation(null);
        }
    }
    

}