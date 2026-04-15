package com.kingzcheung.kithub.presentation.ui.components

import android.content.Context
import android.graphics.Typeface
import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.material3.MaterialTheme
import io.noties.markwon.AbstractMarkwonPlugin
import io.noties.markwon.Markwon
import io.noties.markwon.MarkwonSpansFactory
import io.noties.markwon.core.MarkwonTheme
import io.noties.markwon.ext.tables.TablePlugin
import io.noties.markwon.image.coil.CoilImagesPlugin
import io.noties.markwon.html.HtmlPlugin
import io.noties.markwon.core.CorePlugin
import io.noties.markwon.core.CoreProps
import org.commonmark.node.*

@Composable
fun MarkwonText(
    markdown: String,
    owner: String,
    repo: String,
    branch: String,
    modifier: Modifier = Modifier
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val textColor = MaterialTheme.colorScheme.onSurface.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()
    val codeBackgroundColor = MaterialTheme.colorScheme.surfaceVariant.toArgb()
    val codeTextColor = MaterialTheme.colorScheme.onSurfaceVariant.toArgb()
    val borderColor = MaterialTheme.colorScheme.outlineVariant.toArgb()
    
    val processedMarkdown = remember(markdown, owner, repo, branch) {
        processImageUrls(markdown, owner, repo, branch)
    }
    
    val markwon = remember(context, textColor, linkColor, codeBackgroundColor, codeTextColor, borderColor) {
        createMaterial3Markwon(context, textColor, linkColor, codeBackgroundColor, codeTextColor, borderColor)
    }
    
    AndroidView(
        factory = { ctx ->
            TextView(ctx).apply {
                setPadding(32, 24, 32, 24)
                textSize = 14f
                setTextColor(textColor)
                setLineSpacing(6f, 1.15f)
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = { textView ->
            markwon.setMarkdown(textView, processedMarkdown)
        },
        modifier = modifier.fillMaxWidth()
    )
}

fun processImageUrls(markdown: String, owner: String, repo: String, branch: String): String {
    val baseUrl = "https://raw.githubusercontent.com/$owner/$repo/$branch/"
    
    return markdown
        .replace(Regex("""src="(?!http|data:|//)([^"]+)""")) { match ->
            val src = match.groupValues[1]
            "src=\"$baseUrl$src\""
        }
        .replace(Regex("""!\[([^]]*)\]\((?!http|data:|//)([^)]+)\)""")) { match ->
            val alt = match.groupValues[1]
            val url = match.groupValues[2]
            "![$alt]($baseUrl$url)"
        }
}

fun createMaterial3Markwon(
    context: Context,
    textColor: Int,
    linkColor: Int,
    codeBackgroundColor: Int,
    codeTextColor: Int,
    borderColor: Int
): Markwon {
    val density = context.resources.displayMetrics.density
    val dp2 = (2 * density).toInt()
    val dp4 = (4 * density).toInt()
    val dp6 = (6 * density).toInt()
    val dp8 = (8 * density).toInt()
    val dp12 = (12 * density).toInt()
    val dp16 = (16 * density).toInt()
    val dp24 = (24 * density).toInt()
    
    return Markwon.builder(context)
        .usePlugin(CorePlugin.create())
        .usePlugin(object : AbstractMarkwonPlugin() {
            override fun configureTheme(builder: MarkwonTheme.Builder) {
                builder
                    .linkColor(linkColor)
                    .isLinkUnderlined(false)
                    .blockMargin(dp24)
                    .blockQuoteWidth(dp4)
                    .blockQuoteColor(borderColor)
                    .bulletWidth(dp6)
                    .bulletListItemStrokeWidth(dp2)
                    .listItemColor(textColor)
                    .headingBreakHeight(dp2)
                    .headingBreakColor(borderColor)
                    .headingTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
                    .headingTextSizeMultipliers(floatArrayOf(1.6f, 1.4f, 1.2f, 1.0f, 0.9f, 0.85f))
                    .codeTextColor(codeTextColor)
                    .codeBackgroundColor(codeBackgroundColor)
                    .codeBlockTextColor(codeTextColor)
                    .codeBlockBackgroundColor(codeBackgroundColor)
                    .codeTypeface(Typeface.MONOSPACE)
                    .codeBlockTypeface(Typeface.MONOSPACE)
                    .thematicBreakColor(borderColor)
                    .thematicBreakHeight(dp2)
            }
            
            override fun configureSpansFactory(builder: MarkwonSpansFactory.Builder) {
                builder.appendFactory(Heading::class.java) { configuration, props ->
                    val level = props.get(CoreProps.HEADING_LEVEL) ?: 1
                    val textSize = when (level) {
                        1 -> 28f
                        2 -> 22f
                        3 -> 18f
                        4 -> 16f
                        5 -> 14f
                        else -> 13f
                    }
                    val textSizePx = (textSize * density).toInt()
                    arrayOf(
                        android.text.style.AbsoluteSizeSpan(textSizePx),
                        android.text.style.StyleSpan(Typeface.BOLD)
                    )
                }
            }
        })
        .usePlugin(TablePlugin.create(context))
        .usePlugin(HtmlPlugin.create())
        .usePlugin(CoilImagesPlugin.create(context))
        .build()
}