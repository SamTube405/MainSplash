package com.samtube405.timeline;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.Tweet;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;

import com.mywork.ui.R;
import com.samtube405.timeline.PullToRefreshListView.OnRefreshListener;

import android.app.ListActivity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;

public class TimelineActivity extends ListActivity{
	private LinkedList<Tweet> mList;
	
	TimelineAdapter mListAdapter;
	
	

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub		
		super.onCreate(savedInstanceState);
        setContentView(R.layout.timeline);
 
        // Set a listener to be invoked when the list should be refreshed.
        ((PullToRefreshListView) getListView()).setOnRefreshListener(new OnRefreshListener() {
            //@Override
            public void onRefresh() {
                // Do work to refresh the list here.
            	Log.d("Start :","Refreshing");
                new GetDataTask().execute();
            }
        });
        
        
        
        mList = new LinkedList<Tweet>();
        //mList.addAll(Arrays.asList(mStrings));
        
        new GetDataTask().execute();

        //mListAdapter = new ArrayAdapter<String>(this,
                //R.layout.mod_simple_list_item_1, mList);
        
        mListAdapter=new TimelineAdapter(this, mList);

        setListAdapter(mListAdapter);
        
        Log.d("Start :","Set List");

	}
	
	private class GetDataTask extends AsyncTask<String, String, String> {
		private LinkedList<Tweet> localList;
		
		
		
		protected void onPreExecute() {
			localList = new LinkedList<Tweet>();
			Log.d("Pre :","Running");
		} 
		
		protected String doInBackground(String... params) {
			Log.d("Thread :","Before");
			//int page=0;
			Twitter twitter = new TwitterFactory().getInstance();
	        try {	            
	            
	            for (int page = 1; page <= 1; page++) {
	            	Query query = new Query("#RedKnotProject");
	                query.setRpp(100); // 100 results per page
	                query.setPage(page);
	                //QueryResult qr = twitter.search(query);
	                //List<Tweet> qrTweets = qr.getTweets();
	            	
	                QueryResult result = twitter.search(query);
	                
	                List<Tweet> tweets = result.getTweets();
	                
	                int resultscount=tweets.size();
	                
	                if(resultscount == 0) break;
	                
	                
	                for (Tweet tweet : tweets) {
	                    Log.d("Tweet :","@" + tweet.getFromUser()+ " - " + tweet.getText());
	                    localList.add(tweet);
	                }
	                
	            } 
	            
	        } catch (TwitterException te) {
	            te.printStackTrace();
	            System.out.println("Failed to search tweets: " + te.getMessage());
	            
	        } catch (Exception ex){
                //localList=null;
                Log.d("Exception during refresh:",ex.toString());
            }
			
			
			Log.d("Thread :","After");
            //return mStrings;
			return null;
		} 
        
 
        protected void onPostExecute(String url) {
        	//super.onPostExecute(null);
        	Log.d("Post :","Running");
        	
        	//mList.addFirst("Added after refresh...");
        	
        	if(localList != null){
        		if(mList.size()<localList.size()){
        			mList.clear();
        			for(Tweet t:localList){
        				mList.add(t);
        			}
        		}
                //mList = localList;
                mListAdapter.notifyDataSetChanged();
                Log.d("LocalList not null :","Inside");
            }
           // Call onRefreshComplete when the list has been refreshed.
           ((PullToRefreshListView) getListView()).onRefreshComplete();
           
           

           //setListAdapter(mListAdapter);

           
        }
        
        protected void onCancelled() {
            // reset the UI
            ((PullToRefreshListView) getListView()).onRefreshComplete();
       }

		
		
		

		
    }

}
