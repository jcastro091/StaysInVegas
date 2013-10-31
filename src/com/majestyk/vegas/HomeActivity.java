package com.majestyk.vegas;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.majestyk.vegas.API_Users_GetAll.OnTaskCompletedListener;

public class HomeActivity extends Activity {
	
	ImageView sideHandle1, sideHandle2;
	LinearLayout sideContent;
	ListView listContent;
	GridView gridContent;
	EditText search_bar;
	
	TextView Row1;
	
	ArrayList<Profile> profilesAll;
	ArrayList<Profile> profilesNearby;
	ArrayList<Profile> profilesFavorites;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sideHandle1 = (ImageView) this.findViewById(R.id.sideHandle1);
        sideHandle2 = (ImageView) this.findViewById(R.id.sideHandle2);
        sideContent = (LinearLayout) this.findViewById(R.id.sideContent);
        listContent = (ListView) this.findViewById(R.id.listContent);
        gridContent = (GridView) this.findViewById(R.id.gridContent);
        
        sideHandle1.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(sideContent.getVisibility()==(View.VISIBLE)) {
            		sideHandle1.setVisibility(View.VISIBLE);
            		sideContent.setVisibility(View.INVISIBLE);
            		sideHandle2.setVisibility(View.INVISIBLE);
            	} else if(sideContent.getVisibility()==(View.INVISIBLE) || sideContent.getVisibility()==(View.GONE)) {
            		sideHandle1.setVisibility(View.INVISIBLE);
            		sideContent.setVisibility(View.VISIBLE);
            		sideHandle2.setVisibility(View.VISIBLE);
            	}
           	}
        });
        
        sideHandle2.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(sideContent.getVisibility()==(View.VISIBLE)) {
            		sideHandle1.setVisibility(View.VISIBLE);
            		sideContent.setVisibility(View.INVISIBLE);
            		sideHandle2.setVisibility(View.INVISIBLE);
            	} else if(sideContent.getVisibility()==(View.INVISIBLE)) {
            		sideHandle1.setVisibility(View.INVISIBLE);
            		sideContent.setVisibility(View.VISIBLE);
            		sideHandle2.setVisibility(View.VISIBLE);
            	}
        	}
        });
        
        new API_Users_GetAll(HomeActivity.this, new OnTaskCompletedListener() {
			@Override
			public void onComplete(boolean success, String result) {
				JSONArray jArray;
				
				profilesAll = new ArrayList<Profile>();
				
				try {
					jArray = new JSONArray(result.trim());
					for(int i=0; i<jArray.length(); i++) {
						JSONObject j = (JSONObject)jArray.get(i);
						System.out.println(j);
						
						profilesAll.add(new Profile(
								j.get("user_id").toString(),
								j.get("image").toString(),
								j.get("username").toString()));

					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				
		        HomeActivity_AddGridContact gridAdapter = new HomeActivity_AddGridContact(HomeActivity.this, profilesAll);
		        gridContent.setAdapter(gridAdapter);

		        gridContent.setOnItemClickListener(new OnItemClickListener() {
		        	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
						Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
						intent.putExtra("mode", "You");
						intent.putExtra("id", profilesAll.get(position).getUserId());
						intent.putExtra("img", profilesAll.get(position).getUserImg());
						intent.putExtra("name", profilesAll.get(position).getUserName());
		        		startActivity(intent);
		        	}
		        });
		        
		        search_bar = (EditText) findViewById(R.id.search);
		        
		        search_bar.addTextChangedListener(new TextWatcher()
		        {
		            public void afterTextChanged(Editable s) 
		            {
		            }
		            public void beforeTextChanged(CharSequence s, int start, int count, int after) 
		            { 
		            }
		            public void onTextChanged(CharSequence s, int start, int before, int count) 
		            {
		            }
		        });
		        
				tabSetUp();
				
			}
        }).execute("/user/getAll");
        
        ((Button)findViewById(R.id.button_nearby)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new API_Users_GetAll(HomeActivity.this, new OnTaskCompletedListener() {
					@Override
					public void onComplete(boolean success, String result) {
						JSONArray jArray;
						
						profilesNearby = new ArrayList<Profile>();
						
						try {
							jArray = new JSONArray(result.trim());
							for(int i=0; i<jArray.length(); i++) {
								JSONObject j = (JSONObject)jArray.get(i);
								System.out.println(j);
								
								profilesNearby.add(new Profile(
										j.get("user_id").toString(),
										j.get("image").toString(),
										j.get("username").toString()));

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
				        HomeActivity_AddGridContact gridAdapter = new HomeActivity_AddGridContact(HomeActivity.this, profilesNearby);
				        gridContent.setAdapter(gridAdapter);

				        gridContent.setOnItemClickListener(new OnItemClickListener() {
				        	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				        		Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
								intent.putExtra("mode", "You");
								intent.putExtra("id", profilesNearby.get(position).getUserId());
								intent.putExtra("img", profilesNearby.get(position).getUserImg());
								intent.putExtra("name", profilesNearby.get(position).getUserName());
				        		startActivity(intent);
				        	}
				        });
					}
		        }).execute("/user/getNearby");
			}
        });

        ((Button)findViewById(R.id.button_fav)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				new API_Users_GetAll(HomeActivity.this, new OnTaskCompletedListener() {
					@Override
					public void onComplete(boolean success, String result) {
						JSONArray jArray;
						
						profilesFavorites = new ArrayList<Profile>();
						
						try {
							jArray = new JSONArray(result.trim());
							for(int i=0; i<jArray.length(); i++) {
								JSONObject j = (JSONObject)jArray.get(i);
								System.out.println(j);
								
								profilesFavorites.add(new Profile(
										j.get("user_id").toString(),
										j.get("image").toString(),
										j.get("username").toString()));

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						
				        HomeActivity_AddGridContact gridAdapter = new HomeActivity_AddGridContact(HomeActivity.this, profilesFavorites);
				        gridContent.setAdapter(gridAdapter);

				        gridContent.setOnItemClickListener(new OnItemClickListener() {
				        	public void onItemClick(AdapterView<?> l, View v, int position, long id) {
				        		Intent intent = new Intent(getApplicationContext(), ProfileActivity.class);
								intent.putExtra("mode", "You");
								intent.putExtra("id", profilesFavorites.get(position).getUserId());
								intent.putExtra("img", profilesFavorites.get(position).getUserImg());
								intent.putExtra("name", profilesFavorites.get(position).getUserName());
				        		startActivity(intent);
				        	}
				        });
					}
				}).execute("/favorite/getAll");
			}
        });
	}
	
	private void tabSetUp() {
		final CheckBox chatTab		= (CheckBox)findViewById(R.id.button2);
		final CheckBox peopleTab	= (CheckBox)findViewById(R.id.button1);
		final CheckBox meTab		= (CheckBox)findViewById(R.id.button3);

		chatTab.setChecked(false);
		peopleTab.setChecked(true);
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
						
						chatTab.setChecked(false);
						peopleTab.setChecked(true);
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
						
						chatTab.setChecked(true);
						peopleTab.setChecked(false);
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
		((CheckBox)findViewById(R.id.button1)).setChecked(false);
		((CheckBox)findViewById(R.id.button2)).setChecked(true);
		((CheckBox)findViewById(R.id.button3)).setChecked(false);
	}
*/
}
