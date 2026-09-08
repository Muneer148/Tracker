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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ScheduleEntity
import com.example.ui.theme.AccentBlue
import com.example.ui.theme.DeepBlue
import com.example.ui.theme.LightSlate
import com.example.ui.theme.SlateGray
import com.example.ui.theme.SuccessGreen
import com.example.ui.theme.TextHighContrast
import com.example.ui.theme.TextMuted
import com.example.ui.viewmodel.StudyViewModel

@Composable
fun DailyTrackerScreen(viewModel: StudyViewModel) {
    val dailyBlocks by viewModel.dailyBlocks.collectAsState()

    var noteTitle by remember { mutableStateOf("") }
    var noteContent by remember { mutableStateOf("") }
    var noteTags by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DeepBlue)
            .padding(16.dp)
            .testTag("daily_tracker_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Today's Study Blocks",
                color = TextHighContrast,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Multi-Phase GATE CS/DA + Mandatory TCS NQT Invariant",
                color = TextMuted,
                fontSize = 13.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(dailyBlocks, key = { it.id }) { block ->
            StudyBlockCard(block = block) { isChecked, topic ->
                viewModel.toggleBlockCompletion(block.id, isChecked, topic)
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = LightSlate)
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Active Recall Notes",
                color = TextHighContrast,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                text = "Jot down formulas or logic with tags (#Algorithms, #TCS-NQT, #DataManagement)",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(top = 2.dp)
            )
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateGray),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    OutlinedTextField(
                        value = noteTitle,
                        onValueChange = { noteTitle = it },
                        placeholder = { Text("Note Title (e.g., Dijkstra Min-Heap Optimization)", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextHighContrast,
                            unfocusedTextColor = TextHighContrast,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = LightSlate
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteTags,
                        onValueChange = { noteTags = it },
                        placeholder = { Text("#Algorithms, #CompilerDesign, #DataManagement, #TCS-NQT", color = TextMuted) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextHighContrast,
                            unfocusedTextColor = TextHighContrast,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = LightSlate
                        ),
                        singleLine = true
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = noteContent,
                        onValueChange = { noteContent = it },
                        placeholder = { Text("Markdown supported logic, formulas, time complexity...", color = TextMuted) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = TextHighContrast,
                            unfocusedTextColor = TextHighContrast,
                            focusedBorderColor = AccentBlue,
                            unfocusedBorderColor = LightSlate
                        )
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = {
                            if (noteContent.isNotBlank()) {
                                val finalTitle = if (noteTitle.isNotBlank()) noteTitle else "Quick Note"
                                val finalTags = if (noteTags.isNotBlank()) noteTags else "#General"
                                viewModel.saveActiveRecallNote(finalTitle, noteContent, finalTags)
                                noteTitle = ""
                                noteContent = ""
                                noteTags = ""
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentBlue),
                        modifier = Modifier
                            .align(Alignment.End)
                            .testTag("save_note_button")
                    ) {
                        Text("Save Note", color = DeepBlue, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun StudyBlockCard(block: ScheduleEntity, onToggle: (Boolean, String) -> Unit) {
    var topicStudied by remember(block.topicStudied) { mutableStateOf(block.topicStudied ?: "") }

    Card(
        colors = CardDefaults.cardColors(containerColor = SlateGray),
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = block.blockType, color = AccentBlue, fontWeight = FontWeight.Bold)
                    Text(text = "${block.durationMinutes} Mins Target", color = TextMuted, fontSize = 12.sp)
                }
                Checkbox(
                    checked = block.isCompleted,
                    onCheckedChange = { onToggle(it, topicStudied) },
                    colors = CheckboxDefaults.colors(checkedColor = SuccessGreen),
                    modifier = Modifier.testTag("checkbox_${block.id}")
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = topicStudied,
                onValueChange = {
                    topicStudied = it
                    onToggle(block.isCompleted, it)
                },
                placeholder = { Text("Log specific topic studied...", color = TextMuted) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = TextHighContrast,
                    unfocusedTextColor = TextHighContrast,
                    focusedBorderColor = AccentBlue,
                    unfocusedBorderColor = LightSlate
                )
            )
        }
    }
}
