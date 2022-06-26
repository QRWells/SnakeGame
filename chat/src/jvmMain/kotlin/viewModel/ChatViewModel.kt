package viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.Contact
import data.Message

class ChatViewModel : ViewModel() {
  var text = mutableStateOf("")
  var contacts = mutableMapOf<Int, Contact>()
  var history = mutableStateListOf<Message>()
  var self: Int = kotlin.random.Random.nextInt()
  var with: Int = kotlin.random.Random.nextInt()
}