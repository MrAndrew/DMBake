package com.example.dmbake.models;

import android.os.Parcel;
import android.os.Parcelable;


public class IngredientsParcelable extends ClassLoader implements Parcelable {

    private Double quantity;
    private String measure;
    private String ingredientName;

    private IngredientsParcelable(Parcel in) {
        this.quantity = in.readDouble();
        this.measure = in.readString();
        this.ingredientName = in.readString();
    }

    public IngredientsParcelable () {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(quantity);
        dest.writeString(measure);
        dest.writeString(ingredientName);
    }

    public int describeContents() {
        return 0;
    }

    //getters
    public Double getQuantity() {
        return quantity;
    }
    public String getMeasure() {
        return measure;
    }
    public String getIngredientName() {
        return ingredientName;
    }

    //setters
    public void setQuantity(Double quantity) {
        this.quantity = quantity;
    }
    public void setMeasure(String measure) {
        this.measure = measure;
    }
    public void setIngredientName(String ingredientName) {
        this.ingredientName = ingredientName;
    }

    public static final Creator<IngredientsParcelable> CREATOR
            = new Creator<IngredientsParcelable>() {

        public IngredientsParcelable createFromParcel(Parcel in) {
            return new IngredientsParcelable(in);
        }

        public IngredientsParcelable[] newArray(int size) {
            return new IngredientsParcelable[size];
        }
    };

}
