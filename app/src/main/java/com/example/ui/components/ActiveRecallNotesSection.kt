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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FlashOn
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.PushPin
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.ActiveRecallNoteEntity
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

val ACTIVE_RECALL_TAGS = listOf(
    "#DataManagement",
    "#CompilerDesign",
    "#Algorithms",
    "#TCS-NQT",
    "#MathOverlap",
    "#Snowflake"
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ActiveRecallNotesSection(
    notes: List<ActiveRecallNoteEntity>,
    searchQuery: String,
    selectedTag: String,
    onSearchQueryChange: (String) -> Unit,
    onTagSelected: (String) -> Unit,
    onAddNote: (title: String, markdown: String, tag: String, reviewTomorrow: Boolean) -> Unit,
    onToggleReviewTomorrow: (ActiveRecallNoteEntity) -> Unit,
    onDeleteNote: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val pinnedCount = notes.count { it.reviewTomorrow }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(BorderStroke(1.dp, Slate700), RoundedCornerShape(16.dp))
            .testTag("active_recall_notes_section"),
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
                            .background(AmberWarning.copy(alpha = 0.15f), RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.FlashOn,
                            contentDescription = null,
                            tint = AmberWarning,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "Active Recall Notes Manager",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Slate50
                            )
                        )
                        Text(
                            text = "Formulas, theorems & concepts with spaced review",
                            style = MaterialTheme.typography.labelSmall.copy(color = Slate400)
                        )
                    }
                }

                Button(
                    onClick = { showAddDialog = true },
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AmberWarning,
                        contentColor = Color(0xFF080C16)
                    ),
                    modifier = Modifier.testTag("add_recall_note_btn")
                ) {
                    Icon(Icons.Filled.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Jot Note", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Search formulas, algorithms, or concepts...", color = Slate400, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(Icons.Filled.Search, contentDescription = "Search", tint = Slate400, modifier = Modifier.size(18.dp))
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(Icons.Filled.Close, contentDescription = "Clear", tint = Slate400, modifier = Modifier.size(16.dp))
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AmberWarning,
                    unfocusedBorderColor = Slate700,
                    focusedTextColor = Slate50,
                    unfocusedTextColor = Slate100,
                    focusedContainerColor = Slate850,
                    unfocusedContainerColor = Slate850
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Tag Filter Chips
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                val allTags = listOf("All") + ACTIVE_RECALL_TAGS
                allTags.forEach { tag ->
                    val isSelected = selectedTag == tag
                    val tagColor = getTagAccentColor(tag)
                    FilterChip(
                        selected = isSelected,
                        onClick = { onTagSelected(tag) },
                        label = {
                            Text(
                                text = tag,
                                fontSize = 11.sp,
                                fontFamily = if (tag.startsWith("#")) FontFamily.Monospace else FontFamily.SansSerif,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            containerColor = Slate800,
                            selectedContainerColor = tagColor.copy(alpha = 0.25f),
                            labelColor = Slate300,
                            selectedLabelColor = tagColor
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = Slate700,
                            selectedBorderColor = tagColor
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // "Review Tomorrow" notice
            if (pinnedCount > 0) {
                Surface(
                    color = AmberWarning.copy(alpha = 0.12f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, AmberWarning.copy(alpha = 0.35f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Filled.PushPin, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "$pinnedCount note${if (pinnedCount > 1) "s" else ""} flagged for 'Review Tomorrow' (surfaced at top)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AmberWarning,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            // Notes List
            if (notes.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Slate850, RoundedCornerShape(10.dp))
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (searchQuery.isNotEmpty()) "No notes match '$searchQuery'."
                        else "No recall notes yet. Tap '+ Jot Note' to save formulas and gate concepts.",
                        style = MaterialTheme.typography.bodyMedium.copy(color = Slate400)
                    )
                }
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    notes.forEach { note ->
                        NoteItem(
                            note = note,
                            onToggleReview = { onToggleReviewTomorrow(note) },
                            onDelete = { onDeleteNote(note.id) }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        AddNoteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, content, tag, review ->
                onAddNote(title, content, tag, review)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun NoteItem(
    note: ActiveRecallNoteEntity,
    onToggleReview: () -> Unit,
    onDelete: () -> Unit
) {
    val tagColor = getTagAccentColor(note.tag)

    Surface(
        color = Slate850,
        shape = RoundedCornerShape(10.dp),
        border = BorderStroke(
            1.dp,
            if (note.reviewTomorrow) AmberWarning.copy(alpha = 0.7f) else Slate700
        ),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top row: Tag & Pinned badge & actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Surface(
                        color = tagColor.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(6.dp),
                        border = BorderStroke(1.dp, tagColor.copy(alpha = 0.4f))
                    ) {
                        Text(
                            text = note.tag,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = tagColor,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp
                            )
                        )
                    }

                    if (note.reviewTomorrow) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Surface(
                            color = AmberWarning.copy(alpha = 0.2f),
                            shape = RoundedCornerShape(4.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Filled.PushPin, contentDescription = null, tint = AmberWarning, modifier = Modifier.size(10.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "REVIEW TOMORROW",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = AmberWarning,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onToggleReview,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = if (note.reviewTomorrow) Icons.Filled.Bookmark else Icons.Filled.BookmarkBorder,
                            contentDescription = "Review Tomorrow Toggle",
                            tint = if (note.reviewTomorrow) AmberWarning else Slate400,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(30.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Delete,
                            contentDescription = "Delete Note",
                            tint = Slate400,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Note Title
            Text(
                text = note.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Slate50
                )
            )

            Spacer(modifier = Modifier.height(6.dp))

            // Markdown Styled Content
            Surface(
                color = Slate900.copy(alpha = 0.7f),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = renderMarkdownText(note.contentMarkdown),
                    modifier = Modifier.padding(10.dp),
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontFamily = if (note.contentMarkdown.contains("T(n)") || note.contentMarkdown.contains("=")|| note.contentMarkdown.contains("λ")) FontFamily.Monospace else FontFamily.SansSerif,
                        color = Slate100,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AddNoteDialog(
    onDismiss: () -> Unit,
    onConfirm: (title: String, content: String, tag: String, reviewTomorrow: Boolean) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var selectedTag by remember { mutableStateOf("#MathOverlap") }
    var reviewTomorrow by remember { mutableStateOf(true) }

    // Quick formula templates
    val templates = listOf(
        Pair("Master Theorem", "T(n) = a*T(n/b) + Θ(n^k * log^p(n))\nCase 1: log_b(a) > k => Θ(n^{log_b(a)})\nCase 2: log_b(a) = k, p > -1 => Θ(n^k * log^{p+1}(n))"),
        Pair("Eigenvalue Sum/Product", "• Trace(A) = Σ λ_i (Sum of diagonal elements)\n• Det(A) = Π λ_i (Product of eigenvalues)\n• Cayley-Hamilton: Matrix satisfies characteristic eq"),
        Pair("BCNF Rule", "For every functional dependency X -> Y in R:\nX must be a superkey of R.\nDecomposition is lossless-join, but dependency preserving is not guaranteed."),
        Pair("Snowflake Architecture", "1. Cloud Services (Security, Metadata)\n2. Virtual Warehouses (Compute clusters)\n3. Centralized Database Storage (Micro-partitions)"),
        Pair("TCS Probability", "P(A ∪ B) = P(A) + P(B) - P(A ∩ B)\nBayes: P(A|B) = [P(B|A)*P(A)] / P(B)")
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Slate900,
        title = {
            Text(
                text = "Quick Active Recall Note",
                style = MaterialTheme.typography.titleLarge.copy(color = Slate50, fontWeight = FontWeight.Bold)
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text("Select Tag:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    ACTIVE_RECALL_TAGS.forEach { tag ->
                        val isSelected = selectedTag == tag
                        val tagColor = getTagAccentColor(tag)
                        Surface(
                            color = if (isSelected) tagColor.copy(alpha = 0.25f) else Slate800,
                            shape = RoundedCornerShape(6.dp),
                            border = BorderStroke(1.dp, if (isSelected) tagColor else Slate700),
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .clickable { selectedTag = tag }
                        ) {
                            Text(
                                text = tag,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = if (isSelected) Slate50 else Slate300
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                Text("Quick Formula / Concept Templates:", style = MaterialTheme.typography.labelSmall.copy(color = Slate400))
                Spacer(modifier = Modifier.height(4.dp))
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    templates.forEach { (tplTitle, tplContent) ->
                        Surface(
                            color = Slate800,
                            shape = RoundedCornerShape(4.dp),
                            border = BorderStroke(1.dp, Slate700),
                            modifier = Modifier
                                .clip(RoundedCornerShape(4.dp))
                                .clickable {
                                    title = tplTitle
                                    content = tplContent
                                    if (tplTitle.contains("Master") || tplTitle.contains("Eigen")) selectedTag = "#MathOverlap"
                                    else if (tplTitle.contains("BCNF")) selectedTag = "#DBMS/SQL"
                                    else if (tplTitle.contains("Snowflake")) selectedTag = "#Snowflake"
                                    else if (tplTitle.contains("TCS")) selectedTag = "#TCS-NQT"
                                }
                        ) {
                            Text(
                                text = "+ $tplTitle",
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontSize = 9.sp,
                                    color = AmberWarning
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Note Title / Concept", color = Slate400) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberWarning,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate100
                    )
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = content,
                    onValueChange = { content = it },
                    label = { Text("Markdown Formula / Concept Body", color = Slate400) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AmberWarning,
                        unfocusedBorderColor = Slate700,
                        focusedTextColor = Slate50,
                        unfocusedTextColor = Slate100
                    ),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(fontFamily = FontFamily.Monospace)
                )

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { reviewTomorrow = !reviewTomorrow }
                        .padding(vertical = 4.dp)
                ) {
                    Checkbox(
                        checked = reviewTomorrow,
                        onCheckedChange = { reviewTomorrow = it },
                        colors = CheckboxDefaults.colors(
                            checkedColor = AmberWarning,
                            checkmarkColor = Slate950,
                            uncheckedColor = Slate700
                        )
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Pin to 'Review Tomorrow' (Spaced Recall)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (reviewTomorrow) AmberWarning else Slate300,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank() && content.isNotBlank()) {
                        onConfirm(title.trim(), content.trim(), selectedTag, reviewTomorrow)
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = AmberWarning,
                    contentColor = Color(0xFF080C16)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Save Note", fontWeight = FontWeight.Bold)
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

private fun getTagAccentColor(tag: String): Color {
    return when (tag) {
        "#MathOverlap" -> ElectricCyan
        "#Algorithms" -> NeonIndigo
        "#DBMS/SQL" -> EmeraldSuccess
        "#TCS-NQT" -> TcsOrange
        "#Snowflake" -> SnowflakeBlue
        else -> Slate300
    }
}

// Lightweight Markdown Renderer (bold, bullets, highlighted terms)
private fun renderMarkdownText(text: String): androidx.compose.ui.text.AnnotatedString {
    return buildAnnotatedString {
        val lines = text.split("\n")
        lines.forEachIndexed { index, line ->
            if (line.startsWith("# ")) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Slate50, fontSize = 14.sp)) {
                    append(line.removePrefix("# "))
                }
            } else if (line.startsWith("## ")) {
                withStyle(SpanStyle(fontWeight = FontWeight.SemiBold, color = Slate50, fontSize = 13.sp)) {
                    append(line.removePrefix("## "))
                }
            } else if (line.startsWith("• ") || line.startsWith("- ")) {
                withStyle(SpanStyle(color = AmberWarning, fontWeight = FontWeight.Bold)) {
                    append("• ")
                }
                appendInlineFormatted(line.removePrefix("• ").removePrefix("- "))
            } else {
                appendInlineFormatted(line)
            }
            if (index < lines.size - 1) append("\n")
        }
    }
}

private fun androidx.compose.ui.text.AnnotatedString.Builder.appendInlineFormatted(text: String) {
    // Process **bold** and `code`
    var i = 0
    while (i < text.length) {
        if (text.startsWith("**", i)) {
            val endBold = text.indexOf("**", i + 2)
            if (endBold != -1) {
                withStyle(SpanStyle(fontWeight = FontWeight.Bold, color = Slate50)) {
                    append(text.substring(i + 2, endBold))
                }
                i = endBold + 2
                continue
            }
        }
        if (text[i] == '`') {
            val endCode = text.indexOf('`', i + 1)
            if (endCode != -1) {
                withStyle(SpanStyle(fontFamily = FontFamily.Monospace, color = ElectricCyan)) {
                    append(text.substring(i + 1, endCode))
                }
                i = endCode + 1
                continue
            }
        }
        append(text[i])
        i++
    }
}
