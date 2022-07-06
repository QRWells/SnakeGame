import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.window.WindowDraggableArea
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import androidx.compose.ui.window.rememberWindowState
import components.TitleBar
import data.BooleanOption
import org.jetbrains.exposed.sql.Database
import screen.LoginScreen
import screen.MainScreen
import viewModel.LoginViewModel
import viewModel.MainScreenViewModel
import wang.qrwells.message.impl.LogoutMessage

@Composable
@Preview
fun App(onKey: () -> Unit, onTitleChange: (String) -> Unit) {
  val loginViewModel = LoginViewModel()
  val mainScreenViewModel = MainScreenViewModel()
  val loggedIn = remember { mutableStateOf(false) }

  Surface {
    if (!loggedIn.value) {
      onTitleChange("Login")
      LoginScreen(loginViewModel, onLogin = {
        mainScreenViewModel.chatViewModel.self = it
        AppContext.user = it
        loggedIn.value = true
        Database.connect("jdbc:sqlite:" + it.name, driver = "org.sqlite.JDBC")
        Client.clearHandler()
      }, onRegister = {
        mainScreenViewModel.chatViewModel.self = it
        AppContext.user = it
        loggedIn.value = true
        Database.connect("jdbc:sqlite:" + it.name, driver = "org.sqlite.JDBC")
        Client.clearHandler()
      })
    } else {
      onTitleChange("${AppContext.user.name} (${AppContext.user.id})")
      MainScreen(mainScreenViewModel)
    }
  }
}

fun main() = application {
  val state = rememberWindowState(isMinimized = false)
  val title = remember { mutableStateOf("Login") }
  val theme = remember { mutableStateOf(false) }
  val colors = remember { mutableStateOf(lightColors()) }

  Settings.options["Theme"] = BooleanOption(theme) {
    colors.value = if (it) darkColors() else lightColors()
  }

  Window(
    onCloseRequest = ::exitApplication,
    title = "Snake",
    undecorated = true,
    state = state,
    onPreviewKeyEvent = { e ->
      AppContext.keyEventHandlers.forEach {
        it.invoke(e)
      }
      false
    }
  ) {
    MaterialTheme(colors = colors.value) {
      Column(Modifier.fillMaxSize()) {
        WindowDraggableArea {
          TitleBar(
            onMinClick = { state.isMinimized = !state.isMinimized },
            onCloseClick = {
              // send logout message if user has logged in
              if (AppContext.user.id > 0) {
                Client.send(LogoutMessage(AppContext.user.id))
                // make sure the message is sent before exit
                Thread.sleep(1000)
              }
              Client.close()
              exitApplication()
            }, title.value
          )
        }
        App(onKey = {}) { title.value = it }
      }
    }
  }
}
