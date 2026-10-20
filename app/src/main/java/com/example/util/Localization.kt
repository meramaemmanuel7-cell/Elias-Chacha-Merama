package com.example.util

import com.example.model.AppLanguage

object AppStrings {
  fun appTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "SafeRide - Ufuatiliaji wa Usafiri"
    AppLanguage.ENGLISH -> "SafeRide - School Tracker"
  }

  fun roleParent(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Mzazi / Mlezi"
    AppLanguage.ENGLISH -> "Parent / Guardian"
  }

  fun roleDriver(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Dereva / Msaidizi"
    AppLanguage.ENGLISH -> "Driver / Conductor"
  }

  fun roleSchoolAdmin(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Msimamizi wa Shule"
    AppLanguage.ENGLISH -> "School Admin"
  }

  fun roleSuperAdmin(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Super Admin"
    AppLanguage.ENGLISH -> "Super Admin"
  }

  fun stageBoarded(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Amepanda basi"
    AppLanguage.ENGLISH -> "Boarded Bus"
  }

  fun stageOnTheWay(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Yupo njiani"
    AppLanguage.ENGLISH -> "On the way"
  }

  fun stageNearStop(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Kituo cha karibu"
    AppLanguage.ENGLISH -> "Near stop"
  }

  fun stageArrivedSafely(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Amefika salama"
    AppLanguage.ENGLISH -> "Arrived safely"
  }

  fun uploadTicket(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Pakia Tiketi ya Gari"
    AppLanguage.ENGLISH -> "Upload Bus Ticket"
  }

  fun trackByPlate(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Fuatilia kwa Namba ya Gari"
    AppLanguage.ENGLISH -> "Track by Bus Plate"
  }

  fun searchPlatePlaceholder(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Weka namba mfano T 418 DKP"
    AppLanguage.ENGLISH -> "Enter plate e.g. T 418 DKP"
  }

  fun liveMap(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Ramani ya Moja kwa Moja"
    AppLanguage.ENGLISH -> "Live Bus Map"
  }

  fun tripStops(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Vituo vya Safari"
    AppLanguage.ENGLISH -> "Trip Stops & ETA"
  }

  fun statusPassed(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Limepita"
    AppLanguage.ENGLISH -> "Departed"
  }

  fun statusCurrent(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Lipo hapa"
    AppLanguage.ENGLISH -> "Current Stop"
  }

  fun statusUpcoming(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Linakuja"
    AppLanguage.ENGLISH -> "Upcoming"
  }

  fun emergencySOS(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Dharura / SOS"
    AppLanguage.ENGLISH -> "Emergency / SOS"
  }

  fun boardingVerification(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Uthibitisho wa Kupanda/Kushuka"
    AppLanguage.ENGLISH -> "Boarding Verification"
  }

  fun smsFallbackNotice(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "SMS ya dharura imetumwa kwenye simu ya mzazi"
    AppLanguage.ENGLISH -> "Fallback SMS delivered to parent's phone"
  }

  fun offlineMode(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Hali ya Nje ya Mtandao (Offline Sync)"
    AppLanguage.ENGLISH -> "Offline Mode (Auto-Sync)"
  }

  // Auth Flow Strings
  fun tabLogin(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Ingia (Sign In)"
    AppLanguage.ENGLISH -> "Sign In"
  }

  fun tabRegister(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Jisajili (Register)"
    AppLanguage.ENGLISH -> "Register"
  }

  fun authSubtitle(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Ufuatiliaji Salama wa Safari ya Mwanafunzi"
    AppLanguage.ENGLISH -> "Safe Student Transit & School Bus Tracking"
  }

  fun emailLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Barua Pepe au Simu"
    AppLanguage.ENGLISH -> "Email or Phone"
  }

  fun passwordLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Nenosiri"
    AppLanguage.ENGLISH -> "Password"
  }

  fun confirmPasswordLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Thibitisha Nenosiri"
    AppLanguage.ENGLISH -> "Confirm Password"
  }

  fun forgotPassword(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Umesahau nenosiri?"
    AppLanguage.ENGLISH -> "Forgot password?"
  }

  fun signInButton(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Ingia Kwenye Mfumo"
    AppLanguage.ENGLISH -> "Sign In to Account"
  }

  fun demoParentQuickLogin(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Ingia Haraka Kama Mzazi (Demo)"
    AppLanguage.ENGLISH -> "Quick Parent Demo Sign In"
  }

  fun fullNameLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Jina Kamili la Mzazi"
    AppLanguage.ENGLISH -> "Parent's Full Name"
  }

  fun phoneLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Namba ya Simu (+255 ...)"
    AppLanguage.ENGLISH -> "Phone Number (+255 ...)"
  }

  fun schoolSelectLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Shule ya Mwanafunzi"
    AppLanguage.ENGLISH -> "Student's School"
  }

  fun studentNameLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Jina Kamili la Mtoto"
    AppLanguage.ENGLISH -> "Student's Full Name"
  }

  fun studentGradeLabel(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Darasa / Kidato (mfano: Darasa 4B)"
    AppLanguage.ENGLISH -> "Grade / Class (e.g. Grade 4B)"
  }

  fun registerButton(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Wasilisha Maombi ya Usajili"
    AppLanguage.ENGLISH -> "Submit Registration Request"
  }

  fun otpVerificationTitle(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Uthibitisho wa OTP (Barua Pepe/SMS)"
    AppLanguage.ENGLISH -> "OTP Verification (Email/SMS)"
  }

  fun otpInstructions(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Weka nambari 6 za siri zilizotumwa kwenye barua pepe yako ili kuwezesha akaunti."
    AppLanguage.ENGLISH -> "Enter the 6-digit verification code sent to your email to activate account."
  }

  fun verifyOtpButton(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Thibitisha Nambari ya Siri (OTP)"
    AppLanguage.ENGLISH -> "Verify Code (OTP)"
  }

  fun approvalNotice(lang: AppLanguage) = when (lang) {
    AppLanguage.SWAHILI -> "Ili kulinda usalama wa wanafunzi, msimamizi wa shule atalinganisha jina la mtoto na daftari la shule kabla ya kutoa idhini."
    AppLanguage.ENGLISH -> "To safeguard student security, the school administrator validates student details against school records before granting access."
  }
}
