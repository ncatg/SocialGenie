package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostDraftDao {
    @Query("SELECT * FROM post_drafts ORDER BY timestamp DESC")
    fun getAllDrafts(): Flow<List<PostDraft>>

    @Query("SELECT * FROM post_drafts WHERE id = :id")
    suspend fun getDraftById(id: Int): PostDraft?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDraft(draft: PostDraft): Long

    @Update
    suspend fun updateDraft(draft: PostDraft)

    @Query("DELETE FROM post_drafts WHERE id = :id")
    suspend fun deleteDraftById(id: Int)
}

@Dao
interface ContentTemplateDao {
    @Query("SELECT * FROM content_templates ORDER BY title ASC")
    fun getAllTemplates(): Flow<List<ContentTemplate>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplate(template: ContentTemplate)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTemplates(templates: List<ContentTemplate>)
}

@Dao
interface CollaborationProfileDao {
    @Query("SELECT * FROM collaboration_profiles ORDER BY name ASC")
    fun getAllProfiles(): Flow<List<CollaborationProfile>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfile(profile: CollaborationProfile)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProfiles(profiles: List<CollaborationProfile>)
}

@Database(
    entities = [PostDraft::class, ContentTemplate::class, CollaborationProfile::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun postDraftDao(): PostDraftDao
    abstract fun contentTemplateDao(): ContentTemplateDao
    abstract fun collaborationProfileDao(): CollaborationProfileDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "social_genie_db"
                )
                .addCallback(DatabaseCallback(scope))
                .build()
                INSTANCE = instance
                instance
            }
        }
    }

    private class DatabaseCallback(
        private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch(Dispatchers.IO) {
                    try {
                        populateInitialData(database)
                    } catch (e: Exception) {
                        android.util.Log.e("AppDatabase", "Error launching populateInitialData", e)
                    }
                }
            }
        }

        suspend fun populateInitialData(db: AppDatabase) {
            try {
            // Seed content templates for the quick library
            val templates = listOf(
                ContentTemplate(
                    title = "Product Growth Launch",
                    category = "Launch",
                    tone = "professional",
                    promptTemplate = "Build excitement for our new product launch! Major highlights: high efficiency, premium design, and modern features. Ask users what problem they want us to solve.",
                    iconName = "rocket"
                ),
                ContentTemplate(
                    title = "Milestone & Success Celebration",
                    category = "Milestone",
                    tone = "witty",
                    promptTemplate = "We just hit a massive milestone: serving 10,000 active creators! Celebrate with gratitude, shares some humor about midnight coding, and give a supportive nod to our early adopters.",
                    iconName = "stars"
                ),
                ContentTemplate(
                    title = "Industry Knowledge Drop",
                    category = "Educational",
                    tone = "professional",
                    promptTemplate = "Share 3 highly actionable trends in digital marketing / AI that every manager needs to know this year. Write in an insightful, thought-leadership style.",
                    iconName = "school"
                ),
                ContentTemplate(
                    title = "Flash Deal / Urgent Announcement",
                    category = "Promo",
                    tone = "urgent",
                    promptTemplate = "Announce a 48-hour flash sale of 40% off with coupon code SPEEDY. Focus on high urgency, limited slots, and immediate benefits.",
                    iconName = "flash_on"
                )
            )
            db.contentTemplateDao().insertTemplates(templates)

            // Seed Collaboration Members
            val profiles = listOf(
                CollaborationProfile(name = "Sarah Miller", role = "Content Strategist", avatarColor = 0, initial = "S"),
                CollaborationProfile(name = "Alex Rivera", role = "Brand Manager", avatarColor = 1, initial = "A"),
                CollaborationProfile(name = "Jessica Vance", role = "Marketing Lead", avatarColor = 2, initial = "J")
            )
            db.collaborationProfileDao().insertProfiles(profiles)

            // Seed some default Mock Scheduled & Posted posts to populate Analytics immediately
            val drafts = listOf(
                PostDraft(
                    originalIdea = "Sharing top 3 productivity tips",
                    tone = "witty",
                    linkedinText = "🚀 Let's talk about the standard 8-hour workday. Spoiler: Nobody is active for all of it! Here's how to focus on key deep work cycles.\n\n1. Protect your first 90 minutes. Do not open email yet.\n2. Limit meeting limits. Keep summaries direct.\n3. Stack workflows with SocialGenie.\n\nWhich productivity tips actually survived past Monday for you? Let's discuss!",
                    twitterText = "The secret to 10x output isn't working 10x more. It's defending your morning deep-work block with your life. Turn off Slack, close your inbox, and let the genie work. 🧞‍♂️",
                    instagramText = "Productivity tips that actually work! ⚡️ Stop wasting time on endless meetings and start focusing on output that matters. Swipe through to see how we protect our deep creative cycles.\n\n#productivity #remotework #socialmedia #creators #hacks",
                    linkedinVisualTitle = "Defend Your Deep-Work Blocks",
                    twitterVisualTitle = "10x Output Hack",
                    instagramVisualTitle = "Protect Your Focus",
                    linkedinImageStyle = "Ocean Breeze",
                    twitterImageStyle = "Sunset Vivid",
                    instagramImageStyle = "Forest Calm",
                    visualKeywords = "office, clean design, gradient",
                    status = "POSTED",
                    views = 1250,
                    likes = 87,
                    shares = 12,
                    comments = 5
                ),
                PostDraft(
                    originalIdea = "Anouncing AI Automation Integration",
                    tone = "professional",
                    linkedinText = "Enterprise AI integration is no longer a luxury. Research shows that early automation adopters see up to a 40% reduction in workflow bottlenecks. We are proud to share our latest dashboard suite tailored for real-time tracking.\n\nJoin our live panel on Thursday.",
                    twitterText = "AI integration is reshaping modern brand strategies. Don't fall behind. Discover how we optimized our visual deployment funnel in our latest dashboard update. 📈",
                    instagramText = "Leading the future of media visual tools with clean UI and real-time smart parameters. Are you ready to level up your workflow? 💫\n\n#automation #innovation #tech #design",
                    linkedinVisualTitle = "Automation Benefits",
                    twitterVisualTitle = "Visual Deployment",
                    instagramVisualTitle = "Level Up Flow",
                    linkedinImageStyle = "Sunset Vivid",
                    twitterImageStyle = "Ocean Breeze",
                    instagramImageStyle = "Cyber Punk",
                    visualKeywords = "tech, clean UI, neon",
                    status = "POSTED",
                    views = 1890,
                    likes = 143,
                    shares = 25,
                    comments = 18
                )
            )
            for (draft in drafts) {
                db.postDraftDao().insertDraft(draft)
            }
            } catch (e: Exception) {
                android.util.Log.e("AppDatabase", "Error inside populateInitialData", e)
            }
        }
    }
}
