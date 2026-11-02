package com.example.data

import com.example.model.GoalEvent
import com.example.model.LeagueId
import com.example.model.Match
import com.example.model.MatchStatus
import com.example.model.Team

object MockDataProvider {

    // Premier League Teams
    val manCity = Team("Manchester City", "Man City", 0xFF6CABDD, 0xFF1C2C5B, "MCI")
    val arsenal = Team("Arsenal", "Arsenal", 0xFFEF0107, 0xFF063672, "ARS")
    val liverpool = Team("Liverpool", "Liverpool", 0xFFC8102E, 0xFF00B2A9, "LIV")
    val chelsea = Team("Chelsea", "Chelsea", 0xFF034694, 0xFFEE242C, "CHE")
    val realMadrid = Team("Real Madrid", "Real Madrid", 0xFFFFFFFF, 0xFFFEBE10, "RMA")
    val barcelona = Team("Barcelona", "Barcelona", 0xFF004D98, 0xFFA50044, "FCB")
    val atleticoMadrid = Team("Atlético Madrid", "Atlético", 0xFFCB3524, 0xFF1D3557, "ATM")
    val bayernMunich = Team("Bayern Munich", "Bayern", 0xFFDC052D, 0xFF0066B2, "FCB")
    val dortmund = Team("Borussia Dortmund", "Dortmund", 0xFFFDE100, 0xFF000000, "BVB")
    val bayerLeverkusen = Team("Bayer Leverkusen", "Leverkusen", 0xFFE32221, 0xFF000000, "B04")
    val interMilan = Team("Inter Milan", "Inter", 0xFF005395, 0xFF000000, "INT")
    val juventus = Team("Juventus", "Juventus", 0xFFFFFFFF, 0xFF000000, "JUV")
    val acMilan = Team("AC Milan", "AC Milan", 0xFFFB090B, 0xFF000000, "ACM")
    val psg = Team("Paris Saint-Germain", "PSG", 0xFF004170, 0xFFDA291C, "PSG")
    val monaco = Team("AS Monaco", "Monaco", 0xFFE31B23, 0xFFFFFFFF, "ASM")
    val marseille = Team("Marseille", "Marseille", 0xFF2FAEE0, 0xFFFFFFFF, "OM")

