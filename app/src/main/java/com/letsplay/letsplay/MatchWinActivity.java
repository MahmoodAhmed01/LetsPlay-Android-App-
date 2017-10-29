package com.letsplay.letsplay;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;

public class MatchWinActivity extends AppCompatActivity {

    String player1Name;
    String player2Name;

    Integer player1Id;
    Integer player2Id;
    Integer matchId;

    EditText etPlayer1Score;
    EditText etPlayer2Score;

    Button btnSubmit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_win);

        Bundle bundle = getIntent().getExtras();
        player1Name = bundle.getString("player1");
        player2Name = bundle.getString("player2");
        player1Id = bundle.getInt("player1Id");
        player2Id = bundle.getInt("player2Id");
        matchId = bundle.getInt("matchId");

        etPlayer1Score = (EditText) findViewById(R.id.et_player1_score);
        etPlayer2Score = (EditText) findViewById(R.id.et_player2_score);

        btnSubmit = (Button) findViewById(R.id.btn_submit_score);


        etPlayer1Score.setHint("Enter Score of " + player1Name);
        etPlayer2Score.setHint("Enter Score of " + player2Name);

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMatchResult();
                //Toast.makeText(MatchWinActivity.this, "Result is submitted..!", Toast.LENGTH_LONG).show();
                //MatchWinActivity.this.finish();
            }
        });

    }

    private void setMatchResult(){
        String p1Score = etPlayer1Score.getText().toString();
        String p2Score = etPlayer2Score.getText().toString();

        String winner;

        int score1 = Integer.parseInt( etPlayer1Score.getText().toString() );
        int score2 = Integer.parseInt( etPlayer2Score.getText().toString() );

        if (score1 < score2){
            winner = String.valueOf(player2Id);
        } else if(score2 < score1){
            winner = String.valueOf(player1Id);
        } else {
            Toast.makeText(MatchWinActivity.this, "Score cant be equal", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = App.url + "matches/" + matchId + "/won";

        Ion.with(MatchWinActivity.this)
                .load("POST", url)
                .setBodyParameter("player", winner)
                .setBodyParameter("player1Score", p1Score)
                .setBodyParameter("player2Score", p2Score)
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(MatchWinActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MatchWinActivity.this, "Result is submitted..!", Toast.LENGTH_LONG).show();
                            MatchWinActivity.this.finish();
                        }
                    }
                });
    }
}
