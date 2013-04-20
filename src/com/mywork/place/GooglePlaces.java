package com.mywork.place;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;

import org.codehaus.jackson.JsonFactory;

//import android.nfc.NfcAdapter.CreateNdefMessageCallback;
import android.util.Log;

import com.google.api.client.googleapis.GoogleHeaders;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpRequestInitializer;
import com.google.api.client.http.HttpResponseException;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
//import com.google.api.client.http.json.*;
import com.google.api.client.http.json.JsonHttpParser;
import com.google.api.client.json.JsonGenerator;
import com.google.api.client.json.JsonParser;
import com.google.api.client.json.jackson.JacksonFactory;


public class GooglePlaces {

	final static HttpTransport HTTP_TRANSPORT=new NetHttpTransport();
	final static String API_KEY="AIzaSyAG1WTuxDSadv-Bd9s8RfYOPwESYwsUHEw";
	
	private static final String PLACES_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_TEXT_SEARCH_URL = "https://maps.googleapis.com/maps/api/place/search/json?";
    private static final String PLACES_DETAILS_URL = "https://maps.googleapis.com/maps/api/place/details/json?";
    
    
	double _latitude,_longitude,_radius;
	
	/**
     * Searching places
     * @param latitude - latitude of place
     * @params longitude - longitude of place
     * @param radius - radius of searchable area
     * @param types - type of place to search
     * @return list of places
     * */
	
	public PlaceList search(double latitude,double longitude,double radius,String types) throws Exception{
		this._latitude=latitude;
		this._longitude=longitude;
		this._radius=radius;
		
		try{
			
			HttpRequestFactory httpReqFac= createRequestFactory(HTTP_TRANSPORT);
			HttpRequest request=httpReqFac.buildGetRequest(new GenericUrl(PLACES_SEARCH_URL));
			
			request.getUrl().put("key", API_KEY);
			request.getUrl().put("location",_latitude+","+_longitude);
			request.getUrl().put("radius",_radius);
			request.getUrl().put("sensor",false);
			
			if(types!=null){
				
				request.getUrl().put("types",types );
				
				PlaceList list=request.execute().parseAs(PlaceList.class);
				
				 // Check log cat for places response status
	            Log.d("Places Status", "" + list.status);
	            return list;
				
			}
			return null;
			
		}catch(HttpResponseException e){
			Log.e("Error",e.getMessage());
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
				
				JsonHttpParser parser=new JsonHttpParser(new JacksonFactory());
				request.addParser(parser);
			}
		});
		
		
	}
	
	public PlaceDetails getPlaceDetails(String reference) throws Exception {
        try {
 
            HttpRequestFactory httpRequestFactory = createRequestFactory(HTTP_TRANSPORT);
            HttpRequest request = httpRequestFactory
                    .buildGetRequest(new GenericUrl(PLACES_DETAILS_URL));
            request.getUrl().put("key", API_KEY);
            request.getUrl().put("reference", reference);
            request.getUrl().put("sensor", "false");
 
            PlaceDetails place = request.execute().parseAs(PlaceDetails.class);
 
            return place;
 
        } catch (HttpResponseException e) {
            Log.e("Error in Perform Details", e.getMessage());
            throw e;
        }
    }
}
