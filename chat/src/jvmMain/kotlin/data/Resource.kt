package data

data class Resource<T>(
  val status: Status,
  val data: T? = null,
  val error: Throwable? = null
) {
  fun ifError(block: (error: Throwable) -> Unit): Resource<T> {
    if (status.isError()) {
      block(error!!)
    }
    return this
  }

  fun ifSuccess(block: (data: T?) -> Unit): Resource<T> {
    if (status.isSuccess()) {
      block(data)
    }
    return this
  }

  fun ifLoading(block: (data: T?) -> Unit): Resource<T> {
    if (status.isLoading()) {
      block(data)
    }
    return this
  }

  fun ifDone(block: () -> Unit): Resource<T> {
    if (status.isDone()) {
      block()
    }
    return this
  }

  companion object {

    fun <T> success(data: T): Resource<T> =
      Resource(
        Status.SUCCESS,
        data,
        null
      )

    fun <T> error(error: Throwable, data: T? = null): Resource<T> =
      Resource(
        Status.ERROR,
        data,
        error
      )

    fun <T> loading(data: T? = null): Resource<T> =
      Resource(
        Status.LOADING,
        data,
        null
      )
  }


  enum class Status {
    SUCCESS,
    ERROR,
    LOADING;

    fun isSuccess(): Boolean = this == SUCCESS
    fun isError(): Boolean = this == ERROR
    fun isLoading(): Boolean = this == LOADING
    fun isDone(): Boolean = (this == SUCCESS || this == ERROR)
  }
}
