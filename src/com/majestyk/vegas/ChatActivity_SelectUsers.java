package com.majestyk.vegas;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.majestyk.vegas.API_Chat_Create.OnNewChatListener;
import com.majestyk.vegas.API_Users_GetAll.OnTaskCompletedListener;

public class ChatActivity_SelectUsers extends Activity {
	
	View.OnClickListener ocl;
	
	ListView listContent;
	Button cancelBtn, doneBtn;
	
    List<String> profNameList;
    List<String> profPicList;
    List<String> profIdList;

	String mode;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select_users);
        
        listContent = (ListView) this.findViewById(R.id.listView1);
        cancelBtn = (Button) this.findViewById(R.id.cancel_button);
        doneBtn = (Button) this.findViewById(R.id.done_button);
        
        profNameList = new ArrayList<String>();
        profPicList = new ArrayList<String>();
        profIdList = new ArrayList<String>();

        new API_Users_GetAll(ChatActivity_SelectUsers.this, new OnTaskCompletedListener() {
			@Override
			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				try {
					jArray = new JSONArray(result.trim());
					for(int i=0; i<jArray.length(); i++) {
						JSONObject j = (JSONObject)jArray.get(i);
						System.out.println(j);
						
						String image = (String)j.get("image");
						String user_id = (String)j.get("user_id");
						String username = (String)j.get("username");

						profIdList.add(user_id);
						profPicList.add(image);
						profNameList.add(username);

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
			}
        }).execute();

        final SelectUsersAdapter listAdapter = new SelectUsersAdapter(ChatActivity_SelectUsers.this, profPicList, profNameList);
        listContent.setAdapter(listAdapter);

        listContent.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        ocl = new OnClickListener() {

			@Override
			public void onClick(View v) {
				LinkedList<String> selectedUsers;
				switch(v.getId()) {
				case R.id.cancel_button:
					onBackPressed();
					break;
				case R.id.done_button:
					selectedUsers = listAdapter.getSelectedItems();
					final String s = selectedUsers.get(0);
			        mode = getIntent().getStringExtra("mode");
			        Log.i("Chats", Integer.toString(selectedUsers.size()));
			        if (mode.equals("New")) {        
						if (selectedUsers.size()==1) {
							((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.GONE);							
							System.out.println("User" + s);
							System.out.println("Users" + selectedUsers);						

							new API_Chat_Create(ChatActivity_SelectUsers.this, new OnNewChatListener() {

								@Override
								public void onComplete(boolean success, String chat_id) {
									if(success) {
										Intent intent = new Intent(ChatActivity_SelectUsers.this, ChatActivity.class);
										intent.putExtra("id", chat_id);
										intent.putExtra("topic", s);
										startActivity(intent);
									}
								}
								
							}).execute("private chat", s);
							
						} else if (selectedUsers.size() > 1) {
						    ((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.VISIBLE);
						    System.out.println("Users" + selectedUsers);
						}
			        }
			        
			        if (mode.equals("Add")) {
						if (selectedUsers.size()==1) {
							((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.GONE);
//							Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
//							startActivity(intent);			
						} else if (selectedUsers.size() > 1) {
						    ((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.VISIBLE);
						}
			        }
			        
			        if (selectedUsers.size() == 0) {
			        	Log.i("", "No items selected");
			        }
			        break;
				case R.id.imageButton1:
					((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.GONE);
					break;
				case R.id.imageButton2:
					final EditText et = (EditText)findViewById(R.id.topic);
					if (et.getText().length() > 0) {
						String users = "";
						
						selectedUsers = listAdapter.getSelectedItems();
						for (int i = 0; i < selectedUsers.size(); i++) {
							users += selectedUsers.get(i);
							if (i!=selectedUsers.size()-1) users += ",";
						}
						
						new API_Chat_Create(ChatActivity_SelectUsers.this, new OnNewChatListener() {

							@Override
							public void onComplete(boolean success, String chat_id) {
								if(success) {
									Intent intent = new Intent(ChatActivity_SelectUsers.this, ChatActivity.class);
									intent.putExtra("id", chat_id);
									intent.putExtra("topic", et.getText().toString());
									startActivity(intent);
								}
							}
							
						}).execute(et.getText().toString(), users);
					} else GlobalValues.showAlert(ChatActivity_SelectUsers.this, "Please enter a topic");
					break;
				}
			}
			
        };
        
        cancelBtn.setOnClickListener(ocl);
        doneBtn.setOnClickListener(ocl);
        (findViewById(R.id.imageButton1)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton2)).setOnClickListener(ocl);

        }

	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
}
