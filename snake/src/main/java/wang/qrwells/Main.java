package wang.qrwells;

import com.almasb.fxgl.app.GameApplication;
import com.almasb.fxgl.app.GameSettings;
import com.almasb.fxgl.dsl.FXGL;
import com.almasb.fxgl.entity.Entity;
import com.almasb.fxgl.localization.Language;
import com.almasb.fxgl.physics.CollisionHandler;
import javafx.scene.input.KeyCode;
import javafx.util.Duration;
import wang.qrwells.net.Client;
import wang.qrwells.net.tcp.TCPClient;

import java.util.Map;

import static com.almasb.fxgl.dsl.FXGLForKtKt.*;

public class Main extends GameApplication {
  private Client client = new TCPClient("localhost", 8887);

  public static void main(String[] args) {
    System.out.println("Hello world!");
    launch(args);
  }

  @Override
  protected void initSettings(GameSettings settings) {
    settings.setWidth(800);
    settings.setHeight(600);
    settings.setTitle("Snake");
    settings.setVersion("0.1");
    settings.set3D(false);
    settings.setFullScreenAllowed(false);
    settings.setCloseConfirmation(true);
    settings.setDefaultLanguage(Language.ENGLISH);
    settings.setMainMenuEnabled(true);
  }

  @Override
  protected void initInput() {
    super.initInput();
    var input = getInput();
    FXGL.onKey(KeyCode.UP, () -> client.getConnection()
                                       .send(null));
    FXGL.onKey(KeyCode.DOWN, () -> client.getConnection()
                                         .send(null));
    FXGL.onKey(KeyCode.LEFT, () -> client.getConnection()
                                         .send(null));
    FXGL.onKey(KeyCode.RIGHT, () -> client.getConnection()
                                          .send(null));
  }

  @Override
  protected void initGameVars(Map<String, Object> vars) {
    super.initGameVars(vars);
  }

  @Override
  protected void initGame() {
    runOnce(() -> {
      client = new TCPClient("localhost", 8887);
      client.connect();
      return null;
    }, Duration.seconds(1));
  }

  @Override
  protected void initPhysics() {
    getPhysicsWorld().setGravity(0, 0);
    getPhysicsWorld().addCollisionHandler(
        new CollisionHandler(GameFactory.EntityType.SNAKE_HEAD,
                             GameFactory.EntityType.FOOD) {
          @Override
          protected void onCollisionBegin(Entity a, Entity b) {
            b.removeFromWorld();
          }
        });
  }

  @Override
  protected void initUI() {

  }

  @Override
  protected void onUpdate(double tpf) {
    super.onUpdate(tpf);
  }
}