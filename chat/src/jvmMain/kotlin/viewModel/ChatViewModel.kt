package viewModel

import AppContext
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.Contact
import data.Message
import data.User

class ChatViewModel : ViewModel() {
  var text = mutableStateOf("")
  var contacts = mutableMapOf<Int, Contact>()
  var history = mutableStateListOf<Message>()
  var self: User = AppContext.user
  var with: Contact = Contact("", -1)
}