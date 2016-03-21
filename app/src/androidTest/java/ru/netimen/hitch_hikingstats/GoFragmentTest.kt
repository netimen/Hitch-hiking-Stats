package ru.netimen.hitch_hikingstats

import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.assertion.ViewAssertions.matches
import android.support.test.espresso.matcher.ViewMatchers.isDisplayed
import android.support.test.espresso.matcher.ViewMatchers.withText
import android.support.test.espresso.action.ViewActions.*
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import ru.netimen.hitch_hikingstats.domain.GoState
import ru.netimen.hitch_hikingstats.test.RxTestRule
import java.util.concurrent.TimeUnit

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 *
 * Author: Dmitry Gordeev @dreamindustries.co>
 * Date:   21.03.16
 */

private inline fun <reified T: Fragment>getFragmentByClass(activity : AppCompatActivity) = activity.supportFragmentManager.fragments.find { it is T } as T

@RunWith(AndroidJUnit4::class)
class GoFragmentTest {
//    @get:Rule
//    val rxTest = RxTestRule()

    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    val fragment by lazy { getFragmentByClass<GoFragment>(activityRule.activity) }

//    @Test
//    fun testStateDisplayed() {
//        fragment.showState(GoState.Idle())
//        waitButton().check(matches(isDisplayed()));
//        rideButton().check(matches(isDisplayed()));
//        stopButton().check(matches(not(isDisplayed())));
//        onView(withText(containsString(R.string.idle))).check(matches(isDisplayed()))
//
//        fragment.showState(GoState.Waiting())
//        waitButton().check(matches(not(isDisplayed())));
//        rideButton().check(matches(isDisplayed()));
//        stopButton().check(matches(isDisplayed()));
//        onView(withText(containsString(R.string.waiting))).check(matches(isDisplayed()))
//
//        fragment.showState(GoState.Riding("Toyota", 3))
//        waitButton().check(matches(not(isDisplayed())));
//        rideButton().check(matches(isDisplayed()));
//        stopButton().check(matches(isDisplayed()));
//        onView(withText(containsString(R.string.riding))).check(matches(isDisplayed()))
//    }

    @Test
    fun testTitleUpdated() { // CUR make two test run
//        fragment.updateTitle(GoState.Waiting(TimeUnit.MILLISECONDS.toMinutes()))
//        onView(withText(containsString(minutes.toString()))).check(matches(isDisplayed()))
    }

    private fun containsString(id: Int) = containsString(getString(id))
    private fun getString(id: Int) = fragment.resources.getString(id)

    private fun waitButton() = onView(withText("Wait"))
    private fun rideButton() = onView(withText("Ride"))
    private fun stopButton() = onView(withText("Stop"))
}