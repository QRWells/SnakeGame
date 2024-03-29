package data

import java.time.LocalDateTime

data class Message(
  val sender: Int,
  val senderName: String,
  val receiver: Int,
  val receiverName: String,
  val group: Boolean,
  val message: String,
  val time: LocalDateTime
)
