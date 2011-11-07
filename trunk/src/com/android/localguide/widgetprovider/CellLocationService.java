package com.android.localguide.widgetprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.provider.Contacts.People;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.RemoteViews;
import android.widget.Toast;

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
	ArrayList<AppWidgetItem> pendingAppWidgetsList;
	Geocoder mReverseGeoCoder;
	Context mContext;
	boolean looperthreadStarted = false;
	int currentAppWidgetId;
	ExecutorService executor;
	int poolSize = 4;
	int maxPoolSize = 4;
	long keepAliveTime = 10;
	SharedPreferences prefs;
	private final int NO_MATCH = -102;
	private final int DELETE_ID = -100;
	private final int UPDATE_ID = -101;
	private boolean isFirstTimeStarting = false;
	public static final String PREFS_NAME = "LocalguideWidgetPrefs";
	
	class AppWidgetItem {
		CollectDataForCategory mConnector;
		int AppWidgetId;
		String category;
		
	}
	
    BroadcastReceiver mNetworkStateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {
System.out.println("Network is connectivity is changed ****************** ");
                if(checkInternetConnection() == true)
                {
                	System.out.println("Network is connectivity is changed ****************** 111");
					// Some other widget instance is still waiting for its current location
					if(mLocationIdentifier.isSearchingLocation() == false )
					{
						mLocationIdentifier.getLocation();
					}
	
					for(int i =0;i< pendingAppWidgetsList.size();i++)
					{
						AppWidgetItem item = pendingAppWidgetsList.get(i);
						appWidgetsList.add(item);
					}
					
					pendingAppWidgetsList.clear();
	
					// Call the looper thread when the first element is added
					if(appWidgetsList.size() == 1)
					{
						System.out.println("In appwidget size euqals 1 starting thread  ");
						startUpdatingWidgetProviders();
					}
	
	                }
                }
            }
        };

   
	public void onCreate()
	{

		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mLocationIdentifier = new LocationIdentifier (this.getApplicationContext(),this);
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int events = PhoneStateListener.LISTEN_CELL_LOCATION ;
        tm.listen(phoneStateListener, events);
        mReverseGeoCoder = new Geocoder(getApplicationContext());
        mContext = this.getApplicationContext();
        appWidgetsList = new ArrayList<AppWidgetItem>();
        pendingAppWidgetsList = new ArrayList<AppWidgetItem>();
        prefs = getApplicationContext().getSharedPreferences(WidgetConfigureActivity.PREFS_NAME,0);
        isFirstTimeStarting = true;
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
		// receive broadcasts
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mContext.registerReceiver(mNetworkStateIntentReceiver, filter);

	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		
		if(intent !=null)
		{
			
			// appWidget id is set to zero, it means, the intent is triggered to delete a appWidget instance from the list.
			if (intent.getIntExtra("appwidgetid", NO_MATCH) == UPDATE_ID)
			{
				
				// Get the update appWidget id and remove it from the list.
				int updateAppId = intent.getIntExtra("updateAppWidgetId", 0);
				System.out.println("update widget id for "+updateAppId);
				for(int i=0;i<appWidgetsList.size();i++)
				{
					
					if(updateAppId == appWidgetsList.get(i).AppWidgetId)
					{ 
				   		RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout3);
				   		if(checkInternetConnection() == true)
				   		{
				   			view.setTextViewText(R.id.title, "Finding the location ...");
				   			
				   		}
				   		else
				   			view.setTextViewText(R.id.title, "No internet connection..Please connect to internet...");

						mAppWidgetManager.updateAppWidget(updateAppId, view);
						appWidgetsList.get(i).mConnector.updateMoreResults();
						/* ask for update from CollectDataForCategory */
						
						break;
					}
				}
			}
			else if(intent.getIntExtra("appwidgetid", NO_MATCH) == DELETE_ID)
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
			else if(intent.getIntExtra("appwidgetid", NO_MATCH) != DELETE_ID && intent.getIntExtra("appwidgetid", NO_MATCH) != UPDATE_ID )
			{
				// Some other widget instance is still waiting for its current location
				if(mLocationIdentifier.isSearchingLocation() == false )
				{
					mLocationIdentifier.getLocation();
				}
				
				// Update the view with "Finding the location...."
				
		   		RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout3);
		   		if(checkInternetConnection() == true)
		   		{
		   			view.setTextViewText(R.id.title, "Finding the location ...");
		   			
		   		}
		   		else
		   			view.setTextViewText(R.id.title, "No internet connection..Please connect to internet...");
				
				mAppWidgetManager.updateAppWidget(intent.getIntExtra("appwidgetid", 0), view);
				
				/* There might be a scenario like.. if there is appwidget running and the service crashes 2 times... then 
				 * appwidget wont be updated. In such case, when the user adds a another widget, then it will come to this place.
				 * So we need to check if the count from preference value is same as appwidgetList count. It wont be same, so 
				 * we need to fetch those appwidget ids and category and add it as a item in appWidgetsList. So that , that appwidget
				 * will be updated from this cycle.
				 */
				
				if(prefs.getInt("count", 0)-1 > appWidgetsList.size() )
				{
					for(int i=0;i<prefs.getInt("count", 0)-1;i++)
					{
					AppWidgetItem item = new AppWidgetItem();
					item.AppWidgetId = prefs.getInt("appwidgetid"+i, 0); 
					item.mConnector= new CollectDataForCategory();
					item.category = prefs.getString("category"+i, null);
					appWidgetsList.add(item);
					}
				}
				
		   		AppWidgetItem item = new AppWidgetItem();
				item.AppWidgetId = intent.getIntExtra("appwidgetid", 0); 
				item.mConnector= new CollectDataForCategory();
				int currcount = prefs.getInt("count", 0);
				if(currcount != 0)
					--currcount;
				item.category = prefs.getString("category"+currcount, null);
				
		   		if(checkInternetConnection() == false)
		   		{
		   			
		   			pendingAppWidgetsList.add(item);
		   		}
		   		else
		   		{
				appWidgetsList.add(item);
				
				// Call the looper thread when the first element is added
				if(appWidgetsList.size() == 1);
					startUpdatingWidgetProviders();
		   		}
			}

		}
		else
		{
			// If the process is killed and restarted by the OS, then we need to collect the appwidgetid and categories from
			// prefs and fill the appWidgetsList then start the looper thread or get location

   		    SharedPreferences prefs = mContext.getSharedPreferences(WidgetConfigureActivity.PREFS_NAME, 0);
	        Editor editor = null;
	        int count = prefs.getInt("count", 0);
	        if(appWidgetsList.size() == 0)
	        {
				if(mLocationIdentifier.isSearchingLocation() == false )
				{
					mLocationIdentifier.getLocation();
				}
		        for(int i=0;i<count;i++)
		        {
			   		AppWidgetItem item = new AppWidgetItem();
					item.AppWidgetId =  prefs.getInt("appwidgetid"+i, 0);
					item.mConnector= new CollectDataForCategory();
					item.category = prefs.getString("category"+i, "");
					appWidgetsList.add(item);
		        }

				
	        }
	        else
	        {
	        	System.out.println("Size if has some values ");
	        }
//	   		appWidgetsList.add(item);
			startUpdatingWidgetProviders();  
		}
		return START_STICKY;
	}
	
    public IBinder onBind(Intent intent) {
        return null;
    }

	private boolean checkInternetConnection() {

		ConnectivityManager conMgr = (ConnectivityManager) mContext.getSystemService (mContext.CONNECTIVITY_SERVICE);

		// ARE WE CONNECTED TO THE NET

		if (conMgr.getActiveNetworkInfo() != null
		&& conMgr.getActiveNetworkInfo().isAvailable()
		&& conMgr.getActiveNetworkInfo().isConnected()) {
			return true;
		} else {
			return false;
		}

	} 
	
	public void onDestroy()
	{
		//Remove the runnables from the looper thread
		mLooperThreadHandler.removeCallbacks(updateWidgetsRunnable);
		
	}
	   public void settingsDisabled() {
		   
	   
	   }
	public void gotLocation(Location location)
	{
		if(location !=null)
		
		try{
			
			// Use the rever Geo coder to conver lat and long to a valid location string.
			List<Address> mAddressList = mReverseGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		   
			if (mAddressList.size()> 0){
			
					String  currlocation = "Location: \n\n"+mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
					currlocation+="\n\n";
					currlocation+="Loading the results....... pls wait";
					
					RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout3);
					view.setTextViewText(R.id.title, currlocation);

					for(int i =0;i<appWidgetsList.size();i++)
					{
						mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
						// Form the search String. Use the preferences to fetch the category for corresponding appWidget.
						String searchString = mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0)+","+appWidgetsList.get(i).category;
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
				System.out.println("Geo reverse coding is having no results..so trying again..");
				if(mLocationIdentifier.isSearchingLocation() == false )
				{
					mLocationIdentifier.getLocation();
				}
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

				if(appWidgetsList.size() > 0)
				{
					// Size greater than zero 
					for(int i =0;i<appWidgetsList.size();i++)
					{
						//Check if searching is going for each appwidget
						//Already connection please wait 
						if(appWidgetsList.get(i).mConnector.isStarted == false)
						{
				
							RemoteViews view;
							//view.setInt(R.id.widgetlayout, "setBackgroundColor", android.graphics.Color.BLACK);
							if(state)
							{
								view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
					            state = false;
							}
							else
							{
								view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout1);
					            state = true;
							} 
				            
							view.setTextViewText(R.id.title, appWidgetsList.get(i).mConnector.getTitle());
							view.setTextViewText(R.id.address, appWidgetsList.get(i).mConnector.getAddress());
							view.setTextViewText(R.id.phonenumber, appWidgetsList.get(i).mConnector.getPhoneNumbers());
						
							Intent intent = new Intent();
				     		intent.setClass(mContext, OptionsScreen.class);
				     		intent.setAction("com.mani.widgetprodiver");
				     		
				       		Bundle bun = new Bundle();
				       		String result1 = appWidgetsList.get(i).mConnector.result;
			                bun.putString("resultString",result1);
			                int position = appWidgetsList.get(i).mConnector.getCurrentCount();
			                bun.putInt("position", position-1); 
			                intent.putExtras(bun);
			                PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
			                        (position*appWidgetsList.get(i).AppWidgetId) /* no requestCode */, intent,  PendingIntent.FLAG_UPDATE_CURRENT/* no flags */);
			                view.setOnClickPendingIntent(R.id.text, pendingIntent);
			                
							Intent serviceIntent = new Intent(mContext, CellLocationService.class);
							serviceIntent.putExtra("updateAppWidgetId", appWidgetsList.get(i).AppWidgetId);
							serviceIntent.putExtra("appwidgetid", UPDATE_ID);
							
							pendingIntent = PendingIntent.getService(mContext,
			                        position/* no requestCode */, serviceIntent,  PendingIntent.FLAG_UPDATE_CURRENT/* no flags */);
			                
			                //view.setOnClickPendingIntent(R.id.button, pendingIntent);
			            
			                /* To make call options */
			                
			       		    Intent callIntent = new Intent(Intent.ACTION_CALL);
			 		        String numberString ="tel:";
			 		        numberString+=appWidgetsList.get(i).mConnector.getPhoneNumbers();
			 		        callIntent.setData(Uri.parse(numberString));
			                pendingIntent = PendingIntent.getActivity(mContext,
			                        (position*appWidgetsList.get(i).AppWidgetId) /* no requestCode */, callIntent,  PendingIntent.FLAG_UPDATE_CURRENT/* no flags */);
			                view.setOnClickPendingIntent(R.id.call, pendingIntent);
			 		        
			                /* To send message */
			        		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			        		String smsBody= appWidgetsList.get(i).mConnector.getTitle()+","+appWidgetsList.get(i).mConnector.getAddress()+","+appWidgetsList.get(i).mConnector.getPhoneNumbers();
			        		sendIntent.putExtra("sms_body", smsBody); 
			        		sendIntent.setType("vnd.android-dir/mms-sms");
			                pendingIntent = PendingIntent.getActivity(mContext,
			                        (position*appWidgetsList.get(i).AppWidgetId), sendIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
			                view.setOnClickPendingIntent(R.id.message, pendingIntent); 

		                
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
			{
				
				if(looperthreadStarted == false)
				{
					mLooperThreadHandler.post(updateWidgetsRunnable ) ;
					looperthreadStarted = true;
				}
			}
			
		}
		
    private final PhoneStateListener phoneStateListener = new PhoneStateListener(){

    			@Override
                public void onCellLocationChanged(CellLocation location)
                {
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
