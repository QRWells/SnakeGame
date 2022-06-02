package viewModel

import androidx.compose.runtime.mutableStateOf
import data.User
import wang.qrwells.message.impl.ChatMessage
import wang.qrwells.message.impl.RegisterMessage
import wang.qrwells.net.Connection
import wang.qrwells.net.tcp.TCPClient

class LoginViewModel : ViewModel() {
  var host = mutableStateOf("localhost")
  var port = mutableStateOf(8080)
  var username = mutableStateOf("")
  var password = mutableStateOf("")
  val client = TCPClient()


  fun login() {
    if (!client.connected) client.setHost(host.value).setPort(port.value)
      .connect()
    client.connection.send(null)
    client.connection.addMessageHandler(::onMessage)
  }

  fun register() {
    if (!client.connected) client.setHost(host.value).setPort(port.value)
      .connect()
    client.connection.send(null)
    client.connection.addMessageHandler(::onRegister)
  }

  fun onLoginSuccess(user: User) {
    client.connection.removeMessageHandler(::onMessage)
  }

  private fun onMessage(connection: Connection, message: ChatMessage) {

  }


  private fun onRegister(connection: Connection, message: RegisterMessage) {
    TODO("Not yet implemented")
  }
}