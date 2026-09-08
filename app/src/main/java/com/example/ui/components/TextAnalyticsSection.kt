package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.TextAnalyticsSummary
import com.example.data.repository.TopicAdherenceRow
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import java.util.Locale

@Composable
fun TextAnalyticsSection(
    summary: TextAnalyticsSummary?,
    onRefresh: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header with Refresh
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Text-Based Study Analytics",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = "Tabular summaries & adherence rates (Zero graphical clutter)",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8)
                )
            }

            IconButton(
                onClick = onRefresh,
                modifier = Modifier.testTag("refresh_analytics_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh Analytics",
                    tint = ElectricCyan
                )
            }
        }

        if (summary == null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Calculating text metrics from database...",
                    color = Color(0xFF94A3B8),
                    fontFamily = FontFamily.Monospace
                )
            }
            return
        }

        // Key Text Metrics Grid (Purely Text-Based Cards)
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextMetricCard(
                    title = "OVERALL ADHERENCE",
                    value = String.format(Locale.getDefault(), "%.1f%%", summary.adherencePercentage),
                    subText = "${summary.completedBlocksCount} of ${summary.totalBlocksCount} blocks completed",
                    highlightColor = if (summary.adherencePercentage >= 75f) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.weight(1f)
                )

                TextMetricCard(
                    title = "ACTIVE STREAK",
                    value = "${summary.currentStreakDays} Days",
                    subText = "Consistent daily 5h logs",
                    highlightColor = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                TextMetricCard(
                    title = "HOURS LOGGED",
                    value = String.format(Locale.getDefault(), "%.1fh", summary.totalHoursStudied),
                    subText = "Target: ${String.format(Locale.getDefault(), "%.1fh", summary.plannedHoursTarget)}",
                    highlightColor = Color(0xFF38BDF8),
                    modifier = Modifier.weight(1f)
                )

                TextMetricCard(
                    title = "OSWAAL ACCURACY",
                    value = String.format(Locale.getDefault(), "%.1f%%", summary.oswaalAverageAccuracy),
                    subText = "${summary.struggledTopicsCount} chapters flagged for review",
                    highlightColor = if (summary.oswaalAverageAccuracy >= 75f) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Tabular Data Section (Formatted Monospace Data Table)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("text_analytics_table_card"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Topic Completion & Adherence Data Table",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                // Horizontal scrollable table to ensure clean scannability on all screen widths
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState())
                ) {
                    Column(
                        modifier = Modifier.width(620.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Table Header Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(6.dp))
                                .background(Slate800)
                                .padding(horizontal = 12.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "SUBJECT / DOMAIN",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(240.dp)
                            )
                            Text(
                                text = "PHASE",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(80.dp)
                            )
                            Text(
                                text = "LOGGED / TARGET",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(130.dp)
                            )
                            Text(
                                text = "STATUS",
                                color = Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.width(140.dp)
                            )
                        }

                        // Data Rows
                        summary.topicCompletionTable.forEach { row ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(6.dp))
                                    .background(Slate950.copy(alpha = 0.5f))
                                    .border(1.dp, Slate850, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = row.subject,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.width(240.dp)
                                )
                                Text(
                                    text = row.phase,
                                    color = Color(0xFFCBD5E1),
                                    fontSize = 11.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(80.dp)
                                )
                                Text(
                                    text = "${String.format(Locale.getDefault(), "%.1f", row.hoursLogged)} / ${String.format(Locale.getDefault(), "%.1f", row.targetHours)}h",
                                    color = ElectricCyan,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    modifier = Modifier.width(130.dp)
                                )
                                Box(
                                    modifier = Modifier
                                        .width(140.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(
                                            when {
                                                row.statusText.contains("MANDATORY") -> AmberWarning.copy(alpha = 0.2f)
                                                row.statusText.contains("ON TRACK") -> EmeraldSuccess.copy(alpha = 0.2f)
                                                else -> Slate800
                                            }
                                        )
                                        .padding(horizontal = 6.dp, vertical = 3.dp)
                                ) {
                                    Text(
                                        text = row.statusText,
                                        color = when {
                                            row.statusText.contains("MANDATORY") -> AmberWarning
                                            row.statusText.contains("ON TRACK") -> EmeraldSuccess
                                            else -> Color(0xFFCBD5E1)
                                        },
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold,
                                        fontFamily = FontFamily.Monospace
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Summary Text Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Weekly Study Audit Notes",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Text(
                    text = "• Phase 1 Strategy: Keep daily focus restricted to GATE CS & DA overlap topics. Defer COA & Digital Logic until Phase 3 crash window.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )

                Text(
                    text = "• Daily Invariant Rule: Mandatory 1.0 - 1.5h dedicated Aptitude and Basic Maths logged every single day without exception.",
                    fontSize = 12.sp,
                    color = AmberWarning,
                    lineHeight = 18.sp
                )

                Text(
                    text = "• Active Recall Repository: ${summary.activeRecallNotesCount} formulas and core theorems saved in local memory.",
                    fontSize = 12.sp,
                    color = Color(0xFF94A3B8),
                    lineHeight = 18.sp
                )
            }
        }
    }
}

@Composable
private fun TextMetricCard(
    title: String,
    value: String,
    subText: String,
    highlightColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Slate900),
        border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF94A3B8),
                fontFamily = FontFamily.Monospace
            )
            Text(
                text = value,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = highlightColor
            )
            Text(
                text = subText,
                fontSize = 11.sp,
                color = Color(0xFF64748B)
            )
        }
    }
}
