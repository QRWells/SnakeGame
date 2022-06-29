package wang.qrwells.snake.db

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID

class User(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<User>(Users)

  var name by Users.name
  var password by Users.password
}

class Group(id: EntityID<Int>) : IntEntity(id) {
  companion object : IntEntityClass<Group>(Groups)

  var name by Groups.name
  var users by User via UserGroups
}