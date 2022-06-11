package wang.qrwells.snake;

import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import com.almasb.fxgl.multiplayer.NetworkComponent;
import javafx.scene.shape.Rectangle;
import wang.qrwells.snake.component.SnakeHeadComponent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class GameFactory implements EntityFactory {
  @Spawns("snakeHead")
  public Entity newSnakeHead(SpawnData data) {
    return entityBuilder(data).type(EntityType.SNAKE_HEAD)
                              .viewWithBBox(new Rectangle(32, 32))
                              .collidable()
                              .with(new AutoRotationComponent())
                              .with(new SnakeHeadComponent())
                              .with(new NetworkComponent())
                              .build();
  }

  @Spawns("snakeBody")
  public Entity newSnakeBody(SpawnData data) {
    return entityBuilder(data).type(EntityType.SNAKE_BODY)
                              .viewWithBBox(new Rectangle(32, 32))
                              .collidable()
                              .with(new AutoRotationComponent())
                              .build();
  }

  public enum EntityType {
    SNAKE_HEAD, SNAKE_BODY, FOOD
  }
}
