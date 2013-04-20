package com.mywork.ui;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;

public class CustomPinpoint extends ItemizedOverlay<OverlayItem>{
	
	private ArrayList<OverlayItem> pinpoints=new ArrayList<OverlayItem>();
	private Context c;

	public CustomPinpoint(Drawable defaultMarker) {
		super(boundCenterBottom(defaultMarker));
		// TODO Auto-generated constructor stub
	}
	
	public CustomPinpoint(Drawable m,Context context) {
		this(m);
		c=context;
		// TODO Auto-generated constructor stub
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return pinpoints.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return pinpoints.size();
	}
	
	public void insertPinpoint(OverlayItem o){
		pinpoints.add(o);
		this.populate();
		
	}

}
