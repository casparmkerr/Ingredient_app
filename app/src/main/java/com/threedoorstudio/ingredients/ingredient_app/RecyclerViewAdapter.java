package com.threedoorstudio.ingredients.ingredient_app;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.vision.text.Line;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Caspar on 28/8/17.
 */

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private static String endocrineKeyword = "  --0AAA "; //Enables to check for "  --0AAA ", to see if it's a match. Makes sure the matched ingredients are easy to identify and end ut first when sorted later.
    private static String sensitiserKeyword = "  --0AAB ";

    Context context;

    private String[] values;
    static String[] mTextList;


    View view;
    ViewHolder viewHolder;

    public RecyclerViewAdapter(Context context, String[] values) {
        this.context = context;
        this.values = values;
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.ViewHolder holder, int position) {

        holder.bindText(values[position]);

    }

    @Override
    public int getItemCount() {
        return values.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView textView1;
        TextView textView2;
        CardView layout;

        public ViewHolder(View itemView) {
            super(itemView);

            textView1 = (TextView) itemView.findViewById(R.id.textViewHeader);
            textView2 = (TextView) itemView.findViewById(R.id.textViewSubheader);
            layout = (CardView) itemView.findViewById(R.id.card_view);

        }

        public void bindText(String text) {
            List<String> tempList = Arrays.asList(text.split("§_§"));
            System.out.println("tempList: "+tempList+ ", Length: "+tempList.size());
            if (tempList.get(0).contains(endocrineKeyword)) {
                layout.setBackgroundColor(Color.RED);
                textView1.setText(tempList.get(0).replaceAll(endocrineKeyword, ""));
                textView2.setText(tempList.get(1));
            } else if (tempList.get(0).contains(sensitiserKeyword)) {
                layout.setBackgroundColor(Color.YELLOW);
                textView1.setText(tempList.get(0).replaceAll(sensitiserKeyword, ""));
                textView2.setText(tempList.get(1));
            } else {
                layout.setBackgroundColor(Color.WHITE);
                textView1.setText(tempList.get(0));
                textView2.setText(tempList.get(1));
            }

        }
    }


}
