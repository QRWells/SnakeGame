package data

import Client
import androidx.compose.runtime.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import wang.qrwells.message.impl.SnakeGameMessage
import wang.qrwells.message.impl.SnakeGameMessage.Direction
import kotlin.random.Random

/**
 * ゲームにある四角を表すクラス
 *
 */
sealed class GameObject(x: Int, y: Int, color: Color) {
  var x by mutableStateOf(x)
  var y by mutableStateOf(y)
  var color by mutableStateOf(color)
}

/**
 * 蛇の頭を表すクラス
 */
class SnakeHeadData(x: Int, y: Int, color: Color) : GameObject(x, y, color)

/**
 * 蛇の身体を表すクラス
 */
class SnakeBodyData(x: Int, y: Int, color: Color) : GameObject(x, y, color)

/**
 * 餌を表すクラス
 */
class FoodData(x: Int, y: Int) : GameObject(x, y, Color.Blue)

/**
 * 蛇一匹に関するデータ
 */
class Snake(
  x: Int, y: Int, private val color: Color
) {
  var score = 0 // 得点
  var head = SnakeHeadData(x, y, color) // 蛇の頭
  var body = mutableStateListOf<SnakeBodyData>() // 蛇の身体
  var direction = Direction.RIGHT // 現在の方向
  var length = SnakeGame.startLength // 現在の長さ
  var px = x // 次の頭の位置
  var py = y // 次の頭の位置
  var dead = false // 死亡フラグ

  // 今の方向で次の位置を計算する
  fun move() {
    if (dead) return
    when (direction) {
      Direction.UP -> py--
      Direction.DOWN -> py++
      Direction.LEFT -> px--
      Direction.RIGHT -> px++
      else -> {}
    }
    when {
      (px < 0) -> px = SnakeGame.areaSize - 1
      (px >= SnakeGame.areaSize) -> px = 0
      (py < 0) -> py = SnakeGame.areaSize - 1
      (py >= SnakeGame.areaSize) -> py = 0
    }
  }

  // 蛇の各部分の位置を更新する
  fun updateTiles() {
    body += SnakeBodyData(head.x, head.y, color)
    head.x = px
    head.y = py
    // 2回repeatはその削除を確保
    repeat(2) {
      if (body.size > length) body.removeFirst()
    }
  }
}

/**
 * ゲームのデータを管理するクラス
 */
class SnakeGame {
  var sessionId: Int = -1 // 対局ID
  var id: Int = -1 // 自分のID
  var self: Snake = Snake(x = 0, y = 0, color = Color.Red) // 自分の蛇
  var other = mutableStateMapOf<Int, Snake>() // 相手たちの蛇
  var speed: Int = 10 // ゲームの速度（実際に使われていない）

  private val logger: Logger = LoggerFactory.getLogger(SnakeGame::class.java)

  companion object {
    const val startLength = 5 // 蛇の初期長さ
    const val areaSize = 20 // ゲーム領域の大きさ
  }

  // キーボードのキーに関する情報
  enum class Key(val code: Long) {
    None(0L), Esc(116500987904L), Up(163745628160L), Right(168040595456L), Down(172335562752L), Left(159450660864L);

    companion object {
      fun of(code: Long) = values().firstOrNull { it.code == code } ?: None
    }
  }

  /**
   * @see GameObject
   */
  var gameObjects = mutableStateListOf<GameObject>()
  private val food = FoodData(5, 5) // 餌
  private var gameOver = true // ゲームオーバーフラグ、最初はtrue
  private var lastKey = Key.None // 最後に押されたキー

  private fun start() {
    gameOver = false
  }

  fun update() {
    if (gameOver) return
    handleInput() // キー入力を処理する

    // 蛇を移動する
    self.move()
    other.forEach { (_, snake) ->
      snake.move()
    }

    // 他の蛇との衝突をチェックする
    handleCollision()

    // 蛇の位置を更新する
    self.updateTiles()
    other.forEach { (_, snake) ->
      snake.updateTiles()
    }

    // 餌を食ったかチェックする
    handleEatenFood()

    // 得点を更新する
    calcScore()

    // 描画する四角を更新する
    updateGameObjects()
  }

  private fun calcScore() {
    self.score = self.body.size - startLength
    for (snake in other.values) {
      snake.score = snake.body.size - startLength
    }
  }

