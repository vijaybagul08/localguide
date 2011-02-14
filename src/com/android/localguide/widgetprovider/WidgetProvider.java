package com.android.localguide.widgetprovider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;


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
		System.out.println("ON deleted of widget provider ********* ");
	      for (int appWidgetId : appWidgetIds) {
	      SharedPreferences config = context.getSharedPreferences(WidgetConfigureActivity.PREFS_NAME, 0);
          SharedPreferences.Editor configEditor = config.edit();
          configEditor.remove("category"+appWidgetId);
          configEditor.commit();
      }

      super.onDeleted(context, appWidgetIds);
	}
	
	public void onEnabled(Context context)
	{
		System.out.println("ON Enabled of widget provider ********* ");
		
	}
	
	public void onDisabled(Context context)
	{
		System.out.println("ON Disabled of widget provider ********* ");
	}
}
