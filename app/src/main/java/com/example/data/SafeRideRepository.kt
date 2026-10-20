package com.example.data

import com.example.data.local.*
import com.example.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SafeRideRepository(private val database: SafeRideDatabase? = null) {

  private val coroutineScope = CoroutineScope(Dispatchers.Default)

  init {
    database?.let { db ->
      // Seed initial data if empty
      coroutineScope.launch {
        try {
          if (db.studentDao().getStudentCount() == 0) {
            db.studentDao().insertStudents(_students.value.map { StudentEntity.fromDomain(it) })
          }
          if (db.tripEventDao().getEventCount() == 0) {
            db.tripEventDao().insertEvents(_tripEvents.value.map { TripEventEntity.fromDomain(it) })
          }
        } catch (e: Exception) {
          // Fallback gracefully
        }
      }

      // Collect Students reactively from Room
      coroutineScope.launch {
        try {
          db.studentDao().getAllStudents().collect { entities ->
            if (entities.isNotEmpty()) {
              val domainList = entities.map { it.toDomain() }
              _students.value = domainList
              val curr = _selectedStudent.value
              domainList.find { it.id == curr.id }?.let { _selectedStudent.value = it }
            }
          }
        } catch (e: Exception) {
          // Fallback gracefully
        }
      }

      // Collect Trip Events reactively from Room
      coroutineScope.launch {
        try {
          db.tripEventDao().getAllEvents().collect { entities ->
            if (entities.isNotEmpty()) {
              _tripEvents.value = entities.map { it.toDomain() }
            }
          }
        } catch (e: Exception) {
          // Fallback gracefully
        }
      }

      // Collect Unsynced Scan Queue Count from Room
      coroutineScope.launch {
        try {
          db.offlineScanDao().getUnsyncedCount().collect { count ->
            _offlineQueuedCount.value = count
          }
        } catch (e: Exception) {
          // Fallback gracefully
        }
      }
    }
  }

  // User State
  private val _isUserLoggedIn = MutableStateFlow(true)
  val isUserLoggedIn: StateFlow<Boolean> = _isUserLoggedIn.asStateFlow()

  private val _loggedInUserEmail = MutableStateFlow("meramaemmanuel7@gmail.com")
  val loggedInUserEmail: StateFlow<String> = _loggedInUserEmail.asStateFlow()

  private val _loggedInUserName = MutableStateFlow("Emmanuel Merama")
  val loggedInUserName: StateFlow<String> = _loggedInUserName.asStateFlow()

  private val _currentRole = MutableStateFlow(UserRole.PARENT)
  val currentRole: StateFlow<UserRole> = _currentRole.asStateFlow()

  private val _currentLanguage = MutableStateFlow(AppLanguage.SWAHILI)
  val currentLanguage: StateFlow<AppLanguage> = _currentLanguage.asStateFlow()

  // App Customization State (Admin controllable)
  private val _appCustomization = MutableStateFlow(AppCustomization())
  val appCustomization: StateFlow<AppCustomization> = _appCustomization.asStateFlow()

  // Students
  private val _students = MutableStateFlow(
    listOf(
      Student(
        id = "std-001",
        name = "Neema Emmanuel",
        schoolName = "Feza Primary School - Dar es Salaam",
        grade = "Darasa la 4B",
        parentEmail = "meramaemmanuel7@gmail.com",
        parentPhone = "+255 754 123 456",
        assignedBusPlate = "T 418 DKP",
        homeStopName = "Kimara Korogwe (Mwisho)",
        schoolStopName = "Feza Primary Campus",
        authorizedPickupName = "Mama Baraka (Amina)",
        authorizedPickupPhone = "+255 784 987 654"
      ),
      Student(
        id = "std-002",
        name = "Baraka Emmanuel",
        schoolName = "Feza Primary School - Dar es Salaam",
        grade = "Darasa la 1A",
        parentEmail = "meramaemmanuel7@gmail.com",
        parentPhone = "+255 754 123 456",
        assignedBusPlate = "T 418 DKP",
        homeStopName = "Kimara Korogwe (Mwisho)",
        schoolStopName = "Feza Primary Campus",
        authorizedPickupName = "Baba Neema (Emmanuel)",
        authorizedPickupPhone = "+255 754 123 456"
      )
    )
  )
  val students: StateFlow<List<Student>> = _students.asStateFlow()

  private val _selectedStudent = MutableStateFlow(_students.value.first())
  val selectedStudent: StateFlow<Student> = _selectedStudent.asStateFlow()

  // Bus Route & Live Tracking
  private val initialStops = listOf(
    BusStop("st-1", "Kimara Korogwe (Nyumbani)", "06:45 AM", StopStatus.PASSED, isHomeStop = true, latitude = -6.7900, longitude = 39.1800),
    BusStop("st-2", "Kimara Temboni", "06:55 AM", StopStatus.PASSED, latitude = -6.7925, longitude = 39.1920),
    BusStop("st-3", "Mbezi Louis Bus Terminal", "07:10 AM", StopStatus.CURRENT, latitude = -6.7980, longitude = 39.2050),
    BusStop("st-4", "Ubungo Interchange", "07:22 AM", StopStatus.UPCOMING, latitude = -6.7850, longitude = 39.2220),
    BusStop("st-5", "Magomeni Mapipa", "07:35 AM", StopStatus.UPCOMING, latitude = -6.8020, longitude = 39.2480),
    BusStop("st-6", "Feza Primary Campus (Shuleni)", "07:45 AM", StopStatus.UPCOMING, isSchoolStop = true, latitude = -6.7720, longitude = 39.2590)
  )

  private val _busRoute = MutableStateFlow(
    BusRoute(
      id = "route-101",
      routeName = "Ruti A: Kimara - Mbezi - Ubungo - Feza",
      busPlateNumber = "T 418 DKP",
      driverName = "Mzee Juma Hamisi",
      driverPhone = "+255 712 345 678",
      currentSpeedKmh = 38,
      direction = RouteDirection.HOME_TO_SCHOOL,
      stops = initialStops,
      currentStopIndex = 2,
      activeEmergency = null,
      isGpsBroadcasting = true,
      estimatedCompletion = "07:45 AM"
    )
  )
  val busRoute: StateFlow<BusRoute> = _busRoute.asStateFlow()

  private val _journeyStage = MutableStateFlow(JourneyStage.ON_THE_WAY)
  val journeyStage: StateFlow<JourneyStage> = _journeyStage.asStateFlow()

  // Trip Events Feed
  private val _tripEvents = MutableStateFlow(
    listOf(
      TripEvent(
        id = "ev-01",
        timestamp = "06:47 AM",
        titleSwahili = "Mtoto Amepanda Basi",
        titleEnglish = "Student Boarded Bus",
        messageSwahili = "Neema Emmanuel amepanda basi T 418 DKP kituo cha Kimara Korogwe.",
        messageEnglish = "Neema Emmanuel has boarded bus T 418 DKP at Kimara Korogwe stop.",
        stage = JourneyStage.BOARDED,
        locationName = "Kimara Korogwe"
      ),
      TripEvent(
        id = "ev-02",
        timestamp = "07:10 AM",
        titleSwahili = "Kituo cha Mbezi Louis",
        titleEnglish = "Mbezi Louis Stop",
        messageSwahili = "Basi limewasili Mbezi Louis. Safari inaendelea kwa usalama.",
        messageEnglish = "Bus arrived at Mbezi Louis. Journey continuing safely.",
        stage = JourneyStage.ON_THE_WAY,
        locationName = "Mbezi Louis"
      )
    )
  )
  val tripEvents: StateFlow<List<TripEvent>> = _tripEvents.asStateFlow()

  // Verified Tickets
  private val _tickets = MutableStateFlow(
    listOf(
      Ticket(
        id = "tkt-01",
        ticketNumber = "SR-2026-9842",
        busPlateNumber = "T 418 DKP",
        studentName = "Neema Emmanuel",
        routeName = "Kimara - Feza Campus",
        issueDate = "18 Oktoba 2026",
        digitalSignatureHash = "e3b0c44298fc1c149afbf4c8996fb92427ae41e4649b934ca495991b7852b855",
        isVerified = true,
        isUsed = true
      )
    )
  )
  val tickets: StateFlow<List<Ticket>> = _tickets.asStateFlow()

  // Announcements
  private val _announcements = MutableStateFlow(
    listOf(
      Announcement(
        id = "anc-1",
        title = "Ukarabati wa Barabara ya Morogoro",
        content = "Kutokana na ujenzi karibu na Ubungo, safari za asubuhi zinaweza kuchelewa kwa dakika 10. Madereva wameelekezwa kutumia njia mbadala.",
        timestamp = "Leo 06:15 AM",
        author = "Utawala wa Shule - Feza"
      ),
      Announcement(
        id = "anc-2",
        title = "Ukaguzi wa Kadi za Kielektroniki (QR Cards)",
        content = "Wanafunzi wote wanatakiwa kuvaa kadi zao shingoni kabla ya kupanda gari ili kurahisisha scanner ya conductor.",
        timestamp = "Jana 04:30 PM",
        author = "Mkuu wa Usafiri"
      )
    )
  )
  val announcements: StateFlow<List<Announcement>> = _announcements.asStateFlow()

  // Audit Logs (CIA Triad Tamper-evident)
  private val _auditLogs = MutableStateFlow(
    listOf(
      AuditLog("log-1", "06:45:12 AM", "driver_juma", "DRIVER", "GPS_BROADCAST_START", "196.249.102.14", "sha256-a1b2c3d4"),
      AuditLog("log-2", "06:47:04 AM", "conductor_ally", "CONDUCTOR", "BOARDING_VERIFIED_QR (Student: Neema)", "196.249.102.14", "sha256-b2c3d4e5"),
      AuditLog("log-3", "07:10:19 AM", "system", "SYSTEM", "GEOFENCE_ENTER (Stop: Mbezi Louis)", "10.0.0.1", "sha256-c3d4e5f6")
    )
  )
  val auditLogs: StateFlow<List<AuditLog>> = _auditLogs.asStateFlow()

  // Parent Registrations
  private val _pendingRegistrations = MutableStateFlow(
    listOf(
      ParentRegistrationRequest(
        id = "req-101",
        parentName = "Fatma Said",
        parentEmail = "fatma.said@gmail.com",
        parentPhone = "+255 714 555 777",
        studentName = "Zuberi Said",
        schoolName = "Feza Primary School",
        dateSubmitted = "18 Oktoba 2026",
        status = "PENDING"
      )
    )
  )
  val pendingRegistrations: StateFlow<List<ParentRegistrationRequest>> = _pendingRegistrations.asStateFlow()

  // Offline Mode for Tanzanian Low-Connectivity Environment
  private val _isOfflineMode = MutableStateFlow(false)
  val isOfflineMode: StateFlow<Boolean> = _isOfflineMode.asStateFlow()

  private val _offlineQueuedCount = MutableStateFlow(0)
  val offlineQueuedCount: StateFlow<Int> = _offlineQueuedCount.asStateFlow()

  // User Actions
  fun setRole(role: UserRole) {
    _currentRole.value = role
  }

  fun setLanguage(language: AppLanguage) {
    _currentLanguage.value = language
  }

  fun selectStudent(student: Student) {
    _selectedStudent.value = student
  }

  fun toggleOfflineMode() {
    val newMode = !_isOfflineMode.value
    _isOfflineMode.value = newMode
    if (!newMode) {
      if (database != null) {
        coroutineScope.launch {
          try {
            val unsynced = database.offlineScanDao().getUnsyncedScansList()
            if (unsynced.isNotEmpty()) {
              unsynced.forEach { scan ->
                database.offlineScanDao().markScanSynced(scan.id)
              }
              database.offlineScanDao().clearSyncedScans()
              val unsyncedEvents = database.tripEventDao().getUnsyncedEvents()
              if (unsyncedEvents.isNotEmpty()) {
                database.tripEventDao().markEventsAsSynced(unsyncedEvents.map { it.id })
              }
              addAuditLog("conductor_offline_sync", "CONDUCTOR", "OFFLINE_BATCH_SYNCED (${unsynced.size} records via Room SQLite)")
            }
          } catch (e: Exception) {
            // Log fallback
          }
        }
      } else if (_offlineQueuedCount.value > 0) {
        val count = _offlineQueuedCount.value
        _offlineQueuedCount.value = 0
        addAuditLog("conductor_offline_sync", "CONDUCTOR", "OFFLINE_BATCH_SYNCED ($count records)")
      }
    }
  }

  fun login(email: String, role: UserRole) {
    _isUserLoggedIn.value = true
    _loggedInUserEmail.value = email
    _currentRole.value = role
    addAuditLog(email, role.name, "USER_LOGIN_SUCCESS")
  }

  fun logout() {
    _isUserLoggedIn.value = false
    addAuditLog(_loggedInUserEmail.value, _currentRole.value.name, "USER_LOGOUT")
  }

  fun registerParent(
    fullName: String,
    email: String,
    phone: String,
    schoolName: String,
    studentName: String,
    grade: String
  ): String {
    val reqId = "req-${System.currentTimeMillis()}"
    val newReq = ParentRegistrationRequest(
      id = reqId,
      parentName = fullName,
      parentEmail = email,
      parentPhone = phone,
      studentName = studentName,
      schoolName = schoolName,
      dateSubmitted = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
      status = "PENDING"
    )
    _pendingRegistrations.value = listOf(newReq) + _pendingRegistrations.value
    addAuditLog(email, "PARENT", "PARENT_REGISTERED_PENDING_APPROVAL (Student: $studentName)")
    return reqId
  }

  // Admin Customization Methods
  fun updateAppBranding(appTitle: String, subtitle: String, profileAvatar: String, adminName: String) {
    _appCustomization.value = _appCustomization.value.copy(
      appTitle = appTitle,
      appSubtitle = subtitle,
      profileAvatarUri = profileAvatar,
      adminProfileName = adminName
    )
    addAuditLog(_loggedInUserEmail.value, "ADMIN", "APP_BRANDING_UPDATED ($appTitle, Avatar: $profileAvatar)")
  }

  fun updateTheme(preset: ThemePreset, isDark: Boolean) {
    _appCustomization.value = _appCustomization.value.copy(
      themePreset = preset,
      isDarkMode = isDark
    )
    addAuditLog(_loggedInUserEmail.value, "ADMIN", "THEME_UPDATED ($preset, DarkMode=$isDark)")
  }

  fun updateSchoolDetails(schoolName: String, emergencyPhone: String, dispatchEmail: String, speedLimit: Int) {
    _appCustomization.value = _appCustomization.value.copy(
      schoolName = schoolName,
      emergencyPhone = emergencyPhone,
      dispatchEmail = dispatchEmail,
      speedAlertLimitKmh = speedLimit
    )
    _busRoute.value = _busRoute.value.copy(driverPhone = emergencyPhone)
    addAuditLog(_loggedInUserEmail.value, "ADMIN", "SCHOOL_DETAILS_UPDATED ($schoolName)")
  }

  fun updateActiveBusDetails(plateNumber: String, driverName: String, driverPhone: String) {
    _busRoute.value = _busRoute.value.copy(
      busPlateNumber = plateNumber,
      driverName = driverName,
      driverPhone = driverPhone
    )
    _students.value = _students.value.map { it.copy(assignedBusPlate = plateNumber) }
    _selectedStudent.value = _selectedStudent.value.copy(assignedBusPlate = plateNumber)
    addAuditLog(_loggedInUserEmail.value, "ADMIN", "BUS_FLEET_UPDATED ($plateNumber, Driver: $driverName)")
  }

  fun updateStudentProfile(studentId: String, name: String, grade: String, pickupName: String, pickupPhone: String) {
    _students.value = _students.value.map { st ->
      if (st.id == studentId) {
        st.copy(
          name = name,
          grade = grade,
          authorizedPickupName = pickupName,
          authorizedPickupPhone = pickupPhone
        )
      } else st
    }
    if (_selectedStudent.value.id == studentId) {
      _selectedStudent.value = _selectedStudent.value.copy(
        name = name,
        grade = grade,
        authorizedPickupName = pickupName,
        authorizedPickupPhone = pickupPhone
      )
    }
    addAuditLog(_loggedInUserEmail.value, "ADMIN", "STUDENT_UPDATED ($name, Grade: $grade)")
  }

  fun resetCustomization() {
    _appCustomization.value = AppCustomization()
    addAuditLog(_loggedInUserEmail.value, "ADMIN", "APP_CUSTOMIZATION_RESET_TO_DEFAULTS")
  }

  // Simulated Conductor QR Scan
  fun verifyStudentBoarding(studentName: String, actionType: String) {
    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    val isBoarding = actionType == "BOARDING"

    val newStage = if (isBoarding) JourneyStage.BOARDED else JourneyStage.ARRIVED_SAFELY
    _journeyStage.value = newStage

    val event = TripEvent(
      id = "ev-${System.currentTimeMillis()}",
      timestamp = time,
      titleSwahili = if (isBoarding) "Mtoto Amepanda Basi" else "Mtoto Amefika Salama!",
      titleEnglish = if (isBoarding) "Student Boarded Bus" else "Student Arrived Safely!",
      messageSwahili = if (isBoarding) {
        "$studentName amepanda basi T 418 DKP. Tiketi na GPS zimethibitishwa."
      } else {
        "$studentName amefika salama Shuleni saa $time. Aliyempokea: Mwalimu Mkuu wa Zamu."
      },
      messageEnglish = if (isBoarding) {
        "$studentName boarded bus T 418 DKP. Ticket and GPS coordinates verified."
      } else {
        "$studentName arrived safely at School at $time. Received by: Duty Teacher."
      },
      stage = newStage,
      locationName = if (isBoarding) "Kimara Korogwe" else "Feza Primary Campus",
      smsFallbackSent = true
    )

    _tripEvents.value = listOf(event) + _tripEvents.value

    if (_isOfflineMode.value) {
      if (database != null) {
        coroutineScope.launch {
          try {
            database.offlineScanDao().insertScan(
              OfflineScanQueueEntity(
                studentName = studentName,
                actionType = actionType,
                busPlate = _busRoute.value.busPlateNumber,
                timestamp = time,
                isSynced = false
              )
            )
            database.tripEventDao().insertEvent(TripEventEntity.fromDomain(event, isSynced = false))
          } catch (e: Exception) {
            _offlineQueuedCount.value += 1
          }
        }
      } else {
        _offlineQueuedCount.value += 1
      }
    } else {
      if (database != null) {
        coroutineScope.launch {
          try {
            database.tripEventDao().insertEvent(TripEventEntity.fromDomain(event, isSynced = true))
          } catch (e: Exception) {
            // Log fallback
          }
        }
      }
      addAuditLog("conductor_qr_scanner", "CONDUCTOR", "QR_SCAN_VERIFIED ($studentName - $actionType)")
    }
  }

  // Advance Bus Stop (Simulation for demonstration)
  fun advanceToNextStop() {
    val route = _busRoute.value
    if (route.currentStopIndex < route.stops.size - 1) {
      val nextIndex = route.currentStopIndex + 1
      val updatedStops = route.stops.mapIndexed { index, stop ->
        when {
          index < nextIndex -> stop.copy(status = StopStatus.PASSED)
          index == nextIndex -> stop.copy(status = StopStatus.CURRENT)
          else -> stop.copy(status = StopStatus.UPCOMING)
        }
      }

      val currentStop = updatedStops[nextIndex]
      val newStage = when {
        currentStop.isSchoolStop -> JourneyStage.ARRIVED_SAFELY
        nextIndex == route.stops.size - 2 -> JourneyStage.NEAR_STOP
        else -> JourneyStage.ON_THE_WAY
      }
      _journeyStage.value = newStage

      _busRoute.value = route.copy(
        currentStopIndex = nextIndex,
        stops = updatedStops
      )

      val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
      val event = TripEvent(
        id = "ev-${System.currentTimeMillis()}",
        timestamp = time,
        titleSwahili = "Basi Limefika: ${currentStop.name}",
        titleEnglish = "Bus Arrived: ${currentStop.name}",
        messageSwahili = if (currentStop.isSchoolStop) "Mtoto amefika salama shuleni saa $time." else "Basi limefika kituo cha ${currentStop.name}.",
        messageEnglish = if (currentStop.isSchoolStop) "Student arrived safely at school at $time." else "Bus reached stop ${currentStop.name}.",
        stage = newStage,
        locationName = currentStop.name
      )
      _tripEvents.value = listOf(event) + _tripEvents.value

      if (database != null) {
        coroutineScope.launch {
          try {
            database.tripEventDao().insertEvent(TripEventEntity.fromDomain(event, isSynced = true))
          } catch (e: Exception) {}
        }
      }

      addAuditLog("gps_geofence", "SYSTEM", "ARRIVED_STOP_${currentStop.name.replace(" ", "_")}")
    }
  }

  // Trigger Emergency SOS
  fun triggerSOS(reason: String) {
    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    _busRoute.value = _busRoute.value.copy(activeEmergency = reason)

    val event = TripEvent(
      id = "ev-sos-${System.currentTimeMillis()}",
      timestamp = time,
      titleSwahili = "TAHADHARI YA DHARURA!",
      titleEnglish = "EMERGENCY ALERT!",
      messageSwahili = "Dereva ameripoti dharura: $reason. Utawala wa shule unafuatilia. Watoto wako salama ndani ya basi.",
      messageEnglish = "Driver reported emergency: $reason. School administration is handling. Students are safe inside bus.",
      stage = _journeyStage.value,
      locationName = "Ubungo Interchange",
      smsFallbackSent = true,
      isEmergency = true
    )
    _tripEvents.value = listOf(event) + _tripEvents.value

    if (database != null) {
      coroutineScope.launch {
        try {
          database.tripEventDao().insertEvent(TripEventEntity.fromDomain(event, isSynced = true))
        } catch (e: Exception) {}
      }
    }

    addAuditLog("driver_emergency", "DRIVER", "SOS_TRIGGERED: $reason")
  }

  fun clearEmergency() {
    _busRoute.value = _busRoute.value.copy(activeEmergency = null)
    addAuditLog("admin_emergency", "ADMIN", "SOS_CLEARED_BY_ADMIN")
  }

  // Add Ticket from OCR
  fun addTicketFromOcr(plate: String, student: String, route: String, ticketNo: String): Boolean {
    // Anti-replay and integrity check
    val existing = _tickets.value.find { it.ticketNumber == ticketNo }
    if (existing != null) {
      return false // already exists
    }

    val newTicket = Ticket(
      id = "tkt-${System.currentTimeMillis()}",
      ticketNumber = ticketNo,
      busPlateNumber = plate,
      studentName = student,
      routeName = route,
      issueDate = SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date()),
      digitalSignatureHash = "sha256-${ticketNo.hashCode().toUInt().toString(16)}",
      isVerified = true,
      isUsed = false
    )

    _tickets.value = listOf(newTicket) + _tickets.value

    if (database != null) {
      coroutineScope.launch {
        try {
          database.ticketDao().insertTicket(TicketEntity.fromDomain(newTicket))
        } catch (e: Exception) {}
      }
    }

    addAuditLog("parent_ocr", "PARENT", "TICKET_UPLOAD_OCR_VERIFIED (Ticket: $ticketNo)")
    return true
  }

  // Admin Actions
  fun approveRegistration(requestId: String) {
    _pendingRegistrations.value = _pendingRegistrations.value.map { req ->
      if (req.id == requestId) req.copy(status = "APPROVED") else req
    }
    addAuditLog("school_admin", "SCHOOL_ADMIN", "APPROVED_PARENT_REGISTRATION ($requestId)")
  }

  fun rejectRegistration(requestId: String) {
    _pendingRegistrations.value = _pendingRegistrations.value.map { req ->
      if (req.id == requestId) req.copy(status = "REJECTED") else req
    }
    addAuditLog("school_admin", "SCHOOL_ADMIN", "REJECTED_PARENT_REGISTRATION ($requestId)")
  }

  fun addAnnouncement(title: String, content: String) {
    val time = SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
    val anc = Announcement(
      id = "anc-${System.currentTimeMillis()}",
      title = title,
      content = content,
      timestamp = "Leo $time",
      author = "School Admin",
      targetBusPlate = null
    )
    _announcements.value = listOf(anc) + _announcements.value
    addAuditLog("school_admin", "SCHOOL_ADMIN", "BROADCAST_ANNOUNCEMENT ($title)")
  }

  private fun addAuditLog(actor: String, role: String, action: String) {
    val time = SimpleDateFormat("hh:mm:ss a", Locale.getDefault()).format(Date())
    val newLog = AuditLog(
      id = "log-${System.currentTimeMillis()}",
      timestamp = time,
      actor = actor,
      role = role,
      action = action,
      ipAddress = "196.249.102.14",
      integrityHash = "sha256-${System.currentTimeMillis().hashCode().toUInt().toString(16)}"
    )
    _auditLogs.value = listOf(newLog) + _auditLogs.value
  }
}
