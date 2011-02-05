package com.android.localguide;
import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.drawable.Drawable;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Overlay;

public class MapsItemizedOverlay extends Overlay{

	private ArrayList<OverlayItem> mOverlays = new ArrayList<OverlayItem>();
	private Context mContext;
	private Paint mPaint;
	private Path mPath;
	private Point mPoint1;
	private Point mPoint2;
	ArrayList<GeoPoint> mGeoPoints;
	
	public MapsItemizedOverlay(Drawable defaultMarker, Context context)
	 {
	 mGeoPoints = new ArrayList<GeoPoint>();
     mPath = new Path();
     mPoint1 = new Point();
     mPoint2 = new Point();
     mContext = context;
	 }

	 public void addOverlay(OverlayItem overlay)
	 {
	 mOverlays.add(overlay);
	 mGeoPoints.add(overlay.getPoint());
	 }
	 
	 protected OverlayItem createItem(int i)
	 {
	 return mOverlays.get(i);
	 }
	 
	 public int size()
	 {
	 return mOverlays.size();
	 }
	 
	 protected boolean onTap(int index)
	 {
	 OverlayItem item = mOverlays.get(index);
	 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	 dialog.setTitle(item.getTitle());
	 dialog.setMessage(item.getSnippet());
	 dialog.show();
	 return true;
	 }

	 public boolean draw(Canvas canvas, MapView mapv, boolean shadow,long when){
	        super.draw(canvas, mapv, shadow);
	        System.out.println("on Draw of overlay ****");
	        mPaint = new Paint();
	        mPaint.setDither(true);
	        mPaint.setColor(Color.RED);
	        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
	        mPaint.setStrokeJoin(Paint.Join.ROUND);
	        mPaint.setStrokeCap(Paint.Cap.ROUND);
	        mPaint.setStrokeWidth(2);


	        for(int i=0;i<mGeoPoints.size()-1;i++)
	        {
	        	
	        	mapv.getProjection().toPixels(mGeoPoints.get(i), mPoint1);
	        	mapv.getProjection().toPixels(mGeoPoints.get(i+1), mPoint2);
		        mPath.moveTo(mPoint2.x, mPoint2.y);
		        mPath.lineTo(mPoint1.x,mPoint1.y);
	
		        canvas.drawPath(mPath, mPaint);
	        }
	        return false;
	    }
}

