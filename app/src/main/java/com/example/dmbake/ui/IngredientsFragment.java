package com.example.dmbake.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dmbake.R;
import com.example.dmbake.adapters.IngredientsListAdapter;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.RecipeParcelable;


import java.util.ArrayList;

public class IngredientsFragment extends Fragment {

    RecyclerView ingredientsRv;

    private static final String RECIPE_KEY = "recipe_key";
    private RecipeParcelable recipe;
    private ArrayList<IngredientsParcelable> recipeIngredients;

    public static IngredientsFragment newInstance(RecipeParcelable recipe) {
        IngredientsFragment fragment = new IngredientsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_KEY, recipe);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        recipe = (RecipeParcelable) getArguments().getParcelable(
                RECIPE_KEY);
        recipeIngredients = recipe.getIngredients();

        View returnView = inflater.inflate(R.layout.fragment_recipe_ingredients, container, false);

        if (savedInstanceState == null) {
            ingredientsRv = returnView.findViewById(R.id.recipe_ingredients_rv);
            //layout manager for ingredients RV
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(container.getContext());
            ingredientsRv.setLayoutManager(layoutManager);
            ingredientsRv.setHasFixedSize(true);
            //set adapter
            IngredientsListAdapter adapter = new IngredientsListAdapter(recipeIngredients);
            ingredientsRv.setAdapter(adapter);
        }
        return returnView;
    }

}
