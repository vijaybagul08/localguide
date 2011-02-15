package com.android.localguide.widgetprovider;

import java.util.ArrayList;
import java.util.List;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.RemoteViews;

import com.android.localguide.LocationIdentifier;
import com.android.localguide.R;
import com.android.localguide.LocationIdentifier.LocationIdentifierCallBack;

public class CellLocationService extends Service implements LocationIdentifierCallBack{

	private AppWidgetManager mAppWidgetManager;
	LocationIdentifier mLocationIdentifier;
	private Thread mTask;
	private Handler mLooperThreadHandler;
	ArrayList<AppWidgetItem> appWidgetsList;
	Geocoder mReverseGeoCoder;
	Context mContext;
	int currentAppWidgetId;
	
	class AppWidgetItem {
		CollectDataForCategory mConnector;
		int AppWidgetId;
	}
	
   
	public void onCreate()
	{
		System.out.println("On create my Service ::::::: ");
		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mLocationIdentifier = new LocationIdentifier (this.getApplicationContext(),this);
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int events = PhoneStateListener.LISTEN_CELL_LOCATION ;
        tm.listen(phoneStateListener, events);
        mReverseGeoCoder = new Geocoder(getApplicationContext());
        mContext = this.getApplicationContext();
        appWidgetsList = new ArrayList<AppWidgetItem>();
        
        // Start a looper thread ( life cycle is valid as long as main process
        
		mTask = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				mLooperThreadHandler = new Handler();
				Looper.loop();
			}
		});
		mTask.start();
		
	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("On startcommand my Service ::::::: ");
		
		if(intent.getIntExtra("appwidgetid", 0) != 0 )
		{
			// Some other widget instance is still waiting for its current location
			if(mLocationIdentifier.isSearchingLocation() == false )
			{
				mLocationIdentifier.getLocation();
			}
			
			// Update the view with "Finding the location...."
	   		RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
			view.setTextViewText(R.id.text, "Finding the location ...");
			System.out.println("Updating the widget id ********* "+intent.getIntExtra("appwidgetid", 0));
			mAppWidgetManager.updateAppWidget(intent.getIntExtra("appwidgetid", 0), view);
	   		AppWidgetItem item = new AppWidgetItem();
			item.AppWidgetId = intent.getIntExtra("appwidgetid", 0); 
			item.mConnector= new CollectDataForCategory();
			appWidgetsList.add(item);
		}
		else
		{
			int deleteAppId = intent.getIntExtra("deleteAppWidgetId", 0);
			System.out.println("Delete app widget id ********* "+deleteAppId);
			//Delete from the appWidgetList
			int i=0;
			for(i=0;i<appWidgetsList.size();i++)
			{
				if(deleteAppId == appWidgetsList.get(i).AppWidgetId)
					break;
			}
			appWidgetsList.remove(i);
			for(i=0;i<appWidgetsList.size();i++)
			{
				System.out.println("Delete app widget id ********* "+appWidgetsList.get(i).AppWidgetId);
			}
		}
		return START_STICKY;
	}
	
    public IBinder onBind(Intent intent) {
        return null;
    }

	public void onDestroy()
	{
		System.out.println("On Destroy my Service ::::::: ");
		mLooperThreadHandler.removeCallbacks(updateWidgetsRunnable);
		
	}
	
	public void gotLocation(Location location)
	{
		if(location !=null)
		System.out.println("Location obtained is ********** "+location.toString());
		
		try{
		List<Address> mAddressList = mReverseGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		   if (mAddressList.size()> 0){
					String  currlocation = "Location: "+mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
					currlocation+="\n";
					currlocation+="Loading the results";
					RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
					view.setTextViewText(R.id.text, currlocation);
					
					SharedPreferences prefs = getApplicationContext().getSharedPreferences(WidgetConfigureActivity.PREFS_NAME,0);
					//Get list of appWidgetIds
					int[] appWidgetIds;
					appWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName("com.android.localguide","com.android.localguide.widgetprovider.WidgetProvider"));
					
					for(int i =0;i<appWidgetsList.size();i++)
					{
						mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
						String searchString = currlocation+","+prefs.getString("category"+appWidgetsList.get(i).AppWidgetId, null);
						System.out.println("Searchstrings are ********* "+searchString);
					}
					startUpdatingWidgetProviders();
			  }
		   }
		   catch(Exception e)
		   {
			   System.out.println("Geo reverse coding is having error");
		   }

		
		
	}
	
	private void startUpdatingWidgetProviders()
	{
		mLooperThreadHandler.post(updateWidgetsRunnable ) ;
		
	}
	
	public Runnable updateWidgetsRunnable = new Runnable() {
			int count = 0;
			public void run() {
				System.out.println("Lopper thread ***************** "+count+"    Size of list is "+appWidgetsList.size() );
				for(int i =0;i<appWidgetsList.size();i++)
				{
					String result;
					
//					result = appWidgetsList.get(i).mConnector.title.get(count);
//					result+= "\n";
//					result = appWidgetsList.get(i).mConnector.address.get(count);
//					result+= "\n";
//					result = appWidgetsList.get(i).mConnector.phonenumbers.get(count);
					result = "Count is ***** "+count;
					RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
					view.setTextViewText(R.id.text, result);
					mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);

				}
				count++;
				
				if(count==7)
					count=0;
				
				mLooperThreadHandler.postDelayed(this, 3000);
			}
		};
	
    private final PhoneStateListener phoneStateListener = new PhoneStateListener(){

    			@Override
                public void onCellLocationChanged(CellLocation location)
                {
    				System.out.println("On cell location changed *********************");
                        String locationString = location.toString();
                        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
                        
                        int cellid = loc.getCid();
                        int lac = loc.getLac();
                        String cellId = "CELL-ID :"+cellid+"\n";
                        cellId+="LAC: "+lac;
                        
                        // Get the current location
                		if(mLocationIdentifier.isSearchingLocation() == false )
                		{
                			mLocationIdentifier.getLocation();
                		}
                		
                        super.onCellLocationChanged(location);
                }
    };
}
