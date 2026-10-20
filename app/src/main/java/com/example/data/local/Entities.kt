package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.model.JourneyStage
import com.example.model.Student
import com.example.model.Ticket
import com.example.model.TripEvent

@Entity(tableName = "students")
data class StudentEntity(
  @PrimaryKey val id: String,
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
) {
  fun toDomain(): Student = Student(
    id = id,
    name = name,
    schoolName = schoolName,
    grade = grade,
    parentEmail = parentEmail,
    parentPhone = parentPhone,
    assignedBusPlate = assignedBusPlate,
    homeStopName = homeStopName,
    schoolStopName = schoolStopName,
    authorizedPickupName = authorizedPickupName,
    authorizedPickupPhone = authorizedPickupPhone,
    isVerifiedByAdmin = isVerifiedByAdmin
  )

  companion object {
    fun fromDomain(s: Student): StudentEntity = StudentEntity(
      id = s.id,
      name = s.name,
      schoolName = s.schoolName,
      grade = s.grade,
      parentEmail = s.parentEmail,
      parentPhone = s.parentPhone,
      assignedBusPlate = s.assignedBusPlate,
      homeStopName = s.homeStopName,
      schoolStopName = s.schoolStopName,
      authorizedPickupName = s.authorizedPickupName,
      authorizedPickupPhone = s.authorizedPickupPhone,
      isVerifiedByAdmin = s.isVerifiedByAdmin
    )
  }
}

@Entity(tableName = "trip_events")
data class TripEventEntity(
  @PrimaryKey val id: String,
  val timestamp: String,
  val titleSwahili: String,
  val titleEnglish: String,
  val messageSwahili: String,
  val messageEnglish: String,
  val stage: String,
  val locationName: String,
  val smsFallbackSent: Boolean = true,
  val isEmergency: Boolean = false,
  val isSynced: Boolean = true,
  val createdAtEpoch: Long = System.currentTimeMillis()
) {
  fun toDomain(): TripEvent = TripEvent(
    id = id,
    timestamp = timestamp,
    titleSwahili = titleSwahili,
    titleEnglish = titleEnglish,
    messageSwahili = messageSwahili,
    messageEnglish = messageEnglish,
    stage = try { JourneyStage.valueOf(stage) } catch (e: Exception) { JourneyStage.ON_THE_WAY },
    locationName = locationName,
    smsFallbackSent = smsFallbackSent,
    isEmergency = isEmergency
  )

  companion object {
    fun fromDomain(e: TripEvent, isSynced: Boolean = true): TripEventEntity = TripEventEntity(
      id = e.id,
      timestamp = e.timestamp,
      titleSwahili = e.titleSwahili,
      titleEnglish = e.titleEnglish,
      messageSwahili = e.messageSwahili,
      messageEnglish = e.messageEnglish,
      stage = e.stage.name,
      locationName = e.locationName,
      smsFallbackSent = e.smsFallbackSent,
      isEmergency = e.isEmergency,
      isSynced = isSynced
    )
  }
}

@Entity(tableName = "offline_scan_queue")
data class OfflineScanQueueEntity(
  @PrimaryKey(autoGenerate = true) val id: Long = 0,
  val studentName: String,
  val actionType: String, // BOARDING or DROPOFF
  val busPlate: String,
  val timestamp: String,
  val latitude: Double = -6.7900,
  val longitude: Double = 39.1800,
  val isSynced: Boolean = false,
  val createdAtEpoch: Long = System.currentTimeMillis()
)

@Entity(tableName = "tickets")
data class TicketEntity(
  @PrimaryKey val ticketNumber: String,
  val busPlateNumber: String,
  val studentName: String,
  val routeName: String,
  val issueDate: String,
  val digitalSignatureHash: String,
  val isVerified: Boolean = true,
  val isUsed: Boolean = false
) {
  fun toDomain(): Ticket = Ticket(
    id = "tkt-$ticketNumber",
    ticketNumber = ticketNumber,
    busPlateNumber = busPlateNumber,
    studentName = studentName,
    routeName = routeName,
    issueDate = issueDate,
    digitalSignatureHash = digitalSignatureHash,
    isVerified = isVerified,
    isUsed = isUsed
  )

  companion object {
    fun fromDomain(t: Ticket): TicketEntity = TicketEntity(
      ticketNumber = t.ticketNumber,
      busPlateNumber = t.busPlateNumber,
      studentName = t.studentName,
      routeName = t.routeName,
      issueDate = t.issueDate,
      digitalSignatureHash = t.digitalSignatureHash,
      isVerified = t.isVerified,
      isUsed = t.isUsed
    )
  }
}
