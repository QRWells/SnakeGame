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

sealed class GameObject(x: Int, y: Int, color: Color) {
  var x by mutableStateOf(x)
  var y by mutableStateOf(y)
  var color by mutableStateOf(color)
}

class SnakeHeadData(x: Int, y: Int, color: Color) : GameObject(x, y, color)
class SnakeBodyData(x: Int, y: Int, color: Color) : GameObject(x, y, color)
class FoodData(x: Int, y: Int) : GameObject(x, y, Color.Blue)

class Snake(
  x: Int, y: Int, private val color: Color
) {
  var score = 0
  var head = SnakeHeadData(x, y, color)
  var body = mutableStateListOf<SnakeBodyData>()
  var direction = Direction.RIGHT
  var length = SnakeGame.startLength
  var px = x
  var py = y
  var dead = false

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

  fun updateTiles() {
    body += SnakeBodyData(head.x, head.y, color)
    head.x = px
    head.y = py
    repeat(2) {
      if (body.size > length) body.removeFirst()
    }
  }
}

class SnakeGame {
  var sessionId: Int = -1
  var id: Int = -1
  var self: Snake = Snake(x = 0, y = 0, color = Color.Red)
  var other = mutableStateMapOf<Int, Snake>()
  var speed: Int = 10

  private val logger: Logger = LoggerFactory.getLogger(SnakeGame::class.java)

  companion object {
    const val startLength = 5
    const val areaSize = 20
  }

  enum class Key(val code: Long) {
    None(0L), Esc(116500987904L), Up(163745628160L), Right(168040595456L), Down(172335562752L), Left(159450660864L);

    companion object {
      fun of(code: Long) = values().firstOrNull { it.code == code } ?: None
    }
  }

  var gameObjects = mutableStateListOf<GameObject>()
  private val food = FoodData(5, 5)
  private var gameOver = true
  private var lastKey = Key.None

  private fun start() {
    gameOver = false
  }

  fun update() {
    if (gameOver) return
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
    self.score = self.body.size - startLength
    for (snake in other.values) {
      snake.score = snake.body.size - startLength
    }
  }

  private fun handleCollision() {
    self.body.firstOrNull { it.x == self.px && it.y == self.py }?.let {
      fireDeadEvent()
    }
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

  private fun fireDeadEvent() {
    self.direction = Direction.NONE
    self.dead = true
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
    gameObjects.clear()
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

  fun handleMessage(message: SnakeGameMessage) {
    logger.info("received message with id: ${message.playerId} with action ${message.action.name} direction ${message.direction.name}")
    when (message.action) {
      SnakeGameMessage.Action.MOVE -> {
        other[message.playerId]!!.direction = message.direction
      }
      SnakeGameMessage.Action.DIE -> {
        other[message.playerId]!!.direction = Direction.NONE
        other[message.playerId]!!.dead = true
      }
      SnakeGameMessage.Action.EAT -> {
        other[message.playerId]!!.length += 1
        food.x = message.x
        food.y = message.y
      }
      SnakeGameMessage.Action.STOP -> {
        // stop the game
        gameOver = true
      }
      SnakeGameMessage.Action.NEW -> {
        food.x = message.x
        food.y = message.y
        start()
      }
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