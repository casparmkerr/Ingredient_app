package com.threedoorstudio.ingredients.ingredient_app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class ResultsActivity extends Activity implements AsyncSearchText.AsyncResponse {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerViewAdapter m2Adapter;
    private RecyclerView.LayoutManager mLayoutManager;
    private String endocrineKeyword = "  --0AAA "; //Enables to check for "  --0AAA ", to see if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
    private String sensitiserKeyword = "  --0AAB ";
    private TextView waitingText;

    List<String> wordsList;

    //AsyncSearchText searchText = new AsyncSearchText();

    String[] ingredients;

    //ImageView mImg;
    //static String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);
        mRecyclerView = (RecyclerView) findViewById(R.id.rec_IngredientList);
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        // specify an adapter (see also next example)

        waitingText = findViewById(R.id.waitingText);

        Bundle extras = getIntent().getExtras(); //Gets image filepath
        String path = extras.getString("filePathString");

        //OcrEngine ocrEngine = new OcrEngine(getApplicationContext()); //Gives OcrEngine some context

        System.out.println("Path in Resultsactivity: " + path); //Again, those paths are sometimes evil, so I like checking

        OcrEngine.setContext(this);
        OcrEngine.setValues(path);

        new AsyncSearchText(this).execute();


        waitingText.setText("Please wait");





/*
        if (path != null) {


            /*
            if (matches.size() == 0 || matches == null) {
                textView.append("Yay, no matches!");
                /*for (String word : wordsList) {
                    textView.append(word + ", ");
                }
            } else {
                textView.append("Oh no, a match!");
                for (String word : matches) {
                    textView.append(word + ", ");
                }


            }
        }else {System.out.println("bmp is null");}



        //TextView textView = (TextView) findViewById(R.id.textView1);
        //textView.setText(null);

        //RelativeLayout rl = (RelativeLayout)findViewById(R.id.resActivity);
/*
        if (modIngredients.equals(wordsList)) {

            rl.setBackgroundColor(Color.GREEN);
        } else {

            rl.setBackgroundColor(Color.RED);
        }*/

        //Arrays.sort(modIngredients);

        //modIngredients.removeAll(Collections.singleton(null));

        //String ingredientsList[] = pathToTextList(path);



    }

    void setPicureDisplay(){

        ImageView mImg = findViewById(R.id.imageView2); //Finds imageview to display image

        Bitmap bmp1 = OcrEngine.getBitmap(); //gets treated bitmap
        mImg.setImageBitmap(bmp1);
    }
/*
    String[] pathToTextList(String path) {
        OcrEngine.setValues(this, path);
        List<String> wordsList = OcrEngine.getWords(); //gets list of recognized words

        SearchEngine search = new SearchEngine();
        List<String> modIngredients = null;
        modIngredients = search.matchWords(wordsList);

        ingredients = modIngredients.toArray(new String[0]);
        Arrays.sort(ingredients);

        return ingredients;

    }
*/
    void createRecyclerView(String[] ingredients){
        mAdapter = new RecyclerViewAdapter(getApplicationContext(), ingredients);
        mRecyclerView.setAdapter(mAdapter); //Starts the "Recyclerview" listview.

    }

    @Override
    public void processFinish(String[] output) {
        waitingText.setText("");
        ingredients = output;
        setPicureDisplay();
        createRecyclerView(ingredients);

    }


  /*  public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {


            // each data item is just a string in this case
            public TextView textViewHeader;
            public TextView textViewSubHeader;
            public ConstraintLayout mConstraintLayout;

            public ViewHolder(View v) {
                super(v);
                textViewHeader = (TextView) itemView.findViewById(R.id.textViewHeader);
                textViewHeader = (TextView) itemView.findViewById(R.id.textViewSubheader);

            }
        }

        // Provide a suitable constructor (depends on the kind of dataset)
        public MyAdapter(String[] myDataset) {
            mDataset = myDataset;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                       int viewType) {
            // create a new view
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_item, parent, false);
            // set the view's size, margins, paddings and layout parameters



            LinearLayout listItemLayout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent,false);

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            //holder.mTextView.setText(mDataset[position]);

            List<String> tempList = Arrays.asList(mDataset[position].split("§_§"));
            System.out.println("tempList: "+tempList+ ", Length: "+tempList.size());

            if (mDataset[position].contains(endocrineKeyword)) {
                //holder.mConstraintLayout.setBackgroundColor(Color.RED);

                holder.textViewHeader.setText(tempList.get(0).replaceAll(endocrineKeyword, ""));
                holder.textViewSubHeader.setText(tempList.get(1));

            } else if (mDataset[position].contains(sensitiserKeyword)) {
                //holder.mConstraintLayout.setBackgroundColor(Color.YELLOW);


                holder.textViewHeader.setText(tempList.get(0).replaceAll(sensitiserKeyword, ""));
                holder.textViewSubHeader.setText(tempList.get(1));


            }
            else {
                //holder.mConstraintLayout.setBackgroundColor(Color.WHITE);
                holder.textViewHeader.setText(tempList.get(0));
                holder.textViewSubHeader.setText(tempList.get(1));
            }



        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.length;
        }
    }




    /*}
    public static void setValue(String filePath) {
        path = filePath;


    }*/



}


