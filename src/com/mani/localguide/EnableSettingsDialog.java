package com.mani.localguide;

import com.mani.localguide.R;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class EnableSettingsDialog extends Dialog {
	

		Context mContext;
		Dialog dialog;
		Typeface fontface;
		int screenWidth;
		CheckBox locationCheckbox;
		EditText locationTextbox;
		boolean isLocationChkBoxChecked;
		TextView mTitle;
		TextView mMessage;
		Button mOK;
		Button mCancel;
		static Typeface mFont;	
		final String FONT_TTF = "quicksand_bold.ttf";	

		public EnableSettingsDialog(Context context )
		{

			super(context, R.style.getdirectionsdialog);	
			mContext = context;
			requestWindowFeature(Window.FEATURE_NO_TITLE);
			setContentView(R.layout.settingsdialog);
			Display display = ((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();  
			isLocationChkBoxChecked = true;
			screenWidth = display.getWidth();  
			int height = display.getHeight();
			mTitle = (TextView) findViewById(R.id.title);
			mTitle.setText("Settings");
			this.setCancelable(false);
			mMessage = (TextView) findViewById(R.id.message);
			mMessage.setText("Your GPS is disabled! Would you like to enable it?");
			
			mOK = (Button) findViewById(R.id.ok);
			mCancel = (Button) findViewById(R.id.cancel);
	 		
 			mOK.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	 Intent gpsOptionsIntent = new Intent(  
	                         android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);  
	                 mContext.startActivity(gpsOptionsIntent); 	 
	                 EnableSettingsDialog.this.dismiss();
	            }
	         });

 			mCancel.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	EnableSettingsDialog.this.dismiss();
	            }
	         });
		         
		}
		public static Typeface getTypeface(Context context, String typeface) {
		    if (mFont == null) {
		        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
		    }
		    return mFont;
		}

		
		public void show()
		{
			super.show();
		    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		    lp.width = screenWidth-10;
		    this.getWindow().setAttributes(lp);
	    }		
		    
	}
	