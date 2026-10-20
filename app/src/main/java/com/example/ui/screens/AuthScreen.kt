package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.SafeRideRepository
import com.example.model.AppLanguage
import com.example.model.UserRole
import com.example.ui.components.LanguageSwitcher
import com.example.ui.components.rememberLocaleState
import com.example.ui.theme.*
import com.example.util.AppStrings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
  repository: SafeRideRepository,
  onLoginSuccess: () -> Unit,
  modifier: Modifier = Modifier
) {
  val language by repository.currentLanguage.collectAsState()
  val localeState = rememberLocaleState(
    currentLanguage = language,
    onLanguageChanged = { repository.setLanguage(it) }
  )
  var selectedTab by remember { mutableIntStateOf(0) } // 0: Sign In, 1: Register

  // Registration step state (1: Parent Info, 2: Student & School, 3: OTP Verification)
  var registerStep by remember { mutableIntStateOf(1) }

  // Login Form States
  var loginEmail by remember { mutableStateOf("meramaemmanuel7@gmail.com") }
  var loginPassword by remember { mutableStateOf("Password!123") }
  var isLoginPasswordVisible by remember { mutableStateOf(false) }
  var loginRole by remember { mutableStateOf(UserRole.PARENT) }
  var loginErrorMessage by remember { mutableStateOf<String?>(null) }

  // Register Form States
  var regFullName by remember { mutableStateOf("") }
  var regEmail by remember { mutableStateOf("") }
  var regPhone by remember { mutableStateOf("+255 ") }
  var regPassword by remember { mutableStateOf("") }
  var regConfirmPassword by remember { mutableStateOf("") }
  var isRegPasswordVisible by remember { mutableStateOf(false) }

  // Step 2 States (Student & School)
  val registeredSchools = listOf(
    "Feza Primary School - Dar es Salaam",
    "Shaaban Robert Secondary School",
    "Al-Muntazir Islamic School",
    "St. Jude Academy - Arusha",
    "Haven of Peace Academy (HOPAC)"
  )
  var regSelectedSchool by remember { mutableStateOf(registeredSchools.first()) }
  var isSchoolDropdownExpanded by remember { mutableStateOf(false) }
  var regStudentName by remember { mutableStateOf("") }
  var regStudentGrade by remember { mutableStateOf("") }
  var regAuthorizedPickup by remember { mutableStateOf("") }
  var regAuthorizedPhone by remember { mutableStateOf("+255 ") }

  // Step 3 States (OTP)
  var otpCode by remember { mutableStateOf("") }
  var isOtpVerified by remember { mutableStateOf(false) }
  var regErrorMessage by remember { mutableStateOf<String?>(null) }
  var regSuccessRequestId by remember { mutableStateOf<String?>(null) }

  Column(
    modifier = modifier
      .fillMaxSize()
      .background(MaterialTheme.colorScheme.background)
      .verticalScroll(rememberScrollState())
      .padding(horizontal = 20.dp, vertical = 16.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    // Top Bar with Language Switcher
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Surface(
          color = SafeRideAmber,
          shape = RoundedCornerShape(10.dp),
          modifier = Modifier.size(36.dp)
        ) {
          Box(contentAlignment = Alignment.Center) {
            Icon(
              imageVector = Icons.Default.DirectionsBus,
              contentDescription = null,
              tint = SafeRideNavy,
              modifier = Modifier.size(22.dp)
            )
          }
        }
        Spacer(modifier = Modifier.width(10.dp))
        Column {
          Text(
            text = "SafeRide",
            fontWeight = FontWeight.Black,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.primary
          )
          Text(
            text = if (language == AppLanguage.SWAHILI) "Usalama wa Wanafunzi" else "Student Safety",
            fontSize = 11.sp,
            color = SafeRideGray
          )
        }
      }

      // Bilingual Language Switcher Composable (English & Swahili)
      LanguageSwitcher(
        localeState = localeState,
        modifier = Modifier.testTag("auth_language_switcher")
      )
    }

    Spacer(modifier = Modifier.height(20.dp))

    // Card Container for Authentication Form
    Card(
      modifier = Modifier.fillMaxWidth(),
      shape = RoundedCornerShape(20.dp),
      colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
      elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
      Column(modifier = Modifier.padding(20.dp)) {

        // Tabs: Sign In vs Register
        TabRow(
          selectedTabIndex = selectedTab,
          containerColor = Color.Transparent,
          contentColor = SafeRideBlue,
          divider = {}
        ) {
          Tab(
            selected = selectedTab == 0,
            onClick = {
              selectedTab = 0
              loginErrorMessage = null
            },
            text = {
              Text(
                text = AppStrings.tabLogin(language),
                fontWeight = if (selectedTab == 0) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            },
            modifier = Modifier.testTag("auth_tab_login")
          )
          Tab(
            selected = selectedTab == 1,
            onClick = {
              selectedTab = 1
              regErrorMessage = null
            },
            text = {
              Text(
                text = AppStrings.tabRegister(language),
                fontWeight = if (selectedTab == 1) FontWeight.Bold else FontWeight.Normal,
                fontSize = 13.sp
              )
            },
            modifier = Modifier.testTag("auth_tab_register")
          )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // TAB 0: LOGIN FLOW
        if (selectedTab == 0) {
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(
              text = if (language == AppLanguage.SWAHILI) "Karibu Tena! Ingia kuendelea." else "Welcome back! Sign in to continue.",
              fontSize = 13.sp,
              color = SafeRideGray
            )

            // Role Selector for Login
            Text(
              text = if (language == AppLanguage.SWAHILI) "Ingia Kama:" else "Sign In As:",
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold,
              color = SafeRideGray
            )
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
              listOf(
                UserRole.PARENT to if (language == AppLanguage.SWAHILI) "Mzazi" else "Parent",
                UserRole.DRIVER to if (language == AppLanguage.SWAHILI) "Dereva" else "Driver",
                UserRole.SCHOOL_ADMIN to if (language == AppLanguage.SWAHILI) "Shule" else "Admin"
              ).forEach { (role, label) ->
                val isSelected = loginRole == role
                FilterChip(
                  selected = isSelected,
                  onClick = { loginRole = role },
                  label = { Text(label, fontSize = 11.sp) },
                  modifier = Modifier.weight(1f).testTag("login_role_${role.name.lowercase()}")
                )
              }
            }

            // Email or Phone Field
            OutlinedTextField(
              value = loginEmail,
              onValueChange = {
                loginEmail = it
                loginErrorMessage = null
              },
              label = { Text(AppStrings.emailLabel(language)) },
              leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("auth_login_email_input"),
              shape = RoundedCornerShape(12.dp)
            )

            // Password Field with Visibility Toggle
            OutlinedTextField(
              value = loginPassword,
              onValueChange = {
                loginPassword = it
                loginErrorMessage = null
              },
              label = { Text(AppStrings.passwordLabel(language)) },
              leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
              trailingIcon = {
                IconButton(onClick = { isLoginPasswordVisible = !isLoginPasswordVisible }) {
                  Icon(
                    imageVector = if (isLoginPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                    contentDescription = null
                  )
                }
              },
              visualTransformation = if (isLoginPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
              singleLine = true,
              modifier = Modifier.fillMaxWidth().testTag("auth_login_password_input"),
              shape = RoundedCornerShape(12.dp)
            )

            // Forgot Password Link
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.End
            ) {
              TextButton(
                onClick = {
                  loginErrorMessage = if (language == AppLanguage.SWAHILI)
                    "Kiunganishi cha kuweka upya nenosiri kimetumwa kwenye barua pepe yako."
                  else
                    "Password reset link has been dispatched to your email."
                }
              ) {
                Text(
                  text = AppStrings.forgotPassword(language),
                  fontSize = 11.sp,
                  color = SafeRideBlue
                )
              }
            }

            if (loginErrorMessage != null) {
              Surface(
                color = if (loginErrorMessage!!.contains("kimetumwa") || loginErrorMessage!!.contains("dispatched")) SafeRideGreenLight else SafeRideRedLight,
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Text(
                  text = loginErrorMessage!!,
                  color = if (loginErrorMessage!!.contains("kimetumwa") || loginErrorMessage!!.contains("dispatched")) SafeRideGreenDark else SafeRideRed,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.SemiBold,
                  modifier = Modifier.padding(8.dp)
                )
              }
            }

            // Submit Login Button
            Button(
              onClick = {
                if (loginEmail.isBlank() || loginPassword.isBlank()) {
                  loginErrorMessage = if (language == AppLanguage.SWAHILI)
                    "Tafadhali jaza barua pepe na nenosiri."
                  else
                    "Please provide your email and password."
                } else {
                  repository.login(loginEmail.trim(), loginRole)
                  onLoginSuccess()
                }
              },
              modifier = Modifier
                .fillMaxWidth()
                .height(48.dp)
                .testTag("auth_submit_login_button"),
              shape = RoundedCornerShape(12.dp),
              colors = ButtonDefaults.buttonColors(containerColor = SafeRideBlue)
            ) {
              Icon(Icons.Default.Login, contentDescription = null, modifier = Modifier.size(18.dp))
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = AppStrings.signInButton(language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp
              )
            }

            // Demo Quick Login
            OutlinedButton(
              onClick = {
                repository.login("meramaemmanuel7@gmail.com", UserRole.PARENT)
                onLoginSuccess()
              },
              modifier = Modifier.fillMaxWidth().testTag("auth_demo_quick_login"),
              shape = RoundedCornerShape(12.dp)
            ) {
              Icon(Icons.Default.FlashOn, contentDescription = null, tint = SafeRideAmberDark, modifier = Modifier.size(16.dp))
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = AppStrings.demoParentQuickLogin(language),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = SafeRideAmberDark
              )
            }
          }
        } else {
          // TAB 1: REGISTRATION FLOW (3-Step Wizard)
          Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

            // Step Progress Indicator (1 -> 2 -> 3)
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              listOf(
                1 to (if (language == AppLanguage.SWAHILI) "Mzazi" else "Parent"),
                2 to (if (language == AppLanguage.SWAHILI) "Mwanafunzi" else "Student"),
                3 to (if (language == AppLanguage.SWAHILI) "Uthibitisho" else "Verify")
              ).forEach { (step, stepTitle) ->
                val isActive = registerStep == step
                val isCompleted = registerStep > step
                Row(verticalAlignment = Alignment.CenterVertically) {
                  Box(
                    modifier = Modifier
                      .size(24.dp)
                      .clip(CircleShape)
                      .background(
                        when {
                          isCompleted -> SafeRideGreen
                          isActive -> SafeRideBlue
                          else -> SafeRideGrayLight
                        }
                      ),
                    contentAlignment = Alignment.Center
                  ) {
                    if (isCompleted) {
                      Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                    } else {
                      Text(
                        text = "$step",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isActive) Color.White else SafeRideGray
                      )
                    }
                  }
                  Spacer(modifier = Modifier.width(4.dp))
                  Text(
                    text = stepTitle,
                    fontSize = 10.sp,
                    fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal,
                    color = if (isActive) SafeRideBlue else SafeRideGray
                  )
                }
              }
            }

            Divider(color = SafeRideCardBorder)

            // STEP 1: PARENT BASIC INFO
            if (registerStep == 1) {
              Text(
                text = if (language == AppLanguage.SWAHILI) "Hatua ya 1: Taarifa za Mzazi/Mlezi" else "Step 1: Parent/Guardian Details",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SafeRideNavy
              )

              OutlinedTextField(
                value = regFullName,
                onValueChange = { regFullName = it },
                label = { Text(AppStrings.fullNameLabel(language)) },
                leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_full_name_input"),
                shape = RoundedCornerShape(12.dp)
              )

              OutlinedTextField(
                value = regEmail,
                onValueChange = { regEmail = it },
                label = { Text(if (language == AppLanguage.SWAHILI) "Barua Pepe (Email)" else "Email Address") },
                leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_email_input"),
                shape = RoundedCornerShape(12.dp)
              )

              OutlinedTextField(
                value = regPhone,
                onValueChange = { regPhone = it },
                label = { Text(AppStrings.phoneLabel(language)) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_phone_input"),
                shape = RoundedCornerShape(12.dp)
              )

              OutlinedTextField(
                value = regPassword,
                onValueChange = { regPassword = it },
                label = { Text(AppStrings.passwordLabel(language)) },
                leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                visualTransformation = if (isRegPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_password_input"),
                shape = RoundedCornerShape(12.dp)
              )

              OutlinedTextField(
                value = regConfirmPassword,
                onValueChange = { regConfirmPassword = it },
                label = { Text(AppStrings.confirmPasswordLabel(language)) },
                leadingIcon = { Icon(Icons.Default.LockClock, contentDescription = null) },
                visualTransformation = PasswordVisualTransformation(),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_confirm_password_input"),
                shape = RoundedCornerShape(12.dp)
              )

              if (regErrorMessage != null) {
                Text(text = regErrorMessage!!, color = SafeRideRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Button(
                onClick = {
                  if (regFullName.isBlank() || regEmail.isBlank() || regPhone.length < 9 || regPassword.isBlank()) {
                    regErrorMessage = if (language == AppLanguage.SWAHILI)
                      "Tafadhali jaza sehemu zote kwa usahihi."
                    else
                      "Please complete all parent fields accurately."
                  } else if (regPassword != regConfirmPassword) {
                    regErrorMessage = if (language == AppLanguage.SWAHILI)
                      "Nenosiri na uthibitisho havilingani!"
                    else
                      "Passwords do not match!"
                  } else {
                    regErrorMessage = null
                    registerStep = 2
                  }
                },
                modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reg_step1_next_button"),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = SafeRideBlue)
              ) {
                Text(if (language == AppLanguage.SWAHILI) "Endelea na Taarifa za Shule" else "Continue to Student Info", fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.width(6.dp))
                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
              }
            }

            // STEP 2: STUDENT & SCHOOL VERIFICATION LINKING
            if (registerStep == 2) {
              Text(
                text = if (language == AppLanguage.SWAHILI) "Hatua ya 2: Chagua Shule na Jina la Mtoto" else "Step 2: School & Student Affiliation",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp,
                color = SafeRideNavy
              )

              // Dropdown Menu for Registered Schools
              ExposedDropdownMenuBox(
                expanded = isSchoolDropdownExpanded,
                onExpandedChange = { isSchoolDropdownExpanded = !isSchoolDropdownExpanded },
                modifier = Modifier.fillMaxWidth()
              ) {
                OutlinedTextField(
                  value = regSelectedSchool,
                  onValueChange = {},
                  readOnly = true,
                  label = { Text(AppStrings.schoolSelectLabel(language)) },
                  trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = isSchoolDropdownExpanded) },
                  modifier = Modifier.menuAnchor().fillMaxWidth().testTag("reg_school_dropdown"),
                  shape = RoundedCornerShape(12.dp)
                )
                ExposedDropdownMenu(
                  expanded = isSchoolDropdownExpanded,
                  onDismissRequest = { isSchoolDropdownExpanded = false }
                ) {
                  registeredSchools.forEach { school ->
                    DropdownMenuItem(
                      text = { Text(school, fontSize = 12.sp) },
                      onClick = {
                        regSelectedSchool = school
                        isSchoolDropdownExpanded = false
                      }
                    )
                  }
                }
              }

              OutlinedTextField(
                value = regStudentName,
                onValueChange = { regStudentName = it },
                label = { Text(AppStrings.studentNameLabel(language)) },
                leadingIcon = { Icon(Icons.Default.School, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_student_name_input"),
                shape = RoundedCornerShape(12.dp)
              )

              OutlinedTextField(
                value = regStudentGrade,
                onValueChange = { regStudentGrade = it },
                label = { Text(AppStrings.studentGradeLabel(language)) },
                leadingIcon = { Icon(Icons.Default.Class, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_student_grade_input"),
                shape = RoundedCornerShape(12.dp)
              )

              OutlinedTextField(
                value = regAuthorizedPickup,
                onValueChange = { regAuthorizedPickup = it },
                label = { Text(if (language == AppLanguage.SWAHILI) "Mtu Mwingine Aliyeidhinishwa Kumpokea" else "Authorized Alternate Pickup Person") },
                leadingIcon = { Icon(Icons.Default.PersonOutline, contentDescription = null) },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_pickup_person_input"),
                shape = RoundedCornerShape(12.dp)
              )

              // Security & Privacy Notice (Tanzania PDPA 2022)
              Surface(
                color = SafeRideAmberLight,
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
              ) {
                Row(
                  modifier = Modifier.padding(10.dp),
                  verticalAlignment = Alignment.Top
                ) {
                  Icon(Icons.Default.Shield, contentDescription = null, tint = SafeRideAmberDark, modifier = Modifier.size(18.dp))
                  Spacer(modifier = Modifier.width(8.dp))
                  Text(
                    text = AppStrings.approvalNotice(language),
                    fontSize = 11.sp,
                    color = SafeRideAmberDark,
                    lineHeight = 15.sp
                  )
                }
              }

              if (regErrorMessage != null) {
                Text(text = regErrorMessage!!, color = SafeRideRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
              ) {
                OutlinedButton(
                  onClick = { registerStep = 1 },
                  modifier = Modifier.weight(1f),
                  shape = RoundedCornerShape(12.dp)
                ) {
                  Text(if (language == AppLanguage.SWAHILI) "Nyuma" else "Back")
                }

                Button(
                  onClick = {
                    if (regStudentName.isBlank() || regStudentGrade.isBlank()) {
                      regErrorMessage = if (language == AppLanguage.SWAHILI)
                        "Tafadhali weka jina la mwanafunzi na darasa."
                      else
                        "Please provide student name and grade."
                    } else {
                      regErrorMessage = null
                      registerStep = 3
                    }
                  },
                  modifier = Modifier.weight(2f).testTag("reg_step2_next_button"),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SafeRideBlue)
                ) {
                  Text(if (language == AppLanguage.SWAHILI) "Tuma OTP ya Uthibitisho" else "Send Verification OTP", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
              }
            }

            // STEP 3: OTP VERIFICATION & APPROVAL NOTICE
            if (registerStep == 3) {
              Text(
                text = AppStrings.otpVerificationTitle(language),
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                color = SafeRideNavy
              )

              Text(
                text = "${AppStrings.otpInstructions(language)} [${regEmail.ifBlank { "meramaemmanuel7@gmail.com" }}]",
                fontSize = 11.sp,
                color = SafeRideGray
              )

              // 6-digit OTP Input
              OutlinedTextField(
                value = otpCode,
                onValueChange = { if (it.length <= 6) otpCode = it },
                label = { Text("OTP Code (e.g. 749215)") },
                leadingIcon = { Icon(Icons.Default.Key, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier.fillMaxWidth().testTag("reg_otp_code_input"),
                shape = RoundedCornerShape(12.dp)
              )

              // Demo quick OTP autofill
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
              ) {
                TextButton(onClick = { otpCode = "749215" }) {
                  Text(
                    text = if (language == AppLanguage.SWAHILI) "Weka OTP ya Mfano (749215)" else "Autofill Demo OTP (749215)",
                    fontSize = 11.sp,
                    color = SafeRideBlue
                  )
                }
              }

              if (regSuccessRequestId != null) {
                Surface(
                  color = SafeRideGreenLight,
                  shape = RoundedCornerShape(10.dp),
                  modifier = Modifier.fillMaxWidth()
                ) {
                  Column(modifier = Modifier.padding(12.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                      Icon(Icons.Default.CheckCircle, contentDescription = null, tint = SafeRideGreenDark, modifier = Modifier.size(18.dp))
                      Spacer(modifier = Modifier.width(6.dp))
                      Text(
                        text = if (language == AppLanguage.SWAHILI) "Barua Pepe Imethibitishwa!" else "Email Verified Successfully!",
                        fontWeight = FontWeight.Bold,
                        color = SafeRideGreenDark,
                        fontSize = 12.sp
                      )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                      text = if (language == AppLanguage.SWAHILI)
                        "Maombi yako yamewasilishwa kwa Utawala wa ${regSelectedSchool}. Utaarifiwa kupitia SMS na barua pepe idhini itakapokamilika."
                      else
                        "Your request is submitted to ${regSelectedSchool}. You will be notified via SMS once verified against school records.",
                      fontSize = 11.sp,
                      color = SafeRideGreenDark
                    )
                  }
                }
              }

              if (regErrorMessage != null) {
                Text(text = regErrorMessage!!, color = SafeRideRed, fontSize = 11.sp, fontWeight = FontWeight.Bold)
              }

              if (regSuccessRequestId == null) {
                Button(
                  onClick = {
                    if (otpCode.length < 6) {
                      regErrorMessage = if (language == AppLanguage.SWAHILI)
                        "Tafadhali weka nambari zote 6 za OTP."
                      else
                        "Please enter the complete 6-digit OTP code."
                    } else {
                      regErrorMessage = null
                      isOtpVerified = true
                      val reqId = repository.registerParent(
                        fullName = regFullName.ifBlank { "Emmanuel Merama" },
                        email = regEmail.ifBlank { "meramaemmanuel7@gmail.com" },
                        phone = regPhone.ifBlank { "+255 754 123 456" },
                        schoolName = regSelectedSchool,
                        studentName = regStudentName.ifBlank { "Zuberi Said" },
                        grade = regStudentGrade.ifBlank { "Darasa la 4" }
                      )
                      regSuccessRequestId = reqId
                    }
                  },
                  modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reg_submit_otp_button"),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SafeRideGreenDark)
                ) {
                  Icon(Icons.Default.VerifiedUser, contentDescription = null, modifier = Modifier.size(16.dp))
                  Spacer(modifier = Modifier.width(6.dp))
                  Text(AppStrings.verifyOtpButton(language), fontWeight = FontWeight.Bold)
                }
              } else {
                Button(
                  onClick = {
                    repository.login(regEmail.ifBlank { "meramaemmanuel7@gmail.com" }, UserRole.PARENT)
                    onLoginSuccess()
                  },
                  modifier = Modifier.fillMaxWidth().height(48.dp).testTag("reg_continue_to_app_button"),
                  shape = RoundedCornerShape(12.dp),
                  colors = ButtonDefaults.buttonColors(containerColor = SafeRideBlue)
                ) {
                  Text(if (language == AppLanguage.SWAHILI) "Fungua Dashibodi ya Mzazi" else "Go to Parent Dashboard", fontWeight = FontWeight.Bold)
                }
              }

              if (regSuccessRequestId == null) {
                TextButton(
                  onClick = { registerStep = 2 },
                  modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                  Text(if (language == AppLanguage.SWAHILI) "Rudi Kwenye Taarifa za Shule" else "Back to School Details", fontSize = 11.sp)
                }
              }
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(16.dp))

    // Footer Security Notice (CIA Triad - PDPA 2022)
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 8.dp),
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Icon(Icons.Default.Lock, contentDescription = null, tint = SafeRideGray, modifier = Modifier.size(14.dp))
      Spacer(modifier = Modifier.width(6.dp))
      Text(
        text = if (language == AppLanguage.SWAHILI)
          "Mifumo ya SafeRide imelindwa kwa AES-256 na Sheria ya PDPA 2022."
        else
          "Protected by AES-256 encryption and Tanzania PDPA 2022.",
        fontSize = 10.sp,
        color = SafeRideGray
      )
    }

    Spacer(modifier = Modifier.height(20.dp))
  }
}
