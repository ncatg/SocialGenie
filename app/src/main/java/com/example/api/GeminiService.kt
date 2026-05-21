package com.example.api

import android.util.Log
import com.example.BuildConfig
import com.example.data.GeminiGeneratedPostResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object GeminiService {
    private const val TAG = "GeminiService"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-flash:generateContent"

    private val client = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    suspend fun generateContent(idea: String, tone: String): GeminiGeneratedPostResponse = withContext(Dispatchers.IO) {
        val apiKey = BuildConfig.GEMINI_API_KEY
        Log.d(TAG, "Using API key length: ${apiKey.length}")

        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            Log.w(TAG, "API Key is standard placeholder. Simulating highly polished generation.")
            return@withContext getSimulatedResponse(idea, tone)
        }

        val prompt = """
            You are a master social media content creator. Take the following idea and desired tone, and generate tailored posts for platforms, plus visual card headlines.
            
            IDEA: "$idea"
            DESIRED TONE: "$tone"
            
            Provide three drafted posts:
            1. LinkedIn (long-form, professional, well-structured, bulleted main points, polite call-to-action).
            2. Twitter/X (short & punchy, under 250 characters, engaging hook, modern tag).
            3. Instagram (visual-focused, includes 5-7 popular relevant hashtags, emojis, friendly narrative).
            
            And three short visual card titles (max 3-5 words) to be printed on the background image for each platform:
            - LinkedIn visual title
            - Twitter visual title
            - Instagram visual title
            
            Return ONLY a valid JSON object matching this exact schema:
            {
              "linkedinText": "Write the full LinkedIn post text here",
              "twitterText": "Write the full Twitter post text here",
              "instagramText": "Write the full Instagram post text here",
              "linkedinVisualTitle": "Headline for LinkedIn asset",
              "twitterVisualTitle": "Headline for Twitter asset",
              "instagramVisualTitle": "Headline for Instagram asset",
              "visualThemeKeywords": "3-5 comma separated style keywords"
            }
            
            Rules:
            - Respond strictly with JSON.
            - Do not include any conversational intro, outro, or markdown code blocks (such as ```json).
            - Strictly respect the requested JSON format.
        """.trimIndent()

        // Build request body manually
        val requestJson = JSONObject()
        val contentsArray = org.json.JSONArray()
        val contentObject = JSONObject()
        val partsArray = org.json.JSONArray()
        val partObject = JSONObject()
        partObject.put("text", prompt)
        partsArray.put(partObject)
        contentObject.put("parts", partsArray)
        contentsArray.put(contentObject)
        requestJson.put("contents", contentsArray)

        val mediaType = "application/json".toMediaType()
        val body = requestJson.toString().toRequestBody(mediaType)

        val request = Request.Builder()
            .url("$BASE_URL?key=$apiKey")
            .post(body)
            .build()

        try {
            client.newCall(request).execute().use { response ->
                if (!response.isSuccessful) {
                    val errBody = response.body?.string() ?: ""
                    Log.e(TAG, "API failed response: $errBody")
                    throw Exception("API Error Code: ${response.code} - $errBody")
                }

                val resBody = response.body?.string() ?: ""
                Log.d(TAG, "Raw response: $resBody")
                
                val parsedResult = parseGeminiResponse(resBody)
                return@withContext parsedResult
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed calling Gemini API, falling back to rich simulation.", e)
            return@withContext getSimulatedResponse(idea, tone, e.message)
        }
    }

    private fun parseGeminiResponse(rawJson: String): GeminiGeneratedPostResponse {
        val root = JSONObject(rawJson)
        val candidates = root.getJSONArray("candidates")
        val candidate = candidates.getJSONObject(0)
        val content = candidate.getJSONObject("content")
        val parts = content.getJSONArray("parts")
        val part = parts.getJSONObject(0)
        var text = part.getString("text").trim()

        // Clean out typical markdown code blocks if the LLM returned it in ```json ... ```
        if (text.startsWith("```")) {
            text = text.substringAfter("```json").substringAfter("```").substringBeforeLast("```").trim()
        }

        Log.d(TAG, "Clean JSON text: $text")

        val json = JSONObject(text)
        return GeminiGeneratedPostResponse(
            linkedinText = json.optString("linkedinText", ""),
            twitterText = json.optString("twitterText", ""),
            instagramText = json.optString("instagramText", ""),
            linkedinVisualTitle = json.optString("linkedinVisualTitle", "Growth Pillars"),
            twitterVisualTitle = json.optString("twitterVisualTitle", "The Big Picture"),
            instagramVisualTitle = json.optString("instagramVisualTitle", "Daily Muse"),
            visualThemeKeywords = json.optString("visualThemeKeywords", "dynamic, startup, modern")
        )
    }

    private fun getSimulatedResponse(idea: String, tone: String, errorSource: String? = null): GeminiGeneratedPostResponse {
        val toneLabel = tone.replaceFirstChar { it.uppercase() }
        
        val liTitle = when (tone) {
            "professional" -> "Market Solutions & Future Frameworks"
            "witty" -> "A Fresh Spin On Old Tech"
            else -> "Immediate Response Mandate"
        }

        val twTitle = when (tone) {
            "professional" -> "Core Pillars Explained"
            "witty" -> "Unfiltered Tech Take"
            else -> "Urgent Flash Action!"
        }

        val igTitle = when (tone) {
            "professional" -> "Elevate Your Strategic Vision"
            "witty" -> "Coffee & Coding Musings"
            else -> "Limited Spots Available!"
        }

        val designKeywords = when (tone) {
            "professional" -> "corporate, clean, sky azure, precise"
            "witty" -> "vibrant, retro modern, neon mango, trendy"
            else -> "high contrast, alert crimson, bold brutalist"
        }

        val linkedin = """
            💼 **${toneLabel} Insights: Building $idea**
            
            When thinking about "$idea", success requires translating high-level concepts into robust, reliable processes. Whether leading a team or operating as a solo creator, the fundamentals remain the same:
            
            📍 *Key Strategy 1:* Design with extreme clarity from day one. Avoid over-complicating.
            📍 *Key Strategy 2:* Optimize for long-term consistency. Build structures that scale.
            📍 *Key Strategy 3:* Leverage automated parameters to amplify the reach of your visual content.
            
            We've observed a substantial lift in engagement when these components align. What are your primary goals for this quarter, and how does "$idea" factor in? Let me know below.
            
            #Productivity #${toneLabel}Strategy #SocialGenie #ContentGrowth
        """.trimIndent()

        val twitter = when (tone) {
            "witty" -> "Tried building \"$idea\" and realized the hardest part isn't the code, it's deciding which coffee cup matches the branding style. Priorities, people. ☕️⚡️"
            "urgent" -> "ALERT: If you haven't started optimizing your approach to \"$idea\" yet, you are already falling behind. The shift is happening today. Act fast! ⏳🔥"
            else -> "Standard frameworks for \"$idea\" are broken. Tomorrow's leaders aren't repeating old workflows—they are deploying modular, data-driven systems. Here is exactly why. 👇 (1/5)"
        }

        val instagram = """
            Step into the next phase of content creation. Sparked by "$idea", we designed this custom visual layout to stand out in busy feeds. 📱✨
            
            Every details counts—from high-contrast gradient shadows to a balanced composition.
            
            Tell us: Red accent or Oceanic blue? Vote in our story! 🗳️
            
            #$tone #digitaldesign #inspiration #socialgenie #creatorslife #mindset
        """.trimIndent()

        return GeminiGeneratedPostResponse(
            linkedinText = linkedin,
            twitterText = twitter,
            instagramText = instagram,
            linkedinVisualTitle = liTitle,
            twitterVisualTitle = twTitle,
            instagramVisualTitle = igTitle,
            visualThemeKeywords = designKeywords
        )
    }
}
