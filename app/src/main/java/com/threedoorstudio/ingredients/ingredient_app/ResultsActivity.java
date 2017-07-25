package com.threedoorstudio.ingredients.ingredient_app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ResultsActivity extends Activity {

    //ImageView mImg;
    //static String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

        Bundle extras = getIntent().getExtras(); //Gets image filepath
        String path = extras.getString("filePathString");

        OcrEngine ocrEngine = new OcrEngine(getApplicationContext()); //Gives OcrEngine some context

        System.out.println("Path in Resultsactivity: " + path); //Again, those paths are sometimes evil, so I like checking





        if (path != null) {




            final Bitmap bmp = BitmapFactory.decodeFile(path); //Creating bitmap
            if (bmp != null) {OcrEngine.setValues(bmp);} //Passes image to text recognition
            else {System.out.println("Shit");}




            ImageView mImg = (ImageView) findViewById(R.id.imageView2);

            Bitmap bmp1 = OcrEngine.getBitmap(); //gets treated bitmap
            mImg.setImageBitmap(bmp1);
            List<String> wordsList = OcrEngine.getWords(); //gets list of recognized words
            TextView textView = (TextView) findViewById(R.id.textView1);
            textView.setText(null);
            for (String word : wordsList) {
                textView.append(word + ", ");

            }
            } else System.out.println("bmp is null");


        }



    /*}
    public static void setValue(String filePath) {
        path = filePath;


    }*/



}
