package com.example.bullsage.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.bullsage.databinding.ListItemLeaderboardBinding;
import com.example.bullsage.models.LeaderBoard;

import java.util.ArrayList;

public class LeaderBoardAdapter extends RecyclerView.Adapter<LeaderBoardAdapter.LeaderBoardViewHolder>{

    private final ArrayList<LeaderBoard> leaderBoardData;

    public LeaderBoardAdapter(ArrayList<LeaderBoard> leaderBoardData) {
        this.leaderBoardData = leaderBoardData;
    }

    @NonNull
    @Override
    public LeaderBoardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ListItemLeaderboardBinding listItemLeaderboardBinding =ListItemLeaderboardBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false
        );

        return new LeaderBoardViewHolder(listItemLeaderboardBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderBoardViewHolder holder, int position) {
        holder.setLeaderBoardData(leaderBoardData.get(position), position);

    }

    @Override
    public int getItemCount() {
        return leaderBoardData.size();
    }



    class LeaderBoardViewHolder extends RecyclerView.ViewHolder{

        ListItemLeaderboardBinding binding;

        public LeaderBoardViewHolder(ListItemLeaderboardBinding listItemLeaderboardBinding) {
            super(listItemLeaderboardBinding.getRoot());
            binding =listItemLeaderboardBinding;
        }

        public void setLeaderBoardData (LeaderBoard data, int position){

            binding.imageProfile.setImageBitmap(getUserImage(data.getImage()));
            binding.score.setText(Float.toString(data.getWeightedScore()));
            binding.userName.setText(data.getName());
            Log.d("Bull", "POSITION IS ===> " + position);
            String positionInLeaderBoard = String.valueOf(position + 4);
            binding.leaderBoardPosition.setText(positionInLeaderBoard);

        }

    }

    private Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
