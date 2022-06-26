package data

import java.time.LocalDateTime

data class Message(
  val sender: Int,
  val receiver: Int,
  val message: String,
  val time: LocalDateTime
)
