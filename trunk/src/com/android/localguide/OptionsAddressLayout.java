package com.android.localguide;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OptionsAddressLayout extends RelativeLayout{

	Context mContext;
	TextView title;
	TextView address;
	TextView resultcount;
	final int THRESHHOLD_MOVEMENT = 50;
	MovementIndicator mCallBack;
	boolean isMovementDetected = false;
	int currX;
	int currY;
	int totalcount;
	final String FONT_TTF = "quicksand_bold.ttf";
	final String FONT_TTF1 = "quicksand_book.ttf";
	static Typeface mFont;	
	static Typeface mFont1;

	
	public interface MovementIndicator
	{
		public void onMovementDetected(boolean left);
	}
	public OptionsAddressLayout(Context context,AttributeSet attr)
	{
		super(context,attr);
		mContext = context;
		
		title = new TextView(context);
		title.setTextSize(22);
		title.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		title.setText("Title");
		title.setTypeface(getTypeface(mContext,FONT_TTF));
	
		title.setId(4);
		
		address = new TextView(context);
		address.setTextSize(20);
		address.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		address.setText("address");
		address.setId(5);
		address.setTypeface(getTypeface1(mContext,FONT_TTF1));
		
		resultcount = new TextView(context);
		resultcount.setTextSize(20);
		resultcount.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		resultcount.setText(mContext.getString(R.string.result));
		resultcount.setTypeface(getTypeface1(mContext,FONT_TTF1));
		
		RelativeLayout.LayoutParams titleParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams addressParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		RelativeLayout.LayoutParams countParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		title.setLayoutParams(titleParams);
		addressParams.addRule(RelativeLayout.BELOW, title.getId());
		address.setLayoutParams(addressParams);

		countParams.addRule(RelativeLayout.BELOW, address.getId());
		countParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
		resultcount.setLayoutParams(countParams);

		
		this.setPadding(3,3,3,3);
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.addView(title);
		this.addView(address);
		this.addView(resultcount);		
		}
	public static Typeface getTypeface(Context context, String typeface) {
	    if (mFont == null) {
	        mFont = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont;
	}
	public static Typeface getTypeface1(Context context, String typeface) {
	    if (mFont1 == null) {
	        mFont1 = Typeface.createFromAsset(context.getAssets(), typeface);
	    }
	    return mFont1;
	}	
	
	   public void setTitle(String atitle)
	   {
		   title.setText(atitle);
	   }
	   
	   public void setAddress(String aAddress)
	   {
		   address.setText(aAddress);
	   }
	   
	   public void setCurrentPosition(int pos) {
		   resultcount.setText(mContext.getString(R.string.result)+": "+pos+"/"+totalcount);
	   }
	   
	   public void setTotalCount(int count) {
		   totalcount = count;
	   }
	   
	   public void setParent(MovementIndicator aCB)
	   {
		   mCallBack = aCB;
	   }
	   @Override
		public final boolean onTouchEvent(MotionEvent event) {
		   
		   int pointerX = (int) event.getX();
		   		   
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				currX = (int) event.getX();
				isMovementDetected = false;
				break;
			case MotionEvent.ACTION_MOVE:
				
				int deltaX = Math.abs(currX - pointerX);
				if(!isMovementDetected)
				{
				if(deltaX > THRESHHOLD_MOVEMENT)
				{
					isMovementDetected = true;
						if(currX > pointerX )
						{
							mCallBack.onMovementDetected(false);
						}
						else
						{
							mCallBack.onMovementDetected(true);
						}
				}
					
					break;
				}
				break;
			case MotionEvent.ACTION_UP:
				isMovementDetected = false;
				break;
			}
			
			return true;
		}
}
