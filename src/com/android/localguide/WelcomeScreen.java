package com.android.localguide;

	

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;


public class WelcomeScreen extends Activity  {
	    
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
	public final static int ACTIVITY_INVOKE = 0;
	
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.welcome);
	        
	        app = (LocalGuideApplication) this.getApplication();
	        app.loadFromDataBase();
	        
	        mContext = getApplicationContext();
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
		System.out.println("On back key pressed ************* ");
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
            showDialog(LOCATION_ID);	                    	  
            locationIdentifier.getLocation();
        }
        else
        {
      	  // Start the results activity with location from location text box.
 		   Intent intent = new Intent();
         intent.putExtra("categoryString", category);
         location = locationTextbox.getText().toString();
         intent.putExtra("locationString", location);
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
	   public void gotLocation(Location aLocation)
	   {
		   if(aLocation != null)
		   {
			   //Reverse Geo coding
			   String currlocation=null;
			   try
			   {
			   mAddressList = mReverseGeoCoder.getFromLocation(aLocation.getLatitude(), aLocation.getLongitude(), 1);
			   if (mAddressList.size()> 0){
				   currlocation = mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
	               Toast.makeText(mContext, currlocation, 4000).show();
			   }	
			   }
			   catch(Exception e)
			   {
				   
			   }
	
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
	           dialog1.dismiss();
		   }
		   else
		   {
			   Toast.makeText(mContext, "Sorry, Couldnt fetch the current location, due to unavailability of network or GPS provider", 4000).show();
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
	                     categoryTextbox.setText(categoryContent[position]);
	                     category=categoryContent[position];
	                     dialog.dismiss();
	                 	}   
	             });
	             
	             ImageView close = (ImageView) layout.findViewById(R.id.close);
	             close.setOnClickListener(new View.OnClickListener() {
					public void onClick(View v){
	            		 dialog.dismiss();
	            	}
	             });
	             
                 builder = new AlertDialog.Builder(mContext);   
                 builder.setView(layout);   
                 dialog = builder.create();   
                 return dialog;
	             //break;   
	         case LOCATION_ID:
 	        	     dialog1 = new ProgressDialog(this);
	                 dialog1.setMessage("Location...");
	                 dialog1.setIndeterminate(true);
	                 dialog1.setCancelable(true);
	              
	                 layout  = inflater.inflate(R.layout.myprogressdialog,(ViewGroup) findViewById(R.id.layout_root));
	                 
	                 TextView text = (TextView)layout.findViewById(R.id.text);
	                 text.setText("Location");
	                 text.setTextSize(25);
	                 
	                 Spinner spinner = (Spinner)layout.findViewById(R.id.spinner);
	                 LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(30, 30, 0.8f);
	                 spinner.setLayoutParams(params);
	                 spinner.start();
	                 
	                 builder = new AlertDialog.Builder(mContext);   
	                 builder.setView(layout);   
	                 dialog = builder.create(); 	
	                 dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	                 
	                 return dialog1;
	        	// break;
	         case INTERNET_ALERT:
	        	 builder =  new AlertDialog.Builder(this);
	        	 builder.setMessage("Please enable the internet connection");
	        	 builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int id) {
	                     //
	                }
	            });
	        	 dialog = builder.create();
	        	 return dialog;
	         case CATEGORY_ALERT:
	        	 builder =  new AlertDialog.Builder(this);
	        	 builder.setMessage("Please enter a category");
	        	 builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int id) {
	                     //
	                }
	            });
	        	 dialog = builder.create();
	        	 return dialog;
	         case LOCATION_ALERT:
	        	 builder =  new AlertDialog.Builder(this);
	        	 builder.setMessage("Please enter a location");
	        	 builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	                 public void onClick(DialogInterface dialog, int id) {
	                     //
	                }
	            });
	        	 dialog = builder.create();
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
	             	  convertView.setLayoutParams(new GridView.LayoutParams(90, 90));
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
					holder.title.setText(categoryContent[position]);
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
       		private String[] categoryContent = {   
               "Pubs", "Restuarants","shopping", 
               "theatre","train", "taxi",   
               "gas", "police","hospital"
               };   
	}
	
		
	    
