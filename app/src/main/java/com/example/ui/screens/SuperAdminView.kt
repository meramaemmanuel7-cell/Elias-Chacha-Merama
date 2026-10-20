package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SafeRideRepository
import com.example.model.AppLanguage
import com.example.ui.theme.*

@Composable
fun SuperAdminView(
  repository: SafeRideRepository,
  modifier: Modifier = Modifier
) {
  val language by repository.currentLanguage.collectAsState()
  val auditLogs by repository.auditLogs.collectAsState()
  var showCustomizerDialog by remember { mutableStateOf(false) }

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .padding(horizontal = 16.dp),
    verticalArrangement = Arrangement.spacedBy(14.dp)
  ) {
    // 1. Super Admin Title Card
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
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = SafeRideAmber, modifier = Modifier.size(24.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "Super Admin Security & Audit Center",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
              )
            }

            IconButton(
              onClick = { showCustomizerDialog = true },
              modifier = Modifier.testTag("super_admin_customizer_button")
            ) {
              Icon(Icons.Default.Tune, contentDescription = "Edit App", tint = Color.White)
            }
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = if (language == AppLanguage.SWAHILI)
              "Usimamizi wa Usalama (CIA Triad), Ukaguzi wa Mifumo, na PDPA 2022 Compliance"
            else
              "Security Governance (CIA Triad), Tamper-Evident Audit Trails & PDPA 2022 Compliance",
            color = Color(0xFFCBD5E1),
            fontSize = 11.sp
          )

          Spacer(modifier = Modifier.height(10.dp))

          Button(
            onClick = { showCustomizerDialog = true },
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.fillMaxWidth()
          ) {
            Icon(Icons.Default.Palette, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.SWAHILI) "Badili Mandhari na Muundo wa App (Edit App)" else "Customize App, Branding & Theme",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // 2. CIA Triad Status Cards
    item {
      Text(
        text = "CIA Triad Security Controls Status",
        fontWeight = FontWeight.Bold,
        style = MaterialTheme.typography.titleSmall
      )
      Spacer(modifier = Modifier.height(6.dp))

      Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // Confidentiality
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Security, contentDescription = null, tint = SafeRideBlue, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(text = "Confidentiality (Usiri): ACTIVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text(
                text = "RBAC (Multi-role isolation), AES-256 At Rest, TLS 1.3 In-Transit, Encrypted Ticket Images.",
                fontSize = 11.sp,
                color = SafeRideGray
              )
            }
          }
        }

        // Integrity
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.Verified, contentDescription = null, tint = SafeRideGreenDark, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(text = "Integrity (Uadilifu): ACTIVE", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text(
                text = "SHA-256 Tamper-Evident Ticket Hashes, Anti-Replay Validation, GPS Spoofing Detection.",
                fontSize = 11.sp,
                color = SafeRideGray
              )
            }
          }
        }

        // Availability
        Card(
          shape = RoundedCornerShape(10.dp),
          colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(Icons.Default.NetworkCheck, contentDescription = null, tint = SafeRideAmberDark, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(text = "Availability (Upatikanaji): 99.9% UPTIME", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              Text(
                text = "Offline Queue & Sync (low-bandwidth TZ), SMS Fallback via Africa's Talking, Auto-scaling.",
                fontSize = 11.sp,
                color = SafeRideGray
              )
            }
          }
        }
      }
    }

    // 3. PDPA 2022 Tanzania Compliance Policy
    item {
      Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SafeRideGrayLight)
      ) {
        Column(modifier = Modifier.padding(12.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.Gavel, contentDescription = null, tint = SafeRideNavy, modifier = Modifier.size(18.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "Tanzania Personal Data Protection Act, 2022",
              fontWeight = FontWeight.Bold,
              fontSize = 12.sp
            )
          }
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = "• Data Retention: GPS tracking telemetries preserved for 12 months, followed by automated pseudonymization.\n" +
                   "• Parent Consent: Explicit opt-in recorded at onboarding with right to access & rectify.",
            fontSize = 11.sp,
            color = SafeRideGray,
            lineHeight = 16.sp
          )
        }
      }
    }

    // 4. Tamper-Evident Audit Logs (Audit Trail)
    item {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (language == AppLanguage.SWAHILI) "Kumbukumbu za Ukaguzi (Audit Logs)" else "Tamper-Evident Audit Trail",
          fontWeight = FontWeight.Bold,
          style = MaterialTheme.typography.titleSmall
        )
        Surface(
          color = SafeRideGreenLight,
          shape = RoundedCornerShape(6.dp)
        ) {
          Text(
            text = "IMMUTABLE",
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            color = SafeRideGreenDark,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
          )
        }
      }
    }

    items(auditLogs) { log ->
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .testTag("audit_log_${log.id}"),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
      ) {
        Column(modifier = Modifier.padding(10.dp)) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = log.action, fontWeight = FontWeight.Bold, fontSize = 12.sp)
            Text(text = log.timestamp, fontSize = 10.sp, color = SafeRideGray)
          }
          Spacer(modifier = Modifier.height(2.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "Actor: ${log.actor} [${log.role}]", fontSize = 11.sp, color = SafeRideGray)
            Text(text = "IP: ${log.ipAddress}", fontSize = 10.sp, color = SafeRideGray)
          }
          Spacer(modifier = Modifier.height(2.dp))
          Text(
            text = "Hash: ${log.integrityHash}",
            fontFamily = FontFamily.Monospace,
            fontSize = 9.sp,
            color = SafeRideBlue
          )
        }
      }
    }

    item {
      Spacer(modifier = Modifier.height(24.dp))
    }
  }

  if (showCustomizerDialog) {
    AdminAppCustomizerDialog(
      repository = repository,
      onDismiss = { showCustomizerDialog = false }
    )
  }
}
