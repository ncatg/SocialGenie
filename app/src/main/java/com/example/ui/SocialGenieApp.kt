package com.example.ui

import androidx.compose.animation.*
import androidx.compose.animation.core.spring
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.CollaborationProfile
import com.example.data.ContentTemplate
import com.example.data.PostDraft

// Gradient Color Presets matching high-fidelity aesthetic guidelines
fun getPresetGradient(name: String): Brush {
    return when (name) {
        "Ocean Breeze" -> Brush.verticalGradient(listOf(Color(0xFF00C6FF), Color(0xFF0072FF)))
        "Sunset Vivid" -> Brush.linearGradient(listOf(Color(0xFFF12711), Color(0xFFF5AF19)))
        "Forest Calm" -> Brush.verticalGradient(listOf(Color(0xFF11998E), Color(0xFF38EF7D)))
        "Cyber Punk" -> Brush.horizontalGradient(listOf(Color(0xFF8A2387), Color(0xFFE94057), Color(0xFFF27121)))
        "Lavender Sky" -> Brush.linearGradient(listOf(Color(0xFFa18cd1), Color(0xFFfbc2eb)))
        "Slate Metal" -> Brush.verticalGradient(listOf(Color(0xFF232526), Color(0xFF414345)))
        "Fire Alert" -> Brush.horizontalGradient(listOf(Color(0xFFf857a6), Color(0xFFff5858)))
        else -> Brush.verticalGradient(listOf(Color(0xFF1D976C), Color(0xFF93F9B9))) // Green Field
    }
}

val ALL_GRADIENTS = listOf(
    "Ocean Breeze", "Sunset Vivid", "Forest Calm", "Cyber Punk", "Lavender Sky", "Slate Metal", "Fire Alert"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SocialGenieApp(viewModel: MainViewModel) {
    val drafts by viewModel.drafts.collectAsStateWithLifecycle()
    val templates by viewModel.templates.collectAsStateWithLifecycle()
    val profiles by viewModel.profiles.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "App Logo Logo",
                                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Text(
                            text = "SocialGenie",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 0.5.sp
                            )
                        )
                    }
                },
                actions = {
                    Box(
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .clip(RoundedCornerShape(50.dp))
                            .background(MaterialTheme.colorScheme.primaryContainer)
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .background(Color.Green, CircleShape)
                            )
                            Text(
                                "Workspace Active",
                                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ) {
                NavigationBarItem(
                    selected = viewModel.activeScreen == Screen.CREATE,
                    onClick = { viewModel.setScreen(Screen.CREATE) },
                    icon = { Icon(Icons.Default.Create, "Craft Content") },
                    label = { Text("Create") },
                    modifier = Modifier.testTag("nav_create")
                )
                NavigationBarItem(
                    selected = viewModel.activeScreen == Screen.SCHEDULE,
                    onClick = { viewModel.setScreen(Screen.SCHEDULE) },
                    icon = {
                        val count = drafts.count { it.status == "SCHEDULED" }
                        BadgedBox(badge = {
                            if (count > 0) {
                                Badge { Text(count.toString()) }
                            }
                        }) {
                            Icon(Icons.Default.DateRange, "Queue Status")
                        }
                    },
                    label = { Text("Queue") },
                    modifier = Modifier.testTag("nav_queue")
                )
                NavigationBarItem(
                    selected = viewModel.activeScreen == Screen.ANALYTICS,
                    onClick = { viewModel.setScreen(Screen.ANALYTICS) },
                    icon = { Icon(Icons.Default.BarChart, "Analytics Track") },
                    label = { Text("Analytics") },
                    modifier = Modifier.testTag("nav_analytics")
                )
                NavigationBarItem(
                    selected = viewModel.activeScreen == Screen.TEMPLATES,
                    onClick = { viewModel.setScreen(Screen.TEMPLATES) },
                    icon = { Icon(Icons.Default.LibraryBooks, "Templates Library") },
                    label = { Text("Templates") },
                    modifier = Modifier.testTag("nav_templates")
                )
                NavigationBarItem(
                    selected = viewModel.activeScreen == Screen.TEAM,
                    onClick = { viewModel.setScreen(Screen.TEAM) },
                    icon = { Icon(Icons.Default.People, "Team Collaboration") },
                    label = { Text("Team") },
                    modifier = Modifier.testTag("nav_team")
                )
            }
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.background)
        ) {
            Crossfade(
                targetState = viewModel.activeScreen,
                animationSpec = spring(),
                label = "ScreenTransitions"
            ) { screen ->
                when (screen) {
                    Screen.CREATE -> CreateScreen(viewModel)
                    Screen.SCHEDULE -> ScheduleScreen(viewModel, drafts)
                    Screen.ANALYTICS -> AnalyticsScreen(viewModel, drafts)
                    Screen.TEMPLATES -> TemplatesScreen(viewModel, templates)
                    Screen.TEAM -> TeamScreen(viewModel, profiles)
                }
            }
        }
    }
}

