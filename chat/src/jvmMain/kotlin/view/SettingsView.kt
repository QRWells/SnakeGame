package view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.material.Checkbox
import androidx.compose.material.Slider
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.BooleanOption
import data.IntOption
import data.StringOption
import viewModel.SettingsViewModel

@Composable
fun SettingsView(viewModel: SettingsViewModel) {
  Column {
    for ((name, option) in viewModel.options) {
      Row {
        Text(name)
        Spacer(modifier = Modifier.width(16.dp))
        when (option.value) {
          is Boolean -> {
            Checkbox(
              checked = option.value as Boolean,
              onCheckedChange = {
                (option as BooleanOption).set(it)
              }
            )
          }
          is String -> {
            TextField(
              value = option.value as String,
              onValueChange = {
                (option as StringOption).set(it)
              }
            )
          }
          is Int -> {
            Slider(
              value = (option.value as Int).toFloat(),
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