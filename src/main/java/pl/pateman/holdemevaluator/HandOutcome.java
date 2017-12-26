package pl.pateman.holdemevaluator;

import java.util.Arrays;

public final class HandOutcome {

  private HandName handName;
  private int highestCard;
  private int lowestCard;
  private Card[] topCards;

  HandOutcome() {
    this.highestCard = CardValue.TWO.getValue();
    this.lowestCard = CardValue.KING.getValue();
    this.handName = HandName.HIGH_CARD;
  }

  public HandName getHandName() {
    return handName;
  }

  void setHandName(HandName handName) {
    this.handName = handName;
  }

  public int getHighestCard() {
    return highestCard;
  }

  void setHighestCard(int highestCard) {
    this.highestCard = highestCard;
  }

  public int getLowestCard() {
    return lowestCard;
  }

  void setLowestCard(int lowestCard) {
    this.lowestCard = lowestCard;
  }

  public Card[] getTopCards() {
    return topCards;
  }

  void setTopCards(Card[] topCards) {
    this.topCards = topCards;
  }

  @Override
  public String toString() {
    return "HandOutcome{" +
        "handName=" + handName +
        ", topCards=" + Arrays.toString(topCards) +
        '}';
  }
}