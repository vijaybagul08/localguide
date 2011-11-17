package com.android.localguide;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.CheckBox;
import android.app.AlertDialog;
import android.content.DialogInterface;

public class GetDirectionsDialog extends Dialog implements OnClickListener {

    interface GetDirectionsDialogListener {
    	public void onButtonOkPressed(boolean isCurrentLocation,String location);
    }

	Context mContext;
	Dialog dialog;
	Typeface fontface;
	int screenWidth;
	CheckBox locationCheckbox;
	EditText locationTextbox;
	boolean isLocationChkBoxChecked;
	TextView mTitle;
	TextView mMessage;
	TextView mLocation;
	Button mOK;
	Button mCancel;
	GetDirectionsDialogListener mCB;
	static Typeface mFont;	
	final String FONT_TTF = "quicksand_bold.ttf";	

	
	public GetDirectionsDialog(Context context,GetDirectionsDialogListener aCB )
	{

		super(context, R.style.getdirectionsdialog);	
		mCB = aCB;
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.maps_options_dialog);
		Display display = ((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();  
		isLocationChkBoxChecked = true;
		screenWidth = display.getWidth();  
		int height = display.getHeight();
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setTypeface(getTypeface(context,FONT_TTF));
		mMessage= (TextView) findViewById(R.id.message);
		mMessage.setTypeface(getTypeface(context,FONT_TTF));

		mLocation = (TextView) findViewById(R.id.text1);
		mLocation.setTypeface(getTypeface(context,FONT_TTF));

		mOK = (Button) findViewById(R.id.ok);
		mOK.setTypeface(getTypeface(context,FONT_TTF));
		mCancel = (Button) findViewById(R.id.cancel);
		mCancel.setTypeface(getTypeface(context,FONT_TTF));
		locationTextbox = (EditText)findViewById(R.id.locationtextbox);
		locationTextbox.setTypeface(getTypeface(context,FONT_TTF));
		locationTextbox.setText("");
		locationTextbox.setTypeface(getTypeface(context,FONT_TTF));
		
		locationCheckbox =(CheckBox)findViewById(R.id.checkbox);
		locationCheckbox.setTypeface(getTypeface(context,FONT_TTF));

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
		
        locationTextbox.setImeOptions(EditorInfo.IME_ACTION_DONE);
        locationTextbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_DONE) {
					getDirections();
		            return true;
		        }
				// TODO Auto-generated method stub
				return false;
			}
		});

 			mOK.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	getDirections();
	            }
	         });

 			mCancel.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	GetDirectionsDialog.this.dismiss();
	            }
	         });

	         
		}
	
	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
	
		public void getDirections() {
			if(locationCheckbox.isChecked() == true)
			{
				mCB.onButtonOkPressed(true,null);
				GetDirectionsDialog.this.dismiss();
			}
			else
			{
				if(locationTextbox.getText().toString().length() > 0)
				{
					mCB.onButtonOkPressed(false,locationTextbox.getText().toString());
					GetDirectionsDialog.this.dismiss();
				}
				else
				{
		    		AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
		    		alertDialog.setTitle("Error");
		    		alertDialog.setMessage("Please enter a valid start location");
		    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
		    		      public void onClick(DialogInterface dialog, int which) {
		    		 
		    		       //here you can add functions
		    		 
		    		    } });
		    		alertDialog.setIcon(R.drawable.icon);
		    		alertDialog.show();
				}
			}
			
			
		}
		public void show()
		{
			super.show();
		    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		    lp.width = screenWidth-10;
		    this.getWindow().setAttributes(lp);
	    }		
	    
		public void onClick(View v) {
		
			if(v == mCancel )
				this.dismiss();
			else if(v == mOK)
			{
				if(locationCheckbox.isChecked() == true)
					mCB.onButtonOkPressed(true,null);
				else
				{
					if(locationTextbox.getText().toString().length() > 0)
						mCB.onButtonOkPressed(false,locationTextbox.getText().toString());
					else
					{
			    		AlertDialog alertDialog = new AlertDialog.Builder(mContext).create();
			    		alertDialog.setTitle("Error");
			    		alertDialog.setMessage("Please enter a valid from location");
			    		alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
			    		      public void onClick(DialogInterface dialog, int which) {
			    		 
			    		       //here you can add functions
			    		 
			    		    } });
			    		alertDialog.setIcon(R.drawable.icon);
			    		alertDialog.show();
					}
				}
			}
		}
}