package com.android.localguide;

	

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
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


public class WelcomeScreen extends Activity implements LocationIdentifierCallBack,CategoryListDialog.onCategoryItemSelected {
	    
	public final int CATEGORY_ALERT = 12;
	public final int INTERNET_ALERT = 13;
	public final int LOCATION_ALERT = 14;
	final String FONT_TTF = "quicksand_bold.ttf";
	LocalGuideApplication app;
	TextView mCategory;
	TextView mLocationText;
	CheckBox mLocationCheckBox;
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
	private Handler mHandler = new Handler();
	static Typeface mFont;	
	static Typeface mFont1;
	
	private Runnable mTask = new Runnable(){
		public void run() {
			checkLocation();
		}
	};
	
	public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.welcome);
	        
	        app = (LocalGuideApplication) this.getApplication();
        
	        if(app.isLoaded == false) {
	        	app.loadFromDataBase();
	        	app.isLoaded = true;
	        }
	        mContext = this;
	        mReverseGeoCoder = new Geocoder(getApplicationContext());
	        locationIdentifier = new LocationIdentifier(mContext,this);
	        mCategory = (TextView) findViewById(R.id.category);
	        mLocationText = (TextView) findViewById(R.id.location);
	        mLocationCheckBox = (CheckBox) findViewById(R.id.checkbox);
	        
	        mCategory.setTypeface(getTypeface(this,FONT_TTF));
	        mLocationText.setTypeface(getTypeface(this,FONT_TTF));
	        mLocationCheckBox.setTypeface(getTypeface(this,FONT_TTF));
	        
			categoryTextbox = (EditText)findViewById(R.id.categotytextbox);
			categoryTextbox.setTypeface(getTypeface(this,FONT_TTF));
			categoryTextbox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
			categoryTextbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						performSearch();			            
			            return true;
			        }
					// TODO Auto-generated method stub
					return false;
				}
			});

	         
	         locationTextbox = (EditText)findViewById(R.id.locationtextbox);
	         locationTextbox.setTypeface(getTypeface(this,FONT_TTF));	         
	         locationTextbox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
	         locationTextbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
				
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == EditorInfo.IME_ACTION_SEARCH) {
						performSearch();			            
			            return true;
			        }
					// TODO Auto-generated method stub
					return false;
				}
			});
	         
	         ImageView search = (ImageView)findViewById(R.id.search);
	         
	         CheckBox locationCheckbox =(CheckBox)findViewById(R.id.checkbox);
	        
	         search.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	performSearch();
	            }
	         });

       	         
	         locationCheckbox.setOnClickListener(new CheckBox.OnClickListener(){
	        	 public void onClick(View v) {   
                       if(((CheckBox)v).isChecked())
                       {
                    	   isLocationChkBoxChecked = true;
                    	   TextView text1 = (TextView)findViewById(R.id.location);
                    	   text1.setVisibility(View.GONE);
                    	   locationTextbox.setVisibility(View.GONE);
                       }
                       else
                       { 
	                   	   isLocationChkBoxChecked = false;
	                       TextView text1 = (TextView)findViewById(R.id.location);
	                	   text1.setVisibility(View.VISIBLE);
	                	   locationTextbox.setVisibility(View.VISIBLE);
                       }
	        	 	}   
	         });
	         
	         Button button = (Button)findViewById(R.id.categories);  
		     button.setTypeface(getTypeface(this,FONT_TTF));

	         button.setOnClickListener(new Button.OnClickListener(){   
	             public void onClick(View v) {   
                        // showDialog(CATEGORY_ID);
	            	  new CategoryListDialog(WelcomeScreen.this,WelcomeScreen.this).show();
	             }   
	         });   
	      }   
	
	public void performSearch() {
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

	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
	public static Typeface getTypeface1(Context context, String typeface) {
	    if (mFont1 == null) {
	        mFont1 = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont1;
	}	
	public void onBackPressed ()
	{
		 app.saveToDataBase();
		 locationIdentifier.stopRequest();
		 this.finish();
	}
	
	public void onDestroy() {
		super.onDestroy();
		locationIdentifier.stopRequest();
	}
	
	public void onItemSelected(String acategory){
        categoryTextbox.setText(acategory);
        category = acategory;
	}
	
	private Dialog mFindDialog;
	private void getLocation()
	{
		  // Check for internet connection
        if(checkInternetConnection())
        {
	        if(isLocationChkBoxChecked)
	        {
	        	if(locationIdentifier.settingsEnabled() == true) {
		            //showDialog(LOCATION_ID);
	        		mFindDialog = new ErrorDialog(this,"",this.getString(R.string.find_location),true);
	        		mFindDialog.show();
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
		   
		   LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		   View layout;
		   
		   if(mLocation != null)
		   {
			   //Reverse Geo coding
			   String currlocation=null;
			   try
			   {
			   mAddressList = mReverseGeoCoder.getFromLocation(mLocation.getLatitude(), mLocation.getLongitude(), 1);
			   if (mAddressList.size()> 0){
				   currlocation = mAddressList.get(0).getCountryName()+","+mAddressList.get(0).getAddressLine(0);
				   if(currlocation == null) {
				   	   layout = inflater.inflate(R.layout.custom_toast, null);
		               TextView message = (TextView)layout.findViewById(R.id.message);
			           message.setTypeface(getTypeface(this,FONT_TTF));
			           message.setText(this.getString(R.string.no_location));

			           Toast toastView = new Toast(this);
			           toastView.setView(layout);
			           toastView.setDuration(Toast.LENGTH_LONG);
			           toastView.setGravity(Gravity.BOTTOM, 0,0);
			           toastView.show();
			           mFindDialog.dismiss();
				   } else {
				   	   layout = inflater.inflate(R.layout.custom_toast, null);
		               TextView message = (TextView)layout.findViewById(R.id.message);
			           message.setTypeface(getTypeface(this,FONT_TTF));
			           message.setText(currlocation);
			           ImageView info = (ImageView)layout.findViewById(R.id.warning);
			           info.setImageResource(R.drawable.info_icon);
			           Toast toastView = new Toast(this);
			           toastView.setView(layout);
			           toastView.setDuration(Toast.LENGTH_LONG);
			           toastView.setGravity(Gravity.BOTTOM, 0,0);
			           toastView.show();
					   
			           mFindDialog.dismiss();
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
				   }
			   	} else {
			   	   layout = inflater.inflate(R.layout.custom_toast, null);
	               TextView message = (TextView)layout.findViewById(R.id.message);
		           message.setTypeface(getTypeface(this,FONT_TTF));
		           message.setText(this.getString(R.string.no_location));
		           Toast toastView = new Toast(this);
		           toastView.setView(layout);
		           toastView.setDuration(Toast.LENGTH_LONG);
		           toastView.setGravity(Gravity.CENTER, 0,0);
		           toastView.show();
		           mFindDialog.dismiss();
			   	}
			   }
			   catch(Exception e)
			   {
			   	   layout = inflater.inflate(R.layout.custom_toast, null);
	               TextView message = (TextView)layout.findViewById(R.id.message);
	               message.setText(this.getString(R.string.reverse_geocoding_error));
		           message.setTypeface(getTypeface(this,FONT_TTF));
		           Toast toastView = new Toast(this);
		           toastView.setView(layout);
		           toastView.setDuration(Toast.LENGTH_LONG);
		           toastView.setGravity(Gravity.CENTER, 0,0);
		           toastView.show();
		           mFindDialog.dismiss();
			   }
		   }
		   else
		   {
		   	   layout = inflater.inflate(R.layout.custom_toast, null);
               TextView message = (TextView)layout.findViewById(R.id.message);
               message.setText(this.getString(R.string.no_location_gps));
	           message.setTypeface(getTypeface(this,FONT_TTF));
	           Toast toastView = new Toast(this);
	           toastView.setView(layout);
	           toastView.setDuration(Toast.LENGTH_LONG);
	           toastView.setGravity(Gravity.CENTER, 0,0);
	           toastView.show();			   
	           mFindDialog.dismiss();
		   }
	   }
     protected Dialog onCreateDialog(int id) {   

         switch(id) {   
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

	}
	
		
	    
