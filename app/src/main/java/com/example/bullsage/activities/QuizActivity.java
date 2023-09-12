package com.example.bullsage.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;

import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.bullsage.data.FirestoreDB;
import com.example.bullsage.data.GetData;
import com.example.bullsage.models.Quiz;
import com.example.bullsage.R;
import com.example.bullsage.databinding.ActivityQuizBinding;
import com.example.bullsage.databinding.SummaryLayoutBinding;
import com.example.bullsage.utilities.Constants;
import com.example.bullsage.utilities.PreferenceManager;

import org.json.JSONException;


import java.util.ArrayList;

public class QuizActivity extends AppCompatActivity {

    private ActivityQuizBinding binding;
    private PreferenceManager preferenceManager;
    private SummaryLayoutBinding summaryLayoutBinding;
    private final ArrayList<Quiz> quizzes = new ArrayList<>();
    private int currentIndex = 0;
    private int score = 0;
    private boolean answerSelected = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQuizBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());

        Intent intent = getIntent();

        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        JsonObjectRequest jsonRequest = GetData.jsonParse(intent, binding, quizzes);
        queue.add(jsonRequest);

        binding.answer1.setOnClickListener(listener());
        binding.answer2.setOnClickListener(listener());
        binding.answer3.setOnClickListener(listener());
        binding.answer4.setOnClickListener(listener());

    }

    public void onNextButtonClick(View view) {
        String numberOfQuestion;
        String tempText;
        ArrayList<String> answers = new ArrayList<>();
        if (answerSelected){
            currentIndex++;
            if (currentIndex < quizzes.size()){
                answerSelected = false;
                binding.answer1.setBackgroundResource(R.drawable.background_answer);
                binding.answer2.setBackgroundResource(R.drawable.background_answer);
                binding.answer3.setBackgroundResource(R.drawable.background_answer);
                binding.answer4.setBackgroundResource(R.drawable.background_answer);
                binding.answer1.setOnClickListener(listener());
                binding.answer2.setOnClickListener(listener());
                binding.answer3.setOnClickListener(listener());
                binding.answer4.setOnClickListener(listener());
                numberOfQuestion = currentIndex + 1 + "/10";
                binding.numberOfQuestion.setText(numberOfQuestion);
                try {
                    answers = GetData.shuffleAnswers(quizzes.get(currentIndex).getCorrectAnswer(),
                            GetData.decodeString(quizzes.get(currentIndex).getWrongAnswers().get(0).toString()),
                            GetData.decodeString(quizzes.get(currentIndex).getWrongAnswers().get(1).toString()),
                            GetData.decodeString(quizzes.get(currentIndex).getWrongAnswers().get(2).toString()));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                binding.question.setText(quizzes.get(currentIndex).getQuestion());
                tempText = "A. " + answers.get(0);
                binding.answer1.setText(tempText);
                tempText = "B. " + answers.get(1);
                binding.answer2.setText(tempText);
                tempText = "C. " + answers.get(2);
                binding.answer3.setText(tempText);
                tempText = "D. " + answers.get(3);
                binding.answer4.setText(tempText);

                if (currentIndex == 9){
                    tempText = "Summary";
                    binding.nextQuestion.setText(tempText);
                }

            }else{
                FirestoreDB.saveScore(getIntent(), score, preferenceManager.getString(Constants.KEY_USER_ID));
                inflateSummary();
            }
        }else {
            Toast.makeText(getApplicationContext(), "Please Select an Answer!", Toast.LENGTH_SHORT).show();
        }

    }

    private void inflateSummary() {
        String txtPrompt = null;
        summaryLayoutBinding = SummaryLayoutBinding.inflate(getLayoutInflater());
        if (score == 0){
            txtPrompt = "The Red Eye!";
            summaryLayoutBinding.imagePrompt.setImageResource(R.drawable.the_red_eye);
        }else if (score > 0 && score <= 3){
            txtPrompt = "At Least You Tried!";
            summaryLayoutBinding.imagePrompt.setImageResource(R.drawable.sad_emoji);
        }else if (score > 3 && score <= 6){
            txtPrompt = "Nice!!!";
            summaryLayoutBinding.imagePrompt.setImageResource(R.drawable.nice_try);
        }else if (score > 6 && score <=9){
            txtPrompt = "Excellent!!!";
            summaryLayoutBinding.imagePrompt.setImageResource(R.drawable.wow);
        }else if (score == 10){
            txtPrompt = "Smurf 100";
            summaryLayoutBinding.imagePrompt.setImageResource(R.drawable.smurf);
        }

        String strScore = score + "/10";
        summaryLayoutBinding.summaryPrompt.setText(txtPrompt);
        summaryLayoutBinding.score.setText(strScore);
        summaryLayoutBinding.playAgain.setOnClickListener(view ->
                startActivity(new Intent(getApplicationContext(), MainActivity.class)));
        setContentView(summaryLayoutBinding.getRoot());
    }


    private View.OnClickListener listener(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView  textViewClicked = (TextView) view;
                String viewText = textViewClicked.getText().toString();
                String correctAnswer = quizzes.get(currentIndex).getCorrectAnswer();
                binding.answer1.setOnClickListener(null);
                binding.answer2.setOnClickListener(null);
                binding.answer3.setOnClickListener(null);
                binding.answer4.setOnClickListener(null);
                answerSelected = true;
                if (viewText.contains(correctAnswer)){
                    textViewClicked.setBackgroundResource(R.drawable.background_correct_answer);
                    score++;
                }else {
                    textViewClicked.setBackgroundResource(R.drawable.background_wrong_answer);
                    if (binding.answer1.getText().toString().contains(correctAnswer)){
                        binding.answer1.setBackgroundResource(R.drawable.background_correct_answer);
                    }else if (binding.answer2.getText().toString().contains(correctAnswer)){
                        binding.answer2.setBackgroundResource(R.drawable.background_correct_answer);
                    }else if (binding.answer3.getText().toString().contains(correctAnswer)){
                        binding.answer3.setBackgroundResource(R.drawable.background_correct_answer);
                    }else if (binding.answer4.getText().toString().contains(correctAnswer)){
                        binding.answer4.setBackgroundResource(R.drawable.background_correct_answer);
                    }
                }

            }
        };

        return listener;

    }




}