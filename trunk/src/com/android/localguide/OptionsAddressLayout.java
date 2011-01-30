package com.android.localguide;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;

public class OptionsAddressLayout extends LinearLayout{

	Context mContext;
	TextView title;
	TextView address;
	final int THRESHHOLD_MOVEMENT = 100;
	MovementIndicator mCallBack;
	boolean isMovementDetected = false;
	int currX;
	int currY;
	
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

		address = new TextView(context);
		address.setTextSize(20);
		address.setTextColor(Color.rgb(0xff, 0xff, 0xff));
		address.setText("address");
		
		LinearLayout.LayoutParams levelParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		LinearLayout.LayoutParams percentageParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		
		title.setLayoutParams(levelParams);
		address.setLayoutParams(percentageParams);
		
		this.setOrientation(VERTICAL);
		this.setPadding(3,3,3,3);
		this.setGravity(Gravity.CENTER_VERTICAL);
		this.addView(title);
		this.addView(address);
		}

	   public void setTitle(String atitle)
	   {
		   title.setText(atitle);
	   }
	   
	   public void setAddress(String aAddress)
	   {
		   address.setText(aAddress);
	   }
	   
	   public void setParent(MovementIndicator aCB)
	   {
		   mCallBack = aCB;
	   }
	   @Override
		public final boolean onTouchEvent(MotionEvent event) {
		   
		   System.out.println("In on touch event address layout");
		   int pointerX = (int) event.getX();
		   		   
			switch(event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				currX = (int) event.getX();
				isMovementDetected = false;
				break;
			case MotionEvent.ACTION_MOVE:
				
				int deltaX = Math.abs(currX - pointerX);
				System.out.println("Action move isss ****** "+currX+"::"+pointerX+":::"+deltaX);
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
