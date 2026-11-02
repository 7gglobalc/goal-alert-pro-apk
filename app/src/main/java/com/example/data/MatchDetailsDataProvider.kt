package com.example.data

import com.example.model.MatchDetailedStats
import com.example.model.Player
import com.example.model.TeamLineup
import com.example.model.TimelineEvent
import com.example.model.TimelineEventType
import com.example.ui.components.TrackerMatchItem

object MatchDetailsDataProvider {

    fun getStatsForMatch(match: TrackerMatchItem): MatchDetailedStats {
        val homePoss = match.possessionHome
        val awayPoss = 100 - homePoss
        return MatchDetailedStats(
            possessionHome = homePoss,
            possessionAway = awayPoss,
            shotsOnTargetHome = match.homeScore + 4,
            shotsOnTargetAway = match.awayScore + 3,
            totalShotsHome = match.homeScore * 3 + 8,
            totalShotsAway = match.awayScore * 3 + 6,
            cornerKicksHome = 6,
            cornerKicksAway = 4,
            foulsHome = 9,
            foulsAway = 12,
            yellowCardsHome = 1,
            yellowCardsAway = 2,
            redCardsHome = 0,
            redCardsAway = 0,
            passAccuracyHome = 89,
            passAccuracyAway = 83,
            xGHome = (match.homeScore * 0.72) + 0.45,
            xGAway = (match.awayScore * 0.65) + 0.38
        )
    }

    fun getTimelineEventsForMatch(match: TrackerMatchItem): List<TimelineEvent> {
        val list = mutableListOf<TimelineEvent>()
        list.add(TimelineEvent(1, null, TimelineEventType.PERIOD_START, true, "Match Kick-off", detail = "1st Half starts"))

        // Goals based on score
        if (match.homeScore > 0) {
            list.add(TimelineEvent(22, null, TimelineEventType.GOAL, true, "Erling Haaland", "Kevin De Bruyne", "Goal! Score: 1-0"))
        }
        if (match.awayScore > 0) {
            list.add(TimelineEvent(38, null, TimelineEventType.GOAL, false, "Bukayo Saka", "Martin Ødegaard", "Goal! Score: 1-1"))
        }

        list.add(TimelineEvent(42, null, TimelineEventType.YELLOW_CARD, false, "Declan Rice", detail = "Tactical foul in midfield"))
        list.add(TimelineEvent(45, 2, TimelineEventType.PERIOD_END, true, "Half Time", detail = "Interval reached"))
        list.add(TimelineEvent(46, null, TimelineEventType.PERIOD_START, true, "2nd Half", detail = "Play resumes"))

        if (match.homeScore > 1) {
            list.add(TimelineEvent(64, null, TimelineEventType.GOAL, true, "Phil Foden", "Bernardo Silva", "Goal! Score: 2-1"))
        }

        list.add(TimelineEvent(68, null, TimelineEventType.SUBSTITUTION, false, "Gabriel Martinelli", "Leandro Trossard", "Tactical switch"))
        list.add(TimelineEvent(72, null, TimelineEventType.YELLOW_CARD, true, "Rúben Dias", detail = "Argument with referee"))
        list.add(TimelineEvent(76, null, TimelineEventType.SUBSTITUTION, true, "Jack Grealish", "Jérémy Doku", "Fresh legs on wing"))

        if (match.homeScore > 2) {
            list.add(TimelineEvent(81, null, TimelineEventType.GOAL, true, "Kylian Mbappé", "Luka Modrić", "Goal! Score: 3-2"))
        }

        return list.sortedBy { it.minute }
    }

    fun getLineupsForMatch(match: TrackerMatchItem): Pair<TeamLineup, TeamLineup> {
        val homeLineup = TeamLineup(
            formation = "4-3-3",
            manager = if (match.homeTeam.code == "MCI") "Pep Guardiola" else if (match.homeTeam.code == "RMA") "Carlo Ancelotti" else "Head Coach",
            startingXI = listOf(
                Player(31, "Ederson", "GK"),
                Player(2, "Kyle Walker", "DEF", isCaptain = true),
                Player(3, "Rúben Dias", "DEF"),
                Player(25, "Manuel Akanji", "DEF"),
                Player(24, "Josko Gvardiol", "DEF"),
                Player(16, "Rodri", "MID"),
                Player(17, "Kevin De Bruyne", "MID"),
                Player(20, "Bernardo Silva", "MID"),
                Player(47, "Phil Foden", "FWD"),
                Player(9, "Erling Haaland", "FWD"),
                Player(11, "Jérémy Doku", "FWD")
            ),
            substitutes = listOf(
                Player(18, "Stefan Ortega", "GK", isStarting = false),
                Player(5, "John Stones", "DEF", isStarting = false),
                Player(8, "Mateo Kovačić", "MID", isStarting = false),
                Player(10, "Jack Grealish", "FWD", isStarting = false),
                Player(27, "Matheus Nunes", "MID", isStarting = false)
            )
        )

        val awayLineup = TeamLineup(
            formation = "4-2-3-1",
            manager = if (match.awayTeam.code == "ARS") "Mikel Arteta" else if (match.awayTeam.code == "FCB") "Hansi Flick" else "Head Coach",
            startingXI = listOf(
                Player(22, "David Raya", "GK"),
                Player(4, "Ben White", "DEF"),
                Player(2, "William Saliba", "DEF"),
                Player(6, "Gabriel Magalhães", "DEF"),
                Player(12, "Jurrien Timber", "DEF"),
                Player(5, "Thomas Partey", "MID"),
                Player(41, "Declan Rice", "MID"),
                Player(7, "Bukayo Saka", "FWD"),
                Player(8, "Martin Ødegaard", "MID", isCaptain = true),
                Player(11, "Gabriel Martinelli", "FWD"),
                Player(29, "Kai Havertz", "FWD")
            ),
            substitutes = listOf(
                Player(32, "Neto", "GK", isStarting = false),
                Player(15, "Jakub Kiwior", "DEF", isStarting = false),
                Player(20, "Jorginho", "MID", isStarting = false),
                Player(19, "Leandro Trossard", "FWD", isStarting = false),
                Player(9, "Gabriel Jesus", "FWD", isStarting = false)
            )
        )

        return Pair(homeLineup, awayLineup)
    }
}
