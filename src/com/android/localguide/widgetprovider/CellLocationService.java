package com.android.localguide.widgetprovider;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Location;
import android.os.IBinder;
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
	public void onCreate()
	{
		System.out.println("On create my Service ::::::: ");
		mAppWidgetManager = AppWidgetManager.getInstance(this);
		mLocationIdentifier = new LocationIdentifier (this.getApplicationContext(),this);
		TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        int events = PhoneStateListener.LISTEN_CELL_LOCATION ;
        tm.listen(phoneStateListener, events);
	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("On startcommand my Service ::::::: ");
		
		// Some other widget instance is still waiting for its current location
		if(mLocationIdentifier.isSearchingLocation() == false )
		{
			//mLocationIdentifier.getLocation();
		}
		
		// Update the view with "Finding the location...."
   		RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
		view.setTextViewText(R.id.text, "Finding the location ...");
		System.out.println("Updating the widget id ********* "+intent.getIntExtra("appwidgetid", 0));
		
		mAppWidgetManager.updateAppWidget(intent.getIntExtra("appwidgetid", 0), view);
		
		return START_STICKY;
	}
	
	public IBinder onBind(Intent bundle)
	{
		return null;
	}
	
	public void onDestroy()
	{
		System.out.println("On Destroy my Service ::::::: ");
		
	}
	
	public void gotLocation(Location location)
	{
		if(location !=null)
		System.out.println("Location obtained is ********** "+location.toString());
		//Get list of appWidgetIds
		int[] appWidgetIds;
		appWidgetIds = mAppWidgetManager.getAppWidgetIds(new ComponentName("com.android.localguide","com.android.localguide.widgetprovider.WidgetProvider"));
		
		SharedPreferences prefs = getApplicationContext().getSharedPreferences(WidgetConfigureActivity.PREFS_NAME,0);
				
		for(int i =0;i<appWidgetIds.length;i++)
		{
			System.out.println("Categories are ************"+prefs.getString("category"+appWidgetIds[i], null));
		}
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
                		RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
                		view.setTextViewText(R.id.text, cellId);
                        ComponentName thisWidget = new ComponentName(CellLocationService.this, WidgetProvider.class);
                        AppWidgetManager manager = AppWidgetManager.getInstance(CellLocationService.this);

                        int appWidgetId[] = manager.getAppWidgetIds(thisWidget);
                        for(int i =0;i<appWidgetId.length;i++)
                        {
                        	//manager.updateAppWidget(appWidgetId[i], view);
                        }
                        super.onCellLocationChanged(location);
                }

   
    };
}
