package com.example.extensions

import android.content.Context
import android.util.Log
import android.webkit.JavascriptInterface
import android.webkit.WebView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import java.net.HttpURLConnection
import java.net.URL
import java.net.URLEncoder

class AuraTranslateBridge(
    private val webView: WebView,
    private val scope: CoroutineScope,
    private val onTranslationFinished: (success: Boolean) -> Unit
) {

    @JavascriptInterface
    fun translateBatches(jsonBatch: String, sourceLang: String, targetLang: String) {
        scope.launch(Dispatchers.IO) {
            try {
                val inputArr = JSONArray(jsonBatch)
                val outputArr = JSONArray()

                for (i in 0 until inputArr.length()) {
                    val originalText = inputArr.getString(i)
                    val translatedText = fetchTranslation(originalText, sourceLang, targetLang)
                    outputArr.put(translatedText)
                }

                val safeJson = outputArr.toString().replace("\\", "\\\\").replace("\"", "\\\"").replace("'", "\\'")
                withContext(Dispatchers.Main) {
                    webView.evaluateJavascript(
                        "window.__auraApplyTranslatedBatch && window.__auraApplyTranslatedBatch('$safeJson');",
                        null
                    )
                    onTranslationFinished(true)
                }
            } catch (e: Exception) {
                Log.e("AuraTranslateBridge", "Translation failed", e)
                withContext(Dispatchers.Main) {
                    onTranslationFinished(false)
                }
            }
        }
    }

    private fun fetchTranslation(text: String, sourceLang: String, targetLang: String): String {
        if (text.isBlank()) return text
        val src = if (sourceLang == "auto" || sourceLang.isBlank()) "auto" else sourceLang
        val cleanTarget = if (targetLang == "zh" || targetLang == "zh-CN") "zh-CN" else targetLang

        return try {
            val encodedQuery = URLEncoder.encode(text, "UTF-8")
            val urlString = "https://translate.googleapis.com/translate_a/single?client=gtx&sl=$src&tl=$cleanTarget&dt=t&q=$encodedQuery"
            val url = URL(urlString)
            val connection = url.openConnection() as HttpURLConnection
            connection.requestMethod = "GET"
            connection.connectTimeout = 4000
            connection.readTimeout = 4000
            connection.setRequestProperty("User-Agent", "Mozilla/5.0 (Android; Mobile)")

            if (connection.responseCode == 200) {
                val responseText = connection.inputStream.bufferedReader().use { it.readText() }
                val rootArray = JSONArray(responseText)
                if (rootArray.length() > 0) {
                    val sentences = rootArray.getJSONArray(0)
                    val sb = StringBuilder()
                    for (j in 0 until sentences.length()) {
                        val sentence = sentences.getJSONArray(j)
                        sb.append(sentence.getString(0))
                    }
                    sb.toString()
                } else {
                    text
                }
            } else {
                text
            }
        } catch (e: Exception) {
            text
        }
    }
}
