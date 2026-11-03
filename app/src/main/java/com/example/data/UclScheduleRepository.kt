package com.example.data

import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import com.example.model.LeagueId
import com.example.model.Team
import com.example.model.UclMatchStatus
import com.example.model.UclScheduleMatch
import com.example.model.UclStage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import java.util.Calendar

class UclScheduleRepository(private val context: Context) {

    // UCL Teams
    private val realMadrid = MockDataProvider.realMadrid
    private val manCity = MockDataProvider.manCity
    private val bayernMunich = MockDataProvider.bayernMunich
    private val psg = MockDataProvider.psg
    private val arsenal = MockDataProvider.arsenal
    private val barcelona = MockDataProvider.barcelona
    private val liverpool = MockDataProvider.liverpool
    private val interMilan = MockDataProvider.interMilan
    private val dortmund = MockDataProvider.dortmund
    private val bayerLeverkusen = MockDataProvider.bayerLeverkusen
    private val atleticoMadrid = MockDataProvider.atleticoMadrid
    private val juventus = MockDataProvider.juventus
    private val acMilan = MockDataProvider.acMilan
    private val monaco = MockDataProvider.monaco

    private val astonVilla = Team("Aston Villa", "Aston Villa", 0xFF670E36, 0xFF95BFE5, "AVL")
    private val sportingCP = Team("Sporting CP", "Sporting", 0xFF008053, 0xFFFFFFFF, "SCP")
    private val benfica = Team("SL Benfica", "Benfica", 0xFFE11922, 0xFFFFFFFF, "SLB")
    private val atalanta = Team("Atalanta BC", "Atalanta", 0xFF1E71B8, 0xFF000000, "ATA")
    private val celtic = Team("Celtic FC", "Celtic", 0xFF018749, 0xFFFFFFFF, "CEL")
    private val feyenoord = Team("Feyenoord", "Feyenoord", 0xFFEE1C25, 0xFFFFFFFF, "FEY")

    private val _matches = MutableStateFlow<List<UclScheduleMatch>>(emptyList())
    val matches: StateFlow<List<UclScheduleMatch>> = _matches.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val alertsSet = mutableSetOf<String>()

    init {
        // Initialize default matches
        _matches.value = generateUclCalendar()
    }

    suspend fun refreshSchedule() {
        _isLoading.value = true
        _error.value = null
        try {
            // Emulate asynchronous network request with coroutines
            withContext(Dispatchers.IO) {
                delay(700)
                val freshList = generateUclCalendar().map { match ->
                    match.copy(isAlertEnabled = alertsSet.contains(match.id))
                }
                _matches.value = freshList
            }
        } catch (e: Exception) {
            _error.value = "Failed to refresh UEFA fixtures: ${e.localizedMessage}"
        } finally {
            _isLoading.value = false
        }
    }

    fun toggleAlert(matchId: String): Boolean {
        val currentMatches = _matches.value.toMutableList()
        val index = currentMatches.indexOfFirst { it.id == matchId }
        if (index != -1) {
            val match = currentMatches[index]
            val newState = !match.isAlertEnabled
            if (newState) {
                alertsSet.add(matchId)
            } else {
                alertsSet.remove(matchId)
            }
            currentMatches[index] = match.copy(isAlertEnabled = newState)
            _matches.value = currentMatches
            return newState
        }
        return false
    }

    fun addMatchToDeviceCalendar(context: Context, match: UclScheduleMatch) {
        try {
            val intent = Intent(Intent.ACTION_INSERT).apply {
                data = CalendarContract.Events.CONTENT_URI
                putExtra(CalendarContract.Events.TITLE, "UEFA Champions League: ${match.homeTeam.name} vs ${match.awayTeam.name}")
                putExtra(CalendarContract.Events.EVENT_LOCATION, "${match.stadium}, ${match.city}")
                putExtra(
                    CalendarContract.Events.DESCRIPTION,
                    "UEFA Champions League - ${match.stage.displayName} (${match.matchdayLabel})\n" +
                            "Kickoff: ${match.timeDisplay} (${match.dateDisplay})\n" +
                            "Broadcaster: ${match.tvBroadcast}\n" +
                            "Key Duel: ${match.keyDuel}\n" +
                            "H2H: ${match.headToHead}"
                )
                putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, match.timestampMillis)
                putExtra(CalendarContract.EXTRA_EVENT_END_TIME, match.timestampMillis + (105 * 60 * 1000))
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
        } catch (e: Exception) {
            // Fallback: silently ignore if no calendar app
        }
    }

