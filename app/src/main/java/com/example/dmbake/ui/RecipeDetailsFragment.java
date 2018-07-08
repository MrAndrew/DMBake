package com.example.dmbake.ui;

import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.dmbake.R;
import com.example.dmbake.adapters.RecipeDetailsListAdapter;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.models.StepsParcelable;

import java.util.ArrayList;

//TREATED AS THE MASTER LIST FRAGMENT TO ALLOW FRAGMENT COMMUNICATION AND UPDATING OF DISPLAY
public class RecipeDetailsFragment extends Fragment {

    RecyclerView recipeDetailsRv;
    private Parcelable mRvListState;

    private static final String RECIPE_KEY = "recipe_key";

    private RecyclerViewClickListener mCallback;

    public interface RecyclerViewClickListener {
        //defines method call in parent activity and variables to pass in
        void onListItemClick(Boolean isStep, Integer stepIndex);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (RecyclerViewClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                + " must implement OnItemClickListener");
        }
    }

    //mandatory constructor for instantiating the fragment, most of the time is empty, but in this
    //case I need to pass in a data object
    public static RecipeDetailsFragment newInstance(RecipeParcelable recipe) {
        RecipeDetailsFragment fragment = new RecipeDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_KEY, recipe);
        fragment.setArguments(bundle);

        return fragment;
    }

    //inflates the fragment layout and sets any resources
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecipeParcelable recipe = getArguments().getParcelable(
                RECIPE_KEY);
        ArrayList<IngredientsParcelable> recipeIngredients = recipe.getIngredients();
        ArrayList<StepsParcelable> recipeSteps = recipe.getSteps();

        View returnView = inflater.inflate(R.layout.fragment_recipe_details_rv, container, false);

        if (savedInstanceState == null){
            recipeDetailsRv = returnView.findViewById(R.id.recipe_details_rv);
            //layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recipeDetailsRv.setLayoutManager(layoutManager);
            recipeDetailsRv.setHasFixedSize(true);
            //create and set rv adapter and listener
            RecipeDetailsListAdapter adapter = new RecipeDetailsListAdapter(recipeIngredients,
                    recipeSteps, mCallback);
            recipeDetailsRv.setAdapter(adapter);
        } else {
            recipeDetailsRv = returnView.findViewById(R.id.recipe_details_rv);
            //layout manager
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getContext());
            recipeDetailsRv.setLayoutManager(layoutManager);
            recipeDetailsRv.setHasFixedSize(true);
            //create and set rv adapter and listener
            RecipeDetailsListAdapter adapter = new RecipeDetailsListAdapter(recipeIngredients,
                    recipeSteps, mCallback);
            recipeDetailsRv.setAdapter(adapter);
            //should restore list state to one saved
            mRvListState = savedInstanceState.getParcelable("ListState");
            recipeDetailsRv.getLayoutManager().onRestoreInstanceState(mRvListState);
        }


        return returnView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save RV's list state
        outState.putParcelable("ListState", recipeDetailsRv.getLayoutManager().onSaveInstanceState());
    }


}