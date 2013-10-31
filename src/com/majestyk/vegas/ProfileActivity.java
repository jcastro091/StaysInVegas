package com.majestyk.vegas;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.majestyk.vegas.API_Users_Get.OnTaskCompletedListener;
import com.majestyk.vegas.GetBitmapFromURL.BitmapListener;

import de.ankri.views.Switch;

public class ProfileActivity extends Activity {
	
	String mode;
	boolean flag_Chat, flag_Favorite, flag_Block, flag_Report;
	boolean flag_Discover, flag_Hide;
	public static final String PREFS_NAME = "VegasPrefs";
	OnClickListener ocl;
	Bitmap bitmap;
	Uri imageUrl;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mode = getIntent().getStringExtra("mode");
        if (mode.equals("Me")) {
        	setContentView(R.layout.profile_me);        
        	setUpMe();
        }
        
        if (mode.equals("You")) {
        	setContentView(R.layout.profile_you);
        	setUpYou();
        }

        tabSetUp();
        
	}
	
	private void setUpMe() {
		
		this.setTheme(R.style.switch_dark);

		final Switch switchA = (Switch) this.findViewById(R.id.switch1);
		final Switch switchB = (Switch) this.findViewById(R.id.switch2);
		switchA.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
					flag_Discover = true;
				else
					flag_Discover = false;
			}
		});
		switchB.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener()
		{
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
			{
				if (isChecked)
					flag_Hide = true;
				else
					flag_Hide = false;
			}
		});
		
        final ImageView prof_pic = (ImageView) this.findViewById(R.id.profile_image);
        
        final TextView tv  = (TextView) this.findViewById(R.id.textView1);
        final TextView tv1 = (TextView) this.findViewById(R.id.About);
        final TextView tv2 = (TextView) this.findViewById(R.id.Interests);
        final TextView tv3 = (TextView) this.findViewById(R.id.Plans);

        final TextView prof_sex = (TextView) this.findViewById(R.id.profile_sex);
        final TextView partysze = (TextView) this.findViewById(R.id.party_size);
        final TextView DISCOVER = (TextView) this.findViewById(R.id.discover);
        final TextView HIGH_PRO = (TextView) this.findViewById(R.id.high_pro);
        
        final EditText profname = (EditText) this.findViewById(R.id.profile_name);
        final EditText prof_age = (EditText) this.findViewById(R.id.profile_age);
        
        FontManager.setTypeFace(tv);
        FontManager.setTypeFace(tv1);
        FontManager.setTypeFace(tv2);
        FontManager.setTypeFace(tv3);
        FontManager.setTypeFace(profname);
        FontManager.setTypeFace(prof_age);
        
        FontManager.setTypeFace(prof_sex);
        FontManager.setTypeFace(partysze);
        FontManager.setTypeFace(DISCOVER);
        FontManager.setTypeFace(HIGH_PRO);

        final EditText txt1 = (EditText) this.findViewById(R.id.about);
        final EditText txt2 = (EditText) this.findViewById(R.id.interests);
        final EditText txt3 = (EditText) this.findViewById(R.id.plans);

		new API_Users_Get(ProfileActivity.this, new OnTaskCompletedListener() {
			@Override
			public void onComplete(boolean success, String result) {
				JSONObject jObject;
				try {
					jObject = new JSONObject(result.trim());
					System.out.println(jObject);
					
					String username = (String)jObject.get("username");
					String age = (String)jObject.get("age");
					String image = (String)jObject.get("image");
					String party = (String)jObject.get("party");
					String gender = (String)jObject.get("gender");
					
					String about = (String)jObject.get("about");
					String interest = (String)jObject.get("interest");
					String plans = (String)jObject.get("plans");

					String distance = (String)jObject.get("distance");
					String favorited = (String)jObject.get("favorited");
					String blocked = (String)jObject.get("blocked");
					
					//String lat = (String)jObject.get("lat");
					//String lng = (String)jObject.get("lng");

					String hide = (String)jObject.get("hide_profile");
					String discover = (String)jObject.get("discoverable");

					if (blocked.equals("0")) flag_Block = false;
						else flag_Block = true;
					if (favorited.equals("0")) flag_Favorite = false;
						else flag_Favorite = true;
					if (hide.equals("0")) switchA.setChecked(false);
						else switchA.setChecked(true);
					if (discover.equals("0")) switchB.setChecked(false);
						else switchB.setChecked(true);

					new GetBitmapFromURL(image, new BitmapListener() {
						@Override
						public void onComplete(Bitmap result) {
							prof_pic.setImageBitmap(result);
						}
					}).execute();

					txt1.setText(about);
			        txt2.setText(interest);
			        txt3.setText(plans);

				    prof_age.setText("AGE: " + age);
				    profname.setText(username);
				    tv.setText(username);

				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
		}).execute(GlobalValues.user_id);

		ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				String image;
				switch(v.getId()) {
				case R.id.save_button:
					String message = "";
					
					String username = profname.getText().toString();
						if(username.length()==0) message+=(message.length()>0?", Name": "Name");
					String password = prof_age.getText().toString();
						if(password.length()==0) message+=(message.length()>0?", Age": "Age");

					image = "";
					if (imageUrl != null) {
						image = BitMapToString(bitmap);
					}
					
					if (message.length() != 0) 
						GlobalValues.showAlert(ProfileActivity.this, "You must fill out all fields");
					else {
				        new API_UpdateProfile(ProfileActivity.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success)
									finish();
							}
				        }).execute(
				        		profname.getText().toString(),
				        		prof_age.getText().toString(),
				        		txt1.getText().toString(),
				        		txt2.getText().toString(),
				        		txt3.getText().toString(),
				        		image);
					}
					break;
				case R.id.imageView1:
					((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.VISIBLE);
					break;
				case R.id.imageButton1:
					if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
						intent = new Intent(getApplicationContext(), AndroidCameraTestsActivity.class);
						startActivityForResult(intent, GlobalValues.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
						((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.GONE);							
					} else {
						GlobalValues.showAlert(ProfileActivity.this, "Error opening media card in read/write mode!");
					}
					break;
				case R.id.imageButton2:
					Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
					mediaChooser.setType("video/*, images/*");
					startActivityForResult(mediaChooser, 1);
					((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.GONE);
					break;
				case R.id.imageButton3:
					((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.GONE);
					intent = new Intent(getApplicationContext(), ChatActivity.class);
					startActivity(intent);
					break;
				case R.id.logout_button:
			        new API_Logout(ProfileActivity.this, new OnTaskCompleteListener() {
						@Override
						public void onComplete(boolean success) {
							if(success) {
							    CookieSyncManager.createInstance(ProfileActivity.this); 
							    CookieManager cookieManager = CookieManager.getInstance();
							    cookieManager.removeAllCookie();

								SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
								SharedPreferences.Editor editor = settings.edit();
								editor.putString("logged", "");
								editor.commit();
								
						        Intent intent = new Intent(ProfileActivity.this, MainActivity.class);
						        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						        startActivity(intent);
						        finish();
							}
						}
			        }).execute();
					break;
				}
			}
        };
		
        (findViewById(R.id.imageView1)).setOnClickListener(ocl);
		(findViewById(R.id.save_button)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton1)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton2)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton3)).setOnClickListener(ocl);
        (findViewById(R.id.logout_button)).setOnClickListener(ocl);

	}
	
	private void setUpYou() {
		
		final String id = getIntent().getStringExtra("id");
		
		flag_Chat	= false;
		flag_Favorite = false;
		flag_Block = false;
		flag_Report	= false;
    	
		final ImageView prof_pic = (ImageView) this.findViewById(R.id.profile_image);
        
        final TextView tv  = (TextView) this.findViewById(R.id.textView1);
        final TextView tv1 = (TextView) this.findViewById(R.id.About);
        final TextView tv2 = (TextView) this.findViewById(R.id.Interests);
        final TextView tv3 = (TextView) this.findViewById(R.id.Plans);
        final TextView profname = (TextView) this.findViewById(R.id.profile_name);
        final TextView prof_age = (TextView) this.findViewById(R.id.profile_age);
        
        final TextView prof_sex = (TextView) this.findViewById(R.id.profile_sex);
        final TextView partysize = (TextView) this.findViewById(R.id.party_size);
        
        FontManager.setTypeFace(tv);
        FontManager.setTypeFace(tv1);
        FontManager.setTypeFace(tv2);
        FontManager.setTypeFace(tv3);
        FontManager.setTypeFace(profname);
        FontManager.setTypeFace(prof_age);
        FontManager.setTypeFace(prof_sex);
        FontManager.setTypeFace(partysize);

        final TextView txt1 = (TextView) this.findViewById(R.id.about);
        final TextView txt2 = (TextView) this.findViewById(R.id.interests);
        final TextView txt3 = (TextView) this.findViewById(R.id.plans);

		new API_Users_Get(ProfileActivity.this, new OnTaskCompletedListener() {
			@Override
			public void onComplete(boolean success, String result) {
				JSONObject jObject;
				try {
					jObject = new JSONObject(result.trim());
					System.out.println(jObject);
					
					String username = (String)jObject.get("username");
					String age = (String)jObject.get("age");
					String image = (String)jObject.get("image");
					String party = (String)jObject.get("party");
					String gender = (String)jObject.get("gender");
					
					String about = (String)jObject.get("about");
					String interest = (String)jObject.get("interest");
					String plans = (String)jObject.get("plans");

					String distance = (String)jObject.get("distance");
					String favorited = (String)jObject.get("favorited");
					String blocked = (String)jObject.get("blocked");
					
					//String lat = (String)jObject.get("lat");
					//String lng = (String)jObject.get("lng");

					String hide = (String)jObject.get("hide_profile");
					String discover = (String)jObject.get("discoverable");

					if (blocked.equals("0")) flag_Block = false;
								   else flag_Block = true;
					if (favorited.equals("0")) flag_Favorite = false;
								     else flag_Favorite = true;
					if (hide.equals("0"));
					if (discover.equals("0"));

					new GetBitmapFromURL(image, new BitmapListener() {
						@Override
						public void onComplete(Bitmap result) {
							prof_pic.setImageBitmap(result);
						}
					}).execute();

					txt1.setText(about);
			        txt2.setText(interest);
			        txt3.setText(plans);

				    prof_age.setText("AGE: " + age + 
				    		" - Distance: " + distance.substring(0, distance.indexOf(".")+2) + " miles");
				    profname.setText(username);
				    prof_sex.setText(gender);
				    partysize.setText(party);
				    tv.setText(username);

				} catch (JSONException e) {
					e.printStackTrace();
				}
				
		    	flagSetUp(id);
		    	
			}
		}).execute(id);
		
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
					if (!this.getClass().getName().contains("ProfileActivity") || !mode.equals("Me")) {
						intent = new Intent(getApplicationContext(), ProfileActivity.class);
						intent.putExtra("mode", "Me");
		        		startActivity(intent);
	
		        		chatTab.setPressed(false);
						peopleTab.setPressed(false);
						meTab.setPressed(true);
						
						meTab.setSelected(true);
						break;
					}
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

		if (mode.equals("Me")) {
			meTab.setChecked(true);
		} else
			meTab.setChecked(false);
		chatTab.setChecked(false);
		peopleTab.setChecked(false);
		
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
					if (!mode.equals("Me")) {
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

	private void flagSetUp(final String id) {
		final CheckBox f_Chat		= (CheckBox)findViewById(R.id.flag_chat);
		final CheckBox f_Favorite	= (CheckBox)findViewById(R.id.flag_favorite);
		final CheckBox f_Block		= (CheckBox)findViewById(R.id.flag_block);
		final CheckBox f_Report		= (CheckBox)findViewById(R.id.flag_report);
		
		f_Chat.setChecked(flag_Chat);
		f_Favorite.setChecked(flag_Favorite);
		f_Block.setChecked(flag_Block);
		f_Report.setChecked(flag_Report);

		OnCheckedChangeListener occl = new OnCheckedChangeListener() {

			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				switch(buttonView.getId()){
				case R.id.flag_chat:
					if (flag_Chat == false) {
						f_Chat.setChecked(true);
						flag_Chat = true;
					}
					if (flag_Chat == true) {
						f_Chat.setChecked(false);
						flag_Chat = false;
					}
					break;
				case R.id.flag_favorite:
					// User is not a favorite - Favorite
					if (flag_Favorite == false) {
						new API_AddRemove(ProfileActivity.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success) {
									flag_Favorite = true;
								}
							}
						}).execute("/favorite/add", id);
					}
					
					// User is a favorite - Unfavorite
					else if (flag_Favorite == true) {
						new API_AddRemove(ProfileActivity.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success) {
									flag_Favorite = false;
								}
							}
						}).execute("/favorite/remove", id);
					}
					break;
				case R.id.flag_block:
					// User has not been blocked - Block
					if (flag_Block == false) {
						new API_AddRemove(ProfileActivity.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success) {
									flag_Block = true;
								}
							}
						}).execute("/block/add", id);
					}
					
					// User has been blocked - Unblock
					else if (flag_Block == true) {
						new API_AddRemove(ProfileActivity.this, new OnTaskCompleteListener() {
							@Override
							public void onComplete(boolean success) {
								if(success) {
									flag_Block = false;
								}
							}
						}).execute("/block/remove", id);
					}
					break;
				case R.id.flag_report:
					if (flag_Report == false) {
						f_Report.setChecked(true);
						flag_Report = true;
					}
					if (flag_Report == true) {
						f_Report.setChecked(false);
						flag_Report = false;
					}

					Intent intent = new Intent(getApplicationContext(), ReportActivity.class);
					intent.putExtra("id", id);
	        		startActivity(intent);

				}
			}
		};
		
		f_Chat.setOnCheckedChangeListener(occl);
		f_Favorite.setOnCheckedChangeListener(occl);
		f_Block.setOnCheckedChangeListener(occl);
		f_Report.setOnCheckedChangeListener(occl);

	}
