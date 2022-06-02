import io.vertx.core.impl.logging.Logger
import io.vertx.core.impl.logging.LoggerFactory
import wang.qrwells.message.AbstractMessage
import wang.qrwells.net.MessageHandler
import wang.qrwells.net.tcp.TCPClient

object Client {
  private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
  private val client = TCPClient()
  private var isConnected = false

  fun connect(host: String, port: Int) {
    logger.info("connect to $host:$port")
    client.setHost(host).setPort(port).connect()
  }

  fun send(msg: AbstractMessage) {
    if (isConnected) {
      client.connection.send(msg)
    } else {
      logger.error("client is not connected")
    }
  }

  fun <T : AbstractMessage> addHandler(handler: MessageHandler<T>) {
    client.connection.addMessageHandler(handler)
  }

  fun close() {
    client.disconnect()
  }
}