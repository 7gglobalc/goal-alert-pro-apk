package com.example.service

import android.content.Context
import com.example.data.MockDataProvider
import com.example.model.GoalEvent
import com.example.model.LeagueId
import com.example.model.Match
import com.example.model.MatchStatus
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import kotlin.random.Random

class MatchSimulationEngine(
    private val context: Context,
    private val scope: CoroutineScope
) {
    val notificationManager = GoalNotificationManager(context)
    val soundPlayer = StadiumSoundPlayer(context)

    private val _matches = MutableStateFlow<List<Match>>(MockDataProvider.getInitialMatches())
    val matches: StateFlow<List<Match>> = _matches.asStateFlow()

    private val _goalEvents = MutableStateFlow<List<GoalEvent>>(
        MockDataProvider.getInitialMatches()
            .flatMap { it.recentGoals }
            .sortedByDescending { it.timestamp }
    )
    val goalEvents: StateFlow<List<GoalEvent>> = _goalEvents.asStateFlow()

    private val _latestGoalAlert = MutableStateFlow<GoalEvent?>(null)
    val latestGoalAlert: StateFlow<GoalEvent?> = _latestGoalAlert.asStateFlow()

    private val _isLiveSimulationRunning = MutableStateFlow(true)
    val isLiveSimulationRunning: StateFlow<Boolean> = _isLiveSimulationRunning.asStateFlow()

    private var simulationJob: Job? = null

    init {
        startSimulation()
    }

    fun startSimulation() {
        _isLiveSimulationRunning.value = true
        simulationJob?.cancel()
        simulationJob = scope.launch(Dispatchers.Default) {
            while (_isLiveSimulationRunning.value) {
                delay(12000L) // tick every 12 seconds to simulate realistic match progress
                advanceMatchesClock()
            }
        }
    }

    fun pauseSimulation() {
        _isLiveSimulationRunning.value = false
        simulationJob?.cancel()
    }

    fun toggleSimulation() {
        if (_isLiveSimulationRunning.value) {
            pauseSimulation()
        } else {
            startSimulation()
        }
    }

    private fun advanceMatchesClock() {
        val currentMatches = _matches.value.toMutableList()
        val updated = currentMatches.map { match ->
            if (match.status == MatchStatus.LIVE) {
                val nextMinute = match.minute + 1
                if (nextMinute >= 90) {
                    match.copy(minute = 90, status = MatchStatus.FINISHED)
                } else if (nextMinute == 45) {
                    match.copy(minute = 45, status = MatchStatus.HALFTIME)
                } else {
                    match.copy(minute = nextMinute)
                }
            } else {
                match
            }
        }
        _matches.value = updated
    }

    fun triggerGoal(
        matchId: String,
        scorer: String,
        assist: String?,
        isHomeTeam: Boolean
    ): GoalEvent? {
        val currentMatches = _matches.value.toMutableList()
        val matchIndex = currentMatches.indexOfFirst { it.id == matchId }
        if (matchIndex == -1) return null

        val currentMatch = currentMatches[matchIndex]
        val team = if (isHomeTeam) currentMatch.homeTeam else currentMatch.awayTeam
        val newHomeScore = if (isHomeTeam) currentMatch.homeScore + 1 else currentMatch.homeScore
        val newAwayScore = if (!isHomeTeam) currentMatch.awayScore + 1 else currentMatch.awayScore
        val minute = if (currentMatch.minute in 1..90) currentMatch.minute else (Random.nextInt(50) + 40)

        val newGoal = GoalEvent(
            id = "g_${UUID.randomUUID().toString().take(8)}",
            matchId = matchId,
            league = currentMatch.league,
            minute = minute,
            extraMinute = null,
            scorer = scorer,
            team = team,
            assist = assist,
            homeScore = newHomeScore,
            awayScore = newAwayScore,
            isHomeTeamGoal = isHomeTeam,
            timestamp = System.currentTimeMillis()
        )

        val updatedGoals = listOf(newGoal) + currentMatch.recentGoals
        val updatedMatch = currentMatch.copy(
            homeScore = newHomeScore,
            awayScore = newAwayScore,
            recentGoals = updatedGoals,
            shotsHome = if (isHomeTeam) currentMatch.shotsHome + 1 else currentMatch.shotsHome,
            shotsAway = if (!isHomeTeam) currentMatch.shotsAway + 1 else currentMatch.shotsAway
        )

        currentMatches[matchIndex] = updatedMatch
        _matches.value = currentMatches

        _goalEvents.value = listOf(newGoal) + _goalEvents.value
        _latestGoalAlert.value = newGoal

        // Fire sound & notification
        val matchSummary = "${currentMatch.homeTeam.name} vs ${currentMatch.awayTeam.name} (${currentMatch.league.displayName})"
        notificationManager.showGoalNotification(newGoal, matchSummary)
        soundPlayer.playStadiumCheer(fullVolume = true)

        return newGoal
    }

    fun triggerRandomGoal(preferredLeague: LeagueId? = null): GoalEvent? {
        val eligibleMatches = _matches.value.filter {
            preferredLeague == null || preferredLeague == LeagueId.ALL || it.league == preferredLeague
        }
        val targetMatch = if (eligibleMatches.isNotEmpty()) eligibleMatches.random() else _matches.value.random()
        val isHome = Random.nextBoolean()
        val candidate = MockDataProvider.candidateScorers.find {
            if (isHome) it.team.name == targetMatch.homeTeam.name else it.team.name == targetMatch.awayTeam.name
        } ?: MockDataProvider.candidateScorers.random()

        return triggerGoal(
            matchId = targetMatch.id,
            scorer = candidate.name,
            assist = candidate.assist,
            isHomeTeam = isHome
        )
    }

    fun clearLatestGoalAlert() {
        _latestGoalAlert.value = null
    }
}