    private fun generateUclCalendar(): List<UclScheduleMatch> {
        val cal = Calendar.getInstance()
        val baseTime = cal.timeInMillis

        return listOf(
            // MATCHDAY 7 (LEAGUE PHASE)
            UclScheduleMatch(
                id = "ucl_sched_1",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 7 of 8",
                dateDisplay = "Tuesday, Jan 21",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 2),
                homeTeam = realMadrid,
                awayTeam = bayernMunich,
                status = UclMatchStatus.UPCOMING,
                stadium = "Santiago Bernabéu",
                city = "Madrid",
                tvBroadcast = "TNT Sports 1, Paramount+, Canal+",
                referee = "Szymon Marciniak (POL)",
                keyDuel = "Vinícius Júnior vs Alphonso Davies",
                headToHead = "Real Madrid 13 wins, Bayern 11 wins, 4 draws"
            ),
            UclScheduleMatch(
                id = "ucl_sched_2",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 7 of 8",
                dateDisplay = "Tuesday, Jan 21",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 2) + 3600000L,
                homeTeam = arsenal,
                awayTeam = monaco,
                status = UclMatchStatus.UPCOMING,
                stadium = "Emirates Stadium",
                city = "London",
                tvBroadcast = "TNT Sports 2, Paramount+, DAZN",
                referee = "Clément Turpin (FRA)",
                keyDuel = "Bukayo Saka vs Vanderson",
                headToHead = "Arsenal 1 win, Monaco 1 win (2015 R16)"
            ),
            UclScheduleMatch(
                id = "ucl_sched_3",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 7 of 8",
                dateDisplay = "Tuesday, Jan 21",
                timeDisplay = "18:45 CET",
                timestampMillis = baseTime + (86400000L * 2) - 7200000L,
                homeTeam = atalanta,
                awayTeam = realMadrid,
                status = UclMatchStatus.UPCOMING,
                stadium = "Gewiss Stadium",
                city = "Bergamo",
                tvBroadcast = "Sky Sport Italia, Paramount+",
                referee = "Slavko Vinčić (SVN)",
                keyDuel = "Ademola Lookman vs Dani Carvajal",
                headToHead = "UEFA Super Cup 2024 rematch (Real 2-0 Atalanta)"
            ),

