package com.example.stats.ui.listitem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ListItem(
    modifier: Modifier = Modifier,
    parameterName: String,
    parameterValue: String
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = parameterName
        )

        Text(
            text = parameterValue
        )
    }
}


//@Composable
//@Preview(showBackground = true)
//fun ListPreview() {
//    Column(modifier = Modifier.fillMaxWidth()) {
//        ListItem(
//            parameterName = "Name",
//            parameterNameModifier = Modifier,
//            parameterValue = "Carlos Sainz",
//            parameterValueModifier = Modifier
//        )
//
//        ListItem(
//            parameterName = "Name",
//            parameterNameModifier = Modifier,
//            parameterValue = "Alex Albon",
//            parameterValueModifier = Modifier
//        )
//
//        ListItem(
//            parameterName = "Name",
//            parameterNameModifier = Modifier,
//            parameterValue = "Max Verstappen",
//            parameterValueModifier = Modifier
//        )
//
//        ListItem(
//            parameterName = "Name",
//            parameterNameModifier = Modifier,
//            parameterValue = "Charles Leclerc",
//            parameterValueModifier = Modifier
//        )
//    }
//}