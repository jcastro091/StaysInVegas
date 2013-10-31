package com.majestyk.vegas;

import java.util.ArrayList;
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
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.TextView;

import com.majestyk.vegas.API_Chat_GetAll.OnTaskCompletedListener;

public class ChatActivity_Home extends Activity {
	
	ListView listContent;
	Button newChatActivity;
	
	ArrayList<String> ids;
	List<String> chats;
    List<String> names;
    
    ArrayList<Invitation> invites;
	private OnClickListener ocl;
    
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_view_all);

        listContent = (ListView) this.findViewById(R.id.listView1);
        newChatActivity = (Button) this.findViewById(R.id.new_chat_button);
        
        ids = new ArrayList<String>();
        chats = new ArrayList<String>();
        names = new ArrayList<String>();
        
        new API_Chat_GetAll(ChatActivity_Home.this, new OnTaskCompletedListener() {
		
			@Override
			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				try {
					
					JSONObject jObject = new JSONObject(result.trim());
					
					try {
						jArray = (JSONArray)jObject.get("chats");
						
						for(int i=0; i<jArray.length(); i++) {
							JSONObject j = (JSONObject)jArray.get(i);
							System.out.println(j);
							
							ids.add((String)j.get("chat_id"));
							chats.add(j.get("topic").toString());
							names.add((String)j.get("last_message"));
							//((String)j.get("private"));
							//((String)j.get("last_timestamp"));
							//((String)j.get("new_messages"));
							//((String)j.get("total_messages"));
	
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}

					invites = new ArrayList<Invitation>();
					
					try {
						jArray = (JSONArray)jObject.get("invites");
						
						for(int i=0; i<jArray.length(); i++) {
							JSONObject j = (JSONObject)jArray.get(i);
							System.out.println(j);
							
							invites.add(new Invitation(j.get("invite_id").toString(),
									j.get("chat_id").toString(),
									j.get("username").toString(),
									j.get("private").toString()));
						}
						
						checkInvites();
						
					} catch (JSONException e) {
						e.printStackTrace();
					}

				} catch (JSONException e) {
					e.printStackTrace();
				}
				
				ChatListAdapter listAdapter = new ChatListAdapter(ChatActivity_Home.this, ids, chats, names);
		        listContent.setAdapter(listAdapter);

		        listContent.setOnItemClickListener(new OnItemClickListener() {
		        	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
						Intent intent = new Intent(getApplicationContext(), ChatActivity.class);
						intent.putExtra("id", Integer.toString(v.getId()));
						intent.putExtra("topic", chats.get(position));
		        		startActivity(intent);
		        	}
		        });

			}
        }).execute();
        
        ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switch(v.getId()) {
				case R.id.new_chat_button:
					Intent intent = new Intent(getApplicationContext(), ChatActivity_SelectUsers.class);
					intent.putExtra("mode", "New");
					startActivity(intent);				
					break;
				case R.id.button1:
					System.out.println("Accept");
					new API_Invite(ChatActivity_Home.this, new OnTaskCompleteListener() {
						@Override
						public void onComplete(boolean success) {
							if(success) {
								invites.remove(0);
								checkInvites();
							}
						}
					}).execute("accept", invites.get(0).getInviteId());
					break;
				case R.id.button2:
					System.out.println("Decline");
					new API_Invite(ChatActivity_Home.this, new OnTaskCompleteListener() {
						@Override
						public void onComplete(boolean success) {
							if(success) {
								invites.remove(0);
								checkInvites();
							}
						}
					}).execute("decline", invites.get(0).getInviteId());
					break;
				}
			}
        };
        
        newChatActivity.setOnClickListener(ocl);
	    
		tabSetUp();
		
	}
