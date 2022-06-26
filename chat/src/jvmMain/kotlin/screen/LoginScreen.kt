package screen

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
  Column(
    modifier = Modifier.fillMaxWidth().fillMaxHeight(),
    verticalArrangement = Arrangement.Center,
    horizontalAlignment = Alignment.CenterHorizontally,
  ) {
    Row(modifier = Modifier.fillMaxWidth(0.5f)) {
      TextField(value = viewModel.host.value,
        label = { Text("Server") },
        onValueChange = { viewModel.host.value = it })
      Spacer(modifier = Modifier.width(8.dp))
      TextField(value = viewModel.port.value.toString(),
        label = { Text("Port") },
        onValueChange = { viewModel.port.value = it.toInt() })
    }
    Spacer(modifier = Modifier.height(4.dp))
    TextField(
      modifier = Modifier.fillMaxWidth(0.5f),
      value = viewModel.username.value,
      label = { Text("Username") },
      onValueChange = { viewModel.username.value = it }
    )
    Spacer(modifier = Modifier.height(4.dp))
    TextField(
      modifier = Modifier.fillMaxWidth(0.5f),
      value = viewModel.password.value,
      label = { Text("Password") },
      onValueChange = { viewModel.password.value = it },
      visualTransformation = PasswordVisualTransformation()
    )
    Spacer(modifier = Modifier.height(8.dp))
    Text(
      viewModel.state.value,
      color = Color.Red,
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
          },
        ) {
          Text("Register")
        }
      }
    }
  }
}
