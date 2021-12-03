package info.orienteering.o_symbolquiz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class QuizActivity extends AppCompatActivity {

    //config
    boolean debug = false;
    String state="starting";

    //State: tested symbols
    List<Integer> ids_tested = new ArrayList<>();
    Integer current_test_id = 0;
    Integer[] current_symbol_ids = new Integer[3];
    List<Integer> quiz_answer_objects;
    Integer quiz_round=0;

    Button back_to_main_button;
    Button button_next;
    TextView quiz_photo_id;
    TextView quiz_option_1;
    TextView quiz_option_2;
    TextView quiz_option_3;
    TextView quiz_result;
    TextView debug_text;
    ImageButton symbol_answer_1;
    ImageButton symbol_answer_2;
    ImageButton symbol_answer_3;
    ArrayList<Integer> available_photo_ids;
    ArrayList<Integer> available_symbol_ids;
    ArrayList<Integer> available_object_ids;

    int color_correct = Color.parseColor("#ccffcc");
    int color_wrong = Color.parseColor("#ffcccc");
    int color_normal = Color.parseColor("#FAF8F8");

    public Drawable get_symbol(Integer symbol_id){
        boolean found = false;
        //get all images
        Field[] drawablesFields = info.orienteering.o_symbolquiz.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
                //Log.i("LOG_TAG", "com.your.project.R.drawable." + field.getName());
                if(field.getName().startsWith("symbol_"+symbol_id.toString())) {
                    return getResources().getDrawable(field.getInt(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getResources().getDrawable(R.drawable.orienteering_not);
    }

    public Drawable get_photo(Integer symbol_id){
        ArrayList<Drawable> candidates=new ArrayList<Drawable>();

        //get all images
        Field[] drawablesFields = info.orienteering.o_symbolquiz.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
                //Log.i("LOG_TAG", "com.your.project.R.drawable." + field.getName());
                if(field.getName().startsWith("photo_"+symbol_id.toString())) {
                    candidates.add(getResources().getDrawable(field.getInt(null)));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (candidates.size()==0) {
            return getResources().getDrawable(R.drawable.orienteering_not);
        } if  (candidates.size()==1) {
            return candidates.get(0);
        } else {
            int index=new Random().nextInt(candidates.size());
            return candidates.get(index);
        }
    }

    public String get_description(Integer object_id, String lang){
        try {
            InputStream is = this.getAssets().open("isom_symbol_name_mapping.csv");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String csvSplitBy = ",";
            br.readLine();

            CSVReader reader = new CSVReader(br);
            Iterator<String[]> it = reader.iterator();
            String[] line;

            while ((line = reader.readNext()) != null) {
                if (Integer.parseInt(line[0])==object_id)
                    if (lang=="de") return line[2];
                    else return line[1];
            }
        } catch (Exception e) {}
        return "/";
    }

    public void new_quiz(){
        current_test_id=quiz_answer_objects.get(quiz_round);

        TextView quiz_result_name = (TextView) findViewById(R.id.quiz_result_name);
        quiz_result_name.setText(""+(quiz_round+1) +"/"+quiz_answer_objects.size());

        ArrayList<Integer> round_answers = new ArrayList<Integer>();
        round_answers.add(current_test_id);
        while (round_answers.size()<3){
            int index=new Random().nextInt(available_object_ids.size()-1);
            if (!round_answers.contains(available_object_ids.get(index))){
                round_answers.add(available_object_ids.get(index));
            }
        }
        Collections.shuffle(round_answers);

        current_symbol_ids[0]=round_answers.get(0);
        current_symbol_ids[1]=round_answers.get(1);
        current_symbol_ids[2]=round_answers.get(2);

        /*if (debug) { // debug ids
            quiz_photo_id = (TextView) findViewById(R.id.text_photo_id);
            quiz_photo_id.setText(current_test_id.toString());
            quiz_option_1 = (TextView) findViewById(R.id.quiz_option_1);
            quiz_option_1.setText(current_symbol_ids[0].toString());
            quiz_option_2 = (TextView) findViewById(R.id.quiz_option_2);
            quiz_option_2.setText(current_symbol_ids[1].toString());
            quiz_option_3 = (TextView) findViewById(R.id.quiz_option_3);
            quiz_option_3.setText(current_symbol_ids[2].toString());
        }*/

        // switch image + symbols
        ImageView photoObject = (ImageView) findViewById(R.id.photoObject);
        photoObject.setImageDrawable(get_photo(current_test_id));
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        //photoObject.set

        symbol_answer_1 = (ImageButton) findViewById(R.id.symbol_answer_1);
        symbol_answer_1.setImageDrawable(get_symbol(current_symbol_ids[0]));
        symbol_answer_2 = (ImageButton) findViewById(R.id.symbol_answer_2);
        symbol_answer_2.setImageDrawable(get_symbol(current_symbol_ids[1]));
        symbol_answer_3 = (ImageButton) findViewById(R.id.symbol_answer_3);
        symbol_answer_3.setImageDrawable(get_symbol(current_symbol_ids[2]));
        quiz_round++;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_start);

        //get all images
        available_photo_ids = new ArrayList<Integer>();
        available_symbol_ids = new ArrayList<Integer>();
        Field[] drawablesFields = info.orienteering.o_symbolquiz.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
                //Log.i("LOG_TAG", "com.your.project.R.drawable." + field.getName());
                String drawable_name = field.getName();
                if(drawable_name.startsWith("photo_")) {
                    try {
                        available_photo_ids.add(Integer.valueOf(drawable_name.split("_")[1]));
                    } catch (Exception e){}
                } else if (drawable_name.startsWith("symbol_")) {
                    try {
                        available_symbol_ids.add(Integer.valueOf(drawable_name.split("_")[1]));
                    } catch (Exception e){}
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        //ids available for the quiz (we have a symbol and an image)
        available_object_ids = new ArrayList<Integer>();
        for (int id:available_symbol_ids){
            if(available_photo_ids.contains(id)) available_object_ids.add(id);
        }

        if (debug) {
            debug_text = (TextView) findViewById(R.id.debug_text);
            debug_text.setText(available_object_ids.toString());
        }

        // create list of quizzes
        quiz_answer_objects = new ArrayList<Integer>();
        for(int i = 0; i < available_object_ids.size() && i < 30; i++) quiz_answer_objects.add(available_object_ids.get(i));
        Collections.shuffle(quiz_answer_objects);

        // start first quiz
        new_quiz();

        // action handler
        symbol_answer_1= (ImageButton)findViewById(R.id.symbol_answer_1);
        symbol_answer_1.setOnClickListener(imgButtonHandler1);
        symbol_answer_2= (ImageButton)findViewById(R.id.symbol_answer_2);
        symbol_answer_2.setOnClickListener(imgButtonHandler2);
        symbol_answer_3= (ImageButton)findViewById(R.id.symbol_answer_3);
        symbol_answer_3.setOnClickListener(imgButtonHandler3);
        button_next= (Button)findViewById(R.id.button_next);
        button_next.setOnClickListener(buttonHandlerNext);
        button_next.setVisibility(View.GONE);
        back_to_main_button = (Button) findViewById(R.id.button_end);
        back_to_main_button.setOnClickListener(buttonHandlerEnd);
        back_to_main_button.setVisibility(View.GONE);

    }

    View.OnClickListener imgButtonHandler1 = new View.OnClickListener() {
        public void onClick(View v) {
            check_answer(0);
        }
    };
    View.OnClickListener imgButtonHandler2 = new View.OnClickListener() {
        public void onClick(View v) {
            check_answer(1);
        }
    };
    View.OnClickListener imgButtonHandler3 = new View.OnClickListener() {
        public void onClick(View v) {
            check_answer(2);
        }
    };
    View.OnClickListener buttonHandlerNext = new View.OnClickListener() {
        public void onClick(View v) {
            next_quiz();
            state="next_quiz";
        }
    };
    View.OnClickListener buttonHandlerEnd = new View.OnClickListener() {
        public void onClick(View v) {
            back_to_main();
        }
    };

    public void back_to_main(){
        finish();
        Intent intent = new Intent(QuizActivity.this, MainActivity.class);
        startActivity(intent);
    }

    public void next_quiz(){
        // clean up buttons
        button_next= (Button)findViewById(R.id.button_next);
        button_next.setVisibility(View.GONE);
        symbol_answer_1.setBackgroundColor(color_normal);
        symbol_answer_2.setBackgroundColor(color_normal);
        symbol_answer_3.setBackgroundColor(color_normal);
        // start
        new_quiz();
    }
    synchronized public void check_answer(Integer choosen_option){
        TextView quiz_result_name = (TextView) findViewById(R.id.quiz_result_name);
        quiz_result_name.setText(""+(quiz_round) +"/"+quiz_answer_objects.size() + " " + get_description(current_test_id, Locale.getDefault().getLanguage()));

        if (state!="answered") {
            // evaluate
            String result = "";
            if (current_symbol_ids[choosen_option] == current_test_id) {
                result = "correct";
            } else {
                result = "wrong";
            }

            // set feedback background color
            symbol_answer_1.setBackgroundColor(color_normal);
            symbol_answer_2.setBackgroundColor(color_normal);
            symbol_answer_3.setBackgroundColor(color_normal);
            if (choosen_option == 0 && result == "wrong") {
                symbol_answer_1.setBackgroundColor(color_wrong);
            } else if (choosen_option == 1 && result == "wrong") {
                symbol_answer_2.setBackgroundColor(color_wrong);
            } else if (choosen_option == 2 && result == "wrong") {
                symbol_answer_3.setBackgroundColor(color_wrong);
            }
            if (current_symbol_ids[0] == current_test_id) {
                symbol_answer_1.setBackgroundColor(color_correct);
            } else if (current_symbol_ids[1] == current_test_id) {
                symbol_answer_2.setBackgroundColor(color_correct);
            } else if (current_symbol_ids[2] == current_test_id) {
                symbol_answer_3.setBackgroundColor(color_correct);
            }

            // enable next button
            button_next = (Button) findViewById(R.id.button_next);
            button_next.setVisibility(View.VISIBLE);

            // test for end of round
            if (quiz_round == quiz_answer_objects.size()) {
                button_next.setVisibility(View.GONE);
                back_to_main_button = (Button) findViewById(R.id.button_end);
                back_to_main_button.setVisibility(View.VISIBLE);
            }
            state = "answered";
        }
    }
}
