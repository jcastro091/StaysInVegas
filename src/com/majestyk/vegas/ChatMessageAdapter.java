package com.majestyk.vegas;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ChatMessageAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<String> Username, Message, Timestamp;
	private String tmp_time = "";
	
	public ChatMessageAdapter(Context c, List<String> list1, List<String> list2, List<String> list3) {
		mContext = c;
		Username = list1;
		Message = list2;
		Timestamp = list3;
	}

	@Override
	public int getCount() {
		return Username.size();
	}
	
	@Override
	public Object getItem(int pos) {
		return Username.get(pos);
	}
	
	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View v = convertView;

		if(v == null) {
			
			System.out.println(Username.get(pos) + " " + Message.get(pos) + " " + Timestamp.get(pos));
			
	        if (Username.get(pos).equals("Me")) {
				LayoutInflater inflater = LayoutInflater.from(mContext);
	            v = inflater.inflate(R.layout.chat_message_me, null);

	    		TextView tv1 = (TextView) v.findViewById(R.id.textView1);
	            ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
	            TextView tv2 = (TextView) v.findViewById(R.id.timeView1);

	            tv1.setText(Message.get(pos));
		    	tv2.setText(convertGMT_Time2Local_Time(Timestamp.get(pos)));
		    	
		    	if (tmp_time.equals(convertGMT_Time2Local_Time(Timestamp.get(pos))))
		    		tv2.setVisibility(View.GONE);
	        }
	        else {
				LayoutInflater inflater = LayoutInflater.from(mContext);
	            v = inflater.inflate(R.layout.chat_message_you, null);

	    		TextView tv1 = (TextView) v.findViewById(R.id.textView1);
	    		TextView tv2 = (TextView) v.findViewById(R.id.textView2);
	    		ImageView iv = (ImageView) v.findViewById(R.id.imageView1);
	            TextView tv3 = (TextView) v.findViewById(R.id.timeView1);
	            
	            tv1.setText(Message.get(pos));
	            tv2.setText(Username.get(pos));
		    	tv3.setText(convertGMT_Time2Local_Time(Timestamp.get(pos)));
		        
		    	if (tmp_time.equals(convertGMT_Time2Local_Time(Timestamp.get(pos))))
		    		tv3.setVisibility(View.GONE);
	    	}
	        
	        tmp_time = convertGMT_Time2Local_Time(Timestamp.get(pos));
		}

        return v;
	}
	
	public class getBitmapFromURL extends AsyncTask<String, Void, Bitmap> {
		
		TextView TV;
		ImageView IV;
		String URL;
		
		public getBitmapFromURL(TextView tv1, ImageView iv, String string) {
			this.TV = tv1;
			this.IV = iv;
			this.URL = string;
		}

		protected void onPreExecute() { }
		
		protected Bitmap doInBackground(String... params) {
			Bitmap myBitmap = null;
	        try {
	            URL url = new URL(URL);
	            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
	            connection.setDoInput(true);
	            connection.connect();
	            InputStream input = connection.getInputStream();
	            
	            BitmapFactory.Options options=new BitmapFactory.Options();
	            options.inSampleSize = 2;
	            
	            myBitmap = BitmapFactory.decodeStream(input, null, options);
	        	
	            connection.disconnect();
            } catch (IOException e) {
	            e.printStackTrace();
	        } catch (OutOfMemoryError e) {
				e.printStackTrace();
				System.gc();
			}
	        return myBitmap;
		}
		
		protected void onPostExecute (Bitmap bitmap) {
			IV.setImageBitmap(bitmap);
			IV.setVisibility(View.VISIBLE);
			TV.setVisibility(View.GONE);
		}
	}
	
	public static String convertGMT_Time2Local_Time(String GMT_Time) {
        
		SimpleDateFormat inputFormat = new SimpleDateFormat("yyy-MM-dd HH:mm:ss", Locale.US);
		inputFormat.setTimeZone(TimeZone.getTimeZone("GMT"));

        SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd, yyyy, hh:mm aa", Locale.US);
        Date tmp = null;
		try {
			tmp = inputFormat.parse(GMT_Time);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return outputFormat.format(tmp);
	}
}
