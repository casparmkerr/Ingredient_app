package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.*;
import java.lang.Math;


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

    String badStuff = "SLIK BESTILLER du PIZZA. nei, kanskje, Joda, Beregn TRYKKPRISEN SELU";


    int k;

    ArrayList<String> matches = new ArrayList<String>();
    public ArrayList<String> matchWords(List<String> ingredients) {
        System.out.println("In SearchEngine");
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
        //k = 2;
        ArrayList<int[]> misma = new ArrayList<>();
        int listSize = ingredients.size();
        for(int i = 0; i < listSize; ++i) {
            System.out.println("In loop at least");
            k=(int) (10-0.3*ingredients.get(i).length());
            if (k<0) {k =0;} else if (k>6) {k=6;}
            System.out.println("Searching: " + ingredients.get(i));
            misma.add(mismatch.searchString(badStuff,
                    ingredients.get(i), k));
                //matches.add(ingredients.get(i));
                //System.out.println("Match");

            System.out.println(k);
            System.out.println(misma);

        }

/*

        for (String word : ingredients) {
            k=(int)Math.exp(31/word.length())-1;
            System.out.println("Searching: "+word);
            if ( mismatch.searchString(badStuff,
                    word, k)[0] != -1) {
                matches.add(word);

            }

        }*/

        return (ArrayList<String>) ingredients;

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
