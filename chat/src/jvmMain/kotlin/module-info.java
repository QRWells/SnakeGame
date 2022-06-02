module SnakeClient.chat.jvmMain {
  requires material.desktop;
  requires foundation.layout.desktop;
  requires ui.desktop;
  requires ui.unit.desktop;
  requires ui.tooling.preview.desktop;
  requires material.icons.core.desktop;
  requires runtime.desktop;
  requires runtime.saveable.desktop;
  requires foundation.desktop;
  requires io.vertx.core;
  requires SnakeClient.network.main;
  requires SnakeClient.snake.main;
  requires kotlin.stdlib;
  requires exposed.dao;
  requires exposed.core;
  requires kotlinx.coroutines.core.jvm;
  requires ui.graphics.desktop;
}