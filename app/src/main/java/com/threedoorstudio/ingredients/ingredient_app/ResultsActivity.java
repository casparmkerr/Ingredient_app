package com.threedoorstudio.ingredients.ingredient_app;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.util.List;

public class ResultsActivity extends Activity {

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLayoutManager;

    List<String> wordsList;

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



        Bundle extras = getIntent().getExtras(); //Gets image filepath
        String path = extras.getString("filePathString");

        //OcrEngine ocrEngine = new OcrEngine(getApplicationContext()); //Gives OcrEngine some context

        System.out.println("Path in Resultsactivity: " + path); //Again, those paths are sometimes evil, so I like checking





        if (path != null) {


            final Bitmap bmp = BitmapFactory.decodeFile(path); //Creating bitmap
            if (bmp != null) {
                OcrEngine.setValues(this, bmp);
            } //Passes image to text recognition
            else {
                System.out.println("Shit");
            }



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


            }*/
        }else {System.out.println("bmp is null");}

        ImageView mImg = (ImageView) findViewById(R.id.imageView2); //Finds imageview to display image

        Bitmap bmp1 = OcrEngine.getBitmap(); //gets treated bitmap
        mImg.setImageBitmap(bmp1);
        List<String> wordsList = OcrEngine.getWords(); //gets list of recognized words
        //TextView textView = (TextView) findViewById(R.id.textView1);
        //textView.setText(null);
        SearchEngine search = new SearchEngine() {
        };
        List<String> modIngredients;
        modIngredients = search.matchWords(wordsList);
        RelativeLayout rl = (RelativeLayout)findViewById(R.id.resActivity);

        if (modIngredients.equals(wordsList)) { //Never equal, need to fix this.

            rl.setBackgroundColor(Color.GREEN);
        } else {

            rl.setBackgroundColor(Color.RED);
        }

        String[] ingredients = modIngredients.toArray(new String[0]);

        mAdapter = new MyAdapter(ingredients);
        mRecyclerView.setAdapter(mAdapter); //Starts the "Recyclerview" listview.


        }

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        private String[] mDataset;

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public TextView mTextView;
            public ViewHolder(TextView v) {
                super(v);
                mTextView = v;
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
            TextView v = (TextView) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.list_layout_textview, parent, false);
            // set the view's size, margins, paddings and layout parameters

            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            holder.mTextView.setText(mDataset[position]);

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
