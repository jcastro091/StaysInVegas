package com.majestyk.vegas;

import org.apache.http.client.CookieStore;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {

	public static final String PREFS_NAME = "VegasPrefs";
	
	public DefaultHttpClient httpclient;
	public HttpContext localContext;

	View.OnClickListener ocl;

	private double lat = 0.0;
	private double lon = 0.0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		FontManager.loadFont(getAssets());
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (settings.getString("logged", "").equals("logged")) {
        	
			GPSTracker gps = new GPSTracker(MainActivity.this);

            // check if GPS enabled       
            if(gps.canGetLocation()) {

                lat = gps.getLatitude();
                lon = gps.getLongitude();
               
        		Log.i("GPS", lon + " " + lat);
        		
            } else {
                gps.showSettingsAlert();
            }

        	new API_Login(this, new OnTaskCompleteListener() {
        		@Override
        		public void onComplete(boolean success) {
        			if(success) {
        				Intent intentCall;
						intentCall = new Intent(getApplicationContext(), ChatActivity_Home.class);
						startActivity(intentCall);					
					}
				}
			}).execute(
					settings.getString("username", ""), 
					settings.getString("password", ""),
					settings.getString("deviceID", ""),
					Double.toString(lat), 
					Double.toString(lon)
			);
    	} 

		httpclient = new DefaultHttpClient();
		localContext = new BasicHttpContext();
		GlobalValues.httpclient = httpclient;
		GlobalValues.localContext = localContext;
		CookieStore cookieStore = new BasicCookieStore();
		localContext.setAttribute(ClientContext.COOKIE_STORE, cookieStore);
		
		ocl = new OnClickListener() {
			public void onClick(View v) {
				Intent intentCall;
				switch(v.getId()) {
				case R.id.sign_in_button:
					intentCall = new Intent(getApplicationContext(), SignInActivity.class);
					startActivity(intentCall);
					break;
				case R.id.sign_up_button:
					intentCall = new Intent(getApplicationContext(), SignUpActivity.class);
					startActivity(intentCall);
					break;
				}
			}
		};
		
		((Button)findViewById(R.id.sign_in_button)).setOnClickListener(ocl);
		((Button)findViewById(R.id.sign_up_button)).setOnClickListener(ocl);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
