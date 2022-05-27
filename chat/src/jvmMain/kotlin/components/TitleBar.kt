package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.KeyboardArrowDown
import androidx.compose.material.icons.outlined.Menu
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
  onCloseClick: () -> Unit
) {
  MaterialTheme(
    MaterialTheme.colors,
    MaterialTheme.typography,
    MaterialTheme.shapes
  ) {
    Row(
      Modifier.fillMaxWidth().height(32.dp).padding(top = 4.dp, bottom = 4.dp),
      Arrangement.Center
    ) {
      Row(
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.weight(1.0f)
      ) {
        Icon(
          Icons.Outlined.Menu,
          "Snake",
          Modifier.size(24.dp)
        )
        Text(
          text = "Snake",
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
            Icons.Outlined.KeyboardArrowDown,
            "Minimum",
            Modifier.size(24.dp)
          )
        }
        Spacer(Modifier.size(16.dp))
        IconButton(onCloseClick, Modifier.size(24.dp)) {
          Icon(Icons.Outlined.Close, "Close", Modifier.size(24.dp))
        }
        Spacer(Modifier.size(8.dp))
      }
    }
  }
}