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

public class API_Chat_Create extends AsyncTask<String, Void, String> {

	public interface OnNewChatListener {

		public void onComplete(boolean success, String result);
		
	}
	
	Context c;
	OnNewChatListener complete;
	ProgressDialog dialog;
	
	public API_Chat_Create (Context c, OnNewChatListener complete) {
		this.c = c;
		this.complete = complete;
		this.dialog = new ProgressDialog(c);
	}
	
	protected void onPreExecute() {
		//dialog.setMessage("...");  
		dialog.show();
	}

	protected String doInBackground(String... params) {
		String result = "";
		HttpClient httpclient = GlobalValues.httpclient;
		HttpPost httppost = new HttpPost(String.format(GlobalValues.Service_URL + "%s", "/chat/create"));
		
		List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
		System.out.println("Create - " + params[0] + " ... " + params[1]);
		nameValuePairs.add(new BasicNameValuePair("topic",	  params[0]));
		nameValuePairs.add(new BasicNameValuePair("users",	  params[1]));

		try {
			httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

			HttpResponse response = httpclient.execute(httppost, GlobalValues.localContext);
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream instream = entity.getContent();
				result = GlobalValues.convertStreamToString(instream);
				Log.i("Chat", "/chat/create: " + result);
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
				complete.onComplete(false, "");
			}

			if(jObject.has("chat_id")) {
				complete.onComplete(true, jObject.getString("chat_id"));
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
}
