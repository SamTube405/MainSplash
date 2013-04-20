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

public class DrawLine extends Overlay {

    Context context;
    GeoPoint geo1,geo2;
    
   

     public DrawLine(Context _context, GeoPoint pt1,GeoPoint pt2) {
            context = _context;
            geo1=pt1;
            geo2=pt2;
            
     }

     public void draw(Canvas canvas, MapView mapView, boolean shadow) {
         super.draw(canvas, mapView, shadow); 

         if(shadow) return; // Ignore the shadow layer

         Projection projection = mapView.getProjection();

         Point pt1 = new Point();
         
         Point pt2 = new Point();

         //GeoPoint geo1 = new GeoPoint((int) (mLat*1E6), (int)(mLon*1E6));
         
         //GeoPoint geo2 = new GeoPoint((int) ((mLat+1)*1E6), (int)((mLon+1)*1E6));

         projection.toPixels(geo1 ,pt1);
         
         projection.toPixels(geo2 ,pt2);
         //float circleRadius = projection.metersToEquatorPixels(mRadius) * (1/ FloatMath.cos((float) Math.toRadians(mLat)));

         Paint innerCirclePaint;

         innerCirclePaint = new Paint();
         innerCirclePaint.setColor(Color.BLUE);
         innerCirclePaint.setAlpha(100);
         innerCirclePaint.setAntiAlias(true);
         innerCirclePaint.setStyle(Paint.Style.STROKE);
         innerCirclePaint.setStrokeWidth(10);

         //innerCirclePaint.setStyle(Paint.Style.FILL);

         //canvas.drawCircle((float)pt.x, (float)pt.y, circleRadius, innerCirclePaint);
         
         canvas.drawLine((float)pt1.x, (float)pt1.y,(float)pt2.x, (float)pt2.y,innerCirclePaint);
         
        
    }
     
     
}

