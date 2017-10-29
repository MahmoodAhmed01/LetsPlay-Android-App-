package com.letsplay.letsplay;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.letsplay.letsplay.models.Match;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Mahmood on 7/10/2017.
 */

public class MatchAdapter extends RecyclerView.Adapter<MatchAdapter.MatchViewHolder> {

    final Context context;
    final List<Match> matches;

    MatchAdapter(Context context, List<Match> matches) {
        this.context = context;
        this.matches = matches;
    }


    @Override
    public MatchViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_match_item, parent, false);
        return new MatchViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MatchViewHolder holder, int position) {
        Match m = matches.get(position);

        holder.itemView.setTag(m);
        holder.imgPlayer1Picture.setTag(m);
        holder.imgPlayer2Picture.setTag(m);

        holder.tvPlayer1Name.setText(m.getPlayer1().getName());
        Picasso.with(context).load(m.getPlayer1().getPictureUrl()).into(holder.imgPlayer1Picture);

        if (m.getPlayer2Id() != null) {
            holder.tvPlayer2Name.setText(m.getPlayer2().getName());
            Picasso.with(context).load(m.getPlayer2().getPictureUrl()).into(holder.imgPlayer2Picture);
        }

        holder.imgWinner1.setVisibility(View.INVISIBLE);
        holder.imgWinner2.setVisibility(View.INVISIBLE);


        Log.d("samsung", m.getWinnerId() + "");

        if (m.getWinnerId() != null) {
            if (m.getWinnerId().equals(m.getPlayer1Id())) {
                holder.imgWinner1.setVisibility(View.VISIBLE);
                holder.imgWinner1.setImageResource(R.drawable.ic_cup);

            } else if (m.getWinnerId().equals(m.getPlayer2Id())) {
                holder.imgWinner2.setVisibility(View.VISIBLE);
                holder.imgWinner2.setImageResource(R.drawable.ic_cup);

            }
        }
        if (m.getAcceptedAt() != null) {
            holder.tvAcceptedTime.setVisibility(View.VISIBLE);
            holder.tvAcceptedTime.setText("Accepted At : " + m.getAcceptedAt().toString());
        } else {
            holder.tvAcceptedTime.setVisibility(View.GONE);
        }
        holder.tvTimeOfMatch.setText("Match Time : " + m.getDateOfMatch().toString());
        holder.tvGame.setText("Game : " + m.getGame().getPlainName());
        holder.tvState.setText("Match Status : " + m.getState().toString());
        holder.tvCreatedAt.setText("Created At : " + m.getCreatedAt());
    }


    @Override
    public int getItemCount() {
        return matches.size();
    }

    public class MatchViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView imgPlayer1Picture;
        ImageView imgPlayer2Picture;
        ImageView imgWinner1;
        ImageView imgWinner2;
        TextView tvPlayer1Name;
        TextView tvPlayer2Name;
        TextView tvTimeOfMatch;
        TextView tvAcceptedTime;
        TextView tvGame;
        TextView tvState;
        TextView tvCreatedAt;


        public MatchViewHolder(View itemView) {
            super(itemView);

            imgPlayer1Picture = (ImageView) itemView.findViewById(R.id.img_player1_picture);
            imgPlayer2Picture = (ImageView) itemView.findViewById(R.id.img_player2_picture);

            imgWinner1 = (ImageView) itemView.findViewById(R.id.iv_winner1);
            imgWinner2 = (ImageView) itemView.findViewById(R.id.iv_winner2);

            tvPlayer1Name = (TextView) itemView.findViewById(R.id.tv_player1_name);
            tvPlayer2Name = (TextView) itemView.findViewById(R.id.tv_player2_name);
            tvTimeOfMatch = (TextView) itemView.findViewById(R.id.tv_create_matchTime);

            tvAcceptedTime = (TextView) itemView.findViewById(R.id.tv_accept_match_time);
            tvGame = (TextView) itemView.findViewById(R.id.tv_game);
            tvState = (TextView) itemView.findViewById(R.id.tv_match_state);
            tvCreatedAt = (TextView) itemView.findViewById(R.id.tv_match_createdAt);


            itemView.setOnClickListener(this);
            imgPlayer1Picture.setOnClickListener(this);
            imgPlayer2Picture.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            if (v == itemView) {
                Match m = (Match) itemView.getTag();
                Context context = v.getContext();
                Intent intent = new Intent(context, AcceptMatchActivity.class);
                intent.putExtra("matchId", m.getId());
                context.startActivity(intent);

            } else if (v == imgPlayer1Picture) {
                Match m = (Match) imgPlayer1Picture.getTag();

                Intent intent = new Intent(context, ProfileActivity.class);
                intent.putExtra("playerId", m.getPlayer1Id());
                context.startActivity(intent);

            } else if (v == imgPlayer2Picture) {
                Match m = (Match) imgPlayer2Picture.getTag();

                if (m.getPlayer2() != null) {
                    Intent intent = new Intent(context, ProfileActivity.class);
                    intent.putExtra("playerId", m.getPlayer2Id());
                    context.startActivity(intent);
                }
            }
        }
    }
}
