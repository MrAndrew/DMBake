package com.example.dmbake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import com.example.dmbake.R;
import com.example.dmbake.adapters.RecipeDetailsListAdapter;
import com.example.dmbake.adapters.RecipeListAdapter;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.models.StepsParcelable;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsFragment.RecyclerViewClickListener{

    @BindView(R.id.recipe_details_rv)
    RecyclerView recipeDetailsRv;

    private RecipeParcelable recipe;

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    public boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //get data from intent that started the activity
        recipe = getIntent().getExtras().getParcelable("recipe");

        //set screen title
        setTitle(recipe.getRecipeName());

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.recipe_step_container2) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;
            // Only create new fragments when there is no previously saved state
            if (savedInstanceState == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fragment = RecipeDetailsFragment.newInstance(recipe);
                ft.replace(R.id.recipe_details_container, fragment);
                ft.commit();
                //load ingredients by default in tab view
                onListItemClick(false, 0);
            }

        } else {
            mTwoPane = false;
                // Only create new fragments when there is no previously saved state
                if (savedInstanceState == null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    Fragment fragment = RecipeDetailsFragment.newInstance(recipe);
                    ft.replace(R.id.recipe_details_container, fragment);
                    ft.commit();
                }
        }
    }

    //TODO MAKE SAVED INSTANCE STATE WORK WITH FRAGMENTS
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //restores saved instances between lifecyle events
        outState.putParcelable("RECIPE", recipe);
        outState.putBoolean("IS_TWO_PANE", mTwoPane);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipe = savedInstanceState.getParcelable("RECIPE");
        mTwoPane = savedInstanceState.getBoolean("IS_TWO_PANE");
        if(savedInstanceState != null) {
          //TODO RESET FRAGMENT VIEWS HERE
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = RecipeDetailsFragment.newInstance(recipe);
            ft.replace(R.id.recipe_details_container, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStart() {
        super.onStart();
    }


    @Override
    public void onListItemClick(Boolean isStep, Integer stepIndex) {
        if(mTwoPane) {
            //if tab view is used, then calls the fragment directly
            if (isStep) {
                Toast.makeText(this, "Step: " + stepIndex,
                        Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //position - 1 b/c ingredients takes up position 0 within the list
                Fragment fragment = StepViewFragment.newInstance(recipe, stepIndex);
                ft.replace(R.id.recipe_step_container2, fragment);
                ft.commit();
            } else if (!isStep) {
                Toast.makeText(this, "Ingredients!",
                        Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                Fragment fragment = IngredientsFragment.newInstance(recipe);
                ft.replace(R.id.recipe_step_container2, fragment);
                ft.commit();
            }
        } else if(!mTwoPane) {
            //if not tablet view, then will start a new intent which will then decide which fragment to display
            if (isStep) {
                Toast.makeText(this, "Step: " + (stepIndex),
                        Toast.LENGTH_SHORT).show();
                Intent startRecipeStepViewAcitivity = new Intent(this, RecipeStepViewActivity.class);
                startRecipeStepViewAcitivity.putExtra("recipe", recipe);
                startRecipeStepViewAcitivity.putExtra("isStep", true);
                startRecipeStepViewAcitivity.putExtra("stepIndex", stepIndex);
                startActivity(startRecipeStepViewAcitivity);
            } else if (!isStep){
                Toast.makeText(this, "Ingredients!",
                        Toast.LENGTH_SHORT).show();
                Intent startRecipeStepViewAcitivity = new Intent(this, RecipeStepViewActivity.class);
                startRecipeStepViewAcitivity.putExtra("recipe", recipe);
                startActivity(startRecipeStepViewAcitivity);
            }
        }
    } //end onListItemClick


}
