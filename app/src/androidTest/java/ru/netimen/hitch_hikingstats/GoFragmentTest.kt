package ru.netimen.hitch_hikingstats

import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import android.support.test.espresso.Espresso.*
import android.support.test.espresso.matcher.ViewMatchers.*
import android.support.test.espresso.assertion.ViewAssertions.*

/**
 * Copyright (c) 2016 Bookmate.
 * All Rights Reserved.
 *
 *
 * Author: Dmitry Gordeev @dreamindustries.co>
 * Date:   21.03.16
 */
@RunWith(AndroidJUnit4::class)
class GoFragmentTest {
    @get:Rule
    val activityRule = ActivityTestRule(MainActivity::class.java)

    @Test
    fun testStateDisplayed() {
        onView(withText("Hello world!")).check(matches(isDisplayed()!!));
    }
}