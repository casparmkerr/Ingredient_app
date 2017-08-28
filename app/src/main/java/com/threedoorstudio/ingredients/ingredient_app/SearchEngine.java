package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.*;
import java.lang.Math;
import java.util.Arrays;

import java.util.HashMap;
import java.util.LinkedList;
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

    String endocrineDisruptors[] = {"3-Benzylidene Camphor", "4-Hydroxybenzoic Acid", "4-Methylbenzylidene Camphor", "Acetyl Hexamethyl Tetralin", "Benzophenone", "Benzophenone-1",
            "Benzophenone-2", "Benzophenone-3", "BHA", "Butylhydroxyanisole","BHT","Butylated Hydroxytoluene","Boric Acid","Butylparaben","Cyclopentasiloxane","Cyclomethicone",
            "Cyclotetrasiloxane","Diethyl Phthalate","DEP","Dihydroxybiphenyl","Deltamethrin","Ethylhexyl Methoxycinnamate","Ethylparaben","Hydroxycinnamic Acid","Hexamethylindanopyran",
            "Methylparaben","Nitrophenol","Octoxynol","Propylparaben","Resorcinol","Resmethrin","Styrene","T-butyl methyl ether","MTBE","Triclosan","Triphenyl phosphate"}; //List of endocrine disruptors, formatted to be easier to detect (no signs, caps or spaces)

    String sensitisers[] = {"Alpha-isomenthyl ionone", "Amylcinnamyl alcohol", "Anise Alcohol", "Benzyl Alcohol", "Benzyl benzoate", "Benzyl cinnamate", "Benzyl salicylate", "Benzylideneheptanal", "Butylphenyl methylpropional",
    "Cinnamal", "Cinnamyl alcohol", "Citral", "Citronnellol", "Coumarin", "Eugenol", "Evernia Furfuracea", "Evernia Prunastri", "Farnesol", "Geraniol", "Hexyl cinnamal", "Hydroksycitronellal", "Hydroxyisohexyl 3- Cyclohexene Carboxaldehyde",
    "Isoeugenol", "Limonene", "Linalool", "Methyl 2-octynoate"};

    int sensitisersLength = sensitisers.length;

    int endocrineLength = endocrineDisruptors.length;

    ArrayList<String> sensitisersLowerArray = new ArrayList<>();
    String sensitisersLower[] = new String[sensitisersLength];
    String endocrineDisruptorsLower[] = new String[endocrineLength];

    int index;

    String enocrineMatch = "  --0AAA ";
    String sensitiserMatch = "  --0AAB ";



    //int k;

    Boolean cont;

    diff_match_patch matchObject = new diff_match_patch();



    ArrayList<String> matches = new ArrayList<String>();
    public List<String> matchWords(List<String> ingredients) {



        System.out.println("In SearchEngine");
        System.out.println("Unsorted: "+Arrays.toString(endocrineDisruptors));
        Arrays.sort(endocrineDisruptors);
        Arrays.sort(sensitisers);
        System.out.println("Sorted: "+Arrays.toString(endocrineDisruptors));
        int ingredientsListSize = ingredients.size();

        ArrayList<String> modIngredients = new ArrayList<>();

        HashMap<String,String[]> ingredientsMap = new HashMap<>();

        ArrayList<ArrayList<String>> listOLists = new ArrayList<ArrayList<String>>();


        for(int i = 0; i < sensitisersLength; i++)
            sensitisersLower[i] = (sensitisers[i].toLowerCase().replaceAll("[^a-zA-Z]", ""));

        for(int i = 0; i < endocrineLength; i++)
            endocrineDisruptorsLower[i] = (endocrineDisruptors[i].toLowerCase().replaceAll("[^a-zA-Z]", ""));


        Arrays.sort(sensitisersLower);


        for(int i = 0; i < ingredientsListSize; i++) { //Iterating through every scanned ingredient
            String temp = ingredients.get(i).replaceAll("[^a-zA-Z ]", "").toLowerCase().trim(); //Strips away stuff for flexibility in writing - doesn't seem to really work well enough though
            //if (temp.length()<3){continue;}
            ArrayList<String> listItem = new ArrayList<>();

            cont = false;


            index = Arrays.binarySearch(endocrineDisruptorsLower,temp); //First looking for direct matches
            System.out.println("Checking: "+temp+" Index: "+ index);
            //String match = binarySearch(endocrineDisruptors, temp);

            if (index >= 0) { //Direct match
                String[] stuff = {endocrineDisruptors[index],"Hormone Disruptor"};
                //ingredientsMap.put(ingredients.get(i),stuff);

                modIngredients.add(enocrineMatch + ingredients.get(i) + "§_§" + "Hormone Disruptor: " +endocrineDisruptors[index]); //Adds String endocrineMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.

                System.out.println("Matched endocrine disruptor: " + temp);
                cont = true;
            } else { //No direct match, proceeds to looking for badly detected matches
                for (int j = 0; j< endocrineLength; j++){

                    float delete = 0;
                    float insert = 0;
                    float equal = 0;
                    float ratio = 0;
                    LinkedList diff = matchObject.diff_main(temp,endocrineDisruptorsLower[j]); //Finds difference between strings
                    int k = 0;
                    for (k = 0; k < diff.size();k++){
                        if (diff.get(k).toString().contains("DELETE")) {
                            delete += (diff.get(k).toString().length() -15);//Returned String contains 15 chars too many
                        } else if (diff.get(k).toString().contains("INSERT")) {
                            insert += (diff.get(k).toString().length() -15);//Returned String contains 15 chars too many
                        } else if (diff.get(k).toString().contains("EQUAL")){
                            equal += (diff.get(k).toString().length() -14);//Returned String contains 14 chars too many
                        }
                    }
                    if (equal >2 && k < temp.length()/3) { //Avoid divide by zero exeption and filter out potential crap matches
                        ratio = (float) ((equal / (equal + delete*0.8)) + (equal / (equal + insert*1.2))) / 2; //calculates a ratio, should probably be refined
                        //System.out.println("Ratio: "+ratio+ "   Delete: "+delete+"    Insert: "+insert+"    Equal: "+equal);
                    }
                    if (ratio > 0.8 && (equal-insert>0) && (equal-delete>0)) { //If the current word is "close enough", count it as a match. Note: this is too sensitive, but at the same time not sensitive enough. A smarter algorithm would be nice.
                        String[] stuff = {endocrineDisruptors[j],"Hormone Disruptor"};
                        ingredientsMap.put(ingredients.get(i),stuff);

                        //modIngredients.add(enocrineMatch + ingredients.get(i)); //Adds String endocrineMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
                        modIngredients.add(enocrineMatch + ingredients.get(i) + "§_§" + "Hormone Disruptor: " +endocrineDisruptors[j]);
                        System.out.println("Mostly matched endocrine disruptor: "+temp+", "+endocrineDisruptorsLower[j]);
                        cont = true;
                        break; //No point in evaluating the current ingredient further
                    }

                }
            }



            if ( cont) {continue;}
            index = Arrays.binarySearch(sensitisersLower,temp); //First looking for direct matches
            System.out.println("Checking: "+temp+" Index: "+ index);


            if (index >= 0) { //Direct match
                String[] stuff = {sensitisers[index],"Sensitiser"};
                ingredientsMap.put(ingredients.get(i),stuff);

                modIngredients.add(sensitiserMatch + ingredients.get(i) + "§_§" + "Sensitiser: " +sensitisers[index]);
                //modIngredients.add(sensitiserMatch + ingredients.get(i)); //Adds String sensitiserMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
                System.out.println("Matched sensitiser: " + temp);
                cont = true;
            } else { //No direct match, proceeds to looking for badly detected matches
                for (int j = 0; j< sensitisersLength; j++){

                    float delete = 0;
                    float insert = 0;
                    float equal = 0;
                    float ratio = 0;
                    LinkedList diff = matchObject.diff_main(temp,sensitisersLower[j]); //Finds difference between strings
                    for (int k = 0; k < diff.size();k++){
                        if (diff.get(k).toString().contains("DELETE")) {
                            delete += (diff.get(k).toString().length() -15); //Returned String contains 15 chars too many
                        } else if (diff.get(k).toString().contains("INSERT")) {
                            insert += (diff.get(k).toString().length() -15);//Returned String contains 15 chars too many
                        } else if (diff.get(k).toString().contains("EQUAL")){
                            equal += (diff.get(k).toString().length() -14);//Returned String contains 14 chars too many
                        }
                    }
                    if (equal != 0) { //Avoid divide by zero exeption
                        ratio = (float) ((equal / (equal + delete*0.8)) + (equal / (equal + insert*1.2))) / 2; //calculates a ratio, should probably be refined
                        //System.out.println("Ratio: "+ratio+ "   Delete: "+delete+"    Insert: "+insert+"    Equal: "+equal);
                    }
                    if (ratio > 0.8 && (equal-insert>0)) { //If the current word is "close enough", count it as a match. Note: this is too sensitive, but at the same time not sensitive enough. A smarter algorithm would be nice.
                        String[] stuff = {endocrineDisruptors[j],"Sensitiser"};
                        ingredientsMap.put(ingredients.get(i),stuff);

                        //modIngredients.add(sensitiserMatch + ingredients.get(i)); //Adds String sensitiserMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
                        modIngredients.add(sensitiserMatch + ingredients.get(i) + "§_§" + "Sensitiser: " +sensitisers[j]);

                        System.out.println("Mostly matched sensitiser: "+temp+", "+sensitisersLower[j]);
                        cont = true;
                        break; //No point in evaluating the current ingredient further
                    }

                }
            }


            if (cont) { //Element is already added to list
                continue;
            }

            //String[] stuff = {"Seems all right, as far as I know.",""};
            //ingredientsMap.put(ingredients.get(i),stuff);
            modIngredients.add(ingredients.get(i) + "§_§" + "Seems all right to me");
            //modIngredients.add(ingredients.get(i)); //if no match, add element as detected


            //index = Arrays.binarySearch(endocrineDisruptors,temp);
 /*           System.out.println("Checking: "+temp+" Index: "+ index);
            //String match = binarySearch(endocrineDisruptors, temp);

            if (index <= -1) { //No match
                modIngredients.add(ingredients.get(i)); //if no match, add element as detected
            } else {
                //System.out.println("Whaaaaat?"); //Test to see if this ever happens
                modIngredients.add("  --0AAA "+ingredients.get(i)); //Adds "  --0AA" if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
                //matches.add(match);
            }*/

        }






        return modIngredients;

    }







}
