import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import components.TitleBar
import data.Context
import org.jetbrains.exposed.sql.Database
import screen.LoginScreen
import view.MainScreen
import viewModel.LoginViewModel
import viewModel.MainScreenViewModel

@Composable
@Preview
fun App(onTitleChange: (String) -> Unit) {
  val loginViewModel = LoginViewModel()
  val mainScreenViewModel = MainScreenViewModel()
  val loggedIn = remember { mutableStateOf(false) }
  MaterialTheme {
    if (!loggedIn.value) {
      onTitleChange("Login")
      LoginScreen(loginViewModel, onLogin = {
        Database.connect(
          "jdbc:sqlite:user",
          "org.sqlite.JDBC",
        )
        mainScreenViewModel.chatViewModel.self = it.id
        Context.user = it
        loggedIn.value = true
      }, onRegister = {
        mainScreenViewModel.chatViewModel.self = it.id
        Context.user = it
        loggedIn.value = true
      })
    } else {
      onTitleChange("Chat")
      MainScreen(mainScreenViewModel)
    }
  }
}

fun main() = application {
  val state = rememberWindowState(isMinimized = false)
  val title = remember { mutableStateOf("Login") }

  Window(
    onCloseRequest = ::exitApplication,
    title = "Snake",
    undecorated = true,
    state = state
  ) {
    Column(Modifier.fillMaxSize()) {
      WindowDraggableArea {
        TitleBar(
          onMinClick = { state.isMinimized = !state.isMinimized },
          onCloseClick = {
            // todo : save state
            Client.close()
            exitApplication()
          }, title.value
        )
      }
      App { title.value = it }
    }
  }
}
