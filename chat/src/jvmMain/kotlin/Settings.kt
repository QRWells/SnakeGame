import androidx.compose.runtime.mutableStateMapOf
import data.Option

object Settings {
  val options: MutableMap<String, Option<*>> = mutableStateMapOf()
}