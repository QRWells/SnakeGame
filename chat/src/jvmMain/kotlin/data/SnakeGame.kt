package data

import Client
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.type
import wang.qrwells.message.impl.SnakeGameMessage
import wang.qrwells.message.impl.SnakeGameMessage.Direction
import wang.qrwells.net.Connection
import kotlin.random.Random

sealed class GameObject(x: Int, y: Int, color: Color) {
  var x by mutableStateOf(x)
  var y by mutableStateOf(y)
  var color by mutableStateOf(color)
}

class SnakeHeadData(x: Int, y: Int, color: Color) : GameObject(x, y, color)
class SnakeBodyData(x: Int, y: Int, color: Color) : GameObject(x, y, color)
class FoodData(x: Int, y: Int) : GameObject(x, y, Color.Magenta)

class Snake(
  x: Int,
  y: Int,
  private val color: Color,
  private val wall: Boolean = false
) {
  val score = 0
  var head = SnakeHeadData(x, y, color)
  var body = mutableListOf(SnakeBodyData(x, y, color))
  var direction = Direction.NONE
  var length = SnakeGame.startLength
  var px = x
  var py = y

  fun move() {
    when (direction) {
      Direction.UP -> py--
      Direction.DOWN -> py++
      Direction.LEFT -> px--
      Direction.RIGHT -> px++
      else -> {}
    }
    if (wall) {
      when {
        (px < 0) -> px = SnakeGame.areaSize - 1
        (px >= SnakeGame.areaSize) -> px = 0
        (py < 0) -> py = SnakeGame.areaSize - 1
        (py >= SnakeGame.areaSize) -> py = 0
      }
    }
  }

  fun updateTiles() {
    body += SnakeBodyData(head.x, head.y, color)
    head.x = px
    head.y = py
    repeat(2) {
      if (body.size > length) body.removeFirst()
    }
  }
}

class SnakeGame(
  width: Int,
  height: Int,
  private val id: Int,
  private val self: Snake,
  private val other: Map<Int, Snake>,
  val speed: Int = 10,
) {
  companion object {
    const val startLength = 5
    const val areaSize = 20
  }

  enum class Key(val code: Long) {
    None(0L),
    Esc(116500987904L),
    Up(163745628160L),
    Right(168040595456L),
    Down(172335562752L),
    Left(159450660864L);

    companion object {
      fun of(code: Long) = values().firstOrNull { it.code == code } ?: None
    }
  }

  val gameObjects = mutableStateListOf<GameObject>()
  private val food = FoodData(areaSize * 3 / 4, areaSize * 3 / 4)
  private var gameOver = false
  private var lastKey = Key.None

  fun update() {
    handleInput()

    self.move()
    other.forEach { (_, snake) ->
      snake.move()
    }

    handleCollision()

    self.updateTiles()
    other.forEach { (_, snake) ->
      snake.updateTiles()
    }

    handleEatenFood()

    calcScore()

    updateGameObjects()
  }

  private fun calcScore() {
    // todo: calc score
  }

  private fun handleCollision() {
    self.body.firstOrNull { it.x == self.head.x && it.y == self.head.y }?.let {
      // todo: fire dead event
    }
    other.forEach { (_, snake) ->
      snake.body.firstOrNull {
        it.x == self.head.x && it.y == self.head.y
      }?.let {
        // todo: fire dead event
      }
      if (snake.head.x == self.head.x && snake.head.y == self.head.y) {
        // todo: fire dead event
      }
    }
  }

  private fun updateGameObjects() {
    gameObjects.clear()
    gameObjects += food
    gameObjects.add(self.head)
    gameObjects.addAll(self.body)
    other.forEach { (_, snake) ->
      gameObjects.add(snake.head)
      gameObjects.addAll(snake.body)
    }
  }

  private fun handleEatenFood() {
    if ((food.x == self.px) && (food.y == self.py)) {
      self.length++
      food.x = Random.nextInt(areaSize)
      food.y = Random.nextInt(areaSize)
      // send eaten food message with new food position
      Client.send(
        SnakeGameMessage.makeMessage(
          id,
          SnakeGameMessage.Action.EAT,
          food.x,
          food.y
        )
      )
    }
  }

  private fun handleInput() = synchronized(this) {
    if (lastKey != Key.None) {
      when (lastKey) {
        Key.Up -> if (self.direction != Direction.DOWN) self.direction =
          Direction.UP
        Key.Right -> if (self.direction != Direction.LEFT) self.direction =
          Direction.RIGHT
        Key.Down -> if (self.direction != Direction.UP) self.direction =
          Direction.DOWN
        Key.Left -> if (self.direction != Direction.RIGHT) self.direction =
          Direction.LEFT
        else -> error("don't know how to handle $lastKey")
      }
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

  fun handleMessage(connection: Connection, message: SnakeGameMessage) {
    if (!other.containsKey(message.playerId))
      return

    when (message.action) {
      SnakeGameMessage.Action.MOVE -> {
        message.playerId.let { playerId ->
          other[playerId]?.direction = message.direction
        }
      }
      SnakeGameMessage.Action.DIE -> {
        message.playerId.let { playerId ->
          other[playerId]?.direction = Direction.NONE
        }
      }
      SnakeGameMessage.Action.EAT -> {
        message.playerId.let { playerId ->
          ++other[playerId]!!.length
        }
        food.x = message.x
        food.y = message.y
      }
      SnakeGameMessage.Action.START -> {
        // start the game
      }
      SnakeGameMessage.Action.STOP -> {
        // stop the game
      }
      else -> {
        TODO()
      }
    }
  }
}