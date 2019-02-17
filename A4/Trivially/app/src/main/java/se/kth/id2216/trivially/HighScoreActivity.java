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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class HighScoreActivity extends Activity {

    SharedPreferences sharedPrefs;
    String[][] scores;
    String[][] personalScore;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
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

        getHighScore();

        ListView personalListView = findViewById(R.id.personalScore);
        ListViewAdapter adapter1 = new ListViewAdapter(this, personalScore);
        personalListView.setAdapter(adapter1);

        adapter1.notifyDataSetChanged();

        ListView lview = findViewById(R.id.listview);
        ListViewAdapter adapter2 = new ListViewAdapter(this, scores);
        lview.setAdapter(adapter2);

        adapter2.notifyDataSetChanged();

    }

    private void getHighScore() {
        JSONObject triviaResponse = null;
        try {
            triviaResponse = new JSONObject(
                    "{\n" +
                            "    \"score\": [\n" +
                            "        {\n" +
                            "            \"name\": \"John Doe\",\n" +
                            "            \"gamesPlayed\": 7777,\n" +
                            "            \"successRate\": 95.00\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"name\": \"McAffee\",\n" +
                            "            \"gamesPlayed\": 314,\n" +
                            "            \"successRate\": 59.00\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"name\": \"Norton\",\n" +
                            "            \"gamesPlayed\": 200,\n" +
                            "            \"successRate\": 20.22\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"name\": \"Cranky Sloth\",\n" +
                            "            \"gamesPlayed\": 10000,\n" +
                            "            \"successRate\": 0.04\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}");

            JSONArray jArray = triviaResponse.getJSONArray("score");
            scores = new String[jArray.length()][3];
            for(int i = 0; i < jArray.length(); i++){
                scores[i][0] = jArray.getJSONObject(i).getString("name");
                scores[i][1] = String.valueOf(jArray.getJSONObject(i).getInt("gamesPlayed"));
                scores[i][2] = jArray.getJSONObject(i).getString("successRate") + "%";
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        personalScore = new String[1][3];
        personalScore[0] =
                new String[]{
                        "John Doe", "7777", "95.00%"
                };


    }
}
