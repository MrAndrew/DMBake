package com.example.dmbake.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.dmbake.R;
import com.example.dmbake.models.RecipeParcelable;

import java.util.ArrayList;

public class RecipeListAdapter extends ArrayAdapter {

    private final LayoutInflater inflater;
    private ArrayList<String> recipeNames;

    @SuppressWarnings("unchecked")
    public RecipeListAdapter(Context context, ArrayList<RecipeParcelable> recipes) {
        super(context, R.layout.grid_item_recipe, recipes);

        if (recipes != null) {
            ArrayList<String> recipeNamesList = new ArrayList<String>();
            for (int i = 0; i < recipes.size(); i++) {
                String title = recipes.get(i).getRecipeName();
                recipeNamesList.add(title);
            }
            this.recipeNames = recipeNamesList;
        }
        inflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return recipeNames.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.grid_item_recipe, parent, false);
        }

        TextView recipeTv = convertView.findViewById(R.id.recipe_tv);

        if(TextUtils.isEmpty(recipeNames.get(position))) {
            recipeTv.setText(null);
        } else {
            recipeTv.setText(recipeNames.get(position));
        }

        return convertView;
    }

}
