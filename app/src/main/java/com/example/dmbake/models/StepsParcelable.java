package com.example.dmbake.models;

import android.os.Parcel;
import android.os.Parcelable;

public class StepsParcelable extends ClassLoader implements Parcelable {

    private Integer stepId;
    private String shortDescription;
    private String description;
    private String videoUrl;
    private String thumbnailUrl;

    private StepsParcelable(Parcel in) {
        this.stepId = in.readInt();
        this.shortDescription = in.readString();
        this.description = in.readString();
        this.videoUrl = in.readString();
        this.thumbnailUrl = in.readString();
    }

    public StepsParcelable () {

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(stepId);
        dest.writeString(shortDescription);
        dest.writeString(description);
        dest.writeString(videoUrl);
        dest.writeString(thumbnailUrl);
    }

    public int describeContents() {
        return 0;
    }

    //getters
    public Integer getStepId() {
        return stepId;
    }
    public String getShortDescription() {
        return shortDescription;
    }
    public String getDescription() {
        return description;
    }
    public String getVideoUrl() {
        return videoUrl;
    }
    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    //setters
    public void setStepId(Integer stepId) {
        this.stepId = stepId;
    }
    public void setShortDescription(String shortDescription) {
        this.shortDescription = shortDescription;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public void setVideoUrl(String videoUrl) {
        this.videoUrl = videoUrl;
    }
    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

    public static final Creator<StepsParcelable> CREATOR
            = new Creator<StepsParcelable>() {

        public StepsParcelable createFromParcel(Parcel in) {
            return new StepsParcelable(in);
        }

        public StepsParcelable[] newArray(int size) {
            return new StepsParcelable[size];
        }
    };
}
