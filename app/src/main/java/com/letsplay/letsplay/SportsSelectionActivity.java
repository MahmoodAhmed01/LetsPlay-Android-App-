package com.letsplay.letsplay;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.letsplay.letsplay.models.Game;
import com.zookey.universalpreferences.UniversalPreferences;

import java.util.ArrayList;
import java.util.List;

import static com.letsplay.letsplay.models.Game.BADMINTON;
import static com.letsplay.letsplay.models.Game.SNOOKER;
import static com.letsplay.letsplay.models.Game.TABLE_TENNIS;

public class SportsSelectionActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    CheckBox badminton;
    CheckBox tableTennis;
    CheckBox snooker;

    Button submitSport;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sports_selection);

        badminton = (CheckBox) findViewById(R.id.cb_badminton);
        tableTennis = (CheckBox) findViewById(R.id.cb_table_tennis);
        snooker = (CheckBox) findViewById(R.id.cb_snooker);

        mAuth = FirebaseAuth.getInstance();

        submitSport = (Button) findViewById(R.id.btnSportSubmit);

        submitSport.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        storingUserGames();

    }

    private void storingUserGames() {
        List<Game> selectedGames = new ArrayList<>();
        if (badminton.isChecked()) {
            selectedGames.add(BADMINTON);
        }

        if (tableTennis.isChecked()) {
            selectedGames.add(TABLE_TENNIS);
        }

        if (snooker.isChecked()) {
            selectedGames.add(SNOOKER);
        }
        if (selectedGames.size() == 0) {
            Toast.makeText(this, "Select at least one GAME", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseUser user = mAuth.getCurrentUser();


        try {
            DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("users");
            dbRef.child(user.getUid()).child("games").setValue(selectedGames);
            loginSuccessful();
            startMainActivity();
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private void loginSuccessful(){
        UniversalPreferences.getInstance().put(LoginActivity.KEY_USER_LOGIN, true);
    }

    private void startMainActivity(){
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        this.finish();
    }
}
