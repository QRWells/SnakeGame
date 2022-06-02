package util

class EncryptionPassword : Encryption {
  override fun encryptionPassword(password: String): ByteArray {
    val passwordBytes = password.toByteArray(Charsets.UTF_8)
    val key = gammaKey(passwordBytes.size)
    return ByteArray(passwordBytes.size) {
      passwordBytes[it].toUByte().xor(key[it]).toByte()
    }
  }

  override fun decryptionPassword(encryptedPassword: ByteArray): String {
    val key = gammaKey(encryptedPassword.size)
    return ByteArray(encryptedPassword.size) {
      encryptedPassword[it].toUByte().xor(key[it]).toByte()
    }.toString(Charsets.UTF_8)
  }

  private fun gammaKey(length: Int): Array<UByte> {
    val a = 5u
    val c = 3u
    var g: UByte = 1u
    return Array(length) {
      if (it != 0)
        g = (a * g + c).toUByte()
      g
    }
  }
}
