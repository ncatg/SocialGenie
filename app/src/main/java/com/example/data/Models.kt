package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.JsonClass

@Entity(tableName = "post_drafts")
data class PostDraft(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val originalIdea: String,
    val tone: String,
    val linkedinText: String,
    val twitterText: String,
    val instagramText: String,
    val linkedinVisualTitle: String = "Brand Strategy",
    val twitterVisualTitle: String = "Let's Talk",
    val instagramVisualTitle: String = "Inspiration of the Day",
    val linkedinImageStyle: String = "Ocean Breeze", // gradient preset
    val twitterImageStyle: String = "Sunset Vivid",
    val instagramImageStyle: String = "Forest Calm",
    val visualKeywords: String = "modern, business, tech",
    val timestamp: Long = System.currentTimeMillis(),
    val scheduledTime: Long? = null, // Null if in draft, Long if scheduled
    val status: String = "DRAFT", // "DRAFT", "SCHEDULED", "POSTED"
    // Simulated analytics (filled over time after posting)
    val views: Int = 0,
    val likes: Int = 0,
    val shares: Int = 0,
    val comments: Int = 0
)

@Entity(tableName = "content_templates")
data class ContentTemplate(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val category: String,
    val tone: String,
    val promptTemplate: String,
    val iconName: String
)

@Entity(tableName = "collaboration_profiles")
data class CollaborationProfile(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val role: String,
    val avatarColor: Int, // Int representation of color index
    val initial: String
)

@JsonClass(generateAdapter = true)
data class GeminiGeneratedPostResponse(
    val linkedinText: String,
    val twitterText: String,
    val instagramText: String,
    val linkedinVisualTitle: String,
    val twitterVisualTitle: String,
    val instagramVisualTitle: String,
    val visualThemeKeywords: String
)
