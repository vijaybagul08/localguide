package com.android.localguide;

import android.app.Dialog;
import android.content.Context;
import android.view.Display;
import android.view.WindowManager;

public class CustomDialog extends Dialog {
	
	private int mScreenWidth;
	
	public CustomDialog(Context context) {
		super(context,R.style.getdirectionsdialog);
		Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();  
		mScreenWidth = display.getWidth();  
	}
	
	public void show()
	{
		super.show();
	    WindowManager.LayoutParams lp = this.getWindow().getAttributes();
	    lp.width = mScreenWidth-10;
	    this.getWindow().setAttributes(lp);
    }	
}
