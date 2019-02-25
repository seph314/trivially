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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameDoneActivity extends Activity {

    private int score;
    private int numberOfQuestions;
    Long firebaseScore = 0L;
    Long firebaseGamesPlayed = 0L;
    DatabaseReference mRootRef;
    DatabaseReference mPlayersRef;
    FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_done);
        setupActivity();
    }

    private void updateFirebase(){
        System.out.println(currentFirebaseUser.getUid());
        mPlayersRef.child(currentFirebaseUser.getUid()).child("score").setValue(firebaseScore + score);
        mPlayersRef.child(currentFirebaseUser.getUid()).child("gamesPlayed").setValue(firebaseGamesPlayed + 1);
    }

    private void setupActivity(){
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mPlayersRef = mRootRef.child("players");
        currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        score = sharedPrefs.getInt("score",-1);
        numberOfQuestions = sharedPrefs.getInt("questions",-1);

        String scoreCommentString = createComment();

        final TextView scoreComment = findViewById(R.id.scoreComment);
        scoreComment.setText(scoreCommentString);

        final TextView scoreText = findViewById(R.id.score);
        scoreText.setText(String.format("Score %d / %d", score, numberOfQuestions ));

        final Button playAgain = findViewById(R.id.playAgainBtn);
        playAgain.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(GameDoneActivity.this,
                        InGameActivity.class);
                finish();
                startActivity(intent);
            }
        });

        final Button quitButton = findViewById(R.id.homeBtn);
        quitButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });
    }

    private String createComment(){
        String scoreCommentString = "Meh, ur OK.";
        if(score == numberOfQuestions)
            scoreCommentString = "Wow, ur really smart!";
        else if(score > (numberOfQuestions/3) * 2)
            scoreCommentString = "Ah, ur kind of smart?";
        else if(score < numberOfQuestions/3)
            scoreCommentString = "Oh, i bet u were just unlucky.";

        return scoreCommentString;
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(currentFirebaseUser != null)
            mPlayersRef.child(currentFirebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    firebaseScore = dataSnapshot.child("score").getValue(Long.class);
                    firebaseGamesPlayed = dataSnapshot.child("gamesPlayed").getValue(Long.class);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if(currentFirebaseUser != null)
            updateFirebase();
    }
}
