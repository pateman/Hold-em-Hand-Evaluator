package pl.pateman.holdemevaluator;

import java.util.Arrays;

public final class HandOutcome {

  private HandName handName;
  private Card highestCard;
  private Card[] topCards;

  HandOutcome() {
    this.handName = HandName.HIGH_CARD;
  }

  public HandName getHandName() {
    return handName;
  }

  void setHandName(HandName handName) {
    this.handName = handName;
  }

  public Card getHighestCard() {
    return highestCard;
  }

  void setHighestCard(Card highestCard) {
    this.highestCard = highestCard;
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