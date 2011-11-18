package com.mani.localguide.widgetprovider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;

import com.mani.localguide.R;


public class WidgetProvider4x1 extends AppWidgetProvider{
	boolean isFirstTimeInstance = true;
	private final int DELETE_ID = -100;
            
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		
	}
	
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		SharedPreferences config = context.getSharedPreferences(WidgetConfigureActivity.PREFS_NAME, 0);
		SharedPreferences.Editor configEditor = config.edit();
 
	      for (int appWidgetId : appWidgetIds) {
		      
		      int count = config.getInt("count", 0);
		      for(int i=0;i<count;i++)
		      {
		    	  System.out.println("Appwidget ids are ********** "+config.getInt("appwidgetid"+i, 0)+"::"+config.getString("category"+count, "null"));
		    	  if(config.getInt("appwidgetid"+i, 0) ==  appWidgetId)
		    	  {
		    		  System.out.println("Removing from the prefs......"+appWidgetId);
		    		  configEditor.remove("category"+count);
		    		  configEditor.remove("appwidgetid"+count);
		    		  configEditor.putInt("count", --count);
		    		  configEditor.commit();
		    		  break;
		    	  }
		      }
		      System.out.println("Appwidget ids count is  ********** "+config.getInt("count", -1));
	          
	          /* Since broadcast receivers cannot bind to a service, we couldn't communicate service
	          	 instance, to intimate that a appWidgetId is deleted.So i found a shortcut, like
	          	 use a intent and set a string, value(int), which takes the appWidget id to be deleted
	          	 and call startService with this intent. In onStartCommand() of service, decide if appWidgetId of 
	          	 "appwidgetid" is set to 0 , then it means, deleteAppWidgetId is added in this intent to get the appWidgetId
	          	 to delete.
	          */
			  Intent serviceIntent = new Intent(context, CellLocationService.class);
			  serviceIntent.putExtra("deleteAppWidgetId", appWidgetId);
			  serviceIntent.putExtra("appwidgetid", DELETE_ID);
			  context.startService(serviceIntent);
      }

      super.onDeleted(context, appWidgetIds);
	}
	
	public void onEnabled(Context context)
	{
		// ON Enabled of widget provider
		// First time a instance of the widget is added to home screen. 
		Intent serviceIntent = new Intent(context, CellLocationService.class);
		serviceIntent.putExtra("appwidgetid", DELETE_ID);
    }
	
	public void onDisabled(Context context)
	{
		//ON Disabled of widget provider i.e the last instance of the widget provider is removed, Then stop the service. 
		
		Intent serviceIntent = new Intent(context, CellLocationService.class);
		context.stopService(serviceIntent);
	}
}
