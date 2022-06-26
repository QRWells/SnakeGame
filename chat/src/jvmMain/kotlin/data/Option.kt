package data

abstract class Option<T>(var value: T) {
  fun get(): T = value
  abstract fun set(value: T)
}

class BooleanOption(value: Boolean) : Option<Boolean>(value){
  override fun set(value: Boolean) {
    this.value = value
  }
}

class IntOption(value: Int) : Option<Int>(value){
  override fun set(value: Int) {
    this.value = value
  }
}

class StringOption(value: String) : Option<String>(value){
  override fun set(value: String) {
    this.value = value
  }
}