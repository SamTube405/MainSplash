package com.mywork.onmaps;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

public class Pinpoint extends ItemizedOverlay<OverlayItem>{
	
	ArrayList<OverlayItem> pinpoints=new ArrayList<OverlayItem>();
	Context c;

	public Pinpoint(Drawable d) {
		super(boundCenterBottom(d));
		// TODO Auto-generated constructor stub
	}
	
	public Pinpoint(Drawable dr,Context con) {
		this(dr);
		c=con;
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
	
	public void insertPoint(OverlayItem o){
		pinpoints.add(o);
		this.populate();
		
	}
	
	

}
