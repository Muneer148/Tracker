package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.outlined.Circle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.StudyBlockEntity
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.BrightPurple
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate300
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate50
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.theme.SnowflakeBlue
import com.example.ui.theme.TcsOrange
import com.example.ui.viewmodel.ActiveTimerState
import java.util.Locale

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StudyBlockCard(
    block: StudyBlockEntity,
    activeTimer: ActiveTimerState,
    onToggleComplete: () -> Unit,
    onStartTimer: () -> Unit,
    onPauseTimer: () -> Unit,
    onResetTimer: () -> Unit,
    onAddQuickTime: (Long) -> Unit,
    onUpdateTopic: (topic: String, subCategory: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    var isEditingCustomTopic by remember { mutableStateOf(false) }
    var customTopicInput by remember(block.selectedTopic) { mutableStateOf(block.selectedTopic) }

    val isThisTimerActive = activeTimer.blockIndex == block.blockIndex && activeTimer.isRunning
    val isTimerSelected = activeTimer.blockIndex == block.blockIndex

    // Remaining seconds calculation
    val currentRemaining = if (isTimerSelected) {
        activeTimer.remainingSeconds
    } else {
        (block.targetSeconds - block.timeSpentSeconds).coerceAtLeast(0L)
    }

    val progressFraction = if (block.targetSeconds > 0) {
        if (block.isCompleted) 1.0f
        else ((block.targetSeconds - currentRemaining).toFloat() / block.targetSeconds.toFloat()).coerceIn(0f, 1f)
    } else 0f

    // Accent colors based on block index
    val (blockAccent, badgeColor) = when (block.blockIndex) {
        1 -> Pair(ElectricCyan, ElectricCyan)
        2 -> Pair(
            if (block.subCategory.contains("ML", ignoreCase = true)) BrightPurple else NeonIndigo,
            if (block.subCategory.contains("ML", ignoreCase = true)) BrightPurple else NeonIndigo
        )
        3 -> Pair(
            if (block.subCategory.contains("Snowflake", ignoreCase = true)) SnowflakeBlue else TcsOrange,
            if (block.subCategory.contains("Snowflake", ignoreCase = true)) SnowflakeBlue else TcsOrange
        )
        else -> Pair(ElectricCyan, ElectricCyan)
    }

    val containerBg by animateColorAsState(
        targetValue = if (block.isCompleted) Slate900.copy(alpha = 0.9f)
        else if (isThisTimerActive) Slate850
        else Slate900,
        animationSpec = tween(300),
        label = "containerBg"
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                BorderStroke(
                    width = if (isThisTimerActive) 1.5.dp else 1.dp,
                    color = if (isThisTimerActive) blockAccent else if (block.isCompleted) EmeraldSuccess.copy(alpha = 0.4f) else Slate700
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .animateContentSize()
            .testTag("study_block_card_${block.blockIndex}"),
        colors = CardDefaults.cardColors(containerColor = containerBg),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isThisTimerActive) 6.dp else 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Badge, Duration, Checkbox
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = badgeColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, badgeColor.copy(alpha = 0.5f))
                    ) {
                        Text(
                            text = "BLOCK ${block.blockIndex} • ${formatHoursLabel(block.targetSeconds)}",
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelMedium.copy(
                                color = badgeColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }

                    if (isThisTimerActive) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = EmeraldSuccess.copy(alpha = 0.2f),
                            shape = CircleShape
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(6.dp)
                                        .background(EmeraldSuccess, CircleShape)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "LIVE",
                                    color = EmeraldSuccess,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }

                // Completion Toggle Checkbox
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onToggleComplete() }
                        .padding(4.dp)
                        .testTag("block_${block.blockIndex}_complete_toggle")
                ) {
                    Icon(
                        imageVector = if (block.isCompleted) Icons.Filled.CheckCircle else Icons.Outlined.Circle,
                        contentDescription = if (block.isCompleted) "Mark Incomplete" else "Mark Complete",
                        tint = if (block.isCompleted) EmeraldSuccess else Slate400,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (block.isCompleted) "Done" else "Pending",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = if (block.isCompleted) EmeraldSuccess else Slate400,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Block Title & Description
            val defaultCategoryTitle = when (block.blockIndex) {
                1 -> "GATE Core Overlap (Math, DSA, DBMS)"
                2 -> "Paper Specific Rotation (CS Systems / DA ML)"
                3 -> "Placements (TCS Aptitude / Snowflake SQL)"
                else -> "Study Block"
            }

            Text(
                text = defaultCategoryTitle,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate50
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Current Active Topic
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Slate800.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "TODAY'S FOCUS TOPIC:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = Slate400,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = if (block.selectedTopic.isNotBlank()) block.selectedTopic else "Select or enter topic below",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (block.selectedTopic.isNotBlank()) Slate100 else Slate400,
                            fontWeight = FontWeight.Medium
                        ),
                        maxLines = 1
                    )
                }
                IconButton(
                    onClick = { expanded = !expanded },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                        contentDescription = "Expand Options",
                        tint = Slate300
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Timer & Progress Display
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = formatTimeRemaining(currentRemaining),
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            color = if (block.isCompleted) EmeraldSuccess else if (isThisTimerActive) ElectricCyan else Slate100,
                            fontSize = 24.sp
                        )
                    )
                    Text(
                        text = if (block.isCompleted) "Interval Goal Completed (100%)"
                        else "${formatTimeRemaining(block.targetSeconds - currentRemaining)} logged / ${formatTimeRemaining(block.targetSeconds)}",
                        style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                    )
                }

                // Play / Pause / Reset Buttons
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    if (isThisTimerActive) {
                        Button(
                            onClick = onPauseTimer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AmberWarning,
                                contentColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("block_${block.blockIndex}_pause_btn")
                        ) {
                            Icon(Icons.Filled.Pause, contentDescription = "Pause", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Pause", fontWeight = FontWeight.Bold)
                        }
                    } else {
                        Button(
                            onClick = onStartTimer,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = blockAccent,
                                contentColor = Slate950
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("block_${block.blockIndex}_start_btn")
                        ) {
                            Icon(Icons.Filled.PlayArrow, contentDescription = "Start", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(if (currentRemaining < block.targetSeconds && currentRemaining > 0) "Resume" else "Start", fontWeight = FontWeight.Bold)
                        }
                    }

                    IconButton(
                        onClick = onResetTimer,
                        modifier = Modifier
                            .size(36.dp)
                            .background(Slate800, RoundedCornerShape(8.dp))
                            .testTag("block_${block.blockIndex}_reset_btn")
                    ) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Reset Timer", tint = Slate300, modifier = Modifier.size(18.dp))
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Progress Bar
            LinearProgressIndicator(
                progress = { progressFraction },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (block.isCompleted) EmeraldSuccess else blockAccent,
                trackColor = Slate800,
            )

            // Quick Add Interval Buttons
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    QuickAddChip(label = "+15m") { onAddQuickTime(15 * 60L) }
                    QuickAddChip(label = "+30m") { onAddQuickTime(30 * 60L) }
                    QuickAddChip(label = "+1h") { onAddQuickTime(60 * 60L) }
                }

                Text(
                    text = "${(progressFraction * 100).toInt()}% Done",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (block.isCompleted) EmeraldSuccess else Slate300,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                )
            }

            // Expandable Topic Selector Section
            AnimatedVisibility(
                visible = expanded,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically()
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp)
                        .background(Slate850, RoundedCornerShape(12.dp))
                        .padding(12.dp)
                ) {
                    Text(
                        text = "MARK WHAT YOU STUDIED TODAY:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = blockAccent,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Block-specific preset topic pills
                    when (block.blockIndex) {
                        1 -> {
                            // Block 1: GATE Core Overlap (Math, DSA, DBMS)
                            val coreTopics = listOf(
                                "Discrete Mathematics (Graphs/Logic)",
                                "Linear Algebra (Eigenvalues/Rank)",
                                "Calculus & Probability Density",
                                "DSA: Trees, Heaps & DP",
                                "DSA: Graph Traversal & Dijkstra",
                                "DBMS: SQL Queries & Joins",
                                "DBMS: Normalization (3NF/BCNF)",
                                "DBMS: Transactions & ACID"
                            )
                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                coreTopics.forEach { topic ->
                                    TopicChip(
                                        title = topic,
                                        isSelected = block.selectedTopic == topic,
                                        accentColor = ElectricCyan,
                                        onClick = {
                                            onUpdateTopic(topic, "GATE Core Overlap")
                                        }
                                    )
                                }
                            }
                        }
                        2 -> {
                            // Block 2: Rotation toggle - CS Systems vs DA Machine Learning
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val isDA = block.subCategory.contains("ML", ignoreCase = true)
                                RotationSwitchPill(
                                    title = "DA: Machine Learning",
                                    isSelected = isDA,
                                    activeColor = BrightPurple,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onUpdateTopic("DA: Supervised ML & Gradient Descent", "DA Machine Learning")
                                }
                                RotationSwitchPill(
                                    title = "CS: Core Systems",
                                    isSelected = !isDA,
                                    activeColor = NeonIndigo,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onUpdateTopic("CS: Operating Systems (Virtual Memory)", "CS Systems")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val currentOptions = if (block.subCategory.contains("ML", ignoreCase = true)) {
                                listOf(
                                    "Linear & Logistic Regression",
                                    "Neural Networks & Backprop",
                                    "PCA & Dimensionality Reduction",
                                    "Decision Trees & Random Forests",
                                    "K-Means & Hierarchical Clustering",
                                    "Python Data Science & Pandas"
                                )
                            } else {
                                listOf(
                                    "OS: Process Sync & Semaphores",
                                    "OS: Memory Management & Paging",
                                    "Networks: IP Addressing & Subnets",
                                    "Networks: TCP Flow & Congestion",
                                    "Compiler: Lexical & LL/LR Parsing",
                                    "COA: Pipelining & Cache Mapping"
                                )
                            }

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                currentOptions.forEach { topic ->
                                    TopicChip(
                                        title = topic,
                                        isSelected = block.selectedTopic == topic,
                                        accentColor = if (block.subCategory.contains("ML", ignoreCase = true)) BrightPurple else NeonIndigo,
                                        onClick = {
                                            onUpdateTopic(topic, block.subCategory)
                                        }
                                    )
                                }
                            }
                        }
                        3 -> {
                            // Block 3: Placements - TCS Aptitude vs Snowflake SQL
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                val isSnowflake = block.subCategory.contains("Snowflake", ignoreCase = true)
                                RotationSwitchPill(
                                    title = "Snowflake Certification",
                                    isSelected = isSnowflake,
                                    activeColor = SnowflakeBlue,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onUpdateTopic("Snowflake: Architecture & Virtual Warehouses", "Snowflake SQL")
                                }
                                RotationSwitchPill(
                                    title = "TCS NQT Placement",
                                    isSelected = !isSnowflake,
                                    activeColor = TcsOrange,
                                    modifier = Modifier.weight(1f)
                                ) {
                                    onUpdateTopic("TCS NQT: Numerical & Quantitative Aptitude", "TCS Aptitude")
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            val placementTopics = if (block.subCategory.contains("Snowflake", ignoreCase = true)) {
                                listOf(
                                    "Snowflake 3-Layer Architecture",
                                    "Virtual Warehouses Scaling & Credits",
                                    "Micro-partitions & Pruning",
                                    "Time Travel (0-90 days) & Undrop",
                                    "Zero-Copy Cloning & Shares",
                                    "SnowSQL & COPY INTO Stages",
                                    "Role-Based Access Control (RBAC)"
                                )
                            } else {
                                listOf(
                                    "TCS: Quantitative Numerical Ability",
                                    "TCS: Reasoning & Data Interpretation",
                                    "TCS: Verbal & Sentence Completion",
                                    "TCS: Advanced Hands-on Coding (Python/C++)",
                                    "TCS: Array Manipulation & Strings"
                                )
                            }

                            FlowRow(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                placementTopics.forEach { topic ->
                                    TopicChip(
                                        title = topic,
                                        isSelected = block.selectedTopic == topic,
                                        accentColor = if (block.subCategory.contains("Snowflake", ignoreCase = true)) SnowflakeBlue else TcsOrange,
                                        onClick = {
                                            onUpdateTopic(topic, block.subCategory)
                                        }
                                    )
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom Topic Option
                    if (!isEditingCustomTopic) {
                        OutlinedButton(
                            onClick = { isEditingCustomTopic = true },
                            modifier = Modifier.fillMaxWidth(),
                            border = BorderStroke(1.dp, Slate700),
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Slate300)
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Type Custom Topic", fontSize = 12.sp)
                        }
                    } else {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            OutlinedTextField(
                                value = customTopicInput,
                                onValueChange = { customTopicInput = it },
                                modifier = Modifier.weight(1f),
                                placeholder = { Text("e.g. Heapsort recurrence", color = Slate400, fontSize = 12.sp) },
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = blockAccent,
                                    unfocusedBorderColor = Slate700,
                                    focusedTextColor = Slate50,
                                    unfocusedTextColor = Slate100
                                ),
                                textStyle = MaterialTheme.typography.bodyMedium
                            )
                            Button(
                                onClick = {
                                    if (customTopicInput.isNotBlank()) {
                                        onUpdateTopic(customTopicInput.trim(), block.subCategory)
                                    }
                                    isEditingCustomTopic = false
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = blockAccent, contentColor = Slate950),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Save", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun QuickAddChip(
    label: String,
    onClick: () -> Unit
) {
    Surface(
        color = Slate800,
        shape = RoundedCornerShape(6.dp),
        border = BorderStroke(1.dp, Slate700),
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .clickable { onClick() }
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall.copy(
                fontFamily = FontFamily.Monospace,
                color = Slate300,
                fontSize = 11.sp
            )
        )
    }
}

@Composable
private fun TopicChip(
    title: String,
    isSelected: Boolean,
    accentColor: Color,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) accentColor.copy(alpha = 0.2f) else Slate800,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            1.dp,
            if (isSelected) accentColor else Slate700
        ),
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isSelected) {
                Icon(
                    imageVector = Icons.Filled.Check,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(12.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
            }
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Slate50 else Slate300,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    fontSize = 11.sp
                )
            )
        }
    }
}

@Composable
private fun RotationSwitchPill(
    title: String,
    isSelected: Boolean,
    activeColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Surface(
        color = if (isSelected) activeColor.copy(alpha = 0.2f) else Slate800,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) activeColor else Slate700
        ),
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
    ) {
        Box(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) activeColor else Slate400,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 11.sp
                )
            )
        }
    }
}

private fun formatHoursLabel(seconds: Long): String {
    val hours = seconds.toDouble() / 3600.0
    return if (hours % 1.0 == 0.0) "${hours.toInt()}.0 Hours" else String.format(Locale.getDefault(), "%.1f Hours", hours)
}

private fun formatTimeRemaining(seconds: Long): String {
    val hrs = seconds / 3600
    val mins = (seconds % 3600) / 60
    val secs = seconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hrs, mins, secs)
}
