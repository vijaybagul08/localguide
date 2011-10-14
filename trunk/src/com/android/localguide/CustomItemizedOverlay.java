package com.android.localguide;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.google.android.maps.Projection;


public class CustomItemizedOverlay extends ItemizedOverlay<OverlayItem> implements com.google.android.maps.ItemizedOverlay.OnFocusChangeListener {

	String TAG = "CustomItemizedOverlay";
	TrackBallFocusListener listener;
	private Context mContext;
	Bitmap bitmap;
	ArrayList<GeoPoint> mGeoPoints;
	Paint mPaint;
	Paint mTextPaint;
	Rect  mMarkerRect;
	int mOveryLayItemsCount=0;
	int icons[]={ R.drawable.exit,R.drawable.beer,R.drawable.hotel,R.drawable.hotel,R.drawable.hotel,R.drawable.hotel,R.drawable.hotel};
	int mMarkersResourceIds[] = {R.drawable.a,R.drawable.b,R.drawable.c,R.drawable.d,R.drawable.e,R.drawable.f,R.drawable.g,
								 R.drawable.h,R.drawable.i,R.drawable.j,R.drawable.k,R.drawable.l,R.drawable.m,R.drawable.n,
								 R.drawable.o,R.drawable.p,R.drawable.q,R.drawable.r,R.drawable.s,R.drawable.t,R.drawable.u,
								 R.drawable.v,R.drawable.w,R.drawable.x,R.drawable.y,R.drawable.z};
	ArrayList<Bitmap> mMarkerList;
	Bitmap mMarker;
	interface TrackBallFocusListener{
		public void focused(int itemIndex);
	}
	ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	int focusCounter = 0;
	
	public CustomItemizedOverlay(Drawable defaultMarker,Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
		mMarkerList = new ArrayList<Bitmap>();
		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setColor(Color.BLUE);
		mPaint.setStrokeWidth(5f);
		mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		mTextPaint = new Paint();
		mTextPaint.setAntiAlias(true);
		//mTextPaint.setColor(Color.WHITE);
		mTextPaint.setColor(Color.rgb(0x12, 0x10, 0x5E));
		
		mTextPaint.setTextSize(12);
		mTextPaint.setStyle(Paint.Style.FILL_AND_STROKE);

		mMarkerRect = new Rect(0,0,18,32);
		mMarker = BitmapFactory.decodeResource(context.getResources(), R.drawable.marker);
		setOnFocusChangeListener(this);
	}
	
	public CustomItemizedOverlay(Drawable defaultMarker, TrackBallFocusListener listener) {
		// if you don't use boundCenterBottom(), somehow it doesn't show up
		super(boundCenterBottom(defaultMarker));
		setOnFocusChangeListener(this);
		this.listener = listener;
	}

	public Drawable boundToCenterBottom(Drawable d){
		return super.boundCenter(d);
	}
	public void addOverlay(OverlayItem item){
		overlays.add(item);
		createMarkerImage();
		// call populate, which internally call createItem
		populate();
	}
	
	public OverlayItem getOverlayItem(int index){
		return overlays.get(index);
	}
	
	@Override
	protected OverlayItem createItem(int index) {
		// TODO Auto-generated method stub
		return overlays.get(index);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return overlays.size();
	}

	public void createMarkerImage()
	{
		Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), mMarkersResourceIds[mOveryLayItemsCount]);
		mMarkerList.add(bitmap);
		mOveryLayItemsCount++;
		
	}
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		Log.i(TAG, "onTap: "+ p.toString());
		return super.onTap(p, mapView);
	}

	 protected boolean onTap(int index)
	 {
		 OverlayItem item = overlays.get(index);
		 MapsMarkerDialog dialog = new MapsMarkerDialog(mContext);
		 dialog.setTitle(item.getTitle());
		 dialog.setMessage(item.getSnippet());
		 dialog.show();
		 return true;
	 }

	 public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
		Log.i(TAG, "onFocusChanged: ");
		if(newFocus != null)
			Log.i(TAG, "Focused Marker: "+ newFocus.getTitle());
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean flag) {
		super.draw(canvas, mapView, flag);
		Projection projection = mapView.getProjection();
		
		float x = 0;
		float y = 0;
		for(int i=0; i<overlays.size(); i++){
			OverlayItem item = overlays.get(i);
			Point p = projection.toPixels(item.getPoint(), null);
			
			if(i == 0){
				x = p.x;
				y = p.y;
			}
			else{
				canvas.drawLine(x, y, p.x, p.y, mPaint);
				canvas.drawBitmap(mMarkerList.get(i-1), x-23, y-45, mPaint);
				x = p.x;
				y = p.y;
			}
			if(i == (overlays.size()-1)){
				canvas.drawBitmap(mMarkerList.get(i), x-23, y-45, mPaint);
			}
		}
	}

}
