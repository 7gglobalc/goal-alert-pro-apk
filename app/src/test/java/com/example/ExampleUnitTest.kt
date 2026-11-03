package com.example

import org.junit.Assert.*
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun testUclStageEnumeration() {
    val stages = com.example.model.UclStage.entries
    assertEquals(7, stages.size)
    assertTrue(stages.contains(com.example.model.UclStage.LEAGUE_PHASE))
    assertTrue(stages.contains(com.example.model.UclStage.ROUND_OF_16))
    assertTrue(stages.contains(com.example.model.UclStage.FINAL))
    assertEquals("Final Munich", com.example.model.UclStage.FINAL.displayName)
  }

  @Test
  fun testUclScheduleMatchProperties() {
    val home = com.example.model.Team("Real Madrid", "Real Madrid", 0xFFFFFFFF, 0xFFFEBE10, "RMA")
    val away = com.example.model.Team("Man City", "Man City", 0xFF6CABDD, 0xFF1C2C5B, "MCI")

    val match = com.example.model.UclScheduleMatch(
      id = "test_1",
      stage = com.example.model.UclStage.ROUND_OF_16,
      matchdayLabel = "Round of 16 - 1st Leg",
      dateDisplay = "Tuesday, Mar 4",
      timeDisplay = "21:00 CET",
      timestampMillis = 1741118400000L,
      homeTeam = home,
      awayTeam = away,
      status = com.example.model.UclMatchStatus.UPCOMING,
      stadium = "Santiago Bernabéu",
      city = "Madrid",
      tvBroadcast = "TNT Sports",
      referee = "Szymon Marciniak",
      keyDuel = "Mbappé vs Gvardiol",
      headToHead = "Epic clash"
    )

    assertFalse(match.isLive)
    assertFalse(match.isFinished)
    assertEquals("Round of 16 - 1st Leg", match.matchdayLabel)
    assertEquals("Santiago Bernabéu", match.stadium)
  }
}
