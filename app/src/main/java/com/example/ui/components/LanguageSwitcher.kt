package com.example.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.ui.theme.SafeRideBlue
import com.example.ui.theme.SafeRideCardBorder
import com.example.ui.theme.SafeRideNavy

/**
 * State holder that manages the active locale/language for the application.
 */
@Stable
class LocaleState(
  initialLanguage: AppLanguage = AppLanguage.SWAHILI,
  private val onLanguageChanged: ((AppLanguage) -> Unit)? = null
) {
  var currentLanguage by mutableStateOf(initialLanguage)
    private set

  val isSwahili: Boolean get() = currentLanguage == AppLanguage.SWAHILI
  val isEnglish: Boolean get() = currentLanguage == AppLanguage.ENGLISH

  fun toggle() {
    val nextLanguage = if (currentLanguage == AppLanguage.SWAHILI) {
      AppLanguage.ENGLISH
    } else {
      AppLanguage.SWAHILI
    }
    setLanguage(nextLanguage)
  }

  fun setLanguage(language: AppLanguage) {
    if (currentLanguage != language) {
      currentLanguage = language
      onLanguageChanged?.invoke(language)
    }
  }
}

@Composable
fun rememberLocaleState(
  currentLanguage: AppLanguage,
  onLanguageChanged: (AppLanguage) -> Unit
): LocaleState {
  val localeState = remember(currentLanguage) {
    LocaleState(
      initialLanguage = currentLanguage,
      onLanguageChanged = onLanguageChanged
    )
  }
  return localeState
}

/**
 * Modern Material 3 Language Switcher composable with interactive toggle pills
 * between Kiswahili and English.
 */
@Composable
fun LanguageSwitcher(
  localeState: LocaleState,
  modifier: Modifier = Modifier
) {
  val isSwahili = localeState.isSwahili

  Surface(
    modifier = modifier
      .clip(RoundedCornerShape(24.dp))
      .border(1.dp, SafeRideCardBorder, RoundedCornerShape(24.dp))
      .testTag("language_switcher_toggle"),
    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
    shape = RoundedCornerShape(24.dp)
  ) {
    Row(
      modifier = Modifier
        .height(40.dp)
        .padding(3.dp),
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
      // Swahili Option Pill
      LanguageOptionPill(
        title = "Kiswahili",
        shortCode = "SW",
        isSelected = isSwahili,
        onClick = { localeState.setLanguage(AppLanguage.SWAHILI) },
        testTag = "lang_option_swahili"
      )

      // English Option Pill
      LanguageOptionPill(
        title = "English",
        shortCode = "EN",
        isSelected = !isSwahili,
        onClick = { localeState.setLanguage(AppLanguage.ENGLISH) },
        testTag = "lang_option_english"
      )
    }
  }
}

@Composable
private fun LanguageOptionPill(
  title: String,
  shortCode: String,
  isSelected: Boolean,
  onClick: () -> Unit,
  testTag: String
) {
  val backgroundColor by animateColorAsState(
    targetValue = if (isSelected) SafeRideBlue else Color.Transparent,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "pillBgColor"
  )
  val textColor by animateColorAsState(
    targetValue = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant,
    animationSpec = spring(stiffness = Spring.StiffnessMediumLow),
    label = "pillTextColor"
  )

  Box(
    modifier = Modifier
      .fillMaxHeight()
      .clip(RoundedCornerShape(20.dp))
      .background(backgroundColor)
      .clickable(
        role = Role.RadioButton,
        onClick = onClick
      )
      .padding(horizontal = 10.dp, vertical = 4.dp)
      .testTag(testTag),
    contentAlignment = Alignment.Center
  ) {
    Row(
      verticalAlignment = Alignment.CenterVertically,
      horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
      Icon(
        imageVector = Icons.Default.Language,
        contentDescription = null,
        tint = textColor,
        modifier = Modifier.size(14.dp)
      )
      Text(
        text = title,
        color = textColor,
        fontSize = 11.sp,
        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
      )
    }
  }
}
