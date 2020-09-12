package com.waslabank.wasslabank.utils;

import android.util.Log;

import com.waslabank.wasslabank.BuildConfig;

public class LogUtil {

 public static void error(String tag,String message){
     if (BuildConfig.DEBUG) {
         // do something for a debug build
         if (message != null)
             Log.e(tag, message);
     }
 }
}
