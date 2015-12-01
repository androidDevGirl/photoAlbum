package com.dr_group.photoalbum;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.CharacterPickerDialog;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.Parse;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.security.Permissions;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button facebookLoginButton;
    ParseUser user0;
    public static final List<String> mPermissions = new ArrayList<String>() {{
        add("public_profile");
        add("email");
    }};
    ParseUser parseUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //ParseFacebookUtils.initialize(this);
/*        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {

                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        // App code
                        Log.d("test","Succcessfull login");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        Log.d("test","Cancel login");
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        Log.d("test","Error login");
                    }

                });


*/



        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        Parse.initialize(this, "bBgMOzOIJG1axq5iGXElmkOpZ2Yte9MtahJU7tbA", "lKnjWeIDCAEpeQaANYiYsePfMNUOd23WshBiBFBI");
        ParseFacebookUtils.initialize(this);
        FacebookSdk.sdkInitialize(getApplicationContext());


        //facebookLoginButton = (Button) findViewById(R.id.facebook_login);
//        ParseFacebookUtils.initialize(this);
        //FacebookSdk.sdkInitialize(getApplicationContext());
        //List<String> permissions = Arrays.asList("public_profile", "user_friends");



 //-----------------------------------------------------------------------
 /*       ParseFacebookUtils.logInWithReadPermissionsInBackground(this, mPermissions, new LogInCallback() {

            @Override
            public void done(ParseUser user, ParseException err) {
                Log.d("test", "working1!");

                if (user == null) {
                    Log.d("test", "Uh oh. The user cancelled the Facebook login.");
                    Toast.makeText(getApplicationContext(), err.getLocalizedMessage(), 1000).show();
                } else if (user.isNew()) {
                    Log.d("test", "User signed up and logged in through Facebook!");
                } else {
                    Log.d("test", "User logged in through Facebook!");
                }
                Log.d("test", "working!");
                Log.d("test", "working2!");
                Log.d("test", "error from login:"+err);
            }

        });

*/


        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);




        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CallbackManager callbackManager = CallbackManager.Factory.create();
        LoginButton loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("user_friends");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            public void onSuccess(LoginResult loginResult) {
                // App code
                Log.d("test", "Succcessfull login");
            }

            @Override
            public void onCancel() {
                // App code
                Log.d("test", "Cancel Login");

            }

            @Override
            public void onError(FacebookException exception) {
                // App code
                Log.d("test", "Error login");

            }
        });



        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivity.this, mPermissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("test", "DD Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("test", "DD, from FB User signed up and logged in through Facebook!");
                            getUserDetailsFromFB();
                        } else {
                            Log.d("test", "DD, from Parse  User logged in through Facebook!");
                            getUserDetailsFromParse();
                        }
                    }
                });
            }
        });


    }

    private void getUserDetailsFromParse() {
        parseUser = ParseUser.getCurrentUser();

        Log.d("test", "email is= " + parseUser.getEmail());
        Log.d("test", "name is= " + parseUser.getUsername());

        Toast.makeText(MainActivity.this, "Welcome back " + parseUser.getUsername().toString(), Toast.LENGTH_SHORT).show();

    }

    private void getUserDetailsFromFB() {
        Log.d("test","Begin get user detail from FB");

        new GraphRequest(
                AccessToken.getCurrentAccessToken(),
                "/me",
                null,
                HttpMethod.GET,
                new GraphRequest.Callback() {
                    public void onCompleted(GraphResponse response) {
            /* handle the result */
                        try {
                            Log.d("test","try of get user detail from FB"+ response.getJSONObject().toString());

                            //String  email = response.getJSONObject().getString("email");
                            String  email = response.getJSONObject().optString("email");
                           // mEmailID.setText(email);
                           String  name = response.getJSONObject().getString("name");
                           // mUsername.setText(name);

                            //saveNewUser();
                            parseUser = ParseUser.getCurrentUser();
                            parseUser.setUsername(name);
                            parseUser.setEmail(email);

                            Log.d("test","email= "+email);
                            Log.d("test", "name= "+name);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Log.d("test","ERROR: "+e);
                        }
                        Log.d("test","in facebook retrieval");
                    }

                }
        ).executeAsync();
    }

    private void saveNewUser() {
 /*       parseUser = ParseUser.getCurrentUser();
        parseUser.setUsername(name);
        parseUser.setEmail(email);
//        Saving profile photo as a ParseFile
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Bitmap bitmap = ((BitmapDrawable) mProfileImage.getDrawable()).getBitmap();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        byte[] data = stream.toByteArray();
        String thumbName = parseUser.getUsername().replaceAll("\\s+", "");
        final ParseFile parseFile = new ParseFile(thumbName + "_thumb.jpg", data);
        parseFile.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                parseUser.put("profileThumb", parseFile);
                //Finally save all the user details
                parseUser.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        Toast.makeText(MainActivity.this, "New user:" + name + " Signed up", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
  */  }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        boolean ok = ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);
        if (ok) Log.d("test", "onActivity ok");
        else Log.d("test", "onActivity NOT ok");

    }
}
