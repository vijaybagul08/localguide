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

public class ErrorDialog extends Dialog {

 	Context mContext;
	Dialog dialog;
	Typeface fontface;
	int screenWidth;
	Button mOK;
	TextView mTitle;
	TextView mContent;
	
	public ErrorDialog(Context context,String title,String msg)
	{
		super(context, R.style.getdirectionsdialog);	
		mContext = context;
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.error_dialog);

		Display display = ((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();  
		screenWidth = display.getWidth();  

		mTitle = (TextView) findViewById(R.id.title);
		mContent = (TextView) findViewById(R.id.content);

		mTitle.setText(title);
		mContent.setText(msg);
		
		mOK = (Button) findViewById(R.id.ok);
		
		mOK.setOnClickListener(new Button.OnClickListener(){
	            public void onClick(View v) {
	            	ErrorDialog.this.dismiss();
	            }
	         });
		}
	
		public void show()
		{
			super.show();
		    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
		    lp.width = screenWidth-10;
		    this.getWindow().setAttributes(lp);
		    mOK.setWidth((int) (screenWidth*0.7));
	    }		
	    
}