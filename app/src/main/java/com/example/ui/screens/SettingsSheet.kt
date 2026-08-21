package com.example.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.entity.CacheClearLogEntity
import com.example.model.CookiePolicy
import com.example.model.SearchEngine
import com.example.model.SiteDataInfo
import com.example.model.StorageBreakdown
import com.example.model.WallpaperOption
import com.example.ui.components.GlassSurface
import com.example.ui.components.PrivacyDataDashboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    currentWallpaper: WallpaperOption? = WallpaperOption.Aurora,
    currentSearchEngine: SearchEngine? = SearchEngine.GOOGLE,
    storageBreakdown: StorageBreakdown = StorageBreakdown(14_850_000L, 2_150_000L, 4_320_000L),
    cookiePolicy: CookiePolicy = CookiePolicy.BLOCK_THIRD_PARTY,
    siteDataList: List<SiteDataInfo> = emptyList(),
    clearLogs: List<CacheClearLogEntity> = emptyList(),
    onSelectWallpaper: (WallpaperOption) -> Unit,
    onSelectSearchEngine: (SearchEngine) -> Unit,
    onSelectCookiePolicy: (CookiePolicy) -> Unit = {},
    onToggleSiteCookie: (domain: String, allow: Boolean) -> Unit = { _, _ -> },
    onToggleSiteThirdParty: (domain: String, allow: Boolean) -> Unit = { _, _ -> },
    onClearSiteData: (domain: String) -> Unit = {},
    onAddSiteException: (domain: String, allowCookies: Boolean, allowThirdParty: Boolean) -> Unit = { _, _, _ -> },
    onExecuteClean: (cookies: Boolean, cache: Boolean, history: Boolean, timeRange: String) -> Unit = { _, _, _, _ -> },
    onClearLogs: () -> Unit = {},
    onClearData: (cookies: Boolean, cache: Boolean, history: Boolean) -> Unit,
    onDismiss: () -> Unit
) {
    val activeWallpaper = currentWallpaper ?: WallpaperOption.Aurora
    val activeEngine = currentSearchEngine ?: SearchEngine.GOOGLE
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var selectedSectionIndex by remember { mutableIntStateOf(0) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFF0F1420),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 18.dp, vertical = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // Header with Close Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF312E81)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Ajustes",
                            tint = Color(0xFF818CF8),
                            modifier = Modifier.size(22.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Configuración",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                fontSize = 20.sp
                            )
                        )
                        Text(
                            text = "Privacidad, cookies y personalización",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )
                        )
                    }
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Cerrar",
                        tint = Color(0xFF94A3B8)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Navigation Tabs: General vs Privacy Dashboard
            TabRow(
                selectedTabIndex = selectedSectionIndex,
                containerColor = Color(0xFF131824),
                contentColor = Color.White,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedSectionIndex]),
                        color = Color(0xFF818CF8),
                        height = 3.dp
                    )
                },
                divider = {},
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
            ) {
                Tab(
                    selected = selectedSectionIndex == 0,
                    onClick = { selectedSectionIndex = 0 },
                    modifier = Modifier.testTag("tab_privacy_dashboard"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Security,
                                contentDescription = null,
                                tint = if (selectedSectionIndex == 0) Color(0xFF34D399) else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Privacidad y Datos",
                                color = if (selectedSectionIndex == 0) Color.White else Color(0xFF94A3B8),
                                fontWeight = if (selectedSectionIndex == 0) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                )

                Tab(
                    selected = selectedSectionIndex == 1,
                    onClick = { selectedSectionIndex = 1 },
                    modifier = Modifier.testTag("tab_personalization"),
                    text = {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                Icons.Default.Palette,
                                contentDescription = null,
                                tint = if (selectedSectionIndex == 1) Color(0xFF818CF8) else Color(0xFF64748B),
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                "Personalización",
                                color = if (selectedSectionIndex == 1) Color.White else Color(0xFF94A3B8),
                                fontWeight = if (selectedSectionIndex == 1) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 12.sp
                            )
                        }
                    }
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            // TAB 0: Visual Privacy & Site Data Dashboard
            if (selectedSectionIndex == 0) {
                PrivacyDataDashboard(
                    storageBreakdown = storageBreakdown,
                    cookiePolicy = cookiePolicy,
                    siteDataList = siteDataList,
                    clearLogs = clearLogs,
                    onSelectCookiePolicy = onSelectCookiePolicy,
                    onToggleSiteCookie = onToggleSiteCookie,
                    onToggleSiteThirdParty = onToggleSiteThirdParty,
                    onClearSiteData = onClearSiteData,
                    onAddSiteException = onAddSiteException,
                    onExecuteClean = onExecuteClean,
                    onClearLogs = onClearLogs
                )
            } else {
                // TAB 1: Personalization (Wallpapers, Search Engines)
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // 1. Wallpaper Selection
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color(0xFF1B2232)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Palette,
                                    contentDescription = null,
                                    tint = Color(0xFFA5B4FC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Fondos de Pantalla de Inicio",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Elige un tema visual o degradado para la página de inicio:",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Row(
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState())
                                    .padding(vertical = 4.dp)
                            ) {
                                WallpaperOption.allOptions.forEach { option ->
                                    val isSelected = activeWallpaper.id == option.id

                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(16.dp))
                                            .clickable { onSelectWallpaper(option) }
                                            .testTag("wallpaper_${option.id}")
                                    ) {
                                        Box(
                                            modifier = Modifier
                                                .size(width = 84.dp, height = 124.dp)
                                                .clip(RoundedCornerShape(16.dp))
                                                .border(
                                                    width = if (isSelected) 2.5.dp else 1.dp,
                                                    color = if (isSelected) Color(0xFF818CF8) else Color(0x33FFFFFF),
                                                    shape = RoundedCornerShape(16.dp)
                                                )
                                        ) {
                                            if (option.isImage && option.drawableRes != null) {
                                                Image(
                                                    painter = painterResource(id = option.drawableRes),
                                                    contentDescription = option.name,
                                                    modifier = Modifier.fillMaxSize(),
                                                    contentScale = ContentScale.Crop
                                                )
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(
                                                            Brush.verticalGradient(
                                                                listOf(
                                                                    Color.Transparent,
                                                                    Color(0x88000000)
                                                                )
                                                            )
                                                        )
                                                )
                                            } else {
                                                Box(
                                                    modifier = Modifier
                                                        .fillMaxSize()
                                                        .background(option.previewGradient)
                                                )
                                            }

                                            if (isSelected) {
                                                Box(
                                                    modifier = Modifier
                                                        .size(24.dp)
                                                        .clip(CircleShape)
                                                        .background(Color(0xFF6366F1))
                                                        .align(Alignment.TopEnd)
                                                        .padding(3.dp),
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Icon(
                                                        Icons.Default.Check,
                                                        contentDescription = "Seleccionado",
                                                        tint = Color.White,
                                                        modifier = Modifier.size(14.dp)
                                                    )
                                                }
                                            }
                                        }

                                        Spacer(modifier = Modifier.height(6.dp))

                                        Text(
                                            text = option.name,
                                            color = if (isSelected) Color(0xFF818CF8) else Color(0xFFCBD5E1),
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 2. Search Engine Selection
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color(0xFF1B2232)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    tint = Color(0xFFA5B4FC),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Motor de Búsqueda Predeterminado",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                SearchEngine.entries.forEach { engine ->
                                    val isSelected = activeEngine == engine
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color(0xFF2E384D) else Color(0xFF141926))
                                            .clickable { onSelectSearchEngine(engine) }
                                            .padding(horizontal = 14.dp, vertical = 10.dp)
                                            .testTag("settings_engine_${engine.displayName.lowercase()}"),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(
                                            text = engine.displayName,
                                            color = if (isSelected) Color.White else Color(0xFF94A3B8),
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            fontSize = 13.sp
                                        )

                                        if (isSelected) {
                                            Icon(
                                                Icons.Default.Check,
                                                contentDescription = "Activo",
                                                tint = Color(0xFF818CF8),
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // 3. Quick Total Reset
                    GlassSurface(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color(0xFF1B2232)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    Icons.Default.CleaningServices,
                                    contentDescription = null,
                                    tint = Color(0xFFEF4444),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    "Limpieza Rápida Total",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                "Elimina inmediatamente todas las cookies, archivos temporales e historial:",
                                color = Color(0xFF94A3B8),
                                fontSize = 12.sp
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            Button(
                                onClick = {
                                    onClearData(true, true, true)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF4444)),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("clear_all_data_button")
                            ) {
                                Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Borrar Todo y Cerrar", color = Color.White, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
