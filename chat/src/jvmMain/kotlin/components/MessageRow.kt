package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import data.Message

@Composable
@Preview
fun MessageRow(message: Message) {
  Box(
    modifier = Modifier.clip(shape = RoundedCornerShape(4.dp))
      .background(color = Color(0, 0, 0, 20))
      .padding(horizontal = 4.dp),
    contentAlignment = Alignment.CenterStart
  ) {
    Text("${message.sender} : ${message.message}")
  }
}