package com.majestyk.vegas;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

public class API_Chat_GetAll extends AsyncTask<String, Void, String> {

	public interface OnTaskCompletedListener {

		public void onComplete(boolean success, String result);
		
	}

	Context c;
	OnTaskCompletedListener complete;
	//ProgressDialog dialog;
	
	public API_Chat_GetAll (Context c, OnTaskCompletedListener complete) {
		this.c = c;
		this.complete = complete;
		//this.dialog = new ProgressDialog(c);
	}
	
	//protected void onPreExecute() {
		//dialog.setMessage("Loading data ...");
		//dialog.show();
	//}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.httpclient;
		HttpPost httppost = new HttpPost(String.format(GlobalValues.Service_URL + "%s", "/chat/getAll"));
		
		try {
			HttpResponse response = httpclient.execute(httppost, GlobalValues.localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i("GetAll", "/chat/getAll: " + result);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
	
	public void onPostExecute(String result) {
		//dialog.dismiss();
		complete.onComplete(true, result);
	}
}
