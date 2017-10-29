package com.letsplay.letsplay;

import android.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.async.future.FutureCallback;
import com.koushikdutta.ion.Ion;
import com.letsplay.letsplay.models.Profile;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Mahmood on 6/28/2017.
 */

public class ProfileFragment extends Fragment {


    @BindView(R.id.iv_profile_picture)
    ImageView ivProfilePicture;

    @BindView(R.id.tv_player)
    TextView tvPlayerName;

    @BindView(R.id.tv_player_game)
    TextView tvPlayerGame;

    @BindView(R.id.tv_total_played)
    TextView tvTotalPlayed;

    @BindView(R.id.tv_total_win)
    TextView tvTotalWin;

    @BindView(R.id.tv_percentage)
    TextView tvPercentage;

    Unbinder unbinder;

    Integer userId;

    public static ProfileFragment newInstance(Integer userId){
        ProfileFragment fragment = new ProfileFragment();
        fragment.userId = userId;
        return fragment;
    }





    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.profile_fragment,container,false);

        unbinder = ButterKnife.bind(this, v);

        getProfile();



        return v;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (unbinder != null) {
            unbinder.unbind();
        }
    }

    private void getProfile(){


        String url = App.url + "profile/" + userId;
        Ion.with(this)
                .load("GET", url)
                .as(Profile.class)
                .setCallback(new FutureCallback<Profile>() {
                    @Override
                    public void onCompleted(Exception e, Profile profile) {
                        if (e != null) {
                            Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            updateUI(profile);
                        }
                    }
                });



    }

    private void updateUI(Profile profile){

        Picasso.with(getActivity()).load(profile.getPictureUrl()).transform(new CircleTransform()).into(ivProfilePicture);
        tvPlayerName.setText(profile.getName());
        tvPlayerGame.setText(profile.getGame().getPlainName() + " Player");
        tvTotalPlayed.setText("Total Played : " + profile.getTotalPlayed());
        tvTotalWin.setText("Total Win : " + profile.getTotalWin());
        tvPercentage.setText("Win Percentage : " + profile.getWinPercentage());

    }
}
