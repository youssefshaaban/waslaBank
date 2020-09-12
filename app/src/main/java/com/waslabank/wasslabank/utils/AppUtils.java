package com.waslabank.wasslabank.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class AppUtils {


    public static final String GOOGLE_KEY="AIzaSyCE29pCYj3ntftgARbTP0FA8xZyLBCF7f8";
    public static void openCall(Context context, String mobileNumber){
        Intent intent = new Intent(Intent.ACTION_CALL, Uri
                .parse("tel:" + mobileNumber));
        context.startActivity(intent);
    }
}
