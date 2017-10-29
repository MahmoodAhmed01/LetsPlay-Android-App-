package com.letsplay.letsplay;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.letsplay.letsplay.models.User;
import com.zookey.universalpreferences.UniversalPreferences;

import org.json.JSONException;
import org.json.JSONObject;


public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    public static final String KEY_USER_LOGIN = "user_login";
    private CallbackManager mCallbackManager;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListner;
    DatabaseReference dbRef;
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

        Boolean aBoolean = UniversalPreferences.getInstance().get(KEY_USER_LOGIN, false);
        if (aBoolean){
            startMainActivity();
            return;
        }

        //printHashKey();

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

        mAuth = FirebaseAuth.getInstance();

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

        final FirebaseUser user = mAuth.getCurrentUser();
        if(user != null) {
            handleUser(user);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {

        }
    }

    private void handleUser(final FirebaseUser user) {

        dbRef = FirebaseDatabase.getInstance().getReference("users");
        Query query = dbRef.orderByChild("userId").equalTo(mAuth.getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    User savedUser = dataSnapshot.child(user.getUid()).getValue(User.class);
                    if (savedUser.getGames() != null) {
                        loginSuccessful();
                        startMainActivity();
                    } else {
                        startSportsActivity();
                    }
                    Log.d(TAG, "check this snapshot" + dataSnapshot.toString());
                } else {
                    LoginActivity.this.user.setUserId(mAuth.getCurrentUser().getUid());
                    dbRef.child(user.getUid()).setValue(LoginActivity.this.user);
                    startSportsActivity();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {

                            // Sign in success, update UI with the signed-in user's information

                            Log.d(TAG, "signInWithCredential:success");
                            final FirebaseUser user = mAuth.getCurrentUser();
                            handleUser(user);


                            //childUserRef.setValue(data);


                            //fetch data from database to decide that which activity has to be launched


                            updateUI();
                            loggedIn.setText("You have successfully logged in");


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }

                        // ...
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

                        handleFacebookAccessToken(userAccessToken);
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

    private void loginSuccessful(){
        UniversalPreferences.getInstance().put(KEY_USER_LOGIN, true);
    }

    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        this.finish();
    }


     /* public void printHashKey() {
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
    }*/

}
