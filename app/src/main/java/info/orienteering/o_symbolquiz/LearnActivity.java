package info.orienteering.o_symbolquiz;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.Set;

public class LearnActivity extends AppCompatActivity {
    ArrayList<Integer> available_photo_ids;
    ArrayList<Integer> available_symbol_ids;
    Set<Integer> available_object_ids;


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
        return getResources().getDrawable(R.drawable.orienteering_not);//TODO: No symbol image
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
            return getResources().getDrawable(R.drawable.orienteering_not);//TODO: No image image
        } if  (candidates.size()==1) {
            return candidates.get(0);
        } else {
            int index=new Random().nextInt(candidates.size());
            return candidates.get(index);
        }
    }

    public List<String[]> getSymbolsAndNames() {//throws IOException {
        List<String[]> rows = new ArrayList<String[]>();
        try {
            InputStream is = this.getAssets().open("isom_symbol_name_mapping.csv");
            InputStreamReader isr = new InputStreamReader(is);
            BufferedReader br = new BufferedReader(isr);
            String csvSplitBy = ",";

            //CSVReader reader = new CSVReader(new FileReader("isom_symbol_name_mapping.csv"));
            CSVReader reader = new CSVReader(br);
            Iterator<String[]> it = reader.iterator();
            String[] line;

            while ((line = reader.readNext()) != null) {
                rows.add(line);
            }
        } catch (Exception e) {}
        return rows;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn);

        List<String[]> symbolsAndNames = getSymbolsAndNames();

        //create rows
        TableLayout table = (TableLayout) findViewById(R.id.table_main);
        for (String[] object_data: symbolsAndNames) {
            Integer object_id=Integer.valueOf(object_data[0]);
            String description = "";
            if(Locale.getDefault().getLanguage()=="de") description = object_data[2];
            else description= object_data[1];
            TableRow tb_row = new TableRow(this);
            tb_row.setBackground(ContextCompat.getDrawable(this, R.drawable.border));
            //tb_row.ellipsize
            if (object_id>10 ) {
                tb_row.setClickable(true);
                tb_row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent myIntent = new Intent(LearnActivity.this, LearnSymbolActivity.class);
                        Bundle b = new Bundle();
                        b.putInt("object_id", object_id); //Your id
                        myIntent.putExtras(b); //Put your id to your next Intent
                        startActivity(myIntent);
                        //finish();
                    }
                });
                TextView tv_id = new TextView(this);
                tv_id.setText(object_id.toString());
                tv_id.setTextColor(Color.BLACK);
                tv_id.setPadding(2,0,2,0);
                tv_id.setGravity(Gravity.CENTER);

                ImageView tv_symbol = new ImageView(this);
                tv_symbol.setImageDrawable(get_symbol(object_id));
                tv_symbol.setLayoutParams(new TableRow.LayoutParams(150, 150));
                tv_symbol.setPadding(2,0,2,0);
                tv_symbol.setScaleType(ImageView.ScaleType.FIT_CENTER);

                tb_row.addView(tv_id);
                tb_row.addView(tv_symbol);
            } else {

                TextView tv_id = new TextView(this);
                tv_id.setText("");
                TextView tv_symbol = new TextView(this);
                tv_symbol.setText("");
                tb_row.addView(tv_id);
                tb_row.addView(tv_symbol);
            }

                TextView tv_desc = new TextView(this);
                tv_desc.setText(description);
                tv_desc.setPadding(2,0,2,0);
                tv_desc.setTextColor(Color.BLACK);
                tv_desc.setTextSize(20);
                tv_desc.setEllipsize(TextUtils.TruncateAt.END);
                //tv_desc.setGravity(Gravity.CENTER);

                /*ImageView tv_photo = new ImageView(this);
                tv_photo.setImageDrawable(get_photo(object_id));
                tv_photo.setScaleType(ImageView.ScaleType.FIT_CENTER);
                tv_photo.setLayoutParams(new TableRow.LayoutParams(150,150));*/

                tb_row.addView(tv_desc);
                //tb_row.addView(tv_photo);
                table.addView(tb_row);

        }

    }
}