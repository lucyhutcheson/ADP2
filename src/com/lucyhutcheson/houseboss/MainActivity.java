/*
 * project		HouseBoss
 * 
 * package		com.lucyhutcheson.houseboss
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Sep 11, 2013
 * 
 */
package com.lucyhutcheson.houseboss;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import com.lucyhutcheson.libs.AppPreferences;
import com.lucyhutcheson.libs.FileFunctions;
import com.lucyhutcheson.libs.GetDataService;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Main Class that handles the weather display as well as the list
 * of reminders that the user has saved.
 */
public class MainActivity extends Activity {
	
	// VARIABLES SETUP
	public static final String TAG = "MainActivity";
	private AppPreferences _appPrefs;
	static Context _context;
	String _zipCode;
	ListView _reminderList;
	SimpleAdapter _adapter;
	ArrayList<HashMap<String, String>> _reminderArrayList;
	static final String[] _from = new String[] { "title", "month", "day", "year", "hour", "minute"};
	static final int[] _to = new int[] { R.id.reminderTitle, R.id.reminderMonth, R.id.reminderDay, R.id.reminderYear, R.id.reminderHour, R.id.reminderMinute};

	
	// Handle communication between this activity and
	// GetDataService class
	@SuppressLint("HandlerLeak")
	Handler searchServiceHandler = new Handler() {

		public void handleMessage(Message mymessage) {

			Log.i(TAG, "HANDLER STARTED");

			if (mymessage.arg1 == RESULT_OK	&& mymessage.obj != null) {
		        //Log.i("RESPONSE", mymessage.obj.toString());
				JSONObject json = null;
				try {
					json = new JSONObject(mymessage.obj.toString());
				} catch (JSONException e) {
					e.printStackTrace();
				}
				//Log.i("UPDATE WITH JSON", json.toString());
				updateWeather(json);
				
			} else if (mymessage.arg1 == RESULT_CANCELED && mymessage.obj != null){
				Toast.makeText(MainActivity.this,mymessage.obj.toString(), Toast.LENGTH_LONG).show();

			} else {
				Toast.makeText(MainActivity.this,"Download failed.", Toast.LENGTH_LONG).show();
			}	
		}

	};
	
	/**
	 * Updates all weather textviews with received JSON data.
	 * 
	 * @param data
	 *            the data
	 */
	public void updateWeather(JSONObject data) {
		//Log.i("UPDATE DATA", data.toString());
		try {
			JSONObject city = data.getJSONObject("display_location");
			String forecast = data.getString("temp_f");
			//Log.i(TAG, forecast);
			((TextView) findViewById(R.id.forecast)).setText(forecast +  (char) 0x00B0);
			((TextView) findViewById(R.id.city)).setText(city.getString("full"));

		} catch (JSONException e) {
			e.printStackTrace();
			Log.e("JSON ERROR", e.toString());
		}
	}
	
	
	@Override
	public void onStart() {
		super.onStart();
		
		try {
			Log.i("VIEW ACTIVITY", "TRYING");
			try {
				_reminderArrayList = getSavedReminders();
				Log.i(TAG, _reminderArrayList.toString());
				
			} catch (Exception e) {
				Log.e(TAG, "Error getting reminders");
				e.printStackTrace();
			}
			
			if (_reminderArrayList != null) {
				// ATTACH LIST ADAPTER
				_reminderList = (ListView) findViewById(R.id.listview);
				SimpleAdapter _myAdapter = new SimpleAdapter(_context, _reminderArrayList, R.layout.activity_main_row, _from, _to);
				_reminderList.setAdapter(_myAdapter);
				((TextView) findViewById(R.id.empty)).setVisibility(View.GONE);
			}
		} catch (Exception e) {
			Log.e(TAG, "GET REMINDERS ERROR");
	        Toast.makeText(_context, "No saved reminders.", Toast.LENGTH_SHORT).show();
		}
	}

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		_context = this;
		
		// GET WEATHER DATA BASED ON SAVED ZIP CODE IN SHARED PREFS
		_appPrefs = new AppPreferences(getApplicationContext());
		if (_appPrefs.getZip().length() > 0) {
			
			// GET OUR ZIP CODE
			_zipCode = _appPrefs.getZip();
			
			// GET WEATHER INFORMATION BASED ON ZIP CODE IN SHARED PREFERENCES
			Messenger messenger = new Messenger(searchServiceHandler);
			Intent startServiceIntent = new Intent(getApplicationContext(), GetDataService.class);
			startServiceIntent.putExtra(GetDataService.MESSENGER_KEY,messenger);
			startServiceIntent.setData(Uri.parse("http://api.wunderground.com/api/c6dc8ff98c36bc6c/conditions/q/"+Uri.encode(_zipCode)+".json"));
			startService(startServiceIntent);
			
		} else {
    		Log.i(TAG, "Zip field is empty.");
			
    		// ALERT USER THAT THERE IS NO ZIP SAVED
			Toast.makeText(this, "No zip code saved. Please add one in app Settings.", Toast.LENGTH_LONG).show();
		}
		
	}

	/**
	 * Function to get read the favorites file which contains any dining data
	 * that was saved as a favorite.
	 * 
	 * @return hashmap of our favorites data
	 */
	@SuppressWarnings({ "unchecked", "unused" })
	public static ArrayList<HashMap<String, String>> getSavedReminders() {
		Log.i("GET SAVED REMINDERS", "TRYING");
		
		
		/* 
		 * GET STORED DATA FROM REMINDERS FILE
		 */
		Object stored = FileFunctions.readObjectFile(_context, AddActivity.REMINDER_FILENAME, false);
		Log.i(TAG, stored.toString());
		ArrayList<HashMap<String, String>> _remindersList = null;

		// CHECK IF OBJECT EXISTS
		if (stored == null) {
			Log.i("HOUSEBOSS", "NO REMINDERS FILE FOUND");
			_remindersList = new ArrayList<HashMap<String, String>>();
		}
		// IF OBJECT EXISTS, BRING IN DATA AND ADD TO HASHMAP
		else {
			// CAST HASHMAP
			_remindersList = (ArrayList<HashMap<String, String>>) stored;
		}
		return _remindersList;

		
		/*
		// CAST ARRAYLIST
		try {
			_remindersList = (ArrayList<HashMap<String, String>>) stored;
			Log.i(TAG, _remindersList.toString());
		} catch (Exception e) {
			Log.e("REMINDERS FOUND","ERROR");
			e.printStackTrace();
		}
				
		return _remindersList;*/
	}
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main_action_bar, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case R.id.action_new:
			onAddActivity();
			return true;
		case R.id.action_settings:
			onSettingsActivity();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public void onAddActivity() {
		// INTENT TO START ADD ACTIVITY
		Intent intent = new Intent(MainActivity.this, AddActivity.class);
		MainActivity.this.startActivity(intent);
	}

	public void onSettingsActivity() {
		Toast.makeText(this, "Settings screen not yet available.", Toast.LENGTH_SHORT).show();

		
		// INTENT TO START ADD ACTIVITY
		//Intent intent = new Intent(MainActivity.this, AddActivity.class);
		//MainActivity.this.startActivity(intent);
	}

}
