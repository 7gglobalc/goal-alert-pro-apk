package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.UclScheduleRepository
import com.example.model.Team
import com.example.model.UclCalendarDay
import com.example.model.UclScheduleMatch
import com.example.model.UclStage
import com.example.ui.components.UclCalendarStripView
import com.example.ui.components.UclMatchDetailSheet
import androidx.compose.material.icons.filled.DateRange
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
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UclScheduleScreen(
    repository: UclScheduleRepository,
    onPreviewSound: () -> Unit = {}
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val matches by repository.matches.collectAsStateWithLifecycle()
    val isLoading by repository.isLoading.collectAsStateWithLifecycle()
    val error by repository.error.collectAsStateWithLifecycle()

    var selectedStage by remember { mutableStateOf(UclStage.ALL) }
    var selectedDateKey by remember { mutableStateOf<String?>(null) }
    var searchQuery by remember { mutableStateOf("") }
    var selectedMatchForDetail by remember { mutableStateOf<UclScheduleMatch?>(null) }
    val detailSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    // Compute Calendar Days from all matches
    val calendarDays = remember(matches) {
        matches.groupBy { it.dateDisplay }.map { (dateKey, matchList) ->
            val first = matchList.first()
            UclCalendarDay(
                dateKey = dateKey,
                dayOfWeek = first.dayOfWeek,
                dayOfMonth = first.dayOfMonth,
                monthShort = first.monthShort,
                matchCount = matchList.size,
                stage = first.stage
            )
        }
    }

    // Filter matches according to selected date, stage, and search query
    val filteredMatches = remember(matches, selectedDateKey, selectedStage, searchQuery) {
        matches.filter { match ->
            val dateMatch = selectedDateKey == null || match.dateDisplay == selectedDateKey
            val stageMatch = selectedStage == UclStage.ALL || match.stage == selectedStage
            val q = searchQuery.trim()
            val queryMatch = q.isBlank() ||
                    match.homeTeam.name.contains(q, ignoreCase = true) ||
                    match.homeTeam.shortName.contains(q, ignoreCase = true) ||
                    match.homeTeam.initials.equals(q, ignoreCase = true) ||
                    match.awayTeam.name.contains(q, ignoreCase = true) ||
                    match.awayTeam.shortName.contains(q, ignoreCase = true) ||
                    match.awayTeam.initials.equals(q, ignoreCase = true) ||
                    match.stadium.contains(q, ignoreCase = true) ||
                    match.city.contains(q, ignoreCase = true) ||
                    match.matchdayLabel.contains(q, ignoreCase = true) ||
                    match.keyDuel.contains(q, ignoreCase = true) ||
                    match.stage.displayName.contains(q, ignoreCase = true)
            dateMatch && stageMatch && queryMatch
        }
    }

    // Group filtered matches by date
    val groupedMatches = remember(filteredMatches) {
        filteredMatches.groupBy { it.dateDisplay }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(StadiumPitchDark)
            .testTag("ucl_schedule_screen")
    ) {
        // Tournament Hero Banner
        UclTournamentHeader(
            isLoading = isLoading,
            totalMatches = filteredMatches.size,
            onRefresh = {
                scope.launch {
                    repository.refreshSchedule()
                    Toast.makeText(context, "UEFA Champions League calendar refreshed", Toast.LENGTH_SHORT).show()
                }
            }
        )

        // Interactive Matchday Calendar Date Strip
        UclCalendarStripView(
            calendarDays = calendarDays,
            selectedDateKey = selectedDateKey,
            onSelectDate = { selectedDateKey = it }
        )

        // Active Date Indicator Banner
        if (selectedDateKey != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 2.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Color(0xFF001489).copy(alpha = 0.5f))
                    .border(1.dp, ElectricCyan.copy(alpha = 0.7f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = null,
                            tint = TrophyGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Date: $selectedDateKey",
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "(${filteredMatches.size} ${if (filteredMatches.size == 1) "match" else "matches"})",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }

                    Text(
                        text = "Show All",
                        color = ElectricCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clickable { selectedDateKey = null }
                            .padding(4.dp)
                            .testTag("btn_clear_date_filter")
                    )
                }
            }
        }

        // Search Bar for finding favorite teams and matches
        UclScheduleSearchBar(
            searchQuery = searchQuery,
            onSearchQueryChange = { searchQuery = it },
            matchCount = filteredMatches.size,
            onSelectQuickTeam = { team ->
                searchQuery = if (searchQuery.equals(team, ignoreCase = true)) "" else team
            }
        )

        // Stage Filter Chips
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(UclStage.entries) { stage ->
                val isSelected = selectedStage == stage
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedStage = stage },
                    label = {
                        Text(
                            text = stage.displayName,
                            fontSize = 12.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    leadingIcon = {
                        if (stage == UclStage.FINAL) {
                            Icon(Icons.Default.EmojiEvents, contentDescription = null, tint = TrophyGold, modifier = Modifier.size(14.dp))
                        }
                    },
                    shape = RoundedCornerShape(20.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = StadiumSurface,
                        labelColor = TextSecondary,
                        selectedContainerColor = Color(0xFF001489),
                        selectedLabelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = StadiumBorder,
                        selectedBorderColor = ElectricCyan
                    ),
                    modifier = Modifier.testTag("filter_stage_${stage.shortCode.lowercase()}")
                )
            }
        }

        // Match List or Empty / Loading State
        Box(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f)
        ) {
            if (isLoading && matches.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    CircularProgressIndicator(color = ElectricCyan, modifier = Modifier.size(36.dp))
                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Fetching UEFA Champions League schedule...", color = TextSecondary, fontSize = 13.sp)
                }
            } else if (filteredMatches.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.size(54.dp),
                        tint = TrophyGold.copy(alpha = 0.5f)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "No matches found for \"$searchQuery\"" else "No fixtures match your selection",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (searchQuery.isNotBlank()) "Check the spelling or try selecting a popular team from the chips above." else "Try clearing your filters or selecting 'All Matches'.",
                        color = TextMuted,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = {
                            searchQuery = ""
                            selectedDateKey = null
                            selectedStage = UclStage.ALL
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoalNeonGreen),
                        modifier = Modifier.testTag("btn_clear_search_empty_state")
                    ) {
                        Text("Clear Search & Filters", color = Color(0xFF003918), fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 32.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    groupedMatches.forEach { (dateHeader, matchesOnDate) ->
                        item(key = "header_$dateHeader") {
                            UclDateHeader(date = dateHeader, count = matchesOnDate.size)
                        }

                        items(matchesOnDate, key = { it.id }) { match ->
                            UclScheduleCard(
                                match = match,
                                onClick = { selectedMatchForDetail = match },
                                onToggleAlert = {
                                    val enabled = repository.toggleAlert(match.id)
                                    if (enabled) {
                                        onPreviewSound()
                                        Toast.makeText(
                                            context,
                                            "🔔 Alert set for ${match.homeTeam.shortName} vs ${match.awayTeam.shortName}",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Toast.makeText(context, "Alert removed", Toast.LENGTH_SHORT).show()
                                    }
                                },
                                onAddToCalendar = {
                                    repository.addMatchToDeviceCalendar(context, match)
                                    Toast.makeText(context, "Adding to calendar...", Toast.LENGTH_SHORT).show()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // Modal Bottom Sheet for Match Details
    selectedMatchForDetail?.let { match ->
        UclMatchDetailSheet(
            match = match,
            sheetState = detailSheetState,
            onDismiss = { selectedMatchForDetail = null },
            onToggleAlert = { matchId ->
                repository.toggleAlert(matchId)
                selectedMatchForDetail = repository.matches.value.find { it.id == matchId }
            },
            onAddToCalendar = { targetMatch ->
                repository.addMatchToDeviceCalendar(context, targetMatch)
            },
            onPlayStadiumAnthem = onPreviewSound
        )
    }
}

@Composable
fun UclTournamentHeader(
    isLoading: Boolean,
    totalMatches: Int,
    onRefresh: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF001489))
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF001489).copy(alpha = 0.5f),
                            StadiumSurfaceElevated
                        )
                    )
                )
                .padding(16.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF001489))
                                .border(1.5.dp, TrophyGold, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                Icons.Default.EmojiEvents,
                                contentDescription = null,
                                tint = TrophyGold,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "UEFA CHAMPIONS LEAGUE",
                                color = TrophyGold,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp
                            )
                            Text(
                                text = "Complete Match Calendar",
                                color = TextPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    IconButton(
                        onClick = onRefresh,
                        modifier = Modifier
                            .size(36.dp)
                            .testTag("btn_refresh_ucl_schedule")
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = ElectricCyan,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                Icons.Default.Refresh,
                                contentDescription = "Refresh",
                                tint = ElectricCyan,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Fast Stats Pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    UclStatPill(label = "Fixtures", value = "$totalMatches Listed")
                    UclStatPill(label = "Format", value = "36 Teams")
                    UclStatPill(label = "Final Venue", value = "Munich 2025")
                }
            }
        }
    }
}

