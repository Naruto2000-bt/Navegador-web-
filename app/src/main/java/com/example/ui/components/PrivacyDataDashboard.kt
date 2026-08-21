package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.Cookie
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.entity.CacheClearLogEntity
import com.example.model.CookiePolicy
import com.example.model.SiteDataInfo
import com.example.model.StorageBreakdown

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PrivacyDataDashboard(
    storageBreakdown: StorageBreakdown,
    cookiePolicy: CookiePolicy,
    siteDataList: List<SiteDataInfo>,
    clearLogs: List<CacheClearLogEntity>,
    onSelectCookiePolicy: (CookiePolicy) -> Unit,
    onToggleSiteCookie: (domain: String, allow: Boolean) -> Unit,
    onToggleSiteThirdParty: (domain: String, allow: Boolean) -> Unit,
    onClearSiteData: (domain: String) -> Unit,
    onAddSiteException: (domain: String, allowCookies: Boolean, allowThirdParty: Boolean) -> Unit,
    onExecuteClean: (cookies: Boolean, cache: Boolean, history: Boolean, timeRange: String) -> Unit,
    onClearLogs: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }

    // Cleaning presets state
    var selectedTimeRange by remember { mutableStateOf("ALL") }
    var cleanCache by remember { mutableStateOf(true) }
    var cleanCookies by remember { mutableStateOf(true) }
    var cleanHistory by remember { mutableStateOf(false) }
    var showCleanSuccess by remember { mutableStateOf(false) }

    val filteredSites = remember(siteDataList, searchQuery) {
        if (searchQuery.isBlank()) siteDataList
        else siteDataList.filter { it.domain.contains(searchQuery.trim(), ignoreCase = true) }
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {

        // --- 1. Visual Storage Gauge & Metrics Cards ---
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xFF1B2232)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = null,
                            tint = Color(0xFF818CF8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Almacenamiento Web y Privacidad",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFF312E81).copy(alpha = 0.6f))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = storageBreakdown.formattedTotal,
                            color = Color(0xFFA5B4FC),
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Multi-segment Visual Storage Bar
                val total = storageBreakdown.totalBytes.coerceAtLeast(1L).toFloat()
                val cacheWeight = (storageBreakdown.cacheBytes / total).coerceIn(0.05f, 0.9f)
                val cookieWeight = (storageBreakdown.cookiesBytes / total).coerceIn(0.05f, 0.9f)
                val siteWeight = (storageBreakdown.siteDataBytes / total).coerceIn(0.05f, 0.9f)

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .clip(RoundedCornerShape(6.dp))
                        .background(Color(0xFF0F172A))
                ) {
                    Box(
                        modifier = Modifier
                            .weight(cacheWeight)
                            .fillMaxWidth()
                            .background(Color(0xFF818CF8))
                    )
                    Box(
                        modifier = Modifier
                            .weight(cookieWeight)
                            .fillMaxWidth()
                            .background(Color(0xFF34D399))
                    )
                    Box(
                        modifier = Modifier
                            .weight(siteWeight)
                            .fillMaxWidth()
                            .background(Color(0xFFFBBF24))
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Storage Legend Cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    StorageStatCard(
                        title = "Caché",
                        value = storageBreakdown.formattedCache,
                        color = Color(0xFF818CF8),
                        modifier = Modifier.weight(1f)
                    )
                    StorageStatCard(
                        title = "Cookies",
                        value = storageBreakdown.formattedCookies,
                        color = Color(0xFF34D399),
                        modifier = Modifier.weight(1f)
                    )
                    StorageStatCard(
                        title = "Datos Sitio",
                        value = storageBreakdown.formattedSiteData,
                        color = Color(0xFFFBBF24),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        // --- 2. Global Cookie Policy Selector ---
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xFF1B2232)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Cookie,
                        contentDescription = null,
                        tint = Color(0xFF34D399),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Permisos Globales de Cookies",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Controla cómo los sitios web almacenan información en tu dispositivo:",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    CookiePolicy.entries.forEach { policy ->
                        val isSelected = cookiePolicy == policy
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(12.dp))
                                .background(if (isSelected) Color(0xFF1E293B) else Color(0xFF131824))
                                .border(
                                    width = if (isSelected) 1.5.dp else 1.dp,
                                    color = if (isSelected) Color(0xFF34D399) else Color(0x22FFFFFF),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onSelectCookiePolicy(policy) }
                                .padding(12.dp)
                                .testTag("cookie_policy_${policy.name.lowercase()}"),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(20.dp)
                                    .clip(CircleShape)
                                    .border(
                                        width = 2.dp,
                                        color = if (isSelected) Color(0xFF34D399) else Color(0xFF64748B),
                                        shape = CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isSelected) {
                                    Box(
                                        modifier = Modifier
                                            .size(10.dp)
                                            .clip(CircleShape)
                                            .background(Color(0xFF34D399))
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = policy.title,
                                    color = Color.White,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 13.sp
                                )
                                Text(
                                    text = policy.description,
                                    color = Color(0xFF94A3B8),
                                    fontSize = 11.sp
                                )
                            }
                        }
                    }
                }
            }
        }

        // --- 3. Interactive Quick Cleaning Presets ---
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xFF1B2232)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CleaningServices,
                        contentDescription = null,
                        tint = Color(0xFFF43F5E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ejecutar Limpieza Selectiva",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Time Range Chips
                Text(
                    text = "Intervalo de tiempo:",
                    color = Color(0xFFCBD5E1),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.height(6.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf("1H" to "Última hora", "24H" to "24 Horas", "ALL" to "Desde siempre").forEach { (code, label) ->
                        val selected = selectedTimeRange == code
                        FilterChip(
                            selected = selected,
                            onClick = { selectedTimeRange = code },
                            label = { Text(label, fontSize = 11.sp, fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF4F46E5),
                                selectedLabelColor = Color.White,
                                containerColor = Color(0xFF131824),
                                labelColor = Color(0xFF94A3B8)
                            ),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                // Checkboxes for Data Types
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { cleanCache = !cleanCache }
                    ) {
                        Checkbox(
                            checked = cleanCache,
                            onCheckedChange = { cleanCache = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF818CF8))
                        )
                        Text("Caché", color = Color.White, fontSize = 12.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { cleanCookies = !cleanCookies }
                    ) {
                        Checkbox(
                            checked = cleanCookies,
                            onCheckedChange = { cleanCookies = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFF34D399))
                        )
                        Text("Cookies", color = Color.White, fontSize = 12.sp)
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { cleanHistory = !cleanHistory }
                    ) {
                        Checkbox(
                            checked = cleanHistory,
                            onCheckedChange = { cleanHistory = it },
                            colors = CheckboxDefaults.colors(checkedColor = Color(0xFFFBBF24))
                        )
                        Text("Historial", color = Color.White, fontSize = 12.sp)
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        onExecuteClean(cleanCookies, cleanCache, cleanHistory, selectedTimeRange)
                        showCleanSuccess = true
                    },
                    enabled = cleanCache || cleanCookies || cleanHistory,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFE11D48)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(44.dp)
                        .testTag("execute_cleanup_button")
                ) {
                    Icon(Icons.Default.CleaningServices, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Limpiar Datos Seleccionados", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }

                AnimatedVisibility(visible = showCleanSuccess) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color(0xFF065F46).copy(alpha = 0.5f))
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF34D399), modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("¡Limpieza registrada en el historial con éxito!", color = Color(0xFF6EE7B7), fontSize = 11.sp)
                    }
                }
            }
        }

        // --- 4. Site Data Manager & Per-Site Permissions ---
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xFF1B2232)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.DataUsage,
                            contentDescription = null,
                            tint = Color(0xFF38BDF8),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Datos y Reglas por Sitio Web",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    IconButton(
                        onClick = { showAddDialog = true },
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF312E81))
                            .testTag("add_site_exception_button")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Añadir sitio", tint = Color.White, modifier = Modifier.size(18.dp))
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Administra las cookies, almacenamiento y excepciones de cada dominio:",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Search Bar for Domains
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    placeholder = { Text("Buscar dominio (ej. google.com)", color = Color(0xFF64748B), fontSize = 12.sp) },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = Color(0xFF94A3B8), modifier = Modifier.size(18.dp)) },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Color(0xFF131824),
                        unfocusedContainerColor = Color(0xFF131824),
                        focusedBorderColor = Color(0xFF818CF8),
                        unfocusedBorderColor = Color(0x33FFFFFF),
                        focusedTextColor = Color.White,
                        unfocusedTextColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (filteredSites.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No se encontraron sitios coincidentes", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        filteredSites.take(8).forEach { site ->
                            SiteDataItemCard(
                                site = site,
                                onToggleCookie = { allow -> onToggleSiteCookie(site.domain, allow) },
                                onToggleThirdParty = { allow -> onToggleSiteThirdParty(site.domain, allow) },
                                onClearSite = { onClearSiteData(site.domain) }
                            )
                        }
                    }
                }
            }
        }

        // --- 5. Cache & Cleaning History Timeline ---
        GlassSurface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            backgroundColor = Color(0xFF1B2232)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = null,
                            tint = Color(0xFFA78BFA),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Historial de Limpiezas de Caché",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp
                        )
                    }

                    if (clearLogs.isNotEmpty()) {
                        TextButton(
                            onClick = onClearLogs,
                            modifier = Modifier.testTag("clear_clean_logs_button")
                        ) {
                            Text("Vaciar Registro", color = Color(0xFFF43F5E), fontSize = 11.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Registro cronológico de espacio liberado y operaciones de mantenimiento:",
                    color = Color(0xFF94A3B8),
                    fontSize = 12.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                if (clearLogs.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No hay registros de limpieza recientes", color = Color(0xFF64748B), fontSize = 12.sp)
                    }
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        clearLogs.take(6).forEach { log ->
                            CleanLogCard(log = log)
                        }
                    }
                }
            }
        }
    }

    // Dialog to add custom site exception
    if (showAddDialog) {
        var domainInput by remember { mutableStateOf("") }
        var allowCookieInput by remember { mutableStateOf(true) }
        var allowThirdPartyInput by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { showAddDialog = false },
            title = { Text("Añadir Excepción de Sitio", color = Color.White, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(
                        value = domainInput,
                        onValueChange = { domainInput = it },
                        label = { Text("Dominio (ej. ejemplo.com)") },
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Permitir Cookies", color = Color.White, fontSize = 13.sp)
                        Switch(
                            checked = allowCookieInput,
                            onCheckedChange = { allowCookieInput = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF34D399))
                        )
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("Permitir de Terceros", color = Color.White, fontSize = 13.sp)
                        Switch(
                            checked = allowThirdPartyInput,
                            onCheckedChange = { allowThirdPartyInput = it },
                            colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF818CF8))
                        )
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (domainInput.isNotBlank()) {
                            onAddSiteException(domainInput, allowCookieInput, allowThirdPartyInput)
                            showAddDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))
                ) {
                    Text("Guardar Regla")
                }
            },
            dismissButton = {
                TextButton(onClick = { showAddDialog = false }) {
                    Text("Cancelar", color = Color(0xFF94A3B8))
                }
            },
            containerColor = Color(0xFF1E293B)
        )
    }
}