@Composable
fun CreateScreen(viewModel: MainViewModel) {
    var scheduleTimeDialog by remember { mutableStateOf(false) }
    val isGenerated = viewModel.linkedinText.isNotEmpty()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp)
    ) {
        // Idea input pane - Styled in Clean Minimalism
        item {
            Column(
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Main Header Title matching clean minimal theme
                Text(
                    text = "New Campaign Suite",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.5).sp
                    ),
                    modifier = Modifier.padding(top = 4.dp, bottom = 2.dp)
                )

                // Premium Core Idea Card from Clean Minimal HTML specifications
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp), // 3xl roundness
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "THE CORE IDEA",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.5.sp
                            ),
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        TextField(
                            value = viewModel.inputIdea,
                            onValueChange = { viewModel.inputIdea = it },
                            placeholder = { 
                                Text(
                                    "Eco-friendly travel tips for Gen Z...", 
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                ) 
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(100.dp)
                                .testTag("idea_input"),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                disabledContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                unfocusedTextColor = MaterialTheme.colorScheme.onBackground
                            ),
                            singleLine = false,
                            maxLines = 4
                        )
                    }
                }

                // Tone Selector Section styled specifically like the HTML spec
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Desired Tone",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.1).sp
                        ),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 2.dp)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        val tones = listOf(
                            "professional" to Icons.Default.Check,
                            "witty" to Icons.Default.Check,
                            "urgent" to Icons.Default.Check
                        )
                        tones.forEach { (toneName, selectIcon) ->
                            val isSelected = viewModel.selectedTone == toneName
                            
                            // Custom styled chip following the precise Tailwind styling of HTML
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.primaryContainer 
                                        else MaterialTheme.colorScheme.surface
                                    )
                                    .border(
                                        width = 1.dp,
                                        color = if (isSelected) MaterialTheme.colorScheme.primary 
                                                else MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable { viewModel.selectedTone = toneName }
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .testTag("tone_$toneName"),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = selectIcon,
                                            contentDescription = "Selected",
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                    Text(
                                        text = toneName.replaceFirstChar { it.uppercase() },
                                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                        color = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer 
                                                else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Capsule/Pill Generate button following Minimalist Guidelines
                Button(
                    onClick = { viewModel.startGeneration() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .testTag("generate_button"),
                    enabled = !viewModel.isGenerating,
                    shape = RoundedCornerShape(28.dp), // Fully rounded pill shape
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
                ) {
                    if (viewModel.isGenerating) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            "Engaging Gemini AI...",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome, 
                            contentDescription = "Spark",
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            "GENERATE DRAFTS",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 0.5.sp)
                        )
                    }
                }

                if (viewModel.errorMessage != null) {
                    Text(
                        text = viewModel.errorMessage!!,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp, start = 8.dp)
                    )
                }
            }
        }

        // Live Generated / Editable Area
        if (isGenerated) {
            item {
                Text(
                    text = "Your Multi-Channel Campaign",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Black),
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            // Tabs for platform switcher
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                        .padding(4.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val platforms = listOf("LinkedIn", "Twitter/X", "Instagram")
                    platforms.forEach { platformName ->
                        val isSelected = viewModel.activePreviewPlatform == platformName
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(30.dp))
                                .background(if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent)
                                .clickable { viewModel.activePreviewPlatform = platformName }
                                .padding(vertical = 10.dp)
                                .testTag("platform_tab_$platformName"),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                platformName,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Visual Card design section
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        "Platform Image Design (Aspect Ratio Tailored)",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                    )

                    // Card visual rendering matching chosen aspect ratio
                    val activeTitle = when (viewModel.activePreviewPlatform) {
                        "LinkedIn" -> viewModel.linkedinTitle
                        "Twitter/X" -> viewModel.twitterTitle
                        else -> viewModel.instagramTitle
                    }
                    val activeStyle = when (viewModel.activePreviewPlatform) {
                        "LinkedIn" -> viewModel.linkedinStyle
                        "Twitter/X" -> viewModel.twitterStyle
                        else -> viewModel.instagramStyle
                    }

                    VisualPostCard(
                        platform = viewModel.activePreviewPlatform,
                        titleText = activeTitle,
                        gradientPresetName = activeStyle,
                        keywords = viewModel.visualKeywords
                    )

                    // Title editor container
                    OutlinedTextField(
                        value = activeTitle,
                        onValueChange = { textVal ->
                            when (viewModel.activePreviewPlatform) {
                                "LinkedIn" -> viewModel.linkedinTitle = textVal
                                "Twitter/X" -> viewModel.twitterTitle = textVal
                                "Instagram" -> viewModel.instagramTitle = textVal
                            }
                        },
                        label = { Text("Modify Image Headline text") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp)
                    )

                    // Gradient Presets list to customize design
                    Text(
                        "Click to change the Cover Gradient theme:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(ALL_GRADIENTS) { gradName ->
                            val isCurrentGrad = gradName == activeStyle
                            Box(
                                modifier = Modifier
                                    .size(width = 110.dp, height = 44.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(getPresetGradient(gradName))
                                    .border(
                                        width = if (isCurrentGrad) 3.dp else 0.dp,
                                        color = if (isCurrentGrad) MaterialTheme.colorScheme.onBackground else Color.Transparent,
                                        shape = RoundedCornerShape(8.dp)
                                    )
                                    .clickable {
                                        when (viewModel.activePreviewPlatform) {
                                            "LinkedIn" -> viewModel.linkedinStyle = gradName
                                            "Twitter/X" -> viewModel.twitterStyle = gradName
                                            "Instagram" -> viewModel.instagramStyle = gradName
                                        }
                                    }
                                    .padding(4.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    gradName,
                                    color = Color.White,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        shadow = androidx.compose.ui.graphics.Shadow(Color.Black, blurRadius = 4f)
                                    )
                                )
                            }
                        }
                    }
                }
            }

            // Tailored Post Text Copy Editing Block
            item {
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Tailored Narrative Text",
                            style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(50.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f))
                                .padding(horizontal = 8.dp, vertical = 2.dp)
                        ) {
                            val count = when (viewModel.activePreviewPlatform) {
                                "LinkedIn" -> viewModel.linkedinText.length
                                "Twitter/X" -> viewModel.twitterText.length
                                else -> viewModel.instagramText.length
                            }
                            Text(
                                "$count chars",
                                style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    val activeText = when (viewModel.activePreviewPlatform) {
                        "LinkedIn" -> viewModel.linkedinText
                        "Twitter/X" -> viewModel.twitterText
                        else -> viewModel.instagramText
                    }

                    OutlinedTextField(
                        value = activeText,
                        onValueChange = { updated ->
                            when (viewModel.activePreviewPlatform) {
                                "LinkedIn" -> viewModel.linkedinText = updated
                                "Twitter/X" -> viewModel.twitterText = updated
                                "Instagram" -> viewModel.instagramText = updated
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(180.dp),
                        shape = RoundedCornerShape(12.dp)
                    )
                }
            }

            // Save, Schedule and Post CTA panel
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f))
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            "Deploy Actions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )

                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Button(
                                onClick = { viewModel.saveDraft("POSTED") },
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .testTag("post_now_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                            ) {
                                Icon(Icons.Default.Send, "Send")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Publish Now", fontSize = 13.sp)
                            }

                            Button(
                                onClick = { scheduleTimeDialog = true },
                                modifier = Modifier
                                    .weight(1.1f)
                                    .height(48.dp)
                                    .testTag("schedule_btn"),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                            ) {
                                Icon(Icons.Default.Schedule, "Schedule")
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Schedule Post", fontSize = 13.sp)
                            }
                        }

                        OutlinedButton(
                            onClick = { viewModel.saveDraft("DRAFT") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("save_draft_btn"),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Save, "Save Draft")
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Save as Draft")
                        }

                        TextButton(
                            onClick = { viewModel.discardGeneration() },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Discard Campaign", color = MaterialTheme.colorScheme.error)
                        }
                    }
                }
            }
        }
    }

    // Scheduling Modal / Dialog
    if (scheduleTimeDialog) {
        AlertDialog(
            onDismissRequest = { scheduleTimeDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Default.Event, "Event picker")
                    Text("Select Posting Interval")
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    Text("Choose when you'd like SocialGenie to release your tailored posts and visual assets to your integrated feeds:")
                    
                    val options = listOf(
                        "In 1 hour (Highly Optimal Today)" to 1,
                        "In 3 hours (Afternoon Spike)" to 3,
                        "In 8 hours" to 8,
                        "In 24 hours (Tomorrow Morning)" to 24
                    )
                    options.forEach { (label, hours) ->
                        OutlinedButton(
                            onClick = {
                                viewModel.saveDraft("SCHEDULED", hours)
                                scheduleTimeDialog = false
                            },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(label)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(onClick = { scheduleTimeDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun VisualPostCard(
    platform: String,
    titleText: String,
    gradientPresetName: String,
    keywords: String,
    modifier: Modifier = Modifier
) {
    val gradient = getPresetGradient(gradientPresetName)
    val aspect = when (platform) {
        "LinkedIn" -> 4f / 3f
        "Twitter/X" -> 16f / 9f
        else -> 1f // Instagram square
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(aspect)
            .clip(RoundedCornerShape(16.dp))
            .background(gradient)
            .padding(20.dp),
        contentAlignment = Alignment.Center
    ) {
        // Aesthetic grid drawings in canvas
        Canvas(modifier = Modifier.fillMaxSize().alpha(0.12f)) {
            drawCircle(
                color = Color.White,
                radius = size.minDimension / 1.4f,
                center = Offset(size.width * 0.85f, size.height * 0.15f)
            )
            drawCircle(
                color = Color.Black,
                radius = size.minDimension / 2.2f,
                center = Offset(size.width * 0.15f, size.height * 0.85f)
            )
        }

        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = platform.uppercase(),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = Color.White.copy(alpha = 0.82f)
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(50.dp))
                        .background(Color.White.copy(alpha = 0.18f))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    val keyw = keywords.split(",").firstOrNull()?.trim() ?: "dynamic"
                    Text(
                        text = "🧞 $keyw",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White
                    )
                }
            }

            Text(
                text = titleText.ifBlank { "Creating Growth Streams" },
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontWeight = FontWeight.ExtraBold,
                    lineHeight = 32.sp
                ),
                color = Color.White,
                modifier = Modifier
                    .weight(1f, fill = false)
                    .padding(vertical = 12.dp),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Powered by SocialGenie",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold),
                    color = Color.White.copy(alpha = 0.65f)
                )

                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = "Aesthetic logo sparkle",
                    tint = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier.size(16.dp)
                )
            }
        }
    }
}

