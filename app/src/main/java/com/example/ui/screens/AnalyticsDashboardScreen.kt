package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.LightSlate
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.StudyViewModel
import java.util.Locale

@Composable
fun AnalyticsDashboardScreen(viewModel: StudyViewModel) {
    val contextState by viewModel.studyContext.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp)
            .testTag("analytics_dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Performance Metrics",
                color = TextHighContrast,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Pure text-based analytics and adherence audit (Zero charts)",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        contextState?.let { context ->
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    MetricBox(
                        title = "Current Phase",
                        value = "Phase ${context.currentPhase}",
                        modifier = Modifier.weight(1f)
                    )
                    MetricBox(
                        title = "Daily Streak",
                        value = "${context.currentStreak} Days",
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                MetricBox(
                    title = "Schedule Adherence",
                    value = "${String.format(Locale.US, "%.1f", context.adherenceRatePercentage)}%",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Syllabus Audit (Text Table)",
                    color = TextHighContrast,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
            }

            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateGray),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("COMPLETED TOPICS", color = SuccessGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = if (context.completedTopics.isEmpty()) "None yet." else context.completedTopics.joinToString(", "),
                            color = TextHighContrast,
                            modifier = Modifier.padding(top = 6.dp, bottom = 16.dp),
                            lineHeight = 20.sp
                        )

                        HorizontalDivider(color = LightSlate)
                        Spacer(modifier = Modifier.height(16.dp))

                        Text("PENDING CORE TOPICS", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(
                            text = if (context.pendingTopics.isEmpty()) "All caught up." else context.pendingTopics.joinToString(", "),
                            color = TextHighContrast,
                            modifier = Modifier.padding(top = 6.dp),
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            if (context.struggledTopics.isNotEmpty()) {
                item {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateGray),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("AI TUTOR MEMORY: STRUGGLED TOPICS LOG", color = AccentBlue, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            context.struggledTopics.forEach { struggle ->
                                Text(
                                    text = "• $struggle",
                                    color = TextHighContrast,
                                    fontSize = 13.sp,
                                    modifier = Modifier.padding(top = 6.dp),
                                    lineHeight = 18.sp
                                )
                            }
                        }
                    }
                }
            }
        } ?: item {
            CircularProgressIndicator(color = AccentBlue)
        }
    }
}

@Composable
fun MetricBox(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SlateGray),
        shape = RoundedCornerShape(8.dp),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = title, color = TextMuted, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = value, color = TextHighContrast, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
    }
}
