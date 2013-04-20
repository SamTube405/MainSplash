package com.mywork.place;

import java.io.Serializable;
import java.util.List;

import com.google.api.client.util.Key;

public class PlacesDetailsList implements Serializable{
	
	@Key
	public String status;
	
	@Key
	public List<PlaceDetails> details;
	
	

}
