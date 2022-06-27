package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
@Preview
fun TitleBar(
  onMinClick: () -> Unit,
  onCloseClick: () -> Unit,
  title: String,
) {
  Row(
    Modifier.background(MaterialTheme.colors.primary).fillMaxWidth()
      .height(36.dp).padding(top = 4.dp, bottom = 4.dp),
    Arrangement.Center
  ) {
    Row(
      horizontalArrangement = Arrangement.Start,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1.0f)
    ) {
      Column(
        modifier = Modifier.width(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
      ) {
        Icon(
          Icons.Default.Call,
          "AppIcon",
          Modifier.size(24.dp)
        )
      }
      Text(
        text = title,
        fontSize = 24.sp,
        fontFamily = FontFamily.SansSerif,
        modifier = Modifier.padding(start = 4.dp)
      )
    }
    Row(
      horizontalArrangement = Arrangement.End,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.weight(1.0f),
    ) {
      IconButton(onMinClick, Modifier.size(24.dp)) {
        Icon(
          Icons.Default.KeyboardArrowDown,
          "Minimum",
          Modifier.size(24.dp)
        )
      }
      Spacer(Modifier.size(16.dp))
      IconButton(onCloseClick, Modifier.size(24.dp)) {
        Icon(Icons.Default.Close, "Close", Modifier.size(24.dp))
      }
      Spacer(Modifier.size(8.dp))
    }
  }
}