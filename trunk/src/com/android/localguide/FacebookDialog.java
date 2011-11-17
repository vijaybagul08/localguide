package com.android.localguide;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Typeface;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;


public class FacebookDialog extends Dialog  {

    interface FacebookDialogListener {
    	public void onFBButtonOkPressed(String msg);
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
	FacebookDialogListener mCB;
	static Typeface mFont;	
	final String FONT_TTF = "quicksand_bold.ttf";	
	public FacebookDialog(Context context,FacebookDialogListener aCB )
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
		mTitle.setTypeface(getTypeface(context,FONT_TTF));

		mTitle.setText("Post to Wall");
		mOK = (Button) findViewById(R.id.ok);
		mCancel = (Button) findViewById(R.id.cancel);
		locationTextbox = (EditText)findViewById(R.id.message_edit);
 		
 			mOK.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	mCB.onFBButtonOkPressed(locationTextbox.getText().toString());
	            }
	         });

 			mCancel.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	FacebookDialog.this.dismiss();
	            }
	         });

 			locationTextbox.setTypeface(getTypeface(context,FONT_TTF));
   
		}
	
	public void setMessage(String msg) {
		locationTextbox.setText(msg);
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
