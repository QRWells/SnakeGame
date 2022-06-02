package data

import java.time.LocalDateTime

data class Message(
  val sender: Long,
  val receiver: Long,
  val message: String,
  val time: LocalDateTime
)
