package com.kingzcheung.kithub.presentation.ui.screens

import android.content.Intent
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.ContentType
import com.kingzcheung.kithub.presentation.viewmodel.FileViewerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileViewerScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: FileViewerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = state.content?.name ?: "File",
                        style = MaterialTheme.typography.titleMedium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {
                        state.content?.htmlUrl?.let { url ->
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "text/plain"
                                putExtra(Intent.EXTRA_SUBJECT, state.content?.name ?: "File")
                                putExtra(Intent.EXTRA_TEXT, url)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share file"))
                        }
                    }) {
                        Icon(Icons.Default.Share, contentDescription = "Share")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (state.loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (state.content != null) {
            val content = state.content!!
            val fileName = content.name.lowercase()
            val isBinary = isBinaryFile(fileName)
            
            if (isBinary) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.AutoMirrored.Filled.InsertDriveFile,
                            contentDescription = "Binary file",
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = content.name,
                            style = MaterialTheme.typography.titleMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Binary file • ${content.size} bytes",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        if (content.downloadUrl != null) {
                            Button(onClick = {}) {
                                Icon(Icons.Default.Download, contentDescription = null)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Download")
                            }
                        }
                    }
                }
            } else if (state.fileContent != null) {
                FileViewerContent(
                    content = content,
                    fileContent = state.fileContent!!,
                    paddingValues = paddingValues
                )
            }
        }
    }
}

