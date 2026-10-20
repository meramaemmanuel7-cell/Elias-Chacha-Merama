package com.example.ui.components

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.theme.*

@Composable
fun TopControlBar(
  currentRole: UserRole,
  currentLanguage: AppLanguage,
  isOfflineMode: Boolean,
  queuedOfflineCount: Int,
  appTitle: String = "SafeRide",
  appSubtitle: String = "Usalama wa Mwanafunzi",
  profileAvatarUri: String = "avatar_shield",
  onRoleSelected: (UserRole) -> Unit,
  onLanguageSelected: (AppLanguage) -> Unit,
  onToggleOffline: () -> Unit,
  onAuthClick: () -> Unit,
  onAdminEditClick: () -> Unit,
  modifier: Modifier = Modifier
) {
  val avatarIcon = when (profileAvatarUri) {
    "avatar_bus" -> Icons.Default.DirectionsBus
    "avatar_crest" -> Icons.Default.School
    "avatar_principal" -> Icons.Default.AccountCircle
    "avatar_star" -> Icons.Default.Stars
    "avatar_radar" -> Icons.Default.Sensors
    else -> Icons.Default.Security
  }

  Column(
    modifier = modifier
      .fillMaxWidth()
      .padding(horizontal = 16.dp, vertical = 6.dp)
  ) {
    // Top Row: App Title + Avatar + Language Switcher + Admin Edit + Offline Toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1f)
      ) {
        Surface(
          color = MaterialTheme.colorScheme.secondary,
          shape = RoundedCornerShape(8.dp),
          modifier = Modifier.size(32.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = avatarIcon,
              contentDescription = "Nembo ya Shule",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = appTitle,
            fontWeight = FontWeight.Black,
            fontSize = 15.sp,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1
          )
          Text(
            text = appSubtitle,
            fontSize = 10.sp,
            color = SafeRideGray,
            maxLines = 1
          )
        }
      }

      Row(verticalAlignment = Alignment.CenterVertically) {
        // Admin Edit App Button (Pencil / Tune icon)
        IconButton(
          onClick = onAdminEditClick,
          modifier = Modifier
            .size(36.dp)
            .testTag("admin_edit_app_button")
        ) {
          Icon(
            imageVector = Icons.Default.Tune,
            contentDescription = "Badili Mfumo / Edit App",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
          )
        }

        // Offline Mode Badge Button
        FilterChip(
          selected = isOfflineMode,
          onClick = onToggleOffline,
          label = {
            Text(
              text = if (isOfflineMode) "Off ($queuedOfflineCount)" else "Online",
              fontSize = 10.sp
            )
          },
          leadingIcon = {
            Icon(
              imageVector = if (isOfflineMode) Icons.Default.WifiOff else Icons.Default.Wifi,
              contentDescription = null,
              tint = if (isOfflineMode) SafeRideRed else SafeRideGreen,
              modifier = Modifier.size(12.dp)
            )
          },
          colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = SafeRideRedLight,
            selectedLabelColor = SafeRideRed
          ),
          modifier = Modifier
            .padding(end = 4.dp)
            .testTag("offline_mode_chip")
        )

        // Language Switcher using LocaleState
        val localeState = rememberLocaleState(
          currentLanguage = currentLanguage,
          onLanguageChanged = onLanguageSelected
        )
        LanguageSwitcher(
          localeState = localeState,
          modifier = Modifier.testTag("language_toggle_button")
        )

        // Account Profile / Login Screen Access Button
        IconButton(
          onClick = onAuthClick,
          modifier = Modifier
            .padding(start = 2.dp)
            .size(34.dp)
            .testTag("auth_screen_toggle_button")
        ) {
          Icon(
            imageVector = Icons.Default.AccountCircle,
            contentDescription = if (currentLanguage == AppLanguage.SWAHILI) "Akaunti na Kuingia" else "Account & Login",
            tint = SafeRideNavy,
            modifier = Modifier.size(22.dp)
          )
        }
      }
    }

    Spacer(modifier = Modifier.height(8.dp))

    // Role Switcher Chips Bar (Role-Based Access Control)
    val scrollState = rememberScrollState()
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .horizontalScroll(scrollState),
      horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
      val roles = listOf(
        UserRole.PARENT to if (currentLanguage == AppLanguage.SWAHILI) "Mzazi" else "Parent",
        UserRole.DRIVER to if (currentLanguage == AppLanguage.SWAHILI) "Dereva/Conductor" else "Driver/Conductor",
        UserRole.SCHOOL_ADMIN to if (currentLanguage == AppLanguage.SWAHILI) "Msimamizi Shule" else "School Admin",
        UserRole.SUPER_ADMIN to if (currentLanguage == AppLanguage.SWAHILI) "Super Admin" else "Super Admin"
      )

      roles.forEach { (role, label) ->
        val isSelected = currentRole == role
        FilterChip(
          selected = isSelected,
          onClick = { onRoleSelected(role) },
          label = {
            Text(
              text = label,
              fontSize = 12.sp,
              fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
            )
          },
          leadingIcon = {
            val icon = when (role) {
              UserRole.PARENT -> Icons.Default.FamilyRestroom
              UserRole.DRIVER -> Icons.Default.DirectionsBus
              UserRole.SCHOOL_ADMIN -> Icons.Default.School
              UserRole.SUPER_ADMIN -> Icons.Default.AdminPanelSettings
            }
            Icon(
              imageVector = icon,
              contentDescription = null,
              modifier = Modifier.size(16.dp),
              tint = if (isSelected) MaterialTheme.colorScheme.primary else SafeRideGray
            )
          },
          modifier = Modifier.testTag("role_chip_${role.name.lowercase()}")
        )
      }
    }
  }
}