/*
	public void onResume() {
		super.onResume();
		
		if (mode.equals("Me")) {
			((CheckBox)findViewById(R.id.button3)).setChecked(true);
		} else
			((CheckBox)findViewById(R.id.button3)).setChecked(false);
		((CheckBox)findViewById(R.id.button1)).setChecked(false);
		((CheckBox)findViewById(R.id.button2)).setChecked(false);
		
	}
*/
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode!=RESULT_CANCELED)
			switch(requestCode) {
			case GlobalValues.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
				Uri imageUri = (Uri)data.getParcelableExtra("URI");
				if ((new File(getRealPathFromURI(imageUri))).length() > 1) {
					System.out.println("Creating image: "+imageUri);
	
					bitmap = null;
					try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						e.printStackTrace();
						System.gc();
					}
					
				    ImageView pic = ((ImageView)findViewById(R.id.profile_image));
				    pic.setImageBitmap(bitmap);

					imageUrl = imageUri;
				}
				break;
			case GlobalValues.MEDIA_TYPE_IMAGE:
		        Uri chosenUri = data.getData();
				System.out.println("Creating image: "+chosenUri);

				bitmap = null;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenUri);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					e.printStackTrace();
					System.gc();
				}

			    ImageView pic = ((ImageView)findViewById(R.id.profile_image));
			    pic.setImageBitmap(bitmap);

				imageUrl = chosenUri;
				
				break;
			}
		else { System.out.println("RESULT_CANCELED"); }
	}

	@SuppressWarnings("deprecation")
	public String getRealPathFromURI(Uri contentUri) {
		String[] proj = { MediaStore.Images.Media.DATA };
		Cursor cursor = managedQuery(contentUri, proj, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		return cursor.getString(column_index);
	}
	
	public String BitMapToString(Bitmap bitmap) {
		ByteArrayOutputStream baos;
        byte [] b;
        String temp=null;
        try {
	        System.gc();
	        baos = new ByteArrayOutputStream();
	        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
	        b = baos.toByteArray();
	        temp = Base64.encodeToString(b, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
        } catch (OutOfMemoryError e) {
            baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
            b = baos.toByteArray();
            temp = Base64.encodeToString(b, Base64.DEFAULT);
            GlobalValues.showAlert(this, "Out of Memory");
            System.gc();
        }
        return temp;
    }
}