@Composable
fun FileViewerContent(
    content: com.kingzcheung.kithub.domain.model.Content,
    fileContent: String,
    paddingValues: PaddingValues
) {
val fileName = content.name.lowercase()
    val isMarkdown = fileName.endsWith(".md") || fileName.endsWith(".markdown")
    val isImage = fileName.endsWith(".png") || fileName.endsWith(".jpg") || 
                 fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || fileName.endsWith("webp")
    val context = LocalContext.current
    
    if (isImage && content.downloadUrl != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            val webView = rememberWebView(context)
            
            DisposableEffect(webView) {
                webView.settings.loadWithOverviewMode = true
                webView.settings.useWideViewPort = true
                webView.loadDataWithBaseURL(
                    null,
                    "<html><body style='display:flex;justify-content:center;align-items:center;min-height:100%;background:#1e1e1e;'><img src='${content.downloadUrl}' style='max-width:100%;max-height:100%;object-fit:contain;'/></body></html>",
                    "text/html",
                    "UTF-8",
                    null
                )
                
                onDispose {
                    webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null)
                    webView.clearHistory()
                    webView.clearCache(true)
                    webView.onPause()
                    webView.removeAllViews()
                    webView.destroy()
                }
            }
            
            AndroidView(
                factory = { webView },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else if (isMarkdown) {
        val isDarkTheme = isSystemInDarkTheme()
        val webView = rememberWebView(context)
        
        DisposableEffect(webView, fileContent, isDarkTheme) {
            webView.settings.javaScriptEnabled = true
            webView.settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
            webView.clearCache(true)
            webView.clearHistory()
            val htmlContent = generateMarkdownHtml(fileContent, isDarkTheme)
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            
            onDispose {
                webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null)
                webView.clearHistory()
                webView.clearCache(true)
                webView.onPause()
                webView.removeAllViews()
                webView.destroy()
            }
        }
        
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    } else {
        val isDarkTheme = isSystemInDarkTheme()
        val webView = rememberWebView(context)
        
        DisposableEffect(webView, fileContent, content.name, isDarkTheme) {
            webView.settings.javaScriptEnabled = true
            webView.settings.cacheMode = android.webkit.WebSettings.LOAD_NO_CACHE
            webView.clearCache(true)
            webView.clearHistory()
            val htmlContent = generateCodeHtml(fileContent, content.name, isDarkTheme)
            webView.loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
            
            onDispose {
                webView.loadDataWithBaseURL(null, "", "text/html", "UTF-8", null)
                webView.clearHistory()
                webView.clearCache(true)
                webView.onPause()
                webView.removeAllViews()
                webView.destroy()
            }
        }
        
        AndroidView(
            factory = { webView },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun rememberWebView(context: android.content.Context): WebView {
    return remember {
        WebView(context.applicationContext).apply {
            webViewClient = WebViewClient()
        }
    }
}

fun generateMarkdownHtml(markdown: String, isDark: Boolean): String {
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
    
    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/$hljsTheme.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <style>
        body {
            font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
            background-color: $bgColor;
            color: $textColor;
            padding: 16px;
            line-height: 1.6;
        }
        h1, h2, h3, h4, h5, h6 {
            color: $linkColor;
            margin-top: 24px;
            margin-bottom: 16px;
        }
        h1 { font-size: 2em; border-bottom: 1px solid $borderColor; }
        h2 { font-size: 1.5em; border-bottom: 1px solid $borderColor; }
        h3 { font-size: 1.25em; }
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
        }
        pre code {
            background-color: transparent;
            padding: 0;
            font-size: 13px;
        }
        .hljs {
            background: transparent !important;
        }
        a {
            color: $linkColor;
            text-decoration: none;
        }
        a:hover {
            text-decoration: underline;
        }
        blockquote {
            border-left: 4px solid $borderColor;
            padding-left: 16px;
            color: $quoteColor;
            margin: 0;
        }
        table {
            border-collapse: collapse;
            width: 100%;
        }
        th, td {
            border: 1px solid $borderColor;
            padding: 8px;
        }
        img {
            max-width: 100%;
        }
        ul, ol {
            padding-left: 2em;
        }
        li {
            margin: 0.25em 0;
        }
    </style>
</head>
<body>
    ${simpleMarkdownToHtml(markdown)}
    <script>hljs.highlightAll();</script>
</body>
</html>
    """.trimIndent()
}

fun simpleMarkdownToHtml(markdown: String): String {
    var html = markdown
    
    // Handle code blocks first (```language ... ```)
    html = html.replace(Regex("""```(\w*)\n([\s\S]*?)```""")) { matchResult ->
        val lang = matchResult.groupValues[1]
        val code = matchResult.groupValues[2]
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        "<pre><code class='language-$lang'>$code</code></pre>"
    }
    
    // Handle inline code
    html = html.replace(Regex("""`([^`]+)`""")) { matchResult ->
        val code = matchResult.groupValues[1]
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
        "<code>$code</code>"
    }
    
    // Headers
    html = html.replace(Regex("""^#### (.*)$"""), "<h4>$1</h4>")
    html = html.replace(Regex("""^### (.*)$"""), "<h3>$1</h3>")
    html = html.replace(Regex("""^## (.*)$"""), "<h2>$1</h2>")
    html = html.replace(Regex("""^# (.*)$"""), "<h1>$1</h1>")
    
    // Bold and italic
    html = html.replace(Regex("""\*\*\*(.*?)\*\*\*"""), "<strong><em>$1</em></strong>")
    html = html.replace(Regex("""\*\*(.*?)\*\*"""), "<strong>$1</strong>")
    html = html.replace(Regex("""\*(.*?)\*"""), "<em>$1</em>")
    
    // Links
    html = html.replace(Regex("""\[([^\]]+)\]\(([^)]+)\)"""), "<a href='$2'>$1</a>")
    
    // Blockquotes
    html = html.replace(Regex("""^> (.*)$"""), "<blockquote>$1</blockquote>")
    
    // Lists
    html = html.replace(Regex("""^- (.*)$"""), "<li>$1</li>")
    html = html.replace(Regex("""(<li>.*</li>\n)+"""), "<ul>$0</ul>")
    
    // Paragraphs
    html = html.replace("\n\n", "</p><p>")
    html = html.replace("\n", "<br>")
    
    return "<p>$html</p>"
}

fun isBinaryFile(fileName: String): Boolean {
    val name = fileName.lowercase()
    val binaryExtensions = listOf(
        ".exe", ".dll", ".so", ".dylib", ".a", ".lib",
        ".zip", ".tar", ".gz", ".rar", ".7z", ".jar", ".war",
        ".pdf", ".doc", ".docx", ".xls", ".xlsx", ".ppt", ".pptx",
        ".mp3", ".mp4", ".avi", ".mov", ".wmv", ".flv", ".mkv",
        ".iso", ".dmg", ".apk", ".aab", ".ipa",
        ".class", ".jar", ".war", ".ear",
        ".png", ".jpg", ".jpeg", ".gif", ".bmp", ".ico", ".webp",
        ".sqlite", ".db", ".mdb",
        ".bin", ".dat"
    )
    val textExtensions = listOf(
        ".txt", ".md", ".markdown", ".json", ".xml", ".yaml", ".yml",
        ".kt", ".kts", ".java", ".py", ".js", ".ts", ".tsx", ".jsx",
        ".c", ".cpp", ".h", ".hpp", ".cs", ".go", ".rs", ".rb",
        ".php", ".html", ".css", ".scss", ".sass", ".less",
        ".sh", ".bash", ".zsh", ".bat", ".cmd", ".ps1",
        ".sql", ".gradle", ".properties", ".gitignore", ".dockerfile",
        ".toml", ".ini", ".cfg", ".conf", ".env"
    )
    
    return binaryExtensions.any { name.endsWith(it) } && 
           !textExtensions.any { name.endsWith(it) }
}

fun generateCodeHtml(code: String, fileName: String, isDark: Boolean): String {
    val language = getLanguageFromFileName(fileName)
    val escapedCode = code
        .replace("&", "&amp;")
        .replace("<", "&lt;")
        .replace(">", "&gt;")
        .replace("\"", "&quot;")
        .replace("'", "&#39;")
    
    val (bgColor, textColor, lineNumColor, hljsTheme) = if (isDark) {
        listOf("#0d1117", "#c9d1d9", "#8b949e", "github-dark")
    } else {
        listOf("#ffffff", "#24292f", "#6e7681", "github")
    }
    
    return """
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/styles/$hljsTheme.min.css">
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/highlightjs-line-numbers.js@2.8.0/dist/style.min.css">
    <script src="https://cdnjs.cloudflare.com/ajax/libs/highlight.js/11.9.0/highlight.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/highlightjs-line-numbers.js@2.8.0/dist/highlightjs-line-numbers.min.js"></script>
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }
        body {
            background-color: $bgColor;
            color: $textColor;
            font-family: 'Fira Code', 'JetBrains Mono', Consolas, Monaco, 'Courier New', monospace;
            padding: 10px;
            min-height: 100vh;
        }
        pre {
            margin: 0;
            padding: 0;
            background: transparent !important;
        }
        code {
            font-family: 'Fira Code', 'JetBrains Mono', Consolas, Monaco, 'Courier New', monospace;
            font-size: 13px;
            line-height: 1.6;
            background: transparent !important;
        }
        .hljs {
            background: transparent !important;
            padding: 0 !important;
        }
        .hljs-ln-numbers {
            text-align: right;
            color: $lineNumColor;
            user-select: none;
            padding: 0 16px 0 16px;
            border-right: 1px solid ${if (isDark) "#30363d" else "#d0d7de"};
            vertical-align: top;
        }
        .hljs-ln-code {
            padding-left: 24px;
            padding-right: 16px;
            vertical-align: top;
        }
        .hljs-ln-code .hljs-ln-line {
            margin: 0;
            padding: 0;
        }
    </style>
</head>
<body>
    <pre><code class="language-$language">$escapedCode</code></pre>
    <script>
        hljs.highlightAll();
        hljs.lineNumbersBlock();
    </script>
</body>
</html>
    """.trimIndent()
}

fun getLanguageFromFileName(fileName: String): String {
    val name = fileName.lowercase()
    return when {
        name.endsWith(".kt") || name.endsWith(".kts") -> "kotlin"
        name.endsWith(".java") -> "java"
        name.endsWith(".py") -> "python"
        name.endsWith(".js") -> "javascript"
        name.endsWith(".ts") -> "typescript"
        name.endsWith(".tsx") -> "typescript"
        name.endsWith(".jsx") -> "javascript"
        name.endsWith(".json") -> "json"
        name.endsWith(".xml") -> "xml"
        name.endsWith(".html") -> "html"
        name.endsWith(".css") -> "css"
        name.endsWith(".scss") || name.endsWith(".sass") -> "scss"
        name.endsWith(".yaml") || name.endsWith(".yml") -> "yaml"
        name.endsWith(".md") || name.endsWith(".markdown") -> "markdown"
        name.endsWith(".c") -> "c"
        name.endsWith(".cpp") || name.endsWith(".hpp") -> "cpp"
        name.endsWith(".h") -> "c"
        name.endsWith(".cs") -> "csharp"
        name.endsWith(".go") -> "go"
        name.endsWith(".rs") -> "rust"
        name.endsWith(".rb") -> "ruby"
        name.endsWith(".php") -> "php"
        name.endsWith(".sh") || name.endsWith(".bash") -> "bash"
        name.endsWith(".sql") -> "sql"
        name.endsWith(".gradle") -> "groovy"
        name.endsWith(".properties") -> "properties"
        name.endsWith(".toml") -> "toml"
        name.endsWith(".dockerfile") -> "dockerfile"
        name.endsWith(".gitignore") -> "gitignore"
        else -> "plaintext"
    }
}