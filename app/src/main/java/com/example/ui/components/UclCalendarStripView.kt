package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.UclCalendarDay
import com.example.model.UclStage
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

@Composable
fun UclCalendarStripView(
    calendarDays: List<UclCalendarDay>,
    selectedDateKey: String?,
    onSelectDate: (String?) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .testTag("ucl_calendar_strip_view")
    ) {
        // Section Title with Calendar icon
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.CalendarMonth,
                    contentDescription = null,
                    tint = ElectricCyan,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "MATCHDAY CALENDAR",
                    color = ElectricCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )
            }

            if (selectedDateKey != null) {
                Text(
                    text = "Show All Dates",
                    color = TrophyGold,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clickable { onSelectDate(null) }
                        .padding(4.dp)
                        .testTag("btn_show_all_dates")
                )
            }
        }

        // Horizontal Calendar Date Strip
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // "ALL" Option Pill Card
            item {
                val isAllSelected = selectedDateKey == null
                val bgColor by animateColorAsState(
                    targetValue = if (isAllSelected) Color(0xFF001489) else StadiumSurface,
                    label = "all_bg_anim"
                )
                val borderColor by animateColorAsState(
                    targetValue = if (isAllSelected) ElectricCyan else StadiumBorder,
                    label = "all_border_anim"
                )

                Card(
                    modifier = Modifier
                        .width(68.dp)
                        .height(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectDate(null) }
                        .testTag("cal_day_all"),
                    colors = CardDefaults.cardColors(containerColor = bgColor),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, borderColor)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DateRange,
                            contentDescription = null,
                            tint = if (isAllSelected) TrophyGold else TextSecondary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "ALL",
                            color = if (isAllSelected) Color.White else TextPrimary,
                            fontWeight = FontWeight.Black,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "DATES",
                            color = if (isAllSelected) ElectricCyan else TextMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // Each Matchday Date Card
            items(calendarDays, key = { it.dateKey }) { day ->
                val isSelected = selectedDateKey == day.dateKey
                val cardBg by animateColorAsState(
                    targetValue = if (isSelected) Color(0xFF001F99) else StadiumSurfaceElevated,
                    label = "card_bg_${day.dateKey}"
                )
                val cardBorder by animateColorAsState(
                    targetValue = if (isSelected) GoalNeonGreen else StadiumBorder,
                    label = "card_border_${day.dateKey}"
                )

                Card(
                    modifier = Modifier
                        .width(68.dp)
                        .height(84.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { onSelectDate(day.dateKey) }
                        .testTag("cal_day_${day.dateKey.replace(" ", "_")}"),
                    colors = CardDefaults.cardColors(containerColor = cardBg),
                    border = androidx.compose.foundation.BorderStroke(
                        if (isSelected) 2.dp else 1.dp,
                        cardBorder
                    )
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        // Month Badge
                        Text(
                            text = day.monthShort,
                            color = if (isSelected) TrophyGold else TextMuted,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )

                        // Day of Month Number (e.g. 21, 22)
                        Text(
                            text = day.dayOfMonth,
                            color = if (isSelected) Color.White else TextPrimary,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )

                        // Day of Week & Matches count pill
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = day.dayOfWeek,
                                color = if (isSelected) ElectricCyan else TextSecondary,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Box(
                                modifier = Modifier
                                    .size(5.dp)
                                    .clip(CircleShape)
                                    .background(if (isSelected) GoalNeonGreen else ElectricCyan)
                            )
                        }
                    }
                }
            }
        }
    }
}
