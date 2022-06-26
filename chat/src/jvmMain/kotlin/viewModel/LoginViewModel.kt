package viewModel

import Client
import androidx.compose.runtime.mutableStateOf
import data.User
import wang.qrwells.message.impl.ChatMessage
import wang.qrwells.message.impl.RegisterMessage
import wang.qrwells.net.Connection

class LoginViewModel : ViewModel() {
  var host = mutableStateOf("localhost")
  var port = mutableStateOf(8080)
  var username = mutableStateOf("")
  var password = mutableStateOf("")
  var state = mutableStateOf("")

  fun login() {
    try {
      Client.connect(host.value, port.value)
    } catch (e: Exception) {
      state.value = e.message ?: "Unknown error"
    }
  }

  fun register() {
  }

  fun onLoginSuccess(user: User) {
  }

  private fun onMessage(connection: Connection, message: ChatMessage) {

  }


  private fun onRegister(connection: Connection, message: RegisterMessage) {
    TODO("Not yet implemented")
  }
}