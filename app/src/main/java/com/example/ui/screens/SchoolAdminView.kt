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
import com.example.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun SchoolAdminView(
  repository: SafeRideRepository,
  modifier: Modifier = Modifier
) {
  val language by repository.currentLanguage.collectAsState()
  val customization by repository.appCustomization.collectAsState()
  val busRoute by repository.busRoute.collectAsState()
  val pendingRegistrations by repository.pendingRegistrations.collectAsState()
  val announcements by repository.announcements.collectAsState()

  var announcementTitle by remember { mutableStateOf("") }
  var announcementContent by remember { mutableStateOf("") }
  var showNewAnnouncementDialog by remember { mutableStateOf(false) }
  var showCustomizerDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. School Admin Header & Fleet Summary
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
                text = if (language == AppLanguage.SWAHILI) "Dashibodi ya Shule (School Admin)" else "School Admin Dashboard",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
              )
              Text(
                text = customization.schoolName,
                color = SafeRideAmber,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold
              )
            }

            IconButton(
              onClick = { showCustomizerDialog = true },
              modifier = Modifier.testTag("school_admin_quick_customize_button")
            ) {
              Icon(
                imageVector = Icons.Default.Tune,
                contentDescription = "Badili Mfumo",
                tint = Color.White
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { showCustomizerDialog = true },
            shape = RoundedCornerShape(10.dp),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
            modifier = Modifier
              .fillMaxWidth()
              .testTag("school_admin_open_customizer_button")
          ) {
            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.SWAHILI) "Badili Muonekano & Taarifa za Shule (Edit App)" else "Customize App, Branding & Theme",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // 3-Stat Row
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "12", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text(
                text = if (language == AppLanguage.SWAHILI) "Mabasi Yapo Nje" else "Active Buses",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "348", color = SafeRideGreen, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text(
                text = if (language == AppLanguage.SWAHILI) "Wanafunzi Ndani" else "Boarded",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
              )
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              Text(text = "98.4%", color = SafeRideAmber, fontWeight = FontWeight.Bold, fontSize = 18.sp)
              Text(
                text = if (language == AppLanguage.SWAHILI) "Muda Sahihi (On-Time)" else "On-Time Rate",
                color = Color(0xFF94A3B8),
                fontSize = 10.sp
              )
            }
          }
        }
      }
    }

    // 2. Pending Parent Registration Approvals (Core Feature 1 & 6)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.SWAHILI) "Maombi ya Wazazi ya Kuidhinishwa" else "Pending Parent Approvals",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleSmall
        )
        Surface(
          color = SafeRideAmberLight,
          shape = RoundedCornerShape(12.dp)
        ) {
          Text(
            text = "${pendingRegistrations.count { it.status == "PENDING" }} Pending",
            fontSize = 11.sp,
            color = SafeRideAmberDark,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp)
          )
        }
      }
    }

    items(pendingRegistrations) { req ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("parent_approval_card_${req.id}"),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column {
              Text(text = req.parentName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
              Text(text = "${req.parentEmail} • ${req.parentPhone}", fontSize = 11.sp, color = SafeRideGray)
            }
            Surface(
              color = if (req.status == "APPROVED") SafeRideGreenLight else SafeRideAmberLight,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = req.status,
                color = if (req.status == "APPROVED") SafeRideGreenDark else SafeRideAmberDark,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          Spacer(modifier = Modifier.height(8.dp))

          Surface(
            color = SafeRideGrayLight,
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Row(
              modifier = Modifier.padding(8.dp),
              verticalAlignment = Alignment.CenterVertically
            ) {
              Icon(Icons.Default.School, contentDescription = null, tint = SafeRideBlue, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "${if (language == AppLanguage.SWAHILI) "Mtoto: " else "Student: "}${req.studentName} (${req.schoolName})",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          if (req.status == "PENDING") {
            Spacer(modifier = Modifier.height(10.dp))
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
              Button(
                onClick = { repository.approveRegistration(req.id) },
                colors = ButtonDefaults.buttonColors(containerColor = SafeRideGreenDark),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                  .weight(1f)
                  .testTag("approve_button_${req.id}")
              ) {
                Icon(Icons.Default.Check, contentDescription = null, modifier = Modifier.size(14.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (language == AppLanguage.SWAHILI) "Idhinisha" else "Approve", fontSize = 11.sp)
              }

              OutlinedButton(
                onClick = { repository.rejectRegistration(req.id) },
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f)
              ) {
                Icon(Icons.Default.Close, contentDescription = null, modifier = Modifier.size(14.dp), tint = SafeRideRed)
                Spacer(modifier = Modifier.width(4.dp))
                Text(if (language == AppLanguage.SWAHILI) "Kataa" else "Reject", fontSize = 11.sp, color = SafeRideRed)
              }
            }
          }
        }
      }
    }

    // 3. Live Fleet Bus Quick Card
    item {
      Text(
        text = if (language == AppLanguage.SWAHILI) "Magari Yanayofuatiliwa Moja kwa Moja" else "Live Fleet Tracking",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall
      )
      Spacer(modifier = Modifier.height(6.dp))

      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(14.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.DirectionsBus, contentDescription = null, tint = SafeRideAmberDark)
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(text = "Basi ${busRoute.busPlateNumber}", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = "${busRoute.driverName} • ${busRoute.currentSpeedKmh} km/h", fontSize = 11.sp, color = SafeRideGray)
              }
            }
            Surface(
              color = if (busRoute.activeEmergency != null) SafeRideRedLight else SafeRideGreenLight,
              shape = RoundedCornerShape(6.dp)
            ) {
              Text(
                text = if (busRoute.activeEmergency != null) "EMERGENCY" else "NORMAL ROUTE",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = if (busRoute.activeEmergency != null) SafeRideRed else SafeRideGreenDark,
                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
              )
            }
          }

          if (busRoute.activeEmergency != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Button(
              onClick = { repository.clearEmergency() },
              colors = ButtonDefaults.buttonColors(containerColor = SafeRideGreenDark),
              modifier = Modifier.fillMaxWidth()
            ) {
              Text(if (language == AppLanguage.SWAHILI) "Tatua na Futa Dharura" else "Resolve & Clear Emergency")
            }
          }
        }
      }
    }

    // 4. Announcements Creator
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.SWAHILI) "Matangazo kwa Wazazi" else "Announcements to Parents",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleSmall
        )

        Button(
          onClick = { showNewAnnouncementDialog = true },
          shape = RoundedCornerShape(8.dp),
          contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
          modifier = Modifier.testTag("create_announcement_button")
        ) {
          Icon(Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(14.dp))
          Spacer(modifier = Modifier.width(4.dp))
          Text(if (language == AppLanguage.SWAHILI) "Tangazo Jipya" else "New Broadcast", fontSize = 11.sp)
        }
      }
    }

    items(announcements) { anc ->
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = anc.title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Text(text = anc.timestamp, fontSize = 10.sp, color = SafeRideGray)
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(text = anc.content, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  // New Announcement Dialog
  if (showNewAnnouncementDialog) {
    AlertDialog(
      onDismissRequest = { showNewAnnouncementDialog = false },
      title = {
        Text(if (language == AppLanguage.SWAHILI) "Tuma Tangazo Jipya (Push + SMS)" else "Send Broadcast Announcement")
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
          OutlinedTextField(
            value = announcementTitle,
            onValueChange = { announcementTitle = it },
            label = { Text(if (language == AppLanguage.SWAHILI) "Kichwa cha Habari" else "Title") },
            modifier = Modifier.fillMaxWidth()
          )
          OutlinedTextField(
            value = announcementContent,
            onValueChange = { announcementContent = it },
            label = { Text(if (language == AppLanguage.SWAHILI) "Ujumbe" else "Content") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3
          )
        }
      },
      confirmButton = {
        Button(
          onClick = {
            if (announcementTitle.isNotBlank() && announcementContent.isNotBlank()) {
              repository.addAnnouncement(announcementTitle.trim(), announcementContent.trim())
              announcementTitle = ""
              announcementContent = ""
              showNewAnnouncementDialog = false
            }
          }
        ) {
          Text(if (language == AppLanguage.SWAHILI) "Tuma kwa Wazazi" else "Broadcast")
        }
      },
      dismissButton = {
        TextButton(onClick = { showNewAnnouncementDialog = false }) {
          Text(if (language == AppLanguage.SWAHILI) "Ghairi" else "Cancel")
        }
      }
    )
  }

  if (showCustomizerDialog) {
    AdminAppCustomizerDialog(
      repository = repository,
      onDismiss = { showCustomizerDialog = false }
    )
  }
}
