package com.android.localguide;

import android.os.Bundle;

public class MapsActivity extends MapsActivity{

	@Override
	public void onCreate(Bundle savedInstanceState) {
	    super.onCreate(savedInstanceState);
	    setContentView(R.layout.mapsview);
	    MapView mapView = (MapView) findViewById(R.id.mapview);
	    mapView.setBuiltInZoomControls(true);
	}
	
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
}
