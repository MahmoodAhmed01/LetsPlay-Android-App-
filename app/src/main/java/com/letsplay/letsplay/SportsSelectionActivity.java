package com.letsplay.letsplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.zookey.universalpreferences.UniversalPreferences;


public class SportsSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    Integer selectedGame = 0;

    RadioGroup gameGroup;

    RadioButton badminton;
    RadioButton tableTennis;
    RadioButton snooker;

    Button submitSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_selection);

        gameGroup = (RadioGroup) findViewById(R.id.rg_games);

        badminton = (RadioButton) findViewById(R.id.rb_badminton);
        tableTennis = (RadioButton) findViewById(R.id.rb_table_tennis);
        snooker = (RadioButton) findViewById(R.id.rb_snooker);

        submitSport = (Button) findViewById(R.id.btnSportSubmit);

        submitSport.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        storingUserGame();

    }

    private void storingUserGame() {
        if (badminton.isChecked()) {
            selectedGame = 1;

        }

        if (tableTennis.isChecked()) {
            selectedGame = 2;


        }

        if (snooker.isChecked()) {
            selectedGame = 3;

        }
        if (selectedGame == 0) {
            Toast.makeText(this, "Select at least one GAME", Toast.LENGTH_SHORT).show();
            return;
        }

        Integer userId = UniversalPreferences.getInstance().get("userId", -1);


        String url = App.url + "game/" + userId;

        Ion.with(this)
                .load("POST", url)
                .setBodyParameter("gameId", selectedGame.toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(SportsSelectionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            UniversalPreferences.getInstance().put("selectedGame", selectedGame);
                            UniversalPreferences.getInstance().put(LoginActivity.KEY_USER_LOGIN, "SUCCESSFUL_LOGIN");
                            startMainActivity();
                        }
                    }
                });
    }

    private void startMainActivity() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
