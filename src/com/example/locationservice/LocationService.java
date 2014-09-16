package com.example.locationservice;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings.Secure;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

public class LocationService extends Service implements LocationListener,
GooglePlayServicesClient.ConnectionCallbacks,
GooglePlayServicesClient.OnConnectionFailedListener{
	
	IBinder mBinder = new LocalBinder();
	
	private LocationRequest mLocationRequest;
	
	private String android_id;

    // Stores the current instantiation of the location client in this object
    private LocationClient mLocationClient;
    
    private static LocationService instance = null;
    
    public static int interval=600000;
    public static String name="name";
    public static String version="0";
    public static boolean trace=true;

    public static boolean isInstanceCreated() { 
       return instance != null; 
    }
	
	public int onStartCommand(Intent intent, int flags, int startId) {
	    try {
			version=getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		instance=this;
		android_id=Secure.getString(this.getContentResolver(),
                Secure.ANDROID_ID); 
		mLocationRequest = LocationRequest.create();
		/*
         * Set the update interval
         */
        mLocationRequest.setInterval(interval);

        // Set the interval
        mLocationRequest.setFastestInterval(interval);

        // Use high accuracy
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        
		mLocationClient = new LocationClient(this, this, this);
		mLocationClient.connect();
		
		startForeground(1336, getNotification("Starting service"));
		return(START_NOT_STICKY);
	}
	
	public class LocalBinder extends Binder {
		public LocationService getServerInstance() {
			return LocationService.this;
		}
	}
	
	public void update(){
		
	}
	
	/** Runs when location is updated 
	 */
	@Override
	public void onLocationChanged(Location location) {
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(1336, getNotification("" + location.getLatitude() + "," + location.getLongitude()));
        Log.i("accuracy",""+location.getAccuracy());
        Log.i("altitude",""+location.getAltitude());
        Log.i("bearing",""+location.getBearing());
        Log.i("provider",""+location.getProvider());
        Log.i("speed",""+location.getSpeed());
        submitPHP(location);
	}
	
	/** Set interval
	 * 
	 * @param n Time (in milliseconds) between updates
	 */
	public void setInterval(int n){
		mLocationRequest.setFastestInterval(n);
        mLocationRequest.setInterval(n);
	}

	@Override
	public IBinder onBind(Intent intent) {
		 return mBinder;
	} 
	
	private Notification getNotification(String msg){
		return new NotificationCompat.Builder(this)
		.setSmallIcon(R.drawable.ic_launcher)
		.setContentTitle("Location")
		.setContentText(msg)
        .setTicker("Starting service")
		.setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), 0))
		.build();
	}
	
	public void cancelNotif(){
		((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancelAll();
		stopSelf();
	}
	
	@Override 
	public void onDestroy() {
		instance = null;
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		Log.i("LocationService", "connected");
		mLocationClient.requestLocationUpdates(mLocationRequest, this);
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		// TODO Auto-generated method stub
		
	}
	
	 /**
	 * Sends location over the web
	 * @param location A Location object containing the current location
	 */
	private void submitPHP(Location location) {
        final JSONObject json = new JSONObject();
        try {
        	//android_id
	        json.put("id", android_id);
			json.put("lat", location.getLatitude());
	        json.put("lng", location.getLongitude());
	        json.put("name", name);
	        json.put("ver", version);
	        json.put("trace", trace);
	        json.put("accuracy", location.getAccuracy());
	        json.put("altitude", location.getAltitude());
	        json.put("bearing", location.getBearing());
	        json.put("provider", location.getProvider());
	        json.put("speed", location.getSpeed());
	        JSONArray postjson=new JSONArray();
            postjson.put(json);
 
            // Execute HTTP Post Request
            //HttpResponse response;
            
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        HttpPost httppost = new HttpPost("http://bobdole0.byethost16.com/googlemaps/gmap.php");
                        
                        // Post the data:
                        /*
                        httppost.setHeader("json",json.toString());
                        httppost.getParams().setParameter("data",json.toString());
*/
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("data", json.toString()));

                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        
                		HttpClient httpclient = new DefaultHttpClient();
        				httpclient.execute(httppost);
        				Log.i("fastest interval",""+mLocationRequest.getFastestInterval());        				
        	            Log.i("HTTP", json.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendMarker(String info) {
		markerPHP(info, mLocationClient.getLastLocation());
	}
	
	private void markerPHP(String info, Location location) {
		final JSONObject json = new JSONObject();
		try {
        	//android_id
        	json.put("info", info);
	        json.put("id", android_id);
			json.put("lat", location.getLatitude());
	        json.put("lng", location.getLongitude());
	        JSONArray postjson=new JSONArray();
            postjson.put(json);
 
            // Execute HTTP Post Request
            Log.i("JSON", json.toString()); 
            //HttpResponse response;
            
            new Thread(new Runnable(){
                @Override
                public void run() {
                    try {
                        HttpPost httppost = new HttpPost("http://bobdole0.byethost16.com/googlemaps/gmap.php");
                        
                        // Post the data:
                        /*
                        httppost.setHeader("json",json.toString());
                        httppost.getParams().setParameter("data",json.toString());
*/
                        List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(1);
                        nameValuePairs.add(new BasicNameValuePair("marker", json.toString()));

                        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                        
                		HttpClient httpclient = new DefaultHttpClient();
        				httpclient.execute(httppost); 
        	            Log.i("HTTP", json.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).start();
            
            
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
