package com.samtube405.track;

import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.OverlayItem;
import com.mywork.place.Place;
import com.mywork.place.PlaceList;


public class CustomizedOverlayItem extends OverlayItem{
	boolean is_visitable=false;
	
	boolean me=false;
	
	GeoPoint gp;
	
	public Place place=null;
	
	public Drawable result;
	
	public CustomizedOverlayItem(GeoPoint g,String t,String n) {
		super(g, t, n);		
	}
	
	public CustomizedOverlayItem(GeoPoint g,String t,String n,boolean me) {
		super(g, t, n);
		
		this.me=me;		
		
	}	
	
	public CustomizedOverlayItem(GeoPoint g,String t,String n,boolean me,Drawable result) {
		super(g, t, n);
		
		this.me=me;		
		
		this.result=result;
		
	}	
	
	
	public CustomizedOverlayItem(GeoPoint g,Place place) {
		super(g, place.types[0].toUpperCase(), place.name);
		
		this.place=place;
		
		this.is_visitable=place.is_visitable;
		
		
	}
	
	public CustomizedOverlayItem(GeoPoint g,Place place,Drawable result) {
		super(g, place.types[0].toUpperCase(), place.name);
		
		this.place=place;
		
		this.is_visitable=place.is_visitable;
		
		this.result=result;
		
		
	}
	
	public Place getPlace(){
		return this.place;
	}
	public void setVisitable(boolean f){		
		this.is_visitable=f;	
	}
	
	
	public boolean getVisitFlag(){		
		
		Log.d("Overlay Visitable :", ""+is_visitable);
		
		return is_visitable;
	}
	
	public boolean getMeFlag(){		
		
		//Log.d("Me :", ""+me);
		
		return me;
	}

	@Override
	public Drawable getMarker(int stateBitset) {
		// TODO Auto-generated method stub
		//return super.getMarker(stateBitset);	
		result.setBounds(0, 0, result.getIntrinsicWidth(),
				result.getIntrinsicHeight());
        //boundCenter(result);
	      
	    setState(result, stateBitset);
	    
	    return(result);
	}
	
	

}
