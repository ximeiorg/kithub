package com.kingzcheung.kithub.presentation.ui.screens

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import org.json.JSONObject

@Composable
fun ReadmeWebView(
    markdown: String,
    owner: String,
    repo: String,
    branch: String,
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val webViewHeight = remember { mutableStateOf(500.dp) }
    
    AndroidView(
        factory = { context ->
            WebView(context).apply {
                webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                        return false
                    }
                }
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                settings.cacheMode = android.webkit.WebSettings.LOAD_DEFAULT
                settings.loadsImagesAutomatically = true
                settings.blockNetworkImage = false
                settings.useWideViewPort = true
                settings.loadWithOverviewMode = true
                setInitialScale(100)
                isVerticalScrollBarEnabled = false
                isHorizontalScrollBarEnabled = false
                
                val htmlContent = generateReadmeHtml(markdown, isDark, owner, repo, branch)
                loadDataWithBaseURL("https://github.com/$owner/$repo", htmlContent, "text/html", "UTF-8", null)
            }
        },
        update = { webView ->
            val htmlContent = generateReadmeHtml(markdown, isDark, owner, repo, branch)
            webView.loadDataWithBaseURL("https://github.com/$owner/$repo", htmlContent, "text/html", "UTF-8", null)
        },
        modifier = modifier
    )
}

fun escapeJsonString(str: String): String {
    return JSONObject.quote(str).let { 
        if (it.length >= 2) it.substring(1, it.length - 1) else it 
    }
}

fun generateReadmeHtml(markdown: String, isDark: Boolean, owner: String, repo: String, branch: String): String {
    val colors = if (isDark) {
        listOf("#0d1117", "#c9d1d9", "#30363d", "#161b22", "#58a6ff", "#8b949e", "github-dark")
    } else {
        listOf("#ffffff", "#24292f", "#d0d7de", "#f6f8fa", "#0969da", "#6e7681", "github")
    }
    val bgColor = colors[0]
    val textColor = colors[1]
    val borderColor = colors[2]
    val codeBgColor = colors[3]
    val linkColor = colors[4]
    val quoteColor = colors[5]
    val hljsTheme = colors[6]
    val escapedMarkdown = escapeJsonString(markdown)
    val baseUrl = "https://raw.githubusercontent.com/$owner/$repo/$branch"
    
    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <script src="https://cdn.jsdelivr.net/npm/marked/marked.min.js"></script>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/$hljsTheme.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background-color: $bgColor;
            color: $textColor;
            padding: 16px;
            line-height: 1.6;
            margin: 0;
        }
        h1, h2, h3, h4, h5, h6 {
            color: $textColor;
            margin-top: 24px;
            margin-bottom: 16px;
        }
        h1 { font-size: 2em; border-bottom: 1px solid $borderColor; padding-bottom: 8px; }
        h2 { font-size: 1.5em; border-bottom: 1px solid $borderColor; padding-bottom: 8px; }
        h3 { font-size: 1.25em; }
        p { margin: 0 0 16px 0; }
        code {
            background-color: $borderColor;
            padding: 2px 6px;
            border-radius: 6px;
            font-family: 'Fira Code', 'JetBrains Mono', Consolas, Monaco, monospace;
            font-size: 0.9em;
        }
        pre {
            background-color: $codeBgColor;
            padding: 16px;
            border-radius: 6px;
            overflow-x: auto;
            margin: 0 0 16px 0;
        }
        pre code { background-color: transparent; padding: 0; font-size: 13px; }
        .hljs { background: transparent !important; }
        a { color: $linkColor; text-decoration: none; }
        a:hover { text-decoration: underline; }
        blockquote {
            border-left: 4px solid $borderColor;
            padding-left: 16px;
            color: $quoteColor;
            margin: 0 0 16px 0;
        }
        table { border-collapse: collapse; width: 100%; margin: 16px 0; }
        th, td { border: 1px solid $borderColor; padding: 8px; text-align: left; }
        img { max-width: 100%; height: auto; display: block; margin: 16px auto; }
        ul, ol { padding-left: 2em; margin: 0 0 16px 0; }
        li { margin: 0.25em 0; }
        hr { border: none; border-top: 1px solid $borderColor; margin: 24px 0; }
        #content { overflow-x: hidden; word-wrap: break-word; }
    </style>
</head>
<body>
    <div id="content"></div>
    <script>
        marked.setOptions({
            breaks: true,
            gfm: true
        });
        
        var markdown = "$escapedMarkdown";
        var baseUrl = "$baseUrl";
        
        // Fix relative image URLs
        markdown = markdown.replace(/<img[^>]+src="(?!http|\\/\\/)([^"]+)"/g, function(match, src) {
            var absoluteUrl = baseUrl + '/' + src;
            return match.replace(src, absoluteUrl);
        });
        markdown = markdown.replace(/src="(?!http|\\/\\/)([^"]+)"/g, function(match, src) {
            var absoluteUrl = baseUrl + '/' + src;
            return 'src="' + absoluteUrl + '"';
        });
        
        document.getElementById('content').innerHTML = marked.parse(markdown);
        hljs.highlightAll();
    </script>
</body>
</html>
""".trimIndent()
}