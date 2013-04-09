package com.mywork.ui;



import java.io.FileInputStream;
import java.util.Map;

import com.example.list.AndroidJSONParsingActivity;
import com.mywork.onmaps.Calendar;
import com.mywork.onmaps.Profile;
import com.mywork.place.AlertDialogManager;
import com.mywork.place.ConnectionDetector;
import com.mywork.place.PlacesMapActivity;
import com.redknot.summary.RetrieveData;
import com.redknot.summary.SummaryActivity;



import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
//import android.os.StrictMode;
import android.text.Html;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;


@TargetApi(Build.VERSION_CODES.GINGERBREAD)
public class Login extends Activity implements OnItemClickListener{

	Button b1,b2,b3,b4;
	TextView tv1,tv2,tv3,tv4,userlabel;
	//EditText username,password;
	
	User user=null;
	String username="USER DEFAULT";
	
	
	static String TWITTER_CONSUMER_KEY = "qAxRSYF37YU8lpivnAqRlw";
	static String TWITTER_CONSUMER_SECRET = "IPAVrefUlRboe1pDY72rOVxs66Djx1TrdO3lJHgZs";
	
	static String PREFERENCE_NAME = "twitter_oauth";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
	
	static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
	
	static final String URL_TWITTER_AUTH = "auth_url";
	static final String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
	static final String URL_TWITTER_OAUTH_TOKEN = "oauth_token";
	
	// Progress dialog
	ProgressDialog pDialog;
	
	private static Twitter twitter;
	private static RequestToken requestToken;

	private static SharedPreferences mSharedPreferences;

	private ConnectionDetector cd;

	AlertDialogManager alert = new AlertDialogManager();
	

	Button btnLoginTwitter,btnLogoutTwitter;
	private AccessToken accessToken;
	
	private GestureDetector gd=null;
	LinearLayout layout;
	
	private int pics=0;
	
