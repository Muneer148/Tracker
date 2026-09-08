package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ChatMessageEntity
import com.example.data.model.PhaseScheduleInfo
import com.example.ui.theme.ElectricCyan
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.NeonIndigo
import com.example.ui.theme.Slate700
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate850
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950

@Composable
fun AiTutorScreen(
    currentPhase: PhaseScheduleInfo,
    chatMessages: List<ChatMessageEntity>,
    inputText: String,
    isGenerating: Boolean,
    onInputTextChanged: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onClearChat: () -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    var isVoiceActive by remember { mutableStateOf(false) }

    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            listState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    val suggestedQuestions = listOf(
        "What topics did I struggle with last week?",
        "Summarize my active recall notes for #DataManagement",
        "Am I on track with Phase 1?",
        "Explain BCNF lossy vs lossless decomposition",
        "Give me 3 practice MCQs on Master Theorem"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Memory Context Banner Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("ai_memory_status_banner"),
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = Slate900),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(NeonIndigo.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = ElectricCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Column {
                            Text(
                                text = "AI Tutor with Contextual Memory",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = "Live Room Database Memory Synced",
                                color = EmeraldSuccess,
                                fontSize = 11.sp,
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }

                    IconButton(
                        onClick = onClearChat,
                        modifier = Modifier.testTag("clear_chat_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.DeleteOutline,
                            contentDescription = "Clear Chat",
                            tint = Color(0xFF94A3B8)
                        )
                    }
                }

                // Memory Context Chips
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate800)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Phase: ${currentPhase.phaseId.number} (Overlap Core)",
                            color = Color(0xFFCBD5E1),
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(Slate800)
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = "Daily Invariant: 1.0h Aptitude Active",
                            color = ElectricCyan,
                            fontSize = 11.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    }
                }
            }
        }

        // Suggested Queries Horizontal Scroll
        Text(
            text = "Ask AI from your database memory:",
            fontSize = 12.sp,
            fontWeight = FontWeight.Medium,
            color = Color(0xFF94A3B8)
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestedQuestions.forEach { question ->
                FilterChip(
                    selected = false,
                    onClick = { onSendMessage(question) },
                    label = {
                        Text(
                            text = question,
                            fontSize = 12.sp,
                            color = Color(0xFFE2E8F0)
                        )
                    },
                    colors = FilterChipDefaults.filterChipColors(
                        containerColor = Slate900,
                        labelColor = Color.White
                    ),
                    border = FilterChipDefaults.filterChipBorder(
                        borderColor = Slate800,
                        enabled = true,
                        selected = false
                    ),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.testTag("suggested_prompt_${question.take(10)}")
                )
            }
        }

        // Chat Messages Container
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp)
                .testTag("ai_messages_container"),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Slate950),
            border = CardDefaults.outlinedCardBorder().copy(brush = androidx.compose.ui.graphics.SolidColor(Slate800))
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(chatMessages, key = { it.id }) { msg ->
                    val isUser = msg.role == "user"
                    ChatMessageBubble(msg = msg, isUser = isUser)
                }

                if (isGenerating) {
                    item {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(8.dp)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = ElectricCyan,
                                strokeWidth = 2.dp
                            )
                            Text(
                                text = "AI Tutor is querying database memory...",
                                fontSize = 12.sp,
                                color = Color(0xFF94A3B8),
                                fontFamily = FontFamily.Monospace
                            )
                        }
                    }
                }
            }
        }

        // Voice Assistant Simulation Banner (when microphone is toggled)
        if (isVoiceActive) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(10.dp))
                    .background(NeonIndigo.copy(alpha = 0.2f))
                    .border(1.dp, NeonIndigo, RoundedCornerShape(10.dp))
                    .padding(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(ElectricCyan)
                        )
                        Text(
                            text = "Voice Assistant Listening... Say: 'What topics did I struggle with?'",
                            color = Color.White,
                            fontSize = 12.sp
                        )
                    }
                    Text(
                        text = "Tap Mic to Close",
                        color = ElectricCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Bottom Input Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            IconButton(
                onClick = {
                    isVoiceActive = !isVoiceActive
                    if (isVoiceActive) {
                        onInputTextChanged("What topics did I struggle with in my practice tests?")
                    }
                },
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (isVoiceActive) ElectricCyan else Slate850)
                    .testTag("voice_assistant_toggle_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Voice Assistant",
                    tint = if (isVoiceActive) Slate950 else Color(0xFFCBD5E1)
                )
            }

            OutlinedTextField(
                value = inputText,
                onValueChange = onInputTextChanged,
                placeholder = {
                    Text(
                        text = "Ask AI Tutor about your schedule, notes, or weak topics...",
                        color = Color(0xFF64748B),
                        fontSize = 13.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input_field"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = ElectricCyan,
                    unfocusedBorderColor = Slate800,
                    focusedContainerColor = Slate900,
                    unfocusedContainerColor = Slate900,
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                maxLines = 3
            )

            IconButton(
                onClick = { onSendMessage(inputText) },
                enabled = inputText.isNotBlank() && !isGenerating,
                modifier = Modifier
                    .clip(CircleShape)
                    .background(if (inputText.isNotBlank() && !isGenerating) ElectricCyan else Slate800)
                    .testTag("send_ai_message_button")
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = "Send",
                    tint = if (inputText.isNotBlank() && !isGenerating) Slate950 else Color(0xFF64748B)
                )
            }
        }
    }
}

@Composable
private fun ChatMessageBubble(
    msg: ChatMessageEntity,
    isUser: Boolean
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.padding(bottom = 2.dp)
        ) {
            Text(
                text = if (isUser) "You" else "AI Tutor (Memory-Connected)",
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = if (isUser) Color(0xFF94A3B8) else ElectricCyan
            )
        }

        Box(
            modifier = Modifier
                .widthIn(max = 320.dp)
                .clip(
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .background(if (isUser) NeonIndigo.copy(alpha = 0.5f) else Slate900)
                .border(
                    1.dp,
                    if (isUser) NeonIndigo else ElectricCyan.copy(alpha = 0.3f),
                    RoundedCornerShape(
                        topStart = 14.dp,
                        topEnd = 14.dp,
                        bottomStart = if (isUser) 14.dp else 2.dp,
                        bottomEnd = if (isUser) 2.dp else 14.dp
                    )
                )
                .padding(12.dp)
        ) {
            Text(
                text = msg.text,
                color = Color.White,
                fontSize = 13.sp,
                lineHeight = 19.sp
            )
        }

        if (!isUser && msg.contextSummary.isNotBlank()) {
            Text(
                text = msg.contextSummary,
                fontSize = 10.sp,
                color = Color(0xFF64748B),
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 3.dp, start = 4.dp)
            )
        }
    }
}
