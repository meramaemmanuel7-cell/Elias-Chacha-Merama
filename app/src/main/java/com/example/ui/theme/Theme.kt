package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.example.model.ThemePreset

fun getThemeColorScheme(preset: ThemePreset, isDark: Boolean): ColorScheme {
  val (primaryColor, secondaryColor) = when (preset) {
    ThemePreset.NAVY_GOLD -> Pair(SafeRideBlue, SafeRideAmberDark)
    ThemePreset.ROYAL_BLUE -> Pair(Color(0xFF0284C7), Color(0xFF2563EB))
    ThemePreset.EMERALD_SAFETY -> Pair(Color(0xFF059669), Color(0xFF10B981))
    ThemePreset.CRIMSON_GUARD -> Pair(Color(0xFFDC2626), Color(0xFFF59E0B))
    ThemePreset.PURPLE_ACADEMY -> Pair(Color(0xFF7C3AED), Color(0xFFA855F7))
  }

  return if (isDark) {
    darkColorScheme(
      primary = primaryColor,
      onPrimary = Color.White,
      primaryContainer = SafeRideNavyLight,
      onPrimaryContainer = Color.White,
      secondary = secondaryColor,
      onSecondary = Color.Black,
      tertiary = SafeRideGreen,
      background = Color(0xFF0F172A),
      surface = Color(0xFF1E293B),
      onBackground = Color(0xFFF8FAFC),
      onSurface = Color(0xFFF8FAFC),
      surfaceVariant = Color(0xFF334155),
      onSurfaceVariant = Color(0xFF94A3B8),
      error = SafeRideRed
    )
  } else {
    lightColorScheme(
      primary = primaryColor,
      onPrimary = Color.White,
      primaryContainer = Color(0xFFEFF6FF),
      onPrimaryContainer = primaryColor,
      secondary = secondaryColor,
      onSecondary = Color.White,
      secondaryContainer = SafeRideAmberLight,
      onSecondaryContainer = secondaryColor,
      tertiary = SafeRideGreenDark,
      background = Color(0xFFF8FAFC),
      surface = Color.White,
      onBackground = Color(0xFF0F172A),
      onSurface = Color(0xFF0F172A),
      surfaceVariant = SafeRideGrayLight,
      onSurfaceVariant = SafeRideGray,
      outline = SafeRideCardBorder,
      error = SafeRideRed
    )
  }
}

@Composable
fun MyApplicationTheme(
  themePreset: ThemePreset = ThemePreset.NAVY_GOLD,
  darkTheme: Boolean = false,
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme = when {
    dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
      val context = LocalContext.current
      if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    }
    else -> getThemeColorScheme(themePreset, darkTheme)
  }

  MaterialTheme(
    colorScheme = colorScheme,
    typography = Typography,
    content = content
  )
}
