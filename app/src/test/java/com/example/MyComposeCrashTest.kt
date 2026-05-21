package com.example

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.ui.MainViewModel
import com.example.ui.SocialGenieApp
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MyComposeCrashTest {

    @get:Rule
    val rule = createComposeRule()

    @Test
    fun testNavigationCrash() {
        val context = ApplicationProvider.getApplicationContext<android.app.Application>()
        val viewModel = MainViewModel(context)
        
        rule.setContent {
            SocialGenieApp(viewModel = viewModel)
        }

        rule.waitForIdle()
        
        // Try clicking tabs
        rule.onNodeWithTag("nav_dashboard").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("nav_create").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("nav_queue").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("nav_analytics").performClick()
        rule.waitForIdle()
        rule.onNodeWithTag("nav_templates").performClick()
        rule.waitForIdle()
    }
}
