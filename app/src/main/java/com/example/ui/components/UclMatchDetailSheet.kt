package com.example.ui.components

import android.widget.Toast
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Team
import com.example.model.UclScheduleMatch
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
fun UclMatchDetailSheet(
    match: UclScheduleMatch,
    sheetState: SheetState,
    onDismiss: () -> Unit,
    onToggleAlert: (String) -> Unit,
    onAddToCalendar: (UclScheduleMatch) -> Unit,
    onPlayStadiumAnthem: () -> Unit
) {
    val context = LocalContext.current

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StadiumPitchDark,
        contentColor = TextPrimary,
        modifier = Modifier.testTag("ucl_match_detail_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 10.dp)
                .padding(bottom = 24.dp)
        ) {
            // Stage & Matchday Header Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Color(0xFF001489), Color(0xFF0D47A1))
                            )
                        )
                        .padding(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = null,
                            tint = TrophyGold,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "UEFA CHAMPIONS LEAGUE",
                            color = TrophyGold,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }

                Text(
                    text = match.matchdayLabel,
                    color = ElectricCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Teams Versus Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumSurfaceElevated),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        // Home Team
                        UclTeamBadgeColumn(team = match.homeTeam, isHome = true)

                        // Center VS & Timing
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFF001489))
                                    .border(1.dp, ElectricCyan.copy(alpha = 0.5f), CircleShape)
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "VS",
                                    color = Color.White,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = match.timeDisplay,
                                color = GoalNeonGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = match.dateDisplay,
                                color = TextSecondary,
                                fontSize = 12.sp
                            )
                        }

                        // Away Team
                        UclTeamBadgeColumn(team = match.awayTeam, isHome = false)
                    }

                    if (match.aggregateScore != null) {
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = match.aggregateScore,
                            color = TrophyGold,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Match Key Information
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = StadiumSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, StadiumBorder)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "EVENT DETAILS",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Venue
                    UclDetailRow(
                        icon = Icons.Default.LocationOn,
                        iconTint = GoalNeonGreen,
                        label = "Venue",
                        value = "${match.stadium}, ${match.city}"
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // TV Broadcaster
                    UclDetailRow(
                        icon = Icons.Default.Tv,
                        iconTint = ElectricCyan,
                        label = "Broadcast",
                        value = match.tvBroadcast
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Key Matchup
                    UclDetailRow(
                        icon = Icons.Default.SportsSoccer,
                        iconTint = TrophyGold,
                        label = "Key Duel",
                        value = match.keyDuel
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Head to Head
                    UclDetailRow(
                        icon = Icons.Default.History,
                        iconTint = Color(0xFFB388FF),
                        label = "H2H Record",
                        value = match.headToHead
                    )

                    if (match.referee.isNotBlank()) {
                        Spacer(modifier = Modifier.height(10.dp))
                        UclDetailRow(
                            icon = Icons.Default.Person,
                            iconTint = TextMuted,
                            label = "Referee",
                            value = match.referee
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Add to Calendar
                Button(
                    onClick = {
                        onAddToCalendar(match)
                        Toast.makeText(context, "Opening Calendar...", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_add_to_calendar"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF001489))
                ) {
                    Icon(
                        Icons.Default.CalendarMonth,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = ElectricCyan
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Add to Calendar", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }

                // Match Reminder Alert Toggle
                OutlinedButton(
                    onClick = {
                        val enabled = !match.isAlertEnabled
                        onToggleAlert(match.id)
                        if (enabled) {
                            onPlayStadiumAnthem()
                            Toast.makeText(
                                context,
                                "🔔 Goal Alert set for ${match.homeTeam.shortName} vs ${match.awayTeam.shortName}!",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(context, "Alert removed", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(48.dp)
                        .testTag("btn_toggle_match_alert"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = if (match.isAlertEnabled) GoalNeonGreen.copy(alpha = 0.15f) else Color.Transparent
                    ),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (match.isAlertEnabled) GoalNeonGreen else StadiumBorder
                    )
                ) {
                    Icon(
                        if (match.isAlertEnabled) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp),
                        tint = if (match.isAlertEnabled) GoalNeonGreen else TextPrimary
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        if (match.isAlertEnabled) "Alert Active" else "Notify Me",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (match.isAlertEnabled) GoalNeonGreen else TextPrimary
                    )
                }
            }
        }
    }
}

@Composable
fun UclTeamBadgeColumn(team: Team, isHome: Boolean) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(90.dp)
    ) {
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(CircleShape)
                .background(Color(team.primaryColorHex))
                .border(2.dp, Color(team.secondaryColorHex), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = team.initials,
                color = if (team.name == "Real Madrid" || team.name == "Juventus") Color.Black else Color.White,
                fontWeight = FontWeight.ExtraBold,
                fontSize = 16.sp
            )
        }
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = team.shortName,
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            maxLines = 2
        )
        Text(
            text = if (isHome) "Home" else "Away",
            color = TextMuted,
            fontSize = 11.sp
        )
    }
}

@Composable
fun UclDetailRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconTint: Color,
    label: String,
    value: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = iconTint,
            modifier = Modifier
                .size(18.dp)
                .padding(top = 2.dp)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = label,
                color = TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
