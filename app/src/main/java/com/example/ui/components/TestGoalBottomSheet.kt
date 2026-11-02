package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LeagueId
import com.example.model.Match
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.GoalNeonGreen
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumPitchDark
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TrophyGold

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestGoalBottomSheet(
    sheetState: SheetState,
    matches: List<Match>,
    onDismiss: () -> Unit,
    onTriggerGoal: (matchId: String, scorer: String, assist: String?, isHome: Boolean) -> Unit,
    onTriggerRandom: () -> Unit,
    onTestSoundOnly: () -> Unit
) {
    var selectedMatchId by remember { mutableStateOf(matches.firstOrNull()?.id ?: "") }
    var isHomeTeam by remember { mutableStateOf(true) }
    var scorerInput by remember { mutableStateOf("Erling Haaland") }
    var assistInput by remember { mutableStateOf("Kevin De Bruyne") }

    val currentMatch = matches.find { it.id == selectedMatchId } ?: matches.firstOrNull()

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = StadiumPitchDark,
        tonalElevation = 6.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Campaign,
                        contentDescription = "Test Alert",
                        tint = TrophyGold,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Goal Notification Tester",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_test_sheet_button")
                ) {
                    Icon(Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Text(
                text = "Triggers an instantaneous loud stadium crowd roar sound and system push notification with goal minute, scorer, team & assist.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp)
            )

            // Fast Instant Random Button
            Button(
                onClick = {
                    onTriggerRandom()
                    onDismiss()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("quick_random_goal_button"),
                colors = ButtonDefaults.buttonColors(containerColor = GoalNeonGreen),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ElectricBolt,
                    contentDescription = "Random Goal",
                    tint = Color(0xFF003918)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "⚡ TRIGGER RANDOM GOAL ALERT",
                    fontWeight = FontWeight.Black,
                    color = Color(0xFF003918),
                    fontSize = 15.sp
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Just test sound button
            Button(
                onClick = onTestSoundOnly,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp)
                    .testTag("preview_stadium_sound_button"),
                colors = ButtonDefaults.buttonColors(containerColor = StadiumSurfaceElevated),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.VolumeUp,
                    contentDescription = "Play Audio",
                    tint = TrophyGold
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Preview Stadium Sound Only",
                    color = TextPrimary,
                    style = MaterialTheme.typography.labelLarge
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "CUSTOM GOAL PARAMETERS",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = TrophyGold,
                letterSpacing = 1.sp
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Match selector
            Text(
                text = "Select Match:",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))

            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                matches.take(4).forEach { match ->
                    val isSelected = match.id == selectedMatchId
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSelected) Color(0xFF003918) else StadiumSurfaceElevated)
                            .border(
                                1.dp,
                                if (isSelected) GoalNeonGreen else StadiumBorder,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedMatchId = match.id }
                            .padding(10.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${match.homeTeam.name} vs ${match.awayTeam.name} (${match.league.shortCode})",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) GoalNeonGreen else TextPrimary
                            )
                            if (isSelected) {
                                Icon(
                                    Icons.Default.Check,
                                    contentDescription = "Selected",
                                    tint = GoalNeonGreen,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Scoring team choice
            if (currentMatch != null) {
                Text(
                    text = "Scoring Team:",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isHomeTeam) Color(0xFF003918) else StadiumSurfaceElevated)
                            .border(1.dp, if (isHomeTeam) GoalNeonGreen else StadiumBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                isHomeTeam = true
                                scorerInput = if (currentMatch.homeTeam.name.contains("City")) "Erling Haaland" else "Kylian Mbappé"
                            }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentMatch.homeTeam.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isHomeTeam) GoalNeonGreen else TextPrimary
                        )
                    }

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (!isHomeTeam) Color(0xFF003918) else StadiumSurfaceElevated)
                            .border(1.dp, if (!isHomeTeam) GoalNeonGreen else StadiumBorder, RoundedCornerShape(8.dp))
                            .clickable {
                                isHomeTeam = false
                                scorerInput = if (currentMatch.awayTeam.name.contains("Arsenal")) "Bukayo Saka" else "Robert Lewandowski"
                            }
                            .padding(10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentMatch.awayTeam.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (!isHomeTeam) GoalNeonGreen else TextPrimary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scorer Name
            OutlinedTextField(
                value = scorerInput,
                onValueChange = { scorerInput = it },
                label = { Text("Scorer's Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_scorer_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoalNeonGreen,
                    unfocusedBorderColor = StadiumBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoalNeonGreen,
                    unfocusedLabelColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Assist Provider
            OutlinedTextField(
                value = assistInput,
                onValueChange = { assistInput = it },
                label = { Text("Assist Provider (optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_assist_name"),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = GoalNeonGreen,
                    unfocusedBorderColor = StadiumBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedLabelColor = GoalNeonGreen,
                    unfocusedLabelColor = TextSecondary
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (currentMatch != null) {
                        onTriggerGoal(
                            currentMatch.id,
                            scorerInput.ifBlank { "Striker" },
                            assistInput.ifBlank { null },
                            isHomeTeam
                        )
                        onDismiss()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("submit_custom_goal_button"),
                colors = ButtonDefaults.buttonColors(containerColor = ElectricCyan),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Fire Custom Goal Alert ⚽",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF00363F)
                )
            }
        }
    }
}
