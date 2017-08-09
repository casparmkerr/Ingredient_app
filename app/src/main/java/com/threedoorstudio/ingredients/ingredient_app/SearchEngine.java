package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Bitmap;
import android.renderscript.*;
import java.lang.Math;
import java.util.Arrays;

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

    String endocrineDisruptors[] = {"3benzylidenecamphor", "4hydroxybenzoicacid", "4methylbenzylidenecamphor", "acetylhexamethyltetralin", "benzophenone", "benzophenone1",
            "benzophenone2", "benzophenone3", "bha", "butylhydroxyanisole","bht","butylatedhydroxytoluene","boricacid","butylparaben","cyclopentasiloxane","cyclomethicone",
            "cyclotetrasiloxane","diethylphthalate","dep","dihydroxybiphenyl","deltamethrin","ethylhexylmethoxycinnamate","ethylparaben","hydroxycinnamicacid","hexamethylindanopyran",
            "methylparaben","nitrophenol","octoxynol","propylparaben","resorcinol","resmethrin","styrene","tbutylmethylether","mtbe","triclosan","triphenylphosphate"}; //List of endocrine disruptors, formatted to be easier to detect (no signs, caps or spaces)

    String sensitisers[] = {"Alpha-isomenthyl ionone", "Amylcinnamyl alcohol", "Anise Alcohol", "Benzyl Alcohol", "Benzyl benzoate", "Benzyl cinnamate", "Benzyl salicylate", "Benzylideneheptanal", "Butylphenyl methylpropional",
    "Cinnamal", "Cinnamyl alcohol", "Citral", "Citronnellol", "Coumarin", "Eugenol", "Evernia Furfuracea", "Evernia Prunastri", "Farnesol", "Geraniol", "Hexyl cinnamal", "Hydroksycitronellal", "Hydroxyisohexyl 3- Cyclohexene Carboxaldehyde",
    "Isoeugenol", "Limonene", "Linalool", "Methyl 2-octynoate"};

    int sensitisersLength = sensitisers.length;

    int endocrineLength = endocrineDisruptors.length;

    ArrayList<String> sensitisersLowerArray = new ArrayList<>();
    String sensitisersLower[] = new String[sensitisersLength];

    int index;

    String enocrineMatch = "  --0AAA ";
    String sensitiserMatch = "  --0AAB ";



    //int k;

    Boolean cont;

    diff_match_patch matchObject = new diff_match_patch();



    ArrayList<String> matches = new ArrayList<String>();
    public ArrayList<String> matchWords(List<String> ingredients) {



        System.out.println("In SearchEngine");
        System.out.println("Unsorted: "+Arrays.toString(endocrineDisruptors));
        Arrays.sort(endocrineDisruptors);
        System.out.println("Sorted: "+Arrays.toString(endocrineDisruptors));
        int ingredientsListSize = ingredients.size();
        //ArrayList<String> modIngredients = (ArrayList<String>) ingredients; //Possible other way to do this
        ArrayList<String> modIngredients = new ArrayList<>();

        for(int i = 0; i < sensitisersLength; i++)
            sensitisersLower[i] = (sensitisers[i].toLowerCase().replaceAll("[^a-zA-Z]", ""));



        Arrays.sort(sensitisersLower);


        for(int i = 0; i < ingredientsListSize; i++) { //Iterating through every scanned ingredient
            String temp = ingredients.get(i).replaceAll("[^a-zA-Z ]", "").toLowerCase().trim(); //Strips away stuff for flexibility in writing - doesn't seem to really work well enough though
            //if (temp.length()<3){continue;}
            cont = false;


            index = Arrays.binarySearch(endocrineDisruptors,temp); //First looking for direct matches
            System.out.println("Checking: "+temp+" Index: "+ index);
            //String match = binarySearch(endocrineDisruptors, temp);

            if (index >= 0) { //Direct match
                modIngredients.add(enocrineMatch + ingredients.get(i)); //Adds String endocrineMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
                System.out.println("Matched endocrine disruptor: " + temp);
                cont = true;
            } else { //No direct match, proceeds to looking for badly detected matches
                for (int j = 0; j< endocrineLength; j++){

                    float delete = 0;
                    float insert = 0;
                    float equal = 0;
                    float ratio = 0;
                    LinkedList diff = matchObject.diff_main(temp,endocrineDisruptors[j]); //Finds difference between strings
                    for (int k = 0; k < diff.size();k++){
                        if (diff.get(k).toString().contains("DELETE")) {
                            delete += (diff.get(k).toString().length() -15);//Returned String contains 15 chars too many
                        } else if (diff.get(k).toString().contains("INSERT")) {
                            insert += (diff.get(k).toString().length() -15);//Returned String contains 15 chars too many
                        } else if (diff.get(k).toString().contains("EQUAL")){
                            equal += (diff.get(k).toString().length() -14);//Returned String contains 14 chars too many
                        }
                    }
                    if (equal != 0) { //Avoid divide by zero exeption
                        ratio = (float) ((equal / (equal + delete*0.8)) + (equal / (equal + insert*1.2))) / 2; //calculates a ratio, should probably be refined
                        System.out.println("Ratio: "+ratio+ "   Delete: "+delete+"    Insert: "+insert+"    Equal: "+equal);
                    }
                    if (ratio > 0.8 && (equal-insert>0)) { //If the current word is "close enough", count it as a match. Note: this is too sensitive, but at the same time not sensitive enough. A smarter algorithm would be nice.
                        modIngredients.add(enocrineMatch + ingredients.get(i)); //Adds String endocrineMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
                        System.out.println("Mostly matched endocrine disruptor: "+temp+", "+endocrineDisruptors[j]);
                        cont = true;
                        break; //No point in evaluating the current ingredient further
                    }

                }
            }



            if ( cont) {continue;}
            index = Arrays.binarySearch(sensitisersLower,temp); //First looking for direct matches
            System.out.println("Checking: "+temp+" Index: "+ index);


            if (index >= 0) { //Direct match
                modIngredients.add(sensitiserMatch + ingredients.get(i)); //Adds String sensitiserMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
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
                        System.out.println("Ratio: "+ratio+ "   Delete: "+delete+"    Insert: "+insert+"    Equal: "+equal);
                    }
                    if (ratio > 0.8 && (equal-insert>0)) { //If the current word is "close enough", count it as a match. Note: this is too sensitive, but at the same time not sensitive enough. A smarter algorithm would be nice.
                        modIngredients.add(sensitiserMatch + ingredients.get(i)); //Adds String sensitiserMatch if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
                        System.out.println("Mostly matched sensitiser: "+temp+", "+sensitisersLower[j]);
                        cont = true;
                        break; //No point in evaluating the current ingredient further
                    }

                }
            }


            if (cont) { //Element is already added to list
                continue;
            }

            modIngredients.add(ingredients.get(i)); //if no match, add element as detected


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
