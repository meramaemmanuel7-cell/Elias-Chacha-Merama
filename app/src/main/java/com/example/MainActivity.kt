package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneInTalk
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.data.SafeRideRepository
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.components.TopControlBar
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SafeRideGreenDark

class MainActivity : ComponentActivity() {

  private lateinit var repository: SafeRideRepository

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    val database = com.example.data.local.SafeRideDatabase.getInstance(applicationContext)
    repository = SafeRideRepository(database = database)
    setContent {
      val customization by repository.appCustomization.collectAsState()
      MyApplicationTheme(
        themePreset = customization.themePreset,
        darkTheme = customization.isDarkMode
      ) {
        SafeRideApp(repository = repository)
      }
    }
  }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SafeRideApp(repository: SafeRideRepository) {
  val currentRole by repository.currentRole.collectAsState()
  val currentLanguage by repository.currentLanguage.collectAsState()
  val isOfflineMode by repository.isOfflineMode.collectAsState()
  val queuedCount by repository.offlineQueuedCount.collectAsState()
  val isUserLoggedIn by repository.isUserLoggedIn.collectAsState()
  val customization by repository.appCustomization.collectAsState()
  val busRoute by repository.busRoute.collectAsState()

  var showAuthScreen by remember { mutableStateOf(false) }
  var showContactDialog by remember { mutableStateOf(false) }
  var showAppCustomizerDialog by remember { mutableStateOf(false) }

  // Handle back button
  BackHandler(enabled = showAuthScreen && isUserLoggedIn) {
    showAuthScreen = false
  }
  BackHandler(enabled = !showAuthScreen && currentRole != UserRole.PARENT) {
    repository.setRole(UserRole.PARENT)
  }

  // If user is not logged in or explicitly requested Auth screen
  if (!isUserLoggedIn || showAuthScreen) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .testTag("auth_scaffold"),
      contentWindowInsets = WindowInsets.systemBars
    ) { paddingValues ->
      Box(modifier = Modifier.padding(paddingValues)) {
        AuthScreen(
          repository = repository,
          onLoginSuccess = { showAuthScreen = false }
        )
      }
    }
    return
  }

  Scaffold(
    modifier = Modifier
      .fillMaxSize()
      .testTag("saferide_main_scaffold"),
    contentWindowInsets = WindowInsets.systemBars,
    topBar = {
      TopControlBar(
        currentRole = currentRole,
        currentLanguage = currentLanguage,
        isOfflineMode = isOfflineMode,
        queuedOfflineCount = queuedCount,
        appTitle = customization.appTitle,
        appSubtitle = customization.appSubtitle,
        profileAvatarUri = customization.profileAvatarUri,
        onRoleSelected = { repository.setRole(it) },
        onLanguageSelected = { repository.setLanguage(it) },
        onToggleOffline = { repository.toggleOfflineMode() },
        onAuthClick = { showAuthScreen = true },
        onAdminEditClick = { showAppCustomizerDialog = true }
      )
    },
    floatingActionButton = {
      if (currentRole == UserRole.PARENT) {
        FloatingActionButton(
          onClick = { showContactDialog = true },
          containerColor = MaterialTheme.colorScheme.primary,
          contentColor = androidx.compose.ui.graphics.Color.White,
          modifier = Modifier.testTag("contact_school_fab")
        ) {
          Icon(
            imageVector = Icons.Default.PhoneInTalk,
            contentDescription = if (currentLanguage == AppLanguage.SWAHILI) "Piga Simu Shuleni" else "Call School Dispatch"
          )
        }
      }
    }
  ) { paddingValues ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues)
    ) {
      when (currentRole) {
        UserRole.PARENT -> ParentView(repository = repository)
        UserRole.DRIVER -> DriverView(repository = repository)
        UserRole.SCHOOL_ADMIN -> SchoolAdminView(repository = repository)
        UserRole.SUPER_ADMIN -> SuperAdminView(repository = repository)
      }
    }
  }

  // Admin App Customization Dialog
  if (showAppCustomizerDialog) {
    AdminAppCustomizerDialog(
      repository = repository,
      onDismiss = { showAppCustomizerDialog = false }
    )
  }

  // Emergency School Contacts Dialog with dynamic customized info
  if (showContactDialog) {
    AlertDialog(
      onDismissRequest = { showContactDialog = false },
      confirmButton = {
        Button(onClick = { showContactDialog = false }) {
          Text(if (currentLanguage == AppLanguage.SWAHILI) "Piga Simu" else "Call Now")
        }
      },
      dismissButton = {
        TextButton(onClick = { showContactDialog = false }) {
          Text(if (currentLanguage == AppLanguage.SWAHILI) "Funga" else "Close")
        }
      },
      title = {
        Text(if (currentLanguage == AppLanguage.SWAHILI) "Mawasiliano ya Dharura ya Shule" else "Emergency School Contacts")
      },
      text = {
        Column {
          Text(
            text = customization.schoolName,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          Text(text = "• Msimamizi wa Usafiri: ${customization.emergencyPhone}")
          Text(text = "• Barua Pepe ya Dispatch: ${customization.dispatchEmail}")
          Text(text = "• Dereva wa Basi (${busRoute.busPlateNumber}): ${busRoute.driverPhone}")
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = if (currentLanguage == AppLanguage.SWAHILI)
              "Kituo cha simu kiko wazi kuanzia saa 06:00 AM hadi saa 06:00 PM."
            else
              "Dispatch helpline is operational 06:00 AM - 06:00 PM.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
          )
        }
      }
    )
  }
}
