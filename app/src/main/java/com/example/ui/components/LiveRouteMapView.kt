package com.example.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.BusRoute
import com.example.model.StopStatus
import com.example.ui.theme.*
import com.example.util.AppStrings

@Composable
fun LiveRouteMapView(
  busRoute: BusRoute,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val infiniteTransition = rememberInfiniteTransition(label = "busPulse")
  val pulseRadius by infiniteTransition.animateFloat(
    initialValue = 10f,
    targetValue = 28f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulseRadius"
  )
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.8f,
    targetValue = 0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1200, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "pulseAlpha"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("live_route_map_card"),
    colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      // Header with Bus Plate & Speed
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Box(
            modifier = Modifier
              .size(10.dp)
              .clip(CircleShape)
              .background(if (busRoute.isGpsBroadcasting) SafeRideGreen else SafeRideAmber)
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (busRoute.isGpsBroadcasting) "GPS LIVE: ${busRoute.busPlateNumber}" else "GPS OFFLINE",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp
          )
        }

        Surface(
          color = SafeRideNavy,
          shape = RoundedCornerShape(8.dp)
        ) {
          Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Speed,
              contentDescription = null,
              tint = SafeRideAmber,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "${busRoute.currentSpeedKmh} km/h",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.SemiBold
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Custom Visual Route Canvas
      Box(
        modifier = Modifier
          .fillMaxWidth()
          .height(130.dp)
          .clip(RoundedCornerShape(12.dp))
          .background(Color(0xFF0F172A))
          .padding(horizontal = 16.dp),
        contentAlignment = Alignment.Center
      ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
          val width = size.width
          val height = size.height
          val yMid = height / 2f
          val stopCount = busRoute.stops.size
          val spacing = width / (stopCount - 1).coerceAtLeast(1)

          // Draw Road Base
          drawLine(
            color = Color(0xFF334155),
            start = Offset(0f, yMid),
            end = Offset(width, yMid),
            strokeWidth = 24f,
            cap = StrokeCap.Round
          )

          // Road Dash Center
          drawLine(
            color = Color(0xFF64748B),
            start = Offset(8f, yMid),
            end = Offset(width - 8f, yMid),
            strokeWidth = 3f,
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(16f, 12f), 0f)
          )

          // Active Traveled Route Highlight
          val activeBusX = (busRoute.currentStopIndex * spacing).coerceIn(0f, width)
          drawLine(
            color = SafeRideBlueLight,
            start = Offset(0f, yMid),
            end = Offset(activeBusX, yMid),
            strokeWidth = 10f,
            cap = StrokeCap.Round
          )

          // Draw Stops Markers
          busRoute.stops.forEachIndexed { index, stop ->
            val stopX = index * spacing
            val stopColor = when (stop.status) {
              StopStatus.PASSED -> SafeRideGreen
              StopStatus.CURRENT -> SafeRideAmber
              StopStatus.UPCOMING -> Color(0xFF94A3B8)
            }

            drawCircle(
              color = Color(0xFF0F172A),
              radius = 12f,
              center = Offset(stopX, yMid)
            )
            drawCircle(
              color = stopColor,
              radius = 8f,
              center = Offset(stopX, yMid)
            )
          }

          // Draw Live Bus with Pulse on Current Stop
          drawCircle(
            color = SafeRideAmber.copy(alpha = pulseAlpha),
            radius = pulseRadius,
            center = Offset(activeBusX, yMid)
          )
          drawCircle(
            color = Color.White,
            radius = 14f,
            center = Offset(activeBusX, yMid)
          )
          drawCircle(
            color = SafeRideAmberDark,
            radius = 11f,
            center = Offset(activeBusX, yMid)
          )
        }

        // Home and School icons on endpoints
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .padding(bottom = 6.dp),
          horizontalArrangement = Arrangement.SpaceBetween
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.Home,
              contentDescription = null,
              tint = SafeRideGreen,
              modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = if (language == AppLanguage.SWAHILI) "Nyumbani" else "Home",
              color = Color(0xFFCBD5E1),
              fontSize = 10.sp
            )
          }

          Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
              text = if (language == AppLanguage.SWAHILI) "Shuleni" else "School",
              color = Color(0xFFCBD5E1),
              fontSize = 10.sp
            )
            Spacer(modifier = Modifier.width(4.dp))
            Icon(
              imageVector = Icons.Default.School,
              contentDescription = null,
              tint = SafeRideBlueLight,
              modifier = Modifier.size(14.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(10.dp))

      // Current Stop & ETA Information
      val currentStop = busRoute.stops.getOrNull(busRoute.currentStopIndex)
      val nextStop = busRoute.stops.getOrNull(busRoute.currentStopIndex + 1)

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Text(
            text = if (language == AppLanguage.SWAHILI) "Kituo cha Sasa:" else "Current Location:",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
          Text(
            text = currentStop?.name ?: "Barabarani",
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = 14.sp
          )
        }

        Column(horizontalAlignment = Alignment.End) {
          Text(
            text = if (language == AppLanguage.SWAHILI) "Kituo Kinachofuata:" else "Next Stop:",
            color = Color(0xFF94A3B8),
            fontSize = 11.sp
          )
          Text(
            text = nextStop?.name ?: if (language == AppLanguage.SWAHILI) "Shuleni (Mwisho)" else "School (Final)",
            color = SafeRideAmber,
            fontWeight = FontWeight.SemiBold,
            fontSize = 13.sp
          )
        }
      }

      // Emergency banner if active
      if (busRoute.activeEmergency != null) {
        Spacer(modifier = Modifier.height(8.dp))
        Surface(
          color = SafeRideRed.copy(alpha = 0.2f),
          shape = RoundedCornerShape(8.dp),
          border = androidx.compose.foundation.BorderStroke(1.dp, SafeRideRed)
        ) {
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Icon(
              imageVector = Icons.Default.Warning,
              contentDescription = null,
              tint = SafeRideRed,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "${if (language == AppLanguage.SWAHILI) "TAHADHARI: " else "ALERT: "}${busRoute.activeEmergency}",
              color = Color.White,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }
  }
}
