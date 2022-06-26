package view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import viewModel.MainScreenViewModel

@Composable
fun MainScreen(mainScreenViewModel: MainScreenViewModel) {
  val chatViewModel = mainScreenViewModel.chatViewModel
  val snakeViewModel = mainScreenViewModel.snakeViewModel
  val settingsViewModel = mainScreenViewModel.settingsViewModel

  val currentView = remember { mutableStateOf(ViewState.Chat) }

  Row(Modifier.fillMaxSize()) {
    Box(
      modifier = Modifier.width(48.dp).background(color = Color(235, 235, 235)),
    ) {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.Top
      ) {
        IconButton(onClick = { currentView.value = ViewState.Chat }) {
          Icon(Icons.Default.AccountCircle, "You")
        }
      }
      Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Bottom
      ) {
        IconButton(onClick = {
          // todo :switch to snake
        }) {
          Icon(Icons.Default.List, "menu")
        }
        IconButton(onClick = { currentView.value = ViewState.Settings }) {
          Icon(Icons.Default.Settings, "settings")
        }
      }
    }
    Box {
      when (currentView.value) {
        ViewState.Chat -> ChatView(chatViewModel)
//        ViewState.Snake -> SnakeView(snakeViewModel)
        ViewState.Settings -> SettingsView(settingsViewModel)
      }
    }
  }
}

enum class ViewState {
  Chat, Snake, Settings
}
