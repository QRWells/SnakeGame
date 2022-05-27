package model

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.Message

class ChatViewModel {
  var text = mutableStateOf("")
  var history = mutableStateListOf<Message>()
}