@Composable
fun ScheduleScreen(viewModel: MainViewModel, drafts: List<PostDraft>) {
    var filterTab by remember { mutableStateOf("All") } // "All", "Scheduled", "Posted", "Drafts"

    val filteredDrafts = when (filterTab) {
        "Scheduled" -> drafts.filter { it.status == "SCHEDULED" }
        "Posted" -> drafts.filter { it.status == "POSTED" }
        "Drafts" -> drafts.filter { it.status == "DRAFT" }
        else -> drafts
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Content Deployment Queue",
            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
        )

        // Switcher chips
        ScrollableTabRow(
            selectedTabIndex = when (filterTab) {
                "Scheduled" -> 1
                "Posted" -> 2
                "Drafts" -> 3
                else -> 0
            },
            edgePadding = 0.dp,
            indicator = {},
            divider = {}
        ) {
            val tabs = listOf("All", "Scheduled", "Posted", "Drafts")
            tabs.forEach { tab ->
                val isSelected = filterTab == tab
                val count = when (tab) {
                    "Scheduled" -> drafts.count { it.status == "SCHEDULED" }
                    "Posted" -> drafts.count { it.status == "POSTED" }
                    "Drafts" -> drafts.count { it.status == "DRAFT" }
                    else -> drafts.size
                }
                Tab(
                    selected = isSelected,
                    onClick = { filterTab = tab },
                    text = {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(tab)
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f)
                                        else MaterialTheme.colorScheme.surfaceVariant
                                    )
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    count.toString(),
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    },
                    modifier = Modifier.padding(horizontal = 4.dp).testTag("queue_tab_$tab")
                )
            }
        }

        if (filteredDrafts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.EventNote,
                        contentDescription = "Empty Queue",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.3f)
                    )
                    Text(
                        "No campaigns in this view",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f)
                    )
                    OutlinedButton(onClick = { viewModel.setScreen(Screen.CREATE) }) {
                        Text("Create Campaign Now")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredDrafts, key = { it.id }) { post ->
                    CampaignCard(post, viewModel)
                }
            }
        }
    }
}

