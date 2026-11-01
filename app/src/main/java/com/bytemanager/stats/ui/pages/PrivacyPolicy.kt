package com.bytemanager.stats.ui.pages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.LinkAnnotation
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withLink
import androidx.compose.ui.unit.dp


@Composable
fun PrivacyPolicy() {
    Column(
        modifier = Modifier.padding(10.dp)
    ) {
        PrivacyPolicyLink()
    }
}

@Composable
fun PrivacyPolicyLink() {
    val url = "https://docs.google.com/document/d/e/2PACX-1vS-2llWpavoF4EMLv_ZbfDyPvsqru4-DeEY9lqhjf3dNf9cXoJAxYpCbjEcPehNg2fKq2tBq6-ydUTH/pub"

    val annotatedString = buildAnnotatedString {
        withLink(
            LinkAnnotation.Url(
                url = url
            )
        ) {
            pushStyle(
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline
                )
            )
            append("Privacy policy")
            pop()
        }
    }

    Text(
        text = annotatedString,
        style = MaterialTheme.typography.bodyLarge,
    )
}