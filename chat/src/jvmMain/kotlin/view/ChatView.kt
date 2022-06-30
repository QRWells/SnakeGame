package view

import AppContext
import Client
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.rememberDialogState
import components.History
import data.Contact
import data.Message
import viewModel.ChatViewModel
import wang.qrwells.message.impl.*
import java.time.LocalDateTime

@Composable
@Preview
fun ChatView(viewModel: ChatViewModel) {
  LaunchedEffect(Unit) {
    Client.addHandler("chatMessage") { _, m ->
      if (m is ChatMessage)
        if (m.isGroup) {
          val senderName = viewModel.contacts[m.senderId]?.name ?: "Unknown"
          viewModel.contacts[-m.receiverId]?.let {
            it.historyList.add(
              Message(
                m.senderId,
                senderName,
                m.receiverId,
                it.name,
                m.isGroup,
                m.message,
                LocalDateTime.now()
              )
            )
          }
        } else {
          viewModel.contacts[m.senderId]?.let {
            it.historyList.add(
              Message(
                m.senderId,
                it.name,
                m.receiverId,
                AppContext.user.name,
                m.isGroup,
                m.message,
                LocalDateTime.now()
              )
            )
          }
        }
    }
  }

  val dialogState = rememberDialogState()
  val searchDialog = remember { mutableStateOf(false) }
  val createDialog = remember { mutableStateOf(false) }
  Dialog(
    onCloseRequest = {
      Client.removeHandler("SearchForId")
      searchDialog.value = false
    },
    title = "Add a new contact",
    visible = searchDialog.value,
    state = dialogState
  ) {
    val id = remember { mutableStateOf("") }
    val errorMessage = mutableStateOf("")
    val name = remember { mutableStateOf("") }
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
                if (selectedValue.value == "People") {
                  Client.send(
                    IdRequestMessage(
                      selectedValue.value == "Group",
                      id.value.toInt()
                    )
                  )
                } else {
                  Client.send(
                    JoinToGroupMessage(
                      id.value.toInt(),
                      AppContext.user.id,
                    )
                  )
                }
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
                if (selectedValue.value != "People" || id.value.toInt() != AppContext.user.id) {
                  val sessionId = if (selectedValue.value == "People") {
                    id.value.toInt()
                  } else {
                    -id.value.toInt()
                  }
                  searchDialog.value = false
                  viewModel.contacts[sessionId] = Contact(
                    name.value,
                    id.value.toInt(),
                    selectedValue.value == "Group"
                  )
                } else {
                  errorMessage.value = "You can't add yourself"
                }
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

  Dialog(
    onCloseRequest = {
      Client.removeHandler("CreateGroup")
      createDialog.value = false
    },
    title = "Create a new group",
    visible = createDialog.value,
    state = dialogState
  ) {
    val name = remember { mutableStateOf("") }
    val errorMessage = mutableStateOf("")
    val groupId = remember { mutableStateOf("") }

    Column {
      Row(modifier = Modifier.fillMaxWidth().weight(1f)) {
        TextField(
          value = name.value,
          label = { Text("Name") },
          onValueChange = { name.value = it },
          singleLine = true,
        )
      }
      Row(
        modifier = Modifier.fillMaxWidth().weight(1f),
        horizontalArrangement = Arrangement.SpaceAround
      ) {
        Text(errorMessage.value, color = MaterialTheme.colors.error)
        Button(
          onClick = {
            if (name.value.isNotEmpty()) {
              createDialog.value = false
              Client.addHandler("CreateGroup") { _, m ->
                if (m is ResponseMessage) {
                  when (m.status) {
                    ResponseMessage.Status.CREATED -> {
                      createDialog.value = false
                      groupId.value = m.detail
                    }
                    ResponseMessage.Status.ERROR -> {
                      errorMessage.value = m.detail
                    }
                  }
                }
              }
              Client.send(CreateGroupMessage(name.value))
            } else {
              errorMessage.value = "Name can't be empty"
            }
          },
        ) {
          Text("Create")
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
            .padding(4.dp)
        ) {
          Column {
            for (item in viewModel.contacts) {
              Card(
                modifier = Modifier.fillMaxWidth().height(60.dp).padding(4.dp)
                  .clickable {
                    viewModel.with.value = item.value
                    viewModel.history = item.value.historyList
                  },
              ) {
                Column(
                  modifier = Modifier.fillMaxSize().padding(4.dp),
                  verticalArrangement = Arrangement.Center,
                  horizontalAlignment = Alignment.Start
                ) {
                  Text(item.value.name, fontSize = 24.sp)
                  if (item.value.historyList.isEmpty()) {
                    Text("", fontSize = 12.sp)
                  } else {
                    val prefix =
                      if (item.value.historyList.last().sender == AppContext.user.id) {
                        "You: "
                      } else {
                        "${item.value.name}: "
                      }
                    Text(
                      prefix + item.value.historyList.last().message,
                      fontSize = 12.sp
                    )
                  }
                }
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
            searchDialog.value = true
          },
          elevation = FloatingActionButtonDefaults.elevation(),
        ) {
          Icon(
            Icons.Default.Search,
            "Search",
            Modifier.size(24.dp)
          )
        }
        FloatingActionButton(
          modifier = Modifier.padding(16.dp).size(36.dp)
            .align(Alignment.BottomStart),
          onClick = {
            createDialog.value = true
          },
          elevation = FloatingActionButtonDefaults.elevation(),
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

      Text(viewModel.with.value.name, fontSize = 24.sp)

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
            .heightIn(32.dp, 64.dp).padding(8.dp)
        )
        Button(
          onClick = {
            if (viewModel.with.value.id > 0)
              text.trim().let {
                text = ""
                if (it.isNotEmpty()) {
                  val message = Message(
                    viewModel.self.id,
                    viewModel.self.name,
                    viewModel.with.value.id,
                    viewModel.with.value.name,
                    viewModel.with.value.group,
                    it,
                    LocalDateTime.now()
                  )
                  viewModel.history.add(
                    message
                  )
                  viewModel.send(message)
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