package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.SafeRideRepository
import com.example.data.local.*
import com.example.model.JourneyStage
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

  private lateinit var context: Context
  private lateinit var database: SafeRideDatabase

  @Before
  fun setUp() {
    context = ApplicationProvider.getApplicationContext<Context>()
    database = SafeRideDatabase.createInMemory(context)
  }

  @After
  fun tearDown() {
    database.close()
  }

  @Test
  fun testReadAppNameString() {
    val appName = context.getString(R.string.app_name)
    assertEquals("SafeRide", appName)
  }

  @Test
  fun testStudentDaoPersistence() = runBlocking {
    val student = StudentEntity(
      id = "std-test-1",
      name = "Zuberi Said",
      schoolName = "Feza Primary School",
      grade = "Darasa la 4B",
      parentEmail = "fatma.said@gmail.com",
      parentPhone = "+255 714 555 777",
      assignedBusPlate = "T 418 DKP",
      homeStopName = "Kimara",
      schoolStopName = "Feza Campus",
      authorizedPickupName = "Baba Zuberi",
      authorizedPickupPhone = "+255 714 555 777"
    )

    database.studentDao().insertStudent(student)
    val count = database.studentDao().getStudentCount()
    assertEquals(1, count)

    val retrieved = database.studentDao().getStudentById("std-test-1")
    assertNotNull(retrieved)
    assertEquals("Zuberi Said", retrieved?.name)
  }

  @Test
  fun testOfflineScanQueueAndSync() = runBlocking {
    // 1. Queue scan offline
    val scanId = database.offlineScanDao().insertScan(
      OfflineScanQueueEntity(
        studentName = "Neema Emmanuel",
        actionType = "BOARDING",
        busPlate = "T 418 DKP",
        timestamp = "06:47 AM",
        isSynced = false
      )
    )
    assertTrue(scanId > 0)

    val unsyncedList = database.offlineScanDao().getUnsyncedScansList()
    assertEquals(1, unsyncedList.size)
    assertFalse(unsyncedList.first().isSynced)

    // 2. Mark as synced
    database.offlineScanDao().markScanSynced(unsyncedList.first().id)
    val remainingUnsynced = database.offlineScanDao().getUnsyncedScansList()
    assertEquals(0, remainingUnsynced.size)

    // 3. Clear synced
    database.offlineScanDao().clearSyncedScans()
  }

  @Test
  fun testTripEventDaoPersistence() = runBlocking {
    val event = TripEventEntity(
      id = "ev-test-1",
      timestamp = "07:15 AM",
      titleSwahili = "Kituo cha Ubungo",
      titleEnglish = "Ubungo Stop",
      messageSwahili = "Basi limefika Ubungo",
      messageEnglish = "Bus reached Ubungo",
      stage = JourneyStage.ON_THE_WAY.name,
      locationName = "Ubungo",
      isSynced = false
    )

    database.tripEventDao().insertEvent(event)
    val unsynced = database.tripEventDao().getUnsyncedEvents()
    assertEquals(1, unsynced.size)

    database.tripEventDao().markEventsAsSynced(listOf("ev-test-1"))
    val remaining = database.tripEventDao().getUnsyncedEvents()
    assertEquals(0, remaining.size)
  }

  @Test
  fun testRepositoryWithRoomDatabase() = runBlocking {
    val repo = SafeRideRepository(database = database)
    assertNotNull(repo)
    assertTrue(repo.students.value.isNotEmpty())
  }
}
