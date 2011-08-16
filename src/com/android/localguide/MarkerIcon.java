package com.android.localguide;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.MeasureSpec;


  public class MarkerIcon extends View {
		   
		   private Bitmap mMarker;
		   private Bitmap mRequiredMarker;
		   String markers[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
		   Rect  mMarkerRect;
		   Paint mTextPaint;
		   Canvas drawcanvas;
		   
		   public MarkerIcon(Context context,AttributeSet attrs ) {
			   super(context,attrs);
				mMarkerRect = new Rect(0,0,32,32);
				mMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
				mTextPaint = new Paint();
				mTextPaint.setTextSize(18);
				mTextPaint.setAntiAlias(true);
				mTextPaint.setDither(true);
				mTextPaint.setColor(Color.rgb(0x12, 0x10, 0x5E));
			    mRequiredMarker = Bitmap.createBitmap(32,32, Config.ARGB_8888);
			    drawcanvas = new Canvas(mRequiredMarker);
		   }

		   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			   
			    int width = MeasureSpec.getSize(widthMeasureSpec);	
			    int height = MeasureSpec.getSize(heightMeasureSpec);	
				this.setMeasuredDimension(32, 32);   
		   }
		   
		   public void createMarkerIcon(int mOveryLayItemsCount ) {

				drawcanvas.drawBitmap(mMarker,null,mMarkerRect,mTextPaint);

				if(mOveryLayItemsCount == 8 ||mOveryLayItemsCount == 9 ||mOveryLayItemsCount == 14 ||mOveryLayItemsCount == 15)
					drawcanvas.drawText(markers[mOveryLayItemsCount], 14, 16, mTextPaint);
				else
					drawcanvas.drawText(markers[mOveryLayItemsCount], 12, 16, mTextPaint);
		   }

		   public void onDraw(Canvas canvas ) {
			   canvas.drawBitmap(mRequiredMarker,0,0,mTextPaint);
		   }
		   
}
