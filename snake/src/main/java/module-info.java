module SnakeClient.snake.main {
  requires org.apache.logging.log4j;
  requires com.almasb.fxgl.all;
  requires SnakeClient.network.main;
  requires java.datatransfer;
  requires java.desktop;
  requires annotations;


  exports wang.qrwells.snake;
}