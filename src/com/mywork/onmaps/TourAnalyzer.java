package com.mywork.onmaps;

import com.mywork.ui.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class TourAnalyzer extends Activity{

	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.tour_analyzer);
		
		tv=(TextView) findViewById(R.id.touranalyzermaintv);
	}
	
}
