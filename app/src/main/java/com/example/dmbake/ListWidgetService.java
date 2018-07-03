package com.example.dmbake;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.example.dmbake.ui.RecipeListActivity;

import java.util.ArrayList;
import java.util.List;

public class ListWidgetService extends RemoteViewsService {
    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new ListRemoteViewsFactory(this.getApplicationContext());
    }
}

class ListRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {

    private Context mContext;
    private ArrayList Ingredients = new ArrayList<>();
    private int appWidgetId;

    private static final int mCount = 10;
    private List<String> mWidgetItems = new ArrayList<String>();
    private int mAppWidgetId;

    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
//        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
//                AppWidgetManager.INVALID_APPWIDGET_ID);
//        Ingredients = intent.getExtras().getStringArrayList("Ingredients");
        Log.d("ListRemoteViewsFactory", "Ingredients: " + Ingredients);
    }

    @Override
    public void onCreate() {
        Log.d("ListWidgetService", "onCreate() called");
//        for(int i=0; i<Ingredients.size(); i++) {
//            mWidgetItems.add(Ingredients.get(i).toString());
//        }
    }
    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        //get the ingredients from shared preferences
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("RECIPE_PREF", Context.MODE_PRIVATE);
//        mRecipeName = sharedPreferences.getString("Recipe_Name", "recipe name default");
        int numOfIngredients = sharedPreferences.getInt("Ingredients_Size", 0);
        ArrayList<String> Ingredients = new ArrayList<>();

        for(int i=0; i<numOfIngredients; i++) {
            String ingredient = "ingred" + i;
            ingredient = sharedPreferences.getString("Ingredient_name_" + i, "");
            Ingredients.add(ingredient);
        }

        mWidgetItems = Ingredients;
        Log.d("ListWidgetService", "IngredientsList: " + Ingredients);
    }

    @Override
    public void onDestroy() {
        Log.d("ListWidgetService", "onDestroy() called");
        mWidgetItems.clear();
    }

    @Override
    public int getCount() {
        Log.d("ListWidgetService", "getCount() called");
        return mWidgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        Log.d("ListWidgetService", "getViewAt() called");
        //get name of ingredient at the position
//        String ingredient = (String) Ingredients.get(position).toString();
        //set remote views
        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_view_item);
        //update the views
        views.setTextViewText(R.id.widget_list_view_item_tv, mWidgetItems.get(position));

        //set on click pending intent so app opens no matter where the widget is clicked
        Bundle extras = new Bundle();
        extras.putInt(RecipeWidgetProvider.EXTRA_ITEM, position);
        Intent fillInIntent = new Intent();
        views.setOnClickFillInIntent(R.id.widget_list_view_item_tv, fillInIntent);

        return views;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1; // Treat all items in the GridView the same
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

}
