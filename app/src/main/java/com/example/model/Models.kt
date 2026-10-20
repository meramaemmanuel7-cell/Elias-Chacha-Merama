package com.example.model

enum class UserRole {
  PARENT,
  DRIVER,
  SCHOOL_ADMIN,
  SUPER_ADMIN
}

enum class AppLanguage {
  SWAHILI,
  ENGLISH
}

enum class JourneyStage {
  NOT_STARTED,
  BOARDED,        // Amepanda basi
  ON_THE_WAY,     // Yupo njiani
  NEAR_STOP,      // Kituo cha karibu
  ARRIVED_SAFELY  // Amefika salama
}

enum class StopStatus {
  PASSED,
  CURRENT,
  UPCOMING
}

enum class RouteDirection {
  HOME_TO_SCHOOL, // Asubuhi (Home -> School)
  SCHOOL_TO_HOME  // Mchana/Jioni (School -> Home)
}

data class Student(
  val id: String,
  val name: String,
  val schoolName: String,
  val grade: String,
  val parentEmail: String,
  val parentPhone: String,
  val assignedBusPlate: String,
  val homeStopName: String,
  val schoolStopName: String,
  val authorizedPickupName: String,
  val authorizedPickupPhone: String,
  val isVerifiedByAdmin: Boolean = true
)

data class BusStop(
  val id: String,
  val name: String,
  val estimatedArrival: String,
  val status: StopStatus,
  val isHomeStop: Boolean = false,
  val isSchoolStop: Boolean = false,
  val latitude: Double = -6.7924,
  val longitude: Double = 39.2083
)

data class BusRoute(
  val id: String,
  val routeName: String,
  val busPlateNumber: String,
  val driverName: String,
  val driverPhone: String,
  val currentSpeedKmh: Int,
  val direction: RouteDirection,
  val stops: List<BusStop>,
  val currentStopIndex: Int,
  val activeEmergency: String? = null,
  val isGpsBroadcasting: Boolean = true,
  val estimatedCompletion: String = "07:45 AM"
)

data class Ticket(
  val id: String,
  val ticketNumber: String,
  val busPlateNumber: String,
  val studentName: String,
  val routeName: String,
  val issueDate: String,
  val digitalSignatureHash: String,
  val isVerified: Boolean = true,
  val isUsed: Boolean = false,
  val pickupStop: String = "Kimara Korogwe",
  val dropoffStop: String = "Feza Primary Campus"
)

data class TripEvent(
  val id: String,
  val timestamp: String,
  val titleSwahili: String,
  val titleEnglish: String,
  val messageSwahili: String,
  val messageEnglish: String,
  val stage: JourneyStage,
  val locationName: String,
  val smsFallbackSent: Boolean = true,
  val isEmergency: Boolean = false
)

data class Announcement(
  val id: String,
  val title: String,
  val content: String,
  val timestamp: String,
  val author: String,
  val targetBusPlate: String? = null
)

data class AuditLog(
  val id: String,
  val timestamp: String,
  val actor: String,
  val role: String,
  val action: String,
  val ipAddress: String,
  val integrityHash: String
)

data class ParentRegistrationRequest(
  val id: String,
  val parentName: String,
  val parentEmail: String,
  val parentPhone: String,
  val studentName: String,
  val schoolName: String,
  val dateSubmitted: String,
  val status: String // "PENDING", "APPROVED", "REJECTED"
)

enum class ThemePreset {
  NAVY_GOLD,      // SafeRide Original: Deep Navy & School Bus Amber
  ROYAL_BLUE,     // Royal Blue & Cyan
  EMERALD_SAFETY, // Emerald Green & Mint
  CRIMSON_GUARD,  // Crimson Safety Red & Warm Amber
  PURPLE_ACADEMY  // Royal Purple & Lavender
}

data class AppCustomization(
  val appTitle: String = "SafeRide",
  val appSubtitle: String = "Ufuatiliaji Salama wa Wanafunzi",
  val schoolName: String = "Feza Primary Campus - Dar es Salaam",
  val emergencyPhone: String = "+255 712 345 678",
  val dispatchEmail: String = "dispatch@feza.sc.tz",
  val themePreset: ThemePreset = ThemePreset.NAVY_GOLD,
  val isDarkMode: Boolean = false,
  val profileAvatarUri: String = "avatar_shield",
  val adminProfileName: String = "Emmanuel Merama (Mkuu wa Usafiri)",
  val speedAlertLimitKmh: Int = 50
)

