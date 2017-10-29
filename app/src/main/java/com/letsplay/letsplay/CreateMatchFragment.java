package com.letsplay.letsplay;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.letsplay.letsplay.models.Match;
import com.letsplay.letsplay.models.MatchState;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;
import com.zookey.universalpreferences.UniversalPreferences;

import java.util.Calendar;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Mahmood on 6/28/2017.
 */

public class CreateMatchFragment extends Fragment implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    OnMatchCreatedListener mCallback;

    private static final String TAG = "CreateMatchFragment";

    boolean isTimeSet = false;
    boolean isDateSet = false;

    public interface OnMatchCreatedListener {
        public void onMatchCreated();
    }

    @BindView(R.id.tv_game_selection)
    TextView tvSelectGame;

    @BindView(R.id.area_selection_spinner)
    Spinner spAreaSpinner;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.tv_time)
    TextView tvTime;

    @BindView(R.id.et_challenge_phrase)
    EditText etChallengePhrase;

    @BindView(R.id.btn_create_match)
    Button btnCreateMatch;

    Unbinder unbinder;


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


        tvSelectGame.setText("Create Match of Your Selected Game");


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

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        MainActivity mainActivity = (MainActivity) activity;
        mCallback = mainActivity;
    }

    private void createMatch() {

        if (!isDateSet || !isTimeSet) {
            Toast.makeText(getActivity(), "Set Time and Date of Match", Toast.LENGTH_SHORT).show();
            return;
        }

        Log.d(TAG, matchDate.getTimeInMillis() + "match actual time");


        long then = Calendar.getInstance().getTimeInMillis() + (long) (1 * 60 * 60 * 1000);
        Log.d(TAG, then + "1 hour add to time");
        if (matchDate.getTimeInMillis() < then) {
            Toast.makeText(getActivity(), "Its too late to create Match", Toast.LENGTH_SHORT).show();
            return;
        }

        final Integer gameId = UniversalPreferences.getInstance().get("selectedGame", -1);

        Match match = new Match();
        match.setGameId(gameId);
        match.setAreaId(spAreaSpinner.getSelectedItemPosition()+1);
        match.setChallengePhrase(etChallengePhrase.getText().toString());
        Integer userId = UniversalPreferences.getInstance().get("userId", -1);
        match.setPlayer1Id(userId);
        match.setState(MatchState.CREATED);
        match.setDateOfMatch(matchDate.getTime());
        match.setCreatedAt(new Date());

        String url = App.url + "matches";

        Log.d("spinvalue", match.getGameId().toString());

        Ion.with(this)
                .load("POST", url)
                .setJsonPojoBody(match)
                .as(Match.class)
                .setCallback(new FutureCallback<Match>() {
                    @Override
                    public void onCompleted(Exception e, Match result) {
                        if (e != null){
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            mCallback.onMatchCreated();
                        }
                    }
                });

    }

    @Override
    public void onDateSet(DatePickerDialog view, int year, int monthOfYear, int dayOfMonth) {
        matchDate.set(Calendar.YEAR, year);
        matchDate.set(Calendar.MONTH, monthOfYear);
        matchDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);

        tvDate.setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
        isDateSet = true;
    }

    @Override
    public void onTimeSet(TimePickerDialog view, int hourOfDay, int minute, int second) {
        matchDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
        matchDate.set(Calendar.MINUTE, minute);
        matchDate.set(Calendar.SECOND, second);
        isTimeSet = true;

        if (hourOfDay <= 12) {
            tvTime.setText(hourOfDay + ":" + minute + " AM");
        } else {
            tvTime.setText((hourOfDay - 12) + ":" + minute + " PM");
        }
    }
}
