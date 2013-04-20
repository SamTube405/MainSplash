package com.samtube405.timeline;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Date;
import java.util.LinkedList;

import twitter4j.Tweet;

import com.mywork.ui.R;





import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TimelineAdapter extends BaseAdapter{
	
	private Activity activity;
	private LinkedList<Tweet> data;
	private LayoutInflater inflater;
	private ImageLoader imageLoader;
	
	Date now=new Date();

	public TimelineAdapter(Activity a,LinkedList<Tweet> l){
		activity = a;		
        data=l;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader=new ImageLoader(activity.getApplicationContext());
		
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return data.size();
	}

	public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View vi=convertView;
        if(convertView==null)
            vi = inflater.inflate(R.layout.mod_simple_list_item_1, null);

        TextView tweet = (TextView)vi.findViewById(R.id.tvTweet); // title
        TextView tuser = (TextView)vi.findViewById(R.id.tvuser_name); // artist name
        TextView tdate = (TextView)vi.findViewById(R.id.tvTdate); // duration
        ImageView thumb_image=(ImageView)vi.findViewById(R.id.list_image); // thumb image
        
        
        Tweet t = data.get(position);
        
        
        
        
        
        tweet.setText(t.getText());
        
        tuser.setText("@"+t.getFromUser());
        
        tdate.setText(getTimeFlag(t.getCreatedAt()));
        
        /*URL url;Bitmap bmp=null;
		try {
			url = new URL(t.getProfileImageUrl());
			
			bmp = BitmapFactory.decodeStream(url.openConnection() .getInputStream());   
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(bmp!=null) thumb_image.setImageBitmap(bmp);
		else{
			thumb_image.setImageDrawable(activity.getResources().getDrawable(R.drawable.noimages));
		}*/
        
        imageLoader.DisplayImage(t.getProfileImageUrl(), thumb_image);
        
        return vi;
	}
	
	public String getTimeFlag(Date tdate){	
		String flag="";
		long difference = now.getTime() - tdate.getTime(); 
		int sec=(int) (difference / 1000);  
		int min = sec/60;  
		int hours = min/60; 
		int days = hours/24;
		
		if(days>0) flag=String.valueOf(days)+" days";
		else if(hours>0) flag=String.valueOf(hours)+" hours";
		else if(min>0) flag=String.valueOf(min)+" mins";
		else{
			flag=String.valueOf(sec)+" secs";
		}
		
		Log.d("Tweet date : ",tdate.toString()+"   "+now.toString());
		
		if(!flag.isEmpty()) flag+=" ago";	
		
		
		return flag;
	}

}
