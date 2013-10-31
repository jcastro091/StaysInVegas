package com.majestyk.vegas;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class API_Register extends AsyncTask<String, Void, String> {

	Context c;
	OnTaskCompleteListener complete;
	ProgressDialog dialog;
	
	public API_Register (Context c, OnTaskCompleteListener complete) {
		this.c = c;
		this.complete = complete;
		this.dialog = new ProgressDialog(c);
	}
	
	protected void onPreExecute() {
		dialog.setMessage("Sending data ...");  
		dialog.show();
		//dialog = ProgressDialog.show(c, "", "Sending data. Please wait...", true);
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.httpclient;
		HttpPost httppost = new HttpPost(String.format(GlobalValues.Service_URL + "%s", "/user/register"));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",	  params[0]));
		nameValuePairs.add(new BasicNameValuePair("password",	  params[1]));
		nameValuePairs.add(new BasicNameValuePair("device_id",	  params[2]));
		nameValuePairs.add(new BasicNameValuePair("os",	  		  "android"));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost, GlobalValues.localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i("Register", "/user/register: " + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void onPostExecute(String result) {
		dialog.dismiss();
		try {
			JSONObject jObject = new JSONObject(result.trim());

			if(jObject.has("error")) {
				Toast.makeText(c, jObject.getString("error"), Toast.LENGTH_SHORT).show();
				complete.onComplete(false);
			}

			if(jObject.has("user_id")) {
				GlobalValues.user_id = (String) jObject.get("user_id");
				Toast.makeText(c, "Profile Registered successfully", Toast.LENGTH_SHORT).show();
				complete.onComplete(true);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
