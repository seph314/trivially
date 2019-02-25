package se.kth.id2216.trivially;


import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mAuth = FirebaseAuth.getInstance();

        new getCategoriesFromOpenDB().execute(); // get categories from openTriviaDB by a worker thread (AsyncTask)
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        final FirebaseUser currentUser = mAuth.getCurrentUser();
        updateUI(currentUser);
        DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
        final DatabaseReference mPlayersRef = mRootRef.child("players");

        mPlayersRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (currentUser != null && !dataSnapshot.hasChild(currentUser.getUid())) {
                    mPlayersRef.push().setValue(currentUser.getUid());
                    mPlayersRef.child(currentUser.getUid()).child("alias").setValue("anonymous_user");
                    mPlayersRef.child(currentUser.getUid()).child("score").setValue(0L);
                    mPlayersRef.child(currentUser.getUid()).child("gamesPlayed").setValue(0L);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void updateUI(FirebaseUser currentUser) {
        System.out.println(currentUser + "is logged in");
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

        final Drawable defaultColor = findViewById(R.id.easyBtn).getBackground();
        final TextView easyBtn = findViewById(R.id.easyBtn);
        final TextView normalBtn = findViewById(R.id.normalBtn);
        final TextView hardBtn = findViewById(R.id.hardBtn);
        easyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("difficulty", "easy");
                editor.commit();
                easyBtn.setBackgroundResource(R.drawable.back_chosen);
                normalBtn.setBackground(defaultColor);
                hardBtn.setBackground(defaultColor);
            }
        });
        normalBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("difficulty", "medium");
                editor.commit();
                easyBtn.setBackground(defaultColor);
                normalBtn.setBackgroundResource(R.drawable.back_chosen);
                hardBtn.setBackground(defaultColor);
            }
        });
        hardBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putString("difficulty", "hard");
                editor.commit();
                easyBtn.setBackground(defaultColor);
                normalBtn.setBackground(defaultColor);
                hardBtn.setBackgroundResource(R.drawable.back_chosen);
            }
        });

        ArrayAdapter adapter = new ArrayAdapter<>(this,
                R.layout.activity_listview, categoriesList);

        ListView listView = findViewById(R.id.categoriesList);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                view.setSelected(true);
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putInt("category", categoriesHM.get(categoriesList.get(position)));
                editor.commit();
            }
        });
    }

    /**
     * User a worker thread to GET categories from Open Trivia DB
     */
    private class getCategoriesFromOpenDB extends AsyncTask<Void, Void, Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            String source = "https://opentdb.com/api_category.php";
            URLConnection urlConnection;
            BufferedReader bufferedReader = null;
            try {
                URL url = new URL(source);
                urlConnection = url.openConnection();
                bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));

                StringBuilder stringBuilder = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null){
                    stringBuilder.append(line);
                }
                categoriesOpenTrivia = new JSONObject(stringBuilder.toString());

                JSONArray jArray = categoriesOpenTrivia.getJSONArray("trivia_categories");
                for(int i = 0; i < jArray.length(); i++){
                    categoriesHM.put(jArray.getJSONObject(i).getString("name"), Integer.valueOf(jArray.getJSONObject(i).getString("id")));
                    categoriesList.add(jArray.getJSONObject(i).getString("name"));
                }

            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            finally {
                if (bufferedReader != null){
                    try{
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

}
