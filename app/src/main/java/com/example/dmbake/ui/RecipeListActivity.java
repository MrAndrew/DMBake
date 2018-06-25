package com.example.dmbake.ui;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import com.example.dmbake.R;
import com.example.dmbake.SettingsActivity;
import com.example.dmbake.adapters.RecipeListAdapter;
import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.utils.JsonParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class RecipeListActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.recipe_list_gv)
    GridView recipesGridView;

    ArrayList<RecipeParcelable> recipeParcelables;

    private boolean isTab;

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

    //inflate app settings option
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.app_settings_menu, menu);
        return true;
    }

    //starts setting screen activity if button is pressed
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //restores saved instances between lifecyle events
        outState.putParcelableArrayList("RECIPE_LIST", recipeParcelables);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recipeParcelables = savedInstanceState.getParcelableArrayList("RECIPE_LIST");
        if(savedInstanceState != null) {
            RecipeListAdapter adapter = new RecipeListAdapter(getApplicationContext(), recipeParcelables);
            recipesGridView.setAdapter(adapter);
            recipesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Toast.makeText(RecipeListActivity.this, "" + recipeParcelables.get(position).getRecipeName(),
                            Toast.LENGTH_SHORT).show();
                    Intent startRecipeDetailsIntent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
                    startRecipeDetailsIntent.putExtra("recipe", recipeParcelables.get(position));
                    startActivity(startRecipeDetailsIntent);
                }
            });
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

    //inner class to async load movie list (suppress to get lint error to go away, cannot make it static)
    @SuppressLint("StaticFieldLeak")
    class LoadRecipesQueryTask extends AsyncTask<Void, Void, ArrayList<RecipeParcelable>> {

        @Override
        protected ArrayList<RecipeParcelable> doInBackground(Void... voids) {
            ArrayList<RecipeParcelable> recipes = null;
            String jsonString = null;

            try {
                InputStream inputStream = getResources().getAssets().open("recipeJSON");
                BufferedReader streamReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
                StringBuilder stringBuilder = new StringBuilder();

                String inputStr;
                while ((inputStr = streamReader.readLine()) != null)
                    stringBuilder.append(inputStr);
                jsonString = stringBuilder.toString();

                recipes = JsonParseUtils.getRecipes(jsonString);
                Log.d("recipe doInBack", "" + recipes);
            } catch (IOException e) {
                e.printStackTrace();
            }

            return recipes;
        }


        @Override
        protected void onCancelled() {
            Toast.makeText(RecipeListActivity.this, "Async task cancelled!",
                    Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void onPostExecute(final ArrayList<RecipeParcelable> recipes) {
            Log.d("recipes", "" + recipes);
            if (recipes != null) {
                recipeParcelables = recipes;
                RecipeListAdapter adapter = new RecipeListAdapter(getApplicationContext(), recipes);
                recipesGridView.setAdapter(adapter);
                recipesGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {
                        Toast.makeText(RecipeListActivity.this, "" + recipes.get(position).getRecipeName(),
                                Toast.LENGTH_SHORT).show();
                        Intent startRecipeDetailsIntent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
                        startRecipeDetailsIntent.putExtra("recipe", recipes.get(position));
                        startActivity(startRecipeDetailsIntent);
                    }
                });
            } else {
                Toast.makeText(RecipeListActivity.this, "Async task cancelled!",
                        Toast.LENGTH_SHORT).show();
            }
        }

    } //end recipe query Async task


}
