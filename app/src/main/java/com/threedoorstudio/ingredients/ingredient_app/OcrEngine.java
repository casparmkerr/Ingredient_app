package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v7.app.AlertDialog;
import android.util.SparseArray;
import android.widget.Toast;


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




    static Context c;
    Frame frame;
    static String text1;
    static String detectedText;



    //SparseArray text = new SparseArray();

    public OcrEngine(Context context) {
        c = context;
    }

    static void setValues(String PathToFileString) {


        Bitmap bmp = BitmapFactory.decodeFile( PathToFileString );
        if (bmp == null) {
            Toast.makeText(c, "Null Bitmap!", Toast.LENGTH_LONG).show();
        }
        TextRecognizer textRecognizer = new TextRecognizer.Builder(c).build();
        Frame outputFrame = new Frame.Builder().setBitmap(bmp).build();
        Detector detector = new Detector() {
            @Override
            public SparseArray detect(Frame frame) {
                return null;
            }
        };
        if (!textRecognizer.isOperational()) {
            Toast.makeText(c, "Text Recognizer not operational", Toast.LENGTH_LONG);
            System.out.println("Text Recognizer not operational");
            new AlertDialog.Builder(c)
                    .setMessage("Text recognizer could not be set up on your device :(").show();
            return;
        }
        //SparseArray<TextBlock> text = detector.detect(frame);
        SparseArray<TextBlock> text = textRecognizer.detect(outputFrame);
        //text1 = block.toString();
        for (int i = 0; i < text.size(); i++) {
            TextBlock textBlock = text.valueAt(i);
            if (textBlock != null && textBlock.getValue() != null) {
                detectedText += textBlock.getValue();
            }
        }
        //detectedTextView.setText(detectedText);
        System.out.println("Text: " + detectedText);
        textRecognizer.release();


    }

    static String getValues() {
        if (detectedText != null) {
            return detectedText;
        } else {
            return "No text";
        }
    }








}
