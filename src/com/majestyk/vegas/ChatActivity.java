package com.majestyk.vegas;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.majestyk.vegas.API_Chat_GetMessages.OnTaskCompletedListener;

public class ChatActivity extends Activity {
	
	ListView messages;
	Button addUser, addImage, submit;
	View.OnClickListener ocl;
	Uri imageUrl;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat);

        ((TextView)this.findViewById(R.id.textView1)).setText(getIntent().getStringExtra("topic"));
        System.out.println(getIntent().getStringExtra("topic") + "...");
        
        messages = (ListView) this.findViewById(R.id.chatHistory);
        addUser = (Button) this.findViewById(R.id.add_button);
        addImage = (Button) this.findViewById(R.id.button1);
        submit = (Button) this.findViewById(R.id.button2);
        
        ocl = new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent;
				switch(v.getId()) {
				case R.id.add_button:
					intent = new Intent(getApplicationContext(), ChatActivity_SelectUsers.class);
					intent.putExtra("mode", "Add");
	        		startActivity(intent);	
					break;
				case R.id.button1:
					if(imageUrl == null) {
						((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.VISIBLE);
					}
					break;
				case R.id.button2:
					String message = ((EditText)findViewById(R.id.editText1)).getText().toString();
					if(imageUrl == null && message.length() >= 1) {
						
					        new API_Chat_Message(ChatActivity.this, new OnTaskCompleteListener() {
	
								@Override
								public void onComplete(boolean success) {
									((EditText)findViewById(R.id.editText1)).setText("");
									(findViewById(R.id.linearLayout2)).setVisibility(View.INVISIBLE);
									getChatMessages();
								}
								
					        }).execute(getIntent().getStringExtra("id"), message);
						}
					if(imageUrl != null && message.length() >= 1) {
						String part1 = getRealPathFromURI(imageUrl);
						File part2 = new File(part1);
						Bitmap part3 = decodeFile(part2);
						String part4 = BitMapToString(part3);
				        new API_Chat_Message(ChatActivity.this, new OnTaskCompleteListener() {

							@Override
							public void onComplete(boolean success) {
								((EditText)findViewById(R.id.editText1)).setText("");
								(findViewById(R.id.linearLayout2)).setVisibility(View.INVISIBLE);
								getChatMessages();
							}
							
				        }).execute(getIntent().getStringExtra("id"), message, 
				        		part4);
					}
					if(imageUrl != null && message.length() == 0) {
					    new API_Chat_Message(ChatActivity.this, new OnTaskCompleteListener() {

							@Override
							public void onComplete(boolean success) {
								((EditText)findViewById(R.id.editText1)).setText("");
								(findViewById(R.id.linearLayout2)).setVisibility(View.INVISIBLE);
								getChatMessages();
							}
							
				        }).execute(getIntent().getStringExtra("id"), "",
				        		BitMapToString(decodeFile(new File(getRealPathFromURI(imageUrl)))));					
					}
				
					break;
				case R.id.imageButton1:
					intent = new Intent(getApplicationContext(), AndroidCameraTestsActivity.class);
					startActivityForResult(intent, GlobalValues.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);

					((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.GONE);
					break;
				case R.id.imageButton2:
					Intent mediaChooser = new Intent(Intent.ACTION_GET_CONTENT);
					mediaChooser.setType("video/*, images/*");
					startActivityForResult(mediaChooser, 1);

					((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.GONE);
					break;
				case R.id.imageButton3:
					((RelativeLayout)findViewById(R.id.camera)).setVisibility(View.GONE);
					break;
				case R.id.imageButton4:
					((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.GONE);
					(findViewById(R.id.linearLayout2)).setVisibility(View.INVISIBLE);
					imageUrl = null;
					break;
				case R.id.imageButton5:
					((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.GONE);
					(findViewById(R.id.linearLayout2)).setVisibility(View.INVISIBLE);
					break;
				case 10101:
					((RelativeLayout)findViewById(R.id.alert)).setVisibility(View.VISIBLE);
					break;
				}
			}
        };

        addImage.setOnClickListener(ocl);
        addUser.setOnClickListener(ocl);
        submit.setOnClickListener(ocl);
        
        (findViewById(R.id.imageButton1)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton2)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton3)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton4)).setOnClickListener(ocl);
        (findViewById(R.id.imageButton5)).setOnClickListener(ocl);

        new API_Chat_GetMessages(ChatActivity.this, new OnTaskCompletedListener() {

			@Override
			public void onComplete(boolean success, String result) {
				
		        ArrayList<String> Username = new ArrayList<String>();
		        //Username.add("Me");			Username.add("You");
		        ArrayList<String> Message = new ArrayList<String>();
		        //Message.add("Hi User2!");	Message.add("Hi User1!");
		        ArrayList<String> Timestamp = new ArrayList<String>();
		        //Timestamp.add("Then");		Timestamp.add("Now");

		        try {
		        JSONArray jArray = new JSONArray (result.trim());
				
					for(int i=0; i<jArray.length(); i++) {
						JSONObject j = (JSONObject)jArray.get(i);
						System.out.println(j);
						
						Username .add((String)j.get("username"));
						Message	 .add((String)j.get("message"));
						Timestamp.add((String)j.get("timestamp"));
					}
				
		        } catch (JSONException e) {
					e.printStackTrace();
				}
				
				ChatMessageAdapter cma = new ChatMessageAdapter(ChatActivity.this, Username, Message, Timestamp);
		        messages.setAdapter(cma);    
			}
			
        }).execute(getIntent().getStringExtra("id"), "100");
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode!=RESULT_CANCELED)
			switch(requestCode) {
			case GlobalValues.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
				Uri imageUri = (Uri)data.getParcelableExtra("URI");
				if ((new File(getRealPathFromURI(imageUri))).length() > 1) {
					System.out.println("Creating image: "+imageUri);
					(findViewById(R.id.linearLayout2)).setVisibility(View.VISIBLE);
					ImageView thumbnail = new ImageView(this);
	
					Bitmap bitmap = null;
					try {
						bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
					} catch (FileNotFoundException e) {
						System.out.println("FileNotFoundException");
						e.printStackTrace();
					} catch (IOException e) {
						System.out.println("IOException");
						e.printStackTrace();
					} catch (OutOfMemoryError e) {
						System.out.println("OutOfMemoryError");
						e.printStackTrace();
						System.gc();
					}
	
					Bitmap bmThumbnail = ThumbnailUtils.extractThumbnail(bitmap, 96, 96);
					thumbnail.setImageBitmap(bmThumbnail);
					thumbnail.setOnClickListener(ocl);
					System.out.println(bmThumbnail);
					thumbnail.setId(10101);
					imageUrl = imageUri;

					LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout1);
					
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					params.gravity = Gravity.LEFT;

					ll.addView(thumbnail);
				}
				break;
			case GlobalValues.MEDIA_TYPE_IMAGE:
		        Uri chosenUri = data.getData();
				System.out.println("Creating image: "+chosenUri);
				(findViewById(R.id.linearLayout2)).setVisibility(View.VISIBLE);
				ImageView thumbnail = new ImageView(this);

				Bitmap bitmap = null;
				try {
					bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), chosenUri);
				} catch (FileNotFoundException e) {
					System.out.println("FileNotFoundException");
					e.printStackTrace();
				} catch (IOException e) {
					System.out.println("IOException");
					e.printStackTrace();
				} catch (OutOfMemoryError e) {
					System.out.println("OutOfMemoryError");
					e.printStackTrace();
					System.gc();
				}

				Bitmap bmThumbnail = ThumbnailUtils.extractThumbnail(bitmap, 96, 96);
				thumbnail.setImageBitmap(bmThumbnail);
				thumbnail.setOnClickListener(ocl);
				System.out.println(bmThumbnail);
				thumbnail.setId(10101);
				imageUrl = chosenUri;

				LinearLayout ll = (LinearLayout)findViewById(R.id.linearLayout1);
				
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				params.gravity = Gravity.LEFT;

				ll.addView(thumbnail);
				
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

	public void getChatMessages() {
	    new API_Chat_GetMessages(ChatActivity.this, new OnTaskCompletedListener() {
	
			@Override
			public void onComplete(boolean success, String result) {
				
		        ArrayList<String> Username = new ArrayList<String>();
		        ArrayList<String> Message = new ArrayList<String>();
		        ArrayList<String> Timestamp = new ArrayList<String>();
	
		        try {
		        JSONArray jArray = new JSONArray (result.trim());
				
					for(int i=0; i<jArray.length(); i++) {
						JSONObject j = (JSONObject)jArray.get(i);
						System.out.println(j);
						
						Username .add((String)j.get("username"));
						Message	 .add((String)j.get("message"));
						Timestamp.add((String)j.get("timestamp"));
					}
				
		        } catch (JSONException e) {
					e.printStackTrace();
				}
				
				ChatMessageAdapter cma = new ChatMessageAdapter(ChatActivity.this, Username, Message, Timestamp);
		        messages.setAdapter(cma);    
			}
			
	    }).execute(getIntent().getStringExtra("id"), "100");
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
            GlobalValues.showAlert(ChatActivity.this, "Out of Memory");
            System.gc();
        }
        return temp;
    }
	
	public Bitmap decodeFile(File f) {
	    try {
	        //Decode image size
	        BitmapFactory.Options o = new BitmapFactory.Options();
	        o.inJustDecodeBounds = true;
	        BitmapFactory.decodeStream(new FileInputStream(f), null, o);

	        //The new size we want to scale to
	        final int REQUIRED_SIZE=200;

	        //Find the correct scale value. It should be the power of 2.
	        int scale=1;
	        while(o.outWidth/scale/2>=REQUIRED_SIZE && o.outHeight/scale/2>=REQUIRED_SIZE)
	            scale*=2;

	        //Decode with inSampleSize
	        BitmapFactory.Options o2 = new BitmapFactory.Options();
	        o2.inSampleSize=scale;
	        return BitmapFactory.decodeStream(new FileInputStream(f), null, o2);
	    } catch (FileNotFoundException e) {}
	    return null;
	}
}
