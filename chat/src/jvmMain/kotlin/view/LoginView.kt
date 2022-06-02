package view

import androidx.compose.foundation.layout.Column
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import data.User
import viewModel.LoginViewModel

@Composable
fun LoginView(viewModel: LoginViewModel, onLogin: (User) -> Unit) {
  var username by rememberSaveable { mutableStateOf("") }
  var password by rememberSaveable { mutableStateOf("") }
  var server by rememberSaveable { mutableStateOf("localhost:8888") }
  Column {
    TextField(value = server,
      label = { Text("Server") },
      onValueChange = { server = it })
    TextField(
      value = username,
      label = { Text("Username") },
      onValueChange = { username = it }
    )
    TextField(
      value = password,
      label = { Text("Password") },
      onValueChange = { password = it }
    )
    Button(
      onClick = {
      }
    ) {
      Text("Login")
    }
  }
}
