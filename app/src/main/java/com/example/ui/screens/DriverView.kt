package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import com.example.model.AppLanguage
import com.example.model.StopStatus
import com.example.ui.components.LiveRouteMapView
import com.example.ui.theme.*
import com.example.util.AppStrings

@Composable
fun DriverView(
  repository: SafeRideRepository,
  modifier: Modifier = Modifier
) {
  val language by repository.currentLanguage.collectAsState()
  val busRoute by repository.busRoute.collectAsState()
  val isOffline by repository.isOfflineMode.collectAsState()
  val queuedCount by repository.offlineQueuedCount.collectAsState()
  val students by repository.students.collectAsState()

  var selectedStudentToScan by remember { mutableStateOf(students.first().name) }
  var showSosDialog by remember { mutableStateOf(false) }
  var scanSuccessMessage by remember { mutableStateOf<String?>(null) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Driver Console Header
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = SafeRideNavy)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(
                text = "${if (language == AppLanguage.SWAHILI) "Dereva: " else "Driver: "}${busRoute.driverName}",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
              Text(
                text = "${if (language == AppLanguage.SWAHILI) "Basi: " else "Vehicle: "}${busRoute.busPlateNumber} | ${busRoute.driverPhone}",
                color = SafeRideAmber,
                fontSize = 12.sp
              )
            }

            Surface(
              color = if (isOffline) SafeRideRed else SafeRideGreen,
              shape = RoundedCornerShape(8.dp)
            ) {
              Text(
                text = if (isOffline) "OFFLINE (${queuedCount})" else "LIVE GPS",
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = { repository.advanceToNextStop() },
              modifier = Modifier
                .weight(1f)
                .testTag("driver_advance_stop_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SafeRideBlueLight)
            ) {
              Icon(Icons.Default.DirectionsTransit, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = if (language == AppLanguage.SWAHILI) "Kituo Kinachofuata" else "Next Stop",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Button(
              onClick = { showSosDialog = true },
              modifier = Modifier.testTag("driver_sos_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SafeRideRed)
            ) {
              Icon(Icons.Default.Warning, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text("SOS", fontWeight = FontWeight.Bold)
            }
          }
        }
      }
    }

    // 2. Conductor Boarding/Drop-Off QR Verification Scanner (Core Feature 5)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(SafeRideBlue.copy(alpha = 0.1f)),
              contentAlignment = Alignment.Center
            ) {
              Icon(
                imageVector = Icons.Default.QrCodeScanner,
                contentDescription = null,
                tint = SafeRideBlue,
                modifier = Modifier.size(20.dp)
              )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = AppStrings.boardingVerification(language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
              Text(
                text = if (language == AppLanguage.SWAHILI) "Scan QR / Kadi ya Mwanafunzi" else "Scan QR / Student ID Card",
                fontSize = 11.sp,
                color = SafeRideGray
              )
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          Text(
            text = if (language == AppLanguage.SWAHILI) "Chagua Mwanafunzi Aliyepo Kituoni:" else "Select Student at Stop:",
            fontSize = 12.sp,
            color = SafeRideGray
          )
          Spacer(modifier = Modifier.height(4.dp))

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            students.forEach { st ->
              val isSelected = st.name == selectedStudentToScan
              FilterChip(
                selected = isSelected,
                onClick = { selectedStudentToScan = st.name },
                label = { Text(st.name, fontSize = 12.sp) },
                modifier = Modifier.testTag("driver_student_chip_${st.id}")
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
          ) {
            Button(
              onClick = {
                repository.verifyStudentBoarding(selectedStudentToScan, "BOARDING")
                scanSuccessMessage = if (language == AppLanguage.SWAHILI)
                  "Uthibitisho Umekamilika! Mzazi amearifiwa kupitia App na SMS."
                else
                  "Boarding Verified! Parent alerted via App and SMS."
              },
              modifier = Modifier
                .weight(1f)
                .testTag("scan_boarded_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SafeRideGreenDark)
            ) {
              Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (language == AppLanguage.SWAHILI) "Amepanda Basi" else "Confirm Boarded",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }

            Button(
              onClick = {
                repository.verifyStudentBoarding(selectedStudentToScan, "DROPOFF")
                scanSuccessMessage = if (language == AppLanguage.SWAHILI)
                  "Amefika Salama! Arifa ya mwisho imetumwa kwa mzazi."
                else
                  "Arrived Safely! Final notification sent to parent."
              },
              modifier = Modifier
                .weight(1f)
                .testTag("scan_dropoff_button"),
              shape = RoundedCornerShape(10.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SafeRideNavy)
            ) {
              Icon(Icons.Default.CheckCircle, contentDescription = null, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(4.dp))
              Text(
                text = if (language == AppLanguage.SWAHILI) "Amefika Salama" else "Confirm Arrived",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          if (scanSuccessMessage != null) {
            Spacer(modifier = Modifier.height(10.dp))
            Surface(
              color = SafeRideGreenLight,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.Check, contentDescription = null, tint = SafeRideGreenDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                  text = scanSuccessMessage!!,
                  fontSize = 11.sp,
                  color = SafeRideGreenDark,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }
    }

    // 3. Live Map View for Route Navigation
    item {
      Text(
        text = if (language == AppLanguage.SWAHILI) "Mwongozo wa Ruti na Vituo" else "Route Map & Stops",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall
      )
      Spacer(modifier = Modifier.height(6.dp))
      LiveRouteMapView(
        busRoute = busRoute,
        language = language
      )
    }

    // 4. Low-Connectivity / Offline Mode Explanation (Tanzania Context)
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SafeRideGrayLight)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CloudSync, contentDescription = null, tint = SafeRideBlue, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = AppStrings.offlineMode(language),
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (language == AppLanguage.SWAHILI)
              "Kama gari lipo eneo lisilo na mtandao thabiti, conductor anascan bila tatizo. Data zote zitasawazishwa (sync) kiotomatiki mtandao ukirudi."
            else
              "In areas with weak cell coverage in Tanzania, scans are queued securely and auto-synced immediately when connectivity returns.",
            fontSize = 11.sp,
            color = SafeRideGray
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // SOS Emergency Modal
  if (showSosDialog) {
    AlertDialog(
      onDismissRequest = { showSosDialog = false },
      title = {
        Text(
          text = if (language == AppLanguage.SWAHILI) "Ripoti Dharura ya Safari (SOS)" else "Report Trip Emergency (SOS)",
          fontWeight = FontWeight.Bold,
          color = SafeRideRed
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          Text(
            text = if (language == AppLanguage.SWAHILI)
              "Chagua sababu ya dharura. Arifa ya moja kwa moja na SMS zitatumwa kwa Shule na Wazazi wote:"
            else
              "Select reason for emergency. Push and SMS will be dispatched immediately to school and parents:",
            fontSize = 12.sp
          )

          Button(
            onClick = {
              repository.triggerSOS("Basi Limeharibika (Mechanical Breakdown)")
              showSosDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = SafeRideAmberDark),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Basi Limeharibika (Breakdown)")
          }

          Button(
            onClick = {
              repository.triggerSOS("Foleni Kubwa / Barabara Imefungwa")
              showSosDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = SafeRideAmberDark),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Foleni / Njia Imefungwa (Traffic/Road Blocked)")
          }

          Button(
            onClick = {
              repository.triggerSOS("Ajali ya Barabarani / SOS Msaada wa Haraka")
              showSosDialog = false
            },
            colors = ButtonDefaults.buttonColors(containerColor = SafeRideRed),
            modifier = Modifier.fillMaxWidth()
          ) {
            Text("Ajali / SOS Msaada wa Haraka (Accident)")
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showSosDialog = false }) {
          Text(if (language == AppLanguage.SWAHILI) "Ghairi" else "Cancel")
        }
      }
    )
  }
}
