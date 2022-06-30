package db

import androidx.compose.ui.res.useResource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction
import java.io.File

object DAOImpl : DAO {
  private val db by lazy {
    val file = File("data.db")
    if (!file.exists()) {
      file.createNewFile()
      useResource("data.db") {
        it.copyTo(file.outputStream())
      }
    }
    val db = Database.connect("jdbc:sqlite:data.db", "org.sqlite.JDBC")
    transaction(db) {
      addLogger(StdOutSqlLogger)
      if (!SchemaUtils.checkCycle(HistoryTable)) {
        SchemaUtils.create(HistoryTable)
      }
    }
    db
  }

  override suspend fun getHistory(
    thisUserId: Int,
    userId: Int
  ): List<DBHistory> {
    TODO("Not yet implemented")
  }

  override suspend fun addHistory(userId: Int, data: String): DBHistory {
    TODO("Not yet implemented")
  }
}
