package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.lifecycleScope
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.model.GoalEvent
import com.example.model.LeagueId
import com.example.model.Match
import com.example.service.FavoriteGoalNotificationService
import com.example.service.LicenseManager
import com.example.service.MatchSimulationEngine
import com.example.ui.components.MatchTrackerPlaceholderData
import com.example.ui.screens.PasscodeLockScreen
import com.example.ui.components.GoalCelebrationDialog
import com.example.ui.components.GoalEventCard
import com.example.ui.components.LeagueFilterChips
import com.example.ui.components.MatchCard
import com.example.ui.components.MatchDetailSheet
import com.example.ui.components.MatchTracker
import com.example.ui.components.TrackerMatchDetailSheet
import com.example.ui.components.TrackerMatchItem
import com.example.ui.components.SoundSetupCard
import com.example.ui.components.TestGoalBottomSheet
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoalNeonGreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumPitchDark
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

class MainActivity : ComponentActivity() {

    private lateinit var simulationEngine: MatchSimulationEngine

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val coroutineScope = lifecycleScope
        simulationEngine = MatchSimulationEngine(this, coroutineScope)

        setContent {
            MyApplicationTheme {
                GoalAlertApp(simulationEngine)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        simulationEngine.soundPlayer.stop()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalAlertApp(engine: MatchSimulationEngine) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val licenseManager = remember { LicenseManager(context) }
    val favoriteGoalService = remember { FavoriteGoalNotificationService(context) }
    var isAppUnlocked by remember { mutableStateOf(licenseManager.isActivated()) }

    if (!isAppUnlocked) {
        PasscodeLockScreen(
            licenseManager = licenseManager,
            onUnlocked = { isAppUnlocked = true },
            onPreviewSound = { engine.soundPlayer.playStadiumCheer() }
        )
        return
    }

    val matches by engine.matches.collectAsStateWithLifecycle()
    val goalEvents by engine.goalEvents.collectAsStateWithLifecycle()
    val latestGoal by engine.latestGoalAlert.collectAsStateWithLifecycle()
    val isSimulationRunning by engine.isLiveSimulationRunning.collectAsStateWithLifecycle()

    var selectedLeague by remember { mutableStateOf(LeagueId.ALL) }
    var selectedTabIndex by remember { mutableIntStateOf(0) } // 0: Live Matches, 1: Goal Feed, 2: Sound & Architecture

    var selectedMatchForDetail by remember { mutableStateOf<Match?>(null) }
    var selectedTrackerMatch by remember { mutableStateOf<TrackerMatchItem?>(null) }
    var isTestSheetOpen by remember { mutableStateOf(false) }

    val matchDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val trackerDetailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val testSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Notification Permission Check (Android 13+)
    var hasNotificationPermission by remember {
        mutableStateOf(
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.POST_NOTIFICATIONS
                ) == PackageManager.PERMISSION_GRANTED
            } else true
        )
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        hasNotificationPermission = isGranted
    }

