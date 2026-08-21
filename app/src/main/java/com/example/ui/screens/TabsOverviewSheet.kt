package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material.icons.filled.VpnKey
import androidx.compose.material.icons.rounded.Tab
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.BrowserTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TabsOverviewSheet(
    tabs: List<BrowserTab>,
    activeTabId: String,
    onSelectTab: (String) -> Unit,
    onCloseTab: (String) -> Unit,
    onNewTab: () -> Unit,
    onNewIncognitoTab: () -> Unit = {},
    onCloseAllTabs: () -> Unit,
    onCloseAllIncognitoTabs: () -> Unit = {},
    onDismiss: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    val currentActiveTab = tabs.find { it.id == activeTabId }
    var selectedCategoryIndex by remember(activeTabId) {
        mutableStateOf(if (currentActiveTab?.isIncognito == true) 1 else 0)
    }

    val normalTabs = tabs.filterNot { it.isIncognito }
    val incognitoTabs = tabs.filter { it.isIncognito }
    val isShowingIncognito = selectedCategoryIndex == 1
    val displayedTabs = if (isShowingIncognito) incognitoTabs else normalTabs

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = if (isShowingIncognito) Color(0xFF130E22) else Color(0xFF141926),
        dragHandle = null
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 18.dp)
        ) {
            // Header: Category Tabs (Normal vs Incognito) & Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Segmented Switcher for Normal vs Incognito
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(16.dp))
                        .background(Color(0xFF0F1420))
                        .padding(4.dp)
                ) {
                    // Normal Tabs Option
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (!isShowingIncognito) Color(0xFF6366F1) else Color.Transparent)
                            .clickable { selectedCategoryIndex = 0 }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("tabs_category_normal"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Tab,
                            contentDescription = null,
                            tint = if (!isShowingIncognito) Color.White else Color(0xFF94A3B8),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Normales (${normalTabs.size})",
                            color = if (!isShowingIncognito) Color.White else Color(0xFF94A3B8),
                            fontSize = 12.sp,
                            fontWeight = if (!isShowingIncognito) FontWeight.Bold else FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.width(4.dp))

                    // Incognito Tabs Option
                    Row(
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(if (isShowingIncognito) Color(0xFF7C3AED) else Color.Transparent)
                            .clickable { selectedCategoryIndex = 1 }
                            .padding(horizontal = 12.dp, vertical = 7.dp)
                            .testTag("tabs_category_incognito"),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.VisibilityOff,
                            contentDescription = null,
                            tint = if (isShowingIncognito) Color.White else Color(0xFFA78BFA),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Incógnito (${incognitoTabs.size})",
                            color = if (isShowingIncognito) Color.White else Color(0xFFA78BFA),
                            fontSize = 12.sp,
                            fontWeight = if (isShowingIncognito) FontWeight.Bold else FontWeight.Medium
                        )
                    }
                }

                // Action Buttons
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (isShowingIncognito) {
                        if (incognitoTabs.isNotEmpty()) {
                            IconButton(
                                onClick = onCloseAllIncognitoTabs,
                                modifier = Modifier.testTag("close_all_incognito_tabs_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Cerrar pestañas incógnito",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }

                        Button(
                            onClick = onNewIncognitoTab,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7C3AED)),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("new_incognito_tab_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Nueva",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("+ Incógnito", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    } else {
                        if (normalTabs.size > 1) {
                            IconButton(
                                onClick = onCloseAllTabs,
                                modifier = Modifier.testTag("close_all_tabs_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.DeleteSweep,
                                    contentDescription = "Cerrar todas",
                                    tint = Color(0xFFEF4444)
                                )
                            }
                        }

                        Button(
                            onClick = onNewTab,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6366F1)),
                            shape = RoundedCornerShape(16.dp),
                            contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                            modifier = Modifier.testTag("new_tab_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Nueva",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Nueva", color = Color.White, fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (displayedTabs.isEmpty()) {
                // Empty State for Tab Category
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(CircleShape)
                                .background(if (isShowingIncognito) Color(0x337C3AED) else Color(0x336366F1)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (isShowingIncognito) Icons.Default.VisibilityOff else Icons.Rounded.Tab,
                                contentDescription = null,
                                tint = if (isShowingIncognito) Color(0xFFA78BFA) else Color(0xFF818CF8),
                                modifier = Modifier.size(32.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = if (isShowingIncognito) "Sin pestañas de incógnito" else "Sin pestañas abiertas",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = if (isShowingIncognito) {
                                "Abre una pestaña en modo incógnito para navegar de forma totalmente privada sin guardar historial ni cookies."
                            } else {
                                "Abre una nueva pestaña para explorar la web."
                            },
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = Color(0xFF94A3B8),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                            )
                        )

                        Spacer(modifier = Modifier.height(18.dp))

                        Button(
                            onClick = if (isShowingIncognito) onNewIncognitoTab else onNewTab,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isShowingIncognito) Color(0xFF7C3AED) else Color(0xFF6366F1)
                            ),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                if (isShowingIncognito) "Abrir pestaña de incógnito" else "Abrir nueva pestaña",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            } else {
                // Tabs Grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    horizontalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(displayedTabs, key = { it.id }) { tab ->
                        val isActive = tab.id == activeTabId

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(20.dp))
                                .background(
                                    when {
                                        tab.isIncognito && isActive -> Color(0xFF281C45)
                                        tab.isIncognito -> Color(0xFF1E1535)
                                        isActive -> Color(0xFF232A3B)
                                        else -> Color(0xFF191F2D)
                                    }
                                )
                                .border(
                                    width = if (isActive) 2.dp else 1.dp,
                                    color = when {
                                        tab.isIncognito && isActive -> Color(0xFFA78BFA)
                                        tab.isIncognito -> Color(0x44A78BFA)
                                        isActive -> Color(0xFF818CF8)
                                        else -> Color(0x2BFFFFFF)
                                    },
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .clickable { onSelectTab(tab.id) }
                                .padding(12.dp)
                                .testTag("tab_card_${tab.id}")
                        ) {
                            Column {
                                // Top bar inside card: Favicon/Icon + Title + Close button
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        if (tab.isIncognito) {
                                            Icon(
                                                imageVector = Icons.Default.VisibilityOff,
                                                contentDescription = "Incógnito",
                                                tint = Color(0xFFA78BFA),
                                                modifier = Modifier.size(15.dp)
                                            )
                                        } else if (tab.isHomePage) {
                                            Icon(
                                                imageVector = Icons.Default.Home,
                                                contentDescription = "Inicio",
                                                tint = Color(0xFFA5B4FC),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        } else {
                                            Icon(
                                                imageVector = Icons.Default.Language,
                                                contentDescription = "Web",
                                                tint = Color(0xFF38BDF8),
                                                modifier = Modifier.size(16.dp)
                                            )
                                        }
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Text(
                                            text = if (tab.isHomePage) (if (tab.isIncognito) "Incógnito" else "Inicio") else tab.title,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontWeight = if (isActive) FontWeight.Bold else FontWeight.Medium,
                                                color = Color.White,
                                                fontSize = 12.sp
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }

                                    IconButton(
                                        onClick = { onCloseTab(tab.id) },
                                        modifier = Modifier.size(24.dp).testTag("close_tab_${tab.id}")
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = "Cerrar pestaña",
                                            tint = Color(0xFF94A3B8),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(10.dp))

                                // Preview Thumbnail / Content Placeholder Box
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(90.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                tab.isIncognito && tab.isHomePage -> Brush.linearGradient(
                                                    listOf(Color(0xFF3B1D66), Color(0xFF20123D))
                                                )
                                                tab.isIncognito -> Brush.linearGradient(
                                                    listOf(Color(0xFF281845), Color(0xFF150D26))
                                                )
                                                tab.isHomePage -> Brush.linearGradient(
                                                    listOf(Color(0xFF312E81), Color(0xFF1E1B4B))
                                                )
                                                else -> Brush.linearGradient(
                                                    listOf(Color(0xFF0F172A), Color(0xFF1E293B))
                                                )
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Column(
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        modifier = Modifier.padding(8.dp)
                                    ) {
                                        Text(
                                            text = if (tab.isIncognito && tab.isHomePage) "Pestaña Privada" else tab.displayHost,
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = if (tab.isIncognito) Color(0xFFE9D5FF) else Color(0xFFCBD5E1),
                                                fontSize = 11.sp,
                                                fontWeight = FontWeight.SemiBold
                                            ),
                                            maxLines = 1,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                        if (!tab.isHomePage && tab.isSecure) {
                                            Spacer(modifier = Modifier.height(4.dp))
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Icon(
                                                    imageVector = Icons.Default.Shield,
                                                    contentDescription = "Seguro",
                                                    tint = Color(0xFF34D399),
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Spacer(modifier = Modifier.width(3.dp))
                                                Text(
                                                    text = "Seguro",
                                                    color = Color(0xFF34D399),
                                                    fontSize = 10.sp
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
