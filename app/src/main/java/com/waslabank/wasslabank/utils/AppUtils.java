package com.waslabank.wasslabank.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AppUtils {


    public static final String GOOGLE_KEY="AIzaSyA9Qod1kZPKd8xsRSyoYqBXh0YZsnI0fhk";
    public static void openCall(Context context, String mobileNumber){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri
                .parse("tel:" + mobileNumber));
        context.startActivity(intent);
    }
}
