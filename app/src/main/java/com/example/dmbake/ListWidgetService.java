package com.example.dmbake;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

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

    private List<String> mWidgetItems = new ArrayList<String>();

    public ListRemoteViewsFactory(Context applicationContext) {
        mContext = applicationContext;
    }

    @Override
    public void onCreate() {

    }
    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        //get the ingredients from shared preferences
        SharedPreferences sharedPreferences = mContext.getSharedPreferences("RECIPE_PREF", Context.MODE_PRIVATE);
        int numOfIngredients = sharedPreferences.getInt("Ingredients_Size", 0);
        ArrayList<String> Ingredients = new ArrayList<>();

        for(int i=0; i<numOfIngredients; i++) {
            String ingredient = "";
            ingredient = sharedPreferences.getString("Ingredient_name_" + i, "");
            Ingredients.add(ingredient);
        }
        mWidgetItems = Ingredients;
    }

    @Override
    public void onDestroy() {
        mWidgetItems.clear();
    }

    @Override
    public int getCount() {
        return mWidgetItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
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
