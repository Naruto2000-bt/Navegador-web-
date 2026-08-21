package com.example.ui.components

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.FindInPage
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material.icons.rounded.DesktopWindows
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab

@Composable
fun BrowserBottomBar(
    modifier: Modifier = Modifier,
    currentTab: BrowserTab,
    tabCount: Int,
    enabledExtensionsCount: Int = 0,
    isBookmarked: Boolean,
    onBack: () -> Unit,
    onForward: () -> Unit,
    onHome: () -> Unit,
    onOpenExtensionsManager: () -> Unit,
    onOpenTabsOverview: () -> Unit,
    onOpenNewIncognitoTab: () -> Unit = {},
    onToggleBookmark: () -> Unit,
    onOpenBookmarksHistory: () -> Unit,
    onOpenPasswords: () -> Unit = {},
    onOpenSettings: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onOpenFindInPage: () -> Unit,
    onTriggerReaderMode: () -> Unit,
    onOpenTranslate: () -> Unit = {},
    onOpenPrivacyInfo: () -> Unit = {}
) {
    var showMenu by remember { mutableStateOf(false) }

    val barBg = if (currentTab.isIncognito) Color(0xF5181226) else Color(0xF2121622)
    val barBorder = if (currentTab.isIncognito) Color(0x66A78BFA) else Color(0x33FFFFFF)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCapsule(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .height(52.dp),
            backgroundColor = barBg,
            borderColor = barBorder
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Back Button
                IconButton(
                    onClick = onBack,
                    enabled = currentTab.canGoBack || !currentTab.isHomePage,
                    modifier = Modifier.size(40.dp).testTag("bottom_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = if (currentTab.canGoBack || !currentTab.isHomePage) Color.White else Color(0x40FFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Forward Button
                IconButton(
                    onClick = onForward,
                    enabled = currentTab.canGoForward,
                    modifier = Modifier.size(40.dp).testTag("bottom_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Adelante",
                        tint = if (currentTab.canGoForward) Color.White else Color(0x40FFFFFF),
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Home Button
                IconButton(
                    onClick = onHome,
                    modifier = Modifier.size(40.dp).testTag("bottom_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio",
                        tint = if (currentTab.isHomePage) (if (currentTab.isIncognito) Color(0xFFA78BFA) else Color(0xFF818CF8)) else Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }

                // Tabs Counter Button (with incognito icon if active tab is incognito)
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable { onOpenTabsOverview() }
                        .testTag("bottom_tabs_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .border(
                                1.5.dp,
                                if (currentTab.isIncognito) Color(0xFFA78BFA) else Color(0xFFE0E7FF),
                                RoundedCornerShape(6.dp)
                            )
                            .background(
                                if (currentTab.isIncognito) Color(0x447C3AED) else Color.Transparent,
                                RoundedCornerShape(6.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (currentTab.isIncognito) {
                            Icon(
                                imageVector = Icons.Default.VisibilityOff,
                                contentDescription = "Pestañas incógnito",
                                tint = Color(0xFFA78BFA),
                                modifier = Modifier.size(13.dp)
                            )
                        } else {
                            Text(
                                text = if (tabCount > 99) ":D" else "$tabCount",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                // More Menu Button (3-dots)
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(40.dp).testTag("bottom_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            tint = Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(Color(0xFF1B202E))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    ) {
                        // Incognito Mode Action
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = Color(0xFFA78BFA),
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Nueva pestaña de incógnito", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenNewIncognitoTab()
                            },
                            modifier = Modifier.testTag("menu_new_incognito_tab")
                        )

                        HorizontalDivider(color = Color(0x22FFFFFF), modifier = Modifier.padding(vertical = 4.dp))
                        // Web Page Specific Actions
                        if (!currentTab.isHomePage) {
                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (isBookmarked) Icons.Default.Star else Icons.Default.StarBorder,
                                            contentDescription = null,
                                            tint = if (isBookmarked) Color(0xFFFBBF24) else Color(0xFF94A3B8),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(if (isBookmarked) "Marcador guardado" else "Añadir a Marcadores", color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onToggleBookmark()
                                },
                                modifier = Modifier.testTag("menu_bookmark_action")
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.MenuBook, contentDescription = null, tint = Color(0xFFA78BFA), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Modo Lectura", color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onTriggerReaderMode()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.Translate, contentDescription = null, tint = Color(0xFFA5B4FC), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Traducir página...", color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onOpenTranslate()
                                },
                                modifier = Modifier.testTag("menu_translate_action")
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(Icons.Default.FindInPage, contentDescription = null, tint = Color(0xFF38BDF8), modifier = Modifier.size(18.dp))
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text("Buscar en la página", color = Color.White, fontSize = 13.sp)
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onOpenFindInPage()
                                }
                            )

                            DropdownMenuItem(
                                text = {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = if (currentTab.isDesktopMode) Icons.Default.PhoneAndroid else Icons.Rounded.DesktopWindows,
                                            contentDescription = null,
                                            tint = Color(0xFF34D399),
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(10.dp))
                                        Text(
                                            if (currentTab.isDesktopMode) "Sitio para móviles" else "Sitio para escritorio",
                                            color = Color.White,
                                            fontSize = 13.sp
                                        )
                                    }
                                },
                                onClick = {
                                    showMenu = false
                                    onToggleDesktopMode()
                                }
                            )

                            HorizontalDivider(color = Color(0x22FFFFFF), modifier = Modifier.padding(vertical = 4.dp))
                        }

                        // Navigation & Main Browser Sections
                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Shield, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Navegación Protegida", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenPrivacyInfo()
                            },
                            modifier = Modifier.testTag("menu_privacy_item")
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Bookmark, contentDescription = null, tint = Color(0xFF818CF8), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Marcadores", color = Color.White, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenBookmarksHistory()
                            },
                            modifier = Modifier.testTag("menu_bookmarks_item")
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.History, contentDescription = null, tint = Color(0xFFFBBF24), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Historial", color = Color.White, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenBookmarksHistory()
                            },
                            modifier = Modifier.testTag("menu_history_item")
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Rounded.Extension, contentDescription = null, tint = Color(0xFF67E8F9), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Extensiones ($enabledExtensionsCount)", color = Color.White, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenExtensionsManager()
                            },
                            modifier = Modifier.testTag("menu_extensions_item")
                        )

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Key, contentDescription = null, tint = Color(0xFFA5B4FC), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Contraseñas guardadas", color = Color.White, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenPasswords()
                            },
                            modifier = Modifier.testTag("menu_passwords_item")
                        )

                        HorizontalDivider(color = Color(0x22FFFFFF), modifier = Modifier.padding(vertical = 4.dp))

                        DropdownMenuItem(
                            text = {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(Icons.Default.Settings, contentDescription = null, tint = Color(0xFFCBD5E1), modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(10.dp))
                                    Text("Ajustes y Fondos", color = Color.White, fontSize = 13.sp)
                                }
                            },
                            onClick = {
                                showMenu = false
                                onOpenSettings()
                            },
                            modifier = Modifier.testTag("menu_settings_item")
                        )
                    }
                }
            }
        }
    }
}
