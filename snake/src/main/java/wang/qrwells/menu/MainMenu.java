package wang.qrwells.menu;

import com.almasb.fxgl.app.scene.FXGLMenu;
import com.almasb.fxgl.app.scene.MenuType;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import org.jetbrains.annotations.NotNull;

public class MainMenu extends FXGLMenu {
  private final Pane root = new Pane();
  private Node menu;

  public MainMenu(@NotNull MenuType type) {
    super(type);
  }
}
