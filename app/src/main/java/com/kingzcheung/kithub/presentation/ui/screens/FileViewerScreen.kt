package com.kingzcheung.kithub.presentation.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import com.kingzcheung.kithub.domain.model.ContentType
import com.kingzcheung.kithub.presentation.viewmodel.FileViewerViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FileViewerScreen(
    viewModel: FileViewerViewModel = hiltViewModel()
) {
    val state by viewModel.state.collectAsState()
    
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
                    IconButton(onClick = {}) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
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
        } else if (state.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.Error,
                        contentDescription = "Error",
                        modifier = Modifier.size(48.dp),
                        tint = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = state.error!!,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadFile() }) {
                        Text("Retry")
                    }
                }
            }
        } else if (state.content != null && state.fileContent != null) {
            FileViewerContent(
                content = state.content!!,
                fileContent = state.fileContent!!,
                paddingValues = paddingValues
            )
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
                 fileName.endsWith(".jpeg") || fileName.endsWith(".gif") || fileName.endsWith(".webp")
    val isCode = !isMarkdown && !isImage
    
    if (isImage && content.downloadUrl != null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentAlignment = Alignment.Center
        ) {
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = WebViewClient()
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        loadDataWithBaseURL(
                            null,
                            "<html><body style='display:flex;justify-content:center;align-items:center;min-height:100%;background:#1e1e1e;'><img src='${content.downloadUrl}' style='max-width:100%;max-height:100%;object-fit:contain;'/></body></html>",
                            "text/html",
                            "UTF-8",
                            null
                        )
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    } else if (isMarkdown) {
        val context = LocalContext.current
        AndroidView(
            factory = { ctx ->
                WebView(ctx).apply {
                    webViewClient = WebViewClient()
                    settings.javaScriptEnabled = true
                    val htmlContent = generateMarkdownHtml(fileContent)
                    loadDataWithBaseURL(null, htmlContent, "text/html", "UTF-8", null)
                }
            },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        Icons.Default.Code,
                        contentDescription = "Code",
                        tint = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${content.name} • ${content.size} bytes",
                        style = MaterialTheme.typography.labelMedium
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    text = fileContent,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = FontFamily.Monospace
                    ),
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

fun generateMarkdownHtml(markdown: String): String {
    return """
        <!DOCTYPE html>
        <html>
        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <style>
                body {
                    font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Oxygen, Ubuntu, sans-serif;
                    background-color: #1e1e1e;
                    color: #e1e1e1;
                    padding: 16px;
                    line-height: 1.6;
                }
                h1, h2, h3, h4, h5, h6 {
                    color: #58a6ff;
                    margin-top: 24px;
                    margin-bottom: 16px;
                }
                h1 { font-size: 2em; border-bottom: 1px solid #30363d; }
                h2 { font-size: 1.5em; border-bottom: 1px solid #30363d; }
                h3 { font-size: 1.25em; }
                code {
                    background-color: #30363d;
                    padding: 2px 6px;
                    border-radius: 6px;
                    font-family: 'Fira Code', monospace;
                }
                pre {
                    background-color: #161b22;
                    padding: 16px;
                    border-radius: 6px;
                    overflow-x: auto;
                }
                pre code {
                    background-color: transparent;
                    padding: 0;
                }
                a {
                    color: #58a6ff;
                    text-decoration: none;
                }
                a:hover {
                    text-decoration: underline;
                }
                blockquote {
                    border-left: 4px solid #30363d;
                    padding-left: 16px;
                    color: #8b949e;
                }
                table {
                    border-collapse: collapse;
                    width: 100%;
                }
                th, td {
                    border: 1px solid #30363d;
                    padding: 8px;
                }
                img {
                    max-width: 100%;
                }
            </style>
        </head>
        <body>
            ${simpleMarkdownToHtml(markdown)}
        </body>
        </html>
    """.trimIndent()
}

fun simpleMarkdownToHtml(markdown: String): String {
    var html = markdown
        .replace(Regex("""^### (.*)$"""), "<h3>$1</h3>")
        .replace(Regex("""^## (.*)$"""), "<h2>$1</h2>")
        .replace(Regex("""^# (.*)$"""), "<h1>$1</h1>")
        .replace(Regex("""\*\*(.*?)\*\*"""), "<strong>$1</strong>")
        .replace(Regex("""\*(.*?)\*"""), "<em>$1</em>")
        .replace(Regex("""`([^`]+)`"""), "<code>$1</code>")
        .replace(Regex("""\[([^\]]+)\]\(([^)]+)\)"""), "<a href='$2'>$1</a>")
        .replace(Regex("""^> (.*)$"""), "<blockquote>$1</blockquote>")
        .replace("\n\n", "</p><p>")
        .replace("\n", "<br>")
    
    return "<p>$html</p>"
}