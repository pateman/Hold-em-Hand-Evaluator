package pl.pateman.holdemevaluator.evaluator;

import java.util.Arrays;
import pl.pateman.holdemevaluator.Card;
import pl.pateman.holdemevaluator.HandName;

public final class HandOutcome implements Comparable<HandOutcome> {

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

  @Override
  public int compareTo(final HandOutcome o) {
    if (o == null) {
      return 1;
    }
    final int comparison = this.getHandName().getValue() - o.getHandName().getValue();
    if (comparison != 0) {
      return comparison;
    }
    return this.getHighestCard().compareTo(o.getHighestCard());
  }
}