package com.example.ui

import androidx.compose.animation.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.*
import com.example.ui.theme.*
import kotlinx.coroutines.delay
import java.util.Calendar

enum class ActiveScreen {
    DASHBOARD,
    ACADEMICS,
    AI_STUDIO,
    CGPA,
    ROADMAP_PLANNER
}

@Composable
fun VtuAppContent(viewModel: VtuViewModel) {
    var activeScreen by remember { mutableStateOf(ActiveScreen.DASHBOARD) }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            VtuHeaderBar(
                activeScreen = activeScreen,
                onBackClick = { activeScreen = ActiveScreen.DASHBOARD }
            )

            AnimatedContent(
                targetState = activeScreen,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "ScreenTransition"
            ) { screen ->
                when (screen) {
                    ActiveScreen.DASHBOARD -> DashboardScreen(
                        viewModel = viewModel,
                        onNavigate = { activeScreen = it }
                    )
                    ActiveScreen.ACADEMICS -> AcademicsScreen(viewModel = viewModel)
                    ActiveScreen.AI_STUDIO -> AiStudioScreen(viewModel = viewModel)
                    ActiveScreen.CGPA -> CgpaScreen(viewModel = viewModel)
                    ActiveScreen.ROADMAP_PLANNER -> RoadmapPlannerScreen(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun VtuHeaderBar(activeScreen: ActiveScreen, onBackClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.background),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (activeScreen != ActiveScreen.DASHBOARD) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("header_back_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
            } else {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(TechPrimary, TechSecondary)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "S",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
                Spacer(modifier = Modifier.width(12.dp))
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (activeScreen) {
                        ActiveScreen.DASHBOARD -> "VTU StudentSphere"
                        ActiveScreen.ACADEMICS -> "Academics Hub"
                        ActiveScreen.AI_STUDIO -> "AI Doubt Studio"
                        ActiveScreen.CGPA -> "CGPA Tracker"
                        ActiveScreen.ROADMAP_PLANNER -> "Roadmap & Planner"
                    },
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = "2025 Scheme EdTech System",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Box(
                modifier = Modifier
                    .size(32.dp)
                    .background(TechCardBg, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Alerts",
                    tint = TechAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(
    viewModel: VtuViewModel,
    onNavigate: (ActiveScreen) -> Unit
) {
    val daysLeft = remember {
        val target = Calendar.getInstance().apply {
            set(2026, Calendar.JULY, 12, 9, 0)
        }.timeInMillis
        val diff = target - System.currentTimeMillis()
        val days = diff / (1000 * 60 * 60 * 24)
        if (days > 0) days else 36L
    }

    val tasks by viewModel.studyTasks.collectAsState()
    val incompleteTasksCount = tasks.count { !it.isCompleted }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 12.dp, bottom = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .background(TechAccent.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = "⌛", fontSize = 24.sp)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "VTU Exams Countdown",
                            color = TechAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "$daysLeft Days Remaining",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                        Text(
                            text = "2025 Scheme odd-sem commences soon. Keep revising!",
                            color = TechTextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "🔥 5 Days", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TechSecondary)
                        Text(text = "Study Streak", fontSize = 11.sp, color = TechTextSecondary)
                    }
                    Box(modifier = Modifier.width(1.dp).height(30.dp).background(TechBorder))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "$incompleteTasksCount Pending", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = TechAccent)
                        Text(text = "Planner Tasks", fontSize = 11.sp, color = TechTextSecondary)
                    }
                }
            }
        }

        item {
            Text(
                text = "Academic & AI Systems",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = TechPrimary,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Academic Hub",
                        icon = "📚",
                        badge = "Core Materials",
                        desc = "Notes, Chapter MCQs & Model Papers",
                        testTag = "btn_academics_hub",
                        onClick = { onNavigate(ActiveScreen.ACADEMICS) }
                    )
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "AI Doubt Studio",
                        icon = "🤖",
                        badge = "Gemini AI",
                        desc = "Doubt solver && Project generator",
                        testTag = "btn_ai_studio",
                        onClick = { onNavigate(ActiveScreen.AI_STUDIO) }
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp), modifier = Modifier.fillMaxWidth()) {
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "CGPA Tracker",
                        icon = "📊",
                        badge = "2025 Scheme",
                        desc = "GPA calculator and academic logs",
                        testTag = "btn_cgpa_tracker",
                        onClick = { onNavigate(ActiveScreen.CGPA) }
                    )
                    FeatureCard(
                        modifier = Modifier.weight(1f),
                        title = "Roadmaps & Planner",
                        icon = "🗺️",
                        badge = "Syllabus Map",
                        desc = "8-Semester guide, tasks, trending skills",
                        testTag = "btn_roadmaps_planner",
                        onClick = { onNavigate(ActiveScreen.ROADMAP_PLANNER) }
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hackathons & Official Circulars",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = Color.White
                )
                Text(
                    text = "VTU News",
                    fontSize = 12.sp,
                    color = TechSecondary,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        item {
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(end = 16.dp)
            ) {
                items(viewModel.hackathonsAndCirculars) { hack ->
                    Card(
                        modifier = Modifier
                            .width(280.dp)
                            .testTag("hackathon_card_${hack.id}"),
                        colors = CardDefaults.cardColors(containerColor = TechCardBg),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = hack.organizer,
                                    color = TechTextSecondary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (hack.tags.contains("Official")) TechAccent.copy(alpha = 0.15f)
                                            else TechSecondary.copy(alpha = 0.15f),
                                            RoundedCornerShape(4.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = hack.tags.first(),
                                        color = if (hack.tags.contains("Official")) TechAccent else TechSecondary,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = hack.title,
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "📅 ${hack.date}",
                                    color = TechTextSecondary,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Details →",
                                    color = TechPrimary,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Daily Tech Feeds",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = Color.White,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(viewModel.techUpdates) { update ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("tech_feed_${update.id}"),
                colors = CardDefaults.cardColors(containerColor = TechCardBg.copy(alpha = 0.6f)),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = update.source,
                            color = TechSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = update.time,
                            color = TechTextSecondary,
                            fontSize = 10.sp
                        )
                    }
                    Text(
                        text = update.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Text(
                        text = update.snippet,
                        color = TechTextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}

@Composable
fun FeatureCard(
    modifier: Modifier = Modifier,
    title: String,
    icon: String,
    badge: String,
    desc: String,
    testTag: String,
    onClick: () -> Unit
) {
    ElevatedCard(
        modifier = modifier
            .testTag(testTag)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = TechCardBg),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .background(TechPrimary.copy(alpha = 0.12f), CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = icon, fontSize = 18.sp)
                }
                Box(
                    modifier = Modifier
                        .background(TechSecondary.copy(alpha = 0.15f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(text = badge, color = TechSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }
            }
            Text(
                text = title,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
            )
            Text(
                text = desc,
                color = TechTextSecondary,
                fontSize = 11.sp,
                lineHeight = 15.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun AcademicsScreen(viewModel: VtuViewModel) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Study Notes", "Solved Papers", "Practice MCQs")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTabIndex,
            containerColor = TechDarkBg,
            contentColor = TechPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTabIndex == idx,
                    onClick = { selectedTabIndex = idx },
                    text = { Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            when (selectedTabIndex) {
                0 -> StudyNotesTab(viewModel = viewModel)
                1 -> SolvedPapersTab(viewModel = viewModel)
                2 -> PracticeQuizTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun StudyNotesTab(viewModel: VtuViewModel) {
    val notes by viewModel.offlineNotes.collectAsState()
    val progressMap by viewModel.noteDownloadProgress.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "📚 Syllabus Notes - 2025 Scheme",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        items(notes) { note ->
            val downloadProgress = progressMap[note.id]

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("note_card_${note.id}"),
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Semester ${note.semester}  •  ${note.chapter}",
                            color = TechSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Box(
                            modifier = Modifier
                                .background(
                                    if (note.isDownloaded) TechTertiary.copy(alpha = 0.15f)
                                    else TechBorder,
                                    RoundedCornerShape(4.dp)
                                )
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (note.isDownloaded) "Offline Saved" else "Cloud",
                                color = if (note.isDownloaded) TechTertiary else TechTextSecondary,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Text(
                        text = note.title,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    Text(
                        text = note.subject,
                        color = TechTextSecondary,
                        fontSize = 12.sp
                    )

                    if (downloadProgress != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { downloadProgress / 100f },
                                modifier = Modifier.fillMaxWidth(),
                                color = TechPrimary,
                                trackColor = TechBorder
                            )
                            Text(
                                text = "Obtaining document: $downloadProgress%",
                                fontSize = 10.sp,
                                color = TechTextSecondary
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.downloadNote(note.id) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("btn_download_note_${note.id}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (note.isDownloaded) TechBorder else TechPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = if (note.isDownloaded) Icons.Default.Check else Icons.Default.PlayArrow,
                                    contentDescription = "Action",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (note.isDownloaded) "Review File Offline" else "Fetch & Download Note",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SolvedPapersTab(viewModel: VtuViewModel) {
    val progressMap by viewModel.solvedPapersDownloadProgress.collectAsState()
    val downloadedSet by viewModel.downloadedPapers.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Text(
                text = "📝 Solved Model Examination Papers",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 15.sp,
                modifier = Modifier.padding(bottom = 6.dp)
            )
        }

        items(viewModel.staticSolvedPapers) { paper ->
            val isDownloaded = downloadedSet.contains(paper.subject)
            val process = progressMap[paper.subject]

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("paper_card_${paper.subject.replace(" ", "_")}"),
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Model Paper - Year: ${paper.year}",
                            color = TechSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = "${paper.questionsCount} Solved Items",
                            color = TechAccent,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = paper.subject,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )

                    if (process != null) {
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            LinearProgressIndicator(
                                progress = { process / 100f },
                                modifier = Modifier.fillMaxWidth(),
                                color = TechSecondary,
                                trackColor = TechBorder
                            )
                            Text(
                                text = "Preparing paper package: $process%",
                                fontSize = 10.sp,
                                color = TechTextSecondary
                            )
                        }
                    } else {
                        Button(
                            onClick = { viewModel.downloadSolvedPaper(paper.subject) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp)
                                .testTag("btn_dl_paper_${paper.subject.replace(" ", "_")}"),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isDownloaded) TechTertiary else TechPrimary
                            ),
                            shape = RoundedCornerShape(8.dp),
                            contentPadding = PaddingValues(0.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = if (isDownloaded) Icons.Default.Done else Icons.Default.Share,
                                    contentDescription = "dl",
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text(
                                    text = if (isDownloaded) "Open PDF Solutions" else "Save Solved PDF",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun PracticeQuizTab(viewModel: VtuViewModel) {
    val quizList by viewModel.practiceQuestions.collectAsState()
    val activeIdx by viewModel.practiceSubjectIndex.collectAsState()
    val answers by viewModel.selectedAnswers.collectAsState()

    val currentSubjectPractice = quizList.getOrNull(activeIdx)

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(quizList) { idx, practice ->
                    ElevatedAssistChip(
                        onClick = { viewModel.selectPracticeSubject(idx) },
                        label = { Text(text = practice.subjectName, fontSize = 12.sp) },
                        colors = AssistChipDefaults.elevatedAssistChipColors(
                            containerColor = if (idx == activeIdx) TechPrimary else TechCardBg,
                            labelColor = if (idx == activeIdx) Color.White else TechTextPrimary
                        ),
                        modifier = Modifier.testTag("practice_subject_tab_$idx")
                    )
                }
            }
        }

        if (currentSubjectPractice != null) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = currentSubjectPractice.subjectName,
                        fontWeight = FontWeight.ExtraBold,
                        color = TechSecondary,
                        fontSize = 14.sp
                    )
                    Text(
                        text = currentSubjectPractice.chapterName,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }

            itemsIndexed(currentSubjectPractice.mcqs) { mcqIdx, mcq ->
                val selectedOption = answers[mcqIdx]
                val answered = selectedOption != null

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("practice_question_$mcqIdx"),
                    colors = CardDefaults.cardColors(containerColor = TechCardBg)
                ) {
                    Column(
                        modifier = Modifier.padding(14.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = "Q${mcqIdx + 1}: ${mcq.question}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            lineHeight = 18.sp
                        )

                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            mcq.options.forEachIndexed { optIdx, option ->
                                val isSelected = selectedOption == optIdx
                                val isCorrect = mcq.correctIndex == optIdx

                                val backgroundColor = when {
                                    !answered -> if (isSelected) TechPrimary.copy(alpha = 0.15f) else TechDarkBg
                                    isSelected && isCorrect -> TechTertiary.copy(alpha = 0.2f)
                                    isSelected && !isCorrect -> MaterialTheme.colorScheme.error.copy(alpha = 0.2f)
                                    answered && isCorrect -> TechTertiary.copy(alpha = 0.2f)
                                    else -> TechDarkBg
                                }

                                val borderColor = when {
                                    !answered -> if (isSelected) TechPrimary else TechBorder
                                    isSelected && isCorrect -> TechTertiary
                                    isSelected && !isCorrect -> MaterialTheme.colorScheme.error
                                    answered && isCorrect -> TechTertiary
                                    else -> TechBorder
                                }

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(backgroundColor, RoundedCornerShape(8.dp))
                                        .border(1.dp, borderColor, RoundedCornerShape(8.dp))
                                        .clickable(enabled = !answered) {
                                            viewModel.submitAnswer(
                                                mcqIdx,
                                                optIdx
                                            )
                                        }
                                        .padding(12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "${'A' + optIdx}.  $option",
                                        color = if (answered && isCorrect) Color.White else TechTextPrimary,
                                        fontSize = 12.sp,
                                        fontWeight = if (answered && isCorrect) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
                        }

                        if (answered) {
                            val correct = selectedOption == mcq.correctIndex
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(TechDarkBg, RoundedCornerShape(6.dp))
                                    .padding(8.dp),
                                verticalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                Text(
                                    text = if (correct) "✅ Perfect Answer! +10 Points" else "❌ Inconsistent Selection",
                                    color = if (correct) TechTertiary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                                Text(
                                    text = "Explanation: ${mcq.explanation}",
                                    color = TechTextSecondary,
                                    fontSize = 11.sp,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiStudioScreen(viewModel: VtuViewModel) {
    var selectedTab by remember { mutableStateOf(0) }
    val tabs = listOf("Gemini Doubt Solver", "Project Idea Gen (AI)")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = selectedTab,
            containerColor = TechDarkBg,
            contentColor = TechPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = selectedTab == idx,
                    onClick = { selectedTab = idx },
                    text = { Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            if (selectedTab == 0) {
                AiSolverWorkspace(viewModel = viewModel)
            } else {
                AiProjectGeneratorWorkspace(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun AiSolverWorkspace(viewModel: VtuViewModel) {
    val chatList by viewModel.chatHistory.collectAsState()
    val isLoading by viewModel.isAiLoading.collectAsState()
    var inputQuery by remember { mutableStateOf("") }
    val focusManager = LocalFocusManager.current

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = TechPrimary.copy(alpha = 0.1f)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "🤖 Gemini Doubt Solver - VTUiverse Edition",
                            fontWeight = FontWeight.Bold,
                            color = TechSecondary,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Resolve questions regarding subjects, credits, syllabus guidelines or theoretical problems natively powered by Gemini 3.5 Flash.",
                            fontSize = 11.sp,
                            color = TechTextSecondary,
                            lineHeight = 15.sp
                        )
                    }
                }
            }

            if (chatList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "💬", fontSize = 42.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                "No chats active yet.",
                                color = TechTextSecondary,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                "Type your chemistry, maths or credit query below.",
                                color = TechTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            } else {
                items(chatList) { chat ->
                    val isUser = chat.sender == "user"
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(
                                containerColor = if (isUser) TechPrimary else TechCardBg
                            ),
                            shape = RoundedCornerShape(
                                topStart = 12.dp,
                                topEnd = 12.dp,
                                bottomStart = if (isUser) 12.dp else 0.dp,
                                bottomEnd = if (isUser) 0.dp else 12.dp
                            ),
                            modifier = Modifier
                                .widthIn(max = 290.dp)
                                .testTag("chat_msg_${chat.id}")
                        ) {
                            Text(
                                text = chat.message,
                                color = Color.White,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(12.dp),
                                lineHeight = 16.sp
                            )
                        }
                    }
                }
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TechCardBg),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(14.dp),
                                    strokeWidth = 2.dp,
                                    color = TechSecondary
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Solver processing query...",
                                    fontSize = 11.sp,
                                    color = TechTextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(bottom = 12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = { Text(text = "Ask model, e.g. What is Boolean matrix?", fontSize = 12.sp) },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("ai_doubt_input"),
                    shape = RoundedCornerShape(12.dp),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(onSend = {
                        viewModel.askDoubt(inputQuery)
                        inputQuery = ""
                        focusManager.clearFocus()
                    }),
                    maxLines = 2,
                    textStyle = TextStyle(fontSize = 13.sp)
                )

                IconButton(
                    onClick = {
                        viewModel.askDoubt(inputQuery)
                        inputQuery = ""
                        focusManager.clearFocus()
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .background(TechPrimary, RoundedCornerShape(12.dp))
                        .testTag("ai_doubt_send_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (chatList.isNotEmpty()) {
                TextButton(
                    onClick = { viewModel.clearChatHistory() },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .testTag("btn_clear_chat"),
                    colors = ButtonDefaults.textButtonColors(contentColor = Color.Red.copy(alpha = 0.8f))
                ) {
                    Text(text = "❌ Wipe Chats Database", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AiProjectGeneratorWorkspace(viewModel: VtuViewModel) {
    var domainText by remember { mutableStateOf("Android App using Compose") }
    var selectedSem by remember { mutableStateOf(5) }
    val isGenLoading by viewModel.isProjectLoading.collectAsState()
    val projectsResult by viewModel.generatedProjects.collectAsState()
    val focusManager = LocalFocusManager.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechSecondary.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "✨ AI Engineering Project Generator",
                        fontWeight = FontWeight.Bold,
                        color = TechSecondary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Stuck in 5th or 6th sem miniprojects? Input your target topic domain and we'll generate complete project specifications utilizing Gemini.",
                        fontSize = 11.sp,
                        color = TechTextSecondary,
                        lineHeight = 15.sp
                    )
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text("Select Target Semester:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val sems = listOf(3, 4, 5, 6, 7, 8)
                    items(sems) { sem ->
                        ElevatedAssistChip(
                            onClick = { selectedSem = sem },
                            label = { Text("Sem $sem", fontSize = 11.sp) },
                            colors = AssistChipDefaults.elevatedAssistChipColors(
                                containerColor = if (selectedSem == sem) TechSecondary else TechCardBg,
                                labelColor = if (selectedSem == sem) Color.Black else TechTextPrimary
                            )
                        )
                    }
                }
            }
        }

        item {
            Text("Topic Domain / Domain Focus:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 11.sp)
            Spacer(modifier = Modifier.height(4.dp))
            OutlinedTextField(
                value = domainText,
                onValueChange = { domainText = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("ai_project_domain_input"),
                shape = RoundedCornerShape(10.dp),
                textStyle = TextStyle(fontSize = 13.sp)
            )
        }

        item {
            Button(
                onClick = {
                    viewModel.generateProjects(domainText, selectedSem)
                    focusManager.clearFocus()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("btn_generate_projects"),
                colors = ButtonDefaults.buttonColors(containerColor = TechPrimary)
            ) {
                if (isGenLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.White, strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Architecting Ideas...", fontSize = 12.sp)
                } else {
                    Text("🤖 Generate Project Ideas via AI", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        if (projectsResult.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("ai_projects_result_card"),
                    colors = CardDefaults.cardColors(containerColor = TechDarkBg),
                    border = BorderStroke(1.dp, TechBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Text(
                            text = "Generated Projects Outline:",
                            color = TechSecondary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = projectsResult,
                            color = Color.White,
                            fontSize = 12.sp,
                            lineHeight = 17.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun CgpaScreen(viewModel: VtuViewModel) {
    val currentSemester by viewModel.calculatorSemester.collectAsState()
    val calculatorSubjects by viewModel.calculatorSubjects.collectAsState()
    val calculatedSgpa by viewModel.calculatedSgpa.collectAsState()
    val savedCgpaList by viewModel.savedCgpaList.collectAsState()

    var showSavedCgpaTab by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            Button(
                onClick = { showSavedCgpaTab = false },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (!showSavedCgpaTab) TechSecondary else TechBorder
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
            ) {
                Text("SGPA Calculator", fontSize = 12.sp, color = if (showSavedCgpaTab) Color.White else Color.Black)
            }
            Button(
                onClick = { showSavedCgpaTab = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (showSavedCgpaTab) TechSecondary else TechBorder
                ),
                modifier = Modifier.weight(1f).padding(horizontal = 6.dp)
            ) {
                Text("Saved Logs Base", fontSize = 12.sp, color = if (showSavedCgpaTab) Color.Black else Color.White)
            }
        }

        Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            if (!showSavedCgpaTab) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = TechCardBg.copy(alpha = 0.5f)),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text(
                                    text = "25 Scheme Grade Value Index",
                                    color = TechSecondary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Outstanding [S] = 10, Excellent [A] = 9, Very Good [B] = 8, Good [C] = 7, Above Avg [D] = 6, Avg [E] = 5, Fail [F] = 0.",
                                    fontSize = 11.sp,
                                    color = TechTextSecondary,
                                    lineHeight = 15.sp
                                )
                            }
                        }
                    }

                    item {
                        Text("Select Target Semester Grade Matrix:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 12.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            val itemsList = (1..8).toList()
                            items(itemsList) { s ->
                                FilterChip(
                                    selected = currentSemester == s,
                                    onClick = { viewModel.loadCalculatorSemester(s) },
                                    label = { Text("Sem $s") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = TechPrimary,
                                        selectedLabelColor = Color.White
                                    ),
                                    modifier = Modifier.testTag("calc_sem_$s")
                                )
                            }
                        }
                    }

                    items(calculatorSubjects) { item ->
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("subject_row_${item.code}"),
                            colors = CardDefaults.cardColors(containerColor = TechCardBg)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 12.dp, vertical = 6.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f).padding(end = 8.dp)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(text = item.code, fontWeight = FontWeight.Bold, color = TechSecondary, fontSize = 11.sp)
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(text = "[${item.credits} Credits]", color = TechTextSecondary, fontSize = 10.sp)
                                    }
                                    Text(
                                        text = item.name,
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        maxLines = 1,
                                        overflow = TextOverflow.Ellipsis
                                    )
                                }

                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    val grades = listOf("S", "A", "B", "C", "D", "E", "F")
                                    grades.forEach { grade ->
                                        Box(
                                            modifier = Modifier
                                                .size(24.dp)
                                                .background(
                                                    if (item.grade == grade) TechPrimary else TechDarkBg,
                                                    RoundedCornerShape(4.dp)
                                                )
                                                .clickable { viewModel.updateSubjectGrade(item.code, grade) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = grade,
                                                color = if (item.grade == grade) Color.White else TechTextSecondary,
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    item {
                        Button(
                            onClick = { viewModel.calculateSgpa() },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("btn_trigger_calculate"),
                            colors = ButtonDefaults.buttonColors(containerColor = TechPrimary)
                        ) {
                            Text("⚡ Calculate Semester Credits SGPA", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    if (calculatedSgpa != null) {
                        item {
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("result_sgpa_card"),
                                colors = CardDefaults.cardColors(containerColor = TechTertiary.copy(alpha = 0.15f)),
                                border = BorderStroke(1.dp, TechTertiary)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Text(
                                        text = "Semester $currentSemester Computation Complete",
                                        color = TechSecondary,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "$calculatedSgpa",
                                        color = Color.White,
                                        fontWeight = FontWeight.ExtraBold,
                                        fontSize = 42.sp
                                    )
                                    Text(
                                        text = "Class Definition: " + when {
                                            calculatedSgpa!! >= 7.75 -> "First Class Distinction"
                                            calculatedSgpa!! >= 6.75 -> "First Class"
                                            calculatedSgpa!! >= 5.0 -> "Second Class"
                                            else -> "Outstanding Improvements Required"
                                        },
                                        fontSize = 12.sp,
                                        color = Color.White
                                    )

                                    Button(
                                        onClick = { viewModel.saveCurrentCgpaCalculation() },
                                        colors = ButtonDefaults.buttonColors(containerColor = TechTertiary),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("btn_save_gpa_state")
                                    ) {
                                        Text("💾 Save SGPA log to database", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold)
                                    }
                                }
                            }
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(vertical = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (savedCgpaList.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 50.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("💾", fontSize = 42.sp)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text("Database Log Empty", color = TechTextSecondary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                    Text("Calculate and click Save to log your GPA.", color = TechTextSecondary, fontSize = 11.sp)
                                }
                            }
                        }
                    } else {
                        val cumulativeCgpa = if (savedCgpaList.isNotEmpty()) {
                            savedCgpaList.sumOf { it.sgpa } / savedCgpaList.size
                        } else 0.0

                        item {
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = TechCardBg)
                            ) {
                                Column(
                                    modifier = Modifier.padding(14.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally,
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    Text("Cumulative CGPA Tracker", color = TechSecondary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                                    Text(
                                        text = String.format("%.2f", cumulativeCgpa),
                                        color = Color.White,
                                        fontWeight = FontWeight.Black,
                                        fontSize = 44.sp
                                    )
                                    Text(
                                        text = "Averaged over ${savedCgpaList.size} logged semesters",
                                        fontSize = 11.sp,
                                        color = TechTextSecondary
                                    )
                                }
                            }
                        }

                        item {
                            Text(
                                text = "Logged Semesters database logs:",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp)
                            )
                        }

                        items(savedCgpaList) { row ->
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("saved_cgpa_${row.id}"),
                                colors = CardDefaults.cardColors(containerColor = TechCardBg.copy(alpha = 0.5f))
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column {
                                        Text(
                                            text = row.semesterName,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 14.sp
                                        )
                                        Text(
                                            text = "Logged overall CGPA: ${row.cgpa}",
                                            color = TechTextSecondary,
                                            fontSize = 11.sp
                                        )
                                    }
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(
                                            text = "SGPA: ${row.sgpa}",
                                            fontWeight = FontWeight.Black,
                                            color = TechSecondary,
                                            fontSize = 16.sp,
                                            modifier = Modifier.padding(horizontal = 12.dp)
                                        )

                                        IconButton(
                                            onClick = { viewModel.deleteSavedCgpa(row.id) },
                                            modifier = Modifier.testTag("delete_cgpa_${row.id}")
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Delete,
                                                contentDescription = "Del",
                                                tint = Color.Red.copy(alpha = 0.8f),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun RoadmapPlannerScreen(viewModel: VtuViewModel) {
    var activeSubTab by remember { mutableStateOf(0) }
    val tabs = listOf("2025 Syllabus map", "Core Planner", "Trending Skills")

    Column(modifier = Modifier.fillMaxSize()) {
        TabRow(
            selectedTabIndex = activeSubTab,
            containerColor = TechDarkBg,
            contentColor = TechPrimary,
            modifier = Modifier.fillMaxWidth()
        ) {
            tabs.forEachIndexed { idx, title ->
                Tab(
                    selected = activeSubTab == idx,
                    onClick = { activeSubTab = idx },
                    text = { Text(text = title, fontWeight = FontWeight.SemiBold, fontSize = 12.sp) }
                )
            }
        }

        Box(modifier = Modifier.weight(1f).padding(horizontal = 16.dp)) {
            when (activeSubTab) {
                0 -> SyllabusRoadmapSubTab(viewModel = viewModel)
                1 -> StudyPlannerSubTab(viewModel = viewModel)
                2 -> TrendingSkillsSubTab(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun SyllabusRoadmapSubTab(viewModel: VtuViewModel) {
    var expandedSemester by remember { mutableStateOf<Int?>(1) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechPrimary.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("💡 VTU 2025 Scheme Curriculum Map", fontWeight = FontWeight.Bold, color = TechSecondary, fontSize = 13.sp)
                    Text("Total required degree completion credits is strictly mapped sem-wise. Tap each semester block below to expand subject details.", fontSize = 11.sp, color = TechTextSecondary, lineHeight = 15.sp)
                }
            }
        }

        items(viewModel.semestersRoadmap) { sem ->
            val isExpanded = expandedSemester == sem.semester

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("roadmap_semester_block_${sem.semester}")
                    .clickable { expandedSemester = if (isExpanded) null else sem.semester },
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                "Semester ${sem.semester}",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 15.sp
                            )
                            Text(
                                "Core Credits Structure: ${sem.totalCredits} credits total",
                                color = TechTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(TechPrimary.copy(alpha = 0.15f), CircleShape)
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isExpanded) "▲" else "▼", color = TechPrimary, fontSize = 10.sp)
                        }
                    }

                    if (isExpanded) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            sem.subjects.forEach { subject ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(TechDarkBg, RoundedCornerShape(8.dp))
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(
                                            text = subject.name,
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.weight(1f)
                                        )
                                        Text(
                                            text = "${subject.credits} Cr",
                                            color = TechSecondary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 12.sp,
                                            modifier = Modifier.padding(4.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(2.dp))
                                    Text(
                                        text = "Code: ${subject.code}",
                                        color = TechTextSecondary,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 10.sp
                                    )
                                    Text(
                                        text = subject.description,
                                        color = TechTextSecondary,
                                        fontSize = 11.sp,
                                        lineHeight = 15.sp,
                                        modifier = Modifier.padding(top = 2.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StudyPlannerSubTab(viewModel: VtuViewModel) {
    val tasks by viewModel.studyTasks.collectAsState()
    var newTaskTitle by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Computing") }
    val focusManager = LocalFocusManager.current

    val categories = listOf("Computing", "Core Labs", "Maths/Chem", "Exam Study")

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("✏️ Add New Study Goal", fontWeight = FontWeight.Bold, color = TechSecondary, fontSize = 13.sp)

                    OutlinedTextField(
                        value = newTaskTitle,
                        onValueChange = { newTaskTitle = it },
                        placeholder = { Text("Task description, e.g. Revise Python List, Eigenvalues", fontSize = 11.sp) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("study_planner_title_input"),
                        shape = RoundedCornerShape(10.dp),
                        textStyle = TextStyle(fontSize = 12.sp)
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        categories.forEach { cat ->
                            Box(
                                modifier = Modifier
                                    .background(
                                        if (selectedCategory == cat) TechPrimary else TechDarkBg,
                                        RoundedCornerShape(6.dp)
                                    )
                                    .clickable { selectedCategory = cat }
                                    .padding(vertical = 6.dp, horizontal = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = cat,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selectedCategory == cat) Color.White else TechTextSecondary
                                )
                            }
                        }
                    }

                    Button(
                        onClick = {
                            viewModel.addStudyTask(newTaskTitle, "DueDate - Today", selectedCategory)
                            newTaskTitle = ""
                            focusManager.clearFocus()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("btn_add_study_task"),
                        colors = ButtonDefaults.buttonColors(containerColor = TechPrimary)
                    ) {
                        Text("Add Target Task To Database logs", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                "Your Active Study Milestones:",
                fontWeight = FontWeight.Bold,
                color = Color.White,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        if (tasks.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No goals mapped yet. Add some to start study cycles!", color = TechTextSecondary, fontSize = 11.sp)
                }
            }
        } else {
            items(tasks) { task ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("study_task_${task.id}"),
                    colors = CardDefaults.cardColors(containerColor = TechCardBg.copy(alpha = 0.6f))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Checkbox(
                                checked = task.isCompleted,
                                onCheckedChange = { viewModel.toggleTaskCompletion(task) },
                                modifier = Modifier.testTag("chk_task_${task.id}")
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Column {
                                Text(
                                    text = task.title,
                                    color = if (task.isCompleted) TechTextSecondary else Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 12.sp,
                                    textDecoration = if (task.isCompleted) androidx.compose.ui.text.style.TextDecoration.LineThrough else null
                                )
                                Text(
                                    text = "Category: ${task.category}",
                                    color = TechSecondary,
                                    fontSize = 10.sp
                                )
                            }
                        }

                        IconButton(
                            onClick = { viewModel.deleteTask(task) },
                            modifier = Modifier.testTag("btn_delete_task_${task.id}")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "delete",
                                tint = Color.Red.copy(alpha = 0.8f),
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TrendingSkillsSubTab(viewModel: VtuViewModel) {
    var expandedSkill by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = TechCardBg.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("🔥 High-Demand Tech Skills for 2025 graduates", fontWeight = FontWeight.Bold, color = TechSecondary, fontSize = 13.sp)
                    Text("These skills are highly requested by recruiters hiring from VTU campuses. Align your college miniprojects around these topics.", fontSize = 11.sp, color = TechTextSecondary, lineHeight = 15.sp)
                }
            }
        }

        items(viewModel.trendingSkills) { skill ->
            val isExpanded = expandedSkill == skill.title

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("skill_card_${skill.title.replace(" ", "_")}")
                    .clickable { expandedSkill = if (isExpanded) null else skill.title },
                colors = CardDefaults.cardColors(containerColor = TechCardBg)
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                skill.title,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 13.sp
                            )
                            Text(
                                "Difficulty level: ${skill.difficulty}",
                                color = TechTextSecondary,
                                fontSize = 11.sp
                            )
                        }
                        Box(
                            modifier = Modifier
                                .background(TechPrimary.copy(alpha = 0.15f), CircleShape)
                                .size(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = if (isExpanded) "▲" else "▼", color = TechPrimary, fontSize = 10.sp)
                        }
                    }

                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Market Demand Index", fontSize = 10.sp, color = TechTextSecondary)
                            Text("${skill.demand}%", fontSize = 10.sp, color = TechSecondary, fontWeight = FontWeight.Bold)
                        }
                        LinearProgressIndicator(
                            progress = { skill.demand / 100f },
                            modifier = Modifier.fillMaxWidth(),
                            color = TechSecondary,
                            trackColor = TechBorder
                        )
                    }

                    if (isExpanded) {
                        HorizontalDivider(color = TechBorder)
                        Text(
                            text = skill.description,
                            fontSize = 11.sp,
                            lineHeight = 15.sp,
                            color = TechTextSecondary
                        )

                        Text("Important curriculum keywords:", fontWeight = FontWeight.Bold, color = Color.White, fontSize = 10.sp)
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            skill.keywords.forEach { word ->
                                Box(
                                    modifier = Modifier
                                        .background(TechDarkBg, RoundedCornerShape(4.dp))
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(text = word, color = TechSecondary, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
