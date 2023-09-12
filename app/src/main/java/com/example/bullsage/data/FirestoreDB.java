package com.example.bullsage.data;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.example.bullsage.adapter.LeaderBoardAdapter;
import com.example.bullsage.databinding.ActivityLeaderBoardBinding;
import com.example.bullsage.models.LeaderBoard;
import com.example.bullsage.utilities.Constants;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDB {

    public static void saveScore(Intent intent, int score, String userId){

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        String category = intent.getStringExtra("category");
        String collection = mapCategory(category);
        String difficulty = intent.getStringExtra("difficulty");

        Map<String, Object> data = new HashMap<>();
        data.put(Constants.KEY_CORRECT_ANSWERS, score);
        data.put(Constants.KEY_DIFFICULTY, difficulty);
        data.put(Constants.KEY_TOTAL_QUIZZES,10);
        data.put(Constants.KEY_USER_ID, userId);


        db.collection(collection)
                .whereEqualTo(Constants.KEY_USER_ID, userId)
                .whereEqualTo(Constants.KEY_DIFFICULTY, difficulty)
                .get()
                .addOnCompleteListener(task -> {
                    if(task.isSuccessful() && task.getResult() != null
                            && task.getResult().getDocuments().size() > 0){
                        DocumentReference docRef  = db.collection(collection).document(task.getResult().getDocuments().get(0).getId());
                        docRef.update(Constants.KEY_CORRECT_ANSWERS, FieldValue.increment(score));
                        docRef.update(Constants.KEY_TOTAL_QUIZZES, FieldValue.increment(10));
                    }else {
                        db.collection(collection)
                                .add(data)
                                .addOnSuccessListener(documentReference -> {
                                    Log.d("Bull", "DocumentSnapshot written with ID: " + documentReference.getId());

                                })
                                .addOnFailureListener(exception ->{
                                    Log.d("Bull", "DocumentSnapshot failed to be written. Error : " + exception );
                                });
                    }
                });

    }

    public static void inflateLeaderBoard(String difficulty, String category, ActivityLeaderBoardBinding binding){

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String collection = mapCategory(category);
        ArrayList<LeaderBoard> leaderBoardData = new ArrayList<>();

        db.collection(collection)
                .whereEqualTo(Constants.KEY_DIFFICULTY, difficulty)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            LeaderBoard data = new LeaderBoard();
                            String userId = document.getString("userId");
                            int correctAnswers = Math.toIntExact(document.getLong("correctAnswers"));
                            int totalQuizzes = Math.toIntExact(document.getLong("totalQuizzes"));

                            data.setName(userId);
                            data.setCorrectAnswers(correctAnswers);
                            data.setTotalQuizzes(totalQuizzes);
                            leaderBoardData.add(data);
                        }

                        for (LeaderBoard data : leaderBoardData){
                            Log.d("Bull", "********************   " +  data.getName() + "    " + data.getCorrectAnswers() + "   " + data.getTotalQuizzes() + "!!! ");
                        }
                        processLeaderBoardData(leaderBoardData, binding);
                    }
                });
    }

    private static void processLeaderBoardData(ArrayList<LeaderBoard> leaderBoardData, ActivityLeaderBoardBinding binding){

        FirebaseFirestore db = FirebaseFirestore.getInstance();


        db.collection(Constants.KEY_COLLECTION_USERS)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        boolean moreThanThree = false;

                        for (DocumentSnapshot document : queryDocumentSnapshots.getDocuments()){
                            for (LeaderBoard data : leaderBoardData){
                                if (data.getName().equals(document.getId())){
                                    data.setName(document.getString("name"));
                                    data.setImage(document.getString("image"));
                                }
                            }

                        }
                        if (leaderBoardData.size() > 3){
                            moreThanThree = true;
                        }

                        calculateWeightedScore(leaderBoardData);
                        formatWeightedScore(leaderBoardData);
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        firstThreeSetUp(binding, leaderBoardData);

                        if (moreThanThree){
                            LeaderBoardAdapter adapter = new LeaderBoardAdapter(leaderBoardData);
                            binding.leaderBoardRecyclerView.setAdapter(adapter);
                        }

                    }
                });

    }


    private static void formatWeightedScore(ArrayList<LeaderBoard> leaderBoardData) {

        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        String formattedResult;

        for (LeaderBoard data : leaderBoardData){
            formattedResult = decimalFormat.format(data.getWeightedScore());
            data.setWeightedScore(Float.parseFloat(formattedResult));
        }
    }


    private static void firstThreeSetUp(ActivityLeaderBoardBinding binding, ArrayList<LeaderBoard> leaderBoardData) {
        if (leaderBoardData.size() >= 3){

            binding.imageFirst.setImageBitmap(getUserImage(leaderBoardData.get(0).getImage()));
            binding.firstName.setText(leaderBoardData.get(0).getName() + "\n" + leaderBoardData.get(0).getWeightedScore());
            binding.imageViewGoldMedal.setVisibility(View.VISIBLE);
            binding.imageFirst.setVisibility(View.VISIBLE);
            binding.firstName.setVisibility(View.VISIBLE);

            binding.imageSecond.setImageBitmap(getUserImage(leaderBoardData.get(1).getImage()));
            binding.secondName.setText(leaderBoardData.get(1).getName() + "\n" + leaderBoardData.get(1).getWeightedScore());
            binding.imageViewSilverMedal.setVisibility(View.VISIBLE);
            binding.imageSecond.setVisibility(View.VISIBLE);
            binding.secondName.setVisibility(View.VISIBLE);

            binding.imageThird.setImageBitmap(getUserImage(leaderBoardData.get(2).getImage()));
            binding.thirdName.setText(leaderBoardData.get(2).getName() + "\n" + leaderBoardData.get(2).getWeightedScore());
            binding.imageViewBronzeMedal.setVisibility(View.VISIBLE);
            binding.imageThird.setVisibility(View.VISIBLE);
            binding.thirdName.setVisibility(View.VISIBLE);

            leaderBoardData.remove(0);
            leaderBoardData.remove(0);
            leaderBoardData.remove(0);

        }else if (leaderBoardData.size() == 1){

            binding.imageFirst.setImageBitmap(getUserImage(leaderBoardData.get(0).getImage()));
            binding.firstName.setText(leaderBoardData.get(0).getName() + "\n" + leaderBoardData.get(0).getWeightedScore());
            binding.imageViewGoldMedal.setVisibility(View.VISIBLE);
            binding.imageFirst.setVisibility(View.VISIBLE);
            binding.firstName.setVisibility(View.VISIBLE);

        }else if (leaderBoardData.size() == 2){

            binding.imageFirst.setImageBitmap(getUserImage(leaderBoardData.get(0).getImage()));
            binding.firstName.setText(leaderBoardData.get(0).getName() + "\n" + leaderBoardData.get(0).getWeightedScore());
            binding.imageViewGoldMedal.setVisibility(View.VISIBLE);
            binding.imageFirst.setVisibility(View.VISIBLE);
            binding.firstName.setVisibility(View.VISIBLE);

            binding.imageSecond.setImageBitmap(getUserImage(leaderBoardData.get(1).getImage()));
            binding.secondName.setText(leaderBoardData.get(1).getName() + "\n" + leaderBoardData.get(1).getWeightedScore());
            binding.imageViewSilverMedal.setVisibility(View.VISIBLE);
            binding.imageSecond.setVisibility(View.VISIBLE);
            binding.secondName.setVisibility(View.VISIBLE);

        }



    }


    private static void calculateWeightedScore(ArrayList<LeaderBoard> leaderBoardData){

        int maxTotalQuestions = leaderBoardData.get(0).getTotalQuizzes();

        for (LeaderBoard data : leaderBoardData){
            if (data.getTotalQuizzes() > maxTotalQuestions){
                maxTotalQuestions = data.getTotalQuizzes();
            }
        }

        for (LeaderBoard data : leaderBoardData){

            int correctAnswers = data.getCorrectAnswers();
            int totalQuestions = data.getTotalQuizzes();
            float score  = ((float) correctAnswers / (totalQuestions + maxTotalQuestions)) * 100;
            data.setWeightedScore(score);
        }

        if (leaderBoardData.size() > 1){
            Collections.sort(leaderBoardData, (obj1, obj2) -> Float.compare(obj2.getWeightedScore(), obj1.getWeightedScore()));
        }


        for (LeaderBoard data : leaderBoardData){
            Log.d("Bull", "****00000*****   " +  data.getName() + "    " + data.getCorrectAnswers()
                    + "   " + data.getTotalQuizzes() + "  " + data.getWeightedScore()+"!!! ");
        }



    }


    private static String mapCategory(String category) {

        switch (category) {
            case "General Knowledge":
                return "Leaderboard_General_Knowledge";
            case "History":
                return "Leaderboard_History";
            case "Sports":
                return "Leaderboard_Sports";
            case "Video Games":
                return "Leaderboard_Video_Games";
            case "Geography":
                return "Leaderboard_Geography";
        }
        return "Error";
    }

    public static Bitmap getUserImage(String encodedImage){
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }


}
