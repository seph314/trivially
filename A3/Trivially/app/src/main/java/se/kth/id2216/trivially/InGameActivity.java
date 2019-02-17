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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.concurrent.ThreadLocalRandom;

public class InGameActivity extends Activity {

    int score = 0;
    String[] correctAnswers;
    private String[][] incorrectAnswers;
    private String[][] answers;
    int numberOfQuestions = 3;
    int currentQuestionNumber = 0;
    private String[] questions;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        try {
            setVariables();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        setupActivity();
    }



    private void setVariables() throws JSONException {
        JSONObject triviaResponse = new JSONObject(
        "{\n" +
                "    \"response_code\": 0,\n" +
                "    \"results\": [\n" +
                "        {\n" +
                "            \"category\": \"History\",\n" +
                "            \"type\": \"multiple\",\n" +
                "            \"difficulty\": \"medium\",\n" +
                "            \"question\": \"When was the United States National Security Agency established?\",\n" +
                "            \"correct_answer\": \"November 4, 1952\",\n" +
                "            \"incorrect_answers\": [\n" +
                "                \"July 26, 1908\",\n" +
                "                \" July 1, 1973\",\n" +
                "                \" November 25, 2002\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"category\": \"Entertainment: Video Games\",\n" +
                "            \"type\": \"multiple\",\n" +
                "            \"difficulty\": \"medium\",\n" +
                "            \"question\": \"In Terraria, which of these items is NOT crafted at a Mythril Anvil?\",\n" +
                "            \"correct_answer\": \"Ankh Charm\",\n" +
                "            \"incorrect_answers\": [\n" +
                "                \"Venom Staff\",\n" +
                "                \"Sky Fracture\",\n" +
                "                \"Orichalcum Tools\"\n" +
                "            ]\n" +
                "        },\n" +
                "        {\n" +
                "            \"category\": \"General Knowledge\",\n" +
                "            \"type\": \"multiple\",\n" +
                "            \"difficulty\": \"medium\",\n" +
                "            \"question\": \"Where does water from Poland Spring water bottles come from?\",\n" +
                "            \"correct_answer\": \"Maine, United States\",\n" +
                "            \"incorrect_answers\": [\n" +
                "                \"Hesse, Germany\",\n" +
                "                \"Masovia, Poland\",\n" +
                "                \"Bavaria, Poland\"\n" +
                "            ]\n" +
                "        }\n" +
                "    ]\n" +
                "}");

        incorrectAnswers = new String[numberOfQuestions][3];
        answers = new String[numberOfQuestions][4];
        correctAnswers = new String[numberOfQuestions];
        questions = new String[numberOfQuestions];
        JSONArray incorrectAnswersJson;
        JSONArray jArray = triviaResponse.getJSONArray("results");
        for(int i = 0; i < jArray.length(); i++){
            questions[i] = jArray.getJSONObject(i).getString("question");
            correctAnswers[i] = jArray.getJSONObject(i).getString("correct_answer");
            incorrectAnswersJson = new JSONArray(jArray.getJSONObject(i).getString("incorrect_answers"));
            for(int j = 0; j < incorrectAnswersJson.length(); j++)
                incorrectAnswers[i][j] = incorrectAnswersJson.getString(j);
        }

        int r;
        int k = 0;
        for(int i = 0; i < numberOfQuestions; i++){
            r = ThreadLocalRandom.current().nextInt(0, 3 + 1);
            k = 0;
            for(int j = 0; j < 4; j++){
                if(r == j){
                    answers[i][j] = correctAnswers[i];
                    k = 1;
                }
                if(answers[i][j] == null)
                    answers[i][j] = incorrectAnswers[i][j - k];
            }
        }

    }

    private void setupActivity(){
        final  TextView questionHeading = findViewById(R.id.questionHeading);
        final  TextView questionText = findViewById(R.id.questionText);
        final Button quitButton = findViewById(R.id.quitBtn);
        final Button guessButton1 = findViewById(R.id.guessBtn1);
        final Button guessButton2 = findViewById(R.id.guessBtn2);
        final Button guessButton3 = findViewById(R.id.guessBtn3);
        final Button guessButton4 = findViewById(R.id.guessBtn4);

        questionHeading.setText(String.format("Question %d", currentQuestionNumber +1));
        questionText.setText(questions[currentQuestionNumber]);
        quitButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                finish();
            }
        });

        guessButton1.setText(answers[currentQuestionNumber][0]);
        guessButton1.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                onAnswerChoice(0);
            }
        });
        guessButton2.setText(answers[currentQuestionNumber][1]);
        guessButton2.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                onAnswerChoice(1);
            }
        });
        guessButton3.setText(answers[currentQuestionNumber][2]);
        guessButton3.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                onAnswerChoice(2);
            }
        });
        guessButton4.setText(answers[currentQuestionNumber][3]);
        guessButton4.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                onAnswerChoice(3);
            }
        });
    }

    private void onAnswerChoice(int answerNumber){
        if(correctAnswers[currentQuestionNumber].equals(answers[currentQuestionNumber][answerNumber]))
            score++;
        if(currentQuestionNumber < numberOfQuestions -1)
            currentQuestionNumber++;
        else if(currentQuestionNumber == numberOfQuestions - 1){
            SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
            SharedPreferences.Editor editor = sharedPrefs.edit();
            editor.putInt("score", score);
            editor.putInt("questions", numberOfQuestions);
            editor.commit();
            Intent intent = new Intent(InGameActivity.this,
                    GameDoneActivity.class);
            finish();
            startActivity(intent);
        }
        setupActivity();
    }
}
