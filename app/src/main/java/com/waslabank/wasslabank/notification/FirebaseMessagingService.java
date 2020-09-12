package com.waslabank.wasslabank.notification;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;

import com.android.volley.VolleyError;
import com.google.firebase.messaging.RemoteMessage;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.models.MyRideModel;
import com.waslabank.wasslabank.models.NotificationModel;
import com.waslabank.wasslabank.models.RideModel;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.ui.ConfirmRideRequestActivity;
import com.waslabank.wasslabank.ui.GroupChatActivity;
import com.waslabank.wasslabank.ui.SplashActivity;
import com.waslabank.wasslabank.ui.WhereYouGoActivity;
import com.waslabank.wasslabank.ui.ride_details.RideDetailsActivity;
import com.waslabank.wasslabank.utils.Helper;

import java.util.Map;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService {
    private static final String TAG = "FCM Service";

    Connector connector;
    MyRideModel myRideModel;
    RideModel rideModel;

    Intent resultIntent;

    NotificationCompat.Builder mBuilder;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {


        Map<String, String> notificationMessage = remoteMessage.getData();
        mBuilder = new NotificationCompat.Builder(this, "0");
        mBuilder.setSmallIcon(R.drawable.ic_notification_image);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText(notificationMessage.get("body")));
        mBuilder.setContentTitle(notificationMessage.get("title"));
        mBuilder.setContentText(notificationMessage.get("body"));
        mBuilder.setChannelId("0");
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setDefaults(0);
        mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.plucky));
        int color = ContextCompat.getColor(this, android.R.color.white);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(color);
            mBuilder.setSmallIcon(R.drawable.ic_notification_image);
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_notification_image);
        }
        resultIntent = new Intent(this, SplashActivity.class);
        if (notificationMessage.containsKey("targetScreen")) {
            Helper.writeToLog(notificationMessage.get("targetScreen"));
            if (notificationMessage.get("targetScreen").equals("chat")) {
                resultIntent = new Intent(this, SplashActivity.class)
                        .putExtra("chat_id", notificationMessage.get("chat_id"))
                        .putExtra("request_id", notificationMessage.get("request_id"))
                        .putExtra("goToChat", true);
            } else if (notificationMessage.get("targetScreen").equals("offer")) {
                resultIntent = new Intent(this, SplashActivity.class).putExtra("goToNotification", true);
            } else if (notificationMessage.get("targetScreen").equals("request")) {
                resultIntent = new Intent(this, RideDetailsActivity.class).putExtra("request_id", notificationMessage.get("request_id"));
            }else if (notificationMessage.get("targetScreen").equals("request_cancel")){
                resultIntent = new Intent(this, SplashActivity.class);
            }else if (notificationMessage.get("targetScreen").equals("reject_offer")){
                resultIntent = new Intent(this, RideDetailsActivity.class).putExtra("request_id", notificationMessage.get("request_id"));
            }else if (notificationMessage.get("targetScreen").equals("kill")){
                resultIntent = new Intent(this, WhereYouGoActivity.class);
            }
            else if (notificationMessage.get("targetScreen").equals("group_message")) {
                Intent intent = new Intent("groupMessage");
                intent.putExtra("user_id", notificationMessage.get("user_id"));
                intent.putExtra("chat_id", notificationMessage.get("chat_id"));
                if (!LocalBroadcastManager.getInstance(this).sendBroadcast(intent)) {
                    sendGroupNotification(notificationMessage.get("chat_id"));
                    return;
                }
            } else {
                resultIntent = new Intent(this, SplashActivity.class).putExtra("goToNotification", true);
            }
            NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (!notificationMessage.get("targetScreen").equals("request")) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel("0", "test", NotificationManager.IMPORTANCE_HIGH);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(mChannel);
                    }
                }
            }else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    NotificationChannel mChannel = new NotificationChannel("0", "test", NotificationManager.IMPORTANCE_HIGH);
                    if (mNotificationManager != null) {
                        mNotificationManager.createNotificationChannel(mChannel);
                    }
                }
            }
            resultIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            PendingIntent resultPendingIntent = PendingIntent.getActivity(this,0,resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder.setContentIntent(resultPendingIntent);
            mBuilder.setAutoCancel(true);
            int random = (int) System.currentTimeMillis();
            if (mNotificationManager != null) {
                mNotificationManager.notify(random, mBuilder.build());
            }

        }



    }

    private void sendGroupNotification(String chatId) {
        mBuilder = new NotificationCompat.Builder(this, "0");


        mBuilder.setSmallIcon(R.drawable.ic_notification_image);
        mBuilder.setDefaults(NotificationCompat.DEFAULT_SOUND);
        mBuilder.setStyle(new NotificationCompat.BigTextStyle()
                .bigText("New Group Message"));
        mBuilder.setContentTitle("Group Message");
        mBuilder.setContentText("New Group Message");
        mBuilder.setChannelId("0");
        mBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        mBuilder.setDefaults(0);
        mBuilder.setSound(Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.plucky));
        int color = ContextCompat.getColor(this, android.R.color.white);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mBuilder.setColor(color);
            mBuilder.setSmallIcon(R.drawable.ic_notification_image);
        } else {
            mBuilder.setSmallIcon(R.drawable.ic_notification_image);
        }
        resultIntent = new Intent(this, GroupChatActivity.class).putExtra("group_id", chatId);


        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);


        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addParentStack(SplashActivity.class);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel mChannel = new NotificationChannel("0", "test", NotificationManager.IMPORTANCE_HIGH);
            if (mNotificationManager != null) {
                mNotificationManager.createNotificationChannel(mChannel);
            }
        }

        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(resultPendingIntent);
        mBuilder.setAutoCancel(true);


        int random = (int) System.currentTimeMillis();
        if (mNotificationManager != null) {
            mNotificationManager.notify(random, mBuilder.build());
        }
    }
}
