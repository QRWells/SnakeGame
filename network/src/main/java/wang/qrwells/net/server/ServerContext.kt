package wang.qrwells.net.server

import wang.qrwells.net.server.channel.Channel


object ServerContext {
  val users = HashMap<Int, Channel>()
  val groups = HashMap<Int, HashSet<Channel>>()

  val userGroup = HashMap<Int, HashSet<Int>>()
}