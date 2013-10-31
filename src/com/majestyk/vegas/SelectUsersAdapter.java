package com.majestyk.vegas;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.RejectedExecutionException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.majestyk.vegas.GetBitmapFromURL.BitmapListener;

public class SelectUsersAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<String> PicList;
	private List<String> NameList;
	private LinkedList<Integer> selected;
	
	public SelectUsersAdapter(Context c, List<String> list1, List<String> list2) {
		mContext = c;
		PicList = list1;
		NameList = list2;
		selected = new LinkedList<Integer>();
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
	public View getView(final int pos, View convertView, ViewGroup parent) {
		View v = convertView;

		LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.simple_contact_object_list, null);

        final ImageView profPic = (ImageView) v.findViewById(R.id.pic);
        TextView profName = (TextView) v.findViewById(R.id.name);
        ImageView arrow = (ImageView) v.findViewById(R.id.imageView1);
        CheckBox cb = (CheckBox) v.findViewById(R.id.checkBox1);
        
        cb.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(isChecked) 
					selected.add(pos);
				else 
					selected.remove(selected.indexOf(pos));
			}
        	
        });
        
    	cb.setChecked(selected.contains(pos));
        
        FontManager.setTypeFace(profName);
        profName.setTextColor(Color.parseColor("#000000"));//BLACK);

        try {
			new GetBitmapFromURL(PicList.get(pos), new BitmapListener() {
				public void onComplete(Bitmap result) {
					if (result != null)
						profPic.setImageBitmap(Bitmap.createScaledBitmap(result, 127, 170, false));
				}
			}).execute();
	        profName.setText(NameList.get(pos));
		} catch (RejectedExecutionException e) {
			e.printStackTrace();
		}

        profName.setText(NameList.get(pos));
        arrow.setVisibility(View.GONE);

		return v;
	}
	
	public LinkedList<String> getSelectedItems() {
		LinkedList<String> ret = new LinkedList<String>();
		for(Integer position : selected){
			ret.add(NameList.get(position));
		}
		return ret;
	}

}
