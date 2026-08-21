package com.example

import android.os.Bundle
import java.util.Locale
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.model.BrowserTab
import com.example.ui.components.BrowserBottomBar
import com.example.ui.screens.BookmarksHistorySheet
import com.example.ui.screens.BrowserScreen
import com.example.ui.screens.ExtensionEditorSheet
import com.example.ui.screens.ExtensionPagePopupSheet
import com.example.ui.screens.ExtensionsManagerSheet
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.PrivacyInfoSheet
import com.example.ui.screens.SettingsSheet
import com.example.ui.screens.TabsOverviewSheet
import com.example.ui.theme.MyApplicationTheme
import com.example.viewmodel.BrowserViewModel
import com.example.viewmodel.WebViewAction
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val esLocale = Locale("es", "ES")
        Locale.setDefault(esLocale)
        val config = resources.configuration
        config.setLocale(esLocale)
        @Suppress("DEPRECATION")
        resources.updateConfiguration(config, resources.displayMetrics)

        enableEdgeToEdge()
        setContent {
            MyApplicationTheme(darkTheme = true) {
                AuraBrowserApp()
            }
        }
    }
}

@Composable
fun AuraBrowserApp(
    viewModel: BrowserViewModel = viewModel()
) {
    val tabs by viewModel.tabs.collectAsState()
    val activeTabId by viewModel.activeTabId.collectAsState()
    val extensions by viewModel.extensions.collectAsState()
    val bookmarks by viewModel.bookmarks.collectAsState()
    val history by viewModel.history.collectAsState()
    val wallpaper by viewModel.selectedWallpaper.collectAsState()
    val searchEngine by viewModel.searchEngine.collectAsState()
    val shortcuts by viewModel.shortcuts.collectAsState()
    val blockedAdsCount by viewModel.blockedAdsSessionCount.collectAsState()
    val storageBreakdown by viewModel.storageBreakdown.collectAsState()
    val cookiePolicy by viewModel.globalCookiePolicy.collectAsState()
    val siteDataList by viewModel.siteDataList.collectAsState()
    val cacheClearLogs by viewModel.cacheClearLogs.collectAsState()
    val savedCredentials by viewModel.savedCredentials.collectAsState()
    val isAutoSaveCredentialsEnabled by viewModel.isAutoSaveCredentialsEnabled.collectAsState()
    val pendingCredentialSave by viewModel.pendingCredentialSave.collectAsState()
    val activeAutofillSuggestion by viewModel.activeAutofillSuggestion.collectAsState()

    // Dialog & Sheet States
    val showTabsOverview by viewModel.showTabsOverview.collectAsState()
    val showExtensionsManager by viewModel.showExtensionsManager.collectAsState()
    val editingExtension by viewModel.editingExtension.collectAsState()
    val showBookmarksHistory by viewModel.showBookmarksHistory.collectAsState()
    val showSettings by viewModel.showSettings.collectAsState()
    val showExtensionPagePopup by viewModel.showExtensionPagePopup.collectAsState()
    val showPrivacyInfo by viewModel.showPrivacyInfo.collectAsState()
    val showFindInPage by viewModel.showFindInPage.collectAsState()
    val findQuery by viewModel.findQuery.collectAsState()
    val pageTranslationState by viewModel.pageTranslationState.collectAsState()
    val currentVideoSpeed by viewModel.currentVideoSpeed.collectAsState()
    val webViewAction by viewModel.webViewAction.collectAsState()

    val currentTab = remember(tabs, activeTabId) {
        tabs.find { it.id == activeTabId } ?: tabs.firstOrNull() ?: BrowserTab()
    }
    val isBookmarked = viewModel.isUrlBookmarked(currentTab.url)
    val enabledExtensionsCount = extensions.count { it.isEnabled }

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0D1117)),
        snackbarHost = { SnackbarHost(snackbarHostState) },
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .windowInsetsPadding(WindowInsets.statusBars)
        ) {
            // Main Display: Home Screen or Browser Screen
            if (currentTab.isHomePage) {
                HomeScreen(
                    modifier = Modifier.fillMaxSize(),
                    currentTab = currentTab,
                    wallpaper = wallpaper,
                    searchEngine = searchEngine,
                    shortcuts = shortcuts,
                    enabledExtensionsCount = enabledExtensionsCount,
                    blockedAdsCount = blockedAdsCount,
                    onNavigate = { query -> viewModel.navigateTo(query) },
                    onSelectSearchEngine = { engine -> viewModel.setSearchEngine(engine) },
                    onAddShortcut = { title, url -> viewModel.addCustomShortcut(title, url) },
                    onRemoveShortcut = { shortcut -> viewModel.removeCustomShortcut(shortcut) },
                    onOpenExtensionsManager = { viewModel.setExtensionsManagerVisible(true) },
                    onOpenBookmarksHistory = { viewModel.setBookmarksHistoryVisible(true) },
                    onOpenSettings = { viewModel.setSettingsVisible(true) },
                    onOpenPrivacyInfo = { viewModel.setPrivacyInfoVisible(true) },
                    onOpenNormalTab = { viewModel.openNewTab(isIncognito = false) }
                )
            } else {
                BrowserScreen(
                    modifier = Modifier.fillMaxSize(),
                    currentTab = currentTab,
                    extensions = extensions,
                    webViewAction = webViewAction,
                    showFindInPage = showFindInPage,
                    findQuery = findQuery,
                    translationState = pageTranslationState,
                    pendingCredentialSave = pendingCredentialSave,
                    activeAutofillSuggestion = activeAutofillSuggestion,
                    onSavePendingCredential = { pending ->
                        viewModel.savePendingCredential(pending)
                        coroutineScope.launch {
                            snackbarHostState.showSnackbar("Contraseña guardada para ${pending.domain}")
                        }
                    },
                    onDismissPendingCredential = { viewModel.dismissPendingCredential() },
                    onAutofillCredential = { cred -> viewModel.triggerAutofill(cred) },
                    onDismissAutofillSuggestion = { viewModel.dismissAutofillSuggestion() },
                    onCredentialsDetected = { domain, url, user, pass ->
                        viewModel.onLoginCredentialsDetected(domain, url, user, pass)
                    },
                    onCheckForAutofill = { url -> viewModel.checkForAutofillSuggestion(url) },
                    onFindQueryChange = { q -> viewModel.setFindQuery(q) },
                    onCloseFindInPage = { viewModel.setFindInPageVisible(false) },
                    onTranslate = { targetLang -> viewModel.translatePage(targetLang) },
                    onRevertTranslation = { viewModel.revertPageTranslation() },
                    onSelectSourceLang = { lang -> viewModel.setTranslationSource(lang) },
                    onSelectTargetLang = { lang -> viewModel.setTranslationTarget(lang) },
                    onDismissTranslation = { viewModel.hideTranslationBanner(currentTab.url) },
                    onLanguageDetected = { lang, url -> viewModel.onLanguageDetected(lang, url) },
                    onWebPageStarted = { url -> viewModel.onWebPageStarted(url) },
                    onTranslationFinished = { viewModel.finishTranslating() },
                    onUpdateTab = { url, title, fav, loading, prog, back, fwd, sec ->
                        viewModel.updateTabState(
                            tabId = currentTab.id,
                            url = url,
                            title = title,
                            faviconUrl = fav,
                            isLoading = loading,
                            progress = prog,
                            canGoBack = back,
                            canGoForward = fwd,
                            isSecure = sec
                        )
                    },
                    onNavigate = { query -> viewModel.navigateTo(query) },
                    onRecordHistory = { url, title -> viewModel.recordHistory(url, title) },
                    onClearWebViewAction = { viewModel.clearWebViewAction() },
                    onOpenExtensionPopup = { viewModel.setExtensionPagePopupVisible(true) },
                    onOpenPrivacyInfo = { viewModel.setPrivacyInfoVisible(true) },
                    onToggleExtension = { ext -> viewModel.toggleExtension(ext) },
                    onAdBlocked = { viewModel.incrementAdBlockCount() }
                )
            }

            // Minimalist Floating Bottom Bar
            BrowserBottomBar(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .windowInsetsPadding(WindowInsets.navigationBars),
                currentTab = currentTab,
                tabCount = tabs.size,
                enabledExtensionsCount = enabledExtensionsCount,
                isBookmarked = isBookmarked,
                onBack = { viewModel.triggerWebViewAction(WebViewAction.GoBack) },
                onForward = { viewModel.triggerWebViewAction(WebViewAction.GoForward) },
                onHome = { viewModel.goHome() },
                onOpenExtensionsManager = { viewModel.setExtensionsManagerVisible(true) },
                onOpenTabsOverview = { viewModel.setTabsOverviewVisible(true) },
                onOpenNewIncognitoTab = { viewModel.openIncognitoTab() },
                onToggleBookmark = {
                    viewModel.toggleBookmark(currentTab.url, currentTab.title)
                    coroutineScope.launch {
                        val msg = if (isBookmarked) "Marcador eliminado" else "Marcador guardado ★"
                        snackbarHostState.showSnackbar(msg)
                    }
                },
                onOpenBookmarksHistory = { viewModel.setBookmarksHistoryVisible(true) },
                onOpenSettings = { viewModel.setSettingsVisible(true) },
                onToggleDesktopMode = { viewModel.toggleDesktopMode() },
                onOpenFindInPage = { viewModel.setFindInPageVisible(true) },
                onTriggerReaderMode = { viewModel.triggerWebViewAction(WebViewAction.TriggerReaderMode) },
                onOpenTranslate = { viewModel.showTranslationBanner() },
                onOpenPrivacyInfo = { viewModel.setPrivacyInfoVisible(true) }
            )
        }

        // --- Sheets & Overlays ---

        // 1. Tabs Overview Sheet
        if (showTabsOverview) {
            TabsOverviewSheet(
                tabs = tabs,
                activeTabId = activeTabId,
                onSelectTab = { tabId -> viewModel.selectTab(tabId) },
                onCloseTab = { tabId -> viewModel.closeTab(tabId) },
                onNewTab = { viewModel.openNewTab() },
                onNewIncognitoTab = { viewModel.openIncognitoTab() },
                onCloseAllTabs = { viewModel.closeAllTabs() },
                onCloseAllIncognitoTabs = { viewModel.closeAllIncognitoTabs() },
                onDismiss = { viewModel.setTabsOverviewVisible(false) }
            )
        }

        // 2. Extensions Manager Sheet
        if (showExtensionsManager) {
            ExtensionsManagerSheet(
                extensions = extensions,
                onToggleExtension = { ext -> viewModel.toggleExtension(ext) },
                onEditExtension = { ext -> viewModel.setEditingExtension(ext) },
                onAddNewExtension = {
                    viewModel.setEditingExtension(
                        com.example.data.entity.ExtensionEntity(
                            id = 0L,
                            identifier = "",
                            name = "",
                            description = "",
                            scriptJs = ""
                        )
                    )
                },
                onDeleteExtension = { id -> viewModel.deleteExtension(id) },
                onDismiss = { viewModel.setExtensionsManagerVisible(false) }
            )
        }

        // 3. Extension Editor Sheet
        editingExtension?.let { ext ->
            ExtensionEditorSheet(
                extensionToEdit = if (ext.name.isEmpty() && ext.id == 0L) null else ext,
                onSave = { saved ->
                    viewModel.saveExtension(saved)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Extensión guardada con éxito")
                    }
                },
                onDismiss = { viewModel.setEditingExtension(null) }
            )
        }

        // 4. Extension Live Page Popup Sheet
        if (showExtensionPagePopup) {
            ExtensionPagePopupSheet(
                currentTab = currentTab,
                allExtensions = extensions,
                currentVideoSpeed = currentVideoSpeed,
                onToggleExtension = { ext -> viewModel.toggleExtension(ext) },
                onSetVideoSpeed = { speed -> viewModel.setVideoSpeed(speed) },
                onTriggerReaderMode = { viewModel.triggerWebViewAction(WebViewAction.TriggerReaderMode) },
                onTriggerPiP = { viewModel.triggerWebViewAction(WebViewAction.TriggerPiP) },
                onRunCustomScript = { code -> viewModel.triggerWebViewAction(WebViewAction.RunScript(code)) },
                onOpenFullManager = {
                    viewModel.setExtensionPagePopupVisible(false)
                    viewModel.setExtensionsManagerVisible(true)
                },
                onDismiss = { viewModel.setExtensionPagePopupVisible(false) }
            )
        }

        // 5. Privacy & Security Info Sheet (from Lock 🔒 or Menu 🛡️)
        if (showPrivacyInfo) {
            PrivacyInfoSheet(
                currentTab = currentTab,
                blockedAdsCount = blockedAdsCount,
                enabledExtensionsCount = enabledExtensionsCount,
                onOpenExtensionsManager = {
                    viewModel.setPrivacyInfoVisible(false)
                    viewModel.setExtensionsManagerVisible(true)
                },
                onOpenSettings = {
                    viewModel.setPrivacyInfoVisible(false)
                    viewModel.setSettingsVisible(true)
                },
                onDismiss = { viewModel.setPrivacyInfoVisible(false) }
            )
        }

        // 6. Bookmarks & History Sheet
        if (showBookmarksHistory) {
            BookmarksHistorySheet(
                bookmarks = bookmarks,
                history = history,
                onOpenUrl = { url -> viewModel.navigateTo(url) },
                onDeleteBookmark = { id -> viewModel.deleteBookmark(id) },
                onDeleteHistoryItem = { id -> viewModel.deleteHistoryItem(id) },
                onClearAllHistory = {
                    viewModel.clearAllHistory()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Historial borrado")
                    }
                },
                onDismiss = { viewModel.setBookmarksHistoryVisible(false) }
            )
        }

        // 7. Settings Sheet
        if (showSettings) {
            SettingsSheet(
                currentWallpaper = wallpaper,
                currentSearchEngine = searchEngine,
                storageBreakdown = storageBreakdown,
                cookiePolicy = cookiePolicy,
                siteDataList = siteDataList,
                clearLogs = cacheClearLogs,
                savedCredentials = savedCredentials,
                isAutoSaveCredentialsEnabled = isAutoSaveCredentialsEnabled,
                onToggleAutoSaveCredentials = { enabled -> viewModel.toggleAutoSaveCredentials(enabled) },
                onSaveManualCredential = { domain, url, user, pass ->
                    viewModel.saveCredentialManual(domain, url, user, pass)
                },
                onDeleteSavedCredential = { id -> viewModel.deleteSavedCredential(id) },
                onClearAllSavedCredentials = { viewModel.clearAllSavedCredentials() },
                onSelectWallpaper = { opt -> viewModel.setWallpaper(opt) },
                onSelectSearchEngine = { engine -> viewModel.setSearchEngine(engine) },
                onSelectCookiePolicy = { policy -> viewModel.setGlobalCookiePolicy(policy) },
                onToggleSiteCookie = { domain, allow -> viewModel.setSiteCookiePermission(domain, allow) },
                onToggleSiteThirdParty = { domain, allow -> viewModel.setSiteThirdPartyPermission(domain, allow) },
                onClearSiteData = { domain ->
                    viewModel.clearSiteData(domain)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Datos eliminados para $domain")
                    }
                },
                onAddSiteException = { domain, allowCookies, allowThirdParty ->
                    viewModel.addCustomSiteException(domain, allowCookies, allowThirdParty)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Regla guardada para $domain")
                    }
                },
                onExecuteClean = { cookies, cache, hist, timeRange ->
                    viewModel.clearBrowsingDataDetailed(cookies, cache, hist, timeRange)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Limpieza de datos ejecutada")
                    }
                },
                onClearLogs = {
                    viewModel.clearLogHistory()
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Historial de limpiezas vaciado")
                    }
                },
                onClearData = { cookies, cache, hist ->
                    viewModel.clearBrowsingData(cookies, cache, hist)
                    coroutineScope.launch {
                        snackbarHostState.showSnackbar("Datos de navegación limpiados")
                    }
                },
                onDismiss = { viewModel.setSettingsVisible(false) }
            )
        }
    }
}
