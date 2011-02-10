package com.android.localguide.widgetprovider;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;


public class WidgetProvider extends AppWidgetProvider{
	boolean isFirstTimeInstance = true;

	public void onUpdate(Context context, AppWidgetManager appWidgetManager,int[] appWidgetIds) {
		if(isFirstTimeInstance == true)
			context.startService(new Intent(context, CellLocationService.class));
		else
			isFirstTimeInstance = false;
	
	}
	
	public void onDeleted(Context context, int[] appWidgetIds)
	{
		
	}
	
	public void onEnabled(Context context)
	{
		
	}
	
	public void onDisabled(Context context)
	{
		
	}
}
