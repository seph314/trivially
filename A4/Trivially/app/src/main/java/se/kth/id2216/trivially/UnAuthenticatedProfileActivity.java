package se.kth.id2216.trivially;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;



public class UnAuthenticatedProfileActivity extends Activity implements
        View.OnClickListener {

    SharedPreferences sharedPrefs;

    // declare_auth
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_authenticated_profile);

        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        if(sharedPrefs.getBoolean("authenticated",false)){
            redirectToAuthenticatedProfile();
        }
        else
            setupActivity();

        // Button listeners
        findViewById(R.id.signInButton).setOnClickListener(this);
        findViewById(R.id.logOutBtn).setOnClickListener(this);

        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);


    }

    private void setupActivity(){
        TextView gameButton = findViewById(R.id.gameNav);
        TextView profileButton = findViewById(R.id.highScoreNav);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnAuthenticatedProfileActivity.this,
                        GameActivity.class);
                startActivity(intent);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(UnAuthenticatedProfileActivity.this,
                        HighScoreActivity.class);
                startActivity(intent);
            }
        });

        final Button loginButton = findViewById(R.id.logInBtn);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("token", "2312312312312");
                editor.putBoolean("authenticated", true);
                editor.commit();
                redirectToAuthenticatedProfile();
            }
        });
    }

   private void redirectToAuthenticatedProfile(){
       Intent intent = new Intent(UnAuthenticatedProfileActivity.this,
               AuthenticatedProfileActivity.class);
       finish();
       startActivity(intent);
   }

   /* private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.signInButton) {
            signIn();
        } else if (i == R.id.logInBtn) {
            signOut();
        }
    }*/

}
