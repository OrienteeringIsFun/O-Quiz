package info.orienteering.o_symbolquiz;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button button_start_quiz;
    Button button_support;
    Button button_learn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_start_quiz = (Button) findViewById(R.id.button_start_quiz);
        button_start_quiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openNewActivity();
                Intent intent = new Intent(MainActivity.this, QuizActivity.class);
                startActivity(intent);
            }
        });

        button_support = (Button) findViewById(R.id.button_support);
        button_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openNewActivity();
                Intent intent = new Intent(MainActivity.this, SupportActivity.class);
                startActivity(intent);
            }
        });

        button_learn = (Button) findViewById(R.id.button_learn);
        button_learn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //openNewActivity();
                Intent intent = new Intent(MainActivity.this, LearnActivity.class);
                startActivity(intent);
            }
        });
    }

}