package data

class Contact(val name: String, val id: Int) {
  var historyList = mutableListOf<Message>()
}
