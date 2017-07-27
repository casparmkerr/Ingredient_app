package com.threedoorstudio.ingredients.ingredient_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {

    public static String path;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        OcrEngine ocrEngine = new OcrEngine(getApplicationContext());
        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class);
        if (null == savedInstanceState) {

            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        path = Camera2BasicFragment.getPath();
        new ResultsActivity();
    }

    @Override
    public void onBackPressed() { //Called when shutter button pressed
        if (getFragmentManager().getBackStackEntryCount() == 0) {
            this.finish();
        } else {
            getFragmentManager().popBackStack();
        }/*
        path = Camera2BasicFragment.getPath(); //Retrieves image path
        System.out.println("onBackPressed");
        System.out.println("Path in main: " + path);

        Intent intent = new Intent(getApplicationContext(), ResultsActivity.class); //Starts resultsactivity
        intent.putExtra("filePathString", path);
        startActivity(intent);*/
        super.onBackPressed();
        //path = Camera2BasicFragment.getPath();

    }

    public static String getPath() {
        return path;

    }



    public void buttonClickFunction(View v) //Starts import photo-activity on buttonclick
    {
        Intent intent = new Intent(getApplicationContext(), ImportPhotoActivity.class);
        startActivity(intent);
    }
/*
    public void analyzeImage(View v)
    {
        Intent intent = new Intent(getApplicationContext(), ImportPhotoActivity.class);
        startActivity(intent);
    }*/

}