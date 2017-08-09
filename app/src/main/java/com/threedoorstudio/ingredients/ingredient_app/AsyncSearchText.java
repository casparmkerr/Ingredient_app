package com.threedoorstudio.ingredients.ingredient_app;

import android.os.AsyncTask;

import java.util.Arrays;
import java.util.List;

public class AsyncSearchText extends AsyncTask<Void, Void, String[]> {

    public interface AsyncResponse {
        void processFinish(String[] output);
    }

    public AsyncResponse delegate = null;

    public AsyncSearchText(AsyncResponse delegate){
        this.delegate = delegate;
    }

    @Override
    protected String[] doInBackground(Void... voids) {
        OcrEngine.execute();
        List<String> wordsList = OcrEngine.getWords(); //gets list of recognized words

        SearchEngine search = new SearchEngine();
        List<String> modIngredients = null;
        modIngredients = search.matchWords(wordsList);

        String[] asyncIngredients = modIngredients.toArray(new String[0]);
        Arrays.sort(asyncIngredients);

        return asyncIngredients;
    }

    @Override
    protected void onPostExecute(String[] strings) {
        delegate.processFinish(strings);
    }
}