    fun getInitialMatches(): List<Match> {
        return listOf(
            Match(
                id = "m_ucl_1",
                league = LeagueId.UCL,
                homeTeam = realMadrid,
                awayTeam = manCity,
                homeScore = 3,
                awayScore = 3,
                minute = 86,
                status = MatchStatus.LIVE,
                stadium = "Santiago Bernabéu, Madrid",
                possessionHome = 48,
                shotsHome = 14,
                shotsAway = 15,
                recentGoals = listOf(
                    GoalEvent("g_ucl_1", "m_ucl_1", LeagueId.UCL, 12, null, "Bernardo Silva", manCity, "Jack Grealish", 0, 1, false),
                    GoalEvent("g_ucl_2", "m_ucl_1", LeagueId.UCL, 28, null, "Eduardo Camavinga", realMadrid, "Vinícius Júnior", 1, 1, true),
                    GoalEvent("g_ucl_3", "m_ucl_1", LeagueId.UCL, 34, null, "Rodrygo", realMadrid, "Vinícius Júnior", 2, 1, true),
                    GoalEvent("g_ucl_4", "m_ucl_1", LeagueId.UCL, 66, null, "Phil Foden", manCity, "John Stones", 2, 2, false),
                    GoalEvent("g_ucl_5", "m_ucl_1", LeagueId.UCL, 71, null, "Joško Gvardiol", manCity, "Jack Grealish", 2, 3, false),
                    GoalEvent("g_ucl_6", "m_ucl_1", LeagueId.UCL, 79, null, "Federico Valverde", realMadrid, "Vinícius Júnior", 3, 3, true)
                )
            ),
            Match(
                id = "m_ucl_2",
                league = LeagueId.UCL,
                homeTeam = bayernMunich,
                awayTeam = psg,
                homeScore = 2,
                awayScore = 1,
                minute = 74,
                status = MatchStatus.LIVE,
                stadium = "Allianz Arena, Munich",
                possessionHome = 55,
                shotsHome = 12,
                shotsAway = 9,
                recentGoals = listOf(
                    GoalEvent("g_ucl_7", "m_ucl_2", LeagueId.UCL, 38, null, "Harry Kane", bayernMunich, "Jamal Musiala", 1, 0, true),
                    GoalEvent("g_ucl_8", "m_ucl_2", LeagueId.UCL, 52, null, "Ousmane Dembélé", psg, "Achraf Hakimi", 1, 1, false),
                    GoalEvent("g_ucl_9", "m_ucl_2", LeagueId.UCL, 68, null, "Jamal Musiala", bayernMunich, "Leroy Sané", 2, 1, true)
                )
            ),
            Match(
                id = "m_epl_1",
                league = LeagueId.EPL,
                homeTeam = manCity,
                awayTeam = arsenal,
                homeScore = 2,
                awayScore = 1,
                minute = 67,
                status = MatchStatus.LIVE,
                stadium = "Etihad Stadium, Manchester",
                possessionHome = 58,
                shotsHome = 12,
                shotsAway = 8,
                recentGoals = listOf(
                    GoalEvent("g1", "m_epl_1", LeagueId.EPL, 22, null, "Erling Haaland", manCity, "Kevin De Bruyne", 1, 0, true),
                    GoalEvent("g2", "m_epl_1", LeagueId.EPL, 41, null, "Bukayo Saka", arsenal, "Martin Ødegaard", 1, 1, false),
                    GoalEvent("g3", "m_epl_1", LeagueId.EPL, 64, null, "Phil Foden", manCity, "Bernardo Silva", 2, 1, true)
                )
            ),
            Match(
                id = "m_laliga_1",
                league = LeagueId.LA_LIGA,
                homeTeam = realMadrid,
                awayTeam = barcelona,
                homeScore = 3,
                awayScore = 2,
                minute = 78,
                status = MatchStatus.LIVE,
                stadium = "Santiago Bernabéu, Madrid",
                possessionHome = 52,
                shotsHome = 14,
                shotsAway = 13,
                recentGoals = listOf(
                    GoalEvent("g4", "m_laliga_1", LeagueId.LA_LIGA, 14, null, "Vinícius Júnior", realMadrid, "Jude Bellingham", 1, 0, true),
                    GoalEvent("g5", "m_laliga_1", LeagueId.LA_LIGA, 33, null, "Lamine Yamal", barcelona, "Pedri", 1, 1, false),
                    GoalEvent("g6", "m_laliga_1", LeagueId.LA_LIGA, 55, null, "Robert Lewandowski", barcelona, "Raphinha", 1, 2, false),
                    GoalEvent("g7", "m_laliga_1", LeagueId.LA_LIGA, 69, null, "Kylian Mbappé", realMadrid, "Luka Modrić", 2, 2, true),
                    GoalEvent("g8", "m_laliga_1", LeagueId.LA_LIGA, 75, null, "Jude Bellingham", realMadrid, "Federico Valverde", 3, 2, true)
                )
            ),
            Match(
                id = "m_bundesliga_1",
                league = LeagueId.BUNDESLIGA,
                homeTeam = bayernMunich,
                awayTeam = bayerLeverkusen,
                homeScore = 1,
                awayScore = 1,
                minute = 54,
                status = MatchStatus.LIVE,
                stadium = "Allianz Arena, Munich",
                possessionHome = 62,
                shotsHome = 9,
                shotsAway = 7,
                recentGoals = listOf(
                    GoalEvent("g9", "m_bundesliga_1", LeagueId.BUNDESLIGA, 19, null, "Florian Wirtz", bayerLeverkusen, "Jeremie Frimpong", 0, 1, false),
                    GoalEvent("g10", "m_bundesliga_1", LeagueId.BUNDESLIGA, 43, null, "Harry Kane", bayernMunich, "Jamal Musiala", 1, 1, true)
                )
            ),
            Match(
                id = "m_seriea_1",
                league = LeagueId.SERIE_A,
                homeTeam = interMilan,
                awayTeam = juventus,
                homeScore = 2,
                awayScore = 0,
                minute = 83,
                status = MatchStatus.LIVE,
                stadium = "San Siro, Milan",
                possessionHome = 54,
                shotsHome = 11,
                shotsAway = 6,
                recentGoals = listOf(
                    GoalEvent("g11", "m_seriea_1", LeagueId.SERIE_A, 28, null, "Lautaro Martínez", interMilan, "Nicolò Barella", 1, 0, true),
                    GoalEvent("g12", "m_seriea_1", LeagueId.SERIE_A, 61, null, "Marcus Thuram", interMilan, "Federico Dimarco", 2, 0, true)
                )
            ),
            Match(
                id = "m_ligue1_1",
                league = LeagueId.LIGUE_1,
                homeTeam = psg,
                awayTeam = monaco,
                homeScore = 3,
                awayScore = 1,
                minute = 61,
                status = MatchStatus.LIVE,
                stadium = "Parc des Princes, Paris",
                possessionHome = 65,
                shotsHome = 15,
                shotsAway = 5,
                recentGoals = listOf(
                    GoalEvent("g13", "m_ligue1_1", LeagueId.LIGUE_1, 9, null, "Ousmane Dembélé", psg, "Achraf Hakimi", 1, 0, true),
                    GoalEvent("g14", "m_ligue1_1", LeagueId.LIGUE_1, 27, null, "Bradley Barcola", psg, "Warren Zaïre-Emery", 2, 0, true),
                    GoalEvent("g15", "m_ligue1_1", LeagueId.LIGUE_1, 48, null, "Folarin Balogun", monaco, "Aleksandr Golovin", 2, 1, false),
                    GoalEvent("g16", "m_ligue1_1", LeagueId.LIGUE_1, 58, null, "Vitinha", psg, null, 3, 1, true)
                )
            ),
            Match(
                id = "m_epl_2",
                league = LeagueId.EPL,
                homeTeam = liverpool,
                awayTeam = chelsea,
                homeScore = 1,
                awayScore = 0,
                minute = 32,
                status = MatchStatus.LIVE,
                stadium = "Anfield, Liverpool",
                possessionHome = 56,
                shotsHome = 7,
                shotsAway = 4,
                recentGoals = listOf(
                    GoalEvent("g17", "m_epl_2", LeagueId.EPL, 29, null, "Mohamed Salah", liverpool, "Trent Alexander-Arnold", 1, 0, true)
                )
            )
        )
    }

    // Candidate players to simulate realistic goals
    data class CandidateScorer(val name: String, val team: Team, val assist: String?)

    val candidateScorers = listOf(
        CandidateScorer("Erling Haaland", manCity, "Kevin De Bruyne"),
        CandidateScorer("Kylian Mbappé", realMadrid, "Vinícius Júnior"),
        CandidateScorer("Jude Bellingham", realMadrid, "Luka Modrić"),
        CandidateScorer("Lamine Yamal", barcelona, "Raphinha"),
        CandidateScorer("Harry Kane", bayernMunich, "Jamal Musiala"),
        CandidateScorer("Florian Wirtz", bayerLeverkusen, "Granit Xhaka"),
        CandidateScorer("Lautaro Martínez", interMilan, "Nicolò Barella"),
        CandidateScorer("Ousmane Dembélé", psg, "Achraf Hakimi"),
        CandidateScorer("Mohamed Salah", liverpool, "Alexis Mac Allister"),
        CandidateScorer("Cole Palmer", chelsea, "Enzo Fernández"),
        CandidateScorer("Bukayo Saka", arsenal, "Declan Rice")
    )
}
