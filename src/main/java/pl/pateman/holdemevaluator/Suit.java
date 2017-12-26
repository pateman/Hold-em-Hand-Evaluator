package pl.pateman.holdemevaluator;

public enum Suit {
  CLUBS(1),
  HEARTS(2),
  SPADES(3),
  DIAMONDS(4);

  private final int value;

  Suit(int value) {
    this.value = value;
  }

  public int getValue() {
    return value;
  }
}