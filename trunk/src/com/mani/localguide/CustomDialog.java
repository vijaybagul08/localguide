package com.mani.localguide;

import com.mani.localguide.R;

import android.app.Dialog;
import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.WindowManager;

public class CustomDialog extends Dialog {
	
	private int mScreenWidth;
	private Context mContext;
	public CustomDialog(Context context) {
		super(context,R.style.getdirectionsdialog);
		mContext = context;
		Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();  
		mScreenWidth = display.getWidth();  
	}
	
	public void show()
	{
		super.show();
	    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
	    if(isPortrait())
	    	lp.width = mScreenWidth-10;
	    else
	    	lp.width = mScreenWidth-((mScreenWidth * 20) /100);
	    this.getWindow().setAttributes(lp);
    }	
	
	public boolean isPortrait()
	{
		DisplayMetrics metrics = new DisplayMetrics();
		((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getMetrics(metrics);

		if( metrics.heightPixels > 	metrics.widthPixels )
			return true;
		else
			return false;
	}
}
