import androidx.compose.ui.input.key.KeyEvent
import data.User

object AppContext {
  var user: User = User(-1, "empty")
  var token: String = ""
  val keyEventHandlers = mutableListOf<KeyEventHandler>()

  fun saveAll() {

  }
}

typealias KeyEventHandler = (KeyEvent) -> Boolean