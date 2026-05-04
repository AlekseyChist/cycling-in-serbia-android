package com.cyclinginserbia.app.ui.screens.regulations

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextLinkStyles
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.text.withStyle

private const val LAW_URL =
    "https://www.paragraf.rs/propisi/zakon_o_bezbednosti_saobracaja_na_putevima.html"

private val CONTENT_PATTERN = Regex(
    """(\[([^\]]+)]\(([^)]+)\))""" +
        """|((?:Art\.\s*\d+\s*p\.\s*\d+\s*of\s*the\s*Law)|(?:No\s*provision\s*of\s*the\s*Law))"""
)

@Composable
fun rememberRegulationParagraph(paragraph: String): AnnotatedString {
    val linkColor = MaterialTheme.colorScheme.primary
    val lawColor = MaterialTheme.colorScheme.onSurfaceVariant

    return buildAnnotatedString {
        var lastIndex = 0
        for (match in CONTENT_PATTERN.findAll(paragraph)) {
            if (match.range.first > lastIndex) {
                append(paragraph.substring(lastIndex, match.range.first))
            }

            val mdLink = match.groups[1]
            val lawRef = match.groups[4]

            when {
                mdLink != null -> {
                    val label = match.groups[2]!!.value
                    val url = match.groups[3]!!.value
                    withLink(
                        LinkAnnotation.Url(
                            url = url,
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = linkColor,
                                    textDecoration = TextDecoration.Underline,
                                ),
                            ),
                        ),
                    ) { append(label) }
                }
                lawRef != null -> {
                    withLink(
                        LinkAnnotation.Url(
                            url = LAW_URL,
                            styles = TextLinkStyles(
                                style = SpanStyle(
                                    color = lawColor,
                                    fontStyle = FontStyle.Italic,
                                ),
                            ),
                        ),
                    ) {
                        withStyle(SpanStyle(fontStyle = FontStyle.Italic)) {
                            append(lawRef.value)
                        }
                    }
                }
            }

            lastIndex = match.range.last + 1
        }
        if (lastIndex < paragraph.length) {
            append(paragraph.substring(lastIndex))
        }
    }
}
