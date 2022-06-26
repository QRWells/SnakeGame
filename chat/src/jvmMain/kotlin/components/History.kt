package components

import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import data.Message

@Composable
@Preview
fun History(modifier: Modifier, list: SnapshotStateList<Message>, self: Int) {
  Box(modifier = modifier) {
    val listState = rememberLazyListState()
    LazyColumn(
      Modifier.fillMaxSize().padding(8.dp),
      state = listState
    ) {
      items(list) { message ->
        MessageRow(message, self = message.sender == self)
        Spacer(modifier = Modifier.height(4.dp))
      }
    }
    VerticalScrollbar(
      modifier = Modifier.align(Alignment.CenterEnd).fillMaxHeight(),
      adapter = rememberScrollbarAdapter(
        scrollState = listState
      ),
      reverseLayout = true
    )
  }
}
