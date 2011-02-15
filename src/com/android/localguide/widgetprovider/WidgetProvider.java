package com.android.localguide.widgetprovider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.android.localguide.R;


public class WidgetProvider extends AppWidgetProvider{
	boolean isFirstTimeInstance = true;
            
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		System.out.println("ON update of widget provider ********* "+appWidgetIds[0]);
	}
	
	public void onReceive(Context context, Intent intent)
	{
		System.out.println("ON receive of widget provider ********* ");
		super.onReceive(context, intent);
	}
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		
	      for (int appWidgetId : appWidgetIds) {
	      System.out.println("ON deleted of widget provider ********* "+appWidgetId);
	      SharedPreferences config = context.getSharedPreferences(WidgetConfigureActivity.PREFS_NAME, 0);
          SharedPreferences.Editor configEditor = config.edit();
          configEditor.remove("category"+appWidgetId);
          configEditor.commit();
		  Intent serviceIntent = new Intent(context, CellLocationService.class);
		  serviceIntent.putExtra("deleteAppWidgetId", appWidgetId);
		  serviceIntent.putExtra("appwidgetid", 0);
		  context.startService(serviceIntent);
      }

      super.onDeleted(context, appWidgetIds);
	}
	
	public void onEnabled(Context context)
	{
		System.out.println("ON Enabled of widget provider ********* ");
		Intent serviceIntent = new Intent(context, CellLocationService.class);
		serviceIntent.putExtra("appwidgetid", 0);
        
	}
	
	public void onDisabled(Context context)
	{
		System.out.println("ON Disabled of widget provider ********* ");
		Intent serviceIntent = new Intent(context, CellLocationService.class);
		context.stopService(serviceIntent);
	}
}
