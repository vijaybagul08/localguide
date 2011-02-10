package com.android.localguide.widgetprovider;

import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.IBinder;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.ServiceState;
import android.telephony.TelephonyManager;
import android.telephony.gsm.GsmCellLocation;
import android.widget.RemoteViews;

import com.android.localguide.R;

public class CellLocationService extends Service{

	public void onCreate()
	{
		System.out.println("On create my Service ::::::: ");
	      TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
	        int events = PhoneStateListener.LISTEN_CELL_LOCATION ;
	        	        
	        tm.listen(phoneStateListener, events);
	}
	
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		System.out.println("On startcommand my Service ::::::: ");
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
    private final PhoneStateListener phoneStateListener = new PhoneStateListener(){

    			@Override
                public void onCellLocationChanged(CellLocation location)
                {
                        String locationString = location.toString();
                        TelephonyManager tm = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
                        GsmCellLocation loc = (GsmCellLocation) tm.getCellLocation();
                        
                        int cellid = loc.getCid();
                        int lac = loc.getLac();
                        String cellId = "CELL-ID :"+cellid;
                        cellId="LAC: "+lac;
                		RemoteViews view = new RemoteViews(getApplicationContext().getPackageName(),R.layout.widgetlayout);
                		view.setTextViewText(R.id.text, cellId);
                        ComponentName thisWidget = new ComponentName(CellLocationService.this, WidgetProvider.class);
                        AppWidgetManager manager = AppWidgetManager.getInstance(CellLocationService.this);

                        int appWidgetId[] = manager.getAppWidgetIds(thisWidget);
                        for(int i =0;i<appWidgetId.length;i++)
                        {
                        	manager.updateAppWidget(appWidgetId[i], view);
                        }
                        super.onCellLocationChanged(location);
                }

   
    };
}
