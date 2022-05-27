package wang.qrwells;

import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.dsl.components.AutoRotationComponent;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.entity.EntityFactory;
import com.almasb.fxgl.entity.SpawnData;
import com.almasb.fxgl.entity.Spawns;
import javafx.scene.shape.Rectangle;
import wang.qrwells.component.SnakeHeadComponent;

import static com.almasb.fxgl.dsl.FXGLForKtKt.entityBuilder;

public class GameFactory implements EntityFactory {
  @Spawns("snake")
  public Entity spawnSnake(SpawnData data) {
    return FXGL.entityBuilder(data)
               .view(new Rectangle(70, 70))
               .build();
  }

  @Spawns("snakeHead")
  public Entity newSnakeHead(SpawnData data) {
    return entityBuilder(data).type(EntityType.SNAKE_HEAD)
                              .viewWithBBox(new Rectangle(32, 32))
                              .collidable()
                              .with(new AutoRotationComponent())
                              .with(new SnakeHeadComponent())
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
