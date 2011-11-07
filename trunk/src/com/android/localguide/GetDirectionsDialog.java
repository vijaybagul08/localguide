package com.android.localguide;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
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
	Button mOK;
	Button mCancel;
	GetDirectionsDialogListener mCB;
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
		mOK = (Button) findViewById(R.id.ok);
		mCancel = (Button) findViewById(R.id.cancel);
		locationTextbox = (EditText)findViewById(R.id.locationtextbox);
		locationTextbox.setText("");
		locationCheckbox =(CheckBox)findViewById(R.id.checkbox);
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
 		
 			mOK.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {

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
	         });

 			mCancel.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	GetDirectionsDialog.this.dismiss();
	            }
	         });

	         
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