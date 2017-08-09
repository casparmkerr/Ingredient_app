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
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Caspar on 17/7/17.
 */

public class OcrEngine {




    static Context c;
    static String p;
    Frame frame;
    static String text1;
    static String detectedText;
    static Bitmap treatedBmp;
    static Bitmap bmp;
    private static List<String> tempWordsArrayList = new ArrayList<String>();
    private static List<String> wordsArrayList = new ArrayList<String>();

    static diff_match_patch matchObject = new diff_match_patch();



    //SparseArray text = new SparseArray();
/*
    public OcrEngine(Context context) {
        c = context;
    } //Recieves context necesarry to build the TextRecognizer. Could be merged with setValues


*/
    static void setContext(Context context) {
        c = context;
    }

    static void setValues(String path) {
        p = path;
    }

    static void execute() {
        //c = context;
        Boolean hasWordIngredientAppeared = false;

        System.out.println(p);


        //Bitmap bmp = BitmapFactory.decodeFile( PathToFileString );
        if (p == null) { //Had a bunch of problems with file management, so this tells console if there's no image
            Toast.makeText(c, "Null Path!", Toast.LENGTH_LONG).show();
            System.out.println("Null Path");
        }

        treatedBmp = BitmapPrimer.primeBitmap(c, p); //calls function primeBitmap to prime the bitmap for OCR
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

            /*float delete = 0;
            int numDeletes = 0;
            float insert = 0;
            float equal = 0;
            float ratio = 0;
            LinkedList diff = matchObject.diff_main("ingredi",textBlock.getValue().toLowerCase()); //Finds difference between strings
            for (int k = 0; k < diff.size();k++){
                System.out.println("Reading textblock: "+diff.get(k));
                if (diff.get(k).toString().contains("DELETE")) {
                    delete += (diff.get(k).toString().length() -15);//Returned String contains 15 chars too many
                    numDeletes +=1;
                } else if (diff.get(k).toString().contains("INSERT")) {
                    insert += (diff.get(k).toString().length() -15);//Returned String contains 15 chars too many
                } else if (diff.get(k).toString().contains("EQUAL")){
                    equal += (diff.get(k).toString().length() -14);//Returned String contains 14 chars too many
                }
            }
            if (equal != 0) { //Avoid divide by zero exeption
                ratio = (float) (equal / (equal + insert)); //hopefully this gives a usable ratio
                System.out.println("Ratio to look for 'ingredi': "+ratio+ "   Delete: "+delete+"    Insert: "+insert+"    Equal: "+equal);
            }
            if (ratio > 0.9 && (equal-insert>0) && numDeletes<4) { //If the current word "ingredi" is found "close enough", count it as a match.
                hasWordIngredientAppeared = true;
            }
*/
            if (textBlock.getValue().toLowerCase().contains("ingredi") || textBlock.getValue().toLowerCase().contains("gredien")) {
                hasWordIngredientAppeared = true;
            }

            if (hasWordIngredientAppeared && textBlock != null && textBlock.getValue() != null && textBlock.getValue().length() >2) {
                detectedText += textBlock.getValue().replaceAll("[\n\r]", "").replaceAll(" ", "");
                if (detectedText.endsWith("-")) {
                    detectedText = detectedText.substring(0, detectedText.length() - 1);
                } else
                detectedText += "";

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