/*	
	private void tabSetUp() {
		final Button chatTab	= (Button)findViewById(R.id.button1);
		final Button peopleTab	= (Button)findViewById(R.id.button2);
		final Button meTab		= (Button)findViewById(R.id.button3);
		
		chatTab.setFocusableInTouchMode(false);
		peopleTab.setFocusableInTouchMode(false);
		meTab.setFocusableInTouchMode(false);
		
		OnClickListener ocl = new OnClickListener() {

			public void onClick(View v) {
				Intent intent = new Intent();
				switch(v.getId()) {
				case R.id.button1:
					Log.i("HomeActivity", this.getClass().getName());
					if (!this.getClass().getName().contains("ChatActivity_Home")) {
						intent = new Intent(getApplicationContext(), ChatActivity_Home.class);
						startActivity(intent);

		        		chatTab.setPressed(true);
						peopleTab.setPressed(false);
						meTab.setPressed(false);
	
						chatTab.setSelected(true);
					}
					break;
				case R.id.button2:
					Log.i("HomeActivity", this.getClass().getName());
					if (!this.getClass().getName().contains("HomeActivity")) {
						intent = new Intent(getApplicationContext(), HomeActivity.class);
		        		startActivity(intent);
						
		        		chatTab.setPressed(false);
						peopleTab.setPressed(true);
						meTab.setPressed(false);
						
						peopleTab.setSelected(true);
					}
					break;
				case R.id.button3:
					Log.i("HomeActivity", this.getClass().getName());
					if (!this.getClass().getName().contains("ProfileActivity")) {
						intent = new Intent(getApplicationContext(), ProfileActivity.class);
						intent.putExtra("mode", "Me");
		        		startActivity(intent);
	
		        		chatTab.setPressed(false);
						peopleTab.setPressed(false);
						meTab.setPressed(true);
						
						meTab.setSelected(true);
					}
					break;
				}
			}
		};
		
		chatTab.setOnClickListener(ocl);
		peopleTab.setOnClickListener(ocl);
		meTab.setOnClickListener(ocl);
		
	}
*/
	private void tabSetUp() {
		final CheckBox chatTab		= (CheckBox)findViewById(R.id.button1);
		final CheckBox peopleTab	= (CheckBox)findViewById(R.id.button2);
		final CheckBox meTab		= (CheckBox)findViewById(R.id.button3);

		chatTab.setChecked(true);
		peopleTab.setChecked(false);
		meTab.setChecked(false);
		
		OnCheckedChangeListener occl = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				Intent intent = new Intent();
				switch(buttonView.getId()){
				case R.id.button1:
					Log.i("HomeActivity", this.getClass().getName());
					if (!this.getClass().getName().contains("ChatActivity_Home")) {
						chatTab.setOnCheckedChangeListener(null);
						peopleTab.setOnCheckedChangeListener(null);
						meTab.setOnCheckedChangeListener(null);
						
						chatTab.setChecked(true);
						peopleTab.setChecked(false);
						meTab.setChecked(false);
						
						chatTab.setOnCheckedChangeListener(this);
						peopleTab.setOnCheckedChangeListener(this);
						meTab.setOnCheckedChangeListener(this);

						intent = new Intent(getApplicationContext(), ChatActivity_Home.class);
						startActivity(intent);
					}
					break;
				case R.id.button2:
					Log.i("HomeActivity", this.getClass().getName());
					if (!this.getClass().getName().contains("HomeActivity")) {
						chatTab.setOnCheckedChangeListener(null);
						peopleTab.setOnCheckedChangeListener(null);
						meTab.setOnCheckedChangeListener(null);
						
						chatTab.setChecked(false);
						peopleTab.setChecked(true);
						meTab.setChecked(false);
						
						chatTab.setOnCheckedChangeListener(this);
						peopleTab.setOnCheckedChangeListener(this);
						meTab.setOnCheckedChangeListener(this);

						intent = new Intent(getApplicationContext(), HomeActivity.class);
		        		startActivity(intent);
					}
					break;
				case R.id.button3:
					Log.i("HomeActivity", this.getClass().getName());
					if (!this.getClass().getName().contains("ProfileActivity")) {
						chatTab.setOnCheckedChangeListener(null);
						peopleTab.setOnCheckedChangeListener(null);
						meTab.setOnCheckedChangeListener(null);
						
						chatTab.setChecked(false);
						peopleTab.setChecked(false);
						meTab.setChecked(true);
						
						chatTab.setOnCheckedChangeListener(this);
						peopleTab.setOnCheckedChangeListener(this);
						meTab.setOnCheckedChangeListener(this);

						intent = new Intent(getApplicationContext(), ProfileActivity.class);
						intent.putExtra("mode", "Me");
		        		startActivity(intent);
					}
					break;
				}
			}
		};
		
		chatTab.setOnCheckedChangeListener(occl);
		peopleTab.setOnCheckedChangeListener(occl);
		meTab.setOnCheckedChangeListener(occl);
		
	}
/*
	public void onResume() {
		super.onResume();
		((CheckBox)findViewById(R.id.button1)).setChecked(true);
		((CheckBox)findViewById(R.id.button2)).setChecked(false);
		((CheckBox)findViewById(R.id.button3)).setChecked(false);
	}
*/
	
	public void checkInvites() {
		if (invites.isEmpty())
			findViewById(R.id.relativeLayout1).setVisibility(View.GONE);
		else {
			findViewById(R.id.relativeLayout1).setVisibility(View.VISIBLE);
			((TextView)findViewById(R.id.textView2)).setText(invites.get(0).getUsername() + " has invited you to chat");
			((Button)findViewById(R.id.button1)).setOnClickListener(ocl);
			((Button)findViewById(R.id.button2)).setOnClickListener(ocl);
		}
	}
	
	public class Invitation {
		
		private String invite_id;
		private String chat_id;
		private String username;
		private String status;
		
		public Invitation(String s1, String s2, String s3, String s4) {
			this.invite_id = s1;
			this.chat_id = s2;
			this.username = s3;
			this.status = s4;
		}

		public String getInviteId() {
			return invite_id;
		}

		public void setInviteId(String invite_id) {
			this.invite_id = invite_id;
		}

		public String getChatId() {
			return chat_id;
		}

		public void setChatId(String chat_id) {
			this.chat_id = chat_id;
		}

		public String getUsername() {
			return username;
		}

		public void setUsername(String username) {
			this.username = username;
		}

		public String getPrivate() {
			return status;
		}

		public void setPrivate(String status) {
			this.status = status;
		}
	}
}
