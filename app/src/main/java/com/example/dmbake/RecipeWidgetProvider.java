package com.example.dmbake;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.ui.RecipeListActivity;
import com.example.dmbake.utils.JsonParseUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Implementation of App Widget functionality.
 */
public class RecipeWidgetProvider extends AppWidgetProvider {

    public static final String EXTRA_ITEM = "com.example.android.dmbake.EXTRA_ITEM";
    public static final String TOAST_ACTION = "com.example.android.dmbake.TOAST_ACTION";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, String RecipeName, ArrayList<String> Ingredients) {

        Bundle options = appWidgetManager.getAppWidgetOptions(appWidgetId);
        int height = options.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        RemoteViews remoteViews;
        if (height < 120) {
            remoteViews = getRecipeTitleRV(context, RecipeName);
        } else {
            remoteViews = getIngredientsListView(context, RecipeName, Ingredients, appWidgetId);
        }
        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, remoteViews);
    }

    private static RemoteViews getRecipeTitleRV(Context context, String RecipeName) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.recipe_widget_provider);
        views.setTextViewText(R.id.appwidget_title_tv, RecipeName);

        //Create an Intent to launch RecipeListActivity with clicked
        //*needs to be wrapped in a pending intent because RemoteViews cannot simple add onClick attribute like normal
        //this is because a widget is technically a separate application and PendingIntents wrap around normal Intents
        //and allow them to be accessed from other applications
        Intent intent = new Intent(context, RecipeListActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
        //sets actual click action on the title text view
        views.setOnClickPendingIntent(R.id.appwidget_title_tv, pendingIntent);

        return views;

    }

    private static RemoteViews getIngredientsListView(Context context, String RecipeName, ArrayList<String> Ingredients, int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list_view);
        //set title of recipe
        views.setTextViewText(R.id.widget_list_view_title_tv, RecipeName);
        //set list of ingredients with the ListWidgetService intent to act as the adapter for the LV
        Intent intent = new Intent(context, ListWidgetService.class);
//        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
//        intent.putExtra("Ingredients", Ingredients);
//        // When intents are compared, the extras are ignored, so we need to embed the extras
//        // into the data so that the extras will not be ignored.
//        intent.setData(Uri.parse(intent.toUri(Intent.URI_INTENT_SCHEME)));
        views.setRemoteAdapter(R.id.widget_list_view, intent);
        //Create an Intent to launch RecipeListActivity with clicked
        //*needs to be wrapped in a pending intent because RemoteViews cannot simple add onClick attribute like normal
        //this is because a widget is technically a separate application and PendingIntents wrap around normal Intents
        //and allow them to be accessed from other applications
        Intent appIntent = new Intent(context, RecipeListActivity.class);
        PendingIntent appPendingIntent = PendingIntent.getActivity(context, 0, appIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        //sets actual click action on the title text view
        views.setPendingIntentTemplate(R.id.widget_list_view, appPendingIntent);
        //setting an empty view in case of no data
//        views.setEmptyView(R.id.widget_list_view, R.id.empty_view);

        for(int i = 0; i<Ingredients.size(); i++) {
            Log.d("WidgetProvider", "Ingredient name: " + Ingredients.get(i));
        }

        return views;
    }

    //onUpdate is called whenever we create a new widget and on the update interval in the info.xml file
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        SharedPreferences sharedPreferences = context.getSharedPreferences("RECIPE_PREF", Context.MODE_PRIVATE);
        String recipeName = sharedPreferences.getString("Recipe_Name", "recipe name default");
        int numOfIngredients = sharedPreferences.getInt("Ingredients_Size", 0);
        ArrayList<String> Ingredients = new ArrayList<>();

        for(int i=0; i<numOfIngredients; i++) {
            String ingredient = "ingred" + i;
            ingredient = sharedPreferences.getString("Ingredient_name_" + i, "");
            Ingredients.add(ingredient);
        }
        Log.d("WidgetProvider", "Recipe Name: " + recipeName);
        Log.d("WidgetProvider", "numOfIngredients: " + numOfIngredients);
        Log.d("WidgetProvider", "IngredientsList: " + Ingredients);
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeName, Ingredients);
//            RemoteViews remoteViews = updateWidgetListView(context, appWidgetIds[i]);
        }
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager,
                                          int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(TOAST_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);
            Toast.makeText(context, "Touched view " + viewIndex, Toast.LENGTH_SHORT).show();
        }
        super.onReceive(context, intent);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context);
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

}

