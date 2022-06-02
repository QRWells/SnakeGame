module SnakeClient.network.main {
  requires org.apache.logging.log4j;
  requires javafx.base;
  requires javafx.graphics;
  exports wang.qrwells.net;
  exports wang.qrwells.net.tcp;
  exports wang.qrwells.net.udp;
  exports wang.qrwells.message;
  exports wang.qrwells.message.impl;
}