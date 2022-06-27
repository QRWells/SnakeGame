package view

import Client
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.GameObject
import data.SnakeGame
import data.SnakeHeadData
import wang.qrwells.message.impl.SnakeGameMessage

@Composable
fun GameObjectTile(
  gameObject: GameObject,
  tileSize: Pair<Dp, Dp> = Pair(20.dp, 20.dp),
) =
  Box(
    Modifier.offset(
      tileSize.first * gameObject.x,
      tileSize.second * gameObject.y
    )
      .size(width = tileSize.first - 2.dp, height = tileSize.second - 2.dp)
      .background(gameObject.color)
  )

@Composable
fun GameObjectTileRound(
  gameObject: GameObject,
  tileSize: Pair<Dp, Dp> = Pair(20.dp, 20.dp),
  roundCorner: Dp = 4.dp
) =
  Box(
    Modifier.offset(
      tileSize.first * gameObject.x,
      tileSize.second * gameObject.y
    )
      .size(width = tileSize.first - 2.dp, height = tileSize.second - 2.dp)
      .background(gameObject.color).clip(RoundedCornerShape(roundCorner))
  )

@Composable
@Preview
fun SnakeView(snakeGame: SnakeGame) {
  val game = remember { snakeGame }
  val density = LocalDensity.current
  val refreshTimeNanos = 1_000_000_000 / game.speed
  var lastUpdate by mutableStateOf(0L)
  var tileSize by mutableStateOf(Pair(20.dp, 20.dp))

  Client.addHandler("gameHandler") { connection, message ->
    if (message is SnakeGameMessage) {
      snakeGame.handleMessage(connection, message)
    }
  }

  LaunchedEffect(Unit) {
    while (true) {
      withFrameNanos {
        if ((it - lastUpdate) > refreshTimeNanos) {
          snakeGame.update()
          lastUpdate = it
        }
      }
    }
  }

  Box(modifier = Modifier.fillMaxSize().background(Color.Black)
    .clipToBounds().onSizeChanged {
      with(density) {
        tileSize = Pair(
          it.width.toDp() / SnakeGame.areaSize,
          it.height.toDp() / SnakeGame.areaSize
        )
      }
    }.onPreviewKeyEvent {
      game.registerKeyEvent(it)
    }) {
    game.gameObjects.forEach {
      when (it) {
        is SnakeHeadData -> GameObjectTileRound(it, tileSize)
        else -> GameObjectTile(it, tileSize)
      }
    }
  }
}
