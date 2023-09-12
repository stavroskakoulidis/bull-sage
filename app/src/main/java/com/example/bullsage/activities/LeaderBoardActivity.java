package com.example.bullsage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.bullsage.data.FirestoreDB;
import com.example.bullsage.R;
import com.example.bullsage.databinding.ActivityLeaderBoardBinding;

public class LeaderBoardActivity extends AppCompatActivity {

    private ActivityLeaderBoardBinding binding;
    private static final String[] difficultyItems = {"Easy", "Medium", "Hard"};
    private static final String[] categoryItems = {"General Knowledge", "History", "Geography", "Video Games", "Sports"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLeaderBoardBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayAdapter<String> difficultyAdapter = new ArrayAdapter<String>(this, R.layout.list_item, difficultyItems);
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<String>(this, R.layout.list_item, categoryItems);

        binding.selectCategory.setAdapter(categoryAdapter);
        binding.selectDifficulty.setAdapter(difficultyAdapter);

        binding.leaderBoardBtn.setOnClickListener(view -> {
            String difficulty = binding.selectDifficulty.getText().toString();
            String category = binding.selectCategory.getText().toString();

            hideViews();

            if (!difficulty.isEmpty() && !category.isEmpty()){
                binding.progressBar.setVisibility(View.VISIBLE);
                FirestoreDB.inflateLeaderBoard(difficulty, category, binding);
            }else{
                Toast.makeText(getApplicationContext(), "Please Select Difficulty and Category!", Toast.LENGTH_SHORT).show();
            }

        });


    }

    private void hideViews() {

        binding.imageViewGoldMedal.setVisibility(View.INVISIBLE);
        binding.imageFirst.setVisibility(View.INVISIBLE);
        binding.firstName.setVisibility(View.INVISIBLE);

        binding.imageViewSilverMedal.setVisibility(View.INVISIBLE);
        binding.imageSecond.setVisibility(View.INVISIBLE);
        binding.secondName.setVisibility(View.INVISIBLE);

        binding.imageViewBronzeMedal.setVisibility(View.INVISIBLE);
        binding.imageThird.setVisibility(View.INVISIBLE);
        binding.thirdName.setVisibility(View.INVISIBLE);

        binding.leaderBoardRecyclerView.setAdapter(null);

    }


}