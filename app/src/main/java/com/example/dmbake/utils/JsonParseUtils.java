package com.example.dmbake.utils;

import android.util.Log;

import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.models.StepsParcelable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JsonParseUtils {

    private static final String TAG = JsonParseUtils.class.getSimpleName();

    //Keys for specific recipes
    private static final String KEY_RECIPE_ID = "id";
    private static final String KEY_RECIPE_NAME = "name";
    private static final String KEY_RECIPE_INGREDIENTS = "ingredients";
    private static final String KEY_RECIPE_STEPS = "steps";
    private static final String KEY_RECIPE_SERVINGS = "servings";

    //Keys for ingredients
    private static final String KEY_INGREDIENT_QUANTITY = "quantity";
    private static final String KEY_INGREDIENT_MEASURE = "measure";
    private static final String KEY_INGREDIENT = "ingredient";

    //Keys for steps
    private static final String KEY_STEP_ID = "id";
    private static final String KEY_STEP_DESCRIPTION_SHORT = "shortDescription";
    private static final String KEY_STEP_DESCRIPTION = "description";
    private static final String KEY_STEP_VIDEO_URL = "videoURL";
    private static final String KEY_STEP_THUMBNAIL_URL = "thumbnailURL";


    public JSONObject getRecipesJsonObj(String recipeJson) {
        JSONObject recipesObj = new JSONObject();
        try {
            recipesObj = new JSONObject(recipeJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return recipesObj;
    }

    public static ArrayList<RecipeParcelable> getRecipes(String recipeJson) {

        ArrayList<RecipeParcelable> recipeList = new ArrayList<RecipeParcelable>();

        try {
            JSONArray recipesJsonArray = new JSONArray(recipeJson);
            for(int r = 0; r < recipesJsonArray.length(); r++) {
                RecipeParcelable recipe = new RecipeParcelable();
                JSONObject recipeJsonObj = recipesJsonArray.getJSONObject(r);
                recipe.setRecipeId(recipeJsonObj.getInt(KEY_RECIPE_ID));
                recipe.setRecipeName(recipeJsonObj.getString(KEY_RECIPE_NAME));
                recipe.setServings(recipeJsonObj.getInt(KEY_RECIPE_SERVINGS));
                //set ingredients objects inside the recipe object
                JSONArray ingredientsJsonArray = recipeJsonObj.getJSONArray(KEY_RECIPE_INGREDIENTS);
                ArrayList<IngredientsParcelable> ingredients = new ArrayList<IngredientsParcelable>();
                for(int i = 0; i < ingredientsJsonArray.length(); i++) {
                    IngredientsParcelable ingredient = new IngredientsParcelable();
                    JSONObject ingredientJsonObj = ingredientsJsonArray.getJSONObject(i);
                    ingredient.setQuantity(ingredientJsonObj.getDouble(KEY_INGREDIENT_QUANTITY));
                    ingredient.setMeasure(ingredientJsonObj.getString(KEY_INGREDIENT_MEASURE));
                    ingredient.setIngredientName(ingredientJsonObj.getString(KEY_INGREDIENT));
                    ingredients.add(ingredient);
                }
                recipe.setIngredients(ingredients);
                //set steps inside the recipe object
                JSONArray stepsJsonArray = recipeJsonObj.getJSONArray(KEY_RECIPE_STEPS);
                ArrayList<StepsParcelable> steps = new ArrayList<StepsParcelable>();
                for(int s = 0; s < stepsJsonArray.length(); s++) {
                    StepsParcelable step = new StepsParcelable();
                    JSONObject stepJsonObj = stepsJsonArray.getJSONObject(s);
                    step.setStepId(stepJsonObj.getInt(KEY_STEP_ID));
                    step.setShortDescription(stepJsonObj.getString(KEY_STEP_DESCRIPTION_SHORT));
                    step.setDescription(stepJsonObj.getString(KEY_STEP_DESCRIPTION));
                    step.setVideoUrl(stepJsonObj.getString(KEY_STEP_VIDEO_URL));
                    step.setThumbnailUrl(stepJsonObj.getString(KEY_STEP_THUMBNAIL_URL));
                    steps.add(step);
                }
                recipe.setSteps(steps);
                recipeList.add(recipe);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "recipeList utils" + recipeList);
        return recipeList;
    }




} // end JsonParseUtils