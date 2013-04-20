package com.samtube405.gallery;



import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.mywork.ui.R;
import com.samtube405.singleplace.InboxActivity;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;

public class ImageAdapter extends BaseAdapter {
	private Context mContext;
	//public Integer[] mThumbIds;
	public Bitmap thumbs[];
	
	
	// Constructor
	public ImageAdapter(Context c){
		mContext = c;
	}
	
	// Constructor
	public ImageAdapter(Context c, Bitmap thumbs[]){
			mContext = c;
			
			if(thumbs==null){
				this.thumbs=new Bitmap[1];
				this.thumbs[0]=BitmapFactory.decodeResource(mContext.getResources(), R.drawable.noimages);
			}else{
				this.thumbs=new Bitmap[thumbs.length];	
				this.thumbs=thumbs;
			}
					
			
			
			
			
			
		}


	@Override
	public int getCount() {
		return thumbs.length;
	}

	@Override
	public Object getItem(int position) {
		return thumbs[position];
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {			
		ImageView imageView = new ImageView(mContext);
        //imageView.setImageResource(mThumbIds[position]);
        imageView.setImageBitmap(thumbs[position]);
        
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        
        imageView.setLayoutParams(new GridView.LayoutParams(125, 125));
        
        return imageView;
	}
	
	
	
	
	

	}

