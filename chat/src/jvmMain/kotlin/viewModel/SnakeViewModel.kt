package viewModel

import AppContext
import Client
import androidx.compose.runtime.mutableStateListOf
import wang.qrwells.message.impl.SnakeGameMessage

class SnakeViewModel {
  fun fetchGameList() {
    Client.send(
      SnakeGameMessage(
        0,
        0,
        0,
        SnakeGameMessage.Action.QUERY,
        SnakeGameMessage.Direction.NONE,
        0,
        0
      )
    )
  }

  fun createGame(value: Int) {
    Client.send(
      SnakeGameMessage(
        0,
        0,
        0,
        SnakeGameMessage.Action.NEW,
        SnakeGameMessage.Direction.NONE,
        value,
        0
      )
    )
  }

  fun joinGame(sessionId: Int) {
    Client.send(
      SnakeGameMessage(
        0,
        sessionId,
        AppContext.user.id,
        SnakeGameMessage.Action.JOIN,
        SnakeGameMessage.Direction.NONE,
        0,
        0
      )
    )
  }

  var gameList = mutableStateListOf<SnakeGameInfo>()
}

data class SnakeGameInfo(
  val sessionId: Int,
  val currentPlayer: Int,
  val maxPlayer: Int,
) {
  override fun toString(): String {
    return "$sessionId: $currentPlayer/$maxPlayer"
  }
}

