package com.android.localguide.widgetprovider;

import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.android.localguide.OptionsScreen;
import com.android.localguide.R;

public class WidgetConfigureActivity extends Activity{

	
    public static final String PREFS_NAME = "LocalguideWidgetPrefs";
    public static int count=0;
    private Integer[] mThumbIds = {   
            R.drawable.beer, R.drawable.hotel,R.drawable.shopping, 
            R.drawable.theatre,R.drawable.train, R.drawable.taxi,   
            R.drawable.gas, R.drawable.police,R.drawable.hospital
            };
    private String[] categories = {   
            "beer", "restaurants","shopping", 
            "theatre","train", "taxi",   
            "gas", "police","hospital"
            };
    private int appWidgetId;
    ListView listView;
    Context mContext;
    EditText editText;
    ImageView goButton;
	public void onCreate(Bundle savedInstance)
	{
		super.onCreate(savedInstance);
		mContext = this.getApplicationContext();
		setContentView(R.layout.widgetconfigure);
		editText = (EditText)findViewById(R.id.category);
		goButton = (ImageView)findViewById(R.id.search);
		
		goButton.setOnClickListener(new OnClickListener()
		{
			public void onClick(View v)
			{
				if(editText.getText().length() != 0)
				{
					System.out.println("Edit text text is ******* "+editText.getText().toString()+"::"+appWidgetId);
	            	//Store the appwidgetid and category in the shared prefs
	            	SharedPreferences prefs = getSharedPreferences(PREFS_NAME,0);
	            	int count = prefs.getInt("count", 0);
	                Editor editor = null;
	                editor = prefs.edit();
	                editor.putString("category"+count, editText.getText().toString());
	                editor.putInt("appwidgetid"+count, appWidgetId);
	                count++;
	                editor.putInt("count",count);
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
				else
				{
		    		AlertDialog alertDialog = new AlertDialog.Builder(WidgetConfigureActivity.this).create();
		    		alertDialog.setTitle("Error");
		    		alertDialog.setMessage("Please enter a category and press go");
		    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		    		      public void onClick(DialogInterface dialog, int which) {
		    		 
		    		       //here you can add functions
		    		 
		    		    } });
		    		alertDialog.setIcon(R.drawable.icon);
		    		alertDialog.show();
							
			
				}
			}
			
		});
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
		            	int count = prefs.getInt("count", 0);
		                Editor editor = null;
		                editor = prefs.edit();
		                editor.putString("category"+count, categories[position]);
		                editor.putInt("appwidgetid"+count, appWidgetId);
		                count++;
		                editor.putInt("count",count);
		                editor.commit();
		                System.out.println("Appwidget id after selecting a entry in list is **** "+appWidgetId+"::"+prefs.getInt("appwidgetid0", 0));
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
	                holder = new ViewHolder();
	                holder.title = (TextView) convertView.findViewById(R.id.categoryText);
	                holder.image = (ImageView) convertView.findViewById(R.id.categoryIcon);
	                convertView.setTag(holder);
	            } else {
	                holder = (ViewHolder) convertView.getTag();
	            }
	            holder.title.setText(categories[position]);
	            holder.title.setTextColor(Color.rgb(0xff, 0xff, 0xff));
	            holder.image.setBackgroundResource(mThumbIds[position]);
	            return convertView;
	        }
	         class ViewHolder {
	            TextView title;
	            ImageView image;
	            }
	    }
}
