package com.threedoorstudio.ingredients.ingredient_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;

public class MainActivity extends Activity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        if (null == savedInstanceState) {
            OcrEngine ocrEngine = new OcrEngine(this);
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, Camera2BasicFragment.newInstance())
                    .commit();
        }
    }
    public void buttonClickFunction(View v)
    {
        Intent intent = new Intent(getApplicationContext(), ImportPhotoActivity.class);
        startActivity(intent);
    }

}