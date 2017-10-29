package com.letsplay.letsplay;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.login.widget.ProfilePictureView;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.letsplay.letsplay.models.User;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONException;
import org.json.JSONObject;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String KEY_USER_LOGIN = "user_login";
    private CallbackManager mCallbackManager;
    private ProfilePictureView profilePictureView;
    private LinearLayout infoLayout;
    private LinearLayout loginLayout;

    TextView fb_name;
    TextView loggedIn;
    LoginButton loginButton;
    TextView appName;

    AccessToken userAccessToken;

    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);


        String status = UniversalPreferences.getInstance().get(KEY_USER_LOGIN, "CONTINUE");
        if (status.equals("CONTINUE")) {

        } else if (status.equals("SUCCESSFUL_LOGIN")) {
            startMainActivityWithoutDelay();
            return;
        } else if (status.equals("SELECT_GAME")) {
            startSportsActivity();
            return;
        }

        Log.d("statusdivalue", status);


        printHashKey();

        appName = (TextView) findViewById(R.id.app_name);
        profilePictureView = (ProfilePictureView) findViewById(R.id.fb_image);
        fb_name = (TextView) findViewById(R.id.fb_name);
        loggedIn = (TextView) findViewById(R.id.logged_in);
        infoLayout = (LinearLayout) findViewById(R.id.info_layout);
        loginLayout = (LinearLayout) findViewById(R.id.lets_play);

        if (getIntent().hasExtra("logout")) {
            appName.setText("Lets Play..!");
            LoginManager.getInstance().logOut();
        }

        loginButton = (LoginButton) findViewById(R.id.fb_login_btn);
        loginButton.setReadPermissions("public_profile");


        mCallbackManager = CallbackManager.Factory.create();
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                infoLayout.setVisibility(View.VISIBLE);
                loginLayout.setVisibility(View.GONE);

                userAccessToken = loginResult.getAccessToken();
                getUserProfile(userAccessToken);
            }

            @Override
            public void onCancel() {
                appName.setText("Login Cancled..!");
            }

            @Override
            public void onError(FacebookException error) {
                appName.setText("Error Occur..!");
            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token, final User user) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        updateUI();

        String url = App.url + "players";

        Ion.with(this)
                .load("POST", url)
                .setJsonPojoBody(user)
                .as(User.class)
                .setCallback(new FutureCallback<User>() {
                    @Override
                    public void onCompleted(Exception e, User user1) {
                        if (e != null) {
                            Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            UniversalPreferences.getInstance().put("userId", user1.getId());
                            UniversalPreferences.getInstance().put("userName", user1.getName());
                            UniversalPreferences.getInstance().put("pictureUrl", user1.getPictureUrl());
                            if (user1.getGameId() != null) {
                                UniversalPreferences.getInstance().put("selectedGame", user1.getGameId());
                                UniversalPreferences.getInstance().put(KEY_USER_LOGIN, "SUCCESSFUL_LOGIN");
                                startMainActivityWithoutDelay();
                            } else {
                                UniversalPreferences.getInstance().put(KEY_USER_LOGIN, "SELECT_GAME");
                                startSportsActivity();
                            }
                        }

                    }
                });
    }

    private void updateUI() {
        fb_name.setText(user.getName());
        profilePictureView.setPresetSize(ProfilePictureView.NORMAL);
        profilePictureView.setProfileId(user.getFacebookId());
        infoLayout.setVisibility(View.VISIBLE);
        loginLayout.setVisibility(View.INVISIBLE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
    }

    private void startSportsActivity() {
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent selectSports = new Intent(getApplicationContext(), SportsSelectionActivity.class);
                startActivity(selectSports);
                LoginActivity.this.finish();
            }
        }, 0000);
    }

    public void getUserProfile(final AccessToken accessToken) {
        GraphRequest request = GraphRequest.newMeRequest(
                accessToken,
                new GraphRequest.GraphJSONObjectCallback() {

                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse response) {
                        Log.v("Main", response.toString());

                        Log.d(TAG, "check kr yar" + jsonObject.toString());


                        user = new User();
                        user.setFacebookId(getJsonValue(jsonObject, "id"));
                        user.setName(getJsonValue(jsonObject, "name"));
                        user.setPictureUrl("https://graph.facebook.com/" + user.getFacebookId() + "/picture?type=normal");
                        user.setGender(getJsonValue(jsonObject, "gender"));
                        user.setDob(getJsonValue(jsonObject, "birthday"));

                        handleFacebookAccessToken(userAccessToken, user);
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name");
        request.setParameters(parameters);
        request.executeAsync();
    }


    private String getJsonValue(JSONObject object, String key) {
        if (object.has(key)) {
            try {
                return object.getString(key);
            } catch (JSONException e) {
                return null;
            }
        } else {
            return null;
        }
    }

    private void startMainActivity() {
        Handler handler = new Handler(getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(mainActivity);
                LoginActivity.this.finish();
            }
        }, 2000);
    }

    private void startMainActivityWithoutDelay() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        LoginActivity.this.finish();

    }


      public void printHashKey() {
        try {
            PackageInfo info = this.getPackageManager().getPackageInfo("com.letsplay.letsplay", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String hashKey = new String(Base64.encode(md.digest(), 0));
                Log.i(TAG, "printHashKey() Hash Key: " + hashKey);
            }
        } catch (NoSuchAlgorithmException e) {
            Log.e(TAG, "printHashKey()", e);
        } catch (Exception e) {
            Log.e(TAG, "printHashKey()", e);
        }
    }

}
