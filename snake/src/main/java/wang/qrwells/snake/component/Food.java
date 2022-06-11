package wang.qrwells.snake.component;

import javafx.geometry.Point2D;

import java.awt.*;

public class Food extends Component {
  private Point2D position;

  public Food(Point2D position) {
    this.position = position;
  }

  public Point2D getPosition() {
    return position;
  }

  public void setPosition(Point2D position) {
    this.position = position;
  }
}
