package util

interface Encryption {
  fun encryptionPassword(password: String): ByteArray
  fun decryptionPassword(encryptedPassword: ByteArray): String
}
