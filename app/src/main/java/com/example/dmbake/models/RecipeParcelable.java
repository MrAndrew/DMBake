package com.example.dmbake.models;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class RecipeParcelable implements Parcelable {

    private Integer recipeId;
    private String recipeName;
    //Nests lists of other objects within the data for ease of finding appropriate data within the object
    private ArrayList<IngredientsParcelable> ingredients = new ArrayList<IngredientsParcelable>();
    private ArrayList<StepsParcelable> steps = new ArrayList<StepsParcelable>();
    private Integer servings;

    public RecipeParcelable(Parcel in) {
        this.recipeId = in.readInt();
        this.recipeName = in.readString();
        //readTypedList needed to nest lists of other parcelable objects within the parcelable object
        in.readTypedList(ingredients, IngredientsParcelable.CREATOR);
        in.readTypedList(steps, StepsParcelable.CREATOR);
        this.servings = in.readInt();
    }

    public RecipeParcelable () {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(recipeId);
        dest.writeString(recipeName);
        //writeTypedList for parcelable object list nesting
        dest.writeTypedList(ingredients);
        dest.writeTypedList(steps);
        dest.writeInt(servings);
    }

    public int describeContents() {
        return 0;
    }

    //getters
    public Integer getRecipeId() {
        return recipeId;
    }
    public String getRecipeName() {
        return recipeName;
    }
    public ArrayList<IngredientsParcelable> getIngredients() {
        return ingredients;
    }
    public ArrayList<StepsParcelable> getSteps() {
        return steps;
    }
    public Integer getServings() {
        return servings;
    }

    //setters
    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }
    public void setRecipeName(String recipeName) {
        this.recipeName = recipeName;
    }
    public void setIngredients(ArrayList<IngredientsParcelable> ingredients) {
        this.ingredients = ingredients;
    }
    public void setSteps(ArrayList<StepsParcelable> steps) {
        this.steps = steps;
    }
    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public static final Parcelable.Creator<RecipeParcelable> CREATOR
            = new Parcelable.Creator<RecipeParcelable>() {

        public RecipeParcelable createFromParcel(Parcel in) {
            return new RecipeParcelable(in);
        }

        public RecipeParcelable[] newArray(int size) {
            return new RecipeParcelable[size];
        }
    };

}