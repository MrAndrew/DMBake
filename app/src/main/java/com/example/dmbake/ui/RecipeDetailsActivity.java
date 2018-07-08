package com.example.dmbake.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.Toast;

import com.example.dmbake.R;
import com.example.dmbake.models.RecipeParcelable;

import java.util.Objects;

import butterknife.BindView;

public class RecipeDetailsActivity extends AppCompatActivity implements RecipeDetailsFragment.RecyclerViewClickListener{

    private RecipeParcelable recipe;

    private static final String TAG = RecipeDetailsActivity.class.getSimpleName();

    // Track whether to display a two-pane or single-pane UI
    // A single-pane display refers to phone screens, and two-pane to larger tablet screens
    public boolean mTwoPane;
    public int mStepIndex;
    private Fragment mDetailsFragment;
    private Fragment mStepFragment;
    private Fragment mIngredientsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        //get data from intent that started the activity
        recipe = Objects.requireNonNull(getIntent().getExtras()).getParcelable("recipe");

        //set screen title
        setTitle(Objects.requireNonNull(recipe).getRecipeName());

        // Determine if you're creating a two-pane or single-pane display
        if (findViewById(R.id.recipe_details_container2) != null) {
            // This LinearLayout will only initially exist in the two-pane tablet case
            mTwoPane = true;
            // Only create new fragments when there is no previously saved state
            if (savedInstanceState == null) {
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mDetailsFragment = RecipeDetailsFragment.newInstance(recipe);
                ft.replace(R.id.recipe_details_container2, mDetailsFragment);
                ft.commit();
                //load ingredients by default in tab view
                onListItemClick(false, 0);
            } else {
                //Restore the fragment's state here

            }

        } else {
            mTwoPane = false;
                // Only create new fragments when there is no previously saved state
                if (savedInstanceState == null) {
                    FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                    mDetailsFragment = RecipeDetailsFragment.newInstance(recipe);
                    ft.replace(R.id.recipe_details_container, mDetailsFragment);
                    ft.commit();
                } else {
                    //Restore the fragment's state here

                }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //restores saved instances between lifecyle events
        outState.putParcelable("RECIPE", recipe);
        outState.putBoolean("IS_TWO_PANE", mTwoPane);
        outState.putInt("STEP_INDEX", mStepIndex);
        if (mTwoPane) {
            //save both fragments displayed
            getSupportFragmentManager().putFragment(outState, "RecipeDetailsFragment", mDetailsFragment);
            if (mStepIndex != -1) {
                getSupportFragmentManager().putFragment(outState, "RecipeStepViewFragment", mStepFragment);
            } else {
                getSupportFragmentManager().putFragment(outState, "RecipeIngredientsFragment", mIngredientsFragment);
            }
        } else {
            //only save details fragment on phones
            getSupportFragmentManager().putFragment(outState, "RecipeDetailsFragment", mDetailsFragment);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipe = savedInstanceState.getParcelable("RECIPE");
        mTwoPane = savedInstanceState.getBoolean("IS_TWO_PANE");
        mStepIndex = savedInstanceState.getInt("STEP_INDEX");
        if(savedInstanceState != null && !mTwoPane) {
//            Log.d(TAG, "restore instance state called on phone to restore details fragment");
            mDetailsFragment = getSupportFragmentManager().getFragment(savedInstanceState, "RecipeDetailsFragment");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.recipe_details_container, mDetailsFragment);
            ft.commit();
        } else if(savedInstanceState != null && mTwoPane) {
            mDetailsFragment = getSupportFragmentManager().getFragment(savedInstanceState, "RecipeDetailsFragment");
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.recipe_details_container2, mDetailsFragment);
//            ft.commit();
            //load last step or ingredients
            if (mStepIndex != -1) {
                mStepFragment = getSupportFragmentManager().getFragment(savedInstanceState, "RecipeStepViewFragment");
                ft.replace(R.id.recipe_step_container2, mStepFragment);
                ft.commit();
            } else {
                mIngredientsFragment = getSupportFragmentManager().getFragment(savedInstanceState, "RecipeIngredientsFragment");
                ft.replace(R.id.recipe_step_container2, mIngredientsFragment);
                ft.commit();
            }
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
                mStepIndex = stepIndex;
//                Toast.makeText(this, "Step: " + stepIndex,
//                        Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                //position - 1 b/c ingredients takes up position 0 within the list
                mStepFragment = StepViewFragment.newInstance(recipe, stepIndex, mTwoPane);
                ft.replace(R.id.recipe_step_container2, mStepFragment);
                ft.commit();
            } else {
                mStepIndex = -1;
//                Toast.makeText(this, "Ingredients!",
//                        Toast.LENGTH_SHORT).show();
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                mIngredientsFragment = IngredientsFragment.newInstance(recipe);
                ft.replace(R.id.recipe_step_container2, mIngredientsFragment);
                ft.commit();
            }
        } else {
            //if not tablet view, then will start a new intent which will then decide which fragment to display
            if (isStep) {
                Toast.makeText(this, "Step: " + (stepIndex),
                        Toast.LENGTH_SHORT).show();
                Intent startRecipeStepViewAcitivity = new Intent(this, RecipeStepViewActivity.class);
                startRecipeStepViewAcitivity.putExtra("recipe", recipe);
                startRecipeStepViewAcitivity.putExtra("isStep", true);
                startRecipeStepViewAcitivity.putExtra("stepIndex", stepIndex);
                startActivity(startRecipeStepViewAcitivity);
            } else {
                Toast.makeText(this, "Ingredients!",
                        Toast.LENGTH_SHORT).show();
                Intent startRecipeStepViewAcitivity = new Intent(this, RecipeStepViewActivity.class);
                startRecipeStepViewAcitivity.putExtra("recipe", recipe);
                startActivity(startRecipeStepViewAcitivity);
            }
        }
    } //end onListItemClick


}
