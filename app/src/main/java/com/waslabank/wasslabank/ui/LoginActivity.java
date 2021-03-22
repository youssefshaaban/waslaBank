package com.waslabank.wasslabank.ui;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.VolleyError;
import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.waslabank.wasslabank.R;
import com.waslabank.wasslabank.networkUtils.Connector;
import com.waslabank.wasslabank.utils.Helper;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.sign_up_button)
    View mSignUpButton;
    @BindView(R.id.login_button)
    Button mLoginButton;
    @BindView(R.id.facebook_login)
    Button mFacebookLogin;
    @BindView(R.id.google_login)
    Button mGoogleLogin;
    @BindView(R.id.email)
    EditText mEmailEditText;
    @BindView(R.id.password)
    EditText mPasswordEditText;

    CallbackManager callbackManager;
    GoogleSignInClient mGoogleSignInClient;

    Connector mConnector;
    Connector mConnectorSocial;
    Connector mConnectorSignUp;

    ProgressDialog mProgressDialog;

    String mEmail;
    String mName;
    String mImage;

    private final String TAG = LoginActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

           //printHashKey(this);
        ////
        initFacebookLogin();
        //  LoginManager.getInstance().logOut();
        Log.d("TTT", "onCreate:++++ ");
        Log.d("TTTTTTTT", "onCreate: ");
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.waslabank.wasslabank",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }

        ///


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


        mConnector = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    Helper.saveUserToSharedPreferences(LoginActivity.this, Connector.getUser(response));
                    startActivity(new Intent(LoginActivity.this, WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    Helper.showSnackBarMessage(getString(R.string.error_in_credentials), LoginActivity.this);
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), LoginActivity.this);
            }
        });

        mConnectorSocial = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                if (Connector.checkStatus(response)) {
                    mProgressDialog.dismiss();
                    Helper.saveUserToSharedPreferences(LoginActivity.this, Connector.getUser(response));
                    startActivity(new Intent(LoginActivity.this, WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                } else {
                    mConnectorSignUp.getRequest(TAG, "https://code-grow.com/waslabank/api/signup?password=" + Uri.encode("") + "&username=" + mEmail + "&mobile=" + "" + "&name=" + Uri.encode(mName) + "&image=" + mImage + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));

                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), LoginActivity.this);
            }
        });

        mSignUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
            }
        });

        mLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Helper.hideKeyboard(LoginActivity.this, view);
                mEmail = mEmailEditText.getText().toString();
                if (mEmailEditText.getText().toString().isEmpty()) {
                    Helper.showSnackBarMessage(getString(R.string.mobile), LoginActivity.this);
                } else if (mPasswordEditText.getText().toString().isEmpty()) {
                    Helper.showSnackBarMessage(getString(R.string.enter_password), LoginActivity.this);
                } else {
                    mProgressDialog = Helper.showProgressDialog(LoginActivity.this, getString(R.string.loading), false);
                    mConnector.getRequest(TAG, "https://code-grow.com/waslabank/api/login?password=" + Uri.encode(mPasswordEditText.getText().toString()) + "&username=" + mEmailEditText.getText().toString() + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));
                }
            }
        });

        mFacebookLogin.setOnClickListener(view ->
            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile","email"))
        );


        mConnectorSignUp = new Connector(this, new Connector.LoadCallback() {
            @Override
            public void onComplete(String tag, String response) {
                mProgressDialog.dismiss();
                if (Connector.checkStatus(response)) {
                    Helper.saveUserToSharedPreferences(LoginActivity.this, Connector.getUser(response));
                    startActivity(new Intent(LoginActivity.this, WhereYouGoActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
                }
            }
        }, new Connector.ErrorCallback() {
            @Override
            public void onError(VolleyError error) {
                mProgressDialog.dismiss();
                Helper.showSnackBarMessage(getString(R.string.error), LoginActivity.this);

            }
        });

        mGoogleLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mGoogleSignInClient.signOut().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mGoogleSignInClient.revokeAccess().addOnCompleteListener(LoginActivity.this, new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                signIn();
                                Log.d("TTTTT", "onComplete: " + task.getException());
                            }
                        });
                    }
                });
            }
        });

    }

    private void useLoginInformation(AccessToken accessToken) {

        GraphRequest request = GraphRequest.newMeRequest(accessToken, (object, response) -> {
            try {
                mEmail = object.optString("email");
                mName = object.optString("name");
                mImage = "https://graph.facebook.com/" + object.optString("id") + "/picture?type=large";
                mProgressDialog = Helper.showProgressDialog(LoginActivity.this, getString(R.string.loading), false);
                mConnectorSocial.getRequest(TAG, "https://code-grow.com/waslabank/api/login?" + "username=" + mEmail + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));
            } catch (Exception e) {
                e.printStackTrace();
                Crashlytics.logException(e);
            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,email,picture.width(200)");
        request.setParameters(parameters);
        // Initiate the GraphRequest
        request.executeAsync();
    }

    private void initFacebookLogin() {
        callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        //  hideLoading();
                        Log.d(TAG, "FACEBOOK_SUCCESS");
                        AccessToken accessToken = loginResult.getAccessToken();

                        if (accessToken != null)
                            useLoginInformation(accessToken);
                    }

                    @Override
                    public void onCancel() {
                        Log.e(TAG, "FACEBOOK_CANCELLED");
                        //hideLoading();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        //hideLoading();
                        Log.e(TAG, "FacebookException: " + exception.toString());
                        if (exception instanceof FacebookAuthorizationException) {
                            if (AccessToken.getCurrentAccessToken() != null) {
                                LoginManager.getInstance().logOut();
                            }
                        }
                    }
                });
    }


    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 2);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        callbackManager.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            if (account != null) {
                mEmail = account.getEmail();
                mName = account.getDisplayName();
                if (account.getPhotoUrl() != null)
                    mImage = account.getPhotoUrl().toString();
                else
                    mImage = "";
                if (mEmail != null) {
                    mProgressDialog = Helper.showProgressDialog(LoginActivity.this, getString(R.string.loading), false);
                    mConnectorSocial.getRequest(TAG, "https://code-grow.com/waslabank/api/login?" + "username=" + mEmail + "&token=" + Helper.getTokenFromSharedPreferences(LoginActivity.this));
                }
            }

        } catch (ApiException e) {
            e.printStackTrace();
            Log.w(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (mConnector != null)
            mConnector.cancelAllRequests(TAG);
    }

    public void openEmail(View view) {
        mEmailEditText.setFocusableInTouchMode(true);
        mEmailEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mEmailEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public void openPassword(View view) {
        mPasswordEditText.setFocusableInTouchMode(true);
        mPasswordEditText.requestFocus();
        final InputMethodManager inputMethodManager = (InputMethodManager) LoginActivity.this
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.showSoftInput(mPasswordEditText, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    public static void printHashKey(Context pContext) {
        try {
            PackageInfo info = pContext.getPackageManager().getPackageInfo(pContext.getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i("KeyHash", "printHashKey() Hash Key: " + hashKey);
            }
//            byte[] sha1 = {
//                    0x6A, 0x43, 0x0F, 0x44, (byte) 0xFA, (byte) 0x83, (byte) 0xDF, (byte) 0x8C, (byte) 0xF0, 0x26, (byte) 0xFE, 0x20, 0x65, (byte)0xE8, 0x7D, 0x3E, (byte)0x3C, (byte)0x5A, 0x19, (byte)0x9F
//            };
//            Log.e("keyhash111", Base64.encodeToString(sha1, Base64.NO_WRAP));
        } catch (Exception e) {
            Log.e("", "printHashKey()", e);
        }
    }
}