	CoverFlow coverFlow;
	
	
	//@TargetApi(Build.VERSION_CODES.GINGERBREAD)
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ui_main);
		
		layout=(LinearLayout) findViewById(R.id.rl1);
		
		//Extra methods Twitter
		if (Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
			}
		
		cd = new ConnectionDetector(getApplicationContext());

		// Check if Internet present
		if (!cd.isConnectingToInternet()) {
			// Internet Connection is not present
			alert.showAlertDialog(Login.this, "Internet Connection Error",
					"Please connect to working Internet connection", false);
					//super.onBackPressed();
			//super.
			// stop executing code by return
			return;
		}
		
		// Check if twitter keys are set
		if(TWITTER_CONSUMER_KEY.trim().length() == 0 || TWITTER_CONSUMER_SECRET.trim().length() == 0){
			// Internet Connection is not present
			alert.showAlertDialog(Login.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
			// stop executing code by return
			return;
		}

		// Shared Preferences
		mSharedPreferences = getApplicationContext().getSharedPreferences(
						"MyPref", 0);

		
		//////////////////////////////////////////////Animation Code
				
		
		coverFlow =  (CoverFlow) findViewById(R.id.coverflow);
		

		coverFlow.setAdapter(new ImageAdapter(this));

		ImageAdapter coverImageAdapter = new ImageAdapter(this);

		coverImageAdapter.createReflectedImages();

		coverFlow.setAdapter(coverImageAdapter);
		
		coverFlow.setOnItemClickListener(this);		

		coverFlow.setSpacing(-10);

		coverFlow.setSelection(0, true);

		coverFlow.setAnimationDuration(1000);
		///////////////////////////////////////////////////////////////
		
		//b1=(Button) findViewById(R.id.favs);
		//b2=(Button) findViewById(R.id.newtour);
		//b3=(Button) findViewById(R.id.communityhelp);
		//b4=(Button) findViewById(R.id.intel);
		
		btnLoginTwitter=(Button)findViewById(R.id.btnLoginTwitter);
		btnLogoutTwitter=(Button)findViewById(R.id.btnLogoutTwitter);
		
		//userlabel=(TextView)findViewById(R.id.lblUserName);
		//bsubmit.setOnClickListener(this);
	
		/**
		 * Twitter login button click event will call loginToTwitter() function
		 * */
		btnLoginTwitter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Check if Internet present
				if (!cd.isConnectingToInternet()) {
					// Internet Connection is not present
					alert.showAlertDialog(Login.this, "Internet Connection Error",
							"Please connect to working Internet connection", false);							
					// stop executing code by return
					return;
				}
				// Call login twitter function
				loginToTwitter();
			}
		});
		
		/**
		 * Button click event for logout from twitter
		 * */
		btnLogoutTwitter.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// Call logout twitter function
				logoutFromTwitter();
			}
		});

		/*b1.setOnClickListener(this);
		b2.setOnClickListener(this);
		b3.setOnClickListener(this);
		b4.setOnClickListener(this);*/
		
		gd= new GestureDetector(new MyGestureListener());
		layout.setOnTouchListener(new OnTouchListener(){

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				gd.onTouchEvent(event);
				return true;
			}
			
		});
		
		testRedirect();

		
	}
	
	public void testRedirect(){
		/** This if conditions is tested once is
		 * redirected from twitter page. Parse the uri to get oAuth
		 * Verifier
		 * */
		if (!isTwitterLoggedInAlready()) {			
			Uri uri=getIntent().getData();
			if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
				// oAuth verifier
				String verifier = uri
						.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);

				try {
					// Get the access token
					accessToken = twitter.getOAuthAccessToken(
							requestToken, verifier);

					// Shared Preferences
					Editor e = mSharedPreferences.edit();

					// After getting access token, access token secret
					// store them in application preferences
					e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
					e.putString(PREF_KEY_OAUTH_SECRET,
							accessToken.getTokenSecret());
					// Store login status - true
					e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					e.commit(); // save changes

					Log.e("Twitter OAuth Token", "> " + accessToken.getToken());

					// Hide login button
					btnLoginTwitter.setVisibility(View.GONE);
					
					b1.setVisibility(View.VISIBLE);
					b2.setVisibility(View.VISIBLE);
					b3.setVisibility(View.VISIBLE);
					b4.setVisibility(View.VISIBLE);
					
					tv1.setVisibility(View.VISIBLE);
					tv2.setVisibility(View.VISIBLE);
					tv3.setVisibility(View.VISIBLE);
					tv4.setVisibility(View.VISIBLE);					

					
					setUserName(twitter,accessToken);
					
					
					
				} catch (Exception e) {
					// Check log for login errors
					Log.e("Twitter Login Error", "> " + e.getMessage());
				}
			}
		}
	}
	
	public boolean dispatchTouchEvent(MotionEvent ev){
		super.dispatchTouchEvent(ev);
		if(gd!=null)
			return gd.onTouchEvent(ev);
		
		return false;
		}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.ui_main, menu);
		return true;
	}
	
		
		/**
		 * Function to login twitter
		 * */
		private void loginToTwitter() {
			// Check if already logged in
			if (!isTwitterLoggedInAlready()) {
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
				Configuration configuration = builder.build();
				
				TwitterFactory factory = new TwitterFactory(configuration);
				twitter = factory.getInstance();

				if(!(Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO)) {
					try {
						requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
						this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
						//startActivity(new Intent(Login.this,MainInterface.class));
					} catch (TwitterException e) {
						e.printStackTrace();
					}
				}else{
					new Thread(new Runnable() {
						public void run() {
						try {	
						requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
						Login.this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(requestToken.getAuthenticationURL())));
						//startActivity(new Intent(Login.this,MainInterface.class));
						} catch (TwitterException e) {
						e.printStackTrace();
						}
						}
						}).start();
					}	
				
				//userlabel.setText(""+getUserName());
				
			} else {						
				
				ConfigurationBuilder builder = new ConfigurationBuilder();
				builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
				builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
				
				// Access Token 
				String access_token = mSharedPreferences.getString(PREF_KEY_OAUTH_TOKEN, "");
				// Access Token Secret
				String access_token_secret = mSharedPreferences.getString(PREF_KEY_OAUTH_SECRET, "");
				
				AccessToken accessToken = new AccessToken(access_token, access_token_secret);
				Twitter twitter = new TwitterFactory(builder.build()).getInstance(accessToken);
				
				
				setUserName(twitter,accessToken);
				
				Toast.makeText(getApplicationContext(),
						"Hello You're Already Logged into twitter "+username, Toast.LENGTH_LONG).show();
				
				//userlabel.setText(getUserName());
				
				// Hide login button
				btnLoginTwitter.setVisibility(View.GONE);
				
				/*b1.setVisibility(View.VISIBLE);
				b2.setVisibility(View.VISIBLE);
				b3.setVisibility(View.VISIBLE);
				b4.setVisibility(View.VISIBLE);*/
				
				coverFlow.setVisibility(View.VISIBLE);
				
				//tv1.setVisibility(View.VISIBLE);
				//tv2.setVisibility(View.VISIBLE);
				//tv3.setVisibility(View.VISIBLE);
				//tv4.setVisibility(View.VISIBLE);
				
			}
			
		}

		private void logoutFromTwitter() {
			// Clear the shared preferences
			Editor e = mSharedPreferences.edit();
			e.remove(PREF_KEY_OAUTH_TOKEN);
			e.remove(PREF_KEY_OAUTH_SECRET);
			e.remove(PREF_KEY_TWITTER_LOGIN);
			e.commit();			

			finish();
			//btnLoginTwitter.setVisibility(View.VISIBLE);
		}
		
		public void setUserName(Twitter t,AccessToken a){
			try {
				username=t.showUser(a.getUserId()).getName();
			} catch (TwitterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
		private boolean isTwitterLoggedInAlready() {
			// return twitter login status from Shared Preferences
			return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
		}
		
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int pos,long arg3) {
			// TODO Auto-generated method stub
			String clicked="";
			switch(pos){
			
			case 0:				
				//new NewTour().execute();
				Intent i0=new Intent(Login.this,PlacesMapActivity.class);
				i0.putExtra("username", username);
				startActivity(i0);
				break;
				
			case 1:
				//btn="Intelligence Tour";
				Intent i1=new Intent(Login.this, BarChart.class);
				startActivity(i1);
				break;
				
			case 2:
				//btn="Favourits";
				Intent i2=new Intent(Login.this, RetrieveData.class);
				startActivity(i2);
				break;
				
			case 3:
				//btn="Community";
				Intent i3=new Intent(Login.this, AndroidJSONParsingActivity.class);
				startActivity(i3);
				break;
			}
			
			//Log.d("CLICKED IMAGE: ",v.getId()+" ->"+clicked);
			//Toast.makeText(CoverFlowExample.this,"You've Clicked "+clicked, Toast.LENGTH_SHORT).show();
			
		}

	/*@Override
	public void onClick(View arg0) {
	String btn="";
		
		switch(arg0.getId()){
		
		case R.id.newtour:
			btn="New Tour";
			//new NewTour().execute();
			Intent i0=new Intent(Login.this,PlacesMapActivity.class);
			i0.putExtra("username", username);
			startActivity(i0);
			break;
			
		case R.id.intel:
			//btn="Intelligence Tour";
			Intent i1=new Intent(Login.this, BarChart.class);
			startActivity(i1);
			break;
			
		case R.id.favs:
			//btn="Favourits";
			Intent i2=new Intent(Login.this, RetrieveData.class);
			startActivity(i2);
			break;
			
		case R.id.communityhelp:
			//btn="Community";
			Intent i3=new Intent(Login.this, AndroidJSONParsingActivity.class);
			startActivity(i3);
			break;
		
		}
		
	}*/
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
	}
	
	public class MyGestureListener  extends GestureDetector.SimpleOnGestureListener{

		private static final int SWIPE_MIN_DISTANCE=100;
		private static final int SWIPE_MAX_OFF_PATH=100;
		private static final int SWIPE_THRESHOLD_VELOCITY=50;
		
			
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			float dX = e2.getX()-e1.getX();

			float dY = e1.getY()-e2.getY();

			if (Math.abs(dY)<SWIPE_MAX_OFF_PATH &&

			Math.abs(velocityX)>=SWIPE_THRESHOLD_VELOCITY &&

			Math.abs(dX)>=SWIPE_MIN_DISTANCE ) {

				if (dX>0) {

					Toast.makeText(getApplicationContext(),"Right Swipe", Toast.LENGTH_SHORT).show();
					
				

				} else {

					//Toast.makeText(getApplicationContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
					new MyTask().execute();
				}

				return true;
			}
			return false;
		}
		
	}
	
	class MyTask extends AsyncTask<String, String, String>{

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Intent i=new Intent(Login.this,Profile.class);
			i.putExtra("username", username);
			startActivity(i);
			return null;
		}
		
		
	}
	
	class NewTour extends AsyncTask<String, String, String>{
		
		  @Override
	        protected void onPreExecute() {
	            super.onPreExecute();
	            pDialog = new ProgressDialog(Login.this);
	            pDialog.setMessage(Html.fromHtml("<b>Search</b><br/>Loading Places..."));
	            pDialog.setIndeterminate(false);
	            pDialog.setCancelable(false);
	            pDialog.show();
	        }

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub
			Intent i=new Intent(Login.this, Main.class);
			startActivity(i);
			return null;
		}
		
	
		protected void onPostExecute() {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            
		}
		
		
	}
	
	public class ImageAdapter extends BaseAdapter{

		int mGalleryItemBackground;

		private Context mContext;

		private FileInputStream fis;

		private Integer[] mImageIds = {

		//R.drawable.newtourone,

		//R.drawable.newtourtwo,

		//R.drawable.favourites,

		//R.drawable.favouritesone,

		//R.drawable.favouritestwo,

		//R.drawable.weather,

		//R.drawable.weathertwo,

		//R.drawable.community,

		//R.drawable.communityone
			
		  R.drawable.community,
		  R.drawable.travel,
		  R.drawable.find,
		  R.drawable.bookmark,

		};

		private ImageView[] mImages;

		public ImageAdapter(Context c) {

			mContext = c;

			mImages = new ImageView[mImageIds.length];
			//Toast.makeText(CoverFlowExample.this,"WORKING",Toast.LENGTH_LONG ).show();
			
			/*for(int i=0;i<mImageIds.length;i++){
				
				
				ImageView iv=new ImageView(Login.this);
				
				iv.setImageResource(mImageIds[i]);
				
				//LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(300, 300);
				
				//iv.setLayoutParams(layoutParams);
				
				iv.setLayoutParams(new Gallery.LayoutParams(500, 500));
				
				//iv.getLayoutParams().height=200;
				//iv.getLayoutParams().width=200;

				iv.setScaleType(ImageView.ScaleType.CENTER);

				// Make sure we set anti-aliasing otherwise we get jaggies

				BitmapDrawable drawable = (BitmapDrawable) iv.getDrawable();

				drawable.setAntiAlias(true);
				
				mImages[i]=iv;
				mImages[i].setClickable(true);
				mImages[i].setId(i);
				
				
				
				//Log.d("SET ITEM:",""+mImages[i].getId());
			}*/
			//createReflectedImages();
		}

		public boolean createReflectedImages() {
			//Toast.makeText(CoverFlowExample.this,"WORKING22222",Toast.LENGTH_LONG ).show();

			// The gap we want between the reflection and the original image

			final int reflectionGap = 4;

			int index = 0;

			for (int imageId : mImageIds) {

				Bitmap originalImage = BitmapFactory.decodeResource(
						getResources(),

						imageId);

				int width = originalImage.getWidth();

				int height = originalImage.getHeight();

				// This will not scale but will flip on the Y axis

				Matrix matrix = new Matrix();

				matrix.preScale(1, -1);

				// Create a Bitmap with the flip matrix applied to it.

				// We only want the bottom half of the image

				Bitmap reflectionImage = Bitmap.createBitmap(originalImage, 0,
						height / 2, width, height / 2, matrix, false);

				// Create a new bitmap with same width but taller to fit
				// reflection

				Bitmap bitmapWithReflection = Bitmap.createBitmap(width

				, (height + height / 2), Config.ARGB_8888);

				// Create a new Canvas with the bitmap that's big enough for

				// the image plus gap plus reflection

				Canvas canvas = new Canvas(bitmapWithReflection);

				// Draw in the original image

				canvas.drawBitmap(originalImage, 0, 0, null);

				// Draw in the gap

				Paint deafaultPaint = new Paint();

				canvas.drawRect(0, height, width, height + reflectionGap,
						deafaultPaint);

				// Draw in the reflection

				canvas.drawBitmap(reflectionImage, 0, height + reflectionGap,
						null);

				// Create a shader that is a linear gradient that covers the
				// reflection

				Paint paint = new Paint();

				LinearGradient shader = new LinearGradient(0,
						originalImage.getHeight(), 0,

						bitmapWithReflection.getHeight() + reflectionGap,
						0x70ffffff, 0x00ffffff,

						TileMode.CLAMP);

				// Set the paint to use this shader (linear gradient)

				paint.setShader(shader);

				// Set the Transfer mode to be porter duff and destination in

				paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));

				// Draw a rectangle using the paint with our linear gradient

				canvas.drawRect(0, height, width,

				bitmapWithReflection.getHeight() + reflectionGap, paint);

				ImageView imageView = new ImageView(mContext);

				imageView.setImageBitmap(bitmapWithReflection);
				
				//imageView.setId(index);
				
				//Log.d("INDEX SET : ",""+index);
				
				//imageView.setOnClickListener(this);
				

				imageView.setLayoutParams(new CoverFlow.LayoutParams(120, 180));

				imageView.setScaleType(ScaleType.MATRIX);

				mImages[index++] = imageView;

			}

			return true;

		}

		public int getCount() {

			return mImageIds.length;

		}

		public Object getItem(int position) {

			return position;

		}

		public long getItemId(int position) {

			return position;

		}

		public View getView(int position, View convertView, ViewGroup parent) {

			// Use this code if you want to load from resources

						ImageView i = new ImageView(mContext);

						i.setImageResource(mImageIds[position]);

						i.setLayoutParams(new CoverFlow.LayoutParams(250, 250));

						i.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
						
						i.setId(pics);
						
						pics++;

						// Make sure we set anti-aliasing otherwise we get jaggies

						BitmapDrawable drawable = (BitmapDrawable) i.getDrawable();

						drawable.setAntiAlias(true);

						return i;

		}

		/**
		 * Returns the size (0.0f to 1.0f) of the views 182. depending on the
		 * 'offset' to the center.
		 */

		public float getScale(boolean focused, int offset) {

			/* Formula: 1 / (2 ^ offset) */

			return Math.max(0, 1.0f / (float) Math.pow(2, Math.abs(offset)));

		}

		

	}

	
	
	
}
