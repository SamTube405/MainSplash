package com.mywork.place;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.text.Html;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.mywork.db.JSONParser;
import com.mywork.ui.R;
import com.samtube405.authenticate.Authentication;
import com.samtube405.track.CustomizedOverlayItem;

/**
 * {@link ItemizedOverlay} which draws balloons over POIs at a {@link MapView}.
 * 
 * @author isaac.salgueiro@discalis.com (based on Jeff Gilfelt's
 *         BalloonItemizedOverlay)
 */
public abstract class BalloonItemizedOverlay<Item> extends ItemizedOverlay<CustomizedOverlayItem> {

	private final static String TAG = "Discalis BIO";
	
	private static final String url = "http://redknot.ckreativity.com/android_connect/add_visit_tour.php";
	
	private static final String TAG_SUCCESS = "success";

	protected final MapView mapView;
	protected BalloonLayout balloonView;
	protected View clickRegion;
	protected final int balloonViewOffset;
	Context context;
	
	 public CurrentTrip cur_trip;
	
	public ArrayList<CustomizedOverlayItem> mapOverlays = new ArrayList<CustomizedOverlayItem>();

	private Authentication tauth;
	
	private JSONParser jsonParser= new JSONParser();

	/**
	 * Get a new instance.
	 * 
	 * @param defaultMarker
	 *            Default drawable that will be drawn for each
	 *            {@link OverlayItem}. <b>Rmember to set bounds with something
	 *            like boundCenterBottom</b>
	 * @param mapView
	 */
	public BalloonItemizedOverlay(Drawable defaultMarker, MapView mapView) {
		super(defaultMarker);
		this.mapView = mapView;
		balloonViewOffset = defaultMarker.getIntrinsicHeight();
		populate();
	}
	
	public BalloonItemizedOverlay(Context context,MapView mapView) {
		super(null);
		this.mapView = mapView;
		this.context=context;
		balloonViewOffset = 1;
		//populate();
	}
	
	public BalloonItemizedOverlay(Context context,MapView mapView,CurrentTrip cur_trip) {
		super(null);
		this.mapView = mapView;
		this.context=context;
		this.cur_trip=cur_trip;
		balloonViewOffset = 1;
		
		tauth=new Authentication(context);
		//populate();
	}

	/**
	 * Override this method to execute code when the user taps a balloon.
	 * 
	 * @param index
	 *            {@link OverlayItem} which balloon was tapped
	 * @return true, we're not propagating the event.
	 */
	protected boolean onBalloonTap(int index) {
		Log.d(TAG, "onBalloonTap not catched!");
		return true;
	}

	/**
	 * 
	 * @see ItemizedOverlay#onTap(GeoPoint, MapView)
	 */
	
