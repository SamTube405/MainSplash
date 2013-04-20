package com.redknot.summary;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.CategorySeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.mywork.ui.R;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class BarChart extends Activity implements OnItemSelectedListener {

	GraphicalView mChart;
	CategorySeries series = new CategorySeries("Bar Graph");
	XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	XYSeries mCurrentSeries;
	XYSeriesRenderer mCurrentRenderer = new XYSeriesRenderer();
	LinearLayout layout;
	String type;

	JSONParser jParser = new JSONParser();
	JSONObject json;

	ProgressDialog pDialog;

	private static final String url = "http://redknot.ckreativity.com/android_connect/get_place_counts.php";
	private static final String daysurl = "http://redknot.ckreativity.com/android_connect/get_place_counts_for_days.php";

	private static final String TAG_SUCCESS = "success";
	private static final String TAG_BY_COUNT = "bycount";
	private static final String TAG_BY_RATING = "byrating";
	private static final String TAG_NAME = "name";
	private static final String TAG_COUNT = "count";
	private static final String TAG_RATING = "rating";
	private static final String TAG_BY_COUNT_TOTAL = "counttotal";
	private static final String TAG_BY_RATING_TOTAL = "ratingtotal";

	JSONArray count;
	JSONArray rating;

	Vector<Integer> counts = new Vector<Integer>();
	Vector<Integer> ratings = new Vector<Integer>();
	Vector<String> countnames = new Vector<String>();
	Vector<String> ratingnames = new Vector<String>();

	Spinner daysSpin, countSpin;

	ArrayAdapter<String> countAdapter, daysAdapter;

	protected String dayindicator = "Up to today";

	int globalcounter1, globalcounter2 = 5;

	boolean trigger1, trigger2 = false;

	int pcount = 0;

	boolean noresults = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bar_chart);

		daysSpin = (Spinner) findViewById(R.id.daysspinner);
		countSpin = (Spinner) findViewById(R.id.countspinner);

		Intent i = getIntent();
		type = i.getStringExtra("type").toLowerCase();

		Log.d("GOT TYPE", type);

		layout = (LinearLayout) findViewById(R.id.barchart);
		// init();
		initChart();
		new GetPlaces().execute();
		// addSampleData();

	}

	public void init() {
		List<String> daysOptions = new ArrayList<String>();

		daysOptions.add("Up to today");
		daysOptions.add("Last three days");
		daysOptions.add("Last seven days");

		daysAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, daysOptions);
		daysAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		daysSpin.setAdapter(daysAdapter);
		// daysSpin.setSelection(0);

		List<String> countOptions = new ArrayList<String>();

		countOptions.add("Ratings");
		countOptions.add("No of visits");

		ArrayAdapter<String> countAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item, countOptions);
		countAdapter
				.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

		countSpin.setAdapter(countAdapter);
		// countSpin.setSelection(0);

		daysSpin.setOnItemSelectedListener(this);
		countSpin.setOnItemSelectedListener(this);

		/*
		 * daysSpin.post(new Runnable() { public void run() {
		 * daysSpin.setOnItemSelectedListener(BarChart.this); } });
		 * countSpin.post(new Runnable() { public void run() {
		 * countSpin.setOnItemSelectedListener(BarChart.this); } });
		 */

	}

	public void initChart() {

		mCurrentSeries = new XYSeries("Total");
		mDataset.addSeries(mCurrentSeries);

		// mCurrentRenderer=new XYSeriesRenderer();

		mCurrentRenderer.setDisplayChartValues(true);
		mCurrentRenderer.setChartValuesSpacing((float) 0.5);
		// mCurrentRenderer.setLineWidth((float)1.0);
		mCurrentRenderer.setColor(Color.CYAN);

		mRenderer.addSeriesRenderer(mCurrentRenderer);

		mRenderer.setChartTitle("Top Places For " + type.toUpperCase());
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setMarginsColor(Color.BLACK);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setZoomEnabled(true);
		mRenderer.setZoomButtonsVisible(true);
		mRenderer.setBarSpacing(0.10);
		mRenderer.setXTitle("Places");
		mRenderer.setYTitle("Hits");

	}

	public void triggerGraph() {

		if (!noresults) {
			String filter = countSpin.getSelectedItem().toString();

			if (filter.equals("Ratings")) {

				if (globalcounter2 > 0) {

					for (int i = 0; i < globalcounter2; i++) {

						mCurrentSeries.add(i + 1, ratings.get(i));
						mRenderer.addXTextLabel(i + 1, ratingnames.get(i));
					}

				} else {

					AlertDialogManager alert = new AlertDialogManager();

					alert.showAlertDialog(
							BarChart.this,
							"No Places Found",
							"No places found for this selection.will switch to other selection",
							false);

					countSpin.setSelection(1);
					triggerGraph();

				}

			} else if (filter.equals("No of visits")) {

				if (globalcounter1 > 0) {

					for (int i = 0; i < globalcounter1; i++) {

						mCurrentSeries.add(i + 1, counts.get(i));
						mRenderer.addXTextLabel(i + 1, countnames.get(i));
					}
				} else {

					AlertDialogManager alert = new AlertDialogManager();

					alert.showAlertDialog(
							BarChart.this,
							"No Places Found",
							"No places found for this selection.will switch to other selection",
							false);

					countSpin.setSelection(0);
					triggerGraph();
				}
			}

			/*
			 * if (mChart != null) {
			 * 
			 * layout.removeView(mChart);
			 * 
			 * // mChart = ChartFactory.getBarChartView(this, mDataset, //
			 * mRenderer, // Type.DEFAULT);
			 * 
			 * mChart.repaint();
			 * 
			 * layout.addView(mChart); } else {
			 */
			//layout.removeView(mChart);
			layout.removeAllViews();
			mChart = ChartFactory.getBarChartView(this, mDataset, mRenderer,Type.DEFAULT);
			layout.addView(mChart);
			// }

		}/*else{
			
			Log.d("EXECUTE ","OK,THIS EXECUTES PERFECTLY!!!");
			layout.removeAllViews();
			mChart = ChartFactory.getBarChartView(this, mDataset, mRenderer,
					Type.DEFAULT);
			//mChart.repaint();
			layout.addView(mChart);
			
		}*/

	}

	public void setGraphData() {

		try {

			int success = json.getInt(TAG_SUCCESS);

			if (success == 1) {

				Log.d("SUCCESS", "JSON ARRAY IS OKKKKKK");

				globalcounter1 = json.getInt(TAG_BY_COUNT_TOTAL);

				Log.d("NO OF PLACES FOUND BY COUNT: ", "" + globalcounter1);

				if (globalcounter1 > 0) {
					count = json.getJSONArray(TAG_BY_COUNT);

					for (int i = 0; i < globalcounter1; i++) {
						JSONObject r1 = count.getJSONObject(i);

						int tcount = r1.getInt(TAG_COUNT);
						String cname = r1.getString(TAG_NAME);
						counts.add(i, tcount);
						countnames.add(i, cname);

						Log.d("COUNT - VALS :", cname + "->" + tcount);

					}
				} else {
					
					// FINETUNING
				}

				globalcounter2 = json.getInt(TAG_BY_RATING_TOTAL);

				Log.d("NO OF PLACES FOUND BY RATING: ", "" + globalcounter2);
				if (globalcounter2 > 0) {
					rating = json.getJSONArray(TAG_BY_RATING);
					for (int i = 0; i < globalcounter2; i++) {
						JSONObject r2 = rating.getJSONObject(i);

						int trating = r2.getInt(TAG_RATING);
						String rname = r2.getString(TAG_NAME);

						ratings.add(i, trating);
						ratingnames.add(i, rname);

						Log.d("RATING - VALS :", rname + "->" + trating);

					}
				} else {

					
					// FINETUNING
				}
				
				noresults = false;

			} else {

				Log.d("Results: ", "No Results");

				ratings.clear();
				ratingnames.clear();
				counts.clear();
				countnames.clear();

				AlertDialogManager alert = new AlertDialogManager();

				alert.showAlertDialog(BarChart.this, "No Places Found",
						"No places found for this selection", false);

				noresults = true;
				
				Log.d("CALLING EARLY","I,M REPAINTING EARLY");
				
				layout.removeAllViews();
				
				TextView t=new TextView(BarChart.this);
				t.setText("Sorry, no results. Try another Combination");
				t.setTextSize((float)20.0);
				t.setGravity(Gravity.CENTER);
				
				layout.addView(t);
				
			}

		} catch (Exception e) {

			e.printStackTrace();
		}

	}

	class GetPlaces extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			pDialog = new ProgressDialog(BarChart.this);
			pDialog.setMessage("Loading Data. Please wait...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(false);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... arg0) {
			// TODO Auto-generated method stub

			pcount++;

			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("type", type));

			if (!(dayindicator.equals("Up to today"))) {

				Log.d("******************* HUGE ", "DAYS FILTER CALLED");

				Date d = new Date();
				String date = "";

				Calendar c = Calendar.getInstance();
				c.setTime(d);

				if (dayindicator.equals("Last three days")) {
					c.add(Calendar.DAY_OF_YEAR, -3);
				} else if (dayindicator.equals("Last seven days")) {
					c.add(Calendar.DAY_OF_YEAR, -3);
				}

				Date newdate = c.getTime();
				date = new SimpleDateFormat("yyyy-MM-dd").format(newdate);
				params.add(new BasicNameValuePair("date", date));

				Log.d("MINIMUM DATE I NEED : ", date);

				json = jParser.makeHttpRequest(daysurl, "GET", params);

			}

			// Log.d("PARAMS", params.toString());
			else {
				Log.d("******************* NOT SO HUGE ", "NORMAL JSON");

				json = jParser.makeHttpRequest(url, "GET", params);

			}

			Log.d("JSON RESPONSE", json.toString());

			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			pDialog.dismiss();

			runOnUiThread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub

					if (pcount == 1) {
						setGraphData();
						init();
						triggerGraph();
					} else {

						setGraphData();
						triggerGraph();
					}

				}
			});
		}

	}

	@Override
	public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
			long arg3) {
		// TODO Auto-generated method stub

		if (arg0 == countSpin) {
			// Toast.makeText(BarChart.this,"THIS IS OK :D",
			// Toast.LENGTH_SHORT).show();

			// x++;
			if (!trigger1) {
				trigger1 = true;

			} else {
				triggerGraph();
			}

		} else if (arg0 == daysSpin) {
			// Toast.makeText(BarChart.this,"Wait Dude, This is not Implemented Yet!!!",
			// Toast.LENGTH_SHORT).show();
			// x++;

			if (!trigger2) {
				trigger2 = true;

			} else {

				dayindicator = daysSpin.getItemAtPosition(arg2).toString();
				Toast.makeText(BarChart.this, "You Selected " + dayindicator,
						Toast.LENGTH_SHORT).show();
				// if(){
				new GetPlaces().execute();
			}
			// }
		}

	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		// TODO Auto-generated method stub

	}

}
