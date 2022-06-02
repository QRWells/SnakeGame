package viewModel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.Message

class ChatViewModel : ViewModel() {
  var text = mutableStateOf("")
  var history = mutableStateListOf<Message>()
  var self: Long = kotlin.random.Random.nextLong()
  var with: Long = kotlin.random.Random.nextLong()
}