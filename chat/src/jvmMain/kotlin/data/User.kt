package data

data class User(val id: Int, val name: String, val password: ByteArray) {
  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as User

    if (id != other.id) return false
    if (name != other.name) return false
    if (!password.contentEquals(other.password)) return false

    return true
  }

  override fun hashCode(): Int {
    var result = id
    result = 31 * result + name.hashCode()
    result = 31 * result + password.contentHashCode()
    return result
  }
}
