package com.mywork.ui;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

public class Intelligence extends Activity{

	TextView tv;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.intelligence_tour);
		
		tv=(TextView) findViewById(R.id.intelligencetv);
	}

}
