package com.letsplay.letsplay;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.letsplay.letsplay.models.Match;
import com.letsplay.letsplay.models.User;
import com.zookey.universalpreferences.UniversalPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Mahmood on 6/28/2017.
 */

public class MatchFragment extends Fragment {

    MatchAdapter matchAdapter;

    @BindView(R.id.area_filter_spinner)
    Spinner spAreaFilterSpinner;

    @BindView(R.id.match_recycler_view)
    RecyclerView matchRecycleView;


    ArrayAdapter<String> areaAdapter;
    String[] areaList = {"All", "Sports Complex Multan", "B.Z.U Multan", "Multan Public School", "Railway Club"};


    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.match_fragment, container, false);
        unbinder = ButterKnife.bind(this, v);


        areaAdapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, areaList);
        areaAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
        spAreaFilterSpinner.setAdapter(areaAdapter);

        //matchRecycleView.setHasFixedSize(true);
        matchRecycleView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        //setMatchViewModel();

        final String gameId = UniversalPreferences.getInstance().get("selectedGame", -1).toString();

        spAreaFilterSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    fetchMatches(gameId, null);
                } else {
                    fetchMatches(gameId, String.valueOf(position));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                fetchMatches(gameId, null);
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


    public void fetchMatches(String gameId, String areaId) {

        String url = App.url + "matches";

        Ion.with(this)
                .load("GET", url)
                .addQuery("gameId", gameId)
                .addQuery("areaId", areaId)
                .as(new TypeToken<List<Match>>() {
                })
                .setCallback(new FutureCallback<List<Match>>() {
                    @Override
                    public void onCompleted(Exception e, List<Match> matches) {
                        if (e != null) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            matchAdapter = new MatchAdapter(getActivity(), matches);
                            matchRecycleView.setAdapter(matchAdapter);
                        }
                    }
                });

    }


    interface LoadUserListener {
        void onUsers(List<User> users);
    }

}

