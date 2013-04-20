package com.mywork.place;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.BitmapDrawable;
import android.text.TextPaint;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.Toast;
 
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.OverlayItem;
import com.mywork.ui.R;
import com.samtube405.track.CircleOverlay;
import com.samtube405.track.CustomizedOverlayItem;
 
/**
 * Class used to place marker or any overlay items on Map
 * */
public class AddItemizedOverlay extends BalloonItemizedOverlay<CustomizedOverlayItem> {
 
       public ArrayList<CustomizedOverlayItem> mapOverlays = new ArrayList<CustomizedOverlayItem>();
 
       private Context context;
       
       public PlacesMapActivity a;      
       
       private int markerHeight;     
       
       public MapView mapView;
       
       protected BalloonLayout balloonView;
       
       private static final int FONT_SIZE=12;
       private static final int TITLE_MARGIN=3;
       
       public AddItemizedOverlay(Context context,MapView mapView) {   
    	   
           super(context,mapView);
           
           this.mapView=mapView;
           
           this.context=context;           
           
      }            
 
       public AddItemizedOverlay(Context context,
   			MapView mapView, CurrentTrip cur_trip) {
    	   super(context,mapView,cur_trip);
           
           this.mapView=mapView;          
           
           this.context=context;
   	}
       
      
       
       /*void toggleHeart() {
    	      CustomItem focus=getFocus();
    	      
    	      if (focus!=null) {
    	        focus.toggleHeart();
    	      }
    	      
    	      map.invalidate();
    	    }
    	    
    	    private Drawable getMarker(int resource) {
    	      Drawable marker=getResources().getDrawable(resource);
    	      
    	      marker.setBounds(0, 0, marker.getIntrinsicWidth(),
    	                        marker.getIntrinsicHeight());
    	      boundCenter(marker);

    	      return(marker);
    	    }*/
    	  
	
	





	@Override
       public boolean onTouchEvent(MotionEvent event, MapView mapView)
       {   
 
           if (event.getAction() == 1) {
               GeoPoint geopoint = mapView.getProjection().fromPixels(
                   (int) event.getX(),
                   (int) event.getY());
               // latitude
               double lat = geopoint.getLatitudeE6() / 1E6;
               // longitude
               double lon = geopoint.getLongitudeE6() / 1E6;
               //Toast.makeText(context, "Lat: " + lat + ", Lon: "+lon, Toast.LENGTH_SHORT).show();
           }
           return false;
       } 
 
       @Override
       protected CustomizedOverlayItem createItem(int i) {
          return mapOverlays.get(i);
       }
 
       @Override
       public int size() {
          return mapOverlays.size();
       }
 
       @Override
       protected boolean onTap(int index) {   	
    	   
    	 super.onTap(index,mapOverlays);
         
         return true;
       }
 
       public void addOverlay(CustomizedOverlayItem overlay) {
    	  boundCenter(overlay.result);
          mapOverlays.add(overlay);
       }
       
       public void addOverlay(OverlayItem overlay) {
           mapOverlays.add((CustomizedOverlayItem)overlay);
        }
       
       
 
       public void populateNow(){
           this.populate();
       }
 
    }