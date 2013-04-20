package com.samtube405.track;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;


import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.FloatMath;
import android.widget.Toast;

public class CircleOverlay extends Overlay {

    Context context;
    double mLat;
    double mLon;
    float mRadius;
    GeoPoint geo;
    
    int color=Color.RED;
    int alpha=25;

     public CircleOverlay(Context _context, double _lat, double _lon, float radius ) {
            context = _context;
            mLat = _lat;
            mLon = _lon;
            mRadius = radius;
     }

     public CircleOverlay(Context _context,GeoPoint geoPoint, float radius) {
    	 context = _context;
         geo=geoPoint;
         mRadius = radius;
	}
     
     public CircleOverlay(Context _context,GeoPoint geoPoint, float radius,int color) {
    	 context = _context;
         geo=geoPoint;
         mRadius = radius;
         this.color=color;
         alpha=100;
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
         super.draw(canvas, mapView, shadow); 
         
         if(shadow) return; // Ignore the shadow layer

         Projection projection = mapView.getProjection();

         Point pt = new Point();

         //GeoPoint geo = new GeoPoint((int) (mLat), (int)(mLon));

         projection.toPixels(geo ,pt);
         float circleRadius = projection.metersToEquatorPixels(mRadius) * (1/ FloatMath.cos((float) Math.toRadians(mLat)));

         Paint innerCirclePaint;

         innerCirclePaint = new Paint();
         innerCirclePaint.setColor(color);
         innerCirclePaint.setAlpha(alpha);
         innerCirclePaint.setAntiAlias(true);

         innerCirclePaint.setStyle(Paint.Style.FILL);

         canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, innerCirclePaint);
         
         //canvas.drawL
    }
}
