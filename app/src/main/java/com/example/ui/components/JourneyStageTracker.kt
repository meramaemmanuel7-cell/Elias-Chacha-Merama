package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AppLanguage
import com.example.model.JourneyStage
import com.example.ui.theme.*

@Composable
fun JourneyStageTracker(
  currentStage: JourneyStage,
  language: AppLanguage,
  modifier: Modifier = Modifier
) {
  val stages = listOf(
    JourneyStage.BOARDED to if (language == AppLanguage.SWAHILI) "Amepanda\nBasi" else "Boarded\nBus",
    JourneyStage.ON_THE_WAY to if (language == AppLanguage.SWAHILI) "Yupo\nNjiani" else "On The\nWay",
    JourneyStage.NEAR_STOP to if (language == AppLanguage.SWAHILI) "Kituo cha\nKaribu" else "Near\nStop",
    JourneyStage.ARRIVED_SAFELY to if (language == AppLanguage.SWAHILI) "Amefika\nSalama!" else "Arrived\nSafely!"
  )

  Card(
    modifier = modifier
      .fillMaxWidth()
      .testTag("journey_stage_tracker"),
    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    shape = RoundedCornerShape(16.dp),
    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Text(
        text = if (language == AppLanguage.SWAHILI) "Hatua za Safari ya Mtoto" else "Child's Journey Progression",
        style = MaterialTheme.typography.titleSmall,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurface
      )

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
      ) {
        val currentStageIndex = when (currentStage) {
          JourneyStage.NOT_STARTED -> -1
          JourneyStage.BOARDED -> 0
          JourneyStage.ON_THE_WAY -> 1
          JourneyStage.NEAR_STOP -> 2
          JourneyStage.ARRIVED_SAFELY -> 3
        }

        stages.forEachIndexed { index, (stage, title) ->
          val isDone = index < currentStageIndex
          val isCurrent = index == currentStageIndex
          val isPending = index > currentStageIndex

          Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.weight(1f)
          ) {
            Box(
              modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(
                  when {
                    isDone -> SafeRideGreen
                    isCurrent -> SafeRideAmber
                    else -> SafeRideGrayLight
                  }
                ),
              contentAlignment = Alignment.Center
            ) {
              if (isDone) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = null,
                  tint = Color.White,
                  modifier = Modifier.size(16.dp)
                )
              } else {
                Text(
                  text = "${index + 1}",
                  fontSize = 12.sp,
                  fontWeight = FontWeight.Bold,
                  color = if (isCurrent) SafeRideNavy else SafeRideGray
                )
              }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
              text = title,
              fontSize = 10.sp,
              lineHeight = 12.sp,
              textAlign = TextAlign.Center,
              fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
              color = if (isCurrent) SafeRideAmberDark else if (isDone) SafeRideGreenDark else SafeRideGray
            )
          }

          if (index < stages.size - 1) {
            Box(
              modifier = Modifier
                .weight(0.4f)
                .padding(top = 15.dp)
                .height(3.dp)
                .background(
                  if (index < currentStageIndex) SafeRideGreen else SafeRideGrayLight
                )
            )
          }
        }
      }
    }
  }
}
