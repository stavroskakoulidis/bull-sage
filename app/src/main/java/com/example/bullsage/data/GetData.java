package com.example.bullsage.data;

import android.content.Intent;
import android.util.Base64;
import android.util.Log;
import android.view.View;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.bullsage.databinding.ActivityQuizBinding;
import com.example.bullsage.models.Quiz;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;

public class GetData {

    public static JsonObjectRequest jsonParse(Intent intent, ActivityQuizBinding binding, ArrayList<Quiz> quizzes){


        String category = intent.getStringExtra("category");
        category = mapCategory(category);
        String difficulty = intent.getStringExtra("difficulty").toLowerCase();

        String url = "https://opentdb.com/api.php?amount=10&category=" + category +
                "&difficulty=" + difficulty +"&type=multiple&encode=base64";
        Log.d("Bull","URL ===> "+ url);


        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONArray jsonArray = response.getJSONArray("results");
                            Log.d("Bull", jsonArray.toString());
                            binding.progressBar.setVisibility(View.GONE);
                            binding.nextQuestion.setVisibility(View.VISIBLE);
                            binding.numberOfQuestion.setVisibility(View.VISIBLE);

                            for (int i = 0; i< jsonArray.length(); i++){
                                Quiz quiz = new Quiz();
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                String question = jsonObject.getString("question");
                                String correctAnswer = jsonObject.getString("correct_answer");
                                question = decodeString(question);
                                correctAnswer = decodeString(correctAnswer);
                                JSONArray wrongAnswers = jsonObject.getJSONArray("incorrect_answers");
                                quiz.setQuestion(question);
                                quiz.setCorrectAnswer(correctAnswer);
                                quiz.setWrongAnswers(wrongAnswers);
                                quizzes.add(quiz);
                            }

                            for (int i =0 ; i<quizzes.size(); i++){
                                Log.d("Bull", "Quiz is ===> " + quizzes.get(i).getQuestion());
                            }

                            ArrayList<String> answers;
                            String numberOfQuestion;
                            String tempAnswer;

                            answers = shuffleAnswers(quizzes.get(0).getCorrectAnswer(),
                                    decodeString(quizzes.get(0).getWrongAnswers().get(0).toString()),
                                    decodeString(quizzes.get(0).getWrongAnswers().get(1).toString()),
                                    decodeString(quizzes.get(0).getWrongAnswers().get(2).toString()));

                            numberOfQuestion = "1/10";
                            binding.numberOfQuestion.setText(numberOfQuestion);
                            binding.question.setText(quizzes.get(0).getQuestion());
                            tempAnswer = "A. " + answers.get(0);
                            binding.answer1.setText(tempAnswer);
                            tempAnswer = "B. " + answers.get(1);
                            binding.answer2.setText(tempAnswer);
                            tempAnswer = "C. " + answers.get(2);
                            binding.answer3.setText(tempAnswer);
                            tempAnswer = "D. " + answers.get(3);
                            binding.answer4.setText(tempAnswer);
                            binding.question.setVisibility(View.VISIBLE);
                            binding.answer1.setVisibility(View.VISIBLE);
                            binding.answer2.setVisibility(View.VISIBLE);
                            binding.answer3.setVisibility(View.VISIBLE);
                            binding.answer4.setVisibility(View.VISIBLE);


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        return request;

    }

    private static String mapCategory(String category) {
        switch (category) {
            case "Video Games":
                return "15";
            case "General Knowledge":
                return "9";
            case "History":
                return "23";
            case "Sports":
                return "21";
            case "Geography":
                return "22";
        }
        return "-1";
    }

    public static String decodeString(String encodedString){
        byte[] decodedBytes = Base64.decode(encodedString, Base64.DEFAULT);
        return new String(decodedBytes, StandardCharsets.UTF_8);
    }

    public static ArrayList<String> shuffleAnswers(String answer1, String answer2, String answer3, String answer4){

        ArrayList<String> answers = new ArrayList<>();

        answers.add(answer1);
        answers.add(answer2);
        answers.add(answer3);
        answers.add(answer4);

        Collections.shuffle(answers);

        return answers;
    }


}
