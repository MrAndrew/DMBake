package com.example.dmbake.ui;

import android.content.res.Configuration;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
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
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class StepViewFragment extends Fragment implements ExoPlayer.EventListener{

    //initialize ExoPlayer stuff
    private SimpleExoPlayer mExoPlayer;
    private MediaSessionCompat mMediaSession;
    private PlaybackStateCompat.Builder mStateBuilder;
    private Long mPlayerPosition;
    private boolean mPlaystate;
    SimpleExoPlayerView exoPlayerView;
    ImageView mPlaceHolderIv;
    TextView stepTextView;

    private final static String TAG = StepViewFragment.class.getSimpleName();

    private static final String RECIPE_KEY = "recipe_key";

    public static StepViewFragment newInstance(RecipeParcelable recipe, int stepIndex, boolean isTab) {
        StepViewFragment fragment = new StepViewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(RECIPE_KEY, recipe);
        bundle.putInt("stepIndex", stepIndex);
        bundle.putBoolean("isTab", isTab);
        fragment.setArguments(bundle);

        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        RecipeParcelable recipe = getArguments().getParcelable(
                RECIPE_KEY);
        ArrayList<StepsParcelable> recipeSteps = recipe.getSteps();
        int stepIndex = getArguments().getInt("stepIndex");


        View returnView = inflater.inflate(R.layout.fragment_recipe_step, container, false);

        if (savedInstanceState == null) {
            exoPlayerView = returnView.findViewById(R.id.exoPlayerView);
            mPlaceHolderIv = returnView.findViewById(R.id.Placeholder_Iv);
            stepTextView = returnView.findViewById(R.id.recipe_step_description);
            exoPlayerView.setVisibility(View.VISIBLE);
            exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.default_player_pic));
            mPlayerPosition = null;
            //so video doesn't autoplay at first
            mPlaystate = false;
            loadStep(recipeSteps.get(stepIndex));
        } else {
            //Restore the fragment's state here
            exoPlayerView = returnView.findViewById(R.id.exoPlayerView);
            mPlaceHolderIv = returnView.findViewById(R.id.Placeholder_Iv);
            stepTextView = returnView.findViewById(R.id.recipe_step_description);
            exoPlayerView.setVisibility(View.VISIBLE);
            exoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource
                    (getResources(), R.drawable.default_player_pic));
            mPlayerPosition = savedInstanceState.getLong("PLAYER_POSITION");
            mPlaystate = savedInstanceState.getBoolean("PLAYSTATE");
            loadStep(recipeSteps.get(stepIndex));

        }

        return returnView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //save the fragment's state
//        outState.putParcelable("RECIPE", getArguments().getParcelable(RECIPE_KEY));
//        outState.putInt("STEP_INDEX", getArguments().getInt("stepIndex"));
        if (mExoPlayer != null) {
            outState.putLong("PLAYER_POSITION", mExoPlayer.getCurrentPosition());
            outState.putBoolean("PLAYSTATE", mExoPlayer.getPlayWhenReady());
        }
    }

    private void loadStep(StepsParcelable step) {
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
            //initialize MediaSession
            initializeMediaSession();
            initializePlayer(Uri.parse(stepVideo));
            exoPlayerView.setUseController(true);
        } else if(!stepThumbUrl.isEmpty()) {
            //First reviewer stated to treat this as a picture and an error if a video
            mPlaceHolderIv.setVisibility(View.VISIBLE);
            exoPlayerView.setVisibility(View.GONE);
            releasePlayer();
            Picasso.get()
                    .load(stepThumbUrl)
                    .placeholder(R.drawable.default_player_pic)
                    .error(R.drawable.default_player_pic)
                    .into(mPlaceHolderIv);
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
            //so it won't auto play when device is flipped, but also not unless user pushes the play button
            mExoPlayer.setPlayWhenReady(mPlaystate);
            if (mPlayerPosition != null) {
                mExoPlayer.seekTo(mPlayerPosition);
            }
            //to make video fullscreen on phone flip
            boolean isTab = getArguments().getBoolean("isTab");
            if (getActivity().getResources().getConfiguration().orientation ==
                    Configuration.ORIENTATION_LANDSCAPE && mExoPlayer != null && !isTab) {
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
        if(mMediaSession != null) {
            mMediaSession.release();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null) {
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mPlaystate = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        RecipeParcelable recipe = getArguments().getParcelable(
                RECIPE_KEY);
        ArrayList<StepsParcelable> recipeSteps = recipe.getSteps();
        int stepIndex = getArguments().getInt("stepIndex");
        loadStep(recipeSteps.get(stepIndex));
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
