import data.User

object AppContext{
  var user: User = User(-1, "empty")
  var token: String = ""
}