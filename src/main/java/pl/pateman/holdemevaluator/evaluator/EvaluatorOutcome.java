package pl.pateman.holdemevaluator.evaluator;

import java.util.List;
import pl.pateman.holdemevaluator.Card;
import pl.pateman.holdemevaluator.HandName;

final class EvaluatorOutcome {

  private final Card[] hand;
  private final byte[] cardValueCounts;
  private final byte[] suitCounts;

  private List<Integer> meaningfulCardValues;
  private HandName outcome;
  private Card highestCardInHand;
  private int flushSuit;

  EvaluatorOutcome(final Card[] hand, final byte[] cardValueCounts, final byte[] suitCounts,
      final List<Integer> meaningfulCardValues) {
    this.hand = hand;
    this.cardValueCounts = cardValueCounts;
    this.suitCounts = suitCounts;
    this.meaningfulCardValues = meaningfulCardValues;
  }

  Card[] getHand() {
    return hand;
  }

  byte[] getCardValueCounts() {
    return cardValueCounts;
  }

  byte[] getSuitCounts() {
    return suitCounts;
  }

  List<Integer> getMeaningfulCardValues() {
    return meaningfulCardValues;
  }

  void setMeaningfulCardValues(final List<Integer> meaningfulCardValues) {
    this.meaningfulCardValues = meaningfulCardValues;
  }

  HandName getOutcome() {
    return outcome;
  }

  void setOutcome(final HandName outcome) {
    this.outcome = outcome;
  }

  Card getHighestCardInHand() {
    return highestCardInHand;
  }

  void setHighestCardInHand(final Card highestCardInHand) {
    this.highestCardInHand = highestCardInHand;
  }

  int getFlushSuit() {
    return flushSuit;
  }

  void setFlushSuit(final int flushSuit) {
    this.flushSuit = flushSuit;
  }
}
