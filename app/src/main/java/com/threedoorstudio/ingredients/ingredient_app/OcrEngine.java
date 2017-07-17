package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.util.SparseArray;


import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.text.Text;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;
import com.google.android.gms.vision.Detector;

import java.io.File;

/**
 * Created by Caspar on 17/7/17.
 */

public class OcrEngine {


    Context c;
    Frame frame;
    String text1;

    SparseArray text = new SparseArray();
    void setValues(Context context, String PathToFileString) {
        c = context;

        Bitmap bmp = BitmapFactory.decodeFile( PathToFileString );
        TextRecognizer textRecognizer = new TextRecognizer.Builder(c).build();
        Frame outputFrame = new Frame.Builder().setBitmap(bmp).build();
        /*Detector detector = new Detector() {
            @Override
            public SparseArray detect(Frame frame) {
                return null;
            }
        };*/
        //SparseArray<TextBlock> text = detector.detect(frame);
        SparseArray<TextBlock> block = textRecognizer.detect(outputFrame);
        text1 = block.toString();


    }

    String getValues() {
        return text1;
    }








}
