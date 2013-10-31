package com.majestyk.vegas;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

public class AndroidCameraTestsActivity extends Activity 
{
	private static final String TAG = AndroidCameraTestsActivity.class.getSimpleName(); 
	
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = GlobalValues.CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE;
	public static final int MEDIA_TYPE_IMAGE = GlobalValues.MEDIA_TYPE_IMAGE;

    private Uri fileUri;
    
    /** Called when the activity is first created. **/
    @Override
    public void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);

        // give the image a name so we can store it in the phone's default location
    	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(new Date());
    	
        ContentValues values = new ContentValues();
		values.put(MediaStore.Images.Media.TITLE, "IMG_" + timeStamp + ".jpg");

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        
        //fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image (this doesn't work at all for images)
        fileUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values); // store content values
        
		intent.putExtra(MediaStore.EXTRA_OUTPUT,  fileUri); 
       
        // start the image capture Intent
        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) 
    {
    	super.onActivityResult(requestCode, resultCode, data);
    	
    	Intent res = new Intent();
    	
        if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
            	
				if(fileUri != null) {
					res.putExtra("URI", fileUri);
					Log.d(TAG, "Image saved to:\n" + fileUri);
					Log.d(TAG, "Image path:\n" + fileUri.getPath());
					Log.d(TAG, "Image name:\n" + getName(fileUri)); // use uri.getLastPathSegment() if store in folder
				}
                
            } else if (resultCode == RESULT_CANCELED) {
                // User cancelled the image capture
            } else {
                // Image capture failed, advise user
            }
            setResult(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE, res);

        }

        res.putExtra("URI", fileUri);
        res.putExtra("INT", 1);
        setResult(CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE, res);
        finish();
    }
    
    /** Create a file Uri for saving an image or video to specific folder
     * https://developer.android.com/guide/topics/media/camera.html#saving-media
     * */
    @SuppressWarnings("unused")
	private static Uri getOutputMediaFileUri(int type)
    {
          return Uri.fromFile(getOutputMediaFile(type));
    }

    /** Create a File for saving an image **/
    private static File getOutputMediaFile(int type)
    {
        // To be safe, you should check that the SDCard is mounted
        
    	if(Environment.getExternalStorageState() != null) {
    		// this works for Android 2.2 and above
    		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "AndroidCameraTestsFolder");
            
            // This location works best if you want the created images to be shared
            // between applications and persist after your app has been uninstalled.

            // Create the storage directory if it does not exist
            if (! mediaStorageDir.exists()) {
                if (! mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "failed to create directory");
                    return null;
                }
            }

            // Create a media file name
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",Locale.US).format(new Date());
            File mediaFile;
            if (type == MEDIA_TYPE_IMAGE){
                mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");
            } else {
                return null;
            }

            return mediaFile;
    	}
        
    	return null;
    }

    // grab the name of the media from the Uri
    @SuppressWarnings("deprecation")
	protected String getName(Uri uri) 
	{
		String filename = null;

		try {
			String[] projection = { MediaStore.Images.Media.DISPLAY_NAME };
			Cursor cursor = managedQuery(uri, projection, null, null, null);

			if(cursor != null && cursor.moveToFirst()){
				int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
				filename = cursor.getString(column_index);
			} else {
				filename = null;
			}
		} catch (Exception e) {
			Log.e(TAG, "Error getting file name: " + e.getMessage());
		}

		return filename;
	}
    
}
