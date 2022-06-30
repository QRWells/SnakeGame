package viewModel

import AppContext
import Client
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.Contact
import data.Message
import data.User
import db.HistoryTable
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction
import wang.qrwells.message.impl.ChatMessage
import java.time.LocalDateTime

class ChatViewModel : ViewModel() {
  fun send(message: Message) {
    Client.send(
      ChatMessage(message.sender, message.receiver, with.value.group, message.message)
    )
  }

  fun loadHistories() {
    transaction {
      SchemaUtils.create(HistoryTable)

      HistoryTable.selectAll()
        .forEach {
          val message = Message(
            it[HistoryTable.senderId],
            it[HistoryTable.senderName],
            it[HistoryTable.receiverId],
            it[HistoryTable.receiverName],
            it[HistoryTable.groupMessage] != 0,
            it[HistoryTable.message],
            LocalDateTime.parse(it[HistoryTable.date])
          )
          if (message.group) {
            if (!contacts.containsKey(-it[HistoryTable.sessionId]))
              contacts[it[HistoryTable.sessionId]] = Contact(
                it[HistoryTable.sessionName],
                it[HistoryTable.sessionId],
                true
              )
            contacts[-it[HistoryTable.sessionId]]?.historyList?.add(message)
          } else {
            if (!contacts.containsKey(it[HistoryTable.receiverId]))
              contacts[it[HistoryTable.sessionId]] = Contact(
                it[HistoryTable.sessionName],
                it[HistoryTable.sessionId],
                false
              )
            contacts[it[HistoryTable.sessionId]]?.historyList?.add(message)
          }
        }
    }
  }

  var text = mutableStateOf("")
  var contacts = mutableMapOf<Int, Contact>()
  var history = mutableStateListOf<Message>()
  var self: User = AppContext.user
  var with = mutableStateOf(Contact("", -1))
}