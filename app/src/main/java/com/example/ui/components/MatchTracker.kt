package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.service.FavoritesManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
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
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoalNeonGreen
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumSurface
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

// Placeholder Data Models for the Match Tracker
enum class TrackerLeague(
    val leagueName: String,
    val country: String,
    val flag: String,
    val brandColor: Long
) {
    ALL("All Leagues", "Europe", "🏆", 0xFF6366F1),
    CHAMPIONS_LEAGUE("Champions League", "UEFA", "⭐", 0xFF001489),
    PREMIER_LEAGUE("Premier League", "England", "🏴󠁧󠁢󠁥󠁮󠁧󠁿", 0xFF3D195B),
    LA_LIGA("La Liga", "Spain", "🇪🇸", 0xFFEE1222),
    BUNDESLIGA("Bundesliga", "Germany", "🇩🇪", 0xFFD20515),
    SERIE_A("Serie A", "Italy", "🇮🇹", 0xFF008FD7),
    LIGUE_1("Ligue 1", "France", "🇫🇷", 0xFF091C3E)
}

data class TrackerTeam(
    val name: String,
    val code: String,
    val primaryColorHex: Long,
    val textColorHex: Long = 0xFFFFFFFF
)

data class TrackerMatchItem(
    val id: String,
    val league: TrackerLeague,
    val homeTeam: TrackerTeam,
    val awayTeam: TrackerTeam,
    val homeScore: Int,
    val awayScore: Int,
    val minute: Int,
    val isLive: Boolean = true,
    val statusText: String = "LIVE",
    val stadium: String,
    val possessionHome: Int = 50,
    val latestGoalEvent: String? = null
)

