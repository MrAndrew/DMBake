package com.example.dmbake.ui;

import android.annotation.SuppressLint;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.dmbake.R;
import com.example.dmbake.RecipeWidgetProvider;
import com.example.dmbake.adapters.RecipeListAdapter;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.utils.JsonParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import butterknife.ButterKnife;

public class RecipeListActivity extends AppCompatActivity implements View.OnClickListener {

    GridView recipesGridView;
    boolean isTab;
    ArrayList<RecipeParcelable> recipeParcelables;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recipe_list);
        ButterKnife.bind(this);


        if(findViewById(R.id.recipe_list_gv_tab) != null) {
            isTab = true;
        } else {
            isTab = false;
        }

        if (savedInstanceState == null) {
            String title = getResources().getString(R.string.app_name);
            setTitle(title);
            LoadRecipes();
        }

    }

    private void LoadRecipes() {
        new LoadRecipesQueryTask().execute();
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //restores saved instances between lifecyle events
        outState.putParcelableArrayList("RECIPE_LIST", recipeParcelables);
        outState.putBoolean("TAB_VIEW", isTab);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipeParcelables = savedInstanceState.getParcelableArrayList("RECIPE_LIST");
        isTab = savedInstanceState.getBoolean("TAB_VIEW");
        loadRecipesView(isTab);
    }

    private void loadRecipesView(boolean isTabView) {
        if(isTabView) {
            recipesGridView = findViewById(R.id.recipe_list_gv_tab);
        } else {
            recipesGridView = findViewById(R.id.recipe_list_gv);
        }
        RecipeListAdapter adapter = new RecipeListAdapter(getApplicationContext(), recipeParcelables);
        recipesGridView.setAdapter(adapter);
        recipesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                Toast.makeText(RecipeListActivity.this, "" + recipeParcelables.get(position).getRecipeName(),
                        Toast.LENGTH_SHORT).show();
                //save recipe
                saveRecipe(position);
                //start recipe details intent
                Intent startRecipeDetailsIntent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
                startRecipeDetailsIntent.putExtra("recipe", recipeParcelables.get(position));
                startActivity(startRecipeDetailsIntent);
            }
        });
    }

    public void saveRecipe(int position) {
        SharedPreferences.Editor editor = getSharedPreferences("RECIPE_PREF", MODE_PRIVATE).edit();
        //NOTE will work with same data file, if it updates without user reopening and selecting a different
        //new recipe, then widget might not have up to date info in preferences, in which case it will load
        //default no grid view (unless data was only appended to JSON and not changed completely)
        editor.putInt("Recipe_ID", recipeParcelables.get(position).getRecipeId());
        editor.putString("Recipe_Name", recipeParcelables.get(position).getRecipeName());
        //This is a little complicated, but working way to save and load the widget with the last clicked recipe the
        //user accessed within the app. This allows us not to have to create a DB with provider which would demand
        //3-4 more coding files, yet still shows the user the recipe and ingredients needed for it. Currently it
        //will only show ingredient names, but can (in theory) easily add the measure type and amount if desired later.
        ArrayList<IngredientsParcelable> ingredientsParcelables = recipeParcelables.get(position).getIngredients();
        editor.putInt("Ingredients_Size", ingredientsParcelables.size());
        for(int i=0; i < ingredientsParcelables.size(); i++) {
            if(!ingredientsParcelables.get(i).getIngredientName().isEmpty()){
                editor.putString("Ingredient_name_" + i, ingredientsParcelables.get(i).getIngredientName());
//                editor.putString("Ingredient_quantity_" + i, ingredientsParcelables.get(i).getQuantity().toString());
//                editor.putString("Ingredient_measure_" + i, ingredientsParcelables.get(i).getMeasure());
            }
        }
        editor.apply();
        //update app widget
        //Triggers Widget to update information found here: https://stackoverflow.com/questions/20273543/appwidgetmanager-getappwidgetids-in-activity-returns-an-empty-list/20372326
        //this is so the widget displays last recipe clicked on by the user
        Intent intent = new Intent(this, RecipeWidgetProvider.class);
        intent.setAction("android.appwidget.action.APPWIDGET_UPDATE");
        int ids[] = AppWidgetManager.getInstance(getApplication()).getAppWidgetIds(new ComponentName(getApplication(), RecipeWidgetProvider.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
//
        //trigger data update to handle the GV widgets and force a data refresh
        AppWidgetManager.getInstance(getApplication()).notifyAppWidgetViewDataChanged(ids, R.id.widget_list_view);

        sendBroadcast(intent);
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

    //inner class to async load movie list (suppress to get lint error to go away, cannot make it static)
    @SuppressLint("StaticFieldLeak")
    class LoadRecipesQueryTask extends AsyncTask<Void, Void, ArrayList<RecipeParcelable>> {

        @Override
        protected ArrayList<RecipeParcelable> doInBackground(Void... voids) {
            ArrayList<RecipeParcelable> recipes = null;
            String jsonString;

            recipes = JsonParseUtils.getRecipesFromUrl();

            return recipes;
        }


        @Override
        protected void onCancelled() {
            Toast.makeText(getApplicationContext(), "Async task cancelled!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(final ArrayList<RecipeParcelable> recipes) {
            if (recipes != null) {
                recipeParcelables = recipes;
                loadRecipesView(isTab);
            } else {
                Toast.makeText(getApplicationContext(), "Async task cancelled!",
                        Toast.LENGTH_SHORT).show();
            }
        }

    } //end recipe query Async task


}
