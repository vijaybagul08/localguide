package com.android.localguide;

import java.util.ArrayList;
import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.ZoomControls;

import com.android.localguide.GetDirectionsList.DirectionItem;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapsActivity extends MapActivity implements GetDirectionsList.SearchResultCallBack{

	private MapController myMapController;
	private final int ROUTES_ID = 1;
	ProgressDialog dialog;
	String startLocation;
	List<Overlay> mapOverlays;
	CustomItemizedOverlay itemizedoverlay;
	private Handler mHandler = new Handler();
	
	private Runnable mTask = new Runnable()
	{
		public void run()
		{
			GetDirectionsList obj = new GetDirectionsList(startLocation,address,MapsActivity.this);
			obj.searchRoutes();
		}
	};
	String address;
	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapsview);
	    
	    Bundle bundle= getIntent().getExtras();
	    address = bundle.getString("currentaddress");
	    startLocation = bundle.getString("location");

	    //Show the dialog
    	showDialog(ROUTES_ID);
    	mHandler.postDelayed(mTask, 2000);
    	
	 }
	
	public void OnSearchCompleted(ArrayList<DirectionItem> list,int code)
	{
		
		if(list == null)
		{
			if(code == GetDirectionsList.SearchResultCallBack.NETWORK_FAILURE) {
				dialog.dismiss();
				AlertDialog.Builder builder =  new AlertDialog.Builder(this);
				builder.setMessage(this.getString(R.string.enable_internet));
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {
	                 MapsActivity.this.finish();
	             	}
				});
				builder.create().show();
			} else if (code == GetDirectionsList.SearchResultCallBack.NO_ROUTE) {
				dialog.dismiss();
				AlertDialog.Builder builder =  new AlertDialog.Builder(this);
				builder.setMessage(this.getString(R.string.no_route));
				builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	             public void onClick(DialogInterface dialog, int id) {
	                 MapsActivity.this.finish();
	             	}
				});
				builder.create().show();
			}
	 	} else {
		dialog.dismiss();
		
		MapView mapView = (MapView) findViewById(R.id.mapview);

	    mapView.setBuiltInZoomControls(true);
	   
	    mapOverlays = mapView.getOverlays();
	    mapOverlays.clear();
	    
	    Drawable drawable = this.getResources().getDrawable(R.drawable.facebook);
	    drawable.setAlpha(0);
	    itemizedoverlay = new CustomItemizedOverlay(drawable,this);
	    GeoPoint point;
	    OverlayItem overlayitem;

	    // Iterate the list and get all the route points, duration, distance, instructoins
	    for(int i =0;i<list.size();i++)
	    {
	    	point = new GeoPoint((int)(list.get(i).latitude * 1E6),(int)(list.get(i).longitude * 1E6));
	    	String infoString;
	    	infoString = this.getString(R.string.distance)+" :"+list.get(i).distance+"\n";
	    	infoString += this.getString(R.string.instruction)+" :"+list.get(i).instructions;
	    	overlayitem = new OverlayItem(point,list.get(i).duration,infoString);
	    	itemizedoverlay.addOverlay(overlayitem);
	    }
	    
	    mapOverlays.add(itemizedoverlay);
		mapView.invalidate();
	    
		myMapController = mapView.getController();
	    myMapController.animateTo(new GeoPoint((int)(list.get(0).latitude * 1E6),(int)(list.get(0).longitude * 1E6)));
	    myMapController.setZoom(17); //Fixed Zoom Level
		}
	}
	
	
	public void moveTo(int item) {
		myMapController.animateTo(itemizedoverlay.getOverlayItem(item).getPoint());		
	}
	
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
		case ROUTES_ID:
		
	     dialog = new ProgressDialog(this);
         dialog.setMessage(this.getString(R.string.find_route));
         dialog.setIndeterminate(true);
         dialog.setCancelable(true);
         return dialog;
         
		}
		return null;
	}
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
}
