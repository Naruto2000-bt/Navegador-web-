package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
    onToggleBookmark: () -> Unit,
    onOpenBookmarksHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onToggleDesktopMode: () -> Unit,
    onOpenFindInPage: () -> Unit,
    onTriggerReaderMode: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        contentAlignment = Alignment.Center
    ) {
        GlassCapsule(
            modifier = Modifier
                .widthIn(max = 500.dp)
                .fillMaxWidth()
                .height(58.dp),
            backgroundColor = Color(0xF2121622)
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
                    modifier = Modifier.size(44.dp).testTag("bottom_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Atrás",
                        tint = if (currentTab.canGoBack || !currentTab.isHomePage) Color.White else Color(0x40FFFFFF)
                    )
                }

                // Forward Button
                IconButton(
                    onClick = onForward,
                    enabled = currentTab.canGoForward,
                    modifier = Modifier.size(44.dp).testTag("bottom_forward_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Adelante",
                        tint = if (currentTab.canGoForward) Color.White else Color(0x40FFFFFF)
                    )
                }

                // Home Button
                IconButton(
                    onClick = onHome,
                    modifier = Modifier.size(44.dp).testTag("bottom_home_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Home,
                        contentDescription = "Inicio",
                        tint = if (currentTab.isHomePage) Color(0xFF818CF8) else Color.White
                    )
                }

                // Tabs Counter Button
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onOpenTabsOverview() }
                        .testTag("bottom_tabs_button"),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .border(1.5.dp, Color(0xFFE0E7FF), RoundedCornerShape(7.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (tabCount > 99) ":D" else "$tabCount",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                // More Menu Button
                Box {
                    IconButton(
                        onClick = { showMenu = true },
                        modifier = Modifier.size(44.dp).testTag("bottom_menu_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "Menú",
                            tint = Color.White
                        )
                    }

                    DropdownMenu(
                        expanded = showMenu,
                        onDismissRequest = { showMenu = false },
                        modifier = Modifier
                            .background(Color(0xFF1B202E))
                            .border(1.dp, Color(0x33FFFFFF), RoundedCornerShape(16.dp))
                    ) {
                        if (!currentTab.isHomePage) {
                            DropdownMenuItem(
                                text = { Text(if (isBookmarked) "★ Marcador guardado" else "☆ Añadir a Marcadores", color = Color.White) },
                                onClick = {
                                    showMenu = false
                                    onToggleBookmark()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("📖 Modo Lectura", color = Color.White) },
                                onClick = {
                                    showMenu = false
                                    onTriggerReaderMode()
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("🔍 Buscar en la página", color = Color.White) },
                                onClick = {
                                    showMenu = false
                                    onOpenFindInPage()
                                }
                            )
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        if (currentTab.isDesktopMode) "📱 Sitio para móviles" else "🖥️ Sitio para escritorio",
                                        color = Color.White
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onToggleDesktopMode()
                                }
                            )
                            HorizontalDivider(color = Color(0x22FFFFFF), modifier = Modifier.padding(vertical = 4.dp))
                        }

                        DropdownMenuItem(
                            text = { Text("🔖 Marcadores e Historial", color = Color.White) },
                            onClick = {
                                showMenu = false
                                onOpenBookmarksHistory()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("🧩 Extensiones y Scripts", color = Color.White) },
                            onClick = {
                                showMenu = false
                                onOpenExtensionsManager()
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("⚙️ Configuración y Fondos", color = Color.White) },
                            onClick = {
                                showMenu = false
                                onOpenSettings()
                            }
                        )
                    }
                }
            }
        }
    }
}
