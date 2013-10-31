package com.majestyk.vegas;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.widget.TextView;

public class FontManager {
 private static boolean USE_FONT = true;
 private static Typeface font;

 public static void loadFont(AssetManager assets){
  font = Typeface.createFromAsset(assets, "fonts/BebasNeue.otf");
 }

 private static Typeface getFont() {
	return font;
 }
  
 public static void setTypeFace(TextView textView){
  if(USE_FONT) {
	  textView.setTypeface(getFont());
  }
 }
}