	protected boolean onTap(int index,ArrayList<CustomizedOverlayItem> mapOverlays) {
		final int thisIndex = index;
		final CustomizedOverlayItem item = mapOverlays.get(index);
        AlertDialog.Builder dialog = new AlertDialog.Builder(this.context);         
        
        dialog.setTitle(item.getTitle());
        dialog.setMessage(item.getSnippet());
        
        
        dialog.setNegativeButton("OK", new DialogInterface.OnClickListener() {
           public void onClick(DialogInterface dialog, int which) {
           }
       });
        
        if(item.getVisitFlag()){        	
       	 dialog.setPositiveButton("Check-In", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
               	 Log.d("CHECKED IN", "YES");
               	 
               	 new PlaceVisit(thisIndex,item).execute();
               	 
               	 
                }
            });
        }
        dialog.show();

		

		return true;
	}
	
	public void getBalloon(int index,CustomizedOverlayItem item){
		boolean isRecycled = true;
		final int thisIndex = index;
		final GeoPoint point = item.getPoint();

		if (balloonView == null) {
			Log.d(TAG, "New balloonView");
			balloonView = new BalloonLayout(mapView.getContext(), balloonViewOffset);
			clickRegion = (View) balloonView.findViewById(R.id.balloon_inner_layout);
			isRecycled = false;
		}

		//balloonView.setVisibility(View.GONE);

		/*if (mapView.getOverlays().size() > 1) {
			Log.d(TAG, "Hidding balloons from other overlays");
			final List<Overlay> overlays = mapView.getOverlays();
			for (Overlay overlay : overlays) {
				if ((overlay != this) && (overlay instanceof BalloonItemizedOverlay<?>)) {
					((BalloonItemizedOverlay<?>) overlay).hideBalloon();
				}
			}
		}*/

		balloonView.setText("VISITED at "+new SimpleDateFormat("hh:mm a").format(new Date()));
		
		

		MapView.LayoutParams params = new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, point, MapView.LayoutParams.BOTTOM_CENTER);
		params.mode = MapView.LayoutParams.MODE_MAP;

		clickRegion.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View arg0, MotionEvent arg1) {
				if (arg1.getAction() == MotionEvent.ACTION_UP) {
					return onBalloonTap(thisIndex);
				}
				return true;
			}
		});

		balloonView.setVisibility(View.VISIBLE);

		if (isRecycled) {
			balloonView.setLayoutParams(params);
		} else {
			mapView.addView(balloonView, params);
		}

		// TODO config parameter to determine wether we center the map or not
		mapView.getController().animateTo(point);
		
		mapView.invalidate();
	}

	/**
	 * Call this method to hide the current viewed balloon. It's safe to call
	 * this method even if no balloon is currently shown in the MapView.
	 * 
	 * @return <code>true</code> if it were a balloon visible.
	 */
	public boolean hideBalloon() {
		if ((balloonView != null) && (balloonView.getVisibility() != View.GONE)) {
			Log.d(TAG, "Hidding balloon");
			balloonView.setVisibility(View.GONE);
			return true;
		}
		return false;
	}
	
	
	
	
	
	
	
	
	public class PlaceVisit extends AsyncTask<String, String, String> {
		Place place;
		int success;
		final int thisIndex;
		CustomizedOverlayItem item;
				

		public PlaceVisit(int thisIndex, CustomizedOverlayItem item) {
			this.thisIndex=thisIndex;
			this.item=item;
			this.place=item.getPlace();
		}

		@Override
		protected String doInBackground(String... param) {		
					
			
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					
					params.add(new BasicNameValuePair("trip_id", String.valueOf(cur_trip.getTrip_id())));
					
					long user_id=tauth.getUserID();

					params.add(new BasicNameValuePair("profile_id", Long.toString(user_id)));
					
					params.add(new BasicNameValuePair("place_lat", Double
							.toString(place.geometry.location.lat)));

					params.add(new BasicNameValuePair("place_long", Double
							.toString(place.geometry.location.lng)));

					params.add(new BasicNameValuePair("cur_lat", Double
							.toString(cur_trip.getCur_lat())));

					params.add(new BasicNameValuePair("cur_long", Double
							.toString(cur_trip.getCur_long())));
					
					params.add(new BasicNameValuePair("displaytag", place.displayTag));
					
					
					// Log.d("Place Details: ",placeDetails.result.toString());
					String gotTypes[] = place.types;
					// Log.d("PLACE TYPES: ",gotTypes.toString());
					String t = "";
					for (int it = 0; it < gotTypes.length; it++) {

						if (it == gotTypes.length - 1) {

							t += gotTypes[it];

						} else {
							t += gotTypes[it] + ",";
						}
					}

					params.add(new BasicNameValuePair("type", t));
					

					params.add(new BasicNameValuePair("radius", Double.toString(cur_trip.getRadius())));
					
					try {
						
					JSONObject json = jsonParser.makeHttpRequest(url, "POST",
							params);
					
					
					
					
					success=json.getInt(TAG_SUCCESS);
					
					
					} catch (Exception e) {
						e.printStackTrace();

					}

			
			
			return null;
		}
		
		@Override
		protected void onPostExecute(String file_url) {
				if(success==1){
					getBalloon(thisIndex,item);
					
					String status="RedKnot Shout : "+place.name+ " is visited just now.... "+"Fly with #RedKnotProject @RedKnotProject ";
					
					tauth.updateTweet(status);
				}

		}
	
	
	
	
		}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	

	
}