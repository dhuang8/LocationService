package com.example.locationservice;

import com.example.locationservice.LocationService.LocalBinder;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends FragmentActivity {
	
	private boolean mBounded;
	private Intent mIntent;
	private LocationService mService;
	private SharedPreferences prefs;
	private MyArrayAdapter adapter;
	
	/**
	 * Starts the location service
	 */
	public void startLocationService(){
		//Toast.makeText(getApplicationContext(), "Service has begun", Toast.LENGTH_LONG).show();
		Log.i("Main", "Service starting");
		startService(mIntent);
	}
	
	/**
	 * Stops the location service
	 */
	public void stopLocationService(){
		//Toast.makeText(getApplicationContext(), "Service has begun", Toast.LENGTH_LONG).show();
		Log.i("Main", "Service stopping");
		mService.cancelNotif();
//		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
		stopService(mIntent);
	}
	
	/**
	 * Stops the location service
	 */
	public void stopLocationService(View v){
		Log.i("Main", "Service stopping");
		stopService(mIntent);
	}
	
	public void openMap(){
		startActivity(new Intent(this,MapMainActivity.class));
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	    prefs = this.getPreferences(Context.MODE_PRIVATE);
		
		//final ListView listview = (ListView) findViewById(R.id.list);
	    /*String[] values = new String[] { "Location service", "Send location now", "Set update interval", 
	    		"Add marker at current location", "Set name", "Tracing", "View map"};
	    */

	    String[] values = new String[] { "Location service", "Send location now", "Set update interval", "View map"};

	    adapter = new MyArrayAdapter(this, android.R.layout.simple_list_item_1, values);
	    ListView list = (ListView) findViewById(R.id.list);
	    list.setAdapter(adapter);
	    list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
	        @Override
	        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	        	switch (position){
	    		case 1:
	    			if (LocationService.isInstanceCreated()){
		    			startLocationService();
	    			}
	    			else {
	    				Toast.makeText(getApplicationContext(), "Service is off", Toast.LENGTH_SHORT).show();
	    			}
	    			break;
	    		case 2:
	    			DialogFragment dialog = new IntervalDialog();
	    			dialog.show(getSupportFragmentManager(), "tag");
	    			break;
	    		/*case 3:
	    			if (LocationService.isInstanceCreated()){
	    				mService.sendMarker("here is a marker");
	    			}
	    			break;*/
	    		case 3:
	    			openMap();
	    			break;
	    		}
	        }
	
	    });
	    updateAdapter();
	}
	
	public void updateAdapter(){
		adapter.switchcheck=LocationService.isInstanceCreated();
		adapter.intervaltime=getPreference("interval");
		adapter.notifyDataSetChanged();
	}
	
	 @Override
	 protected void onStart() {
		 super.onStart();
		 mIntent = new Intent(this,LocationService.class);
		 bindService(mIntent, mConnection, BIND_AUTO_CREATE);
	 };
	 
	 @Override
	 protected void onStop() {
		 super.onStop();
		 if(mBounded) {
			 unbindService(mConnection);
			 mBounded = false;
		 }
	 };
	 
	 public int getPreference(String s){
		 return prefs.getInt(s, 60000);
	 }
	 
	 public void setInterval(int n){
		 prefs.edit().putInt("interval", n).apply();
		 if (LocationService.isInstanceCreated()){
			 mService.setInterval(n);
		 }
		 updateAdapter();
	 }

    protected void onListItemClick(ListView l, View v, int position, long id) {
		switch (position){
		case 1:
			startLocationService();
			updateAdapter();
			break;
		case 2:
			DialogFragment dialog = new IntervalDialog();
			dialog.show(getSupportFragmentManager(), "tag");
			updateAdapter();
			break;
		case 3:
//			mService.sendMarker();
			break;
		case 4:
			startActivity(new Intent(this,MapMainActivity.class));
			break;
		}
    }	

/*	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	*/
	
	ServiceConnection mConnection = new ServiceConnection() {
		  
		public void onServiceDisconnected(ComponentName name) {
			mBounded = false;
			mService = null;
		}
		  
		public void onServiceConnected(ComponentName name, IBinder service) {
			LocationService.interval=getPreference("interval");
			mBounded = true;
			LocalBinder mLocalBinder = (LocalBinder)service;
			mService = mLocalBinder.getServerInstance();
		}
	};
}
