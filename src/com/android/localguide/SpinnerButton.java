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

public class SpinnerButton extends View implements Runnable{
	
	public interface SpinnerButtonCallback
	{
		void onButtonPress();
	}
	SpinnerButtonCallback mCallBack;
	Bitmap mSpinnerBitmap;
	Paint mButtonPaint;
	Rect mSpinnerRect;
	Rect mButtonRect;
	int mButtonWidth;
	int mButtonHeight;
	int mSpinnerX;
	int mSpinnerPivotX;
	int mSpinnerPivotY;
	int mSpinnerWidth;
	int mSpinnerHeight;
	int mTextX;
	int mTextY;
	boolean mSpinnerVisible;
	Paint mTextPaint;
	Runnable mTask;
	float mAngle=0;
	
	Handler mHandler = new Handler();
	String mButtonText="More";
	
	public SpinnerButton(Context context,AttributeSet attrs)
	{
		super(context,attrs);
		mSpinnerBitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.spinner_white_48);
		mButtonPaint = new Paint();
		mButtonPaint.setColor(Color.rgb(0Xa2,0x21,0x2c));
		
		mTextPaint = new Paint();
	    mTextPaint.setColor(0xFFFFFFFF);
	    mTextPaint.setTextSize(16);
	    mTextPaint.setAntiAlias(true);
	    
	    Display display = ((WindowManager)context.getSystemService(context.WINDOW_SERVICE)).getDefaultDisplay();  
		mButtonWidth = display.getWidth();  
	}
	public void setParent(SpinnerButtonCallback aCB)
	{
		mCallBack = aCB;
	}
	public void setText(String text)
	{
		mButtonText = text;
	}
	public void setTextSize(int size)
	{
		 mTextPaint.setTextSize(size);
	}
	
	public void setButtonColor(int color)
	{
		mButtonPaint.setColor(color);
	}
	public void setButtonTextColor(int color)
	{
		mTextPaint.setColor(color);
	}
	public void start()
	{
		mHandler.post(this);
		mSpinnerVisible = true;
	}

	public void stop()
	{
		mSpinnerVisible = false;
		mHandler.removeCallbacks(this);
		invalidate();
	}
    
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int height =  MeasureSpec.getSize(heightMeasureSpec);
		int width =  MeasureSpec.getSize(widthMeasureSpec);
		mTextX = width/2 -35;
		mTextY = height-10;
		mButtonHeight = height;
		mSpinnerHeight = mSpinnerWidth = mButtonHeight;
		mSpinnerWidth = mSpinnerHeight = mSpinnerWidth -10;
		int padding = 5; 
		mSpinnerPivotX = mSpinnerWidth /2; 
		mSpinnerPivotY = mSpinnerHeight/2;

		mSpinnerX = (mButtonWidth-mButtonHeight);
	    mSpinnerRect = new Rect(0,0,mSpinnerWidth,mSpinnerHeight);
	    mButtonRect = new Rect(0,0,mButtonWidth,mButtonHeight);
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
	public boolean onTouchEvent(MotionEvent e)
	{
		switch(e.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				//Change the background color
				mButtonPaint.setColor(Color.rgb(0Xe4,0xbe,0xbf));
				mCallBack.onButtonPress();
				start();
				break;
			case MotionEvent.ACTION_UP:
				// Bring to button UP color
				mButtonPaint.setColor(Color.rgb(0Xa2,0x21,0x2c));
				
				break;
		}
		invalidate();
		return true;
	}
	@Override
	public void onDraw(Canvas canvas)
	{
		
		canvas.drawRect(mButtonRect, mButtonPaint);
		canvas.drawText("More", mTextX,mTextY, mTextPaint);
		if(mSpinnerVisible == true)
		{
			canvas.save();
			canvas.translate(mSpinnerX,0);
			canvas.rotate(mAngle,mSpinnerPivotX,mSpinnerPivotY);
			canvas.drawBitmap(mSpinnerBitmap, null, mSpinnerRect, null);
			canvas.restore();
		}

	}

}

