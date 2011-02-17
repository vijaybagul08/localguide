package com.android.localguide;

import java.util.ArrayList;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
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
	
	ArrayList<GeoPoint> mGeoPoints;
	
	interface TrackBallFocusListener{
		public void focused(int itemIndex);
	}
	ArrayList<OverlayItem> overlays = new ArrayList<OverlayItem>();
	int focusCounter = 0;
	
	public CustomItemizedOverlay(Drawable defaultMarker,Context context) {
		super(boundCenterBottom(defaultMarker));
		mContext = context;
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

	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		Log.i(TAG, "onTap: "+ p.toString());
		return super.onTap(p, mapView);
	}

	 protected boolean onTap(int index)
	 {
	 OverlayItem item = overlays.get(index);
	 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
	 dialog.setTitle(item.getTitle());
	 dialog.setMessage(item.getSnippet());
	 dialog.show();
	 return true;
	 }
//	@Override
//	public boolean onTrackballEvent(MotionEvent event, MapView mapView) {
//		// TODO Auto-generated method stub
//		Log.i(TAG, "onTrackballEvent");
//		int x = (int)event.getRawX();
//		int y = (int)event.getRawY();
//		Log.i(TAG, "Clicked on ["+event.getRawX() +", "+ event.getRawY()+"]");
//		// move through markers
//		focusCounter++;
//		if(focusCounter >= overlays.size())
//			focusCounter = 0;
////		OverlayItem item = overlays.get(index);
////		setFocus(overlays.get(focusCounter));
//		listener.focused(focusCounter);
//		return false;
//	}

	public void onFocusChanged(ItemizedOverlay overlay, OverlayItem newFocus) {
		Log.i(TAG, "onFocusChanged: ");
		if(newFocus != null)
			Log.i(TAG, "Focused Marker: "+ newFocus.getTitle());
	}

	// Start- Changes required to draw lines connecting markers
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean flag) {
		super.draw(canvas, mapView, flag);
		Log.i(TAG, "ItemizedOverlay- Draw....");
		Projection projection = mapView.getProjection();
		
		float x = 0;
		float y = 0;
		Paint paint = new Paint();
		paint.setColor(Color.RED);
		paint.setStyle(Paint.Style.FILL_AND_STROKE);
		
		for(int i=0; i<overlays.size(); i++){
			OverlayItem item = overlays.get(i);
			Point p = projection.toPixels(item.getPoint(), null);
			Log.i(TAG, "Point..."+ p.x +" "+ p.y);
			if(i == 0){
				x = p.x;
				y = p.y;
			}
			else{
				canvas.drawLine(x, y, p.x, p.y, paint);
				x = p.x;
				y = p.y;
			}
		}
	}
	// End- Changes required to draw lines connecting markers
}
