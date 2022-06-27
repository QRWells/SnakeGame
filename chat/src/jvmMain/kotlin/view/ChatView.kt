package view

import Client
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import components.History
import data.Contact
import data.Message
import viewModel.ChatViewModel
import wang.qrwells.message.impl.IdRequestMessage
import wang.qrwells.message.impl.ResponseMessage
import java.time.LocalDateTime

@Composable
@Preview
fun ChatView(viewModel: ChatViewModel) {
  val dialogState = rememberDialogState()
  val addDialog = remember { mutableStateOf(false) }

  Dialog(
    onCloseRequest = {
      Client.removeHandler("SearchForId")
      addDialog.value = false
    },
    title = "Add a new contact",
    visible = addDialog.value,
    state = dialogState
  ) {
    val id = remember { mutableStateOf("") }
    val errorMessage = mutableStateOf("")
    val name = mutableStateOf("")
    val addEnabled = remember { mutableStateOf(false) }

    Row(
      horizontalArrangement = Arrangement.Center,
      verticalAlignment = Alignment.CenterVertically,
      modifier = Modifier.fillMaxSize().padding(16.dp)
    ) {
      val selectedValue = remember { mutableStateOf("People") }
      Column(
        modifier = Modifier.fillMaxHeight().fillMaxWidth(0.9f),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
      ) {
        Row(modifier = Modifier.fillMaxWidth().height(48.dp)) {
          Text(
            "Search for:",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(0.dp, 16.dp, 0.dp, 8.dp)
          )
          Column(Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = selectedValue.value == "People",
                onClick = { selectedValue.value = "People" }
              )
              Text(
                text = "People",
              )
            }
          }
          Column(Modifier.weight(1f)) {
            Row(
              verticalAlignment = Alignment.CenterVertically
            ) {
              RadioButton(
                selected = selectedValue.value == "Group",
                onClick = { selectedValue.value = "Group" }
              )
              Text(
                text = "Group",
              )
            }
          }
        }
        Row(modifier = Modifier.fillMaxWidth()) {
          TextField(
            value = id.value,
            label = { Text("ID") },
            onValueChange = {
              addEnabled.value = false
              if (it.isNotEmpty()) {
                try {
                  it.toInt()
                  id.value = it
                } catch (_: java.lang.NumberFormatException) {

                }
              } else {
                id.value = it
              }
            },
            singleLine = true,
          )
        }
        Row(modifier = Modifier.fillMaxWidth().height(32.dp)) {
          Text(errorMessage.value, color = MaterialTheme.colors.error)
        }
        Row(
          modifier = Modifier.fillMaxWidth(),
          verticalAlignment = Alignment.CenterVertically,
          horizontalArrangement = Arrangement.Center
        ) {
          Column(
            Modifier.weight(1f),
            horizontalAlignment = Alignment.Start
          ) {
            Button(
              onClick =
              {
                Client.addHandler("SearchForId") { _, m ->
                  if (m is ResponseMessage) {
                    when (m.status) {
                      ResponseMessage.Status.OK -> {
                        addEnabled.value = true
                        name.value = m.detail
                      }
                      ResponseMessage.Status.ERROR -> {
                        errorMessage.value = m.detail
                      }
                    }
                  }
                }
                Client.send(
                  IdRequestMessage(
                    selectedValue.value == "Group",
                    id.value.toInt()
                  )
                )
              },
              modifier = Modifier.fillMaxWidth(0.8f)
            ) {
              Text(
                text = "Search",
              )
            }
          }
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.End
          ) {
            Button(
              enabled = addEnabled.value,
              onClick = {
                addDialog.value = false
                viewModel.contacts[id.value.toInt()] = Contact(
                  name.value,
                  id.value.toInt(),
                  selectedValue.value == "Group"
                )
              },
              modifier = Modifier.fillMaxWidth(0.8f)
            ) {
              Text(
                text = "Add"
              )
            }
          }
        }
      }
    }
  }


  Row(Modifier.fillMaxSize()) {
    Column(modifier = Modifier.weight(3f, true)) {
      Box(
        modifier = Modifier.fillMaxSize()
      ) {
        val stateVertical = rememberScrollState(0)
        Box(
          modifier = Modifier.fillMaxSize().verticalScroll(stateVertical)
            .padding(end = 12.dp, bottom = 12.dp)
        ) {
          Column {
            for (item in viewModel.contacts) {
              Card {
                Text(item.value.name, fontSize = 24.sp)
                Text(item.value.historyList.last().message, fontSize = 16.sp)
              }
              Spacer(modifier = Modifier.height(4.dp))
            }
          }
        }
        VerticalScrollbar(
          modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
          adapter = rememberScrollbarAdapter(stateVertical)
        )
        FloatingActionButton(
          modifier = Modifier.padding(16.dp).size(36.dp)
            .align(Alignment.BottomEnd),
          onClick = {
            addDialog.value = true
          }
        ) {
          Icon(
            Icons.Default.Add,
            "Add",
            Modifier.size(24.dp)
          )
        }
      }
    }


    Column(
      modifier = Modifier.weight(6f, true)
    ) {
      var text by rememberSaveable { viewModel.text }
      History(
        modifier = Modifier.fillMaxSize().padding(8.dp)
          .clip(shape = RoundedCornerShape(4.dp)).weight(8f, true),
        viewModel.history, viewModel.self
      )

      Box(
        Modifier.heightIn(min = 32.dp, max = 64.dp).fillMaxSize()
          .clip(shape = RoundedCornerShape(4.dp))
          .weight(2f, true)
      ) {
        OutlinedTextField(
          text,
          onValueChange = { text = it },
          modifier = Modifier.align(Alignment.TopStart).fillMaxSize()
            .heightIn(32.dp, 64.dp).padding(8.dp).onPreviewKeyEvent {
              println(it.key.keyCode);false
            }
        )
        Button(
          onClick = {
            text.trim().let {
              text = ""
              if (it.isNotEmpty()) {
                val message = Message(
                  viewModel.self.id,
                  viewModel.self.name,
                  viewModel.with.id,
                  viewModel.with.name,
                  it,
                  LocalDateTime.now()
                )

                viewModel.history.add(
                  message
                )
              }
            }
          },
          modifier = Modifier.align(Alignment.BottomEnd).padding(12.dp)
        ) {
          Text("Send")
        }
      }
    }
  }
}