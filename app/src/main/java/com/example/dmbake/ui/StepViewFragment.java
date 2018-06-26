package com.example.dmbake.ui;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.dmbake.R;
import com.example.dmbake.models.IngredientsParcelable;
import com.example.dmbake.models.RecipeParcelable;
import com.example.dmbake.models.StepsParcelable;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import static android.content.Context.NOTIFICATION_SERVICE;

public class StepViewFragment extends Fragment implements ExoPlayer.EventListener{

    private static final String TAG = "StepViewFragment";

    //initialize ExoPlayer stuff
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private NotificationManager mNotificationManager;
    SimpleExoPlayerView exoPlayerView;
    ImageView mPlaceHolderIv;
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
        stepIndex = getArguments().getInt("stepIndex");

        View returnView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        if (savedInstanceState == null) {
            exoPlayerView = returnView.findViewById(R.id.exoPlayerView);
            mPlaceHolderIv = returnView.findViewById(R.id.Placeholder_Iv);
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
        stepTextView.setVisibility(View.VISIBLE);

        // Initialize the player.
        String stepVideo = step.getVideoUrl();
        String stepThumbUrl = step.getThumbnailUrl();
        //using .isEmpty b/c parcelable object returns an empty string this part frustrated me as I
        // thought it was an exoplayer control view issue b/c I had the default artwork and picasso
        // drawing as the same image file... grrr!!! lol
        if(!stepVideo.isEmpty()) {
            mPlaceHolderIv.setVisibility(View.GONE);
            exoPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(stepVideo));
            //initialize MediaSession
            initializeMediaSession();
            exoPlayerView.setUseController(true);
        } else if(!stepThumbUrl.isEmpty()) {
            mPlaceHolderIv.setVisibility(View.GONE);
            exoPlayerView.setVisibility(View.VISIBLE);
            initializePlayer(Uri.parse(stepThumbUrl));
            //initialize MediaSession
            initializeMediaSession();
            exoPlayerView.setUseController(true);
        } else {
            exoPlayerView.setVisibility(View.GONE);
            releasePlayer();
            mPlaceHolderIv.setVisibility(View.VISIBLE);
            Picasso.get().load(R.drawable.default_player_pic).into(mPlaceHolderIv);
        }

    }

    //class to initialize media session
    private void initializeMediaSession() {
        //create a mediasessioncompat
        mMediaSession = new MediaSessionCompat(getContext(), TAG);
        //enable callbacks from media buttons and trasport controls
        mMediaSession.setFlags(
                MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                        MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);
        //don't let mediabuttons restart the player when app not visible
        mMediaSession.setMediaButtonReceiver(null);
        //set an initial playbackstate with action_play so media buttons can start the player
        mStateBuilder = new PlaybackStateCompat.Builder()
                .setActions(
                        PlaybackStateCompat.ACTION_PLAY |
                                PlaybackStateCompat.ACTION_PAUSE |
                                PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS);
        mMediaSession.setPlaybackState(mStateBuilder.build());
        //MySessionCallback has method that handle callbacks from a media controller
        mMediaSession.setCallback(new mySessionCallback());
        //start the media session
        mMediaSession.setActive(true);
    }

    //media session callbacks so external clients can control the media
    private class mySessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
        }
        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
        }
        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
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
            // Add a listener to receive events from the player.
            mExoPlayer.addListener(this);
            // Prepare the MediaSource.
            String userAgent = Util.getUserAgent(getContext(), "DMBake");
            MediaSource mediaSource = new ExtractorMediaSource(mediaUri, new DefaultDataSourceFactory(
                    getContext(), userAgent), new DefaultExtractorsFactory(), null, null);
            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(true);
            //to make video fullscreen on phone flip
            if (getActivity().getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE && mExoPlayer != null) {
                ViewGroup.LayoutParams params = exoPlayerView.getLayoutParams();
                params.width = params.MATCH_PARENT;
                params.height = params.MATCH_PARENT;
                exoPlayerView.setLayoutParams(params);
                stepTextView.setVisibility(View.GONE);
//                ((AppCompatActivity) getActivity()).getSupportActionBar().hide();
                //seems to work on actual phone, but breaks emulator... :?
                hideSystemUI();
            } else if (getActivity().getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_PORTRAIT) {
                showSystemUI();
            }
        }
    }

    //found code for true fullscreen here: https://developer.android.com/training/system-ui/immersive
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

    // Shows the system bars by removing all the flags
    // except for the ones that make the content appear under the system bars.
    private void showSystemUI() {
        View decorView = getActivity().getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }

    /**
     * Release ExoPlayer.
     */
    private void releasePlayer() {
        if(mExoPlayer != null) {
            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        //only called in onDestroy to allow audio playback when app isn't in the foreground
        releasePlayer();
        if(mMediaSession != null) {
            mMediaSession.setActive(false);
        }
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

    //Methods required for ExoPlayer.EventListener
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if (mMediaSession != null) {
            if((playbackState == ExoPlayer.STATE_READY) && playWhenReady){
                mStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                        mExoPlayer.getCurrentPosition(), 1f);
            } else if((playbackState == ExoPlayer.STATE_READY)){
                mStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                        mExoPlayer.getCurrentPosition(), 1f);
            }
            mMediaSession.setPlaybackState(mStateBuilder.build());
        }
    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {

    }

    @Override
    public void onPositionDiscontinuity() {

    }


}