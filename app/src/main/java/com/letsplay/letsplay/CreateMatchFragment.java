package com.letsplay.letsplay;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.letsplay.letsplay.models.Game;
import com.letsplay.letsplay.models.Match;
import com.letsplay.letsplay.models.MatchState;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Mahmood on 6/28/2017.
 */

public class CreateMatchFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {


    @BindView(R.id.game_selection_spinner)
    Spinner spGamesSpinner;

    @BindView(R.id.area_selection_spinner)
    Spinner spAreaSpinner;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.et_chellenge_phrase)
    EditText etChellengePhrase;

    @BindView(R.id.btn_create_match)
    Button btnCreateMatch;

    Unbinder unbinder;

    ArrayAdapter<String> gamesAdapter;
    String[] gamesList = {"Badminton", "Table Tennis", "Snooker"};


    ArrayAdapter<String> areaAdapter;
    String[] areaList = {"Sports Complex Multan", "B.Z.U Multan", "Multan Public School", "Railway Station Club"};

    Calendar matchDate;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        matchDate = Calendar.getInstance();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.create_match_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);

        gamesAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, gamesList);
        gamesAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spGamesSpinner.setAdapter(gamesAdapter);

        areaAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAreaSpinner.setAdapter(areaAdapter);

        btnCreateMatch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createMatch();
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                DatePickerDialog dpd = DatePickerDialog.newInstance(CreateMatchFragment.this);
                dpd.setMinDate(now);
                dpd.show(getFragmentManager(), "Match Date");
            }
        });

        tvTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar now = Calendar.getInstance();
                TimePickerDialog dpd = TimePickerDialog.newInstance(CreateMatchFragment.this, false);
                dpd.show(getFragmentManager(), "Match Time");
            }
        });

        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void createMatch() {

        final FirebaseAuth mAuth = FirebaseAuth.getInstance();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference("matches");

        Match match = new Match();
        Integer pos = spGamesSpinner.getSelectedItemPosition();
        match.setGame(Game.values()[pos]);
        match.setArea(spAreaSpinner.getSelectedItem().toString());
        match.setChellengePhrase(etChellengePhrase.getText().toString());
        match.setPlayer1(mAuth.getCurrentUser().getUid());
        match.setMatchState(MatchState.CREATED);
        match.setDateOfMatch(matchDate.getTime());
        match.setCreatedAt(new Date());

        dbRef.push().setValue(match);

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        matchDate.set(Calendar.YEAR, year);
        matchDate.set(Calendar.MONTH, monthOfYear);
        matchDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        tvDate.setText(dayOfMonth + "/" + monthOfYear + "/" + year);
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        matchDate.set(Calendar.HOUR, hourOfDay);
        matchDate.set(Calendar.MINUTE, minute);
        matchDate.set(Calendar.SECOND, second);

        if (hourOfDay <= 12) {
            tvTime.setText(hourOfDay + ":" + minute + " AM");
        } else {
            tvTime.setText((hourOfDay - 12) + ":" + minute + " PM");
        }
    }
}
