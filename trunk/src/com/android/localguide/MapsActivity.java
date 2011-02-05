package com.android.localguide;

import java.util.List;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ZoomControls;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class MapsActivity extends MapActivity{

	private MapController myMapController;

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapsview);
	    MapView mapView = (MapView) findViewById(R.id.mapview);

	    ZoomControls zoomControls = (ZoomControls) mapView.getZoomControls();
	    zoomControls.setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT,
	    LayoutParams.WRAP_CONTENT));

	    mapView.addView(zoomControls);
	    
	    mapView.setBuiltInZoomControls(true);
	   
	    List<Overlay> mapOverlays = mapView.getOverlays();
	    mapOverlays.clear();
	    
	    Drawable drawable = this.getResources().getDrawable(R.drawable.icon);
	    
	    MapsItemizedOverlay itemizedoverlay = new MapsItemizedOverlay(drawable,this);
	   
	    GeoPoint point = new GeoPoint(17375812,78490667);
	    OverlayItem overlayitem = new OverlayItem(point, "Laissez les bon temps rouler!", "I'm in Louisiana!");

	    GeoPoint point2 = new GeoPoint(17385812,78480667);
	    OverlayItem overlayitem2 = new OverlayItem(point2, "Namashkaar!", "I'm in Hyderabad, India!");

	    GeoPoint point3 = new GeoPoint(17365812,78460667);
	    OverlayItem overlayitem3 = new OverlayItem(point3, "Manikandan!","I am Mani");

	    
	    itemizedoverlay.addOverlay(overlayitem);
	    itemizedoverlay.addOverlay(overlayitem2);
	    itemizedoverlay.addOverlay(overlayitem3);
	    
	    mapOverlays.add(itemizedoverlay);
		mapView.invalidate();
	    
		myMapController = mapView.getController();
	    myMapController.animateTo(point2);
	    myMapController.setZoom(13); //Fixed Zoom Level
	}
	
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
}
