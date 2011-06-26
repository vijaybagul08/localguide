package com.android.localguide;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class MapstabActivity extends TabActivity{
	TabHost tabHost;
	private ShowDirectionsList mDirectionsList;
	
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapstab);
	    
	    
	    
	    Resources res = getResources(); // Resource object to get Drawables
	    tabHost = getTabHost();  // The activity TabHost
	    TabHost.TabSpec spec;  // Resusable TabSpec for each tab
	    Intent intent;  // Reusable Intent for each tab

	    Bundle bundle= getIntent().getExtras();
	    mDirectionsList = new ShowDirectionsList(this.getApplicationContext(),bundle);
	    
	    // Create an Intent to launch an Activity for the tab (to be reused)
	    intent = new Intent().setClass(this, MapsActivity.class);
    	intent.putExtras(bundle);
	    // Initialize a TabSpec for each tab and add it to the TabHost
	    spec = tabHost.newTabSpec("Maps").setIndicator("Maps",
	                      res.getDrawable(R.drawable.find))
	                  .setContent(intent);
	    tabHost.addTab(spec);

	    TabSpec ts1 = tabHost.newTabSpec("Directions");
	    ts1.setIndicator("Directions", res.getDrawable(R.drawable.car));
        ts1.setContent(new TabHost.TabContentFactory(){
        	 
            public View createTabContent(String tag)
            {                                                                      

            	return mDirectionsList;
            }              

        });            

   
        tabHost.addTab(ts1);
	    
	    tabHost.setCurrentTab(0);
	    for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
	    	{
	    	       tabHost.getTabWidget().getChildAt(i).setBackgroundDrawable(this.getResources().getDrawable(R.drawable.rowselector));
	    	}
	    //tabHost.getTabWidget().setDividerDrawable(R.color.tab_divider);
	    
	}
 
	public void onTabChanged(String tabId) {
		// TODO Auto-generated method stub
		for(int i=0;i<tabHost.getTabWidget().getChildCount();i++)
		{
		tabHost.getTabWidget().getChildAt(i).setBackgroundColor(Color.parseColor("#7392B5"));
		}

		tabHost.getTabWidget().getChildAt(tabHost.getCurrentTab()).setBackgroundColor(Color.parseColor("#4E4E9C"));
		}
}
