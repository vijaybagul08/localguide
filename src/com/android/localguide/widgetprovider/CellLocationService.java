package com.android.localguide.widgetprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.view.View;
import android.widget.RemoteViews;

import com.android.localguide.LocationIdentifier;
import com.android.localguide.OptionsScreen;
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
	ExecutorService executor;
	int poolSize = 4;
	int maxPoolSize = 4;
	long keepAliveTime = 10;
	SharedPreferences prefs;
	class AppWidgetItem {
		CollectDataForCategory mConnector;
		int AppWidgetId;
		String category;
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
         prefs = getApplicationContext().getSharedPreferences(WidgetConfigureActivity.PREFS_NAME,0);
        //Create a pool of 4 threads to communicate to the cloud to fetch local search results.
        executor= Executors.newFixedThreadPool(4);
        
        // Start a looper thread life cycle is valid as long as main process
        
		mTask = new Thread(new Runnable() {
			public void run() {
				Looper.prepare();
				mLooperThreadHandler = new Handler();
				Looper.loop();
			}
		});
		mTask.start();
		
		// Calling immediately after the task is started is giving nullpointer exception for Looperthreadhandler
		//startUpdatingWidgetProviders(); 
		
		// Need to register for screen OFF and ON events to stop and pause the looper thread.
	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("On startcommand my Service ::::::: ");
		if(intent !=null)
		{
		// appWidget id is set to zero, it means, the intent is triggered to delete a appWidget instance from the list.
		if(intent.getIntExtra("appwidgetid", 0) != 0 && intent.getIntExtra("appwidgetid", 0) != -1 )
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
			item.category = prefs.getString("category"+intent.getIntExtra("appwidgetid", 0), null);
			System.out.println("category is ********* "+item.category);
			appWidgetsList.add(item);
			
			// Call the looper thread when the first element is added
			if(appWidgetsList.size() == 1)
				startUpdatingWidgetProviders();
		}
		else if (intent.getIntExtra("appwidgetid", 0) == -1)
		{
			// Get the update appWidget id and remove it from the list.
			int updateAppId = intent.getIntExtra("updateAppWidgetId", 0);
			System.out.println("update widget id for "+updateAppId);
			for(int i=0;i<appWidgetsList.size();i++)
			{
				if(updateAppId == appWidgetsList.get(i).AppWidgetId)
				{
					System.out.println("update widget id for "+appWidgetsList.get(i).category);
					break;
				}
			}
		}
		else if(intent.getIntExtra("appwidgetid", 0) == 0)
		{
			// Get the delete appWidget id and remove it from the list.
			int deleteAppId = intent.getIntExtra("deleteAppWidgetId", 0);

			//Delete from the appWidgetList
			int i=0;
			for(i=0;i<appWidgetsList.size();i++)
			{
				if(deleteAppId == appWidgetsList.get(i).AppWidgetId)
				{
					appWidgetsList.remove(i);
					break;
				}
			}

		}
		}
		else
		{
			// If the process is killed and restarted by the OS, then we need to collect the appwidgetid and categories from
			// prefs and fill the appWidgetsList then start the looper thread or get location
	   		AppWidgetItem item = new AppWidgetItem();
			item.AppWidgetId = intent.getIntExtra("appwidgetid", 0); 
			item.mConnector= new CollectDataForCategory();
			appWidgetsList.add(item);
			startUpdatingWidgetProviders();  
		}
		return START_STICKY;
	}
	
    public IBinder onBind(Intent intent) {
        return null;
    }

	public void onDestroy()
	{
		//Remove the runnables from the looper thread
		mLooperThreadHandler.removeCallbacks(updateWidgetsRunnable);
		
	}
	
	public void gotLocation(Location location)
	{
		if(location !=null)
		System.out.println("Location obtained is ********** "+location.toString());
		
		try{
			
			// Use the rever Geo coder to conver lat and long to a valid location string.
			List<Address> mAddressList = mReverseGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		   
			if (mAddressList.size()> 0){
			
					String  currlocation = "Location: "+mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
					currlocation+="\n";
					currlocation+="Loading the results....... pls wait";
					
					RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
					view.setTextViewText(R.id.text, currlocation);
					
					
					
					for(int i =0;i<appWidgetsList.size();i++)
					{
						mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
						// Form the search String. Use the preferences to fetch the category for corresponding appWidget.
						String searchString = mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0)+","+prefs.getString("category"+appWidgetsList.get(i).AppWidgetId, null);
						appWidgetsList.get(i).mConnector.setSearchString(searchString);
						appWidgetsList.get(i).mConnector.setStartedSearch(true);
						
						final int j =i;
						executor.execute(new Runnable ()
						{
							public void run()
							{
								appWidgetsList.get(j).mConnector.sendSearchRequest();		
							}
						});
						
						
					}
			  }
			else
			{
				
			}
		   }
		   catch(Exception e)
		   {
			   System.out.println("Geo reverse coding is having error");
		   }
		
	}
	

	public Runnable updateWidgetsRunnable = new Runnable() {
		
			//int count = 0;
			boolean state = true;
			public void run() {
				
				System.out.println("Lopper thread *****************  Size of list is "+appWidgetsList.size() );
				if(appWidgetsList.size() > 0)
				{
					// Size greater than zero 
					for(int i =0;i<appWidgetsList.size();i++)
					{
						//Check if searching is going for each appwidget
						//Already connection plese wait 
						if(appWidgetsList.get(i).mConnector.isStarted == false)
						{
							String result;
							result = appWidgetsList.get(i).mConnector.getValue();
						
							RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
							if(state)
							{
								view.setViewVisibility(R.id.widgetLayout1, View.GONE);
					            view.setViewVisibility(R.id.widgetLayout2, View.GONE);
					            state = false;
							}
							else
							{
					            view.setViewVisibility(R.id.widgetLayout1, View.GONE);
					            view.setViewVisibility(R.id.widgetLayout2, View.GONE);
					            state = true;
							}
				            
							view.setTextViewText(R.id.text, result);
							view.setTextViewText(R.id.button, appWidgetsList.get(i).category+"  (More)");
							Intent intent = new Intent();
				     		intent.setClass(mContext, OptionsScreen.class);
				       		Bundle bun = new Bundle();
				       		//System.out.println(appWidgetsList.get(i).mConnector.result);
			                bun.putString("resultString",appWidgetsList.get(i).mConnector.result);
			                bun.putInt("position", appWidgetsList.get(i).mConnector.getCurrentCount()); 
			                intent.putExtras(bun);
			        		
			                PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
			                        0 /* no requestCode */, intent, 0 /* no flags */);
			                view.setOnClickPendingIntent(R.id.text, pendingIntent);
			                
							Intent serviceIntent = new Intent(mContext, CellLocationService.class);
							serviceIntent.putExtra("appwidgetid", -1);
							serviceIntent.putExtra("updateAppWidgetId", appWidgetsList.get(i).AppWidgetId);
				       		
			                pendingIntent = PendingIntent.getService(mContext,
			                        0 /* no requestCode */, serviceIntent, 0 /* no flags */);
			                
			                view.setOnClickPendingIntent(R.id.button, pendingIntent);
			                
			                
							mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
						}
					}
					
					mLooperThreadHandler.postDelayed(this, 5000);
				}
				else
					mLooperThreadHandler.postDelayed(this, 10000);
				
			}
		};
	
		private void startUpdatingWidgetProviders()
		{
			if(mLooperThreadHandler != null)
			mLooperThreadHandler.post(updateWidgetsRunnable ) ;
			
		}
		
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
