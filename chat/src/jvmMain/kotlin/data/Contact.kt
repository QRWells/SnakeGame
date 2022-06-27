package data

class Contact(val name: String, val id: Int, group: Boolean = false) {
  var historyList = mutableListOf<Message>()
}
