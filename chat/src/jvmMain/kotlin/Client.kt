import org.slf4j.Logger
import org.slf4j.LoggerFactory
import wang.qrwells.message.AbstractMessage
import wang.qrwells.net.MessageHandler
import wang.qrwells.net.tcp.TCPClient

object Client {
  private val logger: Logger = LoggerFactory.getLogger(this.javaClass)
  private val client = TCPClient()
  private var isConnected = false

  init {
    client.setOnConnected {
      isConnected = true
      logger.info("connected")
    }
    client.setOnDisconnected {
      isConnected = false
      logger.info("disconnected")
    }
  }

  fun connect(host: String, port: Int) {
    if (isConnected) {
      logger.warn("already connected")
      return
    }
    logger.info("connect to $host:$port")
    client.setHost(host).setPort(port).connect()
    isConnected = true
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