package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.OswaalLogEntity
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun OswaalPracticeSection(
    oswaalLogs: List<OswaalLogEntity>,
    isFilterReviewOnly: Boolean,
    onToggleFilterReviewOnly: () -> Unit,
    onAddLog: (chapter: String, subject: String, questions: Int, accuracy: Float, reviewNeeded: Boolean, notes: String) -> Unit,
    onToggleReview: (OswaalLogEntity) -> Unit,
    onDeleteLog: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val totalAttempted = oswaalLogs.sumOf { it.questionsAttempted }
    val averageAccuracy = if (oswaalLogs.isNotEmpty()) {
        oswaalLogs.map { it.accuracyRate }.average().toFloat()
    } else 0f
    val reviewCount = oswaalLogs.count { it.reviewNeeded }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Slate700), RoundedCornerShape(16.dp))
            .testTag("oswaal_practice_section"),
        colors = CardDefaults.cardColors(containerColor = Slate900)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(ElectricCyan.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.AutoStories,
                            contentDescription = null,
                            tint = ElectricCyan,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Oswaal GATE Book Tracker",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                        )
                        Text(
                            text = "Mathematics & Aptitude Practice Logs",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                        )
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ElectricCyan,
                        contentColor = Color(0xFF080C16)
                    ),
                    modifier = Modifier.testTag("add_oswaal_log_btn")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log Chapter", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Stat Summary Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StatBadge(
                    title = "Total Qs",
                    value = "$totalAttempted",
                    color = ElectricCyan,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    title = "Avg Accuracy",
                    value = String.format("%.1f%%", averageAccuracy),
                    color = if (averageAccuracy >= 75f) EmeraldSuccess else AmberWarning,
                    modifier = Modifier.weight(1f)
                )
                StatBadge(
                    title = "Review Needed",
                    value = "$reviewCount",
                    color = if (reviewCount > 0) RoseDanger else Slate400,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Filter Chip Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "CHAPTER LOGS (${oswaalLogs.size})",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Slate400,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                )

                FilterChip(
                    selected = isFilterReviewOnly,
                    onClick = onToggleFilterReviewOnly,
                    label = {
                        Text(
                            text = if (isFilterReviewOnly) "Showing: Review Needed" else "Filter: Review Needed",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = if (isFilterReviewOnly) Icons.Filled.Warning else Icons.Filled.FilterList,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = if (isFilterReviewOnly) AmberWarning else Slate300
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Slate800,
                        selectedContainerColor = AmberWarning.copy(alpha = 0.2f),
                        labelColor = Slate300,
                        selectedLabelColor = AmberWarning
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        enabled = true,
                        selected = isFilterReviewOnly,
                        borderColor = Slate700,
                        selectedBorderColor = AmberWarning
                    ),
                    modifier = Modifier.testTag("oswaal_review_filter_chip")
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (oswaalLogs.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate850, RoundedCornerShape(10.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (isFilterReviewOnly) "No chapters flagged for review. Great work!"
                        else "No Oswaal logs recorded yet. Tap '+ Log Chapter' to add one.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    oswaalLogs.forEach { log ->
                        OswaalLogItem(
                            log = log,
                            onToggleReview = { onToggleReview(log) },
                            onDelete = { onDeleteLog(log.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddOswaalDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { ch, subj, qCount, acc, rev, notes ->
                onAddLog(ch, subj, qCount, acc, rev, notes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun StatBadge(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Slate700),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = title, style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp))
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontFamily = FontFamily.Monospace
                )
            )
        }
    }
}

@Composable
private fun OswaalLogItem(
    log: OswaalLogEntity,
    onToggleReview: () -> Unit,
    onDelete: () -> Unit
) {
    Surface(
        color = Slate850,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            1.dp,
            if (log.reviewNeeded) AmberWarning.copy(alpha = 0.6f) else Slate700
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = if (log.subject.contains("Math", ignoreCase = true)) ElectricCyan.copy(alpha = 0.15f)
                            else AmberWarning.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Text(
                                text = log.subject.uppercase(),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (log.subject.contains("Math", ignoreCase = true)) ElectricCyan else AmberWarning
                                )
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = log.dateLogged,
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = log.chapterName,
                        style = MaterialTheme.typography.bodyLarge.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Slate50
                        )
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleReview,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = if (log.reviewNeeded) Icons.Filled.Warning else Icons.Outlined.CheckCircle,
                            contentDescription = "Toggle Review",
                            tint = if (log.reviewNeeded) AmberWarning else EmeraldSuccess,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Log",
                            tint = Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Metrics row: Attempted Qs & Accuracy Rate
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "${log.questionsAttempted} Questions Attempted",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Slate300,
                            fontFamily = FontFamily.SansSerif
                        )
                    )
                    Text(
                        text = "Accuracy: ${String.format("%.1f%%", log.accuracyRate)}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = if (log.accuracyRate >= 80f) EmeraldSuccess
                            else if (log.accuracyRate >= 60f) AmberWarning
                            else RoseDanger,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace
                        )
                    )
                }

                if (log.reviewNeeded) {
                    Surface(
                        color = AmberWarning.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "REVIEW NEEDED",
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberWarning,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }
            }

            if (log.notes.isNotBlank()) {
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = log.notes,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate400,
                        fontSize = 12.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddOswaalDialog(
    onDismiss: () -> Unit,
    onConfirm: (chapter: String, subject: String, questions: Int, accuracy: Float, reviewNeeded: Boolean, notes: String) -> Unit
) {
    var subject by remember { mutableStateOf("Engineering Mathematics") }
    var chapterName by remember { mutableStateOf("") }
    var questionsInput by remember { mutableStateOf("30") }
    var accuracyInput by remember { mutableStateOf("75") }
    var reviewNeeded by remember { mutableStateOf(false) }
    var notesInput by remember { mutableStateOf("") }

    val presetChapters = if (subject == "Engineering Mathematics") {
        listOf(
            "Linear Algebra (Matrices/Eigen)",
            "Calculus (Limits & Maxima)",
            "Probability & Bayes Theorem",
            "Random Variables & Distributions",
            "Discrete Math: Graph Theory",
            "Combinatorics & Generating Functions"
        )
    } else {
        listOf(
            "Quantitative: Ratio, Time & Work",
            "Quantitative: Permutations & Prob",
            "Spatial Aptitude & Paper Folding",
            "Verbal: Sentence Completion & Cloze",
            "Logical: Syllogisms & Seating"
        )
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = "Log Oswaal Book Progress",
                style = MaterialTheme.typography.titleLarge.copy(color = Slate50, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                // Subject toggle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = subject == "Engineering Mathematics",
                        onClick = { subject = "Engineering Mathematics" },
                        label = { Text("Engineering Math", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = ElectricCyan.copy(alpha = 0.25f),
                            selectedLabelColor = ElectricCyan,
                            containerColor = Slate800,
                            labelColor = Slate400
                        )
                    )
                    FilterChip(
                        selected = subject == "General Aptitude",
                        onClick = { subject = "General Aptitude" },
                        label = { Text("General Aptitude", fontSize = 11.sp) },
                        modifier = Modifier.weight(1f),
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AmberWarning.copy(alpha = 0.25f),
                            selectedLabelColor = AmberWarning,
                            containerColor = Slate800,
                            labelColor = Slate400
                        )
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Quick Select Oswaal Chapter:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presetChapters.forEach { preset ->
                        Surface(
                            color = if (chapterName == preset) ElectricCyan.copy(alpha = 0.2f) else Slate800,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, if (chapterName == preset) ElectricCyan else Slate700),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { chapterName = preset }
                        ) {
                            Text(
                                text = preset,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = if (chapterName == preset) Slate50 else Slate300
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = chapterName,
                    onValueChange = { chapterName = it },
                    label = { Text("Chapter Name", color = Slate400) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate100
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = questionsInput,
                        onValueChange = { questionsInput = it },
                        label = { Text("Attempted", color = Slate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate100
                        )
                    )
                    OutlinedTextField(
                        value = accuracyInput,
                        onValueChange = { accuracyInput = it },
                        label = { Text("Accuracy (%)", color = Slate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = ElectricCyan,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate100
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = notesInput,
                    onValueChange = { notesInput = it },
                    label = { Text("Notes / Mistakes (Optional)", color = Slate400) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ElectricCyan,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate100
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { reviewNeeded = !reviewNeeded }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = reviewNeeded,
                        onCheckedChange = { reviewNeeded = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AmberWarning,
                            checkmarkColor = Slate950,
                            uncheckedColor = Slate700
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Mark 'Review Needed' (Re-solve difficult Qs)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (reviewNeeded) AmberWarning else Slate300,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (chapterName.isNotBlank()) {
                        val qCount = questionsInput.toIntOrNull() ?: 0
                        val acc = accuracyInput.toFloatOrNull() ?: 0f
                        onConfirm(chapterName.trim(), subject, qCount, acc, reviewNeeded, notesInput.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = ElectricCyan,
                    contentColor = Color(0xFF080C16)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Log", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                border = BorderStroke(1.dp, Slate700),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300)
            ) {
                Text("Cancel")
            }
        }
    )
}
