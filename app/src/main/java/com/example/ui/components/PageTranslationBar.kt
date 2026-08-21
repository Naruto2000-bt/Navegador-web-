package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.LanguageItem
import com.example.model.PageTranslationState
import com.example.model.TranslationLanguages

@Composable
fun PageTranslationBar(
    modifier: Modifier = Modifier,
    translationState: PageTranslationState,
    onTranslate: (targetLang: String) -> Unit,
    onRevertOriginal: () -> Unit,
    onSelectSourceLang: (String) -> Unit,
    onSelectTargetLang: (String) -> Unit,
    onDismiss: () -> Unit
) {
    var showLanguagePickerFor by remember { mutableStateOf<String?>(null) } // "source" or "target" or null

    val sourceLangItem = remember(translationState.sourceLangCode) {
        TranslationLanguages.find(translationState.sourceLangCode)
    }
    val targetLangItem = remember(translationState.targetLangCode) {
        TranslationLanguages.find(translationState.targetLangCode)
    }

    val quickTargetLanguages = remember {
        listOf(
            TranslationLanguages.find("es"),
            TranslationLanguages.find("en"),
            TranslationLanguages.find("fr"),
            TranslationLanguages.find("de"),
            TranslationLanguages.find("it"),
            TranslationLanguages.find("pt"),
            TranslationLanguages.find("ja")
        )
    }

    AnimatedVisibility(
        visible = translationState.isBannerVisible,
        enter = fadeIn() + slideInVertically(initialOffsetY = { -it / 2 }),
        exit = fadeOut() + slideOutVertically(targetOffsetY = { -it / 2 }),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 4.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color(0xFF1E1B4B), Color(0xFF13172A))
                    )
                )
                .border(1.dp, Color(0x44818CF8), RoundedCornerShape(16.dp))
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .testTag("page_translation_bar")
        ) {
            // Main Top Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Translation Header & Languages Chips
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0x336366F1)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Translate,
                            contentDescription = "Traducción",
                            tint = Color(0xFFA5B4FC),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Source language selector chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF262E48),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showLanguagePickerFor = "source" }
                            .testTag("translate_source_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${sourceLangItem.flagEmoji} ${sourceLangItem.name}",
                                color = Color.White,
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "▾", color = Color(0xFF94A3B8), fontSize = 10.sp)
                        }
                    }

                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Hacia",
                        tint = Color(0xFF818CF8),
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(14.dp)
                    )

                    // Target language selector chip
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFF312E81),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { showLanguagePickerFor = "target" }
                            .testTag("translate_target_chip")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                        ) {
                            Text(
                                text = "${targetLangItem.flagEmoji} ${targetLangItem.name}",
                                color = Color(0xFFE0E7FF),
                                fontSize = 11.5.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(text = "▾", color = Color(0xFFA5B4FC), fontSize = 10.sp)
                        }
                    }
                }

                // Action Button: Traducir or Original
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (translationState.isTranslating) {
                        Box(
                            modifier = Modifier
                                .height(30.dp)
                                .padding(horizontal = 8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                color = Color(0xFF818CF8),
                                strokeWidth = 2.dp
                            )
                        }
                    } else if (translationState.isTranslated) {
                        Button(
                            onClick = onRevertOriginal,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF334155)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("revert_translation_button")
                        ) {
                            Text("Original", fontSize = 11.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFFE2E8F0))
                        }
                    } else {
                        Button(
                            onClick = { onTranslate(translationState.targetLangCode) },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(30.dp)
                                .testTag("trigger_translation_button")
                        ) {
                            Text("Traducir", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    }

                    Spacer(modifier = Modifier.width(2.dp))

                    IconButton(
                        onClick = onDismiss,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("dismiss_translation_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Cerrar traductor",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            // Quick Target Language Carousel
            Spacer(modifier = Modifier.height(6.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                item {
                    Text(
                        text = "Traducir a:",
                        color = Color(0xFF94A3B8),
                        fontSize = 10.5.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                items(quickTargetLanguages) { lang ->
                    val isSelected = lang.code == translationState.targetLangCode
                    FilterChip(
                        selected = isSelected,
                        onClick = {
                            onSelectTargetLang(lang.code)
                            onTranslate(lang.code)
                        },
                        label = {
                            Text(
                                text = "${lang.flagEmoji} ${lang.name}",
                                fontSize = 10.5.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Color(0xFF4F46E5),
                            selectedLabelColor = Color.White,
                            containerColor = Color(0x331E293B),
                            labelColor = Color(0xFFCBD5E1)
                        ),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(26.dp)
                    )
                }

                item {
                    TextButton(
                        onClick = { showLanguagePickerFor = "target" },
                        modifier = Modifier.height(26.dp)
                    ) {
                        Text("+ Más idiomas", color = Color(0xFFA5B4FC), fontSize = 10.5.sp, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }

    // Language Picker Bottom Sheet Modal
    if (showLanguagePickerFor != null) {
        val pickerType = showLanguagePickerFor!!
        LanguagePickerModalSheet(
            title = if (pickerType == "source") "Idioma de la página (Origen)" else "Traducir página a (Destino)",
            currentSelectedCode = if (pickerType == "source") translationState.sourceLangCode else translationState.targetLangCode,
            onSelectLanguage = { selected ->
                if (pickerType == "source") {
                    onSelectSourceLang(selected.code)
                } else {
                    onSelectTargetLang(selected.code)
                    onTranslate(selected.code)
                }
                showLanguagePickerFor = null
            },
            onDismiss = { showLanguagePickerFor = null }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguagePickerModalSheet(
    title: String,
    currentSelectedCode: String,
    onSelectLanguage: (LanguageItem) -> Unit,
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var searchQuery by remember { mutableStateOf("") }

    val filteredList = remember(searchQuery) {
        if (searchQuery.isBlank()) TranslationLanguages.supported
        else {
            val q = searchQuery.trim().lowercase()
            TranslationLanguages.supported.filter {
                it.name.lowercase().contains(q) ||
                it.nativeName.lowercase().contains(q) ||
                it.code.lowercase().contains(q)
            }
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF131824),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                )

                IconButton(onClick = onDismiss, modifier = Modifier.size(30.dp)) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar for languages
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Buscar idioma...", color = Color(0xFF64748B), fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        tint = Color(0xFF818CF8),
                        modifier = Modifier.size(18.dp)
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White,
                    focusedContainerColor = Color(0xFF1E2433),
                    unfocusedContainerColor = Color(0xFF1E2433),
                    focusedBorderColor = Color(0xFF818CF8),
                    unfocusedBorderColor = Color(0x22FFFFFF)
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
            ) {
                items(filteredList, key = { it.code }) { item ->
                    val isSelected = item.code.equals(currentSelectedCode, ignoreCase = true)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isSelected) Color(0xFF2E2B69) else Color(0xFF1B202E))
                            .clickable { onSelectLanguage(item) }
                            .padding(horizontal = 14.dp, vertical = 12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(text = item.flagEmoji, fontSize = 20.sp)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = item.name,
                                    color = if (isSelected) Color(0xFFA5B4FC) else Color.White,
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = item.nativeName,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.5.sp
                                )
                            }
                        }

                        if (isSelected) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Seleccionado",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))
        }
    }
}
