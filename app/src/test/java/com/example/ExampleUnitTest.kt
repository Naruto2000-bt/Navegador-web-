package com.example

import com.example.extensions.TranslationEngine
import com.example.model.TranslationLanguages
import org.junit.Assert.*
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun translationLanguages_findKnownLanguage() {
    val spanish = TranslationLanguages.find("es")
    assertEquals("Español", spanish.name)
    assertEquals("🇪🇸", spanish.flagEmoji)

    val english = TranslationLanguages.find("en-US")
    assertEquals("Inglés", english.name)

    val japanese = TranslationLanguages.find("ja")
    assertEquals("Japonés", japanese.name)
  }

  @Test
  fun translationEngine_generatesScripts() {
    val translateScript = TranslationEngine.getFastTranslateScript("en", "fr")
    assertTrue(translateScript.contains("fr"))
    assertTrue(translateScript.contains("__auraNodes"))

    val revertScript = TranslationEngine.getRevertScript()
    assertTrue(revertScript.contains("__auraOrig"))
  }

  @Test
  fun wallpaperOptions_hasDefaults() {
    val aurora = com.example.model.WallpaperOption.fromId("aurora")
    assertEquals("Aurora Boreal", aurora.name)
    assertFalse(aurora.isImage)

    val options = com.example.model.WallpaperOption.allOptions
    assertTrue(options.isNotEmpty())
  }

  @Test
  fun incognitoTab_initializesCorrectly() {
    val incognitoTab = com.example.model.BrowserTab(isIncognito = true)
    assertTrue(incognitoTab.isIncognito)
    assertTrue(incognitoTab.isHomePage)

    val normalTab = com.example.model.BrowserTab(isIncognito = false)
    assertFalse(normalTab.isIncognito)
  }
}

