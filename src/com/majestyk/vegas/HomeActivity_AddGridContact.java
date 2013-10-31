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

public class HomeActivity_AddGridContact extends BaseAdapter {
	
	private Context mContext;
	private List<Profile> ProfList;
	
	public HomeActivity_AddGridContact(Context c, List<Profile> list) {
		mContext = c;
		ProfList = list;
	}

	@Override
	public int getCount() {
		return ProfList.size();
	}
	
	@Override
	public Profile getItem(int pos) {
		return ProfList.get(pos);
	}
	
	@Override
	public long getItemId(int pos) {
		return pos;
	}
	
	@Override
	public View getView(int pos, View convertView, ViewGroup parent) {
		View v = convertView;

		if(v == null) {
			LayoutInflater inflater = LayoutInflater.from(mContext);
            v = inflater.inflate(R.layout.simple_contact_object_grid, null);
		}

        final TextView profName = (TextView) v.findViewById(R.id.name);
        final ImageView profPic = (ImageView) v.findViewById(R.id.pic);
        
        FontManager.setTypeFace(profName);

		try {
			new GetBitmapFromURL(ProfList.get(pos).getUserImg(), new BitmapListener() {
				public void onComplete(Bitmap result) {
					if (result != null)
						profPic.setImageBitmap(Bitmap.createScaledBitmap(result, 127, 170, false));			
				}
			}).execute();
	        profName.setText(ProfList.get(pos).getUserName());
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}
		return v;
	}
}