@Composable
private fun StorageStatCard(
    title: String,
    value: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131824))
            .border(1.dp, Color(0x22FFFFFF), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(color)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = title, color = Color(0xFF94A3B8), fontSize = 11.sp)
        }
        Spacer(modifier = Modifier.height(4.dp))
        Text(text = value, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 13.sp)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SiteDataItemCard(
    site: SiteDataInfo,
    onToggleCookie: (Boolean) -> Unit,
    onToggleThirdParty: (Boolean) -> Unit,
    onClearSite: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(Color(0xFF131824))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(14.dp))
            .clickable { isExpanded = !isExpanded }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f)) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF312E81)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = site.domain.take(1).uppercase(),
                        color = Color(0xFFA5B4FC),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Column {
                    Text(
                        text = site.domain,
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "${site.formattedStorage} • ${site.cookieCount} cookies",
                        color = Color(0xFF94A3B8),
                        fontSize = 11.sp
                    )
                }
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onClearSite,
                    modifier = Modifier
                        .size(30.dp)
                        .testTag("clear_site_${site.domain}")
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Limpiar datos de este sitio",
                        tint = Color(0xFFF43F5E),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Expanded Per-site rules
        AnimatedVisibility(visible = isExpanded) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp)
                    .border(1.dp, Color(0x15FFFFFF), RoundedCornerShape(10.dp))
                    .padding(10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Permitir Cookies Propias", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                    Switch(
                        checked = site.cookiesAllowed,
                        onCheckedChange = onToggleCookie,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF34D399))
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Permitir Cookies de Terceros", color = Color(0xFFE2E8F0), fontSize = 12.sp)
                    Switch(
                        checked = site.thirdPartyAllowed,
                        onCheckedChange = onToggleThirdParty,
                        colors = SwitchDefaults.colors(checkedThumbColor = Color(0xFF818CF8))
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun CleanLogCard(log: CacheClearLogEntity) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF131824))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(12.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = log.title,
                color = Color.White,
                fontWeight = FontWeight.SemiBold,
                fontSize = 12.sp
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = log.formattedDate,
                color = Color(0xFF64748B),
                fontSize = 10.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                if (log.clearedCache) {
                    BadgeChip(text = "Caché", color = Color(0xFF818CF8))
                }
                if (log.clearedCookies) {
                    BadgeChip(text = "Cookies", color = Color(0xFF34D399))
                }
                if (log.clearedHistory) {
                    BadgeChip(text = "Historial", color = Color(0xFFFBBF24))
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(Color(0xFF065F46).copy(alpha = 0.4f))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text(
                text = "-${log.formattedFreed}",
                color = Color(0xFF34D399),
                fontWeight = FontWeight.Bold,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun BadgeChip(text: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(text = text, color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
    }
}
