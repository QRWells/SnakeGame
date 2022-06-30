package screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.unit.dp
import view.ChatView
import view.SettingsView
import view.SnakeView
import viewModel.MainScreenViewModel

@Composable
fun MainScreen(mainScreenViewModel: MainScreenViewModel) {
  LaunchedEffect(Unit) {
    mainScreenViewModel.chatViewModel.loadHistories()
  }
  val chatViewModel = remember { mainScreenViewModel.chatViewModel }
  val snakeViewModel = remember { mainScreenViewModel.snakeViewModel }

  val currentView = remember { mutableStateOf(ViewState.Chat) }

  Row(Modifier.fillMaxSize().onPreviewKeyEvent { false }) {
    Box(
      modifier = Modifier.width(48.dp)
        .background(color = MaterialTheme.colors.secondary),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top
      ) {
        IconButton(onClick = { currentView.value = ViewState.Chat }) {
          Icon(Icons.Default.MailOutline, "Chat")
        }
      }
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
      ) {
        IconButton(onClick = {
          currentView.value = ViewState.Snake
        }) {
          Icon(Icons.Default.Refresh, "Snake")
        }
        IconButton(onClick = { currentView.value = ViewState.Settings }) {
          Icon(Icons.Default.Settings, "settings")
        }
      }
    }
    Box {
      when (currentView.value) {
        ViewState.Chat -> ChatView(chatViewModel)
        ViewState.Snake -> SnakeView(snakeViewModel)
        ViewState.Settings -> SettingsView()
      }
    }
  }
}

enum class ViewState {
  Chat, Snake, Settings
}
