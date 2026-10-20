package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StudentDao {
  @Query("SELECT * FROM students ORDER BY name ASC")
  fun getAllStudents(): Flow<List<StudentEntity>>

  @Query("SELECT * FROM students WHERE id = :id LIMIT 1")
  suspend fun getStudentById(id: String): StudentEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudents(students: List<StudentEntity>)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertStudent(student: StudentEntity)

  @Update
  suspend fun updateStudent(student: StudentEntity)

  @Query("SELECT COUNT(*) FROM students")
  suspend fun getStudentCount(): Int
}

@Dao
interface TripEventDao {
  @Query("SELECT * FROM trip_events ORDER BY createdAtEpoch DESC")
  fun getAllEvents(): Flow<List<TripEventEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvent(event: TripEventEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertEvents(events: List<TripEventEntity>)

  @Query("SELECT * FROM trip_events WHERE isSynced = 0 ORDER BY createdAtEpoch ASC")
  suspend fun getUnsyncedEvents(): List<TripEventEntity>

  @Query("UPDATE trip_events SET isSynced = 1 WHERE id IN (:ids)")
  suspend fun markEventsAsSynced(ids: List<String>)

  @Query("SELECT COUNT(*) FROM trip_events")
  suspend fun getEventCount(): Int
}

@Dao
interface OfflineScanDao {
  @Query("SELECT * FROM offline_scan_queue WHERE isSynced = 0 ORDER BY createdAtEpoch ASC")
  fun getUnsyncedScans(): Flow<List<OfflineScanQueueEntity>>

  @Query("SELECT * FROM offline_scan_queue WHERE isSynced = 0 ORDER BY createdAtEpoch ASC")
  suspend fun getUnsyncedScansList(): List<OfflineScanQueueEntity>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertScan(scan: OfflineScanQueueEntity): Long

  @Query("UPDATE offline_scan_queue SET isSynced = 1 WHERE id = :id")
  suspend fun markScanSynced(id: Long)

  @Query("DELETE FROM offline_scan_queue WHERE isSynced = 1")
  suspend fun clearSyncedScans()

  @Query("SELECT COUNT(*) FROM offline_scan_queue WHERE isSynced = 0")
  fun getUnsyncedCount(): Flow<Int>
}

@Dao
interface TicketDao {
  @Query("SELECT * FROM tickets ORDER BY issueDate DESC")
  fun getAllTickets(): Flow<List<TicketEntity>>

  @Query("SELECT * FROM tickets WHERE ticketNumber = :ticketNumber LIMIT 1")
  suspend fun getTicketByNumber(ticketNumber: String): TicketEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTicket(ticket: TicketEntity)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertTickets(tickets: List<TicketEntity>)
}
