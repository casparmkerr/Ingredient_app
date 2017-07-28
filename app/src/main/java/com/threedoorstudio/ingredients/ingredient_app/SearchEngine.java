package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.*;
import java.lang.Math;
import java.util.Arrays;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import com.threedoorstudio.ingredients_app.ScriptC_search;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static java.lang.Math.*;

/**
 * Created by Caspar on 25/7/17.
 */

public class SearchEngine {
    //SearchEngine searchengine = new SearchEngine();

    String badStuff[] = {"slik", "bestiller", "du", "pizza", "nei", "kanskje", "joda", "beregn", "trykkprisen", "selv","vi"}; //Temp string to be replaced by list of bad substances
    int badLength = badStuff.length;



    int k;

    ArrayList<String> matches = new ArrayList<String>();
    public ArrayList<String> matchWords(List<String> ingredients) {
        System.out.println("In SearchEngine");
        Arrays.sort(badStuff);
        int listSize = ingredients.size();
        ArrayList<String> modIngredients = new ArrayList<String>();


        for(int i = 0; i < listSize; ++i) {
            String temp = ingredients.get(i).replaceAll("[^a-zA-Z ]", "").toLowerCase(); //Strips away stuff for flexibility in writing - doesn't seem to really work well enough though
            if (temp.length()<3){continue;}
            String match = binarySearch(badStuff, temp);
            if (match != null) {
                modIngredients.add(ingredients.get(i)+" MATCH"); //Adds "MATCH" if it's a match. Needs to be changed later, but works for now. List should probably sorted so the matches end ut on top too.
                //matches.add(match);
            } else {
                modIngredients.add(ingredients.get(i)); //if no match, add element as detected
            }

        }




/*
        MismatchSearch mismatch = new MismatchSearch() {
            @Override
            public Object processBytes(byte[] pattern, int k) {
                return null;
            }

            @Override
            public Object processChars(char[] pattern, int k) {
                return null;
            }

            @Override
            public int[] searchBytes(byte[] text, int textStart, int textEnd, byte[] pattern, Object processed, int k) {
                return new int[0];
            }

            @Override
            public int[] searchChars(char[] text, int textStart, int textEnd, char[] pattern, Object processed, int k) {
                return new int[0];
            }
        };
        ArrayList<int[]> misma = new ArrayList<>();

        Pattern pattern;
        Matcher matcher;
        // Returns index of x if it is present in arr[], else
        // return -1


        for(int i = 0; i < listSize; ++i) {
            if (ingredients.get(i).length()<4){continue;}
            char b0 = ingredients.get(i).charAt(0);
            char b1 = ingredients.get(i).charAt(1);
            String ingredientWord = ingredients.get(i);
            int l = 0, r = badLength - 1;
            while (l <= r) {
                int m = l + (r - l) / 2;
                String wordIndex = badStuff[m];
                char a0 = wordIndex.charAt(0);
                char a1 = wordIndex.charAt(1);

                // Check if x is present at mid
                if (ingredientWord == wordIndex) {
                    matches.add(ingredientWord);
                }


                // If x greater, ignore left half
                if (wordIndex < ingredientWord)
                    l = m + 1;

                    // If x is smaller, ignore right half
                else
                    r = m - 1;
            }

            // if we reach here, then element was not present

        }
        for(int i = 0; i < listSize; ++i) {
            for (int j = 0; j< badLength; ++j){


                String temp = ingredients.get(i).replaceAll("[^a-zA-Z ]", "").toLowerCase();
                if (temp.equals(badStuff[j])){
                    matches.add(temp);
                    System.out.println("Matched: "+temp+", "+badStuff[j]);
                } else {
                    System.out.println("Searched: "+temp+", "+badStuff[j]);
                }

                //System.out.println("In loop at least");

            }
        }

/*
pattern = Pattern.compile(temp);
                matcher = pattern.matcher(badStuff);
                if (matcher.lookingAt()) {
                    matches.add(temp);
                    System.out.println("Match: " + temp);
                } else {

                    System.out.println("Searching: " + temp);
                }







k=(int) (10-0.3*ingredients.get(i).length());
            if (k<0) {k =0;} else if (k>5) {k=5;}
System.out.println(mismatch.searchString(badStuff,
                    ingredients.get(i), k));


        for (String word : ingredients) {
            k=(int)Math.exp(31/word.length())-1;
            System.out.println("Searching: "+word);
            if ( mismatch.searchString(badStuff,
                    word, k)[0] != -1) {
                matches.add(word);

            }

        }*/

        return modIngredients;

    }

    public static String binarySearch(String[] a, String x) { //Performs binary search
        int low = 0;
        int high = a.length - 1;
        int mid;

        while (low <= high) {
            mid = (low + high) / 2;

            if (a[mid].compareTo(x) < 0) {
                low = mid + 1;
            } else if (a[mid].compareTo(x) > 0) {
                high = mid - 1;
            } else {
                return x;
            }
        }
        return null;


    }














    /*char[] bob = {'B','E','S','T','I','L','L','E','R',' ','H','E','I',' ','D','U'};




    public String[] matchWords(Context ctx, ArrayList<String> ingredients) {
        System.out.println("In searchEngine");

        int arraylength = ingredients.size();
        String[] matches = new String[arraylength];
        RenderScript rs = RenderScript.create(ctx);
        ScriptC_search search = new ScriptC_search(rs); //The following is basically just what's needed to communicate with renderscript, though I'm not sure if both finish and destroy is necessary. Anyway, didn't seem to do any harm


        //Type ingredientType = Type.createX(rs, ingredients, arraylength);

        Allocation input = null;
        input.copyFrom(ingredients);
        Allocation output = input;
        search.set_listOfBadStuff(bob);

        search.forEach_stringSearch(input, output);
        output.copyTo(matches);
        rs.finish();
        rs.destroy();
        System.out.println("returns outMap"+matches);
        return matches;

    }*/


}
