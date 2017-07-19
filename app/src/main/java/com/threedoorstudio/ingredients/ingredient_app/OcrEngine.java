package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;

import android.graphics.*;
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
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Caspar on 17/7/17.
 */

public class OcrEngine {




    static Context c;
    Frame frame;
    static String text1;
    static String detectedText;
    static Bitmap treatedBmp;
    static List<String> wordsArrayList = new ArrayList<String>();


    //SparseArray text = new SparseArray();

    public OcrEngine(Context context) {
        c = context;
    }

    static void setValues(String PathToFileString) {


        Bitmap bmp = BitmapFactory.decodeFile( PathToFileString );
        if (bmp == null) {
            Toast.makeText(c, "Null Bitmap!", Toast.LENGTH_LONG).show();
        }

        treatedBmp = adjustedContrast(toGrayscale(bmp), 70);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(c).build();
        Frame outputFrame = new Frame.Builder().setBitmap(treatedBmp).build();
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
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(outputFrame);
        //text1 = block.toString();
        detectedText = "";
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.valueAt(i);
            if (textBlock != null && textBlock.getValue() != null) {
                detectedText += textBlock.getValue();
            }
        }
        String blocks = "";
        String lines = "";

        String words = "";


        for (int index = 0; index < textBlocks.size(); index++) {
            //extract scanned text blocks here
            TextBlock tBlock = textBlocks.valueAt(index);
            blocks = blocks + tBlock.getValue() + "\n" + "\n";
            for (Text line : tBlock.getComponents()) {
                //extract scanned text lines here
                lines = lines + line.getValue() + "\n";
                for (Text element : line.getComponents()) {
                    //extract scanned text words here
                    wordsArrayList.add(element.getValue());
                    words = words + element.getValue() + ", ";
                }
            }
        }
        //detectedTextView.setText(detectedText);
        System.out.println("Text: " + detectedText);
        textRecognizer.release();


    }

    static List<String> getWords() {
        return wordsArrayList;
    }

    static String getValues() {
        if (detectedText != null) {
            return detectedText;
        } else {
            return "No text";
        }
    }
    static Bitmap getBitmap() {
        if (treatedBmp == null) {
            System.out.println("OH NO! treatedBmp is null");
        } else {
            System.out.println("YAY! it's not null!");
        }
        return treatedBmp;

    }

    public static Bitmap toGrayscale(Bitmap bmpOriginal)
    {
        int width, height;
        height = bmpOriginal.getHeight();
        width = bmpOriginal.getWidth();

        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bmpOriginal, 0, 0, paint);
        return bmpGrayscale;
    }

    private static Bitmap adjustedContrast(Bitmap src, double value)
    {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, src.getConfig());

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // get contrast value
        double contrast = Math.pow((100 + value) / 100, 2);

        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.alpha(pixel);
                // apply filter contrast for every channel R, G, B
                R = Color.red(pixel);
                R = (int)(((((R / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(R < 0) { R = 0; }
                else if(R > 255) { R = 255; }

                /*
                G = Color.green(pixel);
                G = (int)(((((G / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(G < 0) { G = 0; }
                else if(G > 255) { G = 255; }

                B = Color.blue(pixel);
                B = (int)(((((B / 255.0) - 0.5) * contrast) + 0.5) * 255.0);
                if(B < 0) { B = 0; }
                else if(B > 255) { B = 255; } */
                B = G = R;

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }







}
