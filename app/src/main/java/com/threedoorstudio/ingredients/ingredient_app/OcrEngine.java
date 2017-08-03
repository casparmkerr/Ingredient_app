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
import java.util.Arrays;
import java.util.Collections;
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
    static Bitmap bmp;
    private static List<String> tempWordsArrayList = new ArrayList<String>();
    private static List<String> wordsArrayList = new ArrayList<String>();

    //SparseArray text = new SparseArray();
/*
    public OcrEngine(Context context) {
        c = context;
    } //Recieves context necesarry to build the TextRecognizer. Could be merged with setValues
*/
    static void setValues(Context context, Bitmap bmp) {
        c = context;

        System.out.println(bmp);


        //Bitmap bmp = BitmapFactory.decodeFile( PathToFileString );
        if (bmp == null) { //Had a bunch of problems with file management, so this tells console if there's no image
            Toast.makeText(c, "Null Bitmap!", Toast.LENGTH_LONG).show();
            System.out.println("Null Bitmap");
        }

        treatedBmp = BitmapPrimer.primeBitmap(c, bmp); //calls function primeBitmap to prime the bitmap for OCR
        //treatedBmp = alphaToRGB(argbToAlpha(bmp));
        TextRecognizer textRecognizer = new TextRecognizer.Builder(c).build(); //Initiating Mobile Vision textRecognizer
        Frame outputFrame = new Frame.Builder().setBitmap(treatedBmp).build();
        Detector detector = new Detector() { //Really not 100% sure if this is necessary, but I haven't bothered checking. Things seemed to be ok
            @Override
            public SparseArray detect(Frame frame) {
                return null;
            }
        };
        if (!textRecognizer.isOperational()) { //Supposedly a good idea to check if OCR was initialized
            Toast.makeText(c, "Text Recognizer not operational", Toast.LENGTH_LONG);
            System.out.println("Text Recognizer not operational");
            new AlertDialog.Builder(c)
                    .setMessage("Text recognizer could not be set up on your device :(").show();
            return;
        }
        //SparseArray<TextBlock> text = detector.detect(frame);
        SparseArray<TextBlock> textBlocks = textRecognizer.detect(outputFrame); //Runs actual OCR


        //text1 = block.toString();
        detectedText = ""; //Extracing text in various way down here, needs to be cleaned up eventually
        for (int i = 0; i < textBlocks.size(); i++) {
            TextBlock textBlock = textBlocks.valueAt(i);
            if (textBlock != null && textBlock.getValue() != null && textBlock.getValue().length() >2) {
                detectedText += textBlock.getValue().replaceAll("[\n\r]", "");
                if (detectedText.endsWith("-")) {
                    detectedText = detectedText.substring(0, detectedText.length() - 1);
                } else
                detectedText += " ";

            }
        }
        String blocks = "";
        String lines = "";

        String words = "";
        //wordsArrayList.clear();

        for (int index = 0; index < textBlocks.size(); index++) {
            //extract scanned text blocks here
            TextBlock tBlock = textBlocks.valueAt(index);

            blocks = blocks + tBlock.getValue() + "\n" + "\n";


            for (Text line : tBlock.getComponents()) {
                //extract scanned text lines here
                lines = lines + line.getValue() /*+ "\n"*/;

                for (Text element : line.getComponents()) {
                    //extract scanned text words here
                    //wordsArrayList.add(element.getValue());
                    words = words + element.getValue() + ", ";
                }
            }
        }

        wordsArrayList.clear();
        tempWordsArrayList = Arrays.asList(detectedText.split("(?=[,.])|\\s+"));
        tempWordsArrayList.removeAll(Collections.singleton(null));
        for (int i = 0; i<tempWordsArrayList.size();i++){
            if (tempWordsArrayList.get(i).length()<3) {
                continue; //No point searching through words of shorter length than any substance in the database
            } else if (tempWordsArrayList.get(i).length()>28){ //Sometimes it concatenates words that should not be concatenated; this might help be trying to identify new ingredients based upon capital letters.
                String[] r = tempWordsArrayList.get(i).split("(?=\\p{Lu})"); //Might mess up long words beginning with a number followed by a capital letter, or when it detects a lower-case letter as upper-case.
                for (int j = 0; j<r.length;j++) {
                    wordsArrayList.add(r[j]);
                }
            } else {
                wordsArrayList.add(tempWordsArrayList.get(i));
            }
        }
        //detectedTextView.setText(detectedText);
        System.out.println("Text: " + detectedText);
        textRecognizer.release();


    }

    static List<String> getWords() {
        return wordsArrayList;
    } //Returns text as ArrayList

    static String getValues() { //Returns text as a block
        if (detectedText != null) {
            return detectedText;
        } else {
            return "No text";
        }
    }
    static Bitmap getBitmap() { //Returns primed Bitmap
        if (treatedBmp == null) {
            System.out.println("OH NO! treatedBmp is null");
        } else {
            System.out.println("YAY! it's not null!");
        }
        return treatedBmp;

    }


    /* Failed attemps to use less memory by using an alpha channel only bitmap instead of ARGB:


    private static Bitmap argbToAlpha(Bitmap src) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ALPHA_8);

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A;
        int pixel;
        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            System.out.println("Converting to alpha: "+ x);

            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = Color.red(pixel);

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.alpha(A));
            }
        }
        return bmOut;
    }

    private static Bitmap alphaToRGB(Bitmap src) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();
        // create output bitmap

        // create a mutable empty bitmap
        Bitmap bmOut = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        // create a canvas so that we can draw the bmOut Bitmap from source bitmap
        Canvas c = new Canvas();
        c.setBitmap(bmOut);

        // draw bitmap to bmOut from src bitmap so we can modify it
        c.drawBitmap(src, 0, 0, new Paint(Color.BLACK));


        // color information
        int A, R, G, B;
        int pixel;
        // scan through all pixels
        for(int x = 0; x < width; ++x) {
            System.out.println("Converting to ARGB: " + x);
            for(int y = 0; y < height; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);
                A = 255;
                R = Color.alpha(pixel);


                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A,R,R,R));
            }
        }
        return bmOut;
    }*/









}
