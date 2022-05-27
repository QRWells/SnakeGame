package wang.qrwells;

import wang.qrwells.component.Food;
import wang.qrwells.component.SnakeHeadComponent;

import java.util.HashMap;
import java.util.UUID;

public class Game {
  private int size;
  private boolean isBounded;

  private HashMap<UUID, SnakeHeadComponent> others;

  private SnakeHeadComponent self;
  private Food food;

  public Game(int size, boolean isBounded) {
    this.size = size;
    this.isBounded = isBounded;
  }

  public void Update() {

  }

  public void AddSnake(UUID uuid, SnakeHeadComponent snake) {
    others.put(uuid, snake);
  }

  public void RemoveSnake(UUID uuid) {
    others.remove(uuid);
  }

  public SnakeHeadComponent GetSnake(UUID uuid) {
    return others.get(uuid);
  }

  public SnakeHeadComponent GetSelf() {
    return self;
  }

  public Food GetFood() {
    return food;
  }
}
