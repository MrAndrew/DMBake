package com.example.dmbake.ui;

import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.dmbake.R;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.models.StepsParcelable;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StepViewFragment extends Fragment {

    //initialize ExoPlayer stuff
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    SimpleExoPlayerView exoPlayerView;
    ImageView mThumbView;
    TextView stepTextView;

    private boolean isStep;
    private int stepIndex;

    private static final String RECIPE_KEY = "recipe_key";
    private RecipeParcelable recipe;
    private ArrayList<IngredientsParcelable> recipeIngredients;
    private ArrayList<StepsParcelable> recipeSteps;

    public static StepViewFragment newInstance(RecipeParcelable recipe, int stepIndex) {
        StepViewFragment fragment = new StepViewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_KEY, recipe);
        bundle.putInt("stepIndex", stepIndex);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        recipe = (RecipeParcelable) getArguments().getParcelable(
                RECIPE_KEY);
        recipeIngredients = recipe.getIngredients();
        recipeSteps = recipe.getSteps();
        isStep = getArguments().getBoolean("isStep");
        stepIndex = getArguments().getInt("stepIndex");

        View returnView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        if (savedInstanceState == null && isStep) {
            exoPlayerView = returnView.findViewById(R.id.exoPlayerView);
            mThumbView = returnView.findViewById(R.id.ThumbView);
            stepTextView = returnView.findViewById(R.id.recipe_step_description);
            exoPlayerView.setVisibility(View.VISIBLE);
            exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.default_player_pic));
            loadStep(recipeSteps.get(stepIndex));
        }
        return returnView;
    }

    private void loadStep(StepsParcelable step) {
        //TODO CREATE AND SET STEP DISPLAY

        //load step instructions
        stepTextView.setText(step.getDescription());

        // Initialize the player.
        String stepVideo = step.getVideoUrl();
        String stepThumbUrl = step.getThumbnailUrl();
        if(stepVideo != null) {
            mThumbView.setVisibility(View.GONE);
            initializePlayer(Uri.parse(stepVideo));
        } else if(stepThumbUrl != null) {
            mThumbView.setVisibility(View.VISIBLE);
            Picasso.get().load("http://i.imgur.com/DvpvklR.png").into(mThumbView);
            exoPlayerView.setVisibility(View.INVISIBLE);
        }

    }

    /**
     * Initialize ExoPlayer.
     * @param mediaUri The URI of the sample to play.
     */
    //TODO CUSTOMIZE EXO PLAYER CONTROLS AND FIX ORIENTATION CHANGE ERROR/BUG
    private void initializePlayer(Uri mediaUri) {
        if (mExoPlayer == null) {
            // Create an instance of the ExoPlayer.
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(getContext(), trackSelector, loadControl);
            exoPlayerView.setPlayer(mExoPlayer);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "ClassicalMusicQuiz");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
        }
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        releasePlayer();
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

}
