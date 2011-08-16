package com.android.localguide;

import android.app.Dialog;
import android.content.Context;
import android.text.Html;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class MapsMarkerDialog extends Dialog  {

	private Context mContext;
	private TextView mTitle;
	private TextView mMessage;
	private int mScreenWidth;
	private Button mOK;
	
	public MapsMarkerDialog(Context context) {
		super(context,R.style.getdirectionsdialog);
		mContext = context;
		Display display = ((WindowManager)mContext.getSystemService(mContext.WINDOW_SERVICE)).getDefaultDisplay();  
		mScreenWidth = display.getWidth();  
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.markerdialog);
		mTitle = (TextView) findViewById(R.id.title);
		mMessage = (TextView) findViewById(R.id.message);
		mOK = (Button) findViewById(R.id.ok);
		mOK.setOnClickListener(new Button.OnClickListener(){
            public void onClick(View v) {
            	MapsMarkerDialog.this.dismiss();
            }
		});
	}
	public void show()
	{
		super.show();
	    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
	    lp.width = mScreenWidth-10;
	    this.getWindow().setAttributes(lp);
    }	
	
	public void setTitle(String title) {
		mTitle.setText(title);
	}
	
	public void setMessage(String message) {
		mMessage.setText(Html.fromHtml(message));
	}
}