  private fun handleCollision() {
    // 自分の蛇の体に衝突した場合
    self.body.firstOrNull { it.x == self.px && it.y == self.py }?.let {
      fireDeadEvent()
    }
    // 他の蛇の体に衝突した場合
    other.forEach { (_, snake) ->
      snake.body.firstOrNull {
        it.x == self.px && it.y == self.py
      }?.let {
        fireDeadEvent()
      }
      if (snake.head.x == self.px && snake.head.y == self.py) {
        fireDeadEvent()
      }
    }
  }

  /**
   * 蛇が死んだ時に呼ばれるイベント
   */
  private fun fireDeadEvent() {
    self.direction = Direction.NONE
    self.dead = true
    // サーバに死亡情報を送る
    Client.send(
      SnakeGameMessage(
        System.currentTimeMillis(),
        sessionId,
        id,
        SnakeGameMessage.Action.DIE,
        Direction.NONE,
        0,
        0
      )
    )
  }

  private fun updateGameObjects() {
    // clearする
    gameObjects.clear()

    // すべての蛇と餌を改めて追加する
    gameObjects += food

    gameObjects += self.head
    gameObjects.addAll(self.body)

    other.forEach { (_, snake) ->
      gameObjects += snake.head
      gameObjects.addAll(snake.body)
    }
  }

  private fun handleEatenFood() {
    if ((food.x == self.px) && (food.y == self.py)) {
      self.length++
      food.x = Random.nextInt(areaSize)
      food.y = Random.nextInt(areaSize)
      //send eaten food message with new food position
      Client.send(
        SnakeGameMessage.makeEatMessage(
          sessionId,
          id,
          food.x,
          food.y
        )
      )
    }
  }

  private fun handleInput() = synchronized(this) {
    if (lastKey != Key.None) {
      when (lastKey) {
        Key.Up -> if (self.direction != Direction.DOWN) self.direction = Direction.UP
        Key.Right -> if (self.direction != Direction.LEFT) self.direction = Direction.RIGHT
        Key.Down -> if (self.direction != Direction.UP) self.direction = Direction.DOWN
        Key.Left -> if (self.direction != Direction.RIGHT) self.direction = Direction.LEFT
        else -> error("don't know how to handle $lastKey")
      }
      Client.send(SnakeGameMessage.makeMoveMessage(sessionId, id, self.direction))
      lastKey = Key.None
    }
  }

  fun registerKeyEvent(event: KeyEvent): Boolean = synchronized(this) {
    when {
      (Key.Esc.code == event.key.keyCode) -> true
      ((event.type != KeyEventType.KeyDown) || (lastKey != Key.None)) -> false
      else -> let { lastKey = Key.of(event.key.keyCode); false }
    }
  }

  /**
   * サーバから受け取ったメッセージを処理する
   */
  fun handleMessage(message: SnakeGameMessage) {
    logger.info("received message with id: ${message.playerId} with action ${message.action.name} direction ${message.direction.name}")
    when (message.action) {
      // 他の蛇が方向を変えた時
      SnakeGameMessage.Action.MOVE -> {
        other[message.playerId]!!.direction = message.direction
      }
      // 他の蛇が死んだ時
      SnakeGameMessage.Action.DIE -> {
        other[message.playerId]!!.direction = Direction.NONE
        other[message.playerId]!!.dead = true
      }
      // 他の蛇が餌を食べた時
      SnakeGameMessage.Action.EAT -> {
        other[message.playerId]!!.length += 1
        food.x = message.x
        food.y = message.y
      }
      // ゲームを停止した時
      SnakeGameMessage.Action.STOP -> {
        // stop the game
        gameOver = true
      }
      // ゲームを開始した時
      SnakeGameMessage.Action.NEW -> {
        food.x = message.x
        food.y = message.y
        start()
      }
      // 相手が加入している時
      SnakeGameMessage.Action.JOIN -> {
        if (message.playerId == id) {
          self = Snake(message.x, message.y, Color.Green)
          self.direction = message.direction
        } else {
          other[message.playerId] = Snake(message.x, message.y, Color.Yellow)
          other[message.playerId]!!.direction = message.direction
        }
      }
      else -> {

      }
    }
  }
}