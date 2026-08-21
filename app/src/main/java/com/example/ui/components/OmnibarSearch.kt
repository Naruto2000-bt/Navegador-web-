package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.Shield
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab

@Composable
fun OmnibarSearch(
    modifier: Modifier = Modifier,
    currentTab: BrowserTab,
    activeExtensionsCount: Int,
    onNavigate: (String) -> Unit,
    onReload: () -> Unit,
    onOpenExtensionPopup: () -> Unit,
    onOpenPrivacyInfo: (() -> Unit)? = null,
    isHomeHero: Boolean = false,
    initialFocus: Boolean = false
) {
    var queryText by remember(currentTab.url) {
        mutableStateOf(if (currentTab.isHomePage) "" else currentTab.url)
    }
    var isFocused by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    val submitNavigation = {
        val trimmed = queryText.trim()
        if (trimmed.isNotBlank()) {
            focusManager.clearFocus()
            onNavigate(trimmed)
        }
    }

    LaunchedEffect(initialFocus) {
        if (initialFocus) {
            focusRequester.requestFocus()
        }
    }

    // Slim, sleek search bar (42dp - 44dp height for a streamlined, modern feel)
    val barBg = when {
        currentTab.isIncognito -> if (isHomeHero) Color(0xF0181528) else Color(0xF513101E)
        isHomeHero -> Color(0xD9181E29)
        else -> Color(0xF0131722)
    }

    GlassCapsule(
        modifier = modifier
            .fillMaxWidth()
            .height(if (isHomeHero) 44.dp else 42.dp)
            .testTag("omnibar_capsule"),
        backgroundColor = barBg
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Incognito indicator tag if in incognito mode
            if (currentTab.isIncognito) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .padding(end = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0x33A78BFA))
                        .padding(horizontal = 5.dp, vertical = 2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.VisibilityOff,
                        contentDescription = "Modo Incógnito",
                        tint = Color(0xFFA78BFA),
                        modifier = Modifier.size(13.dp)
                    )
                }
            }

            // Leading Icon: Search or SSL lock (clickable for privacy summary)
            if (isHomeHero || isFocused) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Buscar",
                    tint = if (currentTab.isIncognito) Color(0xFFA78BFA) else Color(0xFFA5B4FC),
                    modifier = Modifier.size(17.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .clickable {
                            if (onOpenPrivacyInfo != null) {
                                onOpenPrivacyInfo()
                            } else {
                                onReload()
                            }
                        }
                        .testTag("omnibar_lock_button"),
                    contentAlignment = Alignment.Center
                ) {
                    if (currentTab.isSecure) {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Conexión Segura - Ver información de privacidad",
                            tint = Color(0xFF34D399),
                            modifier = Modifier.size(15.dp)
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.Public,
                            contentDescription = "Web",
                            tint = Color(0xFF94A3B8),
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Main Input / URL Display
            Box(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = 2.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                if (!isFocused && !isHomeHero && currentTab.url.isNotBlank() && !currentTab.isHomePage) {
                    // Clean Domain + Title display when not focused
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) {
                                isFocused = true
                                focusRequester.requestFocus()
                            },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = currentTab.displayHost,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                fontSize = 13.5.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }
                }

                BasicTextField(
                    value = queryText,
                    onValueChange = { queryText = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .focusRequester(focusRequester)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                            if (focusState.isFocused && queryText.isEmpty() && !currentTab.isHomePage) {
                                queryText = currentTab.url
                            }
                        }
                        .onKeyEvent { keyEvent ->
                            if ((keyEvent.key == Key.Enter || keyEvent.key == Key.NumPadEnter) && keyEvent.type == KeyEventType.KeyUp) {
                                submitNavigation()
                                true
                            } else {
                                false
                            }
                        }
                        .testTag("omnibar_text_input"),
                    singleLine = true,
                    textStyle = TextStyle(
                        color = Color.White,
                        fontSize = if (isHomeHero) 14.sp else 13.5.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    cursorBrush = SolidColor(Color(0xFF818CF8)),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Search
                    ),
                    keyboardActions = KeyboardActions(
                        onSearch = { submitNavigation() },
                        onGo = { submitNavigation() },
                        onDone = { submitNavigation() },
                        onSend = { submitNavigation() }
                    ),
                    decorationBox = { innerTextField ->
                        if (queryText.isEmpty() && (isFocused || isHomeHero)) {
                            val placeholderText = when {
                                currentTab.isIncognito -> if (isHomeHero) "Buscar en privado (Incógnito)…" else "Escribe una URL privada…"
                                isHomeHero -> "Buscar o escribir URL…"
                                else -> "Escribe una dirección…"
                            }
                            Text(
                                text = placeholderText,
                                style = TextStyle(
                                    color = if (currentTab.isIncognito) Color(0xFFA78BFA).copy(alpha = 0.7f) else Color(0xFF94A3B8),
                                    fontSize = if (isHomeHero) 14.sp else 13.5.sp
                                )
                            )
                        }
                        if (isFocused || isHomeHero || currentTab.isHomePage) {
                            innerTextField()
                        }
                    }
                )
            }

            // Trailing Clear Button
            AnimatedVisibility(
                visible = queryText.isNotEmpty() && (isFocused || isHomeHero),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                IconButton(
                    onClick = { queryText = "" },
                    modifier = Modifier.size(26.dp).testTag("omnibar_clear_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Limpiar",
                        tint = Color(0xFF94A3B8),
                        modifier = Modifier.size(15.dp)
                    )
                }
            }

            // Dedicated Submit / Go Action Button when user has entered text
            AnimatedVisibility(
                visible = queryText.trim().isNotEmpty(),
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF6366F1))
                        .clickable { submitNavigation() }
                        .testTag("omnibar_submit_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Ir a página o buscar",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            if (!isHomeHero && !isFocused && queryText.isEmpty()) {
                // Extensions badge & quick popup launcher
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenExtensionPopup() }
                        .padding(horizontal = 5.dp, vertical = 3.dp)
                        .testTag("extension_pill_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Rounded.Extension,
                            contentDescription = "Extensiones en esta página",
                            tint = if (activeExtensionsCount > 0) Color(0xFF67E8F9) else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        if (activeExtensionsCount > 0) {
                            Spacer(modifier = Modifier.width(3.dp))
                            Surface(
                                shape = CircleShape,
                                color = Color(0xFF0284C7)
                            ) {
                                Text(
                                    text = "$activeExtensionsCount",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                    }
                }

                IconButton(
                    onClick = onReload,
                    modifier = Modifier.size(28.dp).testTag("reload_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Recargar",
                        tint = Color(0xFFCBD5E1),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}
