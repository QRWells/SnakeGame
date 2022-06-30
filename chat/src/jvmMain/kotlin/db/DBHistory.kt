package db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object HistoryTable : IntIdTable("history_table") {
  val sessionId: Column<Int> = integer("session_id")
  val sessionName: Column<String> = varchar("session_name", 50)
  val senderId: Column<Int> = integer("sender_id")
  val senderName: Column<String> = varchar("sender_name", 50)
  val receiverId: Column<Int> = integer("receiver_id")
  val receiverName: Column<String> = varchar("receiver_name", 50)
  val groupMessage: Column<Int> = integer("group_message")
  val date: Column<String> = varchar("date", 32)
  val message: Column<String> = text("data")
}

class DBHistory(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<DBHistory>(HistoryTable)

  var sessionId by HistoryTable.sessionId
  var sessionName by HistoryTable.sessionName
  var senderId by HistoryTable.senderId
  var senderName by HistoryTable.senderName
  var receiverId by HistoryTable.receiverId
  var receiverName by HistoryTable.receiverName
  var groupMessage by HistoryTable.groupMessage
  var date by HistoryTable.date
  var message by HistoryTable.message
}
