package com.waslabank.wasslabank.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.provider.Settings;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import com.android.volley.VolleyError;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.utils.Helper;

public class SplashActivity extends AppCompatActivity {

    final int PERMISSIONS_REQUEST = 1;
    boolean isGPSEnabled;
    boolean isNetworkEnabled;

    Connector mConnector;
    ProgressDialog mProgressDialog;
    private final String TAG = SplashActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splahsh_activity);
//        Log.e("TAGFire",FirebaseInstanceId.getInstance().getToken());
        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    Helper.saveUserToSharedPreferences(SplashActivity.this, Connector.getUser(response));
                    if (getIntent() != null && getIntent().hasExtra("goToChat")) {
                        if (getIntent().getBooleanExtra("goToChat", false)) {
                            startActivity(new Intent(SplashActivity.this, ChatActivity.class)
                                    .putExtra("chat_id", getIntent().getStringExtra("chat_id"))
                                    .putExtra("request_id", getIntent().getStringExtra("request_id"))
                                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    } else if (getIntent() != null && getIntent().hasExtra("goToNotification")) {
                        if (getIntent().getBooleanExtra("goToNotification", false)) {
                            startActivity(new Intent(SplashActivity.this, NotificationsActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                        }
                    } else {
                        startActivity(new Intent(SplashActivity.this, WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                    }
                } else {
                    Helper.removeUserFromSharedPreferences(SplashActivity.this);
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                    finish();
                    //Helper.showSnackBarMessage(getString(R.string.error_in_credentials), SplashActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), SplashActivity.this);
            }
        });

    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST: {

                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED
                        && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    if (!isGPSEnabled && !isNetworkEnabled) {
                        showSettingsAlert();
                    } else {
                        FirebaseInstanceId.getInstance().getInstanceId()
                                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                        if (task.getResult() != null) {
                                            String token = task.getResult().getToken();
                                            Helper.saveTokenToSharePreferences(SplashActivity.this, token);
                                        }
                                        if (Helper.preferencesContainsUser(SplashActivity.this)) {
                                            mProgressDialog = Helper.showProgressDialog(SplashActivity.this, getString(R.string.loading), false);
                                            mConnector.getRequest(TAG, "https://code-grow.com/waslabank/api/login?password=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getPassword()) + "&username=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getUsername()) + "&token=" + Helper.getTokenFromSharedPreferences(SplashActivity.this));
                                        } else {
                                            startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                            finish();
                                        }
                                    }
                                });
                    }
                } else {
                    finish();
                }
            }
        }
    }


    public void showSettingsAlert() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);

        // Setting Dialog Title
        alertDialog.setTitle("Location Settings");

        // Setting Dialog Message
        alertDialog.setMessage("Location is disabled, Please Turn it on in High Accuracy");

        // Setting Icon to Dialog
        //alertDialog.setIcon(R.drawable.delete);

        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
                dialog.cancel();
            }
        });

        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                finish();
                dialog.cancel();
            }
        });

        // Showing Alert Message
        alertDialog.show();

    }

    @Override
    protected void onStart() {
        super.onStart();
        LocationManager locationManager = (LocationManager) SplashActivity.this
                .getSystemService(Context.LOCATION_SERVICE);

        // getting GPS status
        isGPSEnabled = locationManager
                .isProviderEnabled(LocationManager.GPS_PROVIDER);

        // getting network status
        isNetworkEnabled = locationManager
                .isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        if (ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat
                .checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION
                            , Manifest.permission.CALL_PHONE},
                    PERMISSIONS_REQUEST);

        } else {
            if (!isGPSEnabled && !isNetworkEnabled) {
                showSettingsAlert();
            } else {
                FirebaseInstanceId.getInstance().getInstanceId()
                        .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                            @Override
                            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                                if (task.getResult() != null) {
                                    String token = task.getResult().getToken();
                                    Helper.saveTokenToSharePreferences(SplashActivity.this, token);
                                }
                                if (Helper.preferencesContainsUser(SplashActivity.this)) {
                                    mProgressDialog = Helper.showProgressDialog(SplashActivity.this, getString(R.string.loading), false);
                                    mConnector.getRequest(TAG, "https://code-grow.com/waslabank/api/login?password=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getPassword()) + "&username=" + Uri.encode(Helper.getUserSharedPreferences(SplashActivity.this).getUsername()) + "&token=" + Helper.getTokenFromSharedPreferences(SplashActivity.this));
                                } else {
                                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                                    finish();
                                }
                            }
                        });
            }
        }
    }
}
