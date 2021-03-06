package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.renderscript.*;

import com.threedoorstudio.ingredients_app.ScriptC_contrast;


/**
 * Created by Caspar on 23/7/17.
 */

public class BitmapPrimer {



    public static Bitmap primeBitmap(Context ctx, Bitmap inMap) {
        System.out.println("In primeBitmap");
        RenderScript rs = RenderScript.create(ctx);

        ScriptC_contrast contrast = new ScriptC_contrast(rs); //The following is basically just what's needed to communicate with renderscript, though I'm not sure if both finish and destroy is necessary. Anyway, didn't seem to do any harm


        Bitmap outMap = Bitmap.createBitmap(inMap.getWidth(), inMap.getHeight(), inMap.getConfig());
        Allocation inAllocation = Allocation.createFromBitmap(rs, inMap);
        Allocation outAllocation = Allocation.createFromBitmap(rs, outMap);
        contrast.forEach_contrastAndBW(inAllocation, outAllocation);
        outAllocation.copyTo(outMap);
        rs.finish();
        rs.destroy();
        System.out.println("returns outMap");
        return outMap; //Returns treated bitmap to OcrEngine

    }

//Old, horribly slow methods that are currently replaced by Renderscript and in-built image effects:
    /*
    private static Bitmap sharpen(Bitmap bitmap) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        int red, green, blue;
        int a1, a2, a3, a4, a5, a6, a7, a8, a9;
        Bitmap bmpBlurred = Bitmap.createBitmap(width, height,bitmap.getConfig());

        Canvas canvas = new Canvas(bmpBlurred);

        canvas.drawBitmap(bitmap, 0, 0, null);
        for (int i = 1; i < width - 1; i++) {
            for (int j = 1; j < height - 1; j++) {

                a1 = bitmap.getPixel(i - 1, j - 1);
                a2 = bitmap.getPixel(i - 1, j);
                a3 = bitmap.getPixel(i - 1, j + 1);
                a4 = bitmap.getPixel(i, j - 1);
                a5 = bitmap.getPixel(i, j);
                a6 = bitmap.getPixel(i, j + 1);
                a7 = bitmap.getPixel(i + 1, j - 1);
                a8 = bitmap.getPixel(i + 1, j);
                a9 = bitmap.getPixel(i + 1, j + 1);

                red = (Color.red(a1) + Color.red(a2) + Color.red(a3) + Color.red(a4) + Color.red(a6) + Color.red(a7) + Color.red(a8) + Color.red(a9)) *(-1)   + Color.red(a5)*9 ;
                if(red < 0) { red = 0; }
                else if(red > 255) { red = 255; }
                /*green = (Color.green(a1) + Color.green(a2) + Color.green(a3) + Color.green(a4) + Color.green(a6) + Color.green(a7) + Color.green(a8) + Color.green(a9)) *(-1)  + Color.green(a5)*9 ;
                if(green < 0) { green = 0; }
                else if(green > 255) { green = 255; }
                blue = (Color.blue(a1) + Color.blue(a2) + Color.blue(a3) + Color.blue(a4) + Color.blue(a6) + Color.blue(a7) + Color.blue(a8) + Color.blue(a9)) *(-1)   + Color.blue(a5)*9 ;
                if(blue < 0) { blue = 0; }
                else if(blue > 255) { blue = 255; }
                green = blue = red; //Expecting gray scale image

                bmpBlurred.setPixel(i, j, Color.rgb(red, green, blue));
            }
        }
        return bmpBlurred;
    }

    private static Bitmap noiseReduction(Bitmap src) {
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

        int[] r = new int[9];
        int[] g = new int[9];
        int[] b = new int[9];
        int A, R, G, B;
        int pixel0;
        int pixel1;

        for(int x = 0; x < width; ++x) {
            System.out.println("Reducing noise");
            for(int y = 0; y < height; ++y) {
                int k = 0;


                // get pixel color
                pixel0 = src.getPixel(x, y);
                int ir = Color.red(pixel0);
                int ig = Color.green(pixel0);
                int ib = Color.blue(pixel0);




                for (int dy = -1; dy <= 1; dy++) {
                    int iy = y + dy;
                    if (0 <= iy && iy < height) {

                        for (int dx = -1; dx <= 1; dx++) {
                            int ix = x + dx;
                            if (0 <= ix && ix < width) {
                                pixel1 = src.getPixel(ix, iy);
                                R = Color.red(pixel1);


                                r[k] = Color.red(pixel1);
                                g[k] = Color.green(pixel1);
                                b[k] = Color.blue(pixel1);
                            } else {
                                r[k] = ir;
                                g[k] = ig;
                                b[k] = ib;
                            }
                            k++;
                        }
                    } else {
                        for (int dx = -1; dx <= 1; dx++) {
                            r[k] = ir;
                            g[k] = ig;
                            b[k] = ib;
                            k++;
                        }
                    }
                }



                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(Color.alpha(pixel0), smooth(r), smooth(g), smooth(b)));
            }
        }
        return bmOut;



    }
    private static int smooth(int[] v) {
        int minindex = 0, maxindex = 0, min = Integer.MAX_VALUE, max = Integer.MIN_VALUE;

        for (int i = 0; i < 9; i++) {
            if ( i != 4 ) {
                if (v[i] < min) {
                    min = v[i];
                    minindex = i;
                }
                if (v[i] > max) {
                    max = v[i];
                    maxindex = i;
                }
            }
        }
        if ( v[4] < min ) {
            return v[minindex];}
        else if ( v[4] > max ) {
            return v[maxindex];}
        else {return v[4];}
    }


    private static Bitmap toGrayscale(Bitmap bmpOriginal)
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
            System.out.println("Adjusting contrast");
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
                else if(B > 255) { B = 255; }
                B = G = R;

                // set new pixel color to output bitmap
                bmOut.setPixel(x, y, Color.argb(A, R, G, B));
            }
        }
        return bmOut;
    }*/
}

