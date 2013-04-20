package com.mywork.place;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Set;
import java.util.Vector;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.text.InputType;
import android.text.format.Time;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.SlidingDrawer;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapController;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.OverlayItem;
import com.mywork.db.AddTourPlaces;
import com.mywork.db.PlacesFromDb;
import com.mywork.db.PutToPlaces;
import com.mywork.db.TripDetailsUpdate;
import com.mywork.preference.SessionManager;
import com.mywork.ui.R;
import com.sam.addplace.Addplace;
import com.samtube405.singleplace.JSONParser;
import com.samtube405.timeline.ImageLoader;
import com.samtube405.timeline.MemoryCache;
import com.samtube405.track.CircleOverlay;
import com.samtube405.track.CustomizedOverlayItem;
import com.samtube405.track.DrawLine;
import com.samtube405.track.ParcelableGeoPoint;

import com.redknot.resources.*;

public class PlacesMapActivity extends MapActivity implements OnClickListener,
		OnCheckedChangeListener, LocationListener, OnDrawerCloseListener {
	
	int visit_radius=1500;
	// Nearest places
	public PlaceList nearPlaces;

	// Map view
	MapView mapView;
	
	
	// Map overlay items
	List<Overlay> mapOverlays;

	AddItemizedOverlay itemizedOverlay, visitOverlay;

	GeoPoint geoPoint, prev, dummy, now, centerLoc;
	// Map controllers
	MapController mc;

	Drawable d;

	final static long MIN_DISTANCE_CHANGE_FOR_UPDATES = 05;

	final static long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

	// detect internet connection status
	ConnectionDetector cd;

	boolean isInternetPresent;

	// alerting services
	AlertDialogManager alert = new AlertDialogManager();

	// check for gps details
	GPSTracker gps;

	ProgressDialog pDialog;

	// found places
	GooglePlaces googlePlaces;

	// manage shared preferences
	SessionManager sm;

	// store preferences
	Vector<String> s = new Vector<String>();

	String rad = "";

	String prev_rad = "";

	boolean is_radius_set = false;

	boolean is_radius_changed = false;

	EditText radiuset;

	private static final String KEY_PREF = "preferences";

	private static final String KEY_RAD = "radius";

	boolean is_prefs_changed = false;

	boolean is_prefs_set = false;

	SlidingDrawer sd;

	CircleOverlay circleOverlay = null;
	
	CircleOverlay circleplaceOverlay = null;

	double latitude;
	double longitude;

	double current_latitude;
	double current_longitude;

	OverlayItem overlayitem;
	
	CustomizedOverlayItem coverlayitem;

	private LocationManager lm;

	private String towers;

	private boolean isGPSEnabled;

	private boolean isNetworkEnabled;

	private Location location;

	private int lat;

	private int longi;

	double radius = 1000;

	private String tourName;

	Intent i;

	private static final String KEY_USER_NAME = "username";
	private static final String KEY_USER_ID = "userid";
	
	PlaceList detailsList=null;	
	
	String user,trip_id;
	
	String userid;
	
	TripDetailsUpdate td;

	private Geocoder gc;

	// Drawable marker icon
	Drawable drawable_user,drawable_place,drawable_visit;
	
	Location nl=null,nnl=null;
	
	ListView l;
	
	CurrentTrip cur_trip;

	protected String[] options=null;
	protected boolean[] selections;

	public final static String TAG_SUCCESS = "success";
	public final static String TAG_CATEGORY = "category";
	public final static String TAG_DISPLAY_NAME = "displaytag";
	public final static String TAG_TYPE = "type";
	public final static String TAG_DESC = "description";

	private static String url = "http://redknot.ckreativity.com/android_connect/get_prefs.php";
	
	private static final String IMG_MARKER_URL = "http://redknot.ckreativity.com/media/map_markers/";

	JSONParser jp = new JSONParser();

	JSONObject json;
	
	ArrayList<HashMap<String, String>> categoryList;

	
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_places);
		i = getIntent();
		user= i.getStringExtra(KEY_USER_NAME);
		userid=i.getStringExtra(KEY_USER_ID);
		
		Log.d("USER ID PLACESMAP :",userid);
		
		initialize();

		
	}

	public void initialize() {
		
		drawable_user=this.getResources().getDrawable(
				R.drawable.mark_red);
		drawable_place = this.getResources().getDrawable(
				R.drawable.mark_blue);
		drawable_visit = this.getResources().getDrawable(
				R.drawable.marker_visited);

		
		mapView = (MapView) findViewById(R.id.mapView);
		mapView.requestFocus();
		mapView.setBuiltInZoomControls(true);

		mapOverlays = mapView.getOverlays();
		
		gc=new Geocoder(getApplicationContext(),Locale.getDefault());

		radiuset = (EditText) findViewById(R.id.rad);
		
		l = (ListView) findViewById(R.id.list);

		sd = (SlidingDrawer) findViewById(R.id.slidingD);

		sd.setOnDrawerCloseListener(this);

		sm = new SessionManager(getApplicationContext());

		cd = new ConnectionDetector(getApplicationContext());

		isInternetPresent = cd.isConnectingToInternet();

		if (!isInternetPresent) {
			alert.showAlertDialog(PlacesMapActivity.this,
					"Internet Connection Error",
					"Please connect to a working Internet connection", false);
				//super.onBackPressed();
		}

		gps = new GPSTracker(this);

		if (gps.canGetLocation()) {

			current_latitude = gps.getLatitude();
			current_longitude = gps.getLongitude();
			Log.d("Your Location", "latitude:" + current_latitude
					+ ", longitude: " + current_longitude);
			
			

		} else {
			// Can't get user's current location -> present to activate GPS
			gps.showSettingsAlert();
			//super.onBackPressed();
			// stop executing code by return
			finish();
		}
		
		
		
		
		
		
		// long got=gps.locationGotTime();

		// SimpleDateFormat formatter=new SimpleDateFormat("dd/MM/yyyy");
		// String dt=formatter.format(new Date(got));

		// alert.showAlertDialog(PlacesMapActivity.this,"Got Last Location","Your Last Known Location time is at: "+dt,
		// false);

		

		Touchy newitem = new Touchy();
		// List<Overlay> overlayList = mapOverlays;
		// overlayList.add(newitem);
		mapOverlays.add(newitem);

		// Geopoint to place on map
		prev = geoPoint = centerLoc = new GeoPoint(
				(int) (current_latitude * 1E6), (int) (current_longitude * 1E6));

		

		// itemizedOverlay = new AddItemizedOverlay(drawable_user, this);

		// Map overlay item
		coverlayitem = new CustomizedOverlayItem(geoPoint, "Your Location", "That is you!",true,drawable_user);

		// itemizedOverlay.addOverlay(overlayitem);
		AddItemizedOverlay point = new AddItemizedOverlay(this,mapView);
		// mapOverlays.add(itemizedOverlay);
		point.addOverlay(coverlayitem);
		
		mapOverlays.add(point);

		// mapOverlays.add(new CircleOverlay(this, geoPoint, 1000));

		// itemizedOverlay.populateNow();
		point.populateNow();

		mc = mapView.getController();
		mc.animateTo(geoPoint);
		mc.setZoom(9);

		lm = (LocationManager) this
				.getSystemService(getApplicationContext().LOCATION_SERVICE);
		isGPSEnabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
		isNetworkEnabled = lm
				.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
		lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,
				MIN_TIME_BW_UPDATES, MIN_DISTANCE_CHANGE_FOR_UPDATES, this);

		// new GetName().execute();
		//getName();
		td=new TripDetailsUpdate(user,"Tour_", PlacesMapActivity.this);
		
		trip_id=td.getTripId();	
		
		new getCategories().execute();

		l.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub

				String clicked = ((TextView) arg1.findViewById(R.id.list_item_title)).getText().toString();
				String types = ((TextView) arg1.findViewById(R.id.list_item_types)).getText().toString();

				
				if (options == null) {
					options = types.toUpperCase().split(",");
					selections = new boolean[options.length];
				}

				Log.d("Clicked Item :", clicked);

				showDialog(arg2);

			}
		});
		
	}

	public void locationProcessHandler(GeoPoint current, boolean is_out,
			boolean is_dist) {

		if (is_out) {

			if (circleOverlay != null) {
				mapOverlays.remove(circleOverlay);
			}
			if (itemizedOverlay != null) {
				mapOverlays.remove(itemizedOverlay);
			}
			circleOverlay = new CircleOverlay(this, current, (float) radius);
			
			mapOverlays.add(circleOverlay);
			
			new LoadPlaces().execute();
			
			

		}

	}

	public void pinNearPlaces() {

		
		itemizedOverlay = new AddItemizedOverlay(this,mapView,cur_trip);
		
		MemoryCache memoryCache=new MemoryCache();
		
		ImageLoader imgLoader=new ImageLoader(getApplicationContext());
		

		// mc.setZoom(25);

		// These values are used to get map boundary area
		// The area where you can see all the markers on screen
		int minLat = Integer.MAX_VALUE;
		int minLong = Integer.MAX_VALUE;
		int maxLat = Integer.MIN_VALUE;
		int maxLong = Integer.MIN_VALUE;

		// check for null in case it is null
		if (nearPlaces.results != null) {

			// loop through all the places
			for (Place place : nearPlaces.results) {
				
				//Log.d("ADDING TO MAP :",place.toString());
				latitude = place.geometry.location.lat; // latitude
				longitude = place.geometry.location.lng; // longitude

				
				// Geopoint to place on map
				geoPoint = new GeoPoint((int) (latitude * 1E6),
						(int) (longitude * 1E6));
				
				

				//Adding relevant display tag
				place.displayTag=getDisplayTag(place);
				
				
				// Get icon from file cahe or web server
				String icon_url=IMG_MARKER_URL+place.types[0]+".png";
				
				Bitmap bitmap=memoryCache.get(icon_url);
				
		        if(bitmap==null)bitmap=imgLoader.getBitmap(icon_url);
				
		        Drawable d = new BitmapDrawable(getResources(),bitmap);		
		        
		        if(d==null) d=getResources().getDrawable(R.drawable.mark_blue);// Default icon	
		        
		        
				
				
				/*int dTag;
				if(iconset.containsKey(place.displayTag)){
            		dTag=iconset.get(place.displayTag);
            	}else{
            		dTag=iconset.get("NO_TAG");
            	}      */   
				
				
				
				Log.d("Display Tag : ",place.displayTag+" Type :"+place.types[0]);
				
				
				coverlayitem = new CustomizedOverlayItem(geoPoint,
						 place,d);
				
				//coverlayitem.setMarker(d);

				itemizedOverlay.addOverlay(coverlayitem);

				// calculating map boundary area
				minLat = (int) Math.min(geoPoint.getLatitudeE6(), minLat);
				minLong = (int) Math.min(geoPoint.getLongitudeE6(), minLong);
				maxLat = (int) Math.max(geoPoint.getLatitudeE6(), maxLat);
				maxLong = (int) Math.max(geoPoint.getLongitudeE6(), maxLong);
				
				
			
			}
			
			mapOverlays.add(itemizedOverlay);
			
			itemizedOverlay.populateNow();
			

			// mapOverlays.add(new CircleOverlay(this,
			// geoPoint.getLatitudeE6(),geoPoint.getLongitudeE6(),1000));
			// mapOverlays.add(new DrawLine(this, geoPoint,dummy));

			// showing all overlay items
			
		}

		// Adjusting the zoom level so that you can see all the markers on map
		mc.zoomToSpan(Math.abs(minLat - maxLat), Math.abs(minLong - maxLong));

		// Showing the center of the map
		mc.animateTo(new GeoPoint((maxLat + minLat) / 2,
				(maxLong + minLong) / 2));

		// mapView.postInvalidate();
		
		

	}

	private String getDisplayTag(Place place) {
		String defTag="NO_TAG";
		String types[],paramtypes[];
		for(HashMap<String,String> dtag:categoryList){
			String type=dtag.get(TAG_DISPLAY_NAME);
			types=type.split(",");
			for(int i=0;i<types.length;i++){
				paramtypes=place.types;
				for(int j=0;j<paramtypes.length;j++){
					if(paramtypes[j].equalsIgnoreCase(types[i])){
						Log.d("BEQZ TRUE :",paramtypes[j]+" "+types[i]);
						return type;
					}
						
				}
			}
		}
		return defTag;
	}

	private String getKey(String type) {		
			for(HashMap<String,String> dtag:categoryList){
				for (Entry<String, String> e : dtag.entrySet()) {					
				    String key = e.getKey();
				    String value = e.getValue();
				    Log.d("Key :",key);
				    Log.d("Value :",value);
				    Log.d("Type :",type);
				    if(value.equals(type)) return key;
				}
			
		}		
		
		return "NO_TAG";
	}

	public void processPreferences(CompoundButton buttonView) {

		// if user adds a preference
		if (buttonView.isChecked()) {
			s.add((String) buttonView.getTag());

			// if no preferences were added to shared pref
			if (!sm.isPreferenceSet()) {
				Log.d("FIRST TIME",
						"Inserting First Preference!!! ---- " + s.get(0));
				sm.addPreference(s.get(0));
			}
			// else
			else {
				String pref = "";

				// prepare string to store in shared pref
				for (int i = 0; i < s.size(); i++) {

					if (i == 0) {
						pref = pref + s.get(i);
					} else {

						pref = pref + "," + s.get(i);
					}
				}
				Log.d("PREFERENCES", pref);
				sm.updatePreference(pref);
			}

		} else { // if user removes a preference

			String pref = "";

			for (int i = 0; i < s.size(); i++) {

				if (s.get(i).equals(buttonView.getTag())) {

					s.remove(i);
				}
			}

			for (int i = 0; i < s.size(); i++) {
				if (pref.length() == 0) {
					pref = pref + s.get(i);
				} else {
					pref = pref + "," + s.get(i);
				}

			}
			Log.d("PREFERENCES", pref);
			sm.updatePreference(pref);
		}

		if (s.size() > 0) {
			is_prefs_set = true;
		} else {
			is_prefs_set = false;
		}

	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean arg1) {
		// TODO Auto-generated method stub
		processPreferences(buttonView);

		is_prefs_changed = true;

	}

	class Touchy extends Overlay {

		private int x;
		private int y;
		private GeoPoint touchedPoint;
		private long start;
		private long stop;

		@Override
		public boolean onTouchEvent(MotionEvent e, MapView mapView) {
			final GeoPoint gp=getTouchLoc(e);
			double lat,lon;
			
			if (e.getAction() == MotionEvent.ACTION_DOWN) {
				start = e.getEventTime();
				x = (int) e.getX();
				y = (int) e.getY();
				touchedPoint = mapView.getProjection().fromPixels(x, y);
			}
			if (e.getAction() == MotionEvent.ACTION_UP) {
				stop = e.getEventTime();
			}
			if (stop - start > 1500) {
				final AlertDialog alert = new AlertDialog.Builder(
						PlacesMapActivity.this).create();
				alert.setTitle("Pick an Option");
				alert.setMessage("Hey..Pick Up!");
				alert.setButton("Quick Info",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								// Intent ourIntent=new
								// Intent("com.samtube405.trackmap."+"SinglePlace");
								// startActivity(ourIntent);
								Geocoder geocoder = new Geocoder(
										getBaseContext(), Locale.getDefault());
								try {

									List<Address> address = geocoder.getFromLocation(
											touchedPoint.getLatitudeE6() / 1E6,
											touchedPoint.getLongitudeE6() / 1E6,
											1);

									if (address.size() > 0) {
										String display = "";
										for (int i = 0; i < address.get(0)
												.getMaxAddressLineIndex(); i++) {

											display += address.get(0)
													.getAddressLine(i) + "\n";
										}
										Toast t3 = Toast.makeText(
												getBaseContext(), display,
												Toast.LENGTH_LONG);
										t3.show();
									}

								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} finally {

								}

							}
						});
				alert.setButton2("All Places",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								
								if(PlacesMapActivity.this.detailsList!=null && PlacesMapActivity.this.detailsList.results!=null){
									Intent ourIntent = new Intent(
											PlacesMapActivity.this, AllPlaces.class);
									Bundle b=new Bundle();
									detailsList.cur_user=user;
									detailsList.cur_userid=userid;
									b.putSerializable("places", detailsList);
									ourIntent.putExtras(b);
									startActivity(ourIntent);

									
								}
								else{
								//Log.d("Dismiss alert","Dismissing Now");
								//alert.dismiss();
								//Log.d("Dismiss alert","Dismiss Success");
								AlertDialogManager a=new AlertDialogManager();
								a.showAlertDialog(PlacesMapActivity.this, "No Places","Sorry, no places were found", false);
								//alert.setTitle("No Places");
								//alert.setMessage("Sorry, no places were found");
								//alert.setCancelable(false);
								//alert.show();
								}
								
								
							}
						});
				alert.setButton3("New Place",
						new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog,
									int which) {
								Intent ourIntent = new Intent(
										PlacesMapActivity.this,
										Addplace.class);
								ParcelableGeoPoint geoPoint=new ParcelableGeoPoint(gp);
								ourIntent.putExtra("geoPoint", geoPoint);
								startActivity(ourIntent);

							}
						});
				alert.show();

				return true;
			}
			return false;
		}

	}
	
	public GeoPoint getTouchLoc(MotionEvent event){
		GeoPoint gp=null;
		if (event.getAction() == 1) {
            gp = mapView.getProjection().fromPixels(
                (int) event.getX(),
                (int) event.getY());   
            
        }
		
		return gp;
	}
	
	public void checkOverlayItemIsVisit(double cur_lat,double cur_lo){
		
		int i=0;	
		
		//visitOverlay = new AddItemizedOverlay(drawable_user, this);
		
		if(itemizedOverlay!=null){
			
			if(itemizedOverlay.mapOverlays!=null){
				
				for(int j=0;j<itemizedOverlay.mapOverlays.size();j++){
					
					double l=itemizedOverlay.mapOverlays.get(j).getPlace().geometry.location.lat;
					
					double lo=itemizedOverlay.mapOverlays.get(j).getPlace().geometry.location.lng;
					
					boolean visit=compareToCircle(cur_lat, cur_lo,l , 
							lo, visit_radius);
					
					
					if(visit){
						
						Log.d("Place Visitable:", "YES");
						
						itemizedOverlay.mapOverlays.get(j).setVisitable(true);
						
						geoPoint = new GeoPoint((int) (l * 1E6),
								(int) (lo * 1E6));
						
						//circleplaceOverlay = new CircleOverlay(this, geoPoint, 30,Color.GREEN);
						
						//mapOverlays.add(circleplaceOverlay);
											
						
						
						
					}
					else{
						Log.d("Place Visitable:", "NO");
					}
		 		  
					
					
				}
				
				
				
			}
		}

		
 	   
 	  
 	   
 	  mapView.invalidate();
 	   
    }
 
	
	
	
	public void visitPlace(Location l){		
		
		nnl=l;
		
		if(nl==null){
			nl=l;
			
			checkOverlayItemIsVisit(nl.getLatitude(), nl.getLongitude());
		}
		
		if(nl.distanceTo(nnl)>visit_radius){
			nl=nnl;
			
			//mapOverlays.remove(circleplaceOverlay);
			
			checkOverlayItemIsVisit(nl.getLatitude(), nl.getLongitude());
			
			
		}
	}

	@Override
	public void onLocationChanged(Location l) {
		int lat = (int) (l.getLatitude() * 1E6);
		int longi = (int) (l.getLongitude() * 1E6);

		now = new GeoPoint(lat, longi);

		boolean is_dist = compareToCircle(prev.getLatitudeE6(),prev.getLongitudeE6(),lat, longi, 15);// 10 is minimal
															// distance between
															// 2 considerable
															// points
		
		//boolean is_visit= compareToCircle(now.getLatitudeE6(), now.getLongitudeE6(), prev.getLatitudeE6(), prev.getLongitudeE6(), 15);
		//compareToPlace(l.getLatitude(), l.getLongitude());
		//checkOverlayItemIsVisit(l.getLatitude(), l.getLongitude());
		visitPlace(l);
		

		if (is_dist) {

			mapOverlays.add(new DrawLine(this, prev, now));

		}

		boolean is_out = compareToCircle(centerLoc.getLatitudeE6(),centerLoc.getLongitudeE6(),lat, longi, radius);

		prev = now;

		if (is_out) {
			this.centerLoc = now;
			current_latitude=lat;
			current_longitude=longi;
			// call location process
		}
		locationProcessHandler(now, is_out, is_dist);

		Toast.makeText(PlacesMapActivity.this,
				"Location Changed Lat: " + lat + " Long: " + longi,
				Toast.LENGTH_SHORT).show();
		
		mapView.postInvalidate();

	}

	public boolean compareToCircle(int l1,int lo1,int l2, int lo2, double dist) {
		double lat2 = Math.toRadians(l2 / 1E6);
		double longi2 = Math.toRadians(lo2 / 1E6);

		double lat1 = Math.toRadians(l1 / 1E6);
		double longi1 = Math.toRadians(lo1 / 1E6);

		double dlongi = longi2 - longi1;
		double R = 6371;

		double dkm = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(dlongi))
				* R;

		double dm = dkm * 1000;

		if (dm >= dist) {

			return true;
		}
		return false;

	}
	
	public boolean compareToCircle(double l1,double lo1,double l2, double lo2, double dist) {
		double lat2 = Math.toRadians(l2);
		double longi2 = Math.toRadians(lo2);

		double lat1 = Math.toRadians(l1) ;
		double longi1 = Math.toRadians(lo1);

		double dlongi = longi2 - longi1;
		double R = 6371;

		double dkm = Math.acos(Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1)
				* Math.cos(lat2) * Math.cos(dlongi))
				* R;
		
		

		double dm = dkm * 1000;
		
		Log.d("Distance", ""+dm+"/"+dist);

		if (dm <= dist) {

			return true;
		}
		return false;

	}
	

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//mapView.requestFocus();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//mapView.requestFocus();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub

		lm.removeUpdates(this);
		super.onDestroy();
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub

	}

	class LoadPlaces extends AsyncTask<String, String, String> {

		private PlacesFromDb places;
		private boolean is_places_found_from_db;

		/**
		 * Before starting background thread Show Progress Dialog
		 * */
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(PlacesMapActivity.this);
			pDialog.setMessage(Html
					.fromHtml("<b>Search</b><br/>Loading Map View with Places..."));
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		/**
		 * getting Places JSON
		 * */
		protected String doInBackground(String... args) {
			// creating Places class object
			googlePlaces = new GooglePlaces();
			places=new PlacesFromDb();

			try {
				// Separeate your place types by PIPE symbol "|"
				// If you want all types places make it as null
				// Check list of types supported by google
				//

				HashMap<String, String> prefs = sm.getPreference();

				String pref = prefs.get(KEY_PREF);

				// radius = Integer.parseInt(prefs.get(KEY_RAD)) * 1000;

				String p[] = pref.split(",");

				String types = "";

				for (int i = 0; i < p.length; i++) {

					if (i == 0) {
						types = types + p[i];
					} else {
						types = types + "|" + p[i];
					}
				}
				

				double clat = centerLoc.getLatitudeE6() / 1E6;
				double clong = centerLoc.getLongitudeE6() / 1E6;
				
				nearPlaces=places.search(clat, clong, radius,pref);
				
				if(places.getNumberOfPlaces()>5){
					
					is_places_found_from_db=true;					
					
				}
				else{
					
					nearPlaces = googlePlaces.search(clat, clong, radius, types);
				}
				
				
				

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}

		/**
		 * After completing background task Dismiss the progress dialog and show
		 * the data in UI Always use runOnUiThread(new Runnable()) to update UI
		 * from background thread, otherwise you will get error
		 * **/
		protected void onPostExecute(String file_url) {
			// dismiss the dialog after getting all products
			pDialog.dismiss();
			// updating UI from Background Thread
			runOnUiThread(new Runnable() {
				public void run() {
					/**
					 * Updating parsed Places into LISTVIEW
					 * */
					// Get json response status
					String status = nearPlaces.status;

					// Check for all possible status
					if (status.equals("OK")) {

						// Successfully got places details
						if (nearPlaces.results != null) {
							
							Log.d("Places: ",nearPlaces.results.toString());
							
							trip_id=td.getTripId();							
							
							AddTourPlaces at=new AddTourPlaces(nearPlaces,
									PlacesMapActivity.this,trip_id,user,centerLoc,radius,is_places_found_from_db);
							
							
							at.init();
							
							cur_trip=new CurrentTrip(Integer.parseInt(trip_id), centerLoc.getLatitudeE6()/1E6, centerLoc.getLongitudeE6()/1E6, radius);
							
							PlacesMapActivity.this.detailsList=at.getDetailsList();							
							
							//pinpoint found places
							pinNearPlaces();
							
							checkOverlayItemIsVisit(centerLoc.getLatitudeE6()/1E6, centerLoc.getLongitudeE6()/1E6);
							
							
							
						}
					} else if (status.equals("ZERO_RESULTS")) {
						// Zero results found
						alert.showAlertDialog(
								PlacesMapActivity.this,
								"Near Places",
								"Sorry no places found. Try to change the types of places",
								false);
					} else if (status.equals("UNKNOWN_ERROR")) {
						alert.showAlertDialog(PlacesMapActivity.this,
								"Places Error", "Sorry unknown error occured.",
								false);
					} else if (status.equals("OVER_QUERY_LIMIT")) {
						alert.showAlertDialog(
								PlacesMapActivity.this,
								"Places Error",
								"Sorry query limit to google places is reached",
								false);
					} else if (status.equals("REQUEST_DENIED")) {
						alert.showAlertDialog(PlacesMapActivity.this,
								"Places Error",
								"Sorry error occured. Request is denied", false);
					} else if (status.equals("INVALID_REQUEST")) {
						alert.showAlertDialog(PlacesMapActivity.this,
								"Places Error",
								"Sorry error occured. Invalid Request", false);
					} else {
						alert.showAlertDialog(PlacesMapActivity.this,
								"Places Error", "Sorry error occured.", false);
					}
				}
			});

		}

	}

	@Override
	public void onDrawerClosed() {
		// TODO Auto-generated method stub

		prev_rad = rad;
		rad = radiuset.getText().toString();

		if (rad.length() > 0) {

			is_radius_set = true;
			if (!prev_rad.equals(rad)) {

				is_radius_changed = true;
				// radius=Integer.parseInt(rad)*1000;
				radius = Double.parseDouble(rad) * 1000;

				if (!sm.isRadiusSet()) {
					sm.addRadius(rad);
				} else {

					sm.updateRadius(rad);
				}

			}

		} else {

			is_radius_set = false;
			// alert.showAlertDialog(PlacesMapActivity.this,"Search Radius",
			// "Please enter a radius value for searching", false);
		}

		if ((is_prefs_changed && is_radius_set)
				| (is_radius_changed && is_prefs_set)
				| (is_prefs_changed && is_radius_changed)) {
			is_prefs_changed = false;
			is_radius_changed = false;
			// new LoadPlaces().execute();
			locationProcessHandler(centerLoc, true, false);
		}

		if (!is_prefs_set | !is_radius_set) {

			alert.showAlertDialog(
					PlacesMapActivity.this,
					"No settings",
					"Please enter a radius value and at least one Preference for searching places",
					false);
		}

	}

	public void getName() {
		
		AlertDialog.Builder alert =new AlertDialog.Builder(PlacesMapActivity.this);

		alert.setCancelable(false);
		alert.setTitle("Tour Name");
		alert.setMessage("Please Enter a Name For Your Tour");

		// Set an EditText view to get user input 
		final EditText input = new EditText(this);
		input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
		public void onClick(DialogInterface dialog, int whichButton) {
		  tourName = input.getText().toString();
		  // Do something with value!
		  if(tourName.length()==0){
			  getName();
		  }
		  else{
			  Toast.makeText(PlacesMapActivity.this,"Tour Name: "+tourName, Toast.LENGTH_SHORT).show();
			  td=new TripDetailsUpdate(user,tourName, PlacesMapActivity.this);
			  
		  }
		  }
		});
		
		AlertDialog a=alert.create();
		a.show();
		WindowManager.LayoutParams lp=a.getWindow().getAttributes();
		lp.dimAmount=0.0f;
		a.getWindow().setAttributes(lp);
		a.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
		

	}
	
	class getCategories extends AsyncTask<String, String, String> {

		

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			json = jp.makeHttpRequest(url, "GET", params);

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			
			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					int success = 0;

					try {

						success = json.getInt(TAG_SUCCESS);

						if (success == 1) {

							JSONArray results = json.getJSONArray(TAG_CATEGORY);
							

							categoryList = new ArrayList<HashMap<String, String>>();

							for (int i = 0; i < results.length(); i++) {

								JSONObject c = results.getJSONObject(i);

								String display_name = c
										.getString(TAG_DISPLAY_NAME);

								Log.d("DISPLAY NAME: ", display_name);

								String types = c.getString(TAG_TYPE);

								//String[] prefs = types.split(",");

								HashMap<String, String> map = new HashMap<String, String>();

								map.put(TAG_DISPLAY_NAME, display_name);
								map.put(TAG_TYPE, types);

								categoryList.add(map);


							}
							
							ListAdapter adapter = new SimpleAdapter(
									PlacesMapActivity.this, categoryList,
									R.layout.list_item_for_pref, new String[] {
											TAG_DISPLAY_NAME, TAG_TYPE },
									new int[] { R.id.list_item_title,
											R.id.list_item_types });
							Log.d("LIST VALS: ", categoryList.toString());
							l.setAdapter(adapter);
							// l.setSelector( R.drawable.listselector);
						}

					} catch (Exception e) {
						e.printStackTrace();

					}

				}
			});
		}

	}
	
	@Override
	protected Dialog onCreateDialog(int id) {
		return new AlertDialog.Builder(this)
				.setTitle("Types")
				.setMultiChoiceItems(options, selections,
						new DialogSelectionClickHandler())
				.setPositiveButton("OK", new DialogButtonClickHandler())
				.create();
	}

	public class DialogSelectionClickHandler implements
			DialogInterface.OnMultiChoiceClickListener {
		public void onClick(DialogInterface dialog, int clicked,
				boolean selected) {
			is_prefs_changed = true;
			Log.i("ME", options[clicked] + " selected: " + selected);
			
			if (selected == true) {
				Log.d("SELECTED PREF TYPE: ", options[clicked]);

				s.add(options[clicked].toLowerCase());

				// if no preferences were added to shared pref
				if (!sm.isPreferenceSet()) {
					Log.d("FIRST TIME",
							"Inserting First Preference : " + s.get(0));
					sm.addPreference(s.get(0));
				}
				// else
				else {
					String pref = "";

					// prepare string to store in shared pref
					for (int j = 0; j < s.size(); j++) {

						if (j == 0) {
							pref = pref + s.get(j);
						} else {

							pref = pref + "," + s.get(j);
						}
					}
					Log.d("PREFERENCES", pref);
					sm.updatePreference(pref);
				}

			} else { // if user removes a preference

				String pref = "";

				for (int j = 0; j < s.size(); j++) {

					if (s.get(j).equals(options[clicked].toLowerCase())) {

						s.remove(j);
					}
				}

				for (int j = 0; j < s.size(); j++) {
					if (pref.length() == 0) {
						pref = pref + s.get(j);
					} else {
						pref = pref + "," + s.get(j);
					}

				}
				Log.d("PREFERENCES", pref);
				sm.updatePreference(pref);
			}
			if (s.size() > 0) {
				is_prefs_set = true;
			} else {
				is_prefs_set = false;
				is_prefs_changed = false;
			}
			
		}
	}

	public class DialogButtonClickHandler implements
			DialogInterface.OnClickListener {
		public void onClick(DialogInterface dialog, int clicked) {
			switch (clicked) {
			case DialogInterface.BUTTON_POSITIVE:

				printSelectedPlanets();
				options = null;
				break;
			}
		}
	}

	protected void printSelectedPlanets() {
		
		for (int i = 0; i < options.length; i++) {
			 Log.i("ME", options[i] + " selected: " + selections[i]);
			
		}
	}
}
