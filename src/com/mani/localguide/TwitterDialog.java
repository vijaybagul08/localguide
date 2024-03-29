package com.mani.localguide;

import com.mani.localguide.R;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class TwitterDialog extends Dialog  {

    interface TwitterDialogListener {
    	public void onTwitterButtonOkPressed(String msg);
    }

	Context mContext;
	Dialog dialog;
	Typeface fontface;
	int screenWidth;
	CheckBox locationCheckbox;
	EditText locationTextbox;
	boolean isLocationChkBoxChecked;
	TextView mTitle;
	Button mOK;
	Button mCancel;
	TwitterDialogListener mCB;
	static Typeface mFont;	
	final String FONT_TTF = "quicksand_bold.ttf";	
	
	public TwitterDialog(Context context,TwitterDialogListener aCB )
	{

		super(context, R.style.getdirectionsdialog);	
		mCB = aCB;
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.twitterlayout);
		Display display = ((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();  
		isLocationChkBoxChecked = true;
		screenWidth = display.getWidth();  
		int height = display.getHeight();
		mTitle = (TextView) findViewById(R.id.title);
		mTitle.setText("Send Tweet (Less than 140 characters)");
		mTitle.setTypeface(getTypeface(context,FONT_TTF));
		mOK = (Button) findViewById(R.id.ok);
		mCancel = (Button) findViewById(R.id.cancel);
		locationTextbox = (EditText)findViewById(R.id.message_edit);
 		
 			mOK.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	mCB.onTwitterButtonOkPressed(locationTextbox.getText().toString());
	            }
	         });

 			mCancel.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	TwitterDialog.this.dismiss();
	            }
	         });
 	        
 			locationTextbox.setImeOptions(EditorInfo.IME_ACTION_GO);
 	        locationTextbox.setOnEditorActionListener(new TextView.OnEditorActionListener() {
 				
 				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
 					if (actionId == EditorInfo.IME_ACTION_GO) {
 						mCB.onTwitterButtonOkPressed(locationTextbox.getText().toString());
 			            return true;
 			        }
 					// TODO Auto-generated method stub
 					return false;
 				}
 			});

 	       locationTextbox.setTypeface(getTypeface(context,FONT_TTF));
		}
	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
	
	public void setMessage(String msg) {
		locationTextbox.setText(msg);
	}
		public void show()
		{
			super.show();
		    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		    lp.width = screenWidth-10;
		    this.getWindow().setAttributes(lp);
	    }		
	    
}
