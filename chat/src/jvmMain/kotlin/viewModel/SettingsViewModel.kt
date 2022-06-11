package viewModel

import data.Option

class SettingsViewModel : ViewModel() {
  val options: Map<String, Option<*>> = emptyMap()
}