object MatchTrackerPlaceholderData {
    val matches = listOf(
        // UEFA Champions League
        TrackerMatchItem(
            id = "ucl_1",
            league = TrackerLeague.CHAMPIONS_LEAGUE,
            homeTeam = TrackerTeam("Real Madrid", "RMA", 0xFFFFFFFF, 0xFF1C2C5B),
            awayTeam = TrackerTeam("Manchester City", "MCI", 0xFF6CABDD, 0xFFFFFFFF),
            homeScore = 3,
            awayScore = 3,
            minute = 86,
            stadium = "Santiago Bernabéu, Madrid",
            possessionHome = 48,
            latestGoalEvent = "79' Federico Valverde (Volley)"
        ),
        TrackerMatchItem(
            id = "ucl_2",
            league = TrackerLeague.CHAMPIONS_LEAGUE,
            homeTeam = TrackerTeam("Bayern Munich", "FCB", 0xFFDC052D, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Paris Saint-Germain", "PSG", 0xFF004170, 0xFFFFFFFF),
            homeScore = 2,
            awayScore = 1,
            minute = 74,
            stadium = "Allianz Arena, Munich",
            possessionHome = 55,
            latestGoalEvent = "68' Jamal Musiala (Solo run)"
        ),

        // English Premier League
        TrackerMatchItem(
            id = "pl_1",
            league = TrackerLeague.PREMIER_LEAGUE,
            homeTeam = TrackerTeam("Manchester City", "MCI", 0xFF6CABDD, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Arsenal", "ARS", 0xFFEF0107, 0xFFFFFFFF),
            homeScore = 2,
            awayScore = 1,
            minute = 67,
            stadium = "Etihad Stadium, Manchester",
            possessionHome = 58,
            latestGoalEvent = "64' Phil Foden (Assist: Bernardo Silva)"
        ),
        TrackerMatchItem(
            id = "pl_2",
            league = TrackerLeague.PREMIER_LEAGUE,
            homeTeam = TrackerTeam("Liverpool", "LIV", 0xFFC8102E, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Chelsea", "CHE", 0xFF034694, 0xFFFFFFFF),
            homeScore = 1,
            awayScore = 0,
            minute = 32,
            stadium = "Anfield, Liverpool",
            possessionHome = 55,
            latestGoalEvent = "29' Mohamed Salah (Penalty)"
        ),

        // Spanish La Liga
        TrackerMatchItem(
            id = "laliga_1",
            league = TrackerLeague.LA_LIGA,
            homeTeam = TrackerTeam("Real Madrid", "RMA", 0xFFFFFFFF, 0xFF1C2C5B),
            awayTeam = TrackerTeam("Barcelona", "FCB", 0xFF004D98, 0xFFFFFFFF),
            homeScore = 3,
            awayScore = 2,
            minute = 78,
            stadium = "Santiago Bernabéu, Madrid",
            possessionHome = 51,
            latestGoalEvent = "75' Jude Bellingham (Assist: Valverde)"
        ),
        TrackerMatchItem(
            id = "laliga_2",
            league = TrackerLeague.LA_LIGA,
            homeTeam = TrackerTeam("Atlético Madrid", "ATM", 0xFFCB3524, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Real Sociedad", "RSO", 0xFF0067B1, 0xFFFFFFFF),
            homeScore = 1,
            awayScore = 0,
            minute = 54,
            stadium = "Metropolitano Stadium, Madrid",
            possessionHome = 48,
            latestGoalEvent = "39' Antoine Griezmann"
        ),

        // German Bundesliga
        TrackerMatchItem(
            id = "bun_1",
            league = TrackerLeague.BUNDESLIGA,
            homeTeam = TrackerTeam("Bayern Munich", "FCB", 0xFFDC052D, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Bayer Leverkusen", "B04", 0xFFE32221, 0xFFFFFFFF),
            homeScore = 1,
            awayScore = 1,
            minute = 58,
            stadium = "Allianz Arena, Munich",
            possessionHome = 62,
            latestGoalEvent = "43' Harry Kane (Assist: Musiala)"
        ),
        TrackerMatchItem(
            id = "bun_2",
            league = TrackerLeague.BUNDESLIGA,
            homeTeam = TrackerTeam("Borussia Dortmund", "BVB", 0xFFFDE100, 0xFF000000),
            awayTeam = TrackerTeam("RB Leipzig", "RBL", 0xFFDD0741, 0xFFFFFFFF),
            homeScore = 2,
            awayScore = 2,
            minute = 84,
            stadium = "Signal Iduna Park, Dortmund",
            possessionHome = 50,
            latestGoalEvent = "79' Serhou Guirassy"
        ),

        // Italian Serie A
        TrackerMatchItem(
            id = "sea_1",
            league = TrackerLeague.SERIE_A,
            homeTeam = TrackerTeam("Inter Milan", "INT", 0xFF005395, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Juventus", "JUV", 0xFFFFFFFF, 0xFF000000),
            homeScore = 2,
            awayScore = 0,
            minute = 83,
            stadium = "San Siro, Milan",
            possessionHome = 54,
            latestGoalEvent = "61' Marcus Thuram (Assist: Dimarco)"
        ),
        TrackerMatchItem(
            id = "sea_2",
            league = TrackerLeague.SERIE_A,
            homeTeam = TrackerTeam("AC Milan", "ACM", 0xFFFB090B, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Napoli", "NAP", 0xFF0080FF, 0xFFFFFFFF),
            homeScore = 1,
            awayScore = 1,
            minute = 45,
            statusText = "HT",
            stadium = "San Siro, Milan",
            possessionHome = 47,
            latestGoalEvent = "34' Christian Pulisic"
        ),

        // French Ligue 1
        TrackerMatchItem(
            id = "l1_1",
            league = TrackerLeague.LIGUE_1,
            homeTeam = TrackerTeam("Paris Saint-Germain", "PSG", 0xFF004170, 0xFFFFFFFF),
            awayTeam = TrackerTeam("AS Monaco", "ASM", 0xFFE31B23, 0xFFFFFFFF),
            homeScore = 3,
            awayScore = 1,
            minute = 61,
            stadium = "Parc des Princes, Paris",
            possessionHome = 66,
            latestGoalEvent = "58' Vitinha (Unassisted)"
        ),
        TrackerMatchItem(
            id = "l1_2",
            league = TrackerLeague.LIGUE_1,
            homeTeam = TrackerTeam("Marseille", "OM", 0xFF2FAEE0, 0xFFFFFFFF),
            awayTeam = TrackerTeam("Lyon", "OL", 0xFF002F6C, 0xFFFFFFFF),
            homeScore = 1,
            awayScore = 1,
            minute = 39,
            stadium = "Stade Vélodrome, Marseille",
            possessionHome = 52,
            latestGoalEvent = "28' Mason Greenwood"
        )
    )
}

/**
 * 'Match Tracker' UI Component
 * Displays a live list of matches across the top 5 European leagues with league filters,
 * live clocks, scores, possession meter, and goal updates.
 */
@Composable
fun MatchTracker(
    matches: List<TrackerMatchItem> = MatchTrackerPlaceholderData.matches,
    onMatchClick: (TrackerMatchItem) -> Unit = {},
    onTriggerFavoriteGoal: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val favoritesManager = remember { FavoritesManager(context) }
    val favoriteMatchIds by favoritesManager.favoriteIds.collectAsStateWithLifecycle()
    var selectedLeague by remember { mutableStateOf(TrackerLeague.ALL) }
    var showFavoritesOnly by remember { mutableStateOf(false) }

    // Prioritize favorites to stay at the very top of the list!
    val sortedMatches = remember(matches, selectedLeague, showFavoritesOnly, favoriteMatchIds) {
        val base = if (selectedLeague == TrackerLeague.ALL) matches
        else matches.filter { it.league == selectedLeague }

        val subset = if (showFavoritesOnly) base.filter { favoriteMatchIds.contains(it.id) } else base

        subset.sortedWith(
            compareByDescending<TrackerMatchItem> { favoriteMatchIds.contains(it.id) }
                .thenByDescending { it.minute }
        )
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .testTag("match_tracker_component")
    ) {
        // Section Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.SportsSoccer,
                    contentDescription = null,
                    tint = GoalNeonGreen,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "MATCH TRACKER",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Black,
                    color = TextPrimary,
                    letterSpacing = 1.sp
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quick Favorites Toggle Chip
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(if (showFavoritesOnly) TrophyGold.copy(alpha = 0.25f) else StadiumSurfaceElevated)
                        .border(
                            1.dp,
                            if (showFavoritesOnly) TrophyGold else StadiumBorder,
                            RoundedCornerShape(8.dp)
                        )
                        .clickable { showFavoritesOnly = !showFavoritesOnly }
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "⭐ ${favoriteMatchIds.size} PINNED",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = if (showFavoritesOnly) TrophyGold else TextSecondary
                    )
                }

                if (onTriggerFavoriteGoal != null) {
                    // Fast Test Favorite Goal Sound
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(TrophyGold)
                            .clickable(onClick = onTriggerFavoriteGoal)
                            .padding(horizontal = 8.dp, vertical = 3.dp)
                            .testTag("test_fav_goal_sound_btn")
                    ) {
                        Text(
                            text = "🔊 TEST GOAL",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF422C00)
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFF003918))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = "${sortedMatches.size} MATCHES",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = GoalNeonGreen
                    )
                }
            }
        }

        // Horizontal League Filter Chips
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TrackerLeague.values().forEach { league ->
                val isSelected = league == selectedLeague
                FilterChip(
                    selected = isSelected,
                    onClick = { selectedLeague = league },
                    label = {
                        Text(
                            text = "${league.flag} ${league.leagueName}",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                            color = if (isSelected) Color(0xFF003918) else TextPrimary
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = GoalNeonGreen,
                        containerColor = StadiumSurfaceElevated,
                        selectedLabelColor = Color(0xFF003918),
                        labelColor = TextSecondary
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isSelected,
                        borderColor = if (isSelected) GoalNeonGreen else StadiumBorder,
                        selectedBorderColor = GoalNeonGreen,
                        borderWidth = 1.dp
                    ),
                    modifier = Modifier.testTag("tracker_league_${league.name.lowercase()}")
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        // Match Items List
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            sortedMatches.forEach { match ->
                val isFav = favoriteMatchIds.contains(match.id)
                TrackerMatchCard(
                    match = match,
                    isFavorite = isFav,
                    onToggleFavorite = {
                        favoritesManager.toggleFavorite(match.id)
                    },
                    onClick = { onMatchClick(match) }
                )
            }
        }
    }
}

@Composable
fun TrackerMatchCard(
    match: TrackerMatchItem,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
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

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                1.dp,
                if (isFavorite) TrophyGold.copy(alpha = 0.75f) else StadiumBorder,
                RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .testTag("tracker_match_${match.id}"),
        colors = CardDefaults.cardColors(containerColor = StadiumSurface),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isFavorite) 4.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Header Row: Favorite Star + League Flag + Status/Minute
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Favorite Star Icon Button
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .size(32.dp)
                            .testTag("favorite_btn_${match.id}")
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = if (isFavorite) "Remove from Priority" else "Mark as Priority Favorite",
                            tint = if (isFavorite) TrophyGold else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Color(match.league.brandColor).copy(alpha = 0.35f))
                            .padding(horizontal = 8.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "${match.league.flag} ${match.league.leagueName}",
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                    }

                    if (isFavorite) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .background(TrophyGold.copy(alpha = 0.2f))
                                .border(1.dp, TrophyGold.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                                .padding(horizontal = 5.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "★ PINNED",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Black,
                                color = TrophyGold,
                                fontSize = 8.sp
                            )
                        }
                    }
                }

                // Live Clock Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(
                            if (match.isLive) Color(0xFF003918)
                            else StadiumSurfaceElevated
                        )
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
                            text = "${match.minute}'",
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

            Spacer(modifier = Modifier.height(10.dp))

            // Scoreboard Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Home Team
                TrackerTeamBadge(
                    team = match.homeTeam,
                    isHome = true,
                    modifier = Modifier.weight(1f)
                )

                // Score Display
                Box(
                    modifier = Modifier
                        .padding(horizontal = 10.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(StadiumSurfaceElevated)
                        .border(1.dp, StadiumBorder, RoundedCornerShape(10.dp))
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${match.homeScore} - ${match.awayScore}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Black,
                        color = TextPrimary,
                        letterSpacing = 2.sp
                    )
                }

                // Away Team
                TrackerTeamBadge(
                    team = match.awayTeam,
                    isHome = false,
                    modifier = Modifier.weight(1f)
                )
            }

            // Latest Goal Flash (if any)
            if (!match.latestGoalEvent.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StadiumSurfaceElevated)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.SportsSoccer,
                        contentDescription = "Goal Event",
                        tint = TrophyGold,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = match.latestGoalEvent,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Possession Progress Bar
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Possession: ${match.possessionHome}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                    Text(
                        text = "${100 - match.possessionHome}%",
                        style = MaterialTheme.typography.labelSmall,
                        color = TextMuted,
                        fontSize = 10.sp
                    )
                }
                Spacer(modifier = Modifier.height(3.dp))
                LinearProgressIndicator(
                    progress = { match.possessionHome / 100f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp)),
                    color = GoalNeonGreen,
                    trackColor = ElectricCyan
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Stadium Footer
            Text(
                text = "📍 ${match.stadium}",
                style = MaterialTheme.typography.labelSmall,
                color = TextMuted,
                fontSize = 10.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun TrackerTeamBadge(
    team: TrackerTeam,
    isHome: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = if (isHome) Arrangement.Start else Arrangement.End
    ) {
        if (!isHome) {
            Text(
                text = team.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.End,
                modifier = Modifier.weight(1f, fill = false)
            )
            Spacer(modifier = Modifier.width(8.dp))
        }

        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Color(team.primaryColorHex)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team.code,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Black,
                color = Color(team.textColorHex)
            )
        }

        if (isHome) {
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = team.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Start,
                modifier = Modifier.weight(1f, fill = false)
            )
        }
    }
}
