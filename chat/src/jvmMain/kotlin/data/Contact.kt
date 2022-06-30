package data

import androidx.compose.runtime.mutableStateListOf

class Contact(val name: String, val id: Int, val group: Boolean = false) {
  var historyList = mutableStateListOf<Message>()
}