@Composable
fun UclStatPill(label: String, value: String) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(StadiumPitchDark.copy(alpha = 0.7f))
            .border(1.dp, StadiumBorder, RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Column {
            Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Medium)
            Text(text = value, color = ElectricCyan, fontSize = 11.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun UclDateHeader(date: String, count: Int) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Default.CalendarMonth,
                contentDescription = null,
                tint = GoalNeonGreen,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = date.uppercase(),
                color = GoalNeonGreen,
                fontSize = 12.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
        }
        Text(
            text = "$count ${if (count == 1) "Match" else "Matches"}",
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
fun UclScheduleCard(
    match: UclScheduleMatch,
    onClick: () -> Unit,
    onToggleAlert: () -> Unit,
    onAddToCalendar: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("ucl_match_card_${match.id}"),
        colors = CardDefaults.cardColors(containerColor = StadiumSurfaceElevated),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (match.isAlertEnabled) GoalNeonGreen.copy(alpha = 0.5f) else StadiumBorder
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Stage and Quick Actions Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF001489))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = match.matchdayLabel,
                        color = Color.White,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Add to Calendar Button
                    IconButton(
                        onClick = onAddToCalendar,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_add_cal_${match.id}")
                    ) {
                        Icon(
                            Icons.Default.CalendarMonth,
                            contentDescription = "Add to Calendar",
                            tint = ElectricCyan,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    // Alert Bell Toggle
                    IconButton(
                        onClick = onToggleAlert,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("btn_alert_${match.id}")
                    ) {
                        Icon(
                            imageVector = if (match.isAlertEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Toggle Goal Alert",
                            tint = if (match.isAlertEnabled) GoalNeonGreen else TextMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Date & Kickoff Time Banner
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = TrophyGold,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${match.dateDisplay}  •  ${match.timeDisplay}",
                    color = TrophyGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Teams Row (Horizontal)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(match.homeTeam.primaryColorHex))
                            .border(1.dp, Color(match.homeTeam.secondaryColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = match.homeTeam.initials,
                            color = if (match.homeTeam.name == "Real Madrid" || match.homeTeam.name == "Juventus") Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text(
                            text = match.homeTeam.shortName,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                        Text(
                            text = match.homeTeam.name,
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                // Center Time / VS Pill
                Box(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(StadiumPitchDark)
                        .border(1.dp, StadiumBorder, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "VS",
                            color = TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = match.timeDisplay,
                            color = GoalNeonGreen,
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 12.sp
                        )
                    }
                }

                // Away Team
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = match.awayTeam.shortName,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
                        Text(
                            text = match.awayTeam.name,
                            color = TextMuted,
                            fontSize = 11.sp,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(match.awayTeam.primaryColorHex))
                            .border(1.dp, Color(match.awayTeam.secondaryColorHex), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = match.awayTeam.initials,
                            color = if (match.awayTeam.name == "Real Madrid" || match.awayTeam.name == "Juventus") Color.Black else Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Venue & TV Broadcast Footer
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f, fill = false)
                ) {
                    Icon(
                        Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${match.stadium}, ${match.city}",
                        color = TextMuted,
                        fontSize = 11.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Tv,
                        contentDescription = null,
                        tint = ElectricCyan.copy(alpha = 0.8f),
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = match.tvBroadcast.split(",").firstOrNull()?.trim() ?: "UEFA TV",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun UclScheduleSearchBar(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    matchCount: Int,
    onSelectQuickTeam: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickTeams = remember {
        listOf(
            "Real Madrid",
            "Man City",
            "Arsenal",
            "Bayern Munich",
            "Barcelona",
            "Liverpool",
            "PSG",
            "Inter Milan",
            "Dortmund",
            "Bayer Leverkusen",
            "Juventus",
            "Atlético"
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("match_schedule_search_bar")
    ) {
        // Main Search Input
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp)
        ) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_search_teams"),
                placeholder = {
                    Text(
                        "Search favorite team, club or match (e.g. Real, Arsenal, City)...",
                        fontSize = 13.sp,
                        color = TextMuted
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = ElectricCyan,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(
                            onClick = { onSearchQueryChange("") },
                            modifier = Modifier.testTag("btn_clear_search")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = StadiumBorder,
                    focusedContainerColor = StadiumSurface,
                    unfocusedContainerColor = StadiumSurface,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                )
            )
        }

        // Quick Favorite Team Filter Pills
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 2.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            items(quickTeams) { team ->
                val isSelected = searchQuery.equals(team, ignoreCase = true)
                val chipBg = if (isSelected) Color(0xFF001489) else StadiumSurface
                val chipBorder = if (isSelected) ElectricCyan else StadiumBorder
                val chipTextColor = if (isSelected) TrophyGold else TextSecondary

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(chipBg)
                        .border(1.dp, chipBorder, RoundedCornerShape(16.dp))
                        .clickable { onSelectQuickTeam(team) }
                        .padding(horizontal = 10.dp, vertical = 5.dp)
                        .testTag("quick_team_${team.replace(" ", "_").lowercase()}")
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = if (isSelected) "★" else "☆",
                            color = if (isSelected) TrophyGold else TextMuted,
                            fontSize = 11.sp
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = team,
                            color = chipTextColor,
                            fontSize = 11.sp,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }

        // Search Active Indicator / Result Count
        if (searchQuery.isNotBlank()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Showing $matchCount ${if (matchCount == 1) "match" else "matches"} for \"$searchQuery\"",
                    color = TrophyGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Text(
                    text = "Clear",
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onSearchQueryChange("") }
                        .padding(4.dp)
                        .testTag("btn_clear_search_text")
                )
            }
        }
    }
}
