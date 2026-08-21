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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.rounded.Extension
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab
import com.example.model.QuickShortcut
import com.example.model.SearchEngine
import com.example.model.WallpaperOption
import com.example.ui.components.GlassSurface
import com.example.ui.components.OmnibarSearch
import com.example.ui.components.SpeedDialGrid
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    currentTab: BrowserTab,
    wallpaper: WallpaperOption? = WallpaperOption.Aurora,
    searchEngine: SearchEngine? = SearchEngine.GOOGLE,
    shortcuts: List<QuickShortcut>,
    enabledExtensionsCount: Int,
    blockedAdsCount: Int,
    onNavigate: (String) -> Unit,
    onSelectSearchEngine: (SearchEngine) -> Unit,
    onAddShortcut: (String, String) -> Unit,
    onRemoveShortcut: (QuickShortcut) -> Unit,
    onOpenExtensionsManager: () -> Unit,
    onOpenBookmarksHistory: () -> Unit,
    onOpenSettings: () -> Unit
) {
    val activeWallpaper = wallpaper ?: WallpaperOption.Aurora
    val activeEngine = searchEngine ?: SearchEngine.GOOGLE
    var currentTime by remember { mutableStateOf(getFormattedTime()) }
    var currentDate by remember { mutableStateOf(getFormattedDate()) }
    var greeting by remember { mutableStateOf(getGreeting()) }

    LaunchedEffect(Unit) {
        while (true) {
            currentTime = getFormattedTime()
            currentDate = getFormattedDate()
            greeting = getGreeting()
            delay(10000)
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // --- Background Layer ---
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(activeWallpaper.previewGradient)
        )

        // --- Main Content Scrollable ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(28.dp))

            // Minimalist Clock & Greeting Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(bottom = 18.dp)
            ) {
                Text(
                    text = currentTime,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Light,
                        color = Color.White,
                        fontSize = 42.sp,
                        letterSpacing = 1.sp
                    )
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentDate,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = Color(0xFFA5B4FC),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = greeting,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Medium,
                            color = Color(0xFFE2E8F0),
                            fontSize = 15.sp
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = Color(0x33FFFFFF),
                        modifier = Modifier.padding(2.dp)
                    ) {
                        Text(
                            text = "AURA",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color(0xFF818CF8),
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Search Engines Selector Chips
            val selectedEngineIndex = SearchEngine.entries.indexOf(activeEngine).coerceAtLeast(0)
            ScrollableTabRow(
                selectedTabIndex = selectedEngineIndex,
                edgePadding = 0.dp,
                containerColor = Color.Transparent,
                divider = {},
                indicator = {},
                modifier = Modifier
                    .widthIn(max = 480.dp)
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                SearchEngine.entries.forEach { engine ->
                    val isSelected = engine == activeEngine
                    Tab(
                        selected = isSelected,
                        onClick = { onSelectSearchEngine(engine) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(if (isSelected) Color(0xFF6366F1) else Color(0x33000000))
                            .border(
                                1.dp,
                                if (isSelected) Color(0xFFA5B4FC) else Color(0x22FFFFFF),
                                RoundedCornerShape(16.dp)
                            )
                            .padding(horizontal = 14.dp, vertical = 6.dp)
                            .testTag("engine_${engine.displayName.lowercase()}"),
                        text = {
                            Text(
                                text = engine.displayName,
                                color = if (isSelected) Color.White else Color(0xFFCBD5E1),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            // Omnibar Hero Input
            Box(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
            ) {
                OmnibarSearch(
                    currentTab = currentTab,
                    activeExtensionsCount = enabledExtensionsCount,
                    onNavigate = onNavigate,
                    onReload = {},
                    onOpenExtensionPopup = onOpenExtensionsManager,
                    isHomeHero = true
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Privacy & Extensions Stats Capsule
            GlassSurface(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
                    .clickable { onOpenExtensionsManager() }
                    .testTag("privacy_stats_banner"),
                shape = RoundedCornerShape(20.dp),
                backgroundColor = Color(0xBF161C27),
                elevation = 6.dp
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(Color(0x3310B981)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Escudo Aura",
                                tint = Color(0xFF34D399),
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Navegación Protegida",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            )
                            Text(
                                text = "$blockedAdsCount elementos bloqueados • $enabledExtensionsCount extensiones activas",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    Icon(
                        imageVector = Icons.Rounded.Extension,
                        contentDescription = "Gestionar",
                        tint = Color(0xFF818CF8),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Speed Dial Shortcuts
            Box(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth()
            ) {
                GlassSurface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    backgroundColor = Color(0xAA131722),
                    elevation = 8.dp
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Accesos Rápidos",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        SpeedDialGrid(
                            shortcuts = shortcuts,
                            onOpenShortcut = onNavigate,
                            onAddShortcut = onAddShortcut,
                            onRemoveShortcut = onRemoveShortcut
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Hub Action Pills (Bookmarks, History, Extensions, Settings)
            Row(
                modifier = Modifier
                    .widthIn(max = 520.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickHubPill(
                    icon = Icons.Default.Bookmark,
                    label = "Marcadores",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenBookmarksHistory
                )
                QuickHubPill(
                    icon = Icons.Default.History,
                    label = "Historial",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenBookmarksHistory
                )
                QuickHubPill(
                    icon = Icons.Rounded.Extension,
                    label = "Extensiones",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenExtensionsManager
                )
                QuickHubPill(
                    icon = Icons.Default.Settings,
                    label = "Ajustes",
                    modifier = Modifier.weight(1f),
                    onClick = onOpenSettings
                )
            }

            Spacer(modifier = Modifier.height(90.dp)) // Space for floating bottom bar
        }
    }
}

@Composable
private fun QuickHubPill(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    GlassSurface(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        backgroundColor = Color(0xB0161C27),
        elevation = 4.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = Color(0xFFA5B4FC),
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color(0xFFE2E8F0),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}

private fun getFormattedTime(): String {
    return SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date())
}

private fun getFormattedDate(): String {
    val sdf = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
    val formatted = sdf.format(Date())
    return formatted.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
}

private fun getGreeting(): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 6..12 -> "Buenos días"
        in 13..20 -> "Buenas tardes"
        else -> "Buenas noches"
    }
}
