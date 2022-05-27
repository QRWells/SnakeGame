import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import components.TitleBar
import model.ChatViewModel
import view.AppsView
import view.ChatView

@Composable
@Preview
fun App() {
  MaterialTheme {
    Row(Modifier.fillMaxSize()) {
      var chat by rememberSaveable { mutableStateOf(true) }
      Box(
        modifier = Modifier.width(48.dp),
      ) {
        Column(
          modifier = Modifier.fillMaxWidth(),
          verticalArrangement = Arrangement.Top
        ) {
          IconButton(onClick = {}) {
            Icon(Icons.Default.AccountCircle, "You")
          }
          IconButton(onClick = { chat = true }) {
            Icon(Icons.Default.List, "Friends")
          }
        }
        Column(
          modifier = Modifier.fillMaxSize(),
          verticalArrangement = Arrangement.Bottom
        ) {
          IconButton(onClick = { chat = false }) {
            Icon(Icons.Default.Menu, "menu")
          }
        }
      }
      Box {
        val chatViewModel = ChatViewModel()
        ChatView(chatViewModel)
        if (!chat)
          AppsView()
      }
    }
  }
}

fun main() = application {
  var isOpen by rememberSaveable { mutableStateOf(true) }
  val state = rememberWindowState(isMinimized = false)
  if (isOpen) Window(
    onCloseRequest = ::exitApplication,
    title = "Snake",
    undecorated = true,
    state = state
  ) {
    Column(Modifier.fillMaxSize()) {
      WindowDraggableArea {
        TitleBar(onMinClick = { state.isMinimized = !state.isMinimized },
          onCloseClick = { isOpen = false })
      }
      App()
    }
  }
}
