package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.model.LeagueId
import com.example.ui.theme.GoalNeonGreen
import com.example.ui.theme.StadiumBorder
import com.example.ui.theme.StadiumSurfaceElevated
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun LeagueFilterChips(
    selectedLeague: LeagueId,
    onSelectLeague: (LeagueId) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        LeagueId.values().forEach { league ->
            val isSelected = league == selectedLeague
            val flagEmoji = when (league) {
                LeagueId.ALL -> "🏆"
                LeagueId.UCL -> "⭐"
                LeagueId.EPL -> "🏴󠁧󠁢󠁥󠁮󠁧󠁿"
                LeagueId.LA_LIGA -> "🇪🇸"
                LeagueId.BUNDESLIGA -> "🇩🇪"
                LeagueId.SERIE_A -> "🇮🇹"
                LeagueId.LIGUE_1 -> "🇫🇷"
            }

            FilterChip(
                selected = isSelected,
                onClick = { onSelectLeague(league) },
                label = {
                    Text(
                        text = "$flagEmoji ${league.displayName}",
                        style = MaterialTheme.typography.labelLarge,
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
                modifier = Modifier.testTag("filter_chip_${league.name.lowercase()}")
            )
        }
    }
}
