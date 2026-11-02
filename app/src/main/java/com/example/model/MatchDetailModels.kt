package com.example.model

enum class TimelineEventType {
    GOAL,
    YELLOW_CARD,
    RED_CARD,
    SUBSTITUTION,
    PERIOD_START,
    PERIOD_END
}

data class TimelineEvent(
    val minute: Int,
    val extraMinute: Int? = null,
    val type: TimelineEventType,
    val isHomeTeam: Boolean,
    val primaryPlayer: String,
    val secondaryPlayer: String? = null,
    val detail: String? = null
) {
    val displayMinute: String
        get() = if (extraMinute != null && extraMinute > 0) "$minute+$extraMinute'" else "$minute'"
}

data class Player(
    val number: Int,
    val name: String,
    val position: String, // "GK", "DEF", "MID", "FWD"
    val isCaptain: Boolean = false,
    val isStarting: Boolean = true
)

data class TeamLineup(
    val formation: String,
    val manager: String,
    val startingXI: List<Player>,
    val substitutes: List<Player>
)

data class MatchDetailedStats(
    val possessionHome: Int = 54,
    val possessionAway: Int = 46,
    val shotsOnTargetHome: Int = 6,
    val shotsOnTargetAway: Int = 4,
    val totalShotsHome: Int = 13,
    val totalShotsAway: Int = 9,
    val cornerKicksHome: Int = 5,
    val cornerKicksAway: Int = 3,
    val foulsHome: Int = 8,
    val foulsAway: Int = 11,
    val yellowCardsHome: Int = 1,
    val yellowCardsAway: Int = 2,
    val redCardsHome: Int = 0,
    val redCardsAway: Int = 0,
    val passAccuracyHome: Int = 88,
    val passAccuracyAway: Int = 82,
    val xGHome: Double = 1.94,
    val xGAway: Double = 1.15
)
