package com.android.localguide;

	

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.android.localguide.LocationIdentifier.LocationIdentifierCallBack;


public class WelcomeScreen extends Activity implements LocationIdentifierCallBack {
	    
	public final int CATEGORY_ID =0;
	public final int LOCATION_ID =1;
	public final int CATEGORY_ALERT = 2;
	public final int INTERNET_ALERT = 3;
	public final int LOCATION_ALERT = 4;
	LocalGuideApplication app;
	EditText categoryTextbox;
	EditText locationTextbox;
	Dialog dialog;
	ProgressDialog dialog1;
	String category;
	String location;
	Context mContext;
	Geocoder mReverseGeoCoder;
	LocationIdentifier locationIdentifier;
	boolean isLocationChkBoxChecked = true;
	List<Address> mAddressList = null;
	ArrayList<String> categoryContent;
	public final static int ACTIVITY_INVOKE = 0;
	private Handler mHandler = new Handler();
	
	private Runnable mTask = new Runnable(){
		public void run() {
			checkLocation();
		}
		
	};
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.welcome);
	        
	        app = (LocalGuideApplication) this.getApplication();
	        categoryContent = new ArrayList<String>();
	        
	        categoryContent.add(this.getString(R.string.pubs));
	        categoryContent.add(this.getString(R.string.restuarant));
			categoryContent.add(this.getString(R.string.shopping));
			categoryContent.add(this.getString(R.string.theatre));
			categoryContent.add(this.getString(R.string.train));
			categoryContent.add(this.getString(R.string.taxi));
			categoryContent.add(this.getString(R.string.gas));
			categoryContent.add(this.getString(R.string.police));
			categoryContent.add(this.getString(R.string.hospital));
	        
	        if(app.isLoaded == false) {
	        	app.loadFromDataBase();
	        	app.isLoaded = true;
	        }
	        mContext = this;
	        mReverseGeoCoder = new Geocoder(getApplicationContext());
	        locationIdentifier = new LocationIdentifier(mContext,this);
	         categoryTextbox = (EditText)findViewById(R.id.categotytextbox);
	         locationTextbox = (EditText)findViewById(R.id.locationtextbox);
	         ImageView search = (ImageView)findViewById(R.id.search);
	         
	         CheckBox locationCheckbox =(CheckBox)findViewById(R.id.checkbox);
	        
	         search.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {

                      if(categoryTextbox.getText().toString().length() >0)
                      {
                    	  //Check for location if location checkbox is enabled
                    	  if(isLocationChkBoxChecked == false)
                    	  {
                    		  
                    	     if(locationTextbox.getText().toString().length() > 0)
                    	     { 
		                    	  getLocation();
		                     }
  	                        else
		                     {
		                    	  showDialog(LOCATION_ALERT); 
		                     }
                    	  }
                    	  else
                    	  {
                    		  getLocation();
                    	  }
                      }
                      else
                      {
                    	  showDialog(CATEGORY_ALERT);
                      }
	            }
	         });

       	         
	         locationCheckbox.setOnClickListener(new CheckBox.OnClickListener(){
	        	 public void onClick(View v) {   
                       if(((CheckBox)v).isChecked())
                       {
                    	   isLocationChkBoxChecked = true;
                    	   TextView text1 = (TextView)findViewById(R.id.text1);
                    	   text1.setVisibility(View.GONE);
                    	   locationTextbox.setVisibility(View.GONE);
                       }
                       else
                       { 
	                   	   isLocationChkBoxChecked = false;
	                       TextView text1 = (TextView)findViewById(R.id.text1);
	                	   text1.setVisibility(View.VISIBLE);
	                	   locationTextbox.setVisibility(View.VISIBLE);
                       }
	        	 	}   
	         });
	         
	         Button button = (Button)findViewById(R.id.categories);   
	         button.setOnClickListener(new Button.OnClickListener(){   
	             public void onClick(View v) {   
                         showDialog(CATEGORY_ID);   
	             }   
	         });   
	      }   
	public void onBackPressed ()
	{
		 app.saveToDataBase();
		 this.finish();
	}
	 
	private void getLocation()
	{
		  // Check for internet connection
        if(checkInternetConnection())
        {
      	
	        if(isLocationChkBoxChecked)
	        {
	        	if(locationIdentifier.settingsEnabled() == true) {
		            //showDialog(LOCATION_ID);	                    	  
		            locationIdentifier.getLocation();
	        	}else {
	        		this.settingsDisabled();
	        	}
	        }
	        else
	        {
	      	  // Start the results activity with location from location text box.
	 		 Intent intent = new Intent();
	         intent.putExtra("categoryString", category);
	         location = locationTextbox.getText().toString();
	         Bundle bun = new Bundle();
	         bun.putString("categoryString", category); 
	         bun.putString("locationString", location);
	         intent.putExtras(bun);
	         intent.setClass(mContext, results.class);
	         startActivity(intent);
	      	  
	        }
        }
    	else
        {
      	  showDialog(INTERNET_ALERT); 
        }
           
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
	 
	  private Location mLocation;
	  
	   public void settingsDisabled() {
		   mHandler.post(new Runnable() {
			   public void run() {
					Dialog dialog = new EnableSettingsDialog(mContext);
					dialog.show();
			   }
		   });
	   }
	   
	   public void gotLocation(Location aLocation)
	   {
		   mLocation = aLocation;
		   mHandler.post(mTask);
	   }
	   
	   public void checkLocation() {
		   
		   if(mLocation != null)
		   {
			   //Reverse Geo coding
			   String currlocation=null;
			   try
			   {
			   mAddressList = mReverseGeoCoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
			   if (mAddressList.size()> 0){
				   if(currlocation == null) {
					   Toast.makeText(mContext, this.getString(R.string.no_location), 4000).show();
					   dialog.dismiss();
				   } else {
					   currlocation = mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
		               Toast.makeText(mContext, currlocation, 4000).show();
					   Intent intent = new Intent();
			           intent.putExtra("categoryString", category);
			           location = locationTextbox.getText().toString();
			           intent.putExtra("locationString", location);
			           Bundle bun = new Bundle();
			           bun.putString("categoryString", category); 
			           bun.putString("locationString", currlocation);
			           intent.putExtras(bun);
			           intent.setClass(mContext, results.class);
			           startActivity(intent);
			           dialog.dismiss();
				   }
			   	} else {
				   Toast.makeText(mContext, this.getString(R.string.no_location), 4000).show();
				   dialog.dismiss();
			   	}
			   }
			   catch(Exception e)
			   {
				   Toast.makeText(mContext, this.getString(R.string.reverse_geocoding_error), 4000).show();
				   dialog.dismiss();
			   }
		   }
		   else
		   {
			   Toast.makeText(mContext, this.getString(R.string.no_location_gps), 4000).show();
			   dialog.dismiss();
		   }
	   }
	     protected Dialog onCreateDialog(int id) {   
        	 AlertDialog.Builder builder;   
             Context mContext = this;       	 
             LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
             View layout;
	         switch(id) {   

	         case CATEGORY_ID:   
                 layout  = inflater.inflate(R.layout.categorydialog,(ViewGroup) findViewById(R.id.layout_root));   
                 GridView gridview = (GridView)layout.findViewById(R.id.gridview);   
                 gridview.setAdapter(new ImageAdapter(this));   
	             
	             gridview.setOnItemClickListener(new OnItemClickListener()   
	          			{
	                 public void onItemClick(AdapterView<?> parent, View v,int position, long id) {   
	                     categoryTextbox.setText(categoryContent.get(position));
	                     category=categoryContent.get(position);
	                     dialog.dismiss();
	                 	}   
	             });
	             
	             ImageView close = (ImageView) layout.findViewById(R.id.close);
	             close.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v){
	            		 dialog.dismiss();
	            	}
	             });
	             dialog = new CustomDialog(mContext);
	             dialog.setContentView(layout);
                 return dialog;
	         case LOCATION_ID:
	             dialog = new ErrorDialog(this,this.getString(R.string.no_internet),"Location...",true);
	             return dialog;
	         case INTERNET_ALERT:
	        	 dialog = new ErrorDialog(this,this.getString(R.string.no_internet),this.getString(R.string.enable_internet),false);
	        	 return dialog;
	         case CATEGORY_ALERT:
	        	 dialog = new ErrorDialog(this,this.getString(R.string.no_category),this.getString(R.string.pls_enter_category),false);
	        	 return dialog;
	         case LOCATION_ALERT:
	        	 dialog = new ErrorDialog(this,this.getString(R.string.no_location_entered),this.getString(R.string.pls_enter_location),false);
	        	 return dialog;	        	 
	         default:   
	             dialog = null;   
	         }   
	         return null;   
	     }   
	     
	   public class ImageAdapter extends BaseAdapter {   
	         private Context mContext;
	         private LayoutInflater mInflater;
	         public ImageAdapter(Context c) { 
	        	 mInflater = LayoutInflater.from(c);
	             mContext = c;   
	         }   
	         public int getCount() {   
	             return mThumbIds.length;   
	         }   
	         public Object getItem(int position) {   
	             return null;   
	         }   
	         public long getItemId(int position) {   
	             return 0;   
	         }   
	         // create a new ImageView for each item referenced by the   
	         public View getView(int position, View convertView, ViewGroup parent) {   
	        	 ViewHolder holder;
	             if (convertView == null) {  // if it's not recycled,   
	                  convertView = mInflater.inflate(R.layout.categorycontent, null);
	             	  convertView.setLayoutParams(new GridView.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
	            	  holder = new ViewHolder();
	                  holder.title = (TextView) convertView.findViewById(R.id.categoryText);
	                  holder.icon = (ImageView )convertView.findViewById(R.id.categoryimage);
	                  convertView.setTag(holder);
	              } else {
	                  holder = (ViewHolder) convertView.getTag();
	              }
					holder.icon.setAdjustViewBounds(true);
					holder.icon.setScaleType(ImageView.ScaleType.CENTER_CROP);   
					holder.icon.setPadding(8, 8, 8, 8);
					holder.title.setText(categoryContent.get(position));
					holder.icon.setImageResource(mThumbIds[position]);
					return convertView;   
	         }   
	         class ViewHolder {
	             TextView title;
	             ImageView icon;
	         }
	         // references to our images   
	         private Integer[] mThumbIds = {   
	                 R.drawable.beer, R.drawable.hotel,R.drawable.shopping, 
	                 R.drawable.theatre,R.drawable.train, R.drawable.taxi,   
	                 R.drawable.gas, R.drawable.police,R.drawable.hospital
	                 };
	     	}   

	}
	
		
	    
