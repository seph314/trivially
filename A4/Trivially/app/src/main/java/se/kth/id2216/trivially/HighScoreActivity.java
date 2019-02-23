package se.kth.id2216.trivially;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HighScoreActivity extends Activity {

    SharedPreferences sharedPrefs;
    String[][] scores;
    String[][] personalScore;
    private DatabaseReference mRootRef;
    private DatabaseReference mPlayersRef;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPlayersRef = mRootRef.child("players");

        setupActivity();
    }

    private void setupActivity(){
        TextView gameButton = findViewById(R.id.gameNav);
        TextView profileButton = findViewById(R.id.profileNav);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighScoreActivity.this,
                        GameActivity.class);
                startActivity(intent);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighScoreActivity.this,
                        AuthenticatedProfileActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();

        final FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentFirebaseUser != null)
            mPlayersRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    scores = new String[(int) dataSnapshot.getChildrenCount()][3];
                    int i = 0;
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        if(snapshot.child("visible").getValue(Boolean.class)) {
                            scores[i][0] = snapshot.child("alias").getValue(String.class);
                            scores[i][1] = String.valueOf(snapshot.child("gamesPlayed").getValue(Long.class));
                            if (Long.parseLong(scores[i][1]) != 0)
                                scores[i][2] = divideLong(snapshot.child("score").getValue(Long.class), Long.parseLong(scores[i][1]));
                            else
                                scores[i][2] = "0";
                        }
                        else{
                            scores[i][0] = "private user";
                            scores[i][2] = "-1";
                        }
                        i++;
                    }
                    personalScore = new String[1][3];
                    personalScore[0][0] = dataSnapshot.child(currentFirebaseUser.getUid()).child("alias").getValue(String.class);
                    personalScore[0][1] = String.valueOf(dataSnapshot.child(currentFirebaseUser.getUid()).child("gamesPlayed").getValue(Long.class));
                    if(Long.parseLong(personalScore[0][1]) != 0)
                        personalScore[0][2] = divideLong(dataSnapshot.child(currentFirebaseUser.getUid()).child("score").getValue(Long.class), Long.parseLong(personalScore[0][1]));
                    else
                        personalScore[0][2] = "0";

                    updateListView(removePrivateUsers(sortArray(scores)), removePrivateUsers(sortArray(personalScore)));
                }
                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });
    }

    private String[][] sortArray(String[][] scores){
        String[] temp;
        for (int i = 0; i < scores.length - 1; i++) {
            for (int j = i + 1; j < scores.length; j++) {
                if (Double.parseDouble(scores[i][2]) < Double.parseDouble(scores[j][2])) {
                    temp = scores[j];
                    scores[j] = scores[i];
                    scores[i] = temp;
                }
                else if (Double.parseDouble(scores[i][2]) == Double.parseDouble(scores[j][2]) && Long.parseLong(scores[i][1]) < Long.parseLong(scores[j][1])) {
                    temp = scores[j];
                    scores[j] = scores[i];
                    scores[i] = temp;
                }
            }
        }
        return scores;
    }

    private String[][] removePrivateUsers(String[][] scores){
        int privateUsers = 0;
        for(int i = 0; i < scores.length; i++){
            if(scores[i][2].equals("-1"))
                privateUsers++;
        }

        String[][] visibleScores = new String[scores.length - privateUsers][3];
        for(int i = 0; i < scores.length; i++){
            if(scores[i][2].equals("-1"))
                continue;
            else
                visibleScores[i] = scores[i];
        }
        return visibleScores;
    }

    private void updateListView(String[][] scores, String[][] personalScore){
        ListView personalListView = findViewById(R.id.personalScore);
        ListViewAdapter adapter1 = new ListViewAdapter(this, personalScore);
        personalListView.setAdapter(adapter1);

        adapter1.notifyDataSetChanged();

        ListView lview = findViewById(R.id.listview);
        ListViewAdapter adapter2 = new ListViewAdapter(this, scores);
        lview.setAdapter(adapter2);

        adapter2.notifyDataSetChanged();
    }

    private String divideLong(Long numerator, Long denominator){

        double result = ((double) numerator / ((double) denominator * 10.00)) * 100;
        DecimalFormat numberFormat = new DecimalFormat("#.00");
        return numberFormat.format(result);
    }
}
