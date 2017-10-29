package com.letsplay.letsplay;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.letsplay.letsplay.models.Match;
import com.letsplay.letsplay.models.MatchState;
import com.squareup.picasso.Picasso;
import com.zookey.universalpreferences.UniversalPreferences;

public class AcceptMatchActivity extends AppCompatActivity {

    Context context;

    TextView tvGame;
    TextView tvTimeAndDate;
    TextView tvChallengePhrase;
    TextView tvCreatedAt;
    TextView tvAcceptAt;
    TextView tvMatchState;

    TextView tvMatchStatus;

    TextView tvPlayer1;
    TextView tvPlayer2;


    ImageView ivPlayer1;
    ImageView ivPlayer2;


    TextView tvPlayer1Score;
    TextView tvPlayer2Score;

    Button btnAcceptMatch;
    Button btnPlayedtMatch;

    Match match;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accept_match);

        tvGame = (TextView) findViewById(R.id.tv_game_name);
        tvTimeAndDate = (TextView) findViewById(R.id.tv_match_time);
        tvPlayer1 = (TextView) findViewById(R.id.tv_player1_name);

        ivPlayer1 = (ImageView) findViewById(R.id.img_player1_picture);


        tvChallengePhrase = (TextView) findViewById(R.id.tv_challenge_phrase);

        tvPlayer2 = (TextView) findViewById(R.id.tv_player2_name);
        ivPlayer2 = (ImageView) findViewById(R.id.img_player2_picture);
        tvPlayer1Score = (TextView) findViewById(R.id.tv_player1_score);
        tvPlayer2Score = (TextView) findViewById(R.id.tv_player2_score);

        tvCreatedAt = (TextView) findViewById(R.id.tv_match_createdAt);
        tvAcceptAt = (TextView) findViewById(R.id.tv_accept_match_time);
        tvMatchState = (TextView) findViewById(R.id.tv_match_state);

        tvMatchStatus = (TextView) findViewById(R.id.tv_match_status);

        btnAcceptMatch = (Button) findViewById(R.id.btn_accept_match);
        btnPlayedtMatch = (Button) findViewById(R.id.btn_played);


        btnAcceptMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMatchAcceptRequest();
            }
        });

        btnPlayedtMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AcceptMatchActivity.this, MatchWinActivity.class);
                intent.putExtra("player1", match.getPlayer1().getName());
                intent.putExtra("player2", match.getPlayer2().getName());
                intent.putExtra("player1Id", match.getPlayer1Id());
                intent.putExtra("player2Id", match.getPlayer2Id());
                intent.putExtra("matchId", match.getId());


                startActivity(intent);
            }
        });
        //getMatchDetailRequest();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getMatchDetailRequest();
    }

    private void updateUi(Match m) {
        this.match = m;
        tvPlayer1.setText(m.getPlayer1().getName());
        tvGame.setText("Game : " + m.getGame().getPlainName());
        tvTimeAndDate.setText("Match Time : " + m.getDateOfMatch().toString());
        Picasso.with(context).load(m.getPlayer1().getPictureUrl()).into(ivPlayer1);
        tvChallengePhrase.setText("Challenge Phrase : " + m.getChallengePhrase());

        Integer userId = UniversalPreferences.getInstance().get("userId", -1);

        btnPlayedtMatch.setVisibility(View.GONE);

        if (userId.equals(m.getPlayer1Id())) {
            if (m.getState() == MatchState.ACCEPTED) {
                btnPlayedtMatch.setVisibility(View.VISIBLE);
            }
        }

        if (userId.equals(m.getPlayer1Id())) {
            btnAcceptMatch.setVisibility(View.GONE);
        }

        if ((m.getState() == MatchState.PLAYED) || (m.getState() == MatchState.PLAYING)) {
            btnAcceptMatch.setVisibility(View.GONE);
        }

        if ((m.getState() == MatchState.ACCEPTED) || m.getState() == MatchState.PLAYED) {
            tvPlayer2.setText(m.getPlayer2().getName());
            Picasso.with(context).load(m.getPlayer2().getPictureUrl()).into(ivPlayer2);
            btnAcceptMatch.setVisibility(View.GONE);
        }

        if (m.getState() == MatchState.PLAYED) {
            tvPlayer1Score.setVisibility(View.VISIBLE);
            tvPlayer2Score.setVisibility(View.VISIBLE);
            tvPlayer1Score.setText(m.getPlayer1().getName() + " Score : " + m.getPlayer1Score().toString());
            tvPlayer2Score.setText(m.getPlayer2().getName() + " Score : " + m.getPlayer2Score().toString());
        } else {
            tvPlayer1Score.setVisibility(View.GONE);
            tvPlayer2Score.setVisibility(View.GONE);
        }

        if (m.getWinnerId() != null) {
            if (m.getWinnerId().equals(m.getPlayer1Id())) {
                tvMatchStatus.setVisibility(View.VISIBLE);
                tvMatchStatus.setText("Status : " + m.getPlayer1().getName() + " Won the Match..!");
            } else if (m.getWinnerId().equals(m.getPlayer2Id())) {
                tvMatchStatus.setVisibility(View.VISIBLE);
                tvMatchStatus.setText("Status : " + m.getPlayer2().getName() + " Won the Match..!");
            } else {
                tvMatchStatus.setVisibility(View.GONE);
            }

        }

        if (m.getAcceptedAt() != null){
            tvAcceptAt.setVisibility(View.VISIBLE);
            tvAcceptAt.setText("Accepted At : " + m.getAcceptedAt().toString());
        } else {
            tvAcceptAt.setVisibility(View.GONE);
        }
        tvCreatedAt.setText("Created At : " + m.getCreatedAt().toString());
        tvMatchState.setText("Match State : " + m.getState().toString());


    }


    private void getMatchDetailRequest() {
        Bundle bundle = getIntent().getExtras();
        String matchId = String.valueOf(bundle.getInt("matchId"));

        Log.d("matchDaId", matchId);


        String url = App.url + "matches/" + matchId;


        Ion.with(this)
                .load("GET", url)
                .as(Match.class)
                .setCallback(new FutureCallback<Match>() {
                    @Override
                    public void onCompleted(Exception e, Match result) {
                        if (e != null) {
                            Toast.makeText(AcceptMatchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            updateUi(result);
                        }
                    }
                });
    }

    private void sendMatchAcceptRequest() {
        Bundle bundle = getIntent().getExtras();
        String matchId = String.valueOf(bundle.getInt("matchId"));
        Integer userId = UniversalPreferences.getInstance().get("userId", -1);
        String url = "http://192.168.10.14:5000/matches/" + matchId + "/accepted";

        Ion.with(AcceptMatchActivity.this)
                .load("POST", url)
                .setBodyParameter("player", userId.toString())
                .asString()
                .setCallback(new FutureCallback<String>() {
                    @Override
                    public void onCompleted(Exception e, String result) {
                        if (e != null) {
                            Toast.makeText(AcceptMatchActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            getMatchDetailRequest();
                        }
                    }
                });
    }
}
