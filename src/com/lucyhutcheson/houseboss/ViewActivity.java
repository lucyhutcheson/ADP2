/*
 * project		HouseBoss
 * 
 * package		com.lucyhutcheson.houseboss
 * 
 * @author		Lucy Hutcheson
 * 
 * date			Sep 19, 2013
 * 
 */
package com.lucyhutcheson.houseboss;


import java.util.HashMap;

import com.lucyhutcheson.libs.NotifyService;
import com.lucyhutcheson.libs.ReminderSingleton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

/**
 * View Activity will show the selected reminder that was saved in the
 * Reminder Singleton.  This activity will allow the user to share the reminder
 * or get to the edit screen.
 */
public class ViewActivity extends Activity {

	public final static String TAG = "VIEWACTIVITY";
	
	@Override
	protected void onNewIntent(Intent intent) {
	    super.onNewIntent(intent);
	    setIntent(intent);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.i(TAG, "STARTING ACTIVITY");
		setContentView(R.layout.activity_view);

		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			int _id = extras.getInt(NotifyService.INTENT_ID);
			Log.i(TAG, Integer.toString(_id));
		} else {

			// ADD DATA FROM SINGLETON TO OUR TEXTVIEWS IN LAYOUT
			HashMap<String, String> _selected = ReminderSingleton.getInstance().get_reminder();
			((TextView) findViewById(R.id.itemTitle)).setText(_selected.get("title"));
			((TextView) findViewById(R.id.itemDescription)).setText(_selected.get("description"));
			((TextView) findViewById(R.id.reminderMonth)).setText(_selected.get("month"));
			((TextView) findViewById(R.id.reminderDay)).setText(_selected.get("day"));
			((TextView) findViewById(R.id.reminderYear)).setText(_selected.get("year"));
			((TextView) findViewById(R.id.reminderTime)).setText(_selected.get("time"));
		
		}
		
	}
	

	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.view_action_bar, menu);
		return true;
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onOptionsItemSelected(android.view.MenuItem)
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle presses on the action bar items
		switch (item.getItemId()) {
		case android.R.id.home:
			ViewActivity.this.finish();
			return true;
		case R.id.action_share:
			onShare();
			return true;
		case R.id.action_edit:
			onEdit();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	/**
	 * IF USER CLICKS SHARE ICON FROM MAIN ACTION BAR
	 */
	public void onShare() {
		Toast.makeText(this, "Share function not yet available.", Toast.LENGTH_SHORT).show();
	}

	/**
	 * IF USER CLICKS EDIT ICON FROM MAIN ACTION BAR
	 */
	public void onEdit() {
		Toast.makeText(this, "Edit function not yet available.", Toast.LENGTH_SHORT).show();
		// INTENT TO START ADD ACTIVITY
		//Intent intent = new Intent(MainActivity.this, AddActivity.class);
		//MainActivity.this.startActivity(intent);
	}

	
}


