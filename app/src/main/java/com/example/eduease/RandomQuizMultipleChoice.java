package com.example.eduease;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class RandomQuizMultipleChoice extends AppCompatActivity {

    private TextView questionText;
    private RadioGroup choicesGroup;
    private Button submitButton;

    private String correctAnswer = "";
    private List<QuizQuestion> questionList = new ArrayList<>();
    private int currentQuestionIndex = 0;

    private int score = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ayan_activity_random_quiz_multiple_choice);

        questionText = findViewById(R.id.question_text);
        choicesGroup = findViewById(R.id.choices_group);
        submitButton = findViewById(R.id.submit_button);

        // Load questions from Firebase
        loadQuestionsFromFirebase();

        submitButton.setOnClickListener(v -> {
            int selectedId = choicesGroup.getCheckedRadioButtonId();
            if (selectedId == -1) {
                Toast.makeText(this, "Please select an answer", Toast.LENGTH_SHORT).show();
                return;
            }

            RadioButton selectedRadio = findViewById(selectedId);
            String selectedAnswer = selectedRadio.getText().toString().substring(0, 1); // A, B, C, or D

            // Check answer
            if (selectedAnswer.equalsIgnoreCase(correctAnswer)) {
                Toast.makeText(this, "Correct!", Toast.LENGTH_SHORT).show();
                score++;
            } else {
                Toast.makeText(this, "Incorrect. Correct answer: " + correctAnswer, Toast.LENGTH_LONG).show();
            }

            // Move to the next question
            currentQuestionIndex++;
            if (currentQuestionIndex < questionList.size()) {
                displayQuestion(questionList.get(currentQuestionIndex));
            } else {
                showResult(); // If no more questions, show result
            }
        });
    }

    private void loadQuestionsFromFirebase() {
        FirebaseApp secondaryApp;
        try {
            secondaryApp = FirebaseApp.getInstance("Secondary");
        } catch (IllegalStateException e) {
            FirebaseOptions options = new FirebaseOptions.Builder()
                    .setApplicationId("1:882141634417:android:ac69b51d83d01def3460d0")
                    .setApiKey("AIzaSyBlECTZf28SbEc4xHsz7JnH99YtTw6T58I")
                    .setProjectId("edu-ease-ni-ayan")
                    .setDatabaseUrl("https://edu-ease-ni-ayan-default-rtdb.firebaseio.com/")
                    .build();

            secondaryApp = FirebaseApp.initializeApp(getApplicationContext(), options, "Secondary");
        }

        FirebaseDatabase db = FirebaseDatabase.getInstance(secondaryApp);

        String topicTitle = getIntent().getStringExtra("topicTitle").toLowerCase();

        String path = "random_quiz_multiple_" + topicTitle;
        DatabaseReference ref = db.getReference(path);



        ref.limitToFirst(15).get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (DataSnapshot snap : task.getResult().getChildren()) {
                    QuizQuestion q = snap.getValue(QuizQuestion.class);
                    questionList.add(q);
                }

                if (!questionList.isEmpty()) {
                    // Shuffle the question list to get random questions
                    Collections.shuffle(questionList);

                    displayQuestion(questionList.get(currentQuestionIndex));
                } else {
                    questionText.setText("No questions available.");
                }
            } else {
                questionText.setText("Failed to fetch from Firebase.");
            }
        });
    }

    private void displayQuestion(QuizQuestion q) {
        questionText.setText(q.getQuestion());
        correctAnswer = q.getAnswer();

        choicesGroup.removeAllViews();
        Map<String, String> choices = q.getChoices();
        for (Map.Entry<String, String> entry : choices.entrySet()) {
            RadioButton rb = new RadioButton(this);
            rb.setText(entry.getKey() + ". " + entry.getValue());
            choicesGroup.addView(rb);
        }
    }

    private void showResult() {
        Intent intent = new Intent(RandomQuizMultipleChoice.this, RandomQuizResult.class);
        intent.putExtra("score", score);  // Pass the score to the result screen
        startActivity(intent);
        finish();  // Close the current quiz activity
    }
}
