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
		   private Context mContext;
		   int mMarkersResourceIds[] = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,
					 R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,
					 R.drawable.o,R.drawable.p,R.drawable.q,R.drawable.r,R.drawable.s,R.drawable.t,R.drawable.u,
					 R.drawable.v,R.drawable.w,R.drawable.x,R.drawable.y,R.drawable.z};
		   
		   Rect  mMarkerRect;
		   Paint mTextPaint;
		   Canvas drawcanvas;
		   
		   public MarkerIcon(Context context,AttributeSet attrs ) {
			   super(context,attrs);
				mMarkerRect = new Rect(0,0,32,32);
				mContext = context;
			    mRequiredMarker = Bitmap.createBitmap(32,32, Config.ARGB_8888);
			    drawcanvas = new Canvas(mRequiredMarker);
		   }

		   protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
			   
			    int width = MeasureSpec.getSize(widthMeasureSpec);	
			    int height = MeasureSpec.getSize(heightMeasureSpec);	
				this.setMeasuredDimension(32, 32);   
		   }
		   
		   public void createMarkerIcon(int mOveryLayItemsCount ) {
			    mMarker = BitmapFactory.decodeResource(mContext.getResources(), mMarkersResourceIds[mOveryLayItemsCount]);
				drawcanvas.drawBitmap(mMarker,null,mMarkerRect,mTextPaint);
		   }

		   public void onDraw(Canvas canvas ) {
			   canvas.drawBitmap(mRequiredMarker,0,0,mTextPaint);
		   }
		   
}
