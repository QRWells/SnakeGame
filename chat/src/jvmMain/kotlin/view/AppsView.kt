package view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
@Preview
fun AppsView() {
  Row(modifier = Modifier.fillMaxSize().padding(16.dp)) {
    Card(modifier = Modifier.size(width = 128.dp, height = 128.dp).clickable {

    }) {
      Text("Snake Game")
    }
  }
}