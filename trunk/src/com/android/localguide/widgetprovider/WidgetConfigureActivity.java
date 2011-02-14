package com.android.localguide.widgetprovider;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.localguide.R;

public class WidgetConfigureActivity extends Activity{

    public static final String PREFS_NAME = "LocalguideWidgetPrefs";
    
    private Integer[] mThumbIds = {   
            R.drawable.beer, R.drawable.hotel,R.drawable.shopping, 
            R.drawable.theatre,R.drawable.train, R.drawable.taxi,   
            R.drawable.gas, R.drawable.police,R.drawable.hospital
            };
    private String[] categories = {   
            "beer", "hotels/Restaurants","shopping", 
            "theatre","train", "taxi",   
            "gas", "police","hospital"
            };
    private int appWidgetId;
    ListView listView;
    Context mContext;
    
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		mContext = this.getApplicationContext();
		setContentView(R.layout.widgetconfigure);
		
		Intent launchIntent = getIntent();
		Bundle extras = launchIntent.getExtras();
		if (extras != null) {
			appWidgetId = extras.getInt(AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);
            Intent cancelResultValue = new Intent();
            cancelResultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
            setResult(RESULT_CANCELED, cancelResultValue);
            
			System.out.println("Appwidget id in Congiure activity is ****** "+appWidgetId);
		}
		
        // If they gave us an intent without the widget id, just bail.
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
        
		listView = (ListView)findViewById(R.id.categories_list);
		listView.setAdapter(new ConfigureListAdapter(this));
	    listView.setOnItemClickListener(new OnItemClickListener() { 
	            public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

	                	//Store the appwidgetid and category in the shared prefs
	                	SharedPreferences prefs = getSharedPreferences(PREFS_NAME,0);
	                    Editor editor = null;;
	                    editor = prefs.edit();
	                    editor.putString("category"+appWidgetId, categories[position]);
	                    editor.commit();
	                    System.out.println("Appwidget id after selecting a entry in list is **** "+appWidgetId);
	                    // tell the app widget manager that we're now configured
	                    Intent resultValue = new Intent();
	                    resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
	                    setResult(RESULT_OK, resultValue);
	                    
	                    Intent serviceIntent = new Intent(mContext, CellLocationService.class);
	                    serviceIntent.putExtra("appwidgetid", appWidgetId);
	                    mContext.startService(serviceIntent);
	                    finish();
	        	}
	        	
	        });
	    System.out.println("Calling finish **************** ");
	    finish();
	    
	}
	
	 /* Call List adapter to display the call icon and phone number along with it in the call list dialog */
	 
	   private  class ConfigureListAdapter extends BaseAdapter {
	        private LayoutInflater mInflater;

	        public ConfigureListAdapter(Context context) {
	        	// Cache the LayoutInflate to avoid asking for a new one each time.
	            mInflater = LayoutInflater.from(context);
	        }

	        public int getCount() {
	            return mThumbIds.length;
	        }
	        public Object getItem(int position) {
	            return position;
	        }

	        public long getItemId(int position) {
	            return position;
	        }

	        public View getView(int position, View convertView, ViewGroup parent) {
	            ViewHolder holder;
	            if (convertView == null) {
	                convertView = mInflater.inflate(R.layout.widgetconfigurelistview, null);
	                AbsListView.LayoutParams params = new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT, 50);
	                convertView.setLayoutParams(params);
	                holder = new ViewHolder();
	                holder.title = (TextView) convertView.findViewById(R.id.categoryText);
	                holder.image = (ImageView) convertView.findViewById(R.id.categoryIcon);
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }
	            holder.title.setText(categories[position]);
	            holder.title.setTextColor(Color.rgb(0xff, 0xff, 0xff));
	            holder.title.setTextSize(22);
	            holder.image.setBackgroundResource(mThumbIds[position]);
	            return convertView;
	        }
	         class ViewHolder {
	            TextView title;
	            ImageView image;
	            }
	    }
}
