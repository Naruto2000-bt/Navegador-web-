package com.example.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.QuickShortcut

sealed interface SpeedDialItem {
    data class ShortcutItem(val shortcut: QuickShortcut) : SpeedDialItem
    object AddAction : SpeedDialItem
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SpeedDialGrid(
    modifier: Modifier = Modifier,
    shortcuts: List<QuickShortcut>,
    onOpenShortcut: (String) -> Unit,
    onAddShortcut: (String, String) -> Unit,
    onRemoveShortcut: (QuickShortcut) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var selectedShortcutToDelete by remember { mutableStateOf<QuickShortcut?>(null) }

    val allItems: List<SpeedDialItem> = shortcuts.map { SpeedDialItem.ShortcutItem(it) } + SpeedDialItem.AddAction
    val chunkedRows = allItems.chunked(4)

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        chunkedRows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                for (i in 0 until 4) {
                    if (i < rowItems.size) {
                        val item = rowItems[i]
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center
                        ) {
                            when (item) {
                                is SpeedDialItem.ShortcutItem -> {
                                    val shortcut = item.shortcut
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .combinedClickable(
                                                onClick = { onOpenShortcut(shortcut.url) },
                                                onLongClick = { selectedShortcutToDelete = shortcut }
                                            )
                                            .padding(4.dp)
                                            .testTag("shortcut_${shortcut.title.lowercase()}")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(
                                                    Brush.linearGradient(
                                                        listOf(
                                                            Color(shortcut.iconColor).copy(alpha = 0.85f),
                                                            Color(shortcut.iconColor).copy(alpha = 0.55f)
                                                        )
                                                    )
                                                ),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = shortcut.initial,
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = Color.White,
                                                    fontSize = 16.sp
                                                )
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = shortcut.title,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFF1F5F9),
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Medium
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis,
                                            textAlign = TextAlign.Center
                                        )
                                    }
                                }
                                is SpeedDialItem.AddAction -> {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .combinedClickable(onClick = { showAddDialog = true })
                                            .padding(4.dp)
                                            .testTag("add_shortcut_button")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(54.dp)
                                                .clip(RoundedCornerShape(18.dp))
                                                .background(Color(0x33FFFFFF)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Add,
                                                contentDescription = "Añadir acceso rápido",
                                                tint = Color(0xFFE2E8F0),
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = "Añadir",
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = Color(0xFFCBD5E1),
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    } else {
                        // Empty spacer cell to keep alignment consistent in 4-column row
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }

    // Add Shortcut Dialog
    if (showAddDialog) {
        var title by remember { mutableStateOf("") }
        var url by remember { mutableStateOf("") }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            containerColor = Color(0xFF1E2433),
            title = { Text("Nuevo Acceso Rápido", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Nombre (ej. Wikipedia)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color(0x40FFFFFF),
                            focusedLabelColor = Color(0xFF818CF8),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("shortcut_title_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedTextField(
                        value = url,
                        onValueChange = { url = it },
                        label = { Text("URL (ej. https://es.wikipedia.org)") },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            focusedBorderColor = Color(0xFF818CF8),
                            unfocusedBorderColor = Color(0x40FFFFFF),
                            focusedLabelColor = Color(0xFF818CF8),
                            unfocusedLabelColor = Color(0xFF94A3B8)
                        ),
                        modifier = Modifier.fillMaxWidth().testTag("shortcut_url_input")
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (title.isNotBlank() && url.isNotBlank()) {
                            onAddShortcut(title.trim(), url.trim())
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    modifier = Modifier.testTag("confirm_add_shortcut_button")
                ) {
                    Text("Guardar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            }
        )
    }

    // Delete Shortcut Confirmation
    selectedShortcutToDelete?.let { shortcut ->
        AlertDialog(
            onDismissRequest = { selectedShortcutToDelete = null },
            containerColor = Color(0xFF1E2433),
            title = { Text("Eliminar acceso directo", color = Color.White) },
            text = { Text("¿Deseas eliminar '${shortcut.title}' de tus accesos rápidos?", color = Color(0xFFCBD5E1)) },
            confirmButton = {
                Button(
                    onClick = {
                        onRemoveShortcut(shortcut)
                        selectedShortcutToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444))
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedShortcutToDelete = null }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            }
        )
    }
}
