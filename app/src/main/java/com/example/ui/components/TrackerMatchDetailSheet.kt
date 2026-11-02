package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FormatListNumbered
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.PrimaryTabRow
import androidx.compose.material3.SheetState
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MatchDetailsDataProvider
import com.example.model.Player
import com.example.model.TeamLineup
import com.example.model.TimelineEvent
import com.example.model.TimelineEventType
import com.example.ui.theme.CardRed
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoalNeonGreen
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumPitchDark
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackerMatchDetailSheet(
    match: TrackerMatchItem?,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onPlaySound: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (match == null) return

    var selectedDetailTab by remember { mutableIntStateOf(0) } // 0: Timeline, 1: Stats, 2: Lineups

    val stats = remember(match) { MatchDetailsDataProvider.getStatsForMatch(match) }
    val timelineEvents = remember(match) { MatchDetailsDataProvider.getTimelineEventsForMatch(match) }
    val (homeLineup, awayLineup) = remember(match) { MatchDetailsDataProvider.getLineupsForMatch(match) }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alphaAnim by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "liveAlpha"
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StadiumPitchDark,
        tonalElevation = 8.dp,
        modifier = modifier.testTag("tracker_match_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 36.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Top Bar: League & Close
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(match.league.brandColor).copy(alpha = 0.35f))
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        Text(
                            text = "${match.league.flag} ${match.league.leagueName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Live Pill
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .background(if (match.isLive) Color(0xFF003918) else StadiumSurfaceElevated)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                    ) {
                        if (match.isLive) {
                            Box(
                                modifier = Modifier
                                    .size(8.dp)
                                    .clip(CircleShape)
                                    .background(GoalNeonGreen)
                                    .alpha(alphaAnim)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "${match.minute}' LIVE",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = GoalNeonGreen
                            )
                        } else {
                            Text(
                                text = match.statusText,
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_tracker_detail_btn")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoreboard Card
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(16.dp))
                    .background(StadiumSurfaceElevated)
                    .border(1.dp, StadiumBorder, RoundedCornerShape(16.dp))
                    .padding(16.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Home Team
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(match.homeTeam.primaryColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = match.homeTeam.code,
                                    fontWeight = FontWeight.Black,
                                    color = Color(match.homeTeam.textColorHex),
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = match.homeTeam.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }

                        // Big Score
                        Text(
                            text = "${match.homeScore} - ${match.awayScore}",
                            style = MaterialTheme.typography.displaySmall,
                            fontWeight = FontWeight.Black,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )

                        // Away Team
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(Color(match.awayTeam.primaryColorHex)),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = match.awayTeam.code,
                                    fontWeight = FontWeight.Black,
                                    color = Color(match.awayTeam.textColorHex),
                                    fontSize = 13.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = match.awayTeam.name,
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary,
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "🏟️ ${match.stadium}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextMuted,
                            modifier = Modifier.weight(1f)
                        )

                        Button(
                            onClick = onPlaySound,
                            colors = ButtonDefaults.buttonColors(containerColor = TrophyGold),
                            contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 2.dp),
                            modifier = Modifier.height(28.dp)
                        ) {
                            Icon(Icons.Default.VolumeUp, contentDescription = null, tint = Color(0xFF422C00), modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stadium Roar", color = Color(0xFF422C00), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sub-Tabs: Timeline, Stats, Lineups
            PrimaryTabRow(
                selectedTabIndex = selectedDetailTab,
                containerColor = StadiumPitchDark,
                contentColor = GoalNeonGreen,
                indicator = {
                    TabRowDefaults.PrimaryIndicator(
                        modifier = Modifier.tabIndicatorOffset(selectedDetailTab),
                        color = GoalNeonGreen
                    )
                }
            ) {
                Tab(
                    selected = selectedDetailTab == 0,
                    onClick = { selectedDetailTab = 0 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Timeline, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Timeline", fontWeight = if (selectedDetailTab == 0) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.testTag("tab_detail_timeline")
                )
                Tab(
                    selected = selectedDetailTab == 1,
                    onClick = { selectedDetailTab = 1 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.QueryStats, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Stats", fontWeight = if (selectedDetailTab == 1) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.testTag("tab_detail_stats")
                )
                Tab(
                    selected = selectedDetailTab == 2,
                    onClick = { selectedDetailTab = 2 },
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Groups, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Lineups", fontWeight = if (selectedDetailTab == 2) FontWeight.Bold else FontWeight.Normal)
                        }
                    },
                    modifier = Modifier.testTag("tab_detail_lineups")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (selectedDetailTab) {
                0 -> {
                    // Timeline View (Events: Goals, Cards, Substitutions)
                    TimelineSection(
                        match = match,
                        events = timelineEvents
                    )
                }
                1 -> {
                    // Detailed Team Stats View
                    DetailedStatsSection(
                        match = match,
                        stats = stats
                    )
                }
                2 -> {
                    // Lineups & Formations View
                    LineupsSection(
                        homeTeam = match.homeTeam,
                        awayTeam = match.awayTeam,
                        homeLineup = homeLineup,
                        awayLineup = awayLineup
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineSection(
    match: TrackerMatchItem,
    events: List<TimelineEvent>
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        events.forEach { event ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(StadiumSurfaceElevated)
                    .padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Minute Badge
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(CircleShape)
                        .background(if (event.type == TimelineEventType.GOAL) Color(0xFF003918) else StadiumSurface),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = event.displayMinute,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (event.type == TimelineEventType.GOAL) GoalNeonGreen else TextPrimary
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Event Icon
                when (event.type) {
                    TimelineEventType.GOAL -> {
                        Icon(Icons.Default.SportsSoccer, contentDescription = "Goal", tint = TrophyGold, modifier = Modifier.size(18.dp))
                    }
                    TimelineEventType.YELLOW_CARD -> {
                        Box(
                            modifier = Modifier
                                .size(14.dp, 18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(TrophyGold)
                        )
                    }
                    TimelineEventType.RED_CARD -> {
                        Box(
                            modifier = Modifier
                                .size(14.dp, 18.dp)
                                .clip(RoundedCornerShape(2.dp))
                                .background(CardRed)
                        )
                    }
                    TimelineEventType.SUBSTITUTION -> {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.ArrowUpward, contentDescription = "Sub In", tint = GoalNeonGreen, modifier = Modifier.size(14.dp))
                            Icon(Icons.Default.ArrowDownward, contentDescription = "Sub Out", tint = CardRed, modifier = Modifier.size(14.dp))
                        }
                    }
                    TimelineEventType.PERIOD_START, TimelineEventType.PERIOD_END -> {
                        Text(text = "⏱️", fontSize = 14.sp)
                    }
                }

                Spacer(modifier = Modifier.width(10.dp))

                // Event Description
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = event.primaryPlayer,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    if (!event.secondaryPlayer.isNullOrBlank()) {
                        Text(
                            text = if (event.type == TimelineEventType.GOAL) "Assist: ${event.secondaryPlayer}" else "Off: ${event.secondaryPlayer}",
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary
                        )
                    }
                    if (!event.detail.isNullOrBlank()) {
                        Text(
                            text = event.detail,
                            style = MaterialTheme.typography.labelSmall,
                            color = GoalNeonGreen,
                            fontSize = 11.sp
                        )
                    }
                }

                // Team Indicator Pill
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(StadiumSurface)
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = if (event.isHomeTeam) match.homeTeam.code else match.awayTeam.code,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = TextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailedStatsSection(
    match: TrackerMatchItem,
    stats: com.example.model.MatchDetailedStats
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatComparisonBar(title = "Ball Possession", homeVal = "${stats.possessionHome}%", awayVal = "${stats.possessionAway}%", homeRatio = stats.possessionHome / 100f)
        StatComparisonBar(title = "Expected Goals (xG)", homeVal = "%.2f".format(stats.xGHome), awayVal = "%.2f".format(stats.xGAway), homeRatio = (stats.xGHome / (stats.xGHome + stats.xGAway)).toFloat())
        StatComparisonBar(title = "Shots on Target", homeVal = "${stats.shotsOnTargetHome}", awayVal = "${stats.shotsOnTargetAway}", homeRatio = stats.shotsOnTargetHome.toFloat() / (stats.shotsOnTargetHome + stats.shotsOnTargetAway))
        StatComparisonBar(title = "Total Shots", homeVal = "${stats.totalShotsHome}", awayVal = "${stats.totalShotsAway}", homeRatio = stats.totalShotsHome.toFloat() / (stats.totalShotsHome + stats.totalShotsAway))
        StatComparisonBar(title = "Corner Kicks", homeVal = "${stats.cornerKicksHome}", awayVal = "${stats.cornerKicksAway}", homeRatio = stats.cornerKicksHome.toFloat() / (stats.cornerKicksHome + stats.cornerKicksAway))
        StatComparisonBar(title = "Pass Accuracy", homeVal = "${stats.passAccuracyHome}%", awayVal = "${stats.passAccuracyAway}%", homeRatio = stats.passAccuracyHome / 100f)
        StatComparisonBar(title = "Fouls Committed", homeVal = "${stats.foulsHome}", awayVal = "${stats.foulsAway}", homeRatio = stats.foulsHome.toFloat() / (stats.foulsHome + stats.foulsAway))
        StatComparisonBar(title = "Yellow Cards", homeVal = "${stats.yellowCardsHome}", awayVal = "${stats.yellowCardsAway}", homeRatio = if (stats.yellowCardsHome + stats.yellowCardsAway > 0) stats.yellowCardsHome.toFloat() / (stats.yellowCardsHome + stats.yellowCardsAway) else 0.5f)
    }
}

@Composable
private fun StatComparisonBar(
    title: String,
    homeVal: String,
    awayVal: String,
    homeRatio: Float
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceElevated)
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = homeVal, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = GoalNeonGreen)
                Text(text = title, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                Text(text = awayVal, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = ElectricCyan)
            }
            Spacer(modifier = Modifier.height(4.dp))
            LinearProgressIndicator(
                progress = { homeRatio.coerceIn(0.05f, 0.95f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = GoalNeonGreen,
                trackColor = ElectricCyan
            )
        }
    }
}

@Composable
private fun LineupsSection(
    homeTeam: TrackerTeam,
    awayTeam: TrackerTeam,
    homeLineup: TeamLineup,
    awayLineup: TeamLineup
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Formation Summary
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(10.dp))
                .background(StadiumSurfaceElevated)
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(horizontalAlignment = Alignment.Start) {
                Text(text = homeTeam.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "Formation: ${homeLineup.formation}", style = MaterialTheme.typography.labelSmall, color = GoalNeonGreen)
                Text(text = "Manager: ${homeLineup.manager}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(text = awayTeam.name, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(text = "Formation: ${awayLineup.formation}", style = MaterialTheme.typography.labelSmall, color = ElectricCyan)
                Text(text = "Manager: ${awayLineup.manager}", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
        }

        // Starting XI List
        Text(
            text = "STARTING XI",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TrophyGold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Home Starting XI
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(StadiumSurfaceElevated)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                homeLineup.startingXI.forEach { player ->
                    PlayerRow(player = player, teamColor = GoalNeonGreen)
                }
            }

            // Away Starting XI
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(StadiumSurfaceElevated)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                awayLineup.startingXI.forEach { player ->
                    PlayerRow(player = player, teamColor = ElectricCyan)
                }
            }
        }

        // Substitutes Bench
        Text(
            text = "BENCH / SUBSTITUTES",
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Bold,
            color = TrophyGold,
            letterSpacing = 1.sp
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(StadiumSurfaceElevated)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                homeLineup.substitutes.forEach { player ->
                    PlayerRow(player = player, teamColor = TextSecondary)
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(10.dp))
                    .background(StadiumSurfaceElevated)
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                awayLineup.substitutes.forEach { player ->
                    PlayerRow(player = player, teamColor = TextSecondary)
                }
            }
        }
    }
}

@Composable
private fun PlayerRow(
    player: Player,
    teamColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "${player.number}",
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            color = teamColor,
            modifier = Modifier.width(20.dp)
        )
        Text(
            text = player.name + if (player.isCaptain) " (C)" else "",
            style = MaterialTheme.typography.bodySmall,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = player.position,
            style = MaterialTheme.typography.labelSmall,
            color = TextMuted,
            fontSize = 9.sp
        )
    }
}
