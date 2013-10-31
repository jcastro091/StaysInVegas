package com.majestyk.vegas;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ChatListAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<String> Chat_IDs, ChatName, UserName;
	
	public ChatListAdapter(Context c, List<String> list1, List<String> list2, List<String> list3) {
		mContext = c;
		Chat_IDs = list1;
		ChatName = list2;
		UserName = list3;
	}

	@Override
	public int getCount() {
		return ChatName.size();
	}
	
	@Override
	public Object getItem(int pos) {
		return ChatName.get(pos);
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
            v = inflater.inflate(R.layout.chat_object, null);
		}
		
		v.setId(Integer.parseInt(Chat_IDs.get(pos)));

		TextView tv1 = (TextView) v.findViewById(R.id.textView1);
        TextView tv2 = (TextView) v.findViewById(R.id.textView2);
        
        FontManager.setTypeFace(tv1);
        FontManager.setTypeFace(tv2);

        tv1.setText(ChatName.get(pos));
        tv2.setText(UserName.get(pos));

		return v;
	}
}
