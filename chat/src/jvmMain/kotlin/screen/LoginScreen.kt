package screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import data.User
import viewModel.LoginViewModel

@Composable
@Preview
fun LoginScreen(
  viewModel: LoginViewModel,
  onLogin: (User) -> Unit,
  onRegister: (User) -> Unit
) {
  val usernameError = remember { mutableStateOf(false) }
  val passwordError = remember { mutableStateOf(false) }

  Column(
    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(modifier = Modifier.fillMaxWidth(0.5f)) {
      TextField(
        value = viewModel.host.value,
        label = { Text("Server") },
        onValueChange = { viewModel.host.value = it }, singleLine = true
      )
      Spacer(modifier = Modifier.width(8.dp))
      TextField(
        value = viewModel.port.value.toString(),
        label = { Text("Port") },
        onValueChange = {
          val num = it.toIntOrNull()
          if (num != null) {
            viewModel.port.value = num
          }
        },
        singleLine = true,
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    TextField(
      modifier = Modifier.fillMaxWidth(0.5f),
      value = viewModel.username.value,
      label = { Text("Username") },
      onValueChange = { viewModel.username.value = it },
      singleLine = true,
      keyboardOptions = KeyboardOptions(
        autoCorrect = false,
      ),
      isError = usernameError.value,
    )
    Spacer(modifier = Modifier.height(4.dp))
    TextField(
      modifier = Modifier.fillMaxWidth(0.5f),
      value = viewModel.password.value,
      label = { Text("Password") },
      onValueChange = { viewModel.password.value = it },
      visualTransformation = PasswordVisualTransformation(),
      keyboardOptions = KeyboardOptions(
        keyboardType = KeyboardType.Password,
        autoCorrect = false,
      ),
      singleLine = true,
      isError = passwordError.value,
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      viewModel.errorMessage.value,
      color = MaterialTheme.colors.error,
      modifier = Modifier.fillMaxWidth(0.5f).height(16.dp),
      overflow = TextOverflow.Ellipsis
    )
    Spacer(modifier = Modifier.height(8.dp))
    Row(
      modifier = Modifier.fillMaxWidth(0.5f).height(36.dp),
      horizontalArrangement = Arrangement.Center,
    ) {
      Column(
        modifier = Modifier.weight(1f, true),
        horizontalAlignment = Alignment.Start
      ) {
        Button(
          modifier = Modifier.fillMaxSize(),
          onClick = {
            viewModel.login()
            viewModel.onLogin = {
              onLogin(it)
            }
          },
        ) {
          Text("Login")
        }
      }
      Spacer(modifier = Modifier.width(8.dp))
      Column(
        modifier = Modifier.weight(1f, true),
        horizontalAlignment = Alignment.End
      ) {
        Button(
          modifier = Modifier.fillMaxSize(),
          onClick = {
            viewModel.register()
            viewModel.onRegister = {
              onRegister(it)
            }
          },
        ) {
          Text("Register")
        }
      }
    }
  }
}
