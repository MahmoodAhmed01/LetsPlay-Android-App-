package com.letsplay.letsplay;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.reflect.TypeToken;
import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.letsplay.letsplay.models.Match;
import com.zookey.universalpreferences.UniversalPreferences;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Mahmood on 6/28/2017.
 */

public class HistoryFragment extends Fragment {

    MatchAdapter matchAdapter;

    @BindView(R.id.history_recycler_view)
    RecyclerView historyRecycleView;


    Unbinder unbinder;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.history_fragment, container, false);

        unbinder = ButterKnife.bind(this, v);

        historyRecycleView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        getAllMatches();

        return v;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void getAllMatches() {

        final String gameId = UniversalPreferences.getInstance().get("selectedGame", -1).toString();
        Integer userId = UniversalPreferences.getInstance().get("userId", -1);

        String url = App.url + "history";

        Ion.with(this)
                .load("GET", url)
                .addQuery("gameId", gameId)
                .addQuery("playerId", String.valueOf(userId))
                .as(new TypeToken<List<Match>>() {
                })
                .setCallback(new FutureCallback<List<Match>>() {
                    @Override
                    public void onCompleted(Exception e, List<Match> matches) {
                        if (e != null) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            matchAdapter = new MatchAdapter(getActivity(), matches);
                            historyRecycleView.setAdapter(matchAdapter);
                        }
                    }
                });
    }
}
