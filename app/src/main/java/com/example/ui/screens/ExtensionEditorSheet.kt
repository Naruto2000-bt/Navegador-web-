package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.ExtensionEntity
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExtensionEditorSheet(
    extensionToEdit: ExtensionEntity?,
    onSave: (ExtensionEntity) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var name by remember { mutableStateOf(extensionToEdit?.name ?: "") }
    var description by remember { mutableStateOf(extensionToEdit?.description ?: "") }
    var matchPattern by remember { mutableStateOf(extensionToEdit?.matchUrlPattern ?: "*") }
    var runAt by remember { mutableStateOf(extensionToEdit?.runAt ?: "DOCUMENT_END") }
    var scriptJs by remember {
        mutableStateOf(
            extensionToEdit?.scriptJs ?: """
                // Script de Usuario Aura (Tampermonkey/Greasemonkey Style)
                (function() {
                    console.log('Script ejecutado en:', window.location.href);
                    // Añade tu código JavaScript aquí
                })();
            """.trimIndent()
        )
    }
    var customCss by remember { mutableStateOf(extensionToEdit?.customCss ?: "") }

    val templates = listOf(
        TemplatePreset(
            title = "Ocultador de Elementos",
            js = """
                (function() {
                    const selector = '.ad, .banner, .sidebar, #popup';
                    document.querySelectorAll(selector).forEach(el => el.style.display = 'none');
                })();
            """.trimIndent()
        ),
        TemplatePreset(
            title = "Modificador de Fondo",
            js = """
                (function() {
                    document.body.style.backgroundColor = '#181A1B';
                    document.body.style.color = '#E8E6E3';
                })();
            """.trimIndent()
        ),
        TemplatePreset(
            title = "Controlador de Velocidad",
            js = """
                (function() {
                    document.querySelectorAll('video').forEach(v => v.playbackRate = 1.75);
                })();
            """.trimIndent()
        ),
        TemplatePreset(
            title = "Notificación Toast",
            js = """
                (function() {
                    const toast = document.createElement('div');
                    toast.textContent = '✨ Extensión Aura activa';
                    toast.style.cssText = 'position:fixed;bottom:20px;right:20px;background:#6366F1;color:#fff;padding:10px 16px;border-radius:12px;z-index:999999;font-family:sans-serif;box-shadow:0 4px 12px rgba(0,0,0,0.3);';
                    document.body.appendChild(toast);
                    setTimeout(() => toast.remove(), 3000);
                })();
            """.trimIndent()
        )
    )

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF141926),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Cancelar", tint = Color.White)
                    }
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (extensionToEdit == null) "Nueva Extensión" else "Editar Extensión",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    )
                }

                Button(
                    onClick = {
                        if (name.isNotBlank() && scriptJs.isNotBlank()) {
                            val saved = ExtensionEntity(
                                id = extensionToEdit?.id ?: 0L,
                                identifier = extensionToEdit?.identifier ?: "custom_${UUID.randomUUID().toString().take(8)}",
                                name = name.trim(),
                                description = description.ifBlank { "Script personalizado del usuario" },
                                author = extensionToEdit?.author ?: "Tú",
                                iconCategory = "code",
                                scriptJs = scriptJs,
                                customCss = customCss,
                                isEnabled = extensionToEdit?.isEnabled ?: true,
                                matchUrlPattern = matchPattern.ifBlank { "*" },
                                runAt = runAt,
                                isBuiltIn = false
                            )
                            onSave(saved)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                    shape = RoundedCornerShape(14.dp),
                    modifier = Modifier.testTag("save_extension_button")
                ) {
                    Icon(imageVector = Icons.Default.Check, contentDescription = "Guardar", modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Guardar", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Scrollable Form
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Name & Match Pattern
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre de la Extensión") },
                    placeholder = { Text("ej. Auto Saltador de Anuncios") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color(0x40FFFFFF),
                        focusedLabelColor = Color(0xFF818CF8),
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.fillMaxWidth().testTag("extension_name_input")
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Descripción corta") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color(0x40FFFFFF),
                        focusedLabelColor = Color(0xFF818CF8),
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedTextField(
                    value = matchPattern,
                    onValueChange = { matchPattern = it },
                    label = { Text("URL Match Pattern (* para todos los sitios)") },
                    placeholder = { Text("ej. *, *.wikipedia.org, youtube.com") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White,
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color(0x40FFFFFF),
                        focusedLabelColor = Color(0xFF818CF8),
                        unfocusedLabelColor = Color(0xFF94A3B8)
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Execution timing: DOCUMENT_START vs DOCUMENT_END
                Text("Momento de Ejecución:", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val timings = listOf("DOCUMENT_END" to "Al cargar página (Recomendado)", "DOCUMENT_START" to "Antes de cargar DOM")
                    timings.forEach { (timing, label) ->
                        val isSelected = runAt == timing
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF312E81) else Color(0xFF1E2433))
                                .border(1.dp, if (isSelected) Color(0xFF818CF8) else Color(0x22FFFFFF), RoundedCornerShape(12.dp))
                                .clickable { runAt = timing }
                                .padding(vertical = 8.dp, horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = label,
                                color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Template presets row
                Text("Plantillas Rápidas:", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(templates) { template ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = Color(0xFF232A3B),
                            modifier = Modifier.clickable { scriptJs = template.js }
                        ) {
                            Text(
                                text = template.title,
                                color = Color(0xFFA5B4FC),
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Code Editor for JavaScript
                Text("Código JavaScript (User Script):", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = scriptJs,
                    onValueChange = { scriptJs = it },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFF67E8F9),
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFF67E8F9),
                        unfocusedTextColor = Color(0xFF67E8F9),
                        focusedContainerColor = Color(0xFF0F131D),
                        unfocusedContainerColor = Color(0xFF0F131D),
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .testTag("extension_script_input")
                )

                Spacer(modifier = Modifier.height(14.dp))

                // Optional CSS Injector
                Text("CSS Personalizado (Opcional):", color = Color(0xFFE2E8F0), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = customCss,
                    onValueChange = { customCss = it },
                    placeholder = { Text("body { filter: contrast(110%); }") },
                    textStyle = TextStyle(
                        fontFamily = FontFamily.Monospace,
                        color = Color(0xFFFDE047),
                        fontSize = 12.sp
                    ),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = Color(0xFFFDE047),
                        unfocusedTextColor = Color(0xFFFDE047),
                        focusedContainerColor = Color(0xFF0F131D),
                        unfocusedContainerColor = Color(0xFF0F131D),
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color(0x33FFFFFF)
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                )

                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

data class TemplatePreset(val title: String, val js: String)
