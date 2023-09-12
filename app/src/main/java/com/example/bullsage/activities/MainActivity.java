package com.example.bullsage.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bullsage.data.FirestoreDB;
import com.example.bullsage.databinding.ActivityMainBinding;
import com.example.bullsage.utilities.Constants;
import com.example.bullsage.utilities.PreferenceManager;
import com.example.bullsage.R;
import com.makeramen.roundedimageview.RoundedImageView;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private PreferenceManager preferenceManager;
    private Dialog myDialog;

    private static final String[] items = {"Easy", "Medium", "Hard"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        preferenceManager = new PreferenceManager(getApplicationContext());
        myDialog = new Dialog(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item, items);
        binding.autoCompleteTxt.setAdapter(adapter);
        loadUserDetails();

        setListeners();

    }

    private void showPopup(){

        AppCompatImageView closeView;
        Button logOutBtn;
        TextView txtName;
        TextView txtEmail;
        RoundedImageView profileImage;

        myDialog.setContentView(R.layout.custom_popup);

        closeView = myDialog.findViewById(R.id.closeView);
        logOutBtn = myDialog.findViewById(R.id.logoutButton);
        txtEmail = myDialog.findViewById(R.id.emailTextView);
        txtName = myDialog.findViewById(R.id.nameTextView);
        profileImage = myDialog.findViewById(R.id.popupImage);

        txtName.setText(preferenceManager.getString(Constants.KEY_NAME));
        txtEmail.setText(preferenceManager.getString(Constants.KEY_EMAIL));
        profileImage.setImageBitmap(FirestoreDB.getUserImage(preferenceManager.getString(Constants.KEY_IMAGE)));

        closeView.setOnClickListener(view -> myDialog.dismiss());
        logOutBtn.setOnClickListener(view -> {
            preferenceManager.clear();
            startActivity(new Intent(getApplicationContext(),SignInActivity.class));
            finish();
        });

        myDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        myDialog.show();


    }


    private void setListeners(){
        View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Button buttonClicked = (Button)view;
                String btnText = (String) buttonClicked.getText();
                String difficulty = binding.autoCompleteTxt.getText().toString();

                Intent intent = new Intent(getApplicationContext(), QuizActivity.class);
                intent.putExtra("category", btnText);
                intent.putExtra("difficulty", difficulty);
                if (difficulty.isEmpty()){
                    Toast.makeText(getApplicationContext(),"Please Select Difficulty",Toast.LENGTH_SHORT).show();
                }else{
                    startActivity(intent);
                }


            }
        };

        binding.generalKnowledge.setOnClickListener(listener);
        binding.history.setOnClickListener(listener);
        binding.sports.setOnClickListener(listener);
        binding.videoGames.setOnClickListener(listener);
        binding.geography.setOnClickListener(listener);

        binding.imageLeaderBoard.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), LeaderBoardActivity.class);
            startActivity(intent);
        });

        binding.imageProfile.setOnClickListener(view -> showPopup());

    }

    private void loadUserDetails(){
        binding.textName.setText(preferenceManager.getString(Constants.KEY_NAME));
        byte[] bytes = Base64.decode(preferenceManager.getString(Constants.KEY_IMAGE),Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);
        binding.imageProfile.setImageBitmap(bitmap);

    }
}