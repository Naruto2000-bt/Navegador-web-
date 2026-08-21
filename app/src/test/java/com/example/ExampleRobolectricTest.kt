package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.model.SearchEngine
import com.example.model.WallpaperOption
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("Aura Web Browser", appName)
  }

  @Test
  fun `verify wallpaper options are available and non-empty`() {
    val options = WallpaperOption.allOptions
    assertTrue(options.isNotEmpty())
    options.forEach { option ->
      assertNotNull(option.id)
      assertNotNull(option.name)
      assertNotNull(option.previewGradient)
    }
  }

  @Test
  fun `verify search engines url generation`() {
    val googleUrl = SearchEngine.GOOGLE.buildUrl("test search")
    assertTrue(googleUrl.contains("google.com/search?q=test+search"))

    val directUrl = SearchEngine.GOOGLE.buildUrl("example.com")
    assertEquals("https://example.com", directUrl)
  }
}
