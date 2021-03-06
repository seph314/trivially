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

public class AuthenticatedProfileActivity extends Activity {

    SharedPreferences sharedPrefs;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticated_profile);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        if(sharedPrefs.getBoolean("authenticated",false)){
            setupActivity();
        }
        else
            redirectToUnAuthenticatedProfile();
    }

    private void setupActivity(){
        TextView gameButton = findViewById(R.id.gameNav);
        TextView profileButton = findViewById(R.id.highScoreNav);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthenticatedProfileActivity.this,
                        GameActivity.class);
                startActivity(intent);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AuthenticatedProfileActivity.this,
                        HighScoreActivity.class);
                startActivity(intent);
            }
        });

        final Button loginButton = findViewById(R.id.logOutBtn);
        loginButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("token", "");
                editor.putBoolean("authenticated", false);
                editor.commit();
                redirectToUnAuthenticatedProfile();
            }
        });

        Switch toggle = findViewById(R.id.highScoreSwitch);
        toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("showScore", true);
                    editor.commit();
                } else {
                    SharedPreferences.Editor editor = sharedPrefs.edit();
                    editor.putBoolean("showScore", false);
                    editor.commit();
                }
            }
        });

    }

    private void redirectToUnAuthenticatedProfile(){
        Intent intent = new Intent(AuthenticatedProfileActivity.this,
                UnAuthenticatedProfileActivity.class);
        finish();
        startActivity(intent);
    }
}
