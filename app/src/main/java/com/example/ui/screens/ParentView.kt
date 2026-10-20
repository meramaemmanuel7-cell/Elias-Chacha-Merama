package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SafeRideRepository
import com.example.model.*
import com.example.ui.components.*
import com.example.ui.theme.*
import com.example.util.AppStrings

@Composable
fun ParentView(
  repository: SafeRideRepository,
  modifier: Modifier = Modifier
) {
  val language by repository.currentLanguage.collectAsState()
  val students by repository.students.collectAsState()
  val selectedStudent by repository.selectedStudent.collectAsState()
  val busRoute by repository.busRoute.collectAsState()
  val journeyStage by repository.journeyStage.collectAsState()
  val tripEvents by repository.tripEvents.collectAsState()
  val announcements by repository.announcements.collectAsState()

  var showTicketDialog by remember { mutableStateOf(false) }
  var plateSearchQuery by remember { mutableStateOf("") }
  var plateSearchResultMessage by remember { mutableStateOf<String?>(null) }
  var showHistoryDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Multi-Child Selector
    item {
      Text(
        text = if (language == AppLanguage.SWAHILI) "Chagua Mtoto:" else "Select Child:",
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = SafeRideGray
      )
      Spacer(modifier = Modifier.height(4.dp))
      Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        students.forEach { student ->
          val isSelected = student.id == selectedStudent.id
          FilterChip(
            selected = isSelected,
            onClick = { repository.selectStudent(student) },
            label = {
              Text(
                text = student.name,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            },
            leadingIcon = {
              Icon(
                imageVector = Icons.Default.Face,
                contentDescription = null,
                tint = if (isSelected) SafeRideAmberDark else SafeRideGray,
                modifier = Modifier.size(16.dp)
              )
            },
            modifier = Modifier.testTag("child_chip_${student.id}")
          )
        }
      }
    }

    // 2. Child Profile & School Info Card
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .padding(14.dp),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(SafeRideBlue.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.DirectionsBus,
                contentDescription = null,
                tint = SafeRideBlue,
                modifier = Modifier.size(24.dp)
              )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
              Text(
                text = selectedStudent.name,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text(
                text = "${selectedStudent.schoolName} (${selectedStudent.grade})",
                fontSize = 11.sp,
                color = SafeRideGray
              )
              Text(
                text = "${if (language == AppLanguage.SWAHILI) "Basi: " else "Assigned Bus: "}${selectedStudent.assignedBusPlate}",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = SafeRideAmberDark
              )
            }
          }

          IconButton(
            onClick = { showHistoryDialog = true },
            modifier = Modifier.testTag("view_history_button")
          ) {
            Icon(
              imageVector = Icons.Default.History,
              contentDescription = "Historia ya Safari",
              tint = SafeRideBlue
            )
          }
        }
      }
    }

    // 3. Journey Stage Tracker
    item {
      JourneyStageTracker(
        currentStage = journeyStage,
        language = language
      )
    }

    // 4. Quick Actions: Upload Ticket & Track by Plate
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        Button(
          onClick = { showTicketDialog = true },
          modifier = Modifier
            .weight(1f)
            .testTag("open_ticket_upload_button"),
          shape = RoundedCornerShape(12.dp),
          colors = ButtonDefaults.buttonColors(containerColor = SafeRideAmberDark)
        ) {
          Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = AppStrings.uploadTicket(language),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }

        OutlinedButton(
          onClick = {
            if (plateSearchQuery.isNotBlank()) {
              if (plateSearchQuery.trim().uppercase() == selectedStudent.assignedBusPlate) {
                plateSearchResultMessage = if (language == AppLanguage.SWAHILI)
                  "Gari linahusiana na mtoto wako! Ufuatiliaji unaendelea."
                else
                  "Bus matches your student's authorized transport!"
              } else {
                plateSearchResultMessage = if (language == AppLanguage.SWAHILI)
                  "Huna ruhusa ya kufuatilia gari hili (RBAC Security Rule)."
                else
                  "Unauthorized to track this vehicle (RBAC Security Rule)."
              }
            }
          },
          modifier = Modifier
            .weight(1f)
            .testTag("track_plate_button"),
          shape = RoundedCornerShape(12.dp)
        ) {
          Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = AppStrings.trackByPlate(language),
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }
    }

    // Plate search input box
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
      ) {
        OutlinedTextField(
          value = plateSearchQuery,
          onValueChange = {
            plateSearchQuery = it
            plateSearchResultMessage = null
          },
          placeholder = { Text(AppStrings.searchPlatePlaceholder(language), fontSize = 12.sp) },
          singleLine = true,
          modifier = Modifier
            .fillMaxWidth()
            .testTag("plate_search_field"),
          trailingIcon = {
            if (plateSearchQuery.isNotBlank()) {
              IconButton(onClick = { plateSearchQuery = "" }) {
                Icon(Icons.Default.Clear, contentDescription = "Futa")
              }
            }
          }
        )
      }
      if (plateSearchResultMessage != null) {
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = plateSearchResultMessage!!,
          fontSize = 11.sp,
          color = if (plateSearchResultMessage!!.contains("Huna") || plateSearchResultMessage!!.contains("Unauthorized")) SafeRideRed else SafeRideGreenDark,
          fontWeight = FontWeight.SemiBold
        )
      }
    }

    // 5. Interactive Live Map
    item {
      Text(
        text = AppStrings.liveMap(language),
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall
      )
      Spacer(modifier = Modifier.height(6.dp))
      LiveRouteMapView(
        busRoute = busRoute,
        language = language
      )
    }

    // 6. Bus Stops and ETA List
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = AppStrings.tripStops(language),
              fontWeight = FontWeight.Bold,
              fontSize = 14.sp
            )
            Text(
              text = "${if (language == AppLanguage.SWAHILI) "Kufika: " else "ETA: "}${busRoute.estimatedCompletion}",
              fontSize = 12.sp,
              color = SafeRideAmberDark,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          busRoute.stops.forEach { stop ->
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 6.dp),
              verticalAlignment = Alignment.CenterVertically,
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(
                      when (stop.status) {
                        StopStatus.PASSED -> SafeRideGreen
                        StopStatus.CURRENT -> SafeRideAmber
                        StopStatus.UPCOMING -> SafeRideGray
                      }
                    )
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = stop.name,
                    fontSize = 13.sp,
                    fontWeight = if (stop.status == StopStatus.CURRENT) FontWeight.Bold else FontWeight.Normal
                  )
                  if (stop.isHomeStop) {
                    Text(
                      text = if (language == AppLanguage.SWAHILI) "Kituo cha Nyumbani" else "Home Stop",
                      fontSize = 10.sp,
                      color = SafeRideGreenDark
                    )
                  } else if (stop.isSchoolStop) {
                    Text(
                      text = if (language == AppLanguage.SWAHILI) "Kituo cha Shuleni" else "School Stop",
                      fontSize = 10.sp,
                      color = SafeRideBlue
                    )
                  }
                }
              }

              Surface(
                color = when (stop.status) {
                  StopStatus.PASSED -> SafeRideGreenLight
                  StopStatus.CURRENT -> SafeRideAmberLight
                  StopStatus.UPCOMING -> SafeRideGrayLight
                },
                shape = RoundedCornerShape(6.dp)
              ) {
                Text(
                  text = when (stop.status) {
                    StopStatus.PASSED -> AppStrings.statusPassed(language)
                    StopStatus.CURRENT -> AppStrings.statusCurrent(language)
                    StopStatus.UPCOMING -> stop.estimatedArrival
                  },
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  color = when (stop.status) {
                    StopStatus.PASSED -> SafeRideGreenDark
                    StopStatus.CURRENT -> SafeRideAmberDark
                    StopStatus.UPCOMING -> SafeRideGray
                  },
                  modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
              }
            }
          }
        }
      }
    }

    // 7. Live Trip Events & Notifications Feed (with SMS Fallback)
    item {
      Text(
        text = if (language == AppLanguage.SWAHILI) "Taarifa na Arifa za Moja kwa Moja" else "Real-Time Trip Events & Alerts",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall
      )
    }

    items(tripEvents) { event ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("event_card_${event.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
          containerColor = if (event.isEmergency) SafeRideRedLight else MaterialTheme.colorScheme.surface
        )
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (event.isEmergency) Icons.Default.Warning else Icons.Default.NotificationsActive,
                contentDescription = null,
                tint = if (event.isEmergency) SafeRideRed else SafeRideBlue,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (language == AppLanguage.SWAHILI) event.titleSwahili else event.titleEnglish,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = if (event.isEmergency) SafeRideRed else MaterialTheme.colorScheme.onSurface
              )
            }
            Text(
              text = event.timestamp,
              fontSize = 11.sp,
              color = SafeRideGray
            )
          }

          Spacer(modifier = Modifier.height(4.dp))

          Text(
            text = if (language == AppLanguage.SWAHILI) event.messageSwahili else event.messageEnglish,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )

          if (event.smsFallbackSent) {
            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Sms,
                contentDescription = null,
                tint = SafeRideGreenDark,
                modifier = Modifier.size(12.dp)
              )
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = AppStrings.smsFallbackNotice(language),
                fontSize = 10.sp,
                color = SafeRideGreenDark
              )
            }
          }
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // Dialogs
  if (showTicketDialog) {
    TicketUploadDialog(
      language = language,
      defaultStudentName = selectedStudent.name,
      onDismiss = { showTicketDialog = false },
      onSubmitTicket = { plate, student, route, ticketNo ->
        repository.addTicketFromOcr(plate, student, route, ticketNo)
      }
    )
  }

  if (showHistoryDialog) {
    AlertDialog(
      onDismissRequest = { showHistoryDialog = false },
      confirmButton = {
        Button(onClick = { showHistoryDialog = false }) {
          Text(if (language == AppLanguage.SWAHILI) "Pakua Ripoti (PDF)" else "Download PDF")
        }
      },
      dismissButton = {
        TextButton(onClick = { showHistoryDialog = false }) {
          Text(if (language == AppLanguage.SWAHILI) "Funga" else "Close")
        }
      },
      title = {
        Text(if (language == AppLanguage.SWAHILI) "Historia ya Safari - Siku 30" else "30-Day Trip History")
      },
      text = {
        Column {
          Text(
            text = "${if (language == AppLanguage.SWAHILI) "Mwanafunzi: " else "Student: "}${selectedStudent.name}",
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(
            text = "• 17 Oktoba 2026: Amefika Shuleni 07:44 AM (Dakika 2 mapema)\n" +
                   "• 16 Oktoba 2026: Amefika Shuleni 07:46 AM (Salama)\n" +
                   "• 15 Oktoba 2026: Amefika Shuleni 07:41 AM (Salama)\n" +
                   "• 14 Oktoba 2026: Amefika Shuleni 07:50 AM (Foleni Mbezi)",
            fontSize = 12.sp,
            lineHeight = 18.sp
          )
          Spacer(modifier = Modifier.height(10.dp))
          Text(
            text = if (language == AppLanguage.SWAHILI)
              "Sera ya Uhifadhi: Data za kina za GPS zimehifadhiwa kwa miezi 12 kwa mujibu wa PDPA 2022."
            else
              "Retention Policy: Granular GPS data preserved for 12 months under PDPA 2022.",
            fontSize = 10.sp,
            color = SafeRideGray
          )
        }
      }
    )
  }
}
