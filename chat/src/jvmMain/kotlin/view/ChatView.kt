package view

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import components.History
import data.Message
import viewModel.ChatViewModel
import java.time.LocalDateTime

@Composable
@Preview
fun ChatView(viewModel: ChatViewModel) {
  Row(Modifier.fillMaxSize()) {
    Column(modifier = Modifier.weight(3f, true)) {
      Box(
        modifier = Modifier.fillMaxSize()
      ) {
        val stateVertical = rememberScrollState(0)
        Box(
          modifier = Modifier.fillMaxSize().verticalScroll(stateVertical)
            .padding(end = 12.dp, bottom = 12.dp)
        ) {
          Column {
            for (item in viewModel.contacts) {
              Text(item.value.name)
              Spacer(modifier = Modifier.height(5.dp))
            }
          }
        }
        VerticalScrollbar(
          modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
          adapter = rememberScrollbarAdapter(stateVertical)
        )
        FloatingActionButton(
          modifier = Modifier.padding(16.dp).size(36.dp)
            .align(Alignment.BottomEnd),
          onClick = {
          }
        ) {
          Icon(
            Icons.Default.Add,
            "Add",
            Modifier.size(24.dp)
          )
        }
      }
    }
    Column(modifier = Modifier.width(4.dp).fillMaxHeight()) {
      Box(modifier = Modifier.fillMaxSize().background(Color.LightGray)) {

      }
    }
    Column(
      modifier = Modifier.weight(6f, true)
    ) {
      var text by rememberSaveable { viewModel.text }
      var history = rememberSaveable { viewModel.history }
      History(
        modifier = Modifier.fillMaxSize().padding(8.dp)
          .clip(shape = RoundedCornerShape(4.dp))
          .background(Color.White).weight(8f, true),
        history, viewModel.self
      )

      Box(
        Modifier.heightIn(min = 32.dp, max = 64.dp).fillMaxSize()
          .clip(shape = RoundedCornerShape(4.dp))
          .weight(2f, true)
      ) {
        OutlinedTextField(
          text,
          onValueChange = { text = it },
          modifier = Modifier.align(Alignment.TopStart).fillMaxSize()
            .heightIn(32.dp, 64.dp).padding(8.dp).onPreviewKeyEvent {
              println(it.key.keyCode);false
            }
        )
        Button(
          onClick = {
            text.trim().let {
              text = ""
              if (it.isNotEmpty()) {
                var message = Message(
                  viewModel.self,
                  viewModel.with,
                  it,
                  LocalDateTime.now()
                )

                history.add(
                  message
                )
              }
            }
          },
          modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
        ) {
          Text("Send")
        }
      }
    }
  }
}