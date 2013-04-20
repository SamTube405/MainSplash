package com.samtube405.gallery;



import java.util.HashMap;

import com.mywork.ui.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class FullImageActivity extends Activity {
	
	
	private HashMap<String, String> image;
	
	public Bitmap thumb;

	public static final String TAG_UP_DATE = "uploaded_time";
	public static final String TAG_USER_NAME = "user_name";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.full_image);
		
		TextView tvDetail=(TextView)findViewById(R.id.tvDetail);
		
		// get intent data
		Intent i = getIntent();
		
		// Selected image id
		//int position = i.getExtras().getInt("id");
		
		thumb=(Bitmap)i.getParcelableExtra("thumb");
		
		image=(HashMap<String,String>)i.getSerializableExtra("image");
		
		//ImageAdapter imageAdapter = new ImageAdapter(this);
		
		ImageView imageView = (ImageView) findViewById(R.id.full_image_view);
		
		imageView.setImageBitmap(thumb);
		
		tvDetail.setText("Image uploaded by "+image.get(TAG_USER_NAME)+" at "+image.get(TAG_UP_DATE));
		
		
	}

}
