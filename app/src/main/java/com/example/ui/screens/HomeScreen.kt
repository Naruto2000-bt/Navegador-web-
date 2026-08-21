package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.rounded.VpnKey
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
    onOpenSettings: () -> Unit,
    onOpenPrivacyInfo: () -> Unit = {},
    onOpenNormalTab: () -> Unit = {}
) {
    val isIncognito = currentTab.isIncognito
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
        if (isIncognito) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color(0xFF1E1435),
                                Color(0xFF130E24),
                                Color(0xFF0B0914),
                                Color(0xFF000000)
                            )
                        )
                    )
            )
        } else if (activeWallpaper.isImage && activeWallpaper.drawableRes != null) {
            Image(
                painter = painterResource(id = activeWallpaper.drawableRes),
                contentDescription = activeWallpaper.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(activeWallpaper.previewGradient)
            )
        }

        // --- Main Content Scrollable ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            if (isIncognito) {
                // --- INCOGNITO HERO HEADER ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .padding(bottom = 16.dp)
                        .testTag("incognito_home_header")
                ) {
                    Box(
                        modifier = Modifier
                            .size(72.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    listOf(Color(0xFF8B5CF6), Color(0xFF4C1D95), Color(0xFF1E1B4B))
                                )
                            )
                            .border(1.5.dp, Color(0xFFA78BFA), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = "Modo Incógnito",
                            tint = Color.White,
                            modifier = Modifier.size(36.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Modo Incógnito",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 26.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Navegación privada y protegida",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = Color(0xFFA78BFA),
                            fontSize = 13.5.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )
                }
            } else {
                // --- NORMAL HERO HEADER ---
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.padding(bottom = 16.dp)
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
                            fontSize = 13.5.sp,
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
                                fontSize = 14.5.sp
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
                    .padding(bottom = 14.dp)
            ) {
                SearchEngine.entries.forEach { engine ->
                    val isSelected = engine == activeEngine
                    Tab(
                        selected = isSelected,
                        onClick = { onSelectSearchEngine(engine) },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                if (isSelected) {
                                    if (isIncognito) Color(0xFF7C3AED) else Color(0xFF6366F1)
                                } else Color(0x33000000)
                            )
                            .border(
                                1.dp,
                                if (isSelected) {
                                    if (isIncognito) Color(0xFFC4B5FD) else Color(0xFFA5B4FC)
                                } else Color(0x22FFFFFF),
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

            // Slim & Sleek Omnibar Hero Input
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
                    onOpenPrivacyInfo = onOpenPrivacyInfo,
                    isHomeHero = true
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            if (isIncognito) {
                // Incognito Informational Privacy Card
                Box(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                ) {
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = Color(0xDD171126),
                        borderColor = Color(0x44A78BFA),
                        elevation = 6.dp
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Security,
                                    contentDescription = null,
                                    tint = Color(0xFFA78BFA),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "Lo que hace el Modo Incógnito:",
                                    style = MaterialTheme.typography.titleSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            Spacer(modifier = Modifier.height(14.dp))

                            IncognitoFeatureItem(
                                icon = Icons.Default.Close,
                                iconColor = Color(0xFFEF4444),
                                title = "No guarda historial",
                                description = "Las páginas visitadas no se registrarán en tu historial."
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            IncognitoFeatureItem(
                                icon = Icons.Default.Close,
                                iconColor = Color(0xFFEF4444),
                                title = "Sin cookies ni datos de sitios",
                                description = "Las sesiones y cookies temporales se borran al cerrar la pestaña."
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            IncognitoFeatureItem(
                                icon = Icons.Default.CheckCircle,
                                iconColor = Color(0xFF34D399),
                                title = "Protección anti-rastreo activa",
                                description = "Bloqueador de anuncios y telemetría de terceros habilitado."
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Button(
                                onClick = onOpenNormalTab,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0x33A78BFA)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("switch_to_normal_tab_button")
                            ) {
                                Text(
                                    text = "Abrir pestaña normal",
                                    color = Color(0xFFDDD6FE),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                        }
                    }
                }
            } else {
                // Normal Speed Dial Shortcuts Grid
                Box(
                    modifier = Modifier
                        .widthIn(max = 520.dp)
                        .fillMaxWidth()
                ) {
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(22.dp),
                        backgroundColor = Color(0xAA131722),
                        elevation = 6.dp
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
            }

            Spacer(modifier = Modifier.height(90.dp)) // Space for floating bottom bar
        }
    }
}

@Composable
private fun IncognitoFeatureItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    iconColor: Color,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(20.dp)
                .clip(CircleShape)
                .background(iconColor.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconColor,
                modifier = Modifier.size(13.dp)
            )
        }

        Spacer(modifier = Modifier.width(10.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF94A3B8),
                    fontSize = 11.5.sp,
                    lineHeight = 15.sp
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
