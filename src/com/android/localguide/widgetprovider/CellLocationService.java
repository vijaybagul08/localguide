package com.android.localguide.widgetprovider;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
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
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.RemoteViews;

import com.android.localguide.FavoritesScreen;
import com.android.localguide.LocationIdentifier;
import com.android.localguide.MaintabActivity;
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
	int poolSize = 5;
	int maxPoolSize = 5;
	long keepAliveTime = 10;
	SharedPreferences prefs;
	boolean isLocation_scanning = true;
	Object mlock = new Object();
	
	private final int NO_MATCH = -102;
	private final int DELETE_ID = -100;
	private final int UPDATE_ID = -101;
	private final int TRY_AGAIN_ID = -102;
	
	private final int widgetType4x1 = 1;
	private final int widgetType4x2 = 2;
	TelephonyManager tm;
	private boolean isFirstTimeStarting = false;
	public static final String PREFS_NAME = "LocalguideWidgetPrefs";
	
	class AppWidgetItem {
		CollectDataForCategory mConnector;
		int AppWidgetId;
		String category;
		int appWidgetType;
	}
	
    BroadcastReceiver mNetworkStateIntentReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent.getAction().equals(
                        ConnectivityManager.CONNECTIVITY_ACTION)) {

                if(checkInternetConnection() == true)
                {
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
						startUpdatingWidgetProviders();
					}
	
	                }
                } else if(Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                	//Stop listening for cell change listener.
                	tm.listen(phoneStateListener, 0);
                	
                } else if (Intent.ACTION_SCREEN_OFF.equals(intent.getAction())) {
                	// Start listening for cell change listener
                	int events = PhoneStateListener.LISTEN_CELL_LOCATION;
               		tm.listen(phoneStateListener, events);
                }
            }
        };

   
	public void onCreate()
	{

		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mLocationIdentifier = new LocationIdentifier (this.getApplicationContext(),this);
		tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int events = PhoneStateListener.LISTEN_CELL_LOCATION ;
        tm.listen(phoneStateListener, events);
        mReverseGeoCoder = new Geocoder(getApplicationContext());
        mContext = this.getApplicationContext();
        appWidgetsList = new ArrayList<AppWidgetItem>();
        pendingAppWidgetsList = new ArrayList<AppWidgetItem>();
        prefs = getApplicationContext().getSharedPreferences(WidgetConfigureActivity.PREFS_NAME,0);
        isFirstTimeStarting = true;
        //Create a pool of 4 threads to communicate to the cloud to fetch local search results.
        executor= Executors.newFixedThreadPool(5);
        
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
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        filter.addAction(Intent.ACTION_SCREEN_ON);
        mContext.registerReceiver(mNetworkStateIntentReceiver, filter);

	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		
		if(intent !=null)
		{
			// appWidget id is set to zero, it means, the intent is triggered to delete a appWidget instance from the list.
			if (intent.getIntExtra("appwidgetid", NO_MATCH) == UPDATE_ID)
			{
				
				int updateAppId = intent.getIntExtra("updateAppWidgetId", 0);
				int position = intent.getIntExtra("position", 0);

				int i=0;
				for(i=0;i<appWidgetsList.size();i++)
				{
					if(updateAppId == appWidgetsList.get(i).AppWidgetId)
					{
						if(appWidgetsList.get(i).mConnector.isLocation_scanning == true) {
							appWidgetsList.get(i).mConnector.isLocation_scanning =false;
							udateMovmentDetection(i,position,false);
						}else {
							appWidgetsList.get(i).mConnector.isLocation_scanning = true;
							udateMovmentDetection(i,position,true);
						}
						break;
					}
				}

			}
			else if (intent.getIntExtra("appwidgetid", NO_MATCH) == TRY_AGAIN_ID)
			{
				
				int AppId = intent.getIntExtra("AppWidgetId", 0);
				int i=0;
				for(i=0;i<appWidgetsList.size();i++)
				{
					if(AppId == appWidgetsList.get(i).AppWidgetId)
					{
						if(appWidgetsList.get(i).mConnector.isLocation_scanning == true) {
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
					   		else  {
					   			String text = mContext.getString(R.string.no_internet)+"."+mContext.getString(R.string.enable_internet);
					   			view.setTextViewText(R.id.title, text);
					   		}
							mAppWidgetManager.updateAppWidget(AppId, view);
							break;
						}
					}
				}

			}
			
			else if(intent.getIntExtra("appwidgetid", NO_MATCH) == DELETE_ID)
			{
				synchronized(mlock) {
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
				if(appWidgetsList.size() == 0)
					mLocationIdentifier.stopRequest();
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
		   		else {
		   			String text = mContext.getString(R.string.no_internet)+"."+mContext.getString(R.string.enable_internet);
		   			view.setTextViewText(R.id.title, text);
		   		}
				
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
					item.appWidgetType = prefs.getInt("appwidgettype"+i, 1);
					appWidgetsList.add(item);
					}
				}
				
		   		AppWidgetItem item = new AppWidgetItem();
				item.AppWidgetId = intent.getIntExtra("appwidgetid", 0); 
				item.appWidgetType = intent.getIntExtra("appwidgettype", 1);
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
	String mLocation;
	public void gotLocation(Location location)
	{
		if(location !=null) {
		
		try{
			
			// Use the rever Geo coder to conver lat and long to a valid location string.
			List<Address> mAddressList = mReverseGeoCoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
		   
			if (mAddressList.size()> 0){
			
					String  currlocation = mContext.getString(R.string.location)+mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
					currlocation+="\n";
					currlocation+=mContext.getString(R.string.loading_results);

					RemoteViews	view= new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout3);
					RemoteViews view1= new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout5);


					mLocation = mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
					for(int i =0;i<appWidgetsList.size();i++)
					{
						if(appWidgetsList.get(i).mConnector.isLocation_scanning == true) {
							
							if(appWidgetsList.get(i).appWidgetType == 1) {
								view1.setTextViewText(R.id.title, currlocation);
								mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view1); 
							}else {
								view.setTextViewText(R.id.title, currlocation);
								mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
							}
							
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
			  }
			else
			{
				String  text = mContext.getString(R.string.no_location);
				RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout4);
				view.setTextViewText(R.id.title, text);
				
				text = mContext.getString(R.string.no_location_gps_4x1);
				RemoteViews view1 = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout6);
				view1.setTextViewText(R.id.title, text);			
				for(int i =0;i<appWidgetsList.size();i++)
				{
					if(appWidgetsList.get(i).mConnector.isLocation_scanning == true) {

						Intent serviceIntent = new Intent(mContext, CellLocationService.class);
						serviceIntent.putExtra("AppWidgetId", appWidgetsList.get(i).AppWidgetId);
						serviceIntent.putExtra("appwidgetid", TRY_AGAIN_ID);
						
						PendingIntent pendingIntent = PendingIntent.getService(mContext,
								appWidgetsList.get(i).AppWidgetId/* no requestCode */, serviceIntent,  PendingIntent.FLAG_UPDATE_CURRENT/* no flags */);
		                
		                view.setOnClickPendingIntent(R.id.try_again, pendingIntent);
		                view1.setOnClickPendingIntent(R.id.try_again, pendingIntent);
		                if(appWidgetsList.get(i).appWidgetType == 1) {
		                	mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view1);
		                }else {
		                	mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
		                }
					}
				}
			}
			
		   }
		   catch(Exception e)
		   {
				String  text = mContext.getString(R.string.reverse_geocoding_error);
				RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout4);
				view.setTextViewText(R.id.title, text);
				
				text = mContext.getString(R.string.no_location_gps_4x1);
				RemoteViews view1 = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout6);
				view1.setTextViewText(R.id.title, text);			
				for(int i =0;i<appWidgetsList.size();i++)
				{
					if(appWidgetsList.get(i).mConnector.isLocation_scanning == true) {

						Intent serviceIntent = new Intent(mContext, CellLocationService.class);
						serviceIntent.putExtra("AppWidgetId", appWidgetsList.get(i).AppWidgetId);
						serviceIntent.putExtra("appwidgetid", TRY_AGAIN_ID);
						
						PendingIntent pendingIntent = PendingIntent.getService(mContext,
								appWidgetsList.get(i).AppWidgetId/* no requestCode */, serviceIntent,  PendingIntent.FLAG_UPDATE_CURRENT/* no flags */);
		                
		                view.setOnClickPendingIntent(R.id.try_again, pendingIntent);
		                view1.setOnClickPendingIntent(R.id.try_again, pendingIntent);
		                if(appWidgetsList.get(i).appWidgetType == 1) {
		                	mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view1);
		                }else {
		                	mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
		                }
					}
				}
			   
		   }
		} else if(location == null){

			String  text = mContext.getString(R.string.no_location_gps);
			RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout4);
			view.setTextViewText(R.id.title, text);
			
			text = mContext.getString(R.string.no_location_gps_4x1);
			RemoteViews view1 = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout6);
			view1.setTextViewText(R.id.title, text);			
			for(int i =0;i<appWidgetsList.size();i++)
			{
				if(appWidgetsList.get(i).mConnector.isLocation_scanning == true) {

					Intent serviceIntent = new Intent(mContext, CellLocationService.class);
					serviceIntent.putExtra("AppWidgetId", appWidgetsList.get(i).AppWidgetId);
					serviceIntent.putExtra("appwidgetid", TRY_AGAIN_ID);
					
					PendingIntent pendingIntent = PendingIntent.getService(mContext,
							appWidgetsList.get(i).AppWidgetId/* no requestCode */, serviceIntent,  PendingIntent.FLAG_UPDATE_CURRENT/* no flags */);
	                
	                view.setOnClickPendingIntent(R.id.try_again, pendingIntent);
	                view1.setOnClickPendingIntent(R.id.try_again, pendingIntent);
	                if(appWidgetsList.get(i).appWidgetType == 1) {
	                	mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view1);
	                }else {
	                	mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
	                }
				}
			}
		}
		
	}
	

	public Runnable updateWidgetsRunnable = new Runnable() {
		
			//int count = 0;
			boolean state = true;
			public void run() {
				synchronized(mlock) {
				if(appWidgetsList.size() > 0)
				{
					// Size greater than zero 
					for(int i =0;i<appWidgetsList.size();i++)
					{
						//Check if searching is going for each appwidget
						//Already connection please wait 
						if(appWidgetsList.get(i).mConnector.isStarted == false )
						{
				
							RemoteViews view = null;
							if(appWidgetsList.get(i).appWidgetType == widgetType4x1 ) {
								view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout4x1);
							} else if(appWidgetsList.get(i).appWidgetType == widgetType4x2) {
								view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
							}
				       		appWidgetsList.get(i).mConnector.incrementCount();
				       		view.setTextViewText(R.id.category, appWidgetsList.get(i).category);
							view.setTextViewText(R.id.title, appWidgetsList.get(i).mConnector.getTitle());
							view.setTextViewText(R.id.address, appWidgetsList.get(i).mConnector.getAddress());
							view.setTextViewText(R.id.phonenumber, appWidgetsList.get(i).mConnector.getPhoneNumbers());
						
							Intent intent = new Intent();
				     		intent.setClass(mContext, OptionsScreen.class);
				     		intent.setAction("com.mani.widgetprodiver");
				     		
				       		Bundle bun = new Bundle();
				       		bun.putStringArrayList("resultString", appWidgetsList.get(i).mConnector.resultsArray);

			                int position = appWidgetsList.get(i).mConnector.getCurrentPosition();
			               
			                bun.putString("location", mLocation);
			                intent.putExtras(bun);
			                intent.putExtra("position", position);
			                PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
			                        (position*appWidgetsList.get(i).AppWidgetId) , intent,  PendingIntent.FLAG_UPDATE_CURRENT);
			                
			                view.setOnClickPendingIntent(R.id.title, pendingIntent);
			                view.setOnClickPendingIntent(R.id.address, pendingIntent);
			                view.setOnClickPendingIntent(R.id.phonenumber, pendingIntent);
			                
							Intent serviceIntent = new Intent(mContext, CellLocationService.class);
							serviceIntent.putExtra("updateAppWidgetId", appWidgetsList.get(i).AppWidgetId);
							serviceIntent.putExtra("appwidgetid", UPDATE_ID);
							serviceIntent.putExtra("position", position);
							pendingIntent = PendingIntent.getService(mContext,
			                        position, serviceIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
					       
							if(appWidgetsList.get(i).mConnector.isLocation_scanning == true)
					        	view.setTextViewText(R.id.setting, "ON");
					        else
					        	view.setTextViewText(R.id.setting, "OFF");
			                
			                view.setOnClickPendingIntent(R.id.setting, pendingIntent);
			                /* To make call options */
			       		    Intent callIntent = new Intent(Intent.ACTION_CALL);
			 		        String numberString ="tel:";
			 		        numberString+=appWidgetsList.get(i).mConnector.getPhoneNumbers();
			 		        callIntent.setData(Uri.parse(numberString));
			                pendingIntent = PendingIntent.getActivity(mContext,
			                        (position*appWidgetsList.get(i).AppWidgetId), callIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
			                view.setOnClickPendingIntent(R.id.call, pendingIntent);
			 		        
			                /* To send message */
			        		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
			        		String smsBody= appWidgetsList.get(i).mConnector.getTitle()+","+appWidgetsList.get(i).mConnector.getAddress()+","+appWidgetsList.get(i).mConnector.getPhoneNumbers();
			        		sendIntent.putExtra("sms_body", smsBody); 
			        		sendIntent.setType("vnd.android-dir/mms-sms");
			                pendingIntent = PendingIntent.getActivity(mContext,
			                        (position*appWidgetsList.get(i).AppWidgetId), sendIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
			                view.setOnClickPendingIntent(R.id.message, pendingIntent); 

			                /* To call favorite screen */
			        		Intent favoriteIntent = new Intent();
			        		favoriteIntent.setClass(mContext, MaintabActivity.class);
			        		bun = new Bundle();
			        		bun.putBoolean("widget_favorite", true);
			        		favoriteIntent.putExtras(bun);
			                pendingIntent = PendingIntent.getActivity(mContext,
			                        0 , favoriteIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
			                view.setOnClickPendingIntent(R.id.favorite, pendingIntent);
		                
  						    mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
  						    
						} else if (appWidgetsList.get(i).mConnector.isNetworkIssue == true) {
							RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout3);
							view.setTextViewText(R.id.title, mContext.getString(R.string.network_issues));
  						    mAppWidgetManager.updateAppWidget(appWidgetsList.get(i).AppWidgetId, view);
						}
					}
					
					mLooperThreadHandler.postDelayed(this, 5000);
				}
				else
					mLooperThreadHandler.postDelayed(this, 10000);
			}
			} //End of lock to avoid crash when delete widget removing all elements from list, and this thread trying to access the elements from list.
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
	
	public void udateMovmentDetection(int position,int count,boolean scanning) {

		RemoteViews view = null;
		if(appWidgetsList.get(position).appWidgetType == widgetType4x1 ) {
			view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout4x1);
		} else if(appWidgetsList.get(position).appWidgetType == widgetType4x2) {
			view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
		}
        		
		view.setTextViewText(R.id.title, appWidgetsList.get(position).mConnector.getTitle());
		view.setTextViewText(R.id.address, appWidgetsList.get(position).mConnector.getAddress());
		view.setTextViewText(R.id.phonenumber, appWidgetsList.get(position).mConnector.getPhoneNumbers());
	
		Intent intent = new Intent();
 		intent.setClass(mContext, OptionsScreen.class);
 		intent.setAction("com.mani.widgetprodiver");
 		
   		Bundle bun = new Bundle();
   		bun.putStringArrayList("resultString", appWidgetsList.get(position).mConnector.resultsArray);
        bun.putString("location", mLocation);
        intent.putExtras(bun);
        intent.putExtra("position", count);
        PendingIntent pendingIntent = PendingIntent.getActivity(mContext,
                (count*appWidgetsList.get(position).AppWidgetId) , intent,  PendingIntent.FLAG_UPDATE_CURRENT);
        
        view.setOnClickPendingIntent(R.id.title, pendingIntent);
        view.setOnClickPendingIntent(R.id.address, pendingIntent);
        view.setOnClickPendingIntent(R.id.phonenumber, pendingIntent);
        
		Intent serviceIntent = new Intent(mContext, CellLocationService.class);
		serviceIntent.putExtra("updateAppWidgetId", appWidgetsList.get(position).AppWidgetId);
		serviceIntent.putExtra("appwidgetid", UPDATE_ID);
		serviceIntent.putExtra("position", position);
		pendingIntent = PendingIntent.getService(mContext,
                position, serviceIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        if(scanning)
        	view.setTextViewText(R.id.setting, "ON");
        else
        	view.setTextViewText(R.id.setting, "OFF");
        
        view.setOnClickPendingIntent(R.id.setting, pendingIntent);
    
        /* To make call options */
	    Intent callIntent = new Intent(Intent.ACTION_CALL);
        String numberString ="tel:";
        numberString+=appWidgetsList.get(position).mConnector.getPhoneNumbers();
        callIntent.setData(Uri.parse(numberString));
        pendingIntent = PendingIntent.getActivity(mContext,
                (position*appWidgetsList.get(position).AppWidgetId), callIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.call, pendingIntent);
	        
        /* To send message */
		Intent sendIntent = new Intent(Intent.ACTION_VIEW);
		String smsBody= appWidgetsList.get(position).mConnector.getTitle()+","+appWidgetsList.get(position).mConnector.getAddress()+","+appWidgetsList.get(position).mConnector.getPhoneNumbers();
		sendIntent.putExtra("sms_body", smsBody); 
		sendIntent.setType("vnd.android-dir/mms-sms");
        pendingIntent = PendingIntent.getActivity(mContext,
                (position*appWidgetsList.get(position).AppWidgetId), sendIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.message, pendingIntent); 

        /* To call favorite screen */
		Intent favoriteIntent = new Intent();
		favoriteIntent.setClass(mContext, FavoritesScreen.class);
        pendingIntent = PendingIntent.getActivity(mContext,
                0 , favoriteIntent,  PendingIntent.FLAG_UPDATE_CURRENT);
        view.setOnClickPendingIntent(R.id.favorite, pendingIntent);
        
		mAppWidgetManager.updateAppWidget(appWidgetsList.get(position).AppWidgetId, view);
		
	}
    private final PhoneStateListener phoneStateListener = new PhoneStateListener(){

    			@Override
                public void onCellLocationChanged(CellLocation location)
                {
                        // Get the current location
                		if(mLocationIdentifier.isSearchingLocation() == false )
                		{
                			boolean itemfound = false;
                			//If any one of the app widget has location scanning set to true, query for location will be
                			// triggered. Else not triggered.
            				for(int i=0;i<appWidgetsList.size();i++)
            				{
            						if(appWidgetsList.get(i).mConnector.isLocation_scanning == true) {
            							itemfound = true;
            							break;
            						}
            				}
            				if(itemfound == true)
            					mLocationIdentifier.getLocation();
                		}
                		
                        super.onCellLocationChanged(location);
                }
    };
}
