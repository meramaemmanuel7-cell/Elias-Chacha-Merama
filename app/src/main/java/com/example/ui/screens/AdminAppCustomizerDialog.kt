package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.ui.window.DialogProperties
import com.example.data.SafeRideRepository
import com.example.model.AppCustomization
import com.example.model.AppLanguage
import com.example.model.ThemePreset
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminAppCustomizerDialog(
  repository: SafeRideRepository,
  onDismiss: () -> Unit
) {
  val language by repository.currentLanguage.collectAsState()
  val customization by repository.appCustomization.collectAsState()
  val busRoute by repository.busRoute.collectAsState()
  val students by repository.students.collectAsState()

  var selectedTab by remember { mutableIntStateOf(0) } // 0: Branding & Theme, 1: School & Helpline, 2: Bus & Fleet, 3: Student Info

  // Form States initialized from repository
  var editAppTitle by remember { mutableStateOf(customization.appTitle) }
  var editAppSubtitle by remember { mutableStateOf(customization.appSubtitle) }
  var editAdminName by remember { mutableStateOf(customization.adminProfileName) }
  var editSelectedAvatar by remember { mutableStateOf(customization.profileAvatarUri) }
  var editThemePreset by remember { mutableStateOf(customization.themePreset) }
  var editIsDarkMode by remember { mutableStateOf(customization.isDarkMode) }

  var editSchoolName by remember { mutableStateOf(customization.schoolName) }
  var editEmergencyPhone by remember { mutableStateOf(customization.emergencyPhone) }
  var editDispatchEmail by remember { mutableStateOf(customization.dispatchEmail) }
  var editSpeedLimit by remember { mutableIntStateOf(customization.speedAlertLimitKmh) }

  var editBusPlate by remember { mutableStateOf(busRoute.busPlateNumber) }
  var editDriverName by remember { mutableStateOf(busRoute.driverName) }
  var editDriverPhone by remember { mutableStateOf(busRoute.driverPhone) }

  val firstStudent = students.first()
  var editStudentName by remember { mutableStateOf(firstStudent.name) }
  var editStudentGrade by remember { mutableStateOf(firstStudent.grade) }
  var editPickupPerson by remember { mutableStateOf(firstStudent.authorizedPickupName) }
  var editPickupPhone by remember { mutableStateOf(firstStudent.authorizedPickupPhone) }

  var saveSuccessMessage by remember { mutableStateOf<String?>(null) }

  val avatarOptions = listOf(
    "avatar_shield" to (Icons.Default.Security to "Ngao ya Ulinzi / Shield"),
    "avatar_bus" to (Icons.Default.DirectionsBus to "Basi la Shule / Bus"),
    "avatar_crest" to (Icons.Default.School to "Nembo ya Shule / Crest"),
    "avatar_principal" to (Icons.Default.AccountCircle to "Mkuu wa Usafiri / Admin"),
    "avatar_star" to (Icons.Default.Stars to "Nyota ya Ubora / Star"),
    "avatar_radar" to (Icons.Default.Sensors to "Rada ya GPS / Radar")
  )

  Dialog(
    onDismissRequest = onDismiss,
    properties = DialogProperties(usePlatformDefaultWidth = false)
  ) {
    Card(
      modifier = Modifier
        .fillMaxWidth(0.95f)
        .fillMaxHeight(0.92f)
        .testTag("admin_customizer_dialog"),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
      Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
      ) {
        // Dialog Header
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Surface(
              color = MaterialTheme.colorScheme.primary,
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = Icons.Default.Tune,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = if (language == AppLanguage.SWAHILI) "Uhariri wa Programu (Admin Edit)" else "App Editor & Customizer",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
              )
              Text(
                text = if (language == AppLanguage.SWAHILI) "Badili jina, picha ya wasifu, mandhari na taarifa" else "Edit name, avatar, theme and fleet data",
                fontSize = 11.sp,
                color = SafeRideGray
              )
            }
          }

          IconButton(onClick = onDismiss) {
            Icon(Icons.Default.Close, contentDescription = "Funga")
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Tabs Row
        ScrollableTabRow(
          selectedTabIndex = selectedTab,
          edgePadding = 0.dp,
          containerColor = Color.Transparent,
          divider = {}
        ) {
          val tabTitles = listOf(
            if (language == AppLanguage.SWAHILI) "Mandhari na Picha" else "Theme & Avatar",
            if (language == AppLanguage.SWAHILI) "Taarifa za Shule" else "School Info",
            if (language == AppLanguage.SWAHILI) "Mabasi na Madereva" else "Fleet & Drivers",
            if (language == AppLanguage.SWAHILI) "Wanafunzi" else "Students"
          )
          tabTitles.forEachIndexed { index, title ->
            Tab(
              selected = selectedTab == index,
              onClick = { selectedTab = index },
              text = { Text(title, fontSize = 12.sp, fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal) },
              modifier = Modifier.testTag("customizer_tab_$index")
            )
          }
        }

        Divider(color = SafeRideCardBorder, modifier = Modifier.padding(vertical = 8.dp))

        // Content Area Scrollable
        Column(
          modifier = Modifier
            .weight(1f)
            .verticalScroll(rememberScrollState()),
          verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
          // TAB 0: BRANDING, THEME & AVATAR
          if (selectedTab == 0) {
            Text(
              text = if (language == AppLanguage.SWAHILI) "Picha ya Wasifu wa Msimamizi / Nembo:" else "Admin Profile Picture / School Logo:",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            // Avatar Options Row
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween
            ) {
              avatarOptions.forEach { (avatarKey, iconPair) ->
                val (icon, desc) = iconPair
                val isSelected = editSelectedAvatar == avatarKey
                Column(
                  horizontalAlignment = Alignment.CenterHorizontally,
                  modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .clickable { editSelectedAvatar = avatarKey }
                    .background(if (isSelected) MaterialTheme.colorScheme.primaryContainer else SafeRideGrayLight)
                    .border(
                      width = if (isSelected) 2.dp else 0.dp,
                      color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                      shape = RoundedCornerShape(12.dp)
                    )
                    .padding(8.dp)
                    .testTag("avatar_option_$avatarKey")
                ) {
                  Icon(
                    imageVector = icon,
                    contentDescription = desc,
                    tint = if (isSelected) MaterialTheme.colorScheme.primary else SafeRideGray,
                    modifier = Modifier.size(28.dp)
                  )
                }
              }
            }

            OutlinedTextField(
              value = editAdminName,
              onValueChange = { editAdminName = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Jina la Msimamizi / Cheo" else "Admin Name / Title") },
              leadingIcon = { Icon(Icons.Default.Badge, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("edit_admin_name_field"),
              shape = RoundedCornerShape(12.dp)
            )

            Divider(color = SafeRideCardBorder)

            Text(
              text = if (language == AppLanguage.SWAHILI) "Jina la Programu na Kaulimbiu:" else "App Name & Subtitle:",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            OutlinedTextField(
              value = editAppTitle,
              onValueChange = { editAppTitle = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Jina la Programu" else "App Name") },
              leadingIcon = { Icon(Icons.Default.AppRegistration, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("edit_app_name_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editAppSubtitle,
              onValueChange = { editAppSubtitle = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Kaulimbiu (Subtitle)" else "Tagline / Subtitle") },
              leadingIcon = { Icon(Icons.Default.ShortText, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("edit_app_subtitle_field"),
              shape = RoundedCornerShape(12.dp)
            )

            Divider(color = SafeRideCardBorder)

            Text(
              text = if (language == AppLanguage.SWAHILI) "Mandhari na Rangi za Programu (Themes):" else "App Color Theme Preset:",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp,
              color = MaterialTheme.colorScheme.onSurface
            )

            // Theme Preset Swatches
            val themePresetsList = listOf(
              Triple(ThemePreset.NAVY_GOLD, "Navy & Gold", Pair(SafeRideBlue, SafeRideAmberDark)),
              Triple(ThemePreset.ROYAL_BLUE, "Royal Azure", Pair(Color(0xFF0284C7), Color(0xFF2563EB))),
              Triple(ThemePreset.EMERALD_SAFETY, "Emerald Green", Pair(Color(0xFF059669), Color(0xFF10B981))),
              Triple(ThemePreset.CRIMSON_GUARD, "Crimson Safety", Pair(Color(0xFFDC2626), Color(0xFFF59E0B))),
              Triple(ThemePreset.PURPLE_ACADEMY, "Purple Academy", Pair(Color(0xFF7C3AED), Color(0xFFA855F7)))
            )

            themePresetsList.forEach { (preset, label, colors) ->
              val (color1, color2) = colors
              val isSelected = editThemePreset == preset
              Surface(
                modifier = Modifier
                  .fillMaxWidth()
                  .clip(RoundedCornerShape(12.dp))
                  .clickable { editThemePreset = preset }
                  .testTag("theme_preset_${preset.name.lowercase()}"),
                color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else SafeRideGrayLight,
                shape = RoundedCornerShape(12.dp)
              ) {
                Row(
                  modifier = Modifier.padding(12.dp),
                  verticalAlignment = Alignment.CenterVertically,
                  horizontalArrangement = Arrangement.SpaceBetween
                ) {
                  Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color1)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Box(
                      modifier = Modifier
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(color2)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(text = label, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal, fontSize = 13.sp)
                  }

                  if (isSelected) {
                    Icon(Icons.Default.CheckCircle, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                  }
                }
              }
            }

            // Dark Mode Switch
            Card(
              shape = RoundedCornerShape(12.dp),
              colors = CardDefaults.cardColors(containerColor = SafeRideGrayLight)
            ) {
              Row(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
              ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Icon(
                    imageVector = if (editIsDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                    contentDescription = null,
                    tint = if (editIsDarkMode) SafeRideAmber else SafeRideNavy
                  )
                  Spacer(modifier = Modifier.width(10.dp))
                  Column {
                    Text(
                      text = if (language == AppLanguage.SWAHILI) "Hali ya Giza (Dark Theme)" else "Dark Theme Mode",
                      fontWeight = FontWeight.Bold,
                      fontSize = 13.sp
                    )
                    Text(
                      text = if (language == AppLanguage.SWAHILI) "Inapunguza mwanga wakati wa usiku" else "Reduces glare and battery usage",
                      fontSize = 11.sp,
                      color = SafeRideGray
                    )
                  }
                }

                Switch(
                  checked = editIsDarkMode,
                  onCheckedChange = { editIsDarkMode = it },
                  modifier = Modifier.testTag("dark_mode_switch")
                )
              }
            }
          }

          // TAB 1: SCHOOL INFO & HELPLINE
          if (selectedTab == 1) {
            Text(
              text = if (language == AppLanguage.SWAHILI) "Taarifa za Shule na Namba za Dharura" else "School Details & Emergency Contacts",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )

            OutlinedTextField(
              value = editSchoolName,
              onValueChange = { editSchoolName = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Jina la Shule" else "School Name") },
              leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_school_name_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editEmergencyPhone,
              onValueChange = { editEmergencyPhone = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Namba ya Simu ya Dharura / Dispatch" else "Emergency Hotline Phone") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_emergency_phone_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editDispatchEmail,
              onValueChange = { editDispatchEmail = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Barua Pepe ya Ofisi ya Usafiri" else "Transport Dispatch Email") },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_dispatch_email_field"),
              shape = RoundedCornerShape(12.dp)
            )

            Column {
              Text(
                text = "${if (language == AppLanguage.SWAHILI) "Kikomo cha Mwendokasi wa Basi: " else "Speed Limit Threshold: "} $editSpeedLimit km/h",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
              )
              Slider(
                value = editSpeedLimit.toFloat(),
                onValueChange = { editSpeedLimit = it.toInt() },
                valueRange = 30f..80f,
                steps = 9,
                modifier = Modifier.fillMaxWidth().testTag("edit_speed_limit_slider")
              )
            }
          }

          // TAB 2: FLEET & DRIVERS
          if (selectedTab == 2) {
            Text(
              text = if (language == AppLanguage.SWAHILI) "Badili Taarifa za Basi na Dereva" else "Edit Active Bus & Driver Details",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )

            OutlinedTextField(
              value = editBusPlate,
              onValueChange = { editBusPlate = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Namba ya Gari (Plate Number)" else "Bus Plate Number") },
              leadingIcon = { Icon(Icons.Default.DirectionsBus, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_bus_plate_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editDriverName,
              onValueChange = { editDriverName = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Jina la Dereva" else "Driver's Name") },
              leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_driver_name_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editDriverPhone,
              onValueChange = { editDriverPhone = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Simu ya Dereva" else "Driver's Phone") },
              leadingIcon = { Icon(Icons.Default.PhoneAndroid, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_driver_phone_field"),
              shape = RoundedCornerShape(12.dp)
            )
          }

          // TAB 3: STUDENTS
          if (selectedTab == 3) {
            Text(
              text = if (language == AppLanguage.SWAHILI) "Taarifa za Mwanafunzi na Mpokeaji" else "Student & Authorized Pickup Details",
              fontWeight = FontWeight.Bold,
              fontSize = 13.sp
            )

            OutlinedTextField(
              value = editStudentName,
              onValueChange = { editStudentName = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Jina la Mtoto" else "Student Name") },
              leadingIcon = { Icon(Icons.Default.Face, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_student_name_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editStudentGrade,
              onValueChange = { editStudentGrade = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Darasa / Kidato" else "Grade / Class") },
              leadingIcon = { Icon(Icons.Default.Class, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_student_grade_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editPickupPerson,
              onValueChange = { editPickupPerson = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Mtu Aliyeidhinishwa Kumpokea" else "Authorized Pickup Person") },
              leadingIcon = { Icon(Icons.Default.FamilyRestroom, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_pickup_person_field"),
              shape = RoundedCornerShape(12.dp)
            )

            OutlinedTextField(
              value = editPickupPhone,
              onValueChange = { editPickupPhone = it },
              label = { Text(if (language == AppLanguage.SWAHILI) "Simu ya Mpokeaji" else "Pickup Person Phone") },
              leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
              modifier = Modifier.fillMaxWidth().testTag("edit_pickup_phone_field"),
              shape = RoundedCornerShape(12.dp)
            )
          }

          if (saveSuccessMessage != null) {
            Surface(
              color = SafeRideGreenLight,
              shape = RoundedCornerShape(8.dp),
              modifier = Modifier.fillMaxWidth()
            ) {
              Row(
                modifier = Modifier.padding(10.dp),
                verticalAlignment = Alignment.CenterVertically
              ) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeRideGreenDark, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = saveSuccessMessage!!, color = SafeRideGreenDark, fontWeight = FontWeight.Bold, fontSize = 11.sp)
              }
            }
          }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Bottom Actions: Save Changes & Reset
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(8.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          OutlinedButton(
            onClick = {
              repository.resetCustomization()
              onDismiss()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.testTag("admin_reset_defaults_button")
          ) {
            Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(14.dp))
            Spacer(modifier = Modifier.width(4.dp))
            Text(if (language == AppLanguage.SWAHILI) "Weka Upya" else "Reset")
          }

          Button(
            onClick = {
              // Apply branding and theme
              repository.updateAppBranding(
                appTitle = editAppTitle.trim().ifBlank { "SafeRide" },
                subtitle = editAppSubtitle.trim(),
                profileAvatar = editSelectedAvatar,
                adminName = editAdminName.trim()
              )
              repository.updateTheme(editThemePreset, editIsDarkMode)

              // Apply school details
              repository.updateSchoolDetails(
                schoolName = editSchoolName.trim(),
                emergencyPhone = editEmergencyPhone.trim(),
                dispatchEmail = editDispatchEmail.trim(),
                speedLimit = editSpeedLimit
              )

              // Apply bus details
              repository.updateActiveBusDetails(
                plateNumber = editBusPlate.trim().uppercase(),
                driverName = editDriverName.trim(),
                driverPhone = editDriverPhone.trim()
              )

              // Apply student profile
              repository.updateStudentProfile(
                studentId = firstStudent.id,
                name = editStudentName.trim(),
                grade = editStudentGrade.trim(),
                pickupName = editPickupPerson.trim(),
                pickupPhone = editPickupPhone.trim()
              )

              saveSuccessMessage = if (language == AppLanguage.SWAHILI)
                "Mabadiliko yote yamehifadhiwa kikamilifu!"
              else
                "All app customizations saved successfully!"

              onDismiss()
            },
            shape = RoundedCornerShape(10.dp),
            modifier = Modifier.weight(1f).testTag("admin_save_changes_button"),
            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
          ) {
            Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = if (language == AppLanguage.SWAHILI) "Hifadhi Mabadiliko" else "Save App Changes",
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
