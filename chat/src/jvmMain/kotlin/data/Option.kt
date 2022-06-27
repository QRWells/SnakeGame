package data

import androidx.compose.runtime.MutableState

abstract class Option<T>(
  var value: MutableState<T>,
  val onChange: (T) -> Unit
) {
  fun get(): MutableState<T> = value
  abstract fun set(value: T)
}

class BooleanOption(value: MutableState<Boolean>, onChange: (Boolean) -> Unit) :
  Option<Boolean>(value, onChange) {

  override fun set(value: Boolean) {
    this.value.value = value
    onChange(value)
  }
}

class IntOption(value: MutableState<Int>, onChange: (Int) -> Unit) :
  Option<Int>(value, onChange) {
  override fun set(value: Int) {
    this.value.value = value
    onChange(value)
  }
}

class StringOption(value: MutableState<String>, onChange: (String) -> Unit) :
  Option<String>(value, onChange) {
  override fun set(value: String) {
    this.value.value = value
    onChange(value)
  }
}