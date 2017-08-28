package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.renderscript.*;
import android.util.Log;

import com.devddagnet.bright.lib.Bright;
import com.threedoorstudio.ingredients_app.ScriptC_contrast;

import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImage3x3ConvolutionFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBilateralFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageKuwaharaFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSharpenFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageSobelEdgeDetection;
import jp.co.cyberagent.android.gpuimage.GPUImageWeakPixelInclusionFilter;

import static java.lang.Math.max;


/**
 * Created by Caspar on 23/7/17.
 */

public class BitmapPrimer {

    static float[] noiseReductionKernel =
            {(float) 0.111, (float) 0.111, (float) 0.111,
                    (float) 0.111, (float) 0.12, (float) 0.111,
                    (float) 0.111, (float) 0.111, (float) 0.111};
    



    public static Bitmap primeBitmap(Context ctx, String path) {
        Bitmap inMap = BitmapFactory.decodeFile(path); //Creating bitmap


        try { //Needed to rotate pictures taken with samsung phones to correct orientation
            ExifInterface exif=new ExifInterface(path);
            Log.d("EXIF value", exif.getAttribute(ExifInterface.TAG_ORIENTATION));
            if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("6")){
                inMap=rotate(inMap, 90);
            }else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("8")){
                inMap=rotate(inMap, 270);
            }else if(exif.getAttribute(ExifInterface.TAG_ORIENTATION).equalsIgnoreCase("3")) {
                inMap = rotate(inMap, 180);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Transformed Bitmap height, width = "+ inMap.getHeight() +", "+ inMap.getWidth());

        Bitmap resizedbitmap1 = inMap;
        //Cropping bitmap:
        if (inMap.getHeight()>inMap.getWidth()) {
            resizedbitmap1 = Bitmap.createBitmap(inMap, inMap.getWidth() / 8, 3 * inMap.getHeight() / 10, (6 * inMap.getWidth() / 8), 4 * (inMap.getHeight() / 10));
        } else {
            resizedbitmap1 = Bitmap.createBitmap(inMap, 3 * inMap.getWidth() / 10, inMap.getHeight() / 8, 4 * (inMap.getWidth() / 10), (6 * inMap.getHeight() / 8));
        }
        //Bitmap resizedbitmap1 = inMap;

        System.out.println("In primeBitmap");
        RenderScript rs = RenderScript.create(ctx);
        int luminance = findAverageBrightness(resizedbitmap1);
        ScriptC_contrast contrast = new ScriptC_contrast(rs);

        GPUImage mGPUImage1 = new GPUImage(ctx);

        GPUImageFilterGroup group = new GPUImageFilterGroup();
        group.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));

        group.addFilter(new GPUImageSharpenFilter(4));
        //group.addFilter(new GPUImageContrastFilter(2));
        group.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));
        if (luminance<130) {
            group.addFilter(new GPUImageBrightnessFilter((float) 0.1));
        }
        group.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));
        //group.addFilter(new GPUImageKuwaharaFilter(1));
        group.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));


        //group.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));

        mGPUImage1.setFilter(group);

        Bitmap tempMap1 = mGPUImage1.getBitmapWithFilterApplied(resizedbitmap1);

        //int luminance = (int) (1.5*(Bright.setup(Bright.Config.RELATIVE | Bright.Config.PERFORMANCE).brightness(tempMap1)));



        float hv = findHues(tempMap1);

        //contrast.set_hue0(hv[0]);

        int firsHueLimit = (int) (hv-22)%45;

        int[] hueLimits = new int[8];

        for (int i = 0; i<hueLimits.length;i++) {

            hueLimits[i] = (firsHueLimit+i*45)%360;

        }

        contrast.set_hueLimit0(hueLimits[0]);
        contrast.set_hueLimit1(hueLimits[1]);
        contrast.set_hueLimit2(hueLimits[2]);
        contrast.set_hueLimit3(hueLimits[3]);
        contrast.set_hueLimit4(hueLimits[4]);
        contrast.set_hueLimit5(hueLimits[5]);
        contrast.set_hueLimit6(hueLimits[6]);
        contrast.set_hueLimit7(hueLimits[7]);
/*
        contrast.set_hue1(hv[1]);
        contrast.set_hue2(hv[2]);

        contrast.set_value0(hv[3]);
        contrast.set_value1(hv[4]);
        contrast.set_value2(hv[5]);

        for (int i = 0; i< hv.length;i++) {
            System.out.println("hv: " + hv[i]);
        }*/


        System.out.println("Average luminance: "+luminance);

        contrast.set_threshold(luminance);
//The following is basically just what's needed to communicate with renderscript, though I'm not sure if both finish and destroy is necessary. Anyway, didn't seem to do any harm
        Bitmap tempMap2 = Bitmap.createBitmap(tempMap1.getWidth(), tempMap1.getHeight(), tempMap1.getConfig());
        Allocation inAllocation = Allocation.createFromBitmap(rs, tempMap1);
        Allocation outAllocation = Allocation.createFromBitmap(rs, tempMap2);
        contrast.forEach_contrastAndBW(inAllocation, outAllocation);
        outAllocation.copyTo(tempMap2);
        rs.finish();
        rs.destroy();


        //group.addFilter(new GPUImageBrightnessFilter((float) 0.1));
        GPUImage mGPUImage2 = new GPUImage(ctx);
        GPUImageFilterGroup group1 = new GPUImageFilterGroup();

        group1.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));

        //group.addFilter(new GPUImageWeakPixelInclusionFilter());
        group1.addFilter(new GPUImageSharpenFilter(8));
        group1.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));
        group1.addFilter(new GPUImageKuwaharaFilter(1));
        group1.addFilter(new GPUImageContrastFilter(15));
        group1.addFilter(new GPUImageKuwaharaFilter(1));
        group1.addFilter(new GPUImage3x3ConvolutionFilter(noiseReductionKernel));

        //group.addFilter(new GPUImageSobelEdgeDetection());
        //group.addFilter(new GPUImageKuwaharaFilter(1));



        mGPUImage2.setFilter(group1);


        Bitmap outMap = mGPUImage2.getBitmapWithFilterApplied(tempMap2);





        System.out.println("returns outMap");
        return outMap; //Returns treated bitmap to OcrEngine

    }

    public static Bitmap rotate(Bitmap bitmap, int degree) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        Matrix mtx = new Matrix();
        mtx.postRotate(degree);

        return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
    }

    private static int findAverageBrightness(Bitmap src) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();

        int sum = 0;
        int num = 0;

        // color information
        int R, G, B;
        int pixel;

        // scan through all pixels
        for(int x = width/2-100; x < width/2+100; x+=2) {
            //System.out.println("Finding Brightness");
            for(int y = height/2-100; y < height/2+100; ++y) {
                // get pixel color
                pixel = src.getPixel(x, y);

                sum += (Color.red(pixel)+2*Color.green(pixel)+Color.blue(pixel))/4;

                num++;

            }
        }
        return (int) (sum/num);
    }

    private static float findHues(Bitmap src) {
        // image size
        int width = src.getWidth();
        int height = src.getHeight();

        float[] hsv = new float[3];
        float[] hv = new float[6];
        float hue0 = 1000;
        float hue1 = 1000;
        float hue2 = 1000;
        float hue3 = 1000;
        float hue4 = 1000;
        float hue5 = 1000;
        float hue6 = 1000;

        float value0 = 1000;
        float value1 = 1000;
        float value2 = 1000;
        float value3 = 1000;
        float value4 = 1000;
        float value5 = 1000;
        float value6 = 1000;


        int count0 = 2;
        int count1 = 2;
        int count2 = 2;
        int count3 = 2;
        int count4 = 2;
        int count5 = 2;
        int count6 = 2;

        int pixel;

        // scan through all pixels
        for(int x = 2*width/5; x < 3*width/5; x+=2) {
            //System.out.println("Finding Hue");
            for(int y = 2*height/5; y < 3*height/5; y+=2) {
                // get pixel color
                pixel = src.getPixel(x, y);

                //sum += (Color.red(pixel)+2*Color.green(pixel)+Color.blue(pixel))/4;
                Color.RGBToHSV(Color.red(pixel),Color.green(pixel),Color.blue(pixel),hsv);

                //if (hsv[0] >=0 && hsv[0] <30) {





                if (hue0 == 1000) {
                    hue0 = hsv[0];
                    value0 = hsv[2];
                } else if (hsv[0] < hue0+30 && hsv[0] > hue0-30 && hsv[2] < value0+0.1f && hsv[2] > value0-0.1f) {
                    hue0 = ((count0-1)*hsv[0]+hue0)/count0;
                    value0 = ((count0-1)*hsv[2]+value0)/count0;
                    count0++;
                } else if (hue1 == 1000){
                    hue1 = hsv[0];
                    value1 = hsv[2];
                } else if (hsv[0] < hue1+30 && hsv[0] > hue1-30 && hsv[2] < value1+0.15f && hsv[2] > value1-0.15f){
                    hue1 = ((count1-1)*hsv[0]+hue1)/count1;
                    value1 = ((count1-1)*hsv[2]+value1)/count1;
                    count1++;
                } else if (hue2 == 1000){
                    hue2 = hsv[0];
                    value1 = hsv[2];
                } else if (hsv[0] < hue2+30 && hsv[0] > hue2-30 && hsv[2] < value2+0.15f && hsv[2] > value2-0.15f){
                    hue2 = ((count1-1)*hsv[0]+hue2)/count2;
                    value2 = ((count2-1)*hsv[2]+value2)/count2;
                    count2++;
                } else if (hue3 == 1000){
                    hue3 = hsv[0];
                    value3 = hsv[2];
                } else if (hsv[0] < hue3+30 && hsv[0] > hue3-30 && hsv[2] < value3+0.15f && hsv[2] > value3-0.15f){
                    hue3 = ((count3-1)*hsv[0]+hue3)/count3;
                    value3 = ((count3-1)*hsv[2]+value3)/count3;
                    count3++;
                } else if (hue4 == 1000){
                    hue4 = hsv[0];
                    value4 = hsv[2];
                } else if (hsv[0] < hue4+30 && hsv[0] > hue4-30 && hsv[2] < value4+0.15f && hsv[2] > value4-0.15f){
                    hue4 = ((count4-1)*hsv[0]+hue4)/count4;
                    value4 = ((count4-1)*hsv[2]+value4)/count4;
                    count4++;
                } else if (hue5 == 1000){
                    hue5 = hsv[0];
                    value5 = hsv[2];
                } else if (hsv[0] < hue5+30 && hsv[0] > hue5-30 && hsv[2] < value5+0.15f && hsv[2] > value5-0.15f){
                    hue5 = ((count5-1)*hsv[0]+hue5)/count5;
                    value5 = ((count5-1)*hsv[2]+value5)/count5;
                    count5++;
                } else if (hue6 == 1000) {
                    hue6 = hsv[0];
                    value6 = hsv[2];
                } else {
                    hue6 = ((count6-1)*hsv[0]+hue6)/count6;
                    value6 = ((count6-1)*hsv[2]+value6)/count6;
                    count6++;
                }

                //System.out.println("Hue, value: "+hsv[0]+ " , "+hsv[2]);
/*
                if (value0 == 1000) {
                    value0 = hsv[2];
                } else if (hsv[2] < value0+10 && hsv[2] > value0-10) {
                    value0 = (hsv[2]+value0)/2;
                } else if (value1 == 1000){
                    value1 = hsv[2];
                } else if (hsv[2] < value1+10 && hsv[2] > value1-10){
                    value1 = (hsv[2]+value1)/2;
                } else if (value2 == 1000) {
                    value2 = hsv[2];
                } else {
                    value2 = (hsv[2]+value2)/2;
                }
*/
            }
        }

        int maxCount = max(max(count0,count1),max(max(count2,count3),max(count4,count5)));
        if (maxCount == count0) {
            return value0;
        } else if (maxCount == count1) {
            return value1;
        } else if (maxCount == count2) {
            return value2;
        } else if (maxCount == count3) {
            return value3;
        } else if (maxCount == count4) {
            return value4;
        } else if (maxCount == count5) {
            return value5;
        } else {
            return value6;
        }
        /*hv[0] = hue0;
        hv[1] = hue1;
        hv[2] = hue2;

        hv[3] = value0;
        hv[4] = value1;
        hv[5] = value2;*/


    }




//Old, horribly slow methods that are currently replaced by GPUImage and in-built image effects, though not well enough:
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
/*
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

