package com.example

import com.example.data.SafeRideRepository
import com.example.model.AppLanguage
import com.example.model.JourneyStage
import com.example.model.UserRole
import com.example.util.AppStrings
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test

class ExampleUnitTest {

  private lateinit var repository: SafeRideRepository

  @Before
  fun setUp() {
    repository = SafeRideRepository()
  }

  @Test
  fun testInitialState() {
    assertEquals(UserRole.PARENT, repository.currentRole.value)
    assertEquals(AppLanguage.SWAHILI, repository.currentLanguage.value)
    assertTrue(repository.students.value.isNotEmpty())
    assertNotNull(repository.busRoute.value)
    assertTrue(repository.isUserLoggedIn.value)
  }

  @Test
  fun testLoginLogoutFlow() {
    repository.logout()
    assertFalse(repository.isUserLoggedIn.value)

    repository.login("parent.test@gmail.com", UserRole.PARENT)
    assertTrue(repository.isUserLoggedIn.value)
    assertEquals("parent.test@gmail.com", repository.loggedInUserEmail.value)
    assertEquals(UserRole.PARENT, repository.currentRole.value)
  }

  @Test
  fun testParentRegistrationFlow() {
    val reqId = repository.registerParent(
      fullName = "Said Juma",
      email = "said.juma@gmail.com",
      phone = "+255 712 999 888",
      schoolName = "Feza Primary School - Dar es Salaam",
      studentName = "Amina Said",
      grade = "Darasa la 2B"
    )
    assertNotNull(reqId)
    val created = repository.pendingRegistrations.value.find { it.id == reqId }
    assertNotNull(created)
    assertEquals("Said Juma", created?.parentName)
    assertEquals("Amina Said", created?.studentName)
    assertEquals("PENDING", created?.status)
  }

  @Test
  fun testBoardingVerificationFlow() {
    repository.verifyStudentBoarding("Neema Emmanuel", "BOARDING")
    assertEquals(JourneyStage.BOARDED, repository.journeyStage.value)
    assertTrue(repository.tripEvents.value.first().titleSwahili.contains("Amepanda"))
  }

  @Test
  fun testAdvanceNextStop() {
    val initialIndex = repository.busRoute.value.currentStopIndex
    repository.advanceToNextStop()
    assertEquals(initialIndex + 1, repository.busRoute.value.currentStopIndex)
  }

  @Test
  fun testTicketAntiReplayDuplicateCheck() {
    val firstAdded = repository.addTicketFromOcr("T 418 DKP", "Baraka", "Kimara - Feza", "TKT-TEST-999")
    assertTrue(firstAdded)
    val duplicateAdded = repository.addTicketFromOcr("T 418 DKP", "Baraka", "Kimara - Feza", "TKT-TEST-999")
    assertFalse("Duplicate ticket must be rejected by anti-replay rule", duplicateAdded)
  }

  @Test
  fun testLocalization() {
    assertEquals("Amepanda basi", AppStrings.stageBoarded(AppLanguage.SWAHILI))
    assertEquals("Boarded Bus", AppStrings.stageBoarded(AppLanguage.ENGLISH))
    assertEquals("Ingia (Sign In)", AppStrings.tabLogin(AppLanguage.SWAHILI))
    assertEquals("Sign In", AppStrings.tabLogin(AppLanguage.ENGLISH))
    assertEquals("Jisajili (Register)", AppStrings.tabRegister(AppLanguage.SWAHILI))
    assertEquals("Register", AppStrings.tabRegister(AppLanguage.ENGLISH))
  }

  @Test
  fun testLocaleStateToggleAndSet() {
    var callbackLanguage: AppLanguage? = null
    val localeState = com.example.ui.components.LocaleState(
      initialLanguage = AppLanguage.SWAHILI,
      onLanguageChanged = { callbackLanguage = it }
    )
    assertTrue(localeState.isSwahili)
    assertFalse(localeState.isEnglish)

    localeState.toggle()
    assertEquals(AppLanguage.ENGLISH, localeState.currentLanguage)
    assertEquals(AppLanguage.ENGLISH, callbackLanguage)
    assertTrue(localeState.isEnglish)

    localeState.setLanguage(AppLanguage.SWAHILI)
    assertEquals(AppLanguage.SWAHILI, localeState.currentLanguage)
    assertEquals(AppLanguage.SWAHILI, callbackLanguage)
  }

  @Test
  fun testAppCustomizationFlow() {
    // 1. Branding & Avatar
    repository.updateAppBranding("Feza SafeRide", "Safe Transit", "avatar_bus", "Mwalimu Juma")
    assertEquals("Feza SafeRide", repository.appCustomization.value.appTitle)
    assertEquals("avatar_bus", repository.appCustomization.value.profileAvatarUri)
    assertEquals("Mwalimu Juma", repository.appCustomization.value.adminProfileName)

    // 2. Theme & Dark Mode
    repository.updateTheme(com.example.model.ThemePreset.EMERALD_SAFETY, true)
    assertEquals(com.example.model.ThemePreset.EMERALD_SAFETY, repository.appCustomization.value.themePreset)
    assertTrue(repository.appCustomization.value.isDarkMode)

    // 3. School Details & Helpline
    repository.updateSchoolDetails("St. Jude Academy", "+255 788 111 222", "dispatch@stjude.ac.tz", 45)
    assertEquals("St. Jude Academy", repository.appCustomization.value.schoolName)
    assertEquals("+255 788 111 222", repository.appCustomization.value.emergencyPhone)
    assertEquals(45, repository.appCustomization.value.speedAlertLimitKmh)

    // 4. Bus Fleet
    repository.updateActiveBusDetails("T 999 ZZZ", "Driver Ally", "+255 711 000 111")
    assertEquals("T 999 ZZZ", repository.busRoute.value.busPlateNumber)
    assertEquals("T 999 ZZZ", repository.students.value.first().assignedBusPlate)

    // 5. Reset to defaults
    repository.resetCustomization()
    assertEquals("SafeRide", repository.appCustomization.value.appTitle)
    assertEquals(com.example.model.ThemePreset.NAVY_GOLD, repository.appCustomization.value.themePreset)
    assertFalse(repository.appCustomization.value.isDarkMode)
  }
}
