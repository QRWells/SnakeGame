module SnakeClient.network.main {
  requires javafx.base;
  requires javafx.graphics;
  requires java.logging;
  exports wang.qrwells.net;
  exports wang.qrwells.net.tcp;
  exports wang.qrwells.net.udp;
  exports wang.qrwells.message;
  exports wang.qrwells.message.impl;
}