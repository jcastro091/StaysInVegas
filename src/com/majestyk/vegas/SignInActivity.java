package com.majestyk.vegas;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SignInActivity extends Activity {
	
	public static final String PREFS_NAME = "VegasPrefs";
	View.OnClickListener ocl;
	
	private double lat = 0.0;
	private double lon = 0.0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_in_view);

		FontManager.setTypeFace(((EditText)findViewById(R.id.username)));
		FontManager.setTypeFace(((EditText)findViewById(R.id.password)));
				
	//TEST MODE
		((EditText)findViewById(R.id.username)).setText("will5");
		((EditText)findViewById(R.id.password)).setText("qwerty");
		
		ocl = new OnClickListener() {
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.sign_in_button:
					
					String message = "";
					
					String username = ((EditText)findViewById(R.id.username)).getText().toString();
						if(username.length()==0) message+=(message.length()>0?", Username": "Username");
					String password = ((EditText)findViewById(R.id.password)).getText().toString();
						if(password.length()==0) message+=(message.length()>0?", Password": "Password");
		
					if (message.length() != 0) 
						GlobalValues.showAlert(SignInActivity.this, "You must fill out all fields");
					else {
						final String user = username;
						final String pass = password;

						GPSTracker gps = new GPSTracker(SignInActivity.this);

		                // check if GPS enabled       
		                if(gps.canGetLocation()) {

		                    lat = gps.getLatitude();
		                    lon = gps.getLongitude();
		                   
		            		Log.i("GPS", lon + " " + lat);
		            		
		                } else {
		                    gps.showSettingsAlert();
		                }

						new API_Login(SignInActivity.this, new OnTaskCompleteListener() {

							@Override
							public void onComplete(boolean success) {
								if(success) {
									Intent intentCall;
									intentCall = new Intent(getApplicationContext(), ChatActivity_Home.class);
									startActivity(intentCall);
									
									SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
									SharedPreferences.Editor editor = settings.edit();
									editor.putString("logged", "logged");
									editor.putString("username", user);
									editor.putString("password", pass);
									editor.putString("deviceID", "qwerty");
									editor.commit();
								}
							}
						}).execute(username, password, "qwerty", Double.toString(lat), Double.toString(lon));
					}
					break;
				}
			}
		};

		((Button)findViewById(R.id.sign_in_button)).setOnClickListener(ocl);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
}
