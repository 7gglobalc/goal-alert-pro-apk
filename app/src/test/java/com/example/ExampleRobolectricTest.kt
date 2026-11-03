package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.LeagueId
import com.example.service.LicenseManager
import com.example.service.MatchSimulationEngine
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  @Test
  fun `read app name string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("GoalAlert", appName)
  }

  @Test
  fun `test license manager passcode validation and lock state`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val licenseManager = LicenseManager(context)

    // Initially or upon lock
    licenseManager.lockApp()
    assertFalse("Should be locked initially", licenseManager.isActivated())

    // Invalid code fails
    val invalidAttempt = licenseManager.activateWithCode("1111")
    assertFalse("Invalid code should not unlock", invalidAttempt)
    assertFalse("App should remain locked", licenseManager.isActivated())

    // Master Admin PIN munali0736 succeeds
    val adminAttempt = licenseManager.activateWithCode("munali0736")
    assertTrue("Admin PIN munali0736 should unlock", adminAttempt)
    assertTrue("App should now be activated", licenseManager.isActivated())

    // Relock and test numeric keypad shortcut 0736
    licenseManager.lockApp()
    assertFalse("Should be relocked", licenseManager.isActivated())
    val shortcutAttempt = licenseManager.activateWithCode("0736")
    assertTrue("Keypad shortcut 0736 should unlock", shortcutAttempt)
    assertTrue("App should now be activated", licenseManager.isActivated())

    // Relock and test unique device activation PIN
    licenseManager.lockApp()
    assertFalse("Should be relocked", licenseManager.isActivated())
    val deviceId = licenseManager.deviceId
    val devicePin = LicenseManager.generateDeviceActivationPin(deviceId)
    assertEquals(4, devicePin.length)
    val devicePinUnlock = licenseManager.activateWithCode(devicePin)
    assertTrue("Unique device activation PIN should unlock this device", devicePinUnlock)
    assertTrue("App should now be activated", licenseManager.isActivated())

    // Verify PIN generated for different device does NOT unlock this device
    val otherDevicePin = LicenseManager.generateDeviceActivationPin("ZZZZ99")
    val isValidForCurrent = LicenseManager.isValidPinForDevice(otherDevicePin, deviceId)
    // If they happen to match by hash collision (1 in 9000), check other pin
    val distinctPin = if (otherDevicePin == devicePin) "0001" else otherDevicePin
    assertFalse("A PIN generated for a different device must not validate", LicenseManager.isValidPinForDevice(distinctPin, deviceId))
  }

  @OptIn(ExperimentalCoroutinesApi::class)
  @Test
  fun `test goal trigger updates score and adds goal event`() = runTest {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val testDispatcher = StandardTestDispatcher(testScheduler)
    val testScope = TestScope(testDispatcher)
    val engine = MatchSimulationEngine(context, testScope)

    val initialMatches = engine.matches.value
    assertTrue("Should have initial matches", initialMatches.isNotEmpty())

    val firstMatch = initialMatches.first()
    val initialGoalsCount = engine.goalEvents.value.size

    val newGoal = engine.triggerGoal(
      matchId = firstMatch.id,
      scorer = "Erling Haaland",
      assist = "Kevin De Bruyne",
      isHomeTeam = true
    )

    assertNotNull("New goal should be generated", newGoal)
    assertEquals(firstMatch.homeScore + 1, newGoal?.homeScore)
    assertEquals(initialGoalsCount + 1, engine.goalEvents.value.size)
  }

  @Test
  fun `test favorites manager toggle and persistence`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val favoritesManager = com.example.service.FavoritesManager(context)

    val testMatchId = "test_match_99"
    // Ensure clean state for testMatchId
    favoritesManager.setFavorite(testMatchId, false)
    assertFalse("Should not be favorite initially", favoritesManager.isFavorite(testMatchId))

    // Toggle on
    val isFavNow = favoritesManager.toggleFavorite(testMatchId)
    assertTrue("Should return true after toggle on", isFavNow)
    assertTrue("isFavorite should return true", favoritesManager.isFavorite(testMatchId))

    // Toggle off
    val isFavOff = favoritesManager.toggleFavorite(testMatchId)
    assertFalse("Should return false after toggle off", isFavOff)
    assertFalse("isFavorite should return false", favoritesManager.isFavorite(testMatchId))
  }

  @Test
  fun `test favorite goal notification service channel and trigger`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val favService = com.example.service.FavoriteGoalNotificationService(context)

    // Non-favorite match should NOT trigger
    val nonFavResult = favService.triggerIfFavoriteGoal(
      matchId = "non_favorite_match_id",
      leagueName = "Premier League",
      homeTeamName = "Wolves",
      awayTeamName = "Brentford",
      scoringTeamName = "Wolves",
      scorer = "Matheus Cunha",
      assist = "Mario Lemina",
      minute = 64,
      homeScore = 1,
      awayScore = 0,
      forceTrigger = false
    )
    assertFalse("Non-favorite match should return false", nonFavResult)

    // Add to favorites and trigger
    favService.favoritesManager.setFavorite("fav_match_100", true)
    val favResult = favService.triggerIfFavoriteGoal(
      matchId = "fav_match_100",
      leagueName = "Premier League",
      homeTeamName = "Arsenal",
      awayTeamName = "Chelsea",
      scoringTeamName = "Arsenal",
      scorer = "Bukayo Saka",
      assist = "Martin Odegaard",
      minute = 77,
      homeScore = 2,
      awayScore = 1,
      forceTrigger = false
    )
    assertTrue("Favorited match should return true and trigger sound/alert", favResult)
  }
}

