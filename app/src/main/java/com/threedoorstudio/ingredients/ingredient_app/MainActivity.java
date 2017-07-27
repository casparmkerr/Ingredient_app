package com.threedoorstudio.ingredients.ingredient_app;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends Activity {

    private static final int SELECT_PICTURE = 1;

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
        //path = Camera2BasicFragment.getPath();
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

    /*public static String getPath() {
        return path;

    }*/



    public void buttonClickFunction(View v) //Starts import photo-activity on buttonclick
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);

        }

    public void onActivityResult(int requestCode, int resultCode, Intent data) { //When image is returned, lots of crap seems to be needed to actually get the correct filepath
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImage = data.getData();
                String wholeID = DocumentsContract.getDocumentId(selectedImage);

                // Split at colon, use second item in the array
                String id = wholeID.split(":")[1];

                String[] column = {MediaStore.Images.Media.DATA};

                // where id is equal to
                String sel = MediaStore.Images.Media._ID + "=?";

                Cursor cursor = getContentResolver().
                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{id}, null);

                String path = "";

                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    path = cursor.getString(columnIndex);
                }
                cursor.close();

                System.out.println("filePath : " + path);
                Intent intent = new Intent(this, ResultsActivity.class); //Starts resultsactivity
                intent.putExtra("filePathString", path);
                startActivity(intent);



            }




        }




        /*Intent intent = new Intent(getApplicationContext(), ImportPhotoActivity.class);
        startActivity(intent);*/
    }
/*
    public void analyzeImage(View v)
    {
        Intent intent = new Intent(getApplicationContext(), ImportPhotoActivity.class);
        startActivity(intent);
    }*/

}