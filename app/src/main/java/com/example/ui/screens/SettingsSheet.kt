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
import androidx.compose.material.icons.filled.Key
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
import com.example.data.entity.SavedCredentialEntity
import com.example.model.CookiePolicy
import com.example.model.SearchEngine
import com.example.model.SiteDataInfo
import com.example.model.StorageBreakdown
import com.example.model.WallpaperOption
import com.example.ui.components.GlassSurface
import com.example.ui.components.PasswordManagerView
import com.example.ui.components.PrivacyDataDashboard

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsSheet(
    initialSectionIndex: Int = 0,
    currentWallpaper: WallpaperOption? = WallpaperOption.Aurora,
    currentSearchEngine: SearchEngine? = SearchEngine.GOOGLE,
    storageBreakdown: StorageBreakdown = StorageBreakdown(14_850_000L, 2_150_000L, 4_320_000L),
    cookiePolicy: CookiePolicy = CookiePolicy.BLOCK_THIRD_PARTY,
    siteDataList: List<SiteDataInfo> = emptyList(),
    clearLogs: List<CacheClearLogEntity> = emptyList(),
    savedCredentials: List<SavedCredentialEntity> = emptyList(),
    isAutoSaveCredentialsEnabled: Boolean = true,
    onToggleAutoSaveCredentials: (Boolean) -> Unit = {},
    onSaveManualCredential: (domain: String, url: String, user: String, pass: String) -> Unit = { _, _, _, _ -> },
    onDeleteSavedCredential: (Long) -> Unit = {},
    onClearAllSavedCredentials: () -> Unit = {},
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
    var selectedSectionIndex by remember(initialSectionIndex) { mutableIntStateOf(initialSectionIndex) }

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
                            text = "Privacidad, contraseñas y personalización",
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

            // Navigation Tabs: Privacy vs Passwords vs Personalization
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .background(Color(0xFF131824))
                    .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(14.dp))
                    .padding(4.dp),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                // Tab 0: Privacidad
                val tab0 = selectedSectionIndex == 0
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (tab0) Color(0xFF1E293B) else Color.Transparent)
                        .border(
                            width = if (tab0) 1.dp else 0.dp,
                            color = if (tab0) Color(0xFF34D399).copy(alpha = 0.6f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedSectionIndex = 0 }
                        .padding(vertical = 10.dp, horizontal = 2.dp)
                        .testTag("tab_privacy_dashboard"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = null,
                            tint = if (tab0) Color(0xFF34D399) else Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Privacidad",
                            color = if (tab0) Color.White else Color(0xFF94A3B8),
                            fontWeight = if (tab0) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.5.sp
                        )
                    }
                }

                // Tab 1: Contraseñas
                val tab1 = selectedSectionIndex == 1
                Box(
                    modifier = Modifier
                        .weight(1.25f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (tab1) Color(0xFF312E81) else Color.Transparent)
                        .border(
                            width = if (tab1) 1.dp else 0.dp,
                            color = if (tab1) Color(0xFF818CF8) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedSectionIndex = 1 }
                        .padding(vertical = 10.dp, horizontal = 2.dp)
                        .testTag("tab_passwords"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Key,
                            contentDescription = null,
                            tint = if (tab1) Color(0xFFA5B4FC) else Color(0xFF818CF8),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Contraseñas",
                            color = if (tab1) Color.White else Color(0xFFA5B4FC),
                            fontWeight = if (tab1) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 11.5.sp
                        )
                        if (savedCredentials.isNotEmpty()) {
                            Spacer(modifier = Modifier.width(4.dp))
                            Box(
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(if (tab1) Color(0xFF4F46E5) else Color(0xFF1E293B))
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "${savedCredentials.size}",
                                    color = Color.White,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // Tab 2: Diseño
                val tab2 = selectedSectionIndex == 2
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (tab2) Color(0xFF1E293B) else Color.Transparent)
                        .border(
                            width = if (tab2) 1.dp else 0.dp,
                            color = if (tab2) Color(0xFF818CF8).copy(alpha = 0.5f) else Color.Transparent,
                            shape = RoundedCornerShape(10.dp)
                        )
                        .clickable { selectedSectionIndex = 2 }
                        .padding(vertical = 10.dp, horizontal = 2.dp)
                        .testTag("tab_personalization"),
                    contentAlignment = Alignment.Center
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Palette,
                            contentDescription = null,
                            tint = if (tab2) Color(0xFF818CF8) else Color(0xFF64748B),
                            modifier = Modifier.size(15.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Diseño",
                            color = if (tab2) Color.White else Color(0xFF94A3B8),
                            fontWeight = if (tab2) FontWeight.Bold else FontWeight.Normal,
                            fontSize = 11.5.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            when (selectedSectionIndex) {
                0 -> {
                    // TAB 0: Visual Privacy & Site Data Dashboard
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
                }
                1 -> {
                    // TAB 1: Password & Credential Manager
                    PasswordManagerView(
                        savedCredentials = savedCredentials,
                        isAutoSaveEnabled = isAutoSaveCredentialsEnabled,
                        onToggleAutoSave = onToggleAutoSaveCredentials,
                        onSaveCredential = onSaveManualCredential,
                        onDeleteCredential = onDeleteSavedCredential,
                        onClearAllCredentials = onClearAllSavedCredentials
                    )
                }
                2 -> {
                    // TAB 2: Personalization (Wallpapers, Search Engines)
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
                                        "Fondo de Pantalla de Inicio",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Elige el estilo visual que deseas ver en la pantalla principal:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    WallpaperOption.allOptions.forEach { option ->
                                        val isSelected = activeWallpaper.id == option.id
                                        Column(
                                            horizontalAlignment = Alignment.CenterHorizontally,
                                            modifier = Modifier
                                                .clickable { onSelectWallpaper(option) }
                                                .testTag("wallpaper_option_${option.id}")
                                        ) {
                                            Box(
                                                modifier = Modifier
                                                    .size(width = 80.dp, height = 110.dp)
                                                    .clip(RoundedCornerShape(12.dp))
                                                    .background(option.previewGradient)
                                                    .border(
                                                        width = if (isSelected) 2.5.dp else 1.dp,
                                                        color = if (isSelected) Color(0xFF818CF8) else Color(0x33FFFFFF),
                                                        shape = RoundedCornerShape(12.dp)
                                                    )
                                            ) {
                                                if (isSelected) {
                                                    Box(
                                                        modifier = Modifier
                                                            .align(Alignment.TopEnd)
                                                            .padding(6.dp)
                                                            .size(20.dp)
                                                            .clip(CircleShape)
                                                            .background(Color(0xFF6366F1)),
                                                        contentAlignment = Alignment.Center
                                                    ) {
                                                        Icon(
                                                            imageVector = Icons.Default.Check,
                                                            contentDescription = "Seleccionado",
                                                            tint = Color.White,
                                                            modifier = Modifier.size(12.dp)
                                                        )
                                                    }
                                                }
                                            }
                                            Spacer(modifier = Modifier.height(6.dp))
                                            Text(
                                                text = option.name,
                                                color = if (isSelected) Color(0xFFA5B4FC) else Color(0xFFCBD5E1),
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                                fontSize = 11.5.sp
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
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White,
                                            fontSize = 15.sp
                                        )
                                    )
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    "Las consultas de la barra de direcciones usarán este motor:",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = Color(0xFF94A3B8),
                                        fontSize = 12.sp
                                    )
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                SearchEngine.entries.forEach { engine ->
                                    val isSelected = activeEngine == engine
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(if (isSelected) Color(0xFF312E81) else Color(0xFF131824))
                                            .border(
                                                1.dp,
                                                if (isSelected) Color(0xFF818CF8) else Color(0x22FFFFFF),
                                                RoundedCornerShape(12.dp)
                                            )
                                            .clickable { onSelectSearchEngine(engine) }
                                            .padding(horizontal = 14.dp, vertical = 12.dp)
                                            .testTag("search_engine_${engine.name}"),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column {
                                            Text(
                                                text = engine.displayName,
                                                color = Color.White,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 14.sp
                                            )
                                            Text(
                                                text = engine.searchUrlTemplate.substringBefore("?"),
                                                color = Color(0xFF94A3B8),
                                                fontSize = 11.sp
                                            )
                                        }

                                        if (isSelected) {
                                            Box(
                                                modifier = Modifier
                                                    .size(22.dp)
                                                    .clip(CircleShape)
                                                    .background(Color(0xFF6366F1)),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.Check,
                                                    contentDescription = "Activo",
                                                    tint = Color.White,
                                                    modifier = Modifier.size(14.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // 3. Quick Clear Button
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
            }

            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
