package com.mywork.db;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.JsonObjectParser;
import com.google.api.client.json.jackson.JacksonFactory;
import com.mywork.place.PlaceList;
import com.samtube405.singleplace.JSONParser;

public class PlacesFromDb {
	
	JSONParser jp= new JSONParser();
	final static HttpTransport HTTP_TRANSPORT=new NetHttpTransport();
	
	private static final String url="http://redknot.ckreativity.com/android_connect/filter_places.php";
	
	
	int found_places=0;
	
	
	public PlaceList search(double lat,double longi,double radius,String types){
		
		try{
			
			HttpRequestFactory httpReqFac= createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request=httpReqFac.buildGetRequest(new GenericUrl(url));
			
			request.getUrl().put("clat", Double.toString(lat));
			request.getUrl().put("clong", Double.toString(longi));
			request.getUrl().put("radius", Double.toString(radius));
			request.getUrl().put("prefs",types);
			
			
			if(types!=null){
				
				PlaceList list=request.execute().parseAs(PlaceList.class);
				
				if(list.results.size()>0){
					
					this.found_places=list.results.size();
					
					return list;
					
				}
				
				return null;
			}
			
			return null;
			
		}catch(Exception e){
			e.printStackTrace();
			return null;
		}
		
	}
	
	
	
	public static HttpRequestFactory createRequestFactory(final HttpTransport transport){
		return transport.createRequestFactory(new HttpRequestInitializer() {
			
			@Override
			public void initialize(HttpRequest request) throws IOException {
				// TODO Auto-generated method stub
				GoogleHeaders header=new GoogleHeaders();
				header.setApplicationName("My Places App");
				request.setHeaders(header);
				
				JsonObjectParser parser = new JsonObjectParser(new JacksonFactory());
				request.setParser(parser);
			}
		});
		
		
	}
	
	public int getNumberOfPlaces(){
		
		return this.found_places;
	}

	

}
