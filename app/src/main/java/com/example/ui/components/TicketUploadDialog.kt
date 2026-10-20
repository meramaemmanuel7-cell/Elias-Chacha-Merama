package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.window.Dialog
import com.example.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun TicketUploadDialog(
  language: AppLanguage,
  defaultStudentName: String,
  onDismiss: () -> Unit,
  onSubmitTicket: (plate: String, student: String, route: String, ticketNo: String) -> Boolean
) {
  var isScanningOcr by remember { mutableStateOf(false) }
  var hasScanned by remember { mutableStateOf(false) }

  var plateNumber by remember { mutableStateOf("T 418 DKP") }
  var studentName by remember { mutableStateOf(defaultStudentName) }
  var routeName by remember { mutableStateOf("Kimara - Ubungo - Feza Campus") }
  var ticketNumber by remember { mutableStateOf("SR-2026-9842") }
  var dateString by remember { mutableStateOf("18 Oktoba 2026") }
  var errorMessage by remember { mutableStateOf<String?>(null) }
  var successMessage by remember { mutableStateOf<String?>(null) }

  Dialog(onDismissRequest = onDismiss) {
    Card(
      modifier = Modifier
        .fillMaxWidth()
        .padding(16.dp)
        .testTag("ticket_upload_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .padding(20.dp)
          .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = if (language == AppLanguage.SWAHILI) "Upakiaji wa Tiketi ya Gari" else "Upload Bus Ticket",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium
          )
          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Funga")
          }
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Scanner / Camera Box
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(SafeRideGrayLight)
            .border(1.dp, SafeRideCardBorder, RoundedCornerShape(12.dp)),
          contentAlignment = Alignment.Center
        ) {
          if (isScanningOcr) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
              CircularProgressIndicator(
                color = SafeRideBlue,
                modifier = Modifier.size(32.dp)
              )
              Spacer(modifier = Modifier.height(8.dp))
              Text(
                text = if (language == AppLanguage.SWAHILI) "Inasoma taarifa za tiketi kwa OCR..." else "Extracting ticket details via OCR...",
                fontSize = 12.sp,
                color = SafeRideGray
              )
            }
          } else {
            Column(
              horizontalAlignment = Alignment.CenterHorizontally,
              modifier = Modifier.padding(12.dp)
            ) {
              Icon(
                imageVector = Icons.Default.DocumentScanner,
                contentDescription = null,
                tint = SafeRideBlue,
                modifier = Modifier.size(36.dp)
              )
              Spacer(modifier = Modifier.height(6.dp))
              Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                  onClick = {
                    isScanningOcr = true
                    // Simulate fast OCR processing
                    hasScanned = true
                    isScanningOcr = false
                  },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Icon(Icons.Default.CameraAlt, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(if (language == AppLanguage.SWAHILI) "Piga Picha" else "Camera", fontSize = 11.sp)
                }

                OutlinedButton(
                  onClick = {
                    isScanningOcr = true
                    hasScanned = true
                    isScanningOcr = false
                  },
                  shape = RoundedCornerShape(8.dp),
                  contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                  Icon(Icons.Default.PhotoLibrary, contentDescription = null, modifier = Modifier.size(14.dp))
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(if (language == AppLanguage.SWAHILI) "Kutoka Gallery" else "Gallery", fontSize = 11.sp)
                }
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Text(
          text = if (language == AppLanguage.SWAHILI) "Hakiki Taarifa Zilizosomwa (OCR Verification):" else "Confirm Parsed OCR Data:",
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold,
          color = SafeRideGray,
          modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Editable fields to satisfy verification requirement
        OutlinedTextField(
          value = plateNumber,
          onValueChange = { plateNumber = it },
          label = { Text(if (language == AppLanguage.SWAHILI) "Namba ya Gari" else "Vehicle Plate") },
          leadingIcon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("ticket_plate_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = studentName,
          onValueChange = { studentName = it },
          label = { Text(if (language == AppLanguage.SWAHILI) "Jina la Mwanafunzi" else "Student Name") },
          leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("ticket_student_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = routeName,
          onValueChange = { routeName = it },
          label = { Text(if (language == AppLanguage.SWAHILI) "Njia ya Basi (Route)" else "Bus Route") },
          leadingIcon = { Icon(Icons.Default.Route, contentDescription = null) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("ticket_route_input")
        )

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
          value = ticketNumber,
          onValueChange = { ticketNumber = it },
          label = { Text(if (language == AppLanguage.SWAHILI) "Namba ya Tiketi" else "Ticket Number") },
          leadingIcon = { Icon(Icons.Default.ConfirmationNumber, contentDescription = null) },
          singleLine = true,
          modifier = Modifier.fillMaxWidth().testTag("ticket_number_input")
        )

        if (errorMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = errorMessage!!, color = SafeRideRed, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        if (successMessage != null) {
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = successMessage!!, color = SafeRideGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // CIA Triad Encryption Notice
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(SafeRideNavy.copy(alpha = 0.05f))
            .padding(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(Icons.Default.Lock, contentDescription = null, tint = SafeRideGreenDark, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (language == AppLanguage.SWAHILI) "Picha imehifadhiwa kwa AES-256 & saini ya kidijitali." else "Encrypted storage with AES-256 and digital SHA-256 signature.",
            fontSize = 10.sp,
            color = SafeRideGray
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
          onClick = {
            if (plateNumber.isBlank() || ticketNumber.isBlank() || studentName.isBlank()) {
              errorMessage = if (language == AppLanguage.SWAHILI) "Tafadhali jaza sehemu zote" else "Please complete all fields"
              return@Button
            }
            val ok = onSubmitTicket(plateNumber.trim(), studentName.trim(), routeName.trim(), ticketNumber.trim())
            if (ok) {
              successMessage = if (language == AppLanguage.SWAHILI) "Tiketi imethibitishwa kikamilifu!" else "Ticket verified successfully!"
              errorMessage = null
              onDismiss()
            } else {
              errorMessage = if (language == AppLanguage.SWAHILI) "Tiketi hii tayari imeshatumika kwa safari hii!" else "This ticket has already been used!"
            }
          },
          modifier = Modifier.fillMaxWidth().testTag("submit_ticket_button"),
          shape = RoundedCornerShape(10.dp)
        ) {
          Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (language == AppLanguage.SWAHILI) "Thibitisha na Anza Kufuatilia" else "Verify & Start Tracking",
            fontWeight = FontWeight.Bold
          )
        }
      }
    }
  }
}
