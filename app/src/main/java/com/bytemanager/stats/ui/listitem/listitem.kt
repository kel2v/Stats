package com.bytemanager.stats.ui.listitem

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.bytemanager.stats.ui.theme.AppTypography

@Composable
fun ListItem(
    parameterName: String,
    parameterValue: String,
) {
    Card(
        shape = RoundedCornerShape(15.dp),
        border = BorderStroke(
            width = 2.dp,
            color = MaterialTheme.colorScheme.outline
        ),
        modifier = Modifier.fillMaxWidth(0.9f)
    ) {
        Row (
            modifier = Modifier
                .fillMaxWidth(),

            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = parameterName,
                modifier = Modifier
                    .padding(15.dp)
                    .weight(0.5f)
                ,
                style = AppTypography.bodyMedium,
                textAlign = TextAlign.Start,
            )

            Spacer(Modifier.width(30.dp))

            Text(
                text = parameterValue,
                modifier = Modifier
                    .padding(15.dp)
                    .weight(0.5f)
                ,
                style = AppTypography.bodyLarge,
                textAlign = TextAlign.Start,
            )
        }
    }

    Spacer(Modifier.height(15.dp))
}