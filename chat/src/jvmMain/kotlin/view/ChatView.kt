package view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import components.History
import data.Message
import model.ChatViewModel
import java.util.*

@Composable
@Preview
fun ChatView(viewModel: ChatViewModel) {
  Row(Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier.weight(3f, true)
    ) {
      val stateVertical = rememberScrollState(0)
      Box(
        modifier = Modifier.fillMaxSize().verticalScroll(stateVertical)
          .padding(end = 12.dp, bottom = 12.dp)
      ) {
        Column {
          for (item in 0..30) {
            Text("Item #$item")
            if (item < 30) {
              Spacer(modifier = Modifier.height(5.dp))
            }
          }
        }
      }
      VerticalScrollbar(
        modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
        adapter = rememberScrollbarAdapter(stateVertical)
      )
    }

    Column(
      modifier = Modifier.weight(6f, true)
    ) {
      var text by rememberSaveable { viewModel.text }
      var history = rememberSaveable { viewModel.history }
      History(
        modifier = Modifier.fillMaxSize().padding(8.dp)
          .clip(shape = RoundedCornerShape(4.dp))
          .background(Color.LightGray).weight(8f, true),
        history,
      )

      Box(
        Modifier.heightIn(min = 32.dp, max = 64.dp).fillMaxSize()
          .clip(shape = RoundedCornerShape(4.dp))
          .weight(2f, true)
      ) {
        TextField(
          text,
          onValueChange = { text = it },
          modifier = Modifier.align(Alignment.TopStart).fillMaxSize()
            .heightIn(32.dp, 64.dp).padding(8.dp)
        )
        Button(onClick = {
          if (text.isNotEmpty()) {
            history.add(Message(UUID.randomUUID(), text))
            text = ""
          }
        }, modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)) {
          Text("Send")
        }
      }
    }
  }
}