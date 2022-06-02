package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import data.Message

@Composable
@Preview
fun MessageRow(message: Message, self: Boolean) {

  Column(
    modifier = Modifier.padding(8.dp).fillMaxWidth(),
    horizontalAlignment = if (self) Alignment.End else Alignment.Start,
    verticalArrangement = Arrangement.Top
  ) {
    Text("${message.sender}", fontSize = 12.sp)
    Box(
      modifier = Modifier.clip(shape = RoundedCornerShape(4.dp))
        .background(color = Color(0, 0, 0, 20))
        .padding(horizontal = 4.dp, vertical = 4.dp),
      contentAlignment = Alignment.CenterStart
    ) {
      Text(
        message.message,
      )
    }
  }
}