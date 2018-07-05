package com.example.dmbake;

import android.support.annotation.Nullable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.IdlingResource;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityInstrumentationTestCase2;
import android.util.DisplayMetrics;

import com.example.dmbake.ui.RecipeDetailsActivity;
import com.example.dmbake.ui.RecipeListActivity;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onData;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.core.AllOf.allOf;
import android.support.test.espresso.IdlingResource;

import java.util.concurrent.atomic.AtomicBoolean;


//reference for finding phone vs. tablet in tests here: https://stackoverflow.com/questions/26231752/android-espresso-tests-for-phone-and-tablet
@RunWith(AndroidJUnit4.class)
public class RecipeListToRecipeDetailsActivityTest {

    private RecipeListActivity mActivity;
    private boolean mIsScreenSw600dp;
    public static final String RECIPE_NAME = "Brownies";

    @Rule
    public ActivityTestRule<RecipeListActivity> mActivityTestRule
            = new ActivityTestRule<>(RecipeListActivity.class);

    @Before
    public void setUp() throws Exception {
        mActivity = mActivityTestRule.getActivity();
        mIsScreenSw600dp = isScreenSw600dp();
    }

    @Test
    public void clickGridViewItem_OpensRecipeDetailsActivity_PhoneTest() {
        // test for phone only
        if (!mIsScreenSw600dp) {
            //get reference to second grid item which should be brownies
            onData(anything()).inAdapterView(withId(R.id.recipe_list_gv)).atPosition(1).perform(click());

            //checks the activity opens the correct recipe
            onView(allOf(instanceOf(RecipeDetailsActivity.class), withParent(withId(R.id.recipe_step_container))))
                    .check(matches(isDisplayed()));

//            onView(withId(R.id.recipe_list_gv)).check(matches(isDisplayed()));
        }
    }

    @Test
    public void clickGridViewItem_OpensRecipeDetailsActivity_TabletTest() {
        // test for tablet only
        if (mIsScreenSw600dp) {
            //get reference to second grid item which should be brownies
            onData(anything()).inAdapterView(withId(R.id.recipe_list_gv_tab)).atPosition(1).perform(click());

            //checks the activity opens the correct recipe
//            onView(allOf(instanceOf(RecipeDetailsActivity.class), withParent(withId(R.layout.activity_recipe_details))))
//                    .check(matches(withText(RECIPE_NAME)));
            onView(allOf(instanceOf(RecipeDetailsActivity.class), withParent(withId(R.id.recipe_step_container2))))
                    .check(matches(isDisplayed()));

//            onView(withId(R.id.recipe_list_gv_tab)).check(matches(isDisplayed()));
        }
    }

    private boolean isScreenSw600dp() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        float widthDp = displayMetrics.widthPixels / displayMetrics.density;
        float heightDp = displayMetrics.heightPixels / displayMetrics.density;
        float screenSw = Math.min(widthDp, heightDp);
        return screenSw >= 600;
    }

}