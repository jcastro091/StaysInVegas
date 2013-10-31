package com.majestyk.vegas;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ReportActivity extends Activity {
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        setUpOCL();
	}
	
	private void setUpOCL() {
		final EditText text = (EditText)findViewById(R.id.description);
		final Button report = (Button)findViewById(R.id.report_button); 
		
		final String id = getIntent().getStringExtra("id");
		report.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {

				String message = "";
				
				String description = text.getText().toString();
					if(description.length()==0) message+=(message.length()>0?", Description": "Description");

				else if (message.length() != 0) 
					GlobalValues.showAlert(ReportActivity.this, "You must fill out all fields");
				else

			        new API_Report_User(ReportActivity.this, new OnTaskCompleteListener() {
						@Override
						public void onComplete(boolean success) {
							if (success)
								finish();
						}
			        }).execute(id, description);
		                			
			}
		});
		
	}

}