    LaunchedEffect(Unit) {
        if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    val filteredMatches = remember(matches, selectedLeague) {
        if (selectedLeague == LeagueId.ALL) matches
        else matches.filter { it.league == selectedLeague }
    }

    val filteredGoals = remember(goalEvents, selectedLeague) {
        if (selectedLeague == LeagueId.ALL) goalEvents
        else goalEvents.filter { it.league == selectedLeague }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .testTag("goal_alert_screen"),
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        containerColor = StadiumPitchDark,
        topBar = {
            GoalAlertTopBar(
                isLiveActive = isSimulationRunning,
                onToggleLive = { engine.toggleSimulation() },
                onOpenTestSheet = { isTestSheetOpen = true },
                onQuickPlaySound = { engine.soundPlayer.playStadiumCheer() }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Notification Permission Banner if missing
            if (!hasNotificationPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF422C00))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🔔 Allow notifications for live goal alerts",
                            style = MaterialTheme.typography.bodySmall,
                            color = TrophyGold,
                            modifier = Modifier.weight(1f)
                        )
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS) },
                            colors = ButtonDefaults.buttonColors(containerColor = TrophyGold),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.height(30.dp)
                        ) {
                            Text(text = "Enable", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // Primary Tabs: Matches vs Goal Feed vs Sound Setup
            PrimaryTabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = StadiumPitchDark,
                contentColor = GoalNeonGreen,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedTabIndex),
                        color = GoalNeonGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.SportsSoccer, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Match Tracker", fontWeight = if (selectedTabIndex == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.testTag("tab_match_tracker")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Notifications, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Goal Feed", fontWeight = if (selectedTabIndex == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.testTag("tab_goal_feed")
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Audio & Guide", fontWeight = if (selectedTabIndex == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.testTag("tab_audio_guide")
                )
            }

            // League Filter Chips (for Goal Feed)
            if (selectedTabIndex == 1) {
                LeagueFilterChips(
                    selectedLeague = selectedLeague,
                    onSelectLeague = { selectedLeague = it }
                )
            }

            // Main Content Area
            when (selectedTabIndex) {
                0 -> {
                    // Match Tracker Component View
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 24.dp)
                    ) {
                        item {
                            MatchTracker(
                                onMatchClick = { matchItem ->
                                    selectedTrackerMatch = matchItem
                                },
                                onTriggerFavoriteGoal = {
                                    val favId = favoriteGoalService.favoritesManager.getFavoriteIds().firstOrNull() ?: "pl_1"
                                    val targetMatch = MatchTrackerPlaceholderData.matches.find { it.id == favId }
                                        ?: MatchTrackerPlaceholderData.matches.first()

                                    val scorer = if (targetMatch.homeTeam.code == "MCI") "Erling Haaland" else "Kylian Mbappé"
                                    val assist = if (targetMatch.homeTeam.code == "MCI") "Kevin De Bruyne" else "Jude Bellingham"

                                    favoriteGoalService.triggerIfFavoriteGoal(
                                        matchId = targetMatch.id,
                                        leagueName = targetMatch.league.leagueName,
                                        homeTeamName = targetMatch.homeTeam.name,
                                        awayTeamName = targetMatch.awayTeam.name,
                                        scoringTeamName = targetMatch.homeTeam.name,
                                        scorer = scorer,
                                        assist = assist,
                                        minute = targetMatch.minute + 1,
                                        homeScore = targetMatch.homeScore + 1,
                                        awayScore = targetMatch.awayScore,
                                        forceTrigger = true
                                    )
                                }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(14.dp))
                            Card(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceElevated)
                            ) {
                                Row(
                                    modifier = Modifier.padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                            text = "🔔 Favorite Goal Alerts",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = TextPrimary
                                        )
                                        Text(
                                            text = "Stadium roar fires automatically whenever a pinned match scores",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextSecondary
                                        )
                                    }
                                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Button(
                                            onClick = {
                                                val favId = favoriteGoalService.favoritesManager.getFavoriteIds().firstOrNull() ?: "pl_1"
                                                val targetMatch = MatchTrackerPlaceholderData.matches.find { it.id == favId }
                                                    ?: MatchTrackerPlaceholderData.matches.first()
                                                val scorer = if (targetMatch.homeTeam.code == "MCI") "Erling Haaland" else "Kylian Mbappé"
                                                val assist = if (targetMatch.homeTeam.code == "MCI") "Kevin De Bruyne" else "Jude Bellingham"

                                                favoriteGoalService.triggerIfFavoriteGoal(
                                                    matchId = targetMatch.id,
                                                    leagueName = targetMatch.league.leagueName,
                                                    homeTeamName = targetMatch.homeTeam.name,
                                                    awayTeamName = targetMatch.awayTeam.name,
                                                    scoringTeamName = targetMatch.homeTeam.name,
                                                    scorer = scorer,
                                                    assist = assist,
                                                    minute = targetMatch.minute + 1,
                                                    homeScore = targetMatch.homeScore + 1,
                                                    awayScore = targetMatch.awayScore,
                                                    forceTrigger = true
                                                )
                                            },
                                            colors = ButtonDefaults.buttonColors(containerColor = TrophyGold),
                                            modifier = Modifier.testTag("test_fav_goal_btn")
                                        ) {
                                            Text("⭐ Fav Goal", color = Color(0xFF422C00), fontWeight = FontWeight.Bold)
                                        }

                                        Button(
                                            onClick = { engine.triggerRandomGoal() },
                                            colors = ButtonDefaults.buttonColors(containerColor = GoalNeonGreen),
                                            modifier = Modifier.testTag("test_goal_now_button")
                                        ) {
                                            Text("All ⚽", color = Color(0xFF003918), fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Goal Feed List
                    if (filteredGoals.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(32.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    imageVector = Icons.Default.SportsSoccer,
                                    contentDescription = null,
                                    tint = TextMuted,
                                    modifier = Modifier.size(48.dp)
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = "No goals recorded yet in this league",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = { engine.triggerRandomGoal(selectedLeague) },
                                    colors = ButtonDefaults.buttonColors(containerColor = GoalNeonGreen)
                                ) {
                                    Text("Simulate Goal Now", color = Color(0xFF003918), fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            items(filteredGoals, key = { it.id }) { goal ->
                                GoalEventCard(
                                    goal = goal,
                                    onPlaySound = { engine.soundPlayer.playStadiumCheer() }
                                )
                            }
                        }
                    }
                }
                2 -> {
                    // Audio & Technical Guide tab
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        item {
                            SoundSetupCard(
                                onPlaySound = { engine.soundPlayer.playStadiumCheer() }
                            )
                        }

                        item {
                            com.example.ui.components.LocalCashSalesCard(
                                onRelockRequested = { isAppUnlocked = false }
                            )
                        }

                        item {
                            TechnicalArchitectureCard()
                        }
                    }
                }
            }
        }
    }

    // Goal Celebration Pop-up Dialog
    GoalCelebrationDialog(
        goal = latestGoal,
        onDismiss = { engine.clearLatestGoalAlert() },
        onReplayAudio = { engine.soundPlayer.playStadiumCheer() }
    )

    // Match Detail Sheet
    if (selectedMatchForDetail != null) {
        val matchId = selectedMatchForDetail!!.id
        val liveMatch = matches.find { it.id == matchId } ?: selectedMatchForDetail!!
        MatchDetailSheet(
            match = liveMatch,
            sheetState = matchDetailSheetState,
            onDismiss = { selectedMatchForDetail = null },
            onTriggerGoalHome = {
                val candidate = com.example.data.MockDataProvider.candidateScorers.find {
                    it.team.name == liveMatch.homeTeam.name
                } ?: com.example.data.MockDataProvider.candidateScorers.random()
                engine.triggerGoal(liveMatch.id, candidate.name, candidate.assist, isHomeTeam = true)
            },
            onTriggerGoalAway = {
                val candidate = com.example.data.MockDataProvider.candidateScorers.find {
                    it.team.name == liveMatch.awayTeam.name
                } ?: com.example.data.MockDataProvider.candidateScorers.random()
                engine.triggerGoal(liveMatch.id, candidate.name, candidate.assist, isHomeTeam = false)
            },
            onPlaySound = { engine.soundPlayer.playStadiumCheer() }
        )
    }

    // Test Goal Bottom Sheet
    if (isTestSheetOpen) {
        TestGoalBottomSheet(
            sheetState = testSheetState,
            matches = matches,
            onDismiss = { isTestSheetOpen = false },
            onTriggerGoal = { matchId, scorer, assist, isHome ->
                engine.triggerGoal(matchId, scorer, assist, isHome)
            },
            onTriggerRandom = {
                engine.triggerRandomGoal(selectedLeague)
            },
            onTestSoundOnly = {
                engine.soundPlayer.playStadiumCheer()
            }
        )
    }

    // Tracker Match Detail Sheet (Timeline, Stats, Lineups)
    if (selectedTrackerMatch != null) {
        TrackerMatchDetailSheet(
            match = selectedTrackerMatch,
            sheetState = trackerDetailSheetState,
            onDismiss = { selectedTrackerMatch = null },
            onPlaySound = { engine.soundPlayer.playStadiumCheer() }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalAlertTopBar(
    isLiveActive: Boolean,
    onToggleLive: () -> Unit,
    onOpenTestSheet: () -> Unit,
    onQuickPlaySound: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(GoalNeonGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Logo",
                        tint = Color(0xFF003918),
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "GoalAlert",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary
                    )
                    Text(
                        text = "TOP 5 EUROPEAN LEAGUES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TrophyGold,
                        fontSize = 9.sp
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(
                onClick = onToggleLive,
                modifier = Modifier.testTag("toggle_live_button")
            ) {
                Icon(
                    imageVector = if (isLiveActive) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isLiveActive) "Pause Live Simulation" else "Resume Live Simulation",
                    tint = if (isLiveActive) GoalNeonGreen else TextSecondary
                )
            }
        },
        actions = {
            IconButton(
                onClick = onQuickPlaySound,
                modifier = Modifier.testTag("topbar_sound_button")
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Stadium Cheer Sound",
                    tint = TrophyGold
                )
            }

            Button(
                onClick = onOpenTestSheet,
                colors = ButtonDefaults.buttonColors(containerColor = GoalNeonGreen),
                modifier = Modifier
                    .padding(end = 8.dp)
                    .height(34.dp)
                    .testTag("topbar_test_alert_button"),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Campaign,
                    contentDescription = null,
                    tint = Color(0xFF003918),
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Test Alert",
                    color = Color(0xFF003918),
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = StadiumPitchDark
        )
    )
}

@Composable
fun TechnicalArchitectureCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(1.dp, StadiumBorder, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Code, contentDescription = null, tint = ElectricCyan)
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Real-Time Goal Architecture",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "1. Live API Data Sources:\n" +
                       "• API-Football (v3/fixtures?live=all&league=39,140,78,135,61)\n" +
                       "• Covers EPL (39), La Liga (140), Bundesliga (78), Serie A (135), Ligue 1 (61)\n\n" +
                       "2. Closed-App Push Trigger:\n" +
                       "• Server backend (Node.js/Go) subscribes to match event webhooks or polls live delta every 10s.\n" +
                       "• When event.type == 'Goal', server formats payload (scorer, assist, minute, score) and dispatches to FCM / Expo Push API with sound: 'stadium_cheer.wav'.\n\n" +
                       "3. Loud Audio & Bypass Silent Mode:\n" +
                       "• Android: Uses NotificationChannel with IMPORTANCE_HIGH, AudioAttributes USAGE_NOTIFICATION_EVENT, and res/raw/stadium_cheer.wav.\n" +
                       "• iOS: In app.json plugins, register notification custom sound file and set interruptionLevel to 'timeSensitive' (or Critical Alerts if entitled).",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                lineHeight = 18.sp
            )
        }
    }
}
