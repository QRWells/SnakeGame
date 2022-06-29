package wang.qrwells.snake.db

import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.Table

object Users : IntIdTable() {
  val name = varchar("name", 50)
  val password = varchar("password", 60)
}

object Groups : IntIdTable() {
  val name = varchar("name", 50)
}

// table for user-group relation
object UserGroups : Table() {
  val userId = reference("user_id", Users)
  val groupId = reference("group_id", Groups)

  override val primaryKey = PrimaryKey(userId, groupId, name = "PK_USER_GROUP")
}