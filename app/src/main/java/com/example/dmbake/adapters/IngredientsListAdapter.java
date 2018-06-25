package com.example.dmbake.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.dmbake.R;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.StepsParcelable;

import java.util.ArrayList;

public class IngredientsListAdapter extends RecyclerView.Adapter<IngredientsListAdapter.IngredientsHolder> {

    private static ArrayList<IngredientsParcelable> recipeIngredients;
    private final int mNumItems;

    public IngredientsListAdapter(ArrayList<IngredientsParcelable> inIngredients) {
        recipeIngredients = inIngredients;

        if(inIngredients.size() > 0) {
            //plus one to account for first item being1 the ingredients
            mNumItems = inIngredients.size();
        } else {
            mNumItems = 0;
        }
    }

    @NonNull
    @Override
    public IngredientsListAdapter.IngredientsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.ingredient_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);

        return new IngredientsHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull IngredientsListAdapter.IngredientsHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumItems;
    }

    public class IngredientsHolder extends RecyclerView.ViewHolder {

        //initiate views
        final View ingredientItemView;
        final TextView ingredientNameView;
        final TextView ingredientQuantityView;
        final TextView ingredientMeausreView;

        IngredientsHolder(View itemView) {
            super(itemView);
            //finds and assigns variables to layout ids
            ingredientItemView = itemView.findViewById(R.id.ingredient_detail_item_view);
            ingredientNameView = itemView.findViewById(R.id.ingredient_name);
            ingredientQuantityView = itemView.findViewById(R.id.ingredient_quantity);
            ingredientMeausreView = itemView.findViewById(R.id.ingredient_measure);
        }

        void bind(final int index) {
            //set values of views here from data source
           ingredientItemView.setTag(index);
           ingredientNameView.setText(recipeIngredients.get(index).getIngredientName());
           ingredientQuantityView.setText(recipeIngredients.get(index).getQuantity().toString());
           ingredientMeausreView.setText(recipeIngredients.get(index).getMeasure());
        }

    }

}
