package view

import AppContext
import Client
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import data.*
import org.slf4j.LoggerFactory
import viewModel.SnakeGameInfo
import viewModel.SnakeViewModel
import wang.qrwells.message.impl.SnakeGameListMessage
import wang.qrwells.message.impl.SnakeGameMessage

/**
 * 四角を表す
 */
@Composable
fun GameObjectTile(
  gameObject: GameObject,
  tileSize: Pair<Dp, Dp> = Pair(20.dp, 20.dp),
) =
  Box(
    Modifier
      .offset(tileSize.first * gameObject.x, tileSize.second * gameObject.y)
      .size(width = tileSize.first - 2.dp, height = tileSize.second - 2.dp)
      .background(gameObject.color)
  )

/**
 * 四角を表す
 */
@Composable
fun GameObjectTileRound(
  gameObject: GameObject,
  tileSize: Pair<Dp, Dp> = Pair(20.dp, 20.dp),
  roundCorner: Dp = 4.dp
) =
  Box(
    Modifier
      .offset(tileSize.first * gameObject.x, tileSize.second * gameObject.y)
      .size(width = tileSize.first - 2.dp, height = tileSize.second - 2.dp)
      .background(gameObject.color).clip(RoundedCornerShape(roundCorner))
  )

/**
 * スネークゲームの画面
 */
@Composable
fun SnakeView(snakeViewModel: SnakeViewModel) {
  val snakeGame = remember { SnakeGame() }
  val refreshTimeNanos = 1_000_000_000 / snakeGame.speed // 何ナノ秒ごとに更新するか
  var lastUpdate by mutableStateOf(0L)  // 最後に更新した時間
  val density = LocalDensity.current // ディスプレイの解像度
  var tileSize by mutableStateOf(Pair(20.dp, 20.dp)) // タイルの大きさ
  val logger = LoggerFactory.getLogger("SnakeView") // ロガー

  // 起動する時に一回だけ呼ばれる
  LaunchedEffect(Unit) {
    snakeGame.id = AppContext.user.id // 自分のIDを設定

    // ゲームリストを取得するメッセージを処理
    Client.addHandler("gameList") { _, message ->
      if (message is SnakeGameListMessage) {
        snakeViewModel.gameList.clear()
        for (game in message.gameInfos) {
          snakeViewModel.gameList.add(
            SnakeGameInfo(
              game.id,
              game.currentPlayers,
              game.maxPlayers,
            )
          )
        }
      }
    }

    // ゲームメッセージをゲームに処理させる
    Client.addHandler("gameHandler") { _, message ->
      if (message is SnakeGameMessage) {
        snakeGame.handleMessage(message)
      }
    }

    // ゲームを更新する
    while (true) {
      withFrameNanos {
        if ((it - lastUpdate) > refreshTimeNanos) {
          snakeGame.update()
          lastUpdate = it
        }
      }
    }
  }

  Row(modifier = Modifier.fillMaxSize()) {
    /**
     * ゲーム情報を表示する
     */
    Column(modifier = Modifier.weight(3f)) {
      val size = remember { mutableStateOf("") }
      val inputEnable = remember { mutableStateOf(true) }
      Row(modifier = Modifier.weight(3f)) {
        Column(modifier = Modifier.padding(4.dp)) {
          for (info in snakeViewModel.gameList) {
            Card(Modifier.clickable {
              snakeViewModel.joinGame(info.sessionId)
              snakeGame.sessionId = info.sessionId
              inputEnable.value = false
              // Windowからキー入力の情報を処理
              AppContext.keyEventHandlers.add {
                snakeGame.registerKeyEvent(it)
              }
            }) {
              Text(
                modifier = Modifier.fillMaxWidth(),
                text = info.toString(),
                textAlign = TextAlign.Justify
              )
            }
            Spacer(modifier = Modifier.height(4.dp))
          }
        }
      }
      Row {
        TextField(
          value = size.value,
          onValueChange = {
            size.value = it
          },
          modifier = Modifier.fillMaxWidth(),
          singleLine = true,
          placeholder = { Text("Max players") },
          enabled = inputEnable.value,
        )
      }
      Row {
        Button(
          onClick = {
            snakeViewModel.fetchGameList()
          },
        ) {
          Text("Fetch")
        }
        Button(
          onClick = {
            var s = 2
            try {
              s = size.value.toInt()
            } catch (e: NumberFormatException) {
              size.value = "2"
            } finally {
              snakeViewModel.createGame(s)
            }
          },
        ) {
          Text("Create")
        }
      }
      Row(modifier = Modifier.weight(3f)) {
        Column {
          Text("${snakeGame.id} ${snakeGame.self.score}")
          for (player in snakeGame.other) {
            Text("${player.key} ${player.value.score}")
          }
        }
      }
    }

    /**
     * ゲーム画面を表示する
     */
    Column(
      modifier = Modifier.weight(7f),
      horizontalAlignment = Alignment.CenterHorizontally,
      verticalArrangement = Arrangement.Center
    ) {
      Box(modifier = Modifier.fillMaxSize().background(Color.Black)
        .clipToBounds().onSizeChanged {
          with(density) {
            tileSize = Pair(
              it.width.toDp() / SnakeGame.areaSize,
              it.height.toDp() / SnakeGame.areaSize
            )
          }
        }
      ) {
        snakeGame.gameObjects.forEach {
          when (it) {
            is SnakeHeadData -> GameObjectTileRound(it, tileSize)
            is SnakeBodyData -> GameObjectTile(it, tileSize)
            is FoodData -> GameObjectTile(it, tileSize)
          }
        }
      }
    }
  }
}