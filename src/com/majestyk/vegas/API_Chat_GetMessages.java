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

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class API_Chat_GetMessages extends AsyncTask<String, Void, String> {

	public interface OnTaskCompletedListener {

		public void onComplete(boolean success, String result);
		
	}

	Context c;
	OnTaskCompletedListener complete;
	ProgressDialog dialog;
	
	public API_Chat_GetMessages (Context c, OnTaskCompletedListener complete) {
		this.c = c;
		this.complete = complete;
		this.dialog = new ProgressDialog(c);
	}
	
	protected void onPreExecute() {
		//dialog.setMessage("Logging in ...");  
		dialog.show();
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.httpclient;
		HttpPost httppost = new HttpPost(String.format(GlobalValues.Service_URL + "%s", "/chat/getMessages"));
		
		System.out.println("Messages - " + params[0] + " ... " + params[1]);
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		nameValuePairs.add(new BasicNameValuePair("chat_id",	params[0]));
		nameValuePairs.add(new BasicNameValuePair("count",	  	params[1]));
		if (params.length > 2) {
			nameValuePairs.add(new BasicNameValuePair("timestamp",	params[2]));
		}

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost, GlobalValues.localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i("GetMessages", "/chat/getMessages: " + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void onPostExecute(String result) {
		dialog.dismiss();
		complete.onComplete(true, result);
	}
}
