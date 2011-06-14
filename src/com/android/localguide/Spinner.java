package com.android.localguide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

public class Spinner extends View implements Runnable{
	

	Bitmap mSpinnerBitmap;

	Rect mSpinnerRect;
	Rect mButtonRect;
	int mSpinnerX;
	int mSpinnerPivotX;
	int mSpinnerPivotY;
	int mSpinnerWidth;
	int mSpinnerHeight;
	Runnable mTask;
	float mAngle=0;
	
	Handler mHandler = new Handler();
	
	public Spinner(Context context,AttributeSet attrs)
	{
		super(context,attrs);
		mSpinnerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spinner_white_48);
	}
	public void start()
	{
		mHandler.post(this);

	}

	public void stop()
	{
		mHandler.removeCallbacks(this);
		invalidate();
	}
    
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int height =  MeasureSpec.getSize(heightMeasureSpec);
		int width =  MeasureSpec.getSize(widthMeasureSpec);
		mSpinnerHeight = mSpinnerWidth = width;
		mSpinnerPivotX = mSpinnerPivotY = width /2 ;
	    mSpinnerRect = new Rect(0,0,mSpinnerWidth,mSpinnerHeight);
		setMeasuredDimension(width,height);
	}
	
	public void run()
    {
    	if(mAngle == 360)
    		mAngle=45;
    	else
    		mAngle+=45;
    	invalidate();
    	mHandler.postDelayed(this, 100); 
    }
	
	@Override
	public void onDraw(Canvas canvas)
	{
			canvas.save();
			canvas.rotate(mAngle,mSpinnerPivotX,mSpinnerPivotY);
			canvas.drawBitmap(mSpinnerBitmap, null, mSpinnerRect, null);
			canvas.restore();
	}

}

