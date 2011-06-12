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
	private Handler mHandler = new Handler();
	
	private Runnable mTask = new Runnable()
	{
		public void run()
		{
					    //GetDirectionsList obj = new GetDirectionsList("Hougang Avenue 8,Singapore",address,MapsActivity.this);
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
	
	public void OnSearchCompleted(ArrayList<DirectionItem> list)
	{
		
		if(list == null)
		{
			dialog.dismiss();
			AlertDialog.Builder builder =  new AlertDialog.Builder(this);
			builder.setMessage("Please enable the internet connection");
			builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
             public void onClick(DialogInterface dialog, int id) {
                 MapsActivity.this.finish();
            }
        });
    	 builder.create().show();
	 
		}
		dialog.dismiss();
		
		System.out.println("On search completed ");
		
		MapView mapView = (MapView) findViewById(R.id.mapview);

	    mapView.setBuiltInZoomControls(true);
	   
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    mapOverlays.clear();
	    
	    Drawable drawable = this.getResources().getDrawable(R.drawable.facebook);
	    drawable.setAlpha(0);
	    CustomItemizedOverlay itemizedoverlay = new CustomItemizedOverlay(drawable,this);
	    GeoPoint point;
	    OverlayItem overlayitem;

	    // Iterate the list and get all the route points, duration, distance, instructoins
	    
	    for(int i =0;i<list.size();i++)
	    {
	    	point = new GeoPoint((int)(list.get(i).latitude * 1E6),(int)(list.get(i).longitude * 1E6));
	    	String infoString;
	    	infoString = "Distance :"+list.get(i).distance+"\n";
	    	infoString += "Instruction :"+list.get(i).instructions;
	    	overlayitem = new OverlayItem(point,list.get(i).duration,infoString);
	    	itemizedoverlay.addOverlay(overlayitem);
	    }
	    
	    mapOverlays.add(itemizedoverlay);
		mapView.invalidate();
	    
		myMapController = mapView.getController();
	    myMapController.animateTo(new GeoPoint((int)(list.get(0).latitude * 1E6),(int)(list.get(0).longitude * 1E6)));
	    myMapController.setZoom(17); //Fixed Zoom Level
	

	}
	
	protected Dialog onCreateDialog(int id)
	{
		switch(id)
		{
		case ROUTES_ID:
		
	     dialog = new ProgressDialog(this);
         dialog.setMessage("Finding the routes...");
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
