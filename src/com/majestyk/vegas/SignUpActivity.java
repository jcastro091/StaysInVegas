package com.majestyk.vegas;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SignUpActivity extends Activity {
	
	View.OnClickListener ocl;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.sign_up_view);

		FontManager.setTypeFace(((EditText)findViewById(R.id.username)));
		FontManager.setTypeFace(((EditText)findViewById(R.id.password)));
		FontManager.setTypeFace(((EditText)findViewById(R.id.confirm_password)));
		
		ocl = new OnClickListener() {
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.sign_up_button:

					String message = "";
					
					String username = ((EditText)findViewById(R.id.username)).getText().toString();
						if(username.length()==0) message+=(message.length()>0?", Username": "Username");
					String password = ((EditText)findViewById(R.id.password)).getText().toString();
						if(password.length()==0) message+=(message.length()>0?", Password": "Password");
					String cpassword = ((EditText)findViewById(R.id.confirm_password)).getText().toString();
						if(cpassword.length()==0) message+=(message.length()>0?", Confirm Password": "Confirm Password");

					if (!cpassword.equals(password)) 
						GlobalValues.showAlert(SignUpActivity.this, "Password and Password confirmation must be the same");
					else if (message.length() != 0) 
						GlobalValues.showAlert(SignUpActivity.this, "You must fill out all fields");
					else
						new API_Register(SignUpActivity.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success) {
									Intent intentCall;
									intentCall = new Intent(getApplicationContext(), ChatActivity_Home.class);
									startActivity(intentCall);									
								}
							}
						}).execute(username, password, "qwerty");
					break;
				}
			}
		};
		
		((Button)findViewById(R.id.sign_up_button)).setOnClickListener(ocl);
		
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

}
