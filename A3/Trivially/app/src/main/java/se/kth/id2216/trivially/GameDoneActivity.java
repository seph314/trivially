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

public class GameDoneActivity extends Activity {

    private int score;
    private int numberOfQuestions;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_done);
        setupActivity();
    }

    private void setupActivity(){
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        score = sharedPrefs.getInt("score",-1);
        numberOfQuestions = sharedPrefs.getInt("questions",-1);
        String scoreCommentString = "Meh, ur OK.";
        if(score == numberOfQuestions)
            scoreCommentString = "Wow, ur really smart!";
        else if(score > (numberOfQuestions/3) * 2)
            scoreCommentString = "Ah, ur kind of smart?";
        else if(score < numberOfQuestions/3)
            scoreCommentString = "Oh, i bet u were just unlucky.";

        final TextView scoreComment = (TextView) findViewById(R.id.scoreComment);
        scoreComment.setText(scoreCommentString);

        final TextView scoreText = (TextView) findViewById(R.id.score);
        scoreText.setText(String.format("Score %d / %d", score, numberOfQuestions ));

        final Button playAgain = (Button) findViewById(R.id.playAgainBtn);
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

        final Button quitButton = (Button) findViewById(R.id.homeBtn);
        quitButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });
    }
}