            // MATCHDAY 7 - WEDNESDAY
            UclScheduleMatch(
                id = "ucl_sched_4",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 7 of 8",
                dateDisplay = "Wednesday, Jan 22",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 3),
                homeTeam = manCity,
                awayTeam = psg,
                status = UclMatchStatus.UPCOMING,
                stadium = "Etihad Stadium",
                city = "Manchester",
                tvBroadcast = "TNT Sports 1, Paramount+, Canal+ Foot",
                referee = "Daniele Orsato (ITA)",
                keyDuel = "Erling Haaland vs Marquinhos",
                headToHead = "Man City 4 wins, PSG 1 win, 2 draws"
            ),
            UclScheduleMatch(
                id = "ucl_sched_5",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 7 of 8",
                dateDisplay = "Wednesday, Jan 22",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 3),
                homeTeam = barcelona,
                awayTeam = dortmund,
                status = UclMatchStatus.UPCOMING,
                stadium = "Estadi Olímpic Lluís Companys",
                city = "Barcelona",
                tvBroadcast = "Movistar Liga de Campeones, Paramount+",
                referee = "Michael Oliver (ENG)",
                keyDuel = "Lamine Yamal vs Nico Schlotterbeck",
                headToHead = "Barcelona 2 wins, Dortmund 0 wins, 1 draw"
            ),
            UclScheduleMatch(
                id = "ucl_sched_6",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 7 of 8",
                dateDisplay = "Wednesday, Jan 22",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 3),
                homeTeam = liverpool,
                awayTeam = bayerLeverkusen,
                status = UclMatchStatus.UPCOMING,
                stadium = "Anfield",
                city = "Liverpool",
                tvBroadcast = "Amazon Prime Video, Paramount+",
                referee = "Danny Makkelie (NED)",
                keyDuel = "Mohamed Salah vs Alejandro Grimaldo",
                headToHead = "Xabi Alonso returns to Anfield; 4 previous meetings"
            ),

            // MATCHDAY 8 (LEAGUE PHASE FINALE - SIMULTANEOUS)
            UclScheduleMatch(
                id = "ucl_sched_7",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 8 (Finale)",
                dateDisplay = "Wednesday, Jan 29",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 10),
                homeTeam = interMilan,
                awayTeam = arsenal,
                status = UclMatchStatus.UPCOMING,
                stadium = "San Siro",
                city = "Milan",
                tvBroadcast = "TNT Sports Ultimate, Paramount+, DAZN",
                referee = "István Kovács (ROU)",
                keyDuel = "Lautaro Martínez vs William Saliba",
                headToHead = "Iconic 2003 group stage meetings (Inter 0-3, Ars 1-5)"
            ),
            UclScheduleMatch(
                id = "ucl_sched_8",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 8 (Finale)",
                dateDisplay = "Wednesday, Jan 29",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 10),
                homeTeam = juventus,
                awayTeam = manCity,
                status = UclMatchStatus.UPCOMING,
                stadium = "Allianz Stadium",
                city = "Turin",
                tvBroadcast = "Sky Sport Uno, Paramount+",
                referee = "Anthony Taylor (ENG)",
                keyDuel = "Dušan Vlahović vs Rúben Dias",
                headToHead = "Juventus 3 wins, Man City 1 win, 2 draws"
            ),
            UclScheduleMatch(
                id = "ucl_sched_9",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 8 (Finale)",
                dateDisplay = "Wednesday, Jan 29",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 10),
                homeTeam = atleticoMadrid,
                awayTeam = bayerLeverkusen,
                status = UclMatchStatus.UPCOMING,
                stadium = "Riyadh Air Metropolitano",
                city = "Madrid",
                tvBroadcast = "Movistar Champions, Paramount+",
                referee = "Felix Zwayer (GER)",
                keyDuel = "Antoine Griezmann vs Florian Wirtz",
                headToHead = "Fierce rivalry, 8 previous UCL meetings"
            ),
            UclScheduleMatch(
                id = "ucl_sched_10",
                stage = UclStage.LEAGUE_PHASE,
                matchdayLabel = "Matchday 8 (Finale)",
                dateDisplay = "Wednesday, Jan 29",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 10),
                homeTeam = astonVilla,
                awayTeam = celtic,
                status = UclMatchStatus.UPCOMING,
                stadium = "Villa Park",
                city = "Birmingham",
                tvBroadcast = "TNT Sports 3, Paramount+",
                referee = "Artur Soares Dias (POR)",
                keyDuel = "Ollie Watkins vs Cameron Carter-Vickers",
                headToHead = "Historic British battle of 1982 & 1967 European Champions"
            ),

            // KNOCKOUT PLAY-OFFS (FEBRUARY)
            UclScheduleMatch(
                id = "ucl_sched_11",
                stage = UclStage.PLAYOFFS,
                matchdayLabel = "Knockout Play-offs 1st Leg",
                dateDisplay = "Tuesday, Feb 11",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 22),
                homeTeam = sportingCP,
                awayTeam = dortmund,
                status = UclMatchStatus.UPCOMING,
                stadium = "Estádio José Alvalade",
                city = "Lisbon",
                tvBroadcast = "TNT Sports, DAZN, Paramount+",
                referee = "Espen Eskås (NOR)",
                keyDuel = "Viktor Gyökeres vs Waldemar Anton",
                headToHead = "Sporting eliminated Dortmund in 2021 group stage"
            ),
            UclScheduleMatch(
                id = "ucl_sched_12",
                stage = UclStage.PLAYOFFS,
                matchdayLabel = "Knockout Play-offs 1st Leg",
                dateDisplay = "Wednesday, Feb 12",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 23),
                homeTeam = acMilan,
                awayTeam = benfica,
                status = UclMatchStatus.UPCOMING,
                stadium = "San Siro",
                city = "Milan",
                tvBroadcast = "Sky Sport, Paramount+, DAZN",
                referee = "Jesús Gil Manzano (ESP)",
                keyDuel = "Rafael Leão vs Ángel Di María",
                headToHead = "Historic European Cup Finals (1963 & 1990 Milan victories)"
            ),

            // ROUND OF 16 (MARCH)
            UclScheduleMatch(
                id = "ucl_sched_13",
                stage = UclStage.ROUND_OF_16,
                matchdayLabel = "Round of 16 - 1st Leg",
                dateDisplay = "Tuesday, Mar 4",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 44),
                homeTeam = realMadrid,
                awayTeam = manCity,
                status = UclMatchStatus.UPCOMING,
                stadium = "Santiago Bernabéu",
                city = "Madrid",
                tvBroadcast = "TNT Sports 1, Paramount+, Movistar+",
                referee = "Szymon Marciniak (POL)",
                keyDuel = "Kylian Mbappé vs Joško Gvardiol",
                headToHead = "4th consecutive season blockbuster clash (2022, 2023, 2024)"
            ),
            UclScheduleMatch(
                id = "ucl_sched_14",
                stage = UclStage.ROUND_OF_16,
                matchdayLabel = "Round of 16 - 1st Leg",
                dateDisplay = "Wednesday, Mar 5",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 45),
                homeTeam = bayernMunich,
                awayTeam = psg,
                status = UclMatchStatus.UPCOMING,
                stadium = "Allianz Arena",
                city = "Munich",
                tvBroadcast = "DAZN 1, Paramount+, Canal+",
                referee = "Daniele Orsato (ITA)",
                keyDuel = "Harry Kane vs Gianluigi Donnarumma",
                headToHead = "Rematch of 2020 Champions League Final (1-0 Bayern)"
            ),
            UclScheduleMatch(
                id = "ucl_sched_15",
                stage = UclStage.ROUND_OF_16,
                matchdayLabel = "Round of 16 - 2nd Leg",
                dateDisplay = "Tuesday, Mar 11",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 51),
                homeTeam = manCity,
                awayTeam = realMadrid,
                status = UclMatchStatus.UPCOMING,
                stadium = "Etihad Stadium",
                city = "Manchester",
                tvBroadcast = "TNT Sports 1, Paramount+",
                referee = "Clément Turpin (FRA)",
                keyDuel = "Phil Foden vs Jude Bellingham",
                headToHead = "Decisive 2nd leg showdown in Manchester",
                aggregateScore = "1st Leg: Bernabéu"
            ),

            // QUARTER-FINALS (APRIL)
            UclScheduleMatch(
                id = "ucl_sched_16",
                stage = UclStage.QUARTER_FINALS,
                matchdayLabel = "Quarter-Finals - 1st Leg",
                dateDisplay = "Tuesday, Apr 8",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 78),
                homeTeam = liverpool,
                awayTeam = barcelona,
                status = UclMatchStatus.UPCOMING,
                stadium = "Anfield",
                city = "Liverpool",
                tvBroadcast = "TNT Sports 1, Paramount+, Movistar+",
                referee = "Michael Oliver (ENG)",
                keyDuel = "Trent Alexander-Arnold vs Raphinha",
                headToHead = "Epic 2019 Semi-Final comeback memories (4-0 Anfield)"
            ),
            UclScheduleMatch(
                id = "ucl_sched_17",
                stage = UclStage.QUARTER_FINALS,
                matchdayLabel = "Quarter-Finals - 1st Leg",
                dateDisplay = "Wednesday, Apr 9",
                timeDisplay = "21:00 CET",
                timestampMillis = baseTime + (86400000L * 79),
                homeTeam = arsenal,
                awayTeam = interMilan,
                status = UclMatchStatus.UPCOMING,
                stadium = "Emirates Stadium",
                city = "London",
                tvBroadcast = "TNT Sports 2, Paramount+, Sky Italia",
                referee = "Slavko Vinčić (SVN)",
                keyDuel = "Martin Ødegaard vs Nicolò Barella",
                headToHead = "Two European heavyweights contesting semifinal spot"
            ),

            // SEMI-FINALS (APRIL / MAY)
            UclScheduleMatch(
                id = "ucl_sched_18",
                stage = UclStage.SEMI_FINALS,
                matchdayLabel = "Semi-Finals - 1st Leg",
                dateDisplay = "Tuesday, Apr 29",
                timeDisplay = "21:00 CEST",
                timestampMillis = baseTime + (86400000L * 99),
                homeTeam = realMadrid,
                awayTeam = liverpool,
                status = UclMatchStatus.UPCOMING,
                stadium = "Santiago Bernabéu",
                city = "Madrid",
                tvBroadcast = "TNT Sports 1, Paramount+, Movistar Champions",
                referee = "István Kovács (ROU)",
                keyDuel = "Vinícius Júnior vs Virgil van Dijk",
                headToHead = "Repeat of 2018 & 2022 Champions League Finals"
            ),

            // GRAND FINAL (MUNICH)
            UclScheduleMatch(
                id = "ucl_sched_19",
                stage = UclStage.FINAL,
                matchdayLabel = "Grand Final - Munich 2025",
                dateDisplay = "Saturday, May 31",
                timeDisplay = "21:00 CEST",
                timestampMillis = baseTime + (86400000L * 131),
                homeTeam = bayernMunich,
                awayTeam = realMadrid,
                status = UclMatchStatus.UPCOMING,
                stadium = "Allianz Arena",
                city = "Munich",
                tvBroadcast = "UEFA World Feed, TNT Sports, CBS Paramount, ZDF, Movistar",
                referee = "UEFA Elite Panel Referee",
                keyDuel = "European Champions Crowning - Road to Munich",
                headToHead = "The Ultimate European Night in Munich"
            )
        )
    }
}
