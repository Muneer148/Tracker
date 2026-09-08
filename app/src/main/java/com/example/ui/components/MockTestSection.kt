package com.example.ui.components

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
import androidx.compose.material.icons.filled.Assignment
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.local.MockTestLogEntity
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BrightPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.RoseDanger
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.SnowflakeBlue
import com.example.ui.theme.TcsOrange

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MockTestSection(
    mockTests: List<MockTestLogEntity>,
    selectedCategory: String,
    onCategoryFilterSelected: (String) -> Unit,
    onAddMockTest: (testName: String, category: String, score: Float, total: Float, mistakes: String) -> Unit,
    onDeleteMockTest: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val categories = listOf("All", "GATE CS", "GATE DA", "TCS NQT", "Snowflake")

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Slate700), RoundedCornerShape(16.dp))
            .testTag("mock_test_section"),
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
                            .background(NeonIndigo.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Assignment,
                            contentDescription = null,
                            tint = NeonIndigo,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Self-Made PYQ Mock Tests",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                        )
                        Text(
                            text = "Free Online Past-Year Papers Score Log",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                        )
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonIndigo,
                        contentColor = Slate50
                    ),
                    modifier = Modifier.testTag("add_mock_test_btn")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Log PYQ Test", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                categories.forEach { cat ->
                    val isSelected = selectedCategory == cat
                    val badgeColor = when (cat) {
                        "GATE CS" -> ElectricCyan
                        "GATE DA" -> BrightPurple
                        "TCS NQT" -> TcsOrange
                        "Snowflake" -> SnowflakeBlue
                        else -> NeonIndigo
                    }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategoryFilterSelected(cat) },
                        label = {
                            Text(
                                text = cat,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Slate800,
                            selectedContainerColor = badgeColor.copy(alpha = 0.25f),
                            labelColor = Slate300,
                            selectedLabelColor = badgeColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Slate700,
                            selectedBorderColor = badgeColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            if (mockTests.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate850, RoundedCornerShape(10.dp))
                        .padding(20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No tests logged under '$selectedCategory'. Tap '+ Log PYQ Test' to add a score.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    mockTests.forEach { test ->
                        MockTestItem(test = test, onDelete = { onDeleteMockTest(test.id) })
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddMockTestDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, cat, score, total, mistakes ->
                onAddMockTest(name, cat, score, total, mistakes)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun MockTestItem(
    test: MockTestLogEntity,
    onDelete: () -> Unit
) {
    val categoryColor = when (test.paperCategory) {
        "GATE CS" -> ElectricCyan
        "GATE DA" -> BrightPurple
        "TCS NQT" -> TcsOrange
        "Snowflake" -> SnowflakeBlue
        else -> NeonIndigo
    }

    val pct = test.percentage
    val isGood = pct >= 65f

    Surface(
        color = Slate850,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(1.dp, Slate700),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = categoryColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, categoryColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = test.paperCategory,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = categoryColor,
                                fontSize = 10.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = test.dateTaken,
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400, fontSize = 10.sp)
                    )
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.Delete,
                        contentDescription = "Delete Mock",
                        tint = Slate400,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = test.testName,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Slate50
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Score display & progress bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Score: ${test.scoreObtained} / ${test.totalMarks}",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Slate300,
                        fontFamily = FontFamily.Monospace
                    )
                )

                Text(
                    text = String.format("%.1f%%", pct),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = if (isGood) EmeraldSuccess else RoseDanger,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Monospace
                    )
                )
            }

            Spacer(modifier = Modifier.height(6.dp))

            LinearProgressIndicator(
                progress = { (pct / 100f).coerceIn(0f, 1f) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (isGood) EmeraldSuccess else RoseDanger,
                trackColor = Slate800
            )

            if (test.keyMistakes.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate800.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = "Mistakes / Takeaways: ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AmberWarning,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = test.keyMistakes,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Slate300,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddMockTestDialog(
    onDismiss: () -> Unit,
    onConfirm: (testName: String, category: String, score: Float, total: Float, mistakes: String) -> Unit
) {
    var testName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("GATE CS") }
    var scoreInput by remember { mutableStateOf("65") }
    var totalInput by remember { mutableStateOf("100") }
    var mistakesInput by remember { mutableStateOf("") }

    val presetTests = when (selectedCategory) {
        "GATE CS" -> listOf("GATE CS 2024 Set 1", "GATE CS 2023 Paper", "GATE CS 2022 Full Mock", "DSA & Algo Sectional Test")
        "GATE DA" -> listOf("GATE DA 2024 Official Paper", "GATE DA AI & ML Mock 1", "Math & Stats Mock DA", "Python & SQL Sectional")
        "TCS NQT" -> listOf("TCS NQT Foundation Mock", "TCS NQT Advanced Coding", "TCS Reasoning Diagnostic", "TCS Verbal Assessment")
        "Snowflake" -> listOf("Snowflake SnowPro Core PYQ 1", "SnowPro SQL & Stages Mock", "Snowflake Architecture Exam", "Micro-partitions Drill")
        else -> emptyList()
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = "Log Free Past-Year Mock Test",
                style = MaterialTheme.typography.titleLarge.copy(color = Slate50, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select Paper Category:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    listOf("GATE CS", "GATE DA", "TCS NQT", "Snowflake").forEach { cat ->
                        Surface(
                            color = if (selectedCategory == cat) NeonIndigo.copy(alpha = 0.25f) else Slate800,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, if (selectedCategory == cat) NeonIndigo else Slate700),
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { selectedCategory = cat }
                        ) {
                            Text(
                                text = cat,
                                modifier = Modifier.padding(vertical = 6.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    color = if (selectedCategory == cat) Slate50 else Slate400
                                ),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text("Quick Presets:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    presetTests.forEach { preset ->
                        Surface(
                            color = if (testName == preset) NeonIndigo.copy(alpha = 0.2f) else Slate800,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, if (testName == preset) NeonIndigo else Slate700),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { testName = preset }
                        ) {
                            Text(
                                text = preset,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 10.sp,
                                    color = if (testName == preset) Slate50 else Slate300
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = testName,
                    onValueChange = { testName = it },
                    label = { Text("Mock Test Name / Year", color = Slate400) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonIndigo,
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
                        value = scoreInput,
                        onValueChange = { scoreInput = it },
                        label = { Text("Score Obtained", color = Slate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonIndigo,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate100
                        )
                    )
                    OutlinedTextField(
                        value = totalInput,
                        onValueChange = { totalInput = it },
                        label = { Text("Total Marks", color = Slate400) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = NeonIndigo,
                            unfocusedBorderColor = Slate700,
                            focusedTextColor = Slate50,
                            unfocusedTextColor = Slate100
                        )
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = mistakesInput,
                    onValueChange = { mistakesInput = it },
                    label = { Text("Key Mistakes & Improvement Plan", color = Slate400) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = NeonIndigo,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate100
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (testName.isNotBlank()) {
                        val sc = scoreInput.toFloatOrNull() ?: 0f
                        val tot = totalInput.toFloatOrNull() ?: 100f
                        onConfirm(testName.trim(), selectedCategory, sc, tot, mistakesInput.trim())
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonIndigo,
                    contentColor = Slate50
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Test", fontWeight = FontWeight.Bold)
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
