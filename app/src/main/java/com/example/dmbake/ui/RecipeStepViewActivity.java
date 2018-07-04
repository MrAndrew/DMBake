package com.example.dmbake.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;

import com.example.dmbake.R;
import com.example.dmbake.models.RecipeParcelable;

import butterknife.BindView;
public class RecipeStepViewActivity extends AppCompatActivity {

    @BindView(R.id.recipe_details_rv)
    RecyclerView recipeDetailsRv;

    private RecipeParcelable recipe;
    private boolean isStep;
    private int stepIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_step);

        // Only create new fragments when there is no previously saved state
        if (savedInstanceState == null) {
            //get data from intent that started the activity
            recipe = getIntent().getExtras().getParcelable("recipe");
            isStep = getIntent().getBooleanExtra("isStep", false);
            stepIndex = getIntent().getIntExtra("stepIndex", 0);
            //set screen title
            setTitle(recipe.getRecipeName());
            //load correct fragment view
            loadFragment();
        }
    }

    private void loadFragment() {
        if (isStep) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = StepViewFragment.newInstance(recipe, stepIndex);
            ft.replace(R.id.recipe_step_container, fragment);
            ft.commit();
        } else if (!isStep) {
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            Fragment fragment = IngredientsFragment.newInstance(recipe);
            ft.replace(R.id.recipe_step_container, fragment);
            ft.commit();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //restores saved instances between lifecyle events
        outState.putParcelable("RECIPE", recipe);
        outState.putBoolean("IS_STEP", isStep);
        outState.putInt("STEP_INDEX", stepIndex);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipe = savedInstanceState.getParcelable("RECIPE");
        isStep = savedInstanceState.getBoolean("IS_STEP");
        stepIndex = savedInstanceState.getInt("STEP_INDEX");
        if(savedInstanceState != null) {
            //reload fragments when app instance is restored
            loadFragment();
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

}
