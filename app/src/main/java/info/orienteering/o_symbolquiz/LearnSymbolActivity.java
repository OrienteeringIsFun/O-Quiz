package info.orienteering.o_symbolquiz;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class LearnSymbolActivity extends AppCompatActivity {

    Integer object_id;
    String current_photo_name;


    public Drawable get_symbol(Integer symbol_id){
        boolean found = false;
        //get all images
        Field[] drawablesFields = info.orienteering.o_symbolquiz.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
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
        ArrayList<Integer> candidates=new ArrayList<Integer>();

        //get all images
        Field[] drawablesFields = info.orienteering.o_symbolquiz.R.drawable.class.getFields();
        for (Field field : drawablesFields) {
            try {
                //Log.i("LOG_TAG", "com.your.project.R.drawable." + field.getName());
                if(field.getName().startsWith("photo_"+symbol_id.toString())) {
                    candidates.add(field.getInt(null));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (candidates.size()==0) {
            current_photo_name =  this.getResources().getResourceEntryName(R.drawable.orienteering_not);
            return getResources().getDrawable(R.drawable.orienteering_not);//TODO: No image image
        } if  (candidates.size()==1) {
            current_photo_name =  this.getResources().getResourceEntryName(candidates.get(0));
            return getResources().getDrawable(candidates.get(0));
        } else {
            int index=new Random().nextInt(candidates.size());
            current_photo_name =  this.getResources().getResourceEntryName(candidates.get(index));
            return getResources().getDrawable(candidates.get(index));
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_learn_symbol);

        Bundle b = getIntent().getExtras();
        object_id = -1; // or other values
        if(b != null)
            object_id = b.getInt("object_id");

        TextView text_object_name = (TextView) findViewById(R.id.text_object_name);
        text_object_name.setText(get_description(object_id, Locale.getDefault().getLanguage()));

        ImageView image_symbol = (ImageView) findViewById(R.id.image_symbol);
        image_symbol.setImageDrawable(get_symbol(object_id));

        ImageView image_photo = (ImageView) findViewById(R.id.image_photo);
        //Drawable object_drawable =get_photo(object_id);
        //current_photo_name = this.getResources().getResourceEntryName(object_drawable.);
        //current_photo_name = object_drawable.toString();
        image_photo.setImageDrawable(get_photo(object_id));
        image_photo.setOnClickListener(photoHandlerNext);

        String description = "ID: "+ object_id;
        TextView text_object_description = (TextView) findViewById(R.id.text_object_description);
        text_object_description.setText(description);
        text_object_description.setOnClickListener(idHandlerPhotoName);

    }

    View.OnClickListener photoHandlerNext = new View.OnClickListener() {
        public void onClick(View v) {
            ImageView image_photo = (ImageView) findViewById(R.id.image_photo);
            //Drawable object_drawable=get_photo(object_id);
            image_photo.setImageDrawable(get_photo(object_id));
            //current_photo_name = object_drawable.toString();

            TextView text_photo_name = (TextView) findViewById(R.id.text_photo_name);
            text_photo_name.setText("");

        }
    };

    View.OnClickListener idHandlerPhotoName = new View.OnClickListener() {
        public void onClick(View v) {
            TextView text_photo_name = (TextView) findViewById(R.id.text_photo_name);
            text_photo_name.setText(current_photo_name);

        }
    };

}