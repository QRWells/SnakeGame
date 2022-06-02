package db

interface DAO {
  suspend fun getHistory(thisUserId: Int, userId: Int): List<DBHistory>
  suspend fun addHistory(userId: Int, data: String): DBHistory
}
