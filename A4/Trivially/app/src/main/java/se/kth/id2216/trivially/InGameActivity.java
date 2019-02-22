package se.kth.id2216.trivially;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class InGameActivity extends Activity {

    int score = 0;
    String[] correctAnswers;
    private String[][] incorrectAnswers;
    private String[][] answers;
    int numberOfQuestions = 10;
    int currentQuestionNumber = 0;
    private String[] questions;
    SharedPreferences sharedPrefs;
    int categoryID;
    String difficulty;
    JSONObject questionsFromOpenTrivia = null;
    List<Button> answerButtons = new ArrayList<>();


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_in_game);
        setSharedPrefs();
        new getQuestionsFromOpenDB().execute();
    }


    private void setSharedPrefs(){
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        categoryID = sharedPrefs.getInt("category", 0);
        difficulty = sharedPrefs.getString("difficulty", "");
    }


    private class getQuestionsFromOpenDB extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            String baseURL = "https://opentdb.com/api.php?amount=10";
            String amountURL = "&category=" + categoryID;
            String difficultyURL = "&difficulty=" + difficulty;
            String typeURL = "&type=multiple";
            String source = baseURL + amountURL + difficultyURL + typeURL;

            URLConnection urlConnection;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(source);
                urlConnection = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line);
                }

                questionsFromOpenTrivia = new JSONObject(stringBuilder.toString());
                System.out.println("Questions " + questionsFromOpenTrivia.toString());


                incorrectAnswers = new String[numberOfQuestions][3];
                answers = new String[numberOfQuestions][4];
                correctAnswers = new String[numberOfQuestions];
                questions = new String[numberOfQuestions];
                JSONArray incorrectAnswersJson;
                JSONArray jArray = null;

                jArray = questionsFromOpenTrivia.getJSONArray("results");
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


            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (bufferedReader != null) {
                    try {
                        bufferedReader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            setupActivity();
        }
    }


    private void setupActivity(){
         TextView questionHeading = findViewById(R.id.questionHeading);
         TextView questionText = findViewById(R.id.questionText);
        Button quitButton = findViewById(R.id.quitBtn);
        Button guessButton1 = findViewById(R.id.guessBtn1);
        Button guessButton2 = findViewById(R.id.guessBtn2);
        Button guessButton3 = findViewById(R.id.guessBtn3);
        Button guessButton4 = findViewById(R.id.guessBtn4);

        answerButtons.add(guessButton1);
        answerButtons.add(guessButton2);
        answerButtons.add(guessButton3);
        answerButtons.add(guessButton4);

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
        if(correctAnswers[currentQuestionNumber].equals(answers[currentQuestionNumber][answerNumber])) {
            score++;
//            Drawable defaultColor = answerButtons.get(answerNumber-1).getBackground();
//            answerButtons.get(answerNumber-1).setBackgroundColor(Color.parseColor("#FF6347"));
//            setupActivity();
//            try {
//                Thread.sleep(500);
//
//            }catch (Exception e){
//                e.printStackTrace();
//            }
//            answerButtons.get(answerNumber-1).setBackground(defaultColor);
        }
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
