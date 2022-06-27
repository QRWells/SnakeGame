package viewModel

import Client
import androidx.compose.runtime.mutableStateOf
import data.User
import org.slf4j.LoggerFactory
import wang.qrwells.message.impl.LoginMessage
import wang.qrwells.message.impl.RegisterMessage
import wang.qrwells.message.impl.ResponseMessage

class LoginViewModel : ViewModel() {
  var host = mutableStateOf("localhost")
  var port = mutableStateOf(8888)
  var username = mutableStateOf("")
  var password = mutableStateOf("")
  var errorMessage = mutableStateOf("")

  var onLogin: (User) -> Unit = {}
  var onRegister: (User) -> Unit = {}

  fun login() {
    errorMessage.value = "Login..."
    // check username and password
    if (username.value.isEmpty() || password.value.isEmpty()) {
      errorMessage.value = "Username and password cannot be empty"
      return
    }

    try {
      Client.connect(host.value, port.value)
      //wait until connected
      Client.addHandler("loginHandler") { _, m ->
        if (m is ResponseMessage)
          when (m.status) {
            ResponseMessage.Status.OK -> {
              LoggerFactory.getLogger(LoginViewModel::class.java)
                .info("Login success")
              errorMessage.value = "Login success"
              onLogin(User(m.detail.toInt(), username.value))
            }
            ResponseMessage.Status.ERROR -> {
              errorMessage.value = m.detail
            }
          }
      }
      // encrypt password on the client side is meaningless
      val message = LoginMessage(username.value, password.value)
      while (!Client.isConnected) {
        Thread.sleep(100)
      }
      Client.send(message)
    } catch (e: Exception) {
      errorMessage.value = e.message ?: "Unknown error"
    }
  }

  fun register() {
    errorMessage.value = "Register..."
    // check username and password
    if (username.value.isEmpty() || password.value.isEmpty()) {
      errorMessage.value = "Username and password cannot be empty"
      return
    }
    //check strength of password
    if (password.value.length < 6) {
      errorMessage.value = "Password too short"
      return
    }

    try {
      Client.connect(host.value, port.value)
      while (!Client.isConnected) {
        Thread.sleep(100)
      }
      Client.addHandler("registerHandler") { _, m ->
        if (m is ResponseMessage) {
          when (m.status) {
            ResponseMessage.Status.CREATED -> {
              errorMessage.value = "Successfully registered"
              onRegister(User(m.detail.toInt(), username.value))
            }
            ResponseMessage.Status.ERROR -> {
              errorMessage.value = m.detail
            }
          }
        }
      }
      // encrypt password on the client side is meaningless
      val message = RegisterMessage(username.value, password.value)
      Client.send(message)
    } catch (e: Exception) {
      errorMessage.value = e.message ?: "Unknown error"
    }
  }
}