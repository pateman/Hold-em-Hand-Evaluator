package pl.pateman.holdemevaluator;

public enum HandName {
  HIGH_CARD(1),
  ONE_PAIR(2),
  TWO_PAIRS(3),
  SET(4),
  STRAIGHT(5),
  FLUSH(6),
  FULL_HOUSE(7),
  QUADS(8),
  STRAIGHT_FLUSH(9),
  ROYAL_FLUSH(10);

  private final int value;

  HandName(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}