package wang.qrwells.snake.db

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.transactions.transaction
import wang.qrwells.net.server.ServerContext

object DBClient {
  init {
    Database.connect("jdbc:sqlite:user.sqlite", "org.sqlite.JDBC")
    transaction {
      SchemaUtils.create(Users, Groups, UserGroups)

      Users.selectAll().forEach {
        ServerContext.userGroup[it[Users.id].value] = hashSetOf()
      }
      UserGroups.selectAll().forEach {
        ServerContext.userGroup[it[UserGroups.userId].value]!!.add(it[UserGroups.groupId].value)
      }
    }
  }

  fun getUserByName(name: String): User? {
    var ret: User? = null
    transaction { ret = User.find { Users.name eq name }.firstOrNull() }
    return ret
  }

  fun getGroupByName(name: String): Group? {
    var ret: Group? = null
    transaction { ret = Group.find { Groups.name eq name }.firstOrNull() }
    return ret
  }

  fun getUserById(id: Int): User? {
    var ret: User? = null
    transaction { ret = User.findById(id) }
    return ret
  }

  fun getGroupById(id: Int): Group? {
    var ret: Group? = null
    transaction { ret = Group.findById(id) }
    return ret
  }

  fun addUser(name: String, password: String): User? {
    var ret: User? = null
    transaction {
      ret = User.new {
        this.name = name
        this.password = password
      }
    }
    return ret
  }

  fun addGroup(name: String): Group? {
    var ret: Group? = null
    transaction {
      if (Group.find { Groups.name eq name }.firstOrNull() == null) {
        ret = Group.new {
          this.name = name
        }
      } else {
        throw Exception("Group $name already exists")
      }
    }
    return ret
  }

  fun addUserToGroup(uId: Int, gId: Int) {
    transaction {
      UserGroups.insert {
        it[userId] = uId
        it[groupId] = gId
      }
    }
  }

  fun userInGroup(uId: Int, gId: Int): Boolean {
    var ret = false
    transaction {
      ret = UserGroups.select {
        (UserGroups.userId eq uId) and (UserGroups.groupId eq gId)
      }.firstOrNull() != null
    }
    return ret
  }

  fun getUsersOfGroup(group: Int): List<User> {
    var ret = emptyList<User>()
    transaction {
      val groupItem = Group.find { Groups.id eq group }.firstOrNull()
      if (groupItem != null) {
        ret = Users
          .join(UserGroups, JoinType.INNER, additionalConstraint = {
            Users.id eq UserGroups.userId
          })
          .slice(Users.columns).selectAll().map {
            User.wrapRow(it)
          }
      }
    }
    return ret
  }

  fun getGroupsOfUser(user: Int): List<Group> {
    var ret = emptyList<Group>()
    transaction {
      val userItem = User.find { Users.id eq user }.firstOrNull()
      if (userItem != null) {
        ret = Groups
          .join(UserGroups, JoinType.INNER, additionalConstraint = {
            Groups.id eq UserGroups.groupId
          })
          .slice(Groups.columns).selectAll().map {
            Group.wrapRow(it)
          }
      }
    }
    return ret
  }
}
