package com.majestyk.vegas;

import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.majestyk.vegas.GetBitmapFromURL.BitmapListener;

public class HomeActivity_AddListContact extends BaseAdapter {
	
	private Context mContext;
	private List<String> PicList;
	private List<String> NameList;
	
	public HomeActivity_AddListContact(Context c, List<String> list1, List<String> list2) {
		mContext = c;
		PicList = list1;
		NameList = list2;
	}

	@Override
	public int getCount() {
		return NameList.size();
	}
	
	@Override
	public Object getItem(int pos) {
		return NameList.get(pos);
	}
	
	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View v = convertView;

		LayoutInflater inflater = LayoutInflater.from(mContext);
		v = inflater.inflate(R.layout.simple_contact_object_list, null);

        final TextView profName = (TextView) v.findViewById(R.id.name);
        final ImageView profPic = (ImageView) v.findViewById(R.id.pic);
        
        FontManager.setTypeFace(profName);

		try {
	        new GetBitmapFromURL(PicList.get(pos), new BitmapListener() {
				public void onComplete(Bitmap result) {
					if (result != null)
						profPic.setImageBitmap(Bitmap.createScaledBitmap(result, 55, 60, false));
				}
			}).execute();
	        profName.setText(NameList.get(pos));
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}

		(v.findViewById(R.id.checkBox1)).setVisibility(View.GONE);

		return v;
	}
}
