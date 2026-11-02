package com.example.model

enum class LeagueId(val displayName: String, val country: String, val shortCode: String, val colorHex: Long) {
    ALL("All Leagues", "Europe", "ALL", 0xFF6366F1),
    UCL("Champions League", "UEFA", "UCL", 0xFF001489),
    EPL("Premier League", "England", "EPL", 0xFF3D195B),
    LA_LIGA("La Liga", "Spain", "LAL", 0xFFEE1222),
    BUNDESLIGA("Bundesliga", "Germany", "BUN", 0xFFD20515),
    SERIE_A("Serie A", "Italy", "SEA", 0xFF008FD7),
    LIGUE_1("Ligue 1", "France", "L1", 0xFF091C3E)
}

enum class MatchStatus(val label: String) {
    LIVE("LIVE"),
    HALFTIME("HT"),
    UPCOMING("PRE-MATCH"),
    FINISHED("FT")
}

data class Team(
    val name: String,
    val shortName: String,
    val primaryColorHex: Long,
    val secondaryColorHex: Long,
    val initials: String
)

data class GoalEvent(
    val id: String,
    val matchId: String,
    val league: LeagueId,
    val minute: Int,
    val extraMinute: Int? = null,
    val scorer: String,
    val team: Team,
    val assist: String? = null,
    val homeScore: Int,
    val awayScore: Int,
    val isHomeTeamGoal: Boolean,
    val isPenalty: Boolean = false,
    val isOwnGoal: Boolean = false,
    val timestamp: Long = System.currentTimeMillis()
) {
    val displayMinute: String
        get() = if (extraMinute != null && extraMinute > 0) "$minute+$extraMinute'" else "$minute'"
}

data class Match(
    val id: String,
    val league: LeagueId,
    val homeTeam: Team,
    val awayTeam: Team,
    val homeScore: Int,
    val awayScore: Int,
    val minute: Int,
    val status: MatchStatus,
    val stadium: String,
    val possessionHome: Int = 50,
    val shotsHome: Int = 6,
    val shotsAway: Int = 5,
    val recentGoals: List<GoalEvent> = emptyList()
) {
    val scoreDisplay: String
        get() = "$homeScore - $awayScore"
}
