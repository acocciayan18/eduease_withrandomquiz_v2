package com.example.eduease;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.eduease.R;

public class RandomQuizResult extends AppCompatActivity {

    private TextView resultMessage;
    private TextView scoreMessage;
    private Button finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayan_activity_random_quiz_result);

        resultMessage = findViewById(R.id.result_message);
        scoreMessage = findViewById(R.id.score_message);
        finishButton = findViewById(R.id.finish_button);

        int score = getIntent().getIntExtra("score", 0);  // Retrieve the score passed from the quiz activity

        scoreMessage.setText("Your score: " + score + "/15");

        finishButton.setOnClickListener(v -> finish());  // Close the result screen
    }
}

