package view

import Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material.Slider
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.BooleanOption
import data.IntOption
import data.StringOption

@Composable
fun SettingsView() {
  Column(modifier = Modifier.padding(16.dp)) {
    for ((name, option) in Settings.options) {
      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Start
      ) {
        Text(name)
        Spacer(modifier = Modifier.width(16.dp))
        when (option.value.value) {
          is Boolean -> {
            Switch(
              checked = option.value.value as Boolean,
              onCheckedChange = {
                (option as BooleanOption).set(it)
              }
            )
          }
          is String -> {
            TextField(
              value = option.value.value as String,
              onValueChange = {
                (option as StringOption).set(it)
              }
            )
          }
          is Int -> {
            Slider(
              value = (option.value.value as Int).toFloat(),
              onValueChange = {
                (option as IntOption).set(it.toInt())
              },
              steps = 1,
            )
          }
        }
      }
    }
  }
}