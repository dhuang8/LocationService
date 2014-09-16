package com.example.locationservice;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Iterator;

import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

public class MapMainActivity extends FragmentActivity {
	private Handler mHandler;
	GoogleMap mMap;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_main);
		mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
		mHandler = new Handler();
		
	    new Thread(new Runnable(){
		    @Override
		    public void run() {
		        try {
		        	HttpGet httpget = new HttpGet ("http://bobdole0.byethost16.com/googlemaps/gmap.php?callback=a&get=locdata");
		            
		            // Post the data:
		            /*
		            httppost.setHeader("json",json.toString());
		            httppost.getParams().setParameter("data",json.toString());
	*/
                    
		    		HttpClient httpclient = new DefaultHttpClient();
		            ResponseHandler<String> responseHandler = new BasicResponseHandler();
					String s = httpclient.execute(httpget,responseHandler);
					final String str=s.substring(2,s.length()-1);
					Log.e("HTTP", str);
					

					mHandler.post(new Runnable() {
						public void run() {
							try {
								LatLngBounds.Builder bounds = new LatLngBounds.Builder();
								JSONObject json = new JSONObject(str);
								Iterator<String> iter = json.keys();
							    while (iter.hasNext()) {
							        final String key = iter.next();
							        try {
							        	final JSONObject value = json.getJSONObject(key);
										Log.e("HTTP", ""+value.getDouble("lat")+","+value.getDouble("lng"));
										
										
										SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
										String time = sdf.format(new Date(value.getLong("time")*1000));
										addMarkerToMap(value.getDouble("lat"),value.getDouble("lng"),key
												+ "\n"+time);
										bounds.include(new LatLng(value.getDouble("lat"),value.getDouble("lng")));
							        } catch (JSONException e) {
										Log.i("MMA", "error");
							            // Something went wrong!
							        }
							    }
							    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(bounds.build(),100));
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					});
					
					
		        } catch (Exception e) { 
		            e.printStackTrace();
		        }
		    }
		}).start();
	}
	
	public void addMarkerToMap(double lat, double lng, String text){
		mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lng)).title(text));
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.map_main, menu);
		return true;
	}

	@Override
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

	/**
	 * A placeholder fragment containing a simple view.
	 */
	public static class PlaceholderFragment extends Fragment {

		public PlaceholderFragment() {
		}

		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			View rootView = inflater.inflate(R.layout.map_fragment_main,
					container, false);
			return rootView;
		}
	}

}
