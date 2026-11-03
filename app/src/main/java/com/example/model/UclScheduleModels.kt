package com.example.model

enum class UclStage(val displayName: String, val shortCode: String) {
    ALL("All Matches", "ALL"),
    LEAGUE_PHASE("League Phase", "LP"),
    PLAYOFFS("Play-offs", "PO"),
    ROUND_OF_16("Round of 16", "R16"),
    QUARTER_FINALS("Quarter-Finals", "QF"),
    SEMI_FINALS("Semi-Finals", "SF"),
    FINAL("Final Munich", "FIN")
}

enum class UclMatchStatus(val label: String) {
    UPCOMING("UPCOMING"),
    LIVE("LIVE"),
    FINISHED("FT")
}

data class UclCalendarDay(
    val dateKey: String,
    val dayOfWeek: String,
    val dayOfMonth: String,
    val monthShort: String,
    val matchCount: Int,
    val stage: UclStage
)

data class UclScheduleMatch(
    val id: String,
    val stage: UclStage,
    val matchdayLabel: String,
    val dateDisplay: String,
    val timeDisplay: String,
    val timestampMillis: Long,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int? = null,
    val awayScore: Int? = null,
    val status: UclMatchStatus = UclMatchStatus.UPCOMING,
    val minute: Int? = null,
    val stadium: String,
    val city: String,
    val tvBroadcast: String,
    val referee: String,
    val keyDuel: String,
    val headToHead: String,
    val aggregateScore: String? = null,
    val isAlertEnabled: Boolean = false
) {
    val isLive: Boolean get() = status == UclMatchStatus.LIVE
    val isFinished: Boolean get() = status == UclMatchStatus.FINISHED

    val dayOfWeek: String
        get() = dateDisplay.split(",").firstOrNull()?.trim()?.take(3)?.uppercase() ?: "MD"

    val dayOfMonth: String
        get() {
            val parts = dateDisplay.split(" ")
            return parts.lastOrNull()?.filter { it.isDigit() }?.padStart(2, '0') ?: ""
        }

    val monthShort: String
        get() {
            val parts = dateDisplay.split(" ")
            return parts.getOrNull(1)?.take(3)?.uppercase() ?: "UCL"
        }
}
