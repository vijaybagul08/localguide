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
	String markers[] = {"A","B","C","D","E","F","G","H","I","J","K","L","M","N","O","P","Q","R","S","T","U","V","W","X","Y","Z"};
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
		System.out.println("Add overrlay ***************************** ");
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
		Bitmap bitmap = Bitmap.createBitmap(18,32, Config.ARGB_8888);
		Canvas drawcanvas = new Canvas(bitmap);
		drawcanvas.drawBitmap(mMarker,null,mMarkerRect,mPaint);

		System.out.println("Cmavket image "+";;;"+markers[mOveryLayItemsCount]);
		if(mOveryLayItemsCount == 8 ||mOveryLayItemsCount == 9 ||mOveryLayItemsCount == 14 ||mOveryLayItemsCount == 15)
			drawcanvas.drawText(markers[mOveryLayItemsCount], 8, 16, mTextPaint);
		else
			drawcanvas.drawText(markers[mOveryLayItemsCount], 6, 16, mTextPaint);
		//drawcanvas.drawRect(mMarkerRect, mPaint);
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
	 AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
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

	// Start- Changes required to draw lines connecting markers
	@Override
	public void draw(Canvas canvas, MapView mapView, boolean flag) {
		super.draw(canvas, mapView, flag);
		//Log.i(TAG, "ItemizedOverlay- Draw....");
		Projection projection = mapView.getProjection();
		
		float x = 0;
		float y = 0;
		System.out.println("size value is ** "+overlays.size());
		for(int i=0; i<overlays.size(); i++){
			//Bitmap bitmap = BitmapFactory.decodeResource(mContext.getResources(), R.drawable.marker );//icons[i] );
			OverlayItem item = overlays.get(i);
			Point p = projection.toPixels(item.getPoint(), null);
			
			//Log.i(TAG, "Point..."+ p.x +" "+ p.y);
			if(i == 0){
				System.out.println("I value is ** "+i);
				x = p.x;
				y = p.y;
				canvas.drawBitmap(mMarkerList.get(0), x-9, y-32, mPaint);
			}
			else{
				System.out.println("I value is ** "+i);
				canvas.drawLine(x, y, p.x, p.y, mPaint);
				canvas.drawBitmap(mMarkerList.get(i), x-9, y-32, mPaint);
				x = p.x;
				y = p.y;
			}
		}
	}
	// End- Changes required to draw lines connecting markers
	
}
