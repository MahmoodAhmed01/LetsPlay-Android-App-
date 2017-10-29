package com.letsplay.letsplay;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class ProfileActivity extends AppCompatActivity {

    TextView player1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        Bundle bundle = getIntent().getExtras();
        Integer player = bundle.getInt("playerId");


        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        ProfileFragment fragment = ProfileFragment.newInstance(player);
        fragmentTransaction.replace(R.id.profileLayout, fragment);
        fragmentTransaction.commit();

    }
}
