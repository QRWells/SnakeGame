package db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Column

object HistoryTable : IntIdTable("history_table") {
  val senderId: Column<Long> = long("sender_id")
  val receiverId: Column<Long> = long("receiver_id")
  val date: Column<String> = varchar("date", 20)
  val text: Column<String> = text("data")
}

class DBHistory(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<DBHistory>(HistoryTable)

  var senderId by HistoryTable.senderId
  var receiverId by HistoryTable.receiverId
  var date by HistoryTable.date
  var text by HistoryTable.text
}