@Composable
fun CampaignCard(post: PostDraft, viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("campaign_card_${post.id}"),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header: Idea title & Badge Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = post.originalIdea,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "Tone: ${post.tone.replaceFirstChar { it.uppercase() }}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                // Status Badge
                val (color, label) = when (post.status) {
                    "SCHEDULED" -> MaterialTheme.colorScheme.secondaryContainer to "Scheduled"
                    "POSTED" -> Color(0xFFC8E6C9) to "Published"
                    else -> MaterialTheme.colorScheme.surfaceVariant to "Draft"
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(30.dp))
                        .background(color)
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                ) {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = if (post.status == "POSTED") Color(0xFF2E7D32) else MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            // Expandable full post contents
            if (expanded) {
                Divider(color = MaterialTheme.colorScheme.outlineVariant)

                // Render tiny aspect-ratio card visual inside
                Text(
                    "Generated Images Suite Check:",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                )

                // Horizontal list of the platform images designed
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    item {
                        TinyPlatformCardPreview("LinkedIn", post.linkedinVisualTitle, post.linkedinImageStyle)
                    }
                    item {
                        TinyPlatformCardPreview("Twitter/X", post.twitterVisualTitle, post.twitterImageStyle)
                    }
                    item {
                        TinyPlatformCardPreview("Instagram", post.instagramVisualTitle, post.instagramImageStyle)
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Segment of social post texts
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "🔗 LinkedIn Tailored Copy:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(post.linkedinText, style = MaterialTheme.typography.bodyMedium)

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        "🐦 Twitter/X Punchy Copy:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(post.twitterText, style = MaterialTheme.typography.bodyMedium)

                    Divider(modifier = Modifier.padding(vertical = 4.dp))

                    Text(
                        "📸 Instagram Post & Hash Copy:",
                        style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    )
                    Text(post.instagramText, style = MaterialTheme.typography.bodyMedium)
                }
            }

            // Simulated Analytics Metrics block for posted history
            if (post.status == "POSTED") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFFF1F8E9))
                        .padding(10.dp)
                ) {
                    Text(
                        "Real-time Live API Analytics Simulator",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, color = Color(0xFF33691E))
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        AnalyticsStatMini("Views", post.views, Icons.Default.Visibility)
                        AnalyticsStatMini("Likes", post.likes, Icons.Default.ThumbUp)
                        AnalyticsStatMini("Shares", post.shares, Icons.Default.Share)
                        AnalyticsStatMini("Chats", post.comments, Icons.Default.ChatBubble)
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = { expanded = !expanded }) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(if (expanded) "Hide Content" else "View Content details")
                        Icon(
                            imageVector = if (expanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                            contentDescription = "Expand toggle"
                        )
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    if (post.status == "SCHEDULED" || post.status == "DRAFT") {
                        IconButton(
                            onClick = { viewModel.publishDraftInstantly(post) },
                            modifier = Modifier.testTag("publish_instantly_${post.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Publish,
                                contentDescription = "Publish instantly to active APIs",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    IconButton(
                        onClick = { viewModel.deleteDraft(post.id) },
                        modifier = Modifier.testTag("delete_campaign_${post.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete from local SQLite database",
                            tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TinyPlatformCardPreview(platform: String, label: String, style: String) {
    Box(
        modifier = Modifier
            .size(width = 120.dp, height = 75.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(getPresetGradient(style))
            .padding(6.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                platform.uppercase(),
                fontSize = 7.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.8f)
            )
            Text(
                label.ifBlank { "Ideas" },
                fontSize = 9.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                lineHeight = 10.sp
            )
            Text(
                "socialgenie.ai",
                fontSize = 5.sp,
                color = Color.White.copy(alpha = 0.6f)
            )
        }
    }
}

@Composable
fun AnalyticsStatMini(label: String, valNum: Int, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Icon(icon, contentDescription = label, modifier = Modifier.size(12.dp), tint = Color(0xFF558B2F))
        Column {
            Text(valNum.toString(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1B5E20))
            Text(label, fontSize = 8.sp, color = Color(0xFF558B2F))
        }
    }
}

@Composable
fun AnalyticsScreen(viewModel: MainViewModel, drafts: List<PostDraft>) {
    val postedCount = drafts.filter { it.status == "POSTED" }
    val totalViews = postedCount.sumOf { it.views }
    val totalLikes = postedCount.sumOf { it.likes }
    val totalShares = postedCount.sumOf { it.shares }
    val totalComments = postedCount.sumOf { it.comments }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(bottom = 32.dp)
    ) {
        item {
            Text(
                text = "Performance Optimization Suite",
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
            )
            Text(
                text = "Track direct API postings response and engagement metrics across LinkedIn, Twitter/X, and Instagram.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
            )
        }

        // Metrics grid
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsKpiCard("Total Views", totalViews.toString(), Icons.Default.Visibility, modifier = Modifier.weight(1f))
                AnalyticsKpiCard("Campaign Clicks", (totalLikes * 1.4f).toInt().toString(), Icons.Default.Gesture, modifier = Modifier.weight(1f))
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                AnalyticsKpiCard("Engagements", (totalLikes + totalComments + totalShares).toString(), Icons.Default.Insights, modifier = Modifier.weight(1f))
                AnalyticsKpiCard("Conversion Share", if (totalViews > 0) String.format("%.2f%%", (totalLikes.toFloat() / totalViews) * 100) else "0%", Icons.Default.Percent, modifier = Modifier.weight(1f))
            }
        }

        // Custom canvas graph chart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Engagement Peak Hours by Platform",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                        Icon(Icons.Default.TrendingUp, "trending icon", tint = MaterialTheme.colorScheme.primary)
                    }

                    // Hand drawn custom beautiful Material 3 line chart via Compose Canvas!
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(150.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                            .padding(8.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val w = size.width
                            val h = size.height

                            // Draw helper axis lines grid
                            for (i in 1..3) {
                                val gridY = h * (i / 4f)
                                drawLine(
                                    color = Color.LightGray.copy(alpha = 0.3f),
                                    start = Offset(0f, gridY),
                                    end = Offset(w, gridY),
                                    strokeWidth = 1.dp.toPx()
                                )
                            }

                            // Draw LinkedIn Azure Path
                            val liPoints = listOf(
                                Offset(w * 0.1f, h * 0.8f),
                                Offset(w * 0.3f, h * 0.4f),
                                Offset(w * 0.5f, h * 0.7f),
                                Offset(w * 0.7f, h * 0.2f),
                                Offset(w * 0.9f, h * 0.6f)
                            )
                            for (idx in 0 until liPoints.size - 1) {
                                drawLine(
                                    color = Color(0xFF0072FF),
                                    start = liPoints[idx],
                                    end = liPoints[idx + 1],
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawCircle(Color(0xFF0072FF), radius = 4.dp.toPx(), center = liPoints[idx])
                            }
                            drawCircle(Color(0xFF0072FF), radius = 4.dp.toPx(), center = liPoints.last())

                            // Draw Instagram Crimson Path
                            val igPoints = listOf(
                                Offset(w * 0.1f, h * 0.9f),
                                Offset(w * 0.3f, h * 0.6f),
                                Offset(w * 0.5f, h * 0.2f),
                                Offset(w * 0.7f, h * 0.5f),
                                Offset(w * 0.9f, h * 0.1f)
                            )
                            for (idx in 0 until igPoints.size - 1) {
                                drawLine(
                                    color = Color(0xFFE94057),
                                    start = igPoints[idx],
                                    end = igPoints[idx + 1],
                                    strokeWidth = 3.dp.toPx()
                                )
                                drawCircle(Color(0xFFE94057), radius = 4.dp.toPx(), center = igPoints[idx])
                            }
                            drawCircle(Color(0xFFE94057), radius = 4.dp.toPx(), center = igPoints.last())
                        }
                    }

                    // Legends
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF0072FF)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("LinkedIn / Business", fontSize = 11.sp, modifier = Modifier.padding(end = 16.dp))

                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFE94057)))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Instagram / Casual", fontSize = 11.sp)
                    }
                }
            }
        }

        // Real-time AI Smart Content Suggestion Tips
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Info, "info", tint = MaterialTheme.colorScheme.primary)
                        Text(
                            "AI audience engagement suggestions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    val tips = listOf(
                        "**LinkedIn Performance**: Professional text layouts featuring structured bullet lists and explicit call-to-actions generate **+35% more reposts** during business hours.",
                        "**Instagram Aspect Ratio Focus**: High-contrast, radiant gradient backgrounds increase image click ratios. We default to a **1:1 square ratio** to avoid bottom-edge crop cuts during dynamic scrolling screens.",
                        "**Witty Tone Spike**: Your witty-toned posts on Twitter / X have experienced their highest retention streams on Tuesday evenings between 7:00 PM and 9:00 PM."
                    )
                    tips.forEach { tip ->
                        Text(
                            text = tip.replace("**", ""), // clean bold indicators
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(bottom = 4.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun AnalyticsKpiCard(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(label, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(value, style = MaterialTheme.typography.headlineLarge.copy(fontWeight = FontWeight.Black))
            }
            Icon(icon, contentDescription = label, tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f), modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun TemplatesScreen(viewModel: MainViewModel, templates: List<ContentTemplate>) {
    var customTemplateDialog by remember { mutableStateOf(false) }

    // Custom template form state
    var customTitle by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var customTone by remember { mutableStateOf("professional") }
    var customPrompt by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Quick Template Suite",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Tap any layout pattern to start instant drafts",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Button(
                onClick = { customTemplateDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_template_btn")
            ) {
                Icon(Icons.Default.Add, "Add Template")
                Text("New")
            }
        }

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f)
        ) {
            items(templates, key = { it.id }) { template ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.setIdeaFromTemplate(template) }
                        .testTag("template_item_${template.id}"),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.35f)
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        val iconVec = when (template.iconName) {
                            "rocket" -> Icons.Default.RocketLaunch
                            "stars" -> Icons.Default.Stars
                            "school" -> Icons.Default.School
                            "flash_on" -> Icons.Default.FlashOn
                            else -> Icons.Default.Bookmark
                        }

                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(iconVec, contentDescription = "icon", tint = MaterialTheme.colorScheme.primary)
                        }

                        Column(modifier = Modifier.weight(1f)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    template.title,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(30.dp))
                                        .background(MaterialTheme.colorScheme.secondaryContainer)
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        template.category,
                                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSecondaryContainer
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                template.promptTemplate,
                                style = MaterialTheme.typography.bodyMedium,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Apply Template",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }
    }

    if (customTemplateDialog) {
        AlertDialog(
            onDismissRequest = { customTemplateDialog = false },
            title = { Text("Create Your Template Layout") },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = customTitle,
                        onValueChange = { customTitle = it },
                        label = { Text("Template Title") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = customCategory,
                        onValueChange = { customCategory = it },
                        label = { Text("Category (E.g. Launch, Tip)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    Text("Default Tone Preset:")
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("professional", "witty", "urgent").forEach { tone ->
                            val isSelected = customTone == tone
                            FilterChip(
                                selected = isSelected,
                                onClick = { customTone = tone },
                                label = { Text(tone.replaceFirstChar { it.uppercase() }) },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    OutlinedTextField(
                        value = customPrompt,
                        onValueChange = { customPrompt = it },
                        label = { Text("Starter prompt logic template") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(115.dp),
                        maxLines = 4
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (customTitle.isNotBlank() && customPrompt.isNotBlank()) {
                            viewModel.createCustomTemplate(customTitle, customCategory, customTone, customPrompt)
                            // Clean up
                            customTitle = ""
                            customCategory = ""
                            customPrompt = ""
                            customTemplateDialog = false
                        }
                    }
                ) {
                    Text("Save to Library")
                }
            },
            dismissButton = {
                TextButton(onClick = { customTemplateDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}

@Composable
fun TeamScreen(viewModel: MainViewModel, profiles: List<CollaborationProfile>) {
    var newMemberDialog by remember { mutableStateOf(false) }
    var memberName by remember { mutableStateOf("") }
    var memberRole by remember { mutableStateOf("") }

    val avatarBackgrounds = listOf(
        Color(0xFFE2F0D9), Color(0xFFE1F5FE), Color(0xFFFFF3E0), Color(0xFFF3E5F5), Color(0xFFE8F5E9)
    )
    val avatarTexts = listOf(
        Color(0xFF388E3C), Color(0xFF0288D1), Color(0xFFF57C00), Color(0xFF7B1FA2), Color(0xFF2E7D32)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Collaboration Hub",
                    style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Black)
                )
                Text(
                    text = "Share campaign drafts and manage team approvals",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            Button(
                onClick = { newMemberDialog = true },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.testTag("add_team_btn")
            ) {
                Icon(Icons.Default.PersonAdd, "Add Member")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Add")
            }
        }

        // Active Team Cards
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    "Workspace Collaborators",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.heightIn(max = 240.dp)
                ) {
                    items(profiles) { profile ->
                        val bgCol = avatarBackgrounds.getOrElse(profile.avatarColor) { avatarBackgrounds.first() }
                        val textCol = avatarTexts.getOrElse(profile.avatarColor) { avatarTexts.first() }

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("team_member_${profile.id}")
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(bgCol),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    profile.initial,
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                                    color = textCol
                                )
                            }

                            Column(modifier = Modifier.weight(1f)) {
                                Text(profile.name, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold))
                                Text(profile.role, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(30.dp))
                                    .background(Color(0xFFE8F5E9))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    "Connected",
                                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                                    color = Color(0xFF2E7D32)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Approval flow cards simulator
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.25f))
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.VerifiedUser, "approvals", tint = MaterialTheme.colorScheme.primary)
                    Text(
                        "Review Board Sync Dashboard",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Text(
                    "Collaborative validation helps prevent branding slip-ups! Send a preview stream link instantly to your team: Jessica Vance approves Instagram visual layouts, and Sarah Miller ensures LinkedIn content aligns with quarterly reports before posting directly.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }

    if (newMemberDialog) {
        AlertDialog(
            onDismissRequest = { newMemberDialog = false },
            title = { Text("Invite Workspace Collaborator") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = memberName,
                        onValueChange = { memberName = it },
                        label = { Text("Full Name") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = memberRole,
                        onValueChange = { memberRole = it },
                        label = { Text("Role (E.g. Creative Lead)") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (memberName.isNotBlank() && memberRole.isNotBlank()) {
                            viewModel.addTeamMember(memberName, memberRole, (0..4).random())
                            memberName = ""
                            memberRole = ""
                            newMemberDialog = false
                        }
                    }
                ) {
                    Text("Send Workspace Invitation")
                }
            },
            dismissButton = {
                TextButton(onClick = { newMemberDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
