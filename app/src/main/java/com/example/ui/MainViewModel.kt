package com.example.ui

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.api.GeminiService
import com.example.data.AppDatabase
import com.example.data.CollaborationProfile
import com.example.data.ContentRepository
import com.example.data.ContentTemplate
import com.example.data.GeminiGeneratedPostResponse
import com.example.data.PostDraft
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar

enum class Screen {
    DASHBOARD, CREATE, SCHEDULE, ANALYTICS, TEMPLATES, TEAM
}

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application, viewModelScope)
    private val repository = ContentRepository(database)

    // Room database flows
    val drafts: StateFlow<List<PostDraft>> = repository.allDrafts.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val templates: StateFlow<List<ContentTemplate>> = repository.allTemplates.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val profiles: StateFlow<List<CollaborationProfile>> = repository.allProfiles.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // Current screen navigation state
    var activeScreen by mutableStateOf(Screen.DASHBOARD)
        private set

    // Input States
    var inputIdea by mutableStateOf("")
    var selectedTone by mutableStateOf("professional") // "professional", "witty", "urgent"

    // Generation UI States
    var isGenerating by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set

    // Editing States (For current live drafts)
    var activePreviewPlatform by mutableStateOf("LinkedIn") // "LinkedIn", "Twitter/X", "Instagram"
    
    var linkedinText by mutableStateOf("")
    var twitterText by mutableStateOf("")
    var instagramText by mutableStateOf("")

    var linkedinTitle by mutableStateOf("")
    var twitterTitle by mutableStateOf("")
    var instagramTitle by mutableStateOf("")

    var linkedinStyle by mutableStateOf("Ocean Breeze") // Gradients Name Preset
    var twitterStyle by mutableStateOf("Sunset Vivid")
    var instagramStyle by mutableStateOf("Forest Calm")
    
    var visualKeywords by mutableStateOf("")

    init {
        // Safe check
    }

    fun setScreen(screen: Screen) {
        activeScreen = screen
        errorMessage = null
    }

    fun setIdeaFromTemplate(template: ContentTemplate) {
        inputIdea = template.promptTemplate
        selectedTone = template.tone
        activeScreen = Screen.CREATE
    }

    fun startGeneration() {
        if (inputIdea.isBlank()) {
            errorMessage = "Please enter an idea or theme first."
            return
        }

        viewModelScope.launch {
            isGenerating = true
            errorMessage = null
            try {
                val response = GeminiService.generateContent(inputIdea, selectedTone)
                linkedinText = response.linkedinText
                twitterText = response.twitterText
                instagramText = response.instagramText
                
                linkedinTitle = response.linkedinVisualTitle
                twitterTitle = response.twitterVisualTitle
                instagramTitle = response.instagramVisualTitle
                
                visualKeywords = response.visualThemeKeywords

                // Set initial default gradient preset based on tone
                when (selectedTone) {
                    "professional" -> {
                        linkedinStyle = "Ocean Breeze"
                        twitterStyle = "Slate Metal"
                        instagramStyle = "Lavender Sky"
                    }
                    "witty" -> {
                        linkedinStyle = "Sunny Mango"
                        twitterStyle = "Sunset Vivid"
                        instagramStyle = "Cyber Punk"
                    }
                    else -> {
                        linkedinStyle = "Fire Alert"
                        twitterStyle = "Crimson Bold"
                        instagramStyle = "Forest Calm"
                    }
                }
            } catch (e: Exception) {
                errorMessage = e.message ?: "An unexpected error occurred."
            } finally {
                isGenerating = false
            }
        }
    }

    fun discardGeneration() {
        linkedinText = ""
        twitterText = ""
        instagramText = ""
        linkedinTitle = ""
        twitterTitle = ""
        instagramTitle = ""
    }

    fun saveDraft(status: String = "DRAFT", scheduledDelayHours: Int? = null) {
        val scheduleTime = if (scheduledDelayHours != null) {
            System.currentTimeMillis() + (scheduledDelayHours * 60 * 60 * 1000L)
        } else null

        viewModelScope.launch {
            val draft = PostDraft(
                originalIdea = if (inputIdea.length > 50) inputIdea.take(47) + "..." else inputIdea,
                tone = selectedTone,
                linkedinText = linkedinText,
                twitterText = twitterText,
                instagramText = instagramText,
                linkedinVisualTitle = linkedinTitle,
                twitterVisualTitle = twitterTitle,
                instagramVisualTitle = instagramTitle,
                linkedinImageStyle = linkedinStyle,
                twitterImageStyle = twitterStyle,
                instagramImageStyle = instagramStyle,
                visualKeywords = visualKeywords,
                status = status,
                scheduledTime = scheduleTime,
                views = if (status == "POSTED") (400..1500).random() else 0,
                likes = if (status == "POSTED") (20..150).random() else 0,
                shares = if (status == "POSTED") (2..20).random() else 0,
                comments = if (status == "POSTED") (1..15).random() else 0
            )

            repository.insertDraft(draft)
            // Reset fields
            discardGeneration()
            inputIdea = ""
            // Redirect to Schedule screen to see the post
            activeScreen = Screen.SCHEDULE
        }
    }

    // Direct simulated posting
    fun publishDraftInstantly(draft: PostDraft) {
        viewModelScope.launch {
            val updated = draft.copy(
                status = "POSTED",
                scheduledTime = null,
                timestamp = System.currentTimeMillis(),
                views = (300..1800).random(),
                likes = (15..180).random(),
                shares = (1..30).random(),
                comments = (1..20).random()
            )
            repository.updateDraft(updated)
        }
    }

    fun updateDraftInDatabase(draft: PostDraft) {
        viewModelScope.launch {
            repository.updateDraft(draft)
        }
    }

    fun deleteDraft(draftId: Int) {
        viewModelScope.launch {
            repository.deleteDraftById(draftId)
        }
    }

    fun createCustomTemplate(title: String, category: String, tone: String, prompt: String) {
        viewModelScope.launch {
            val templ = ContentTemplate(
                title = title,
                category = category,
                tone = tone,
                promptTemplate = prompt,
                iconName = "bookmark"
            )
            repository.insertTemplate(templ)
        }
    }

    fun addTeamMember(name: String, role: String, colorIndex: Int) {
        viewModelScope.launch {
            val profile = CollaborationProfile(
                name = name,
                role = role,
                avatarColor = colorIndex,
                initial = name.firstOrNull()?.toString()?.uppercase() ?: "U"
            )
            repository.insertProfile(profile)
        }
    }
}

class MainViewModelFactory(
    private val application: Application
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
