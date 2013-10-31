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

public class API_UpdateProfile extends AsyncTask<String, Void, String> {

	Context c;
	OnTaskCompleteListener complete;
	ProgressDialog dialog;
	
	public API_UpdateProfile (Context c, OnTaskCompleteListener complete) {
		this.c = c;
		this.complete = complete;
		this.dialog = new ProgressDialog(c);
	}
	
	protected void onPreExecute() {
		dialog.setMessage("Sending data ...");  
		dialog.show();
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.httpclient;
		HttpPost httppost = new HttpPost(String.format(GlobalValues.Service_URL + "%s", "/user/updateProfile"));
		
		System.out.println(params[0] + "; " + params[1] + "; " + params[2] + "; " + params[3] + "; " + params[4]);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("username",	params[0]));
		nameValuePairs.add(new BasicNameValuePair("age",		params[1]));
		nameValuePairs.add(new BasicNameValuePair("about",		params[2]));
		nameValuePairs.add(new BasicNameValuePair("interest",	params[3]));
		nameValuePairs.add(new BasicNameValuePair("plans",		params[4]));
		nameValuePairs.add(new BasicNameValuePair("image",		params[5]));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost, GlobalValues.localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i("Update Profile", "/user/updateProfile: " + result);
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
				Toast.makeText(c, "Profile Updated successfully", Toast.LENGTH_SHORT).show();
				complete.onComplete(true);
			}
			
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
