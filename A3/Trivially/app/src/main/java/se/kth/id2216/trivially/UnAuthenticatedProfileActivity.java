package se.kth.id2216.trivially;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

public class UnAuthenticatedProfileActivity extends Activity {

    SharedPreferences sharedPrefs;

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

    }

    private void setupActivity(){
        TextView gameButton = (TextView) findViewById(R.id.gameNav);
        TextView profileButton = (TextView) findViewById(R.id.highScoreNav);
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

        final Button loginButton = (Button) findViewById(R.id.logInBtn);
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
}
