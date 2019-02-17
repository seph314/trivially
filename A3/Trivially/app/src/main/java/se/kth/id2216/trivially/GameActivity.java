package se.kth.id2216.trivially;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.HttpAuthHandler;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class GameActivity extends AppCompatActivity {

    private HashMap<String, Integer> categoriesHM = new HashMap<>();
    private List<String> categoriesList = new ArrayList<>();
    SharedPreferences sharedPrefs;
    JSONObject categoriesOpenTrivia = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);

        setupActivity();
    }


    private void setupActivity()
    {
        final Button button = findViewById(R.id.startGameBtn);
        button.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                Intent intent = new Intent(GameActivity.this,
                        InGameActivity.class);
                startActivity(intent);

            }
        });
        TextView highScoreButton = findViewById(R.id.highScoreNav);
        TextView profileButton = findViewById(R.id.profileNav);
        highScoreButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this,
                        HighScoreActivity.class);
                startActivity(intent);
            }
        });
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GameActivity.this,
                        AuthenticatedProfileActivity.class);
                startActivity(intent);
            }
        });

        TextView easyBtn = findViewById(R.id.easyBtn);
        TextView normalBtn = findViewById(R.id.normalBtn);
        TextView hardBtn = findViewById(R.id.hardBtn);
        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("difficulty", "easy");
                editor.commit();
            }
        });
        normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("difficulty", "medium");
                editor.commit();
            }
        });
        hardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("difficulty", "hard");
                editor.commit();
            }
        });

        getCategories();

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_listview, categoriesList);

        ListView listView = findViewById(R.id.categoriesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt("category", categoriesHM.get(categoriesList.get(position)));
                editor.commit();
            }
        });
    }


    private void getCategories() {
        JSONObject triviaResponse = null;
        try {
            triviaResponse = new JSONObject(
                    "{\n" +
                            "    \"trivia_categories\": [\n" +
                            "        {\n" +
                            "            \"id\": 9,\n" +
                            "            \"name\": \"General Knowledge\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 10,\n" +
                            "            \"name\": \"Entertainment: Books\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 11,\n" +
                            "            \"name\": \"Entertainment: Film\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 12,\n" +
                            "            \"name\": \"Entertainment: Music\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 13,\n" +
                            "            \"name\": \"Entertainment: Musicals & Theatres\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 14,\n" +
                            "            \"name\": \"Entertainment: Television\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 15,\n" +
                            "            \"name\": \"Entertainment: Video Games\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 16,\n" +
                            "            \"name\": \"Entertainment: Board Games\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 17,\n" +
                            "            \"name\": \"Science & Nature\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 18,\n" +
                            "            \"name\": \"Science: Computers\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 19,\n" +
                            "            \"name\": \"Science: Mathematics\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 20,\n" +
                            "            \"name\": \"Mythology\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 21,\n" +
                            "            \"name\": \"Sports\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 22,\n" +
                            "            \"name\": \"Geography\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 23,\n" +
                            "            \"name\": \"History\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 24,\n" +
                            "            \"name\": \"Politics\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 25,\n" +
                            "            \"name\": \"Art\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 26,\n" +
                            "            \"name\": \"Celebrities\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 27,\n" +
                            "            \"name\": \"Animals\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 28,\n" +
                            "            \"name\": \"Vehicles\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 29,\n" +
                            "            \"name\": \"Entertainment: Comics\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 30,\n" +
                            "            \"name\": \"Science: Gadgets\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 31,\n" +
                            "            \"name\": \"Entertainment: Japanese Anime & Manga\"\n" +
                            "        },\n" +
                            "        {\n" +
                            "            \"id\": 32,\n" +
                            "            \"name\": \"Entertainment: Cartoon & Animations\"\n" +
                            "        }\n" +
                            "    ]\n" +
                            "}");

            JSONArray jArray = triviaResponse.getJSONArray("trivia_categories");
            for(int i = 0; i < jArray.length(); i++){
                categoriesHM.put(jArray.getJSONObject(i).getString("name"), Integer.valueOf(jArray.getJSONObject(i).getString("id")));
                categoriesList.add(jArray.getJSONObject(i).getString("name"));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


}
