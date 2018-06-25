package com.example.dmbake.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.example.dmbake.R;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.StepsParcelable;
import com.example.dmbake.ui.RecipeDetailsActivity;
import com.example.dmbake.ui.RecipeDetailsFragment;

import java.util.ArrayList;

public class RecipeDetailsListAdapter extends RecyclerView.Adapter<RecipeDetailsListAdapter.RecipeStepHolder> {

    private static ArrayList<StepsParcelable> recipeSteps;
    private static ArrayList<IngredientsParcelable> recipeIngredients;

    private final int mNumItems;

    private RecipeDetailsFragment.RecyclerViewClickListener mListener;

    public RecipeDetailsListAdapter(ArrayList<IngredientsParcelable> inIngredients, ArrayList<StepsParcelable> inSteps,
                                    RecipeDetailsFragment.RecyclerViewClickListener inListener) {
        recipeIngredients = inIngredients;
        recipeSteps = inSteps;
        mListener = inListener;
        if(inIngredients.size() > 0 && inSteps.size() > 0) {
            //plus one to account for first item being the ingredients
            mNumItems = inSteps.size() + 1;
        } else {
            mNumItems = 0;
        }
    }

    @NonNull
    @Override
    public RecipeDetailsListAdapter.RecipeStepHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.detail_item;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutId, parent, false);

        return new RecipeStepHolder(view, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeDetailsListAdapter.RecipeStepHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return mNumItems;
    }


    public class RecipeStepHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {
        private RecipeDetailsFragment.RecyclerViewClickListener mListener;
        //initiate views
        final View detailItemView;
        final TextView detailTitleTv;

        RecipeStepHolder(View itemView, RecipeDetailsFragment.RecyclerViewClickListener listener) {
            super(itemView);
            mListener = listener;
            itemView.setOnClickListener(this);
            //finds and assigns views to variables
            detailTitleTv = itemView.findViewById(R.id.recipe_detail_title_tv);
            detailItemView = itemView.findViewById(R.id.recipe_detail_item_view);
        }

        void bind(final int index) {
            //set values of views
            if (index == 0) {
                detailTitleTv.setText(R.string.ingredients_title);
                detailItemView.setTag(recipeIngredients);
                detailItemView.setOnClickListener(this);
            } else {
                Integer tag = recipeSteps.get(index - 1).getStepId();
                // index - 1 b/c ingredients is taking the first position and recipeSteps is a separate array
                detailTitleTv.setText(recipeSteps.get(index - 1).getShortDescription());
                detailItemView.setTag(tag);
                detailItemView.setOnClickListener(this);
            }
        }

        @Override
        public void onClick(View v) {
            if (v.getTag() == recipeIngredients) {
                Integer stepIndex = -1;
                mListener.onListItemClick(false, stepIndex);
            } else {
                Integer stepIndex = (int) v.getTag();
                mListener.onListItemClick(true, stepIndex);
            }
        }

    }

}
