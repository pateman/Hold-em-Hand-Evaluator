package pl.pateman.holdemevaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public final class HoldemEvaluator {

  private static final int INCORRECT_LIMIT = -1;
  public static final int MAX_HAND_CARDS = 5;

  private int findInHand(final Card[] hand, final Predicate<Card> predicate,
      final int limit, final Card[] result) {
    byte currentIndex = 0;
    int matches = 0;

    final Set<Card> dups = new HashSet<>(hand.length);
    for (final Card card : hand) {
      if (predicate.test(card) && !dups.contains(card)) {
        result[currentIndex++] = card;
        ++matches;
        dups.add(card);

        if (limit != INCORRECT_LIMIT && matches >= limit) {
          break;
        }
      }
    }
    return matches;
  }

  private int findInHand(final Card[] hand, final Predicate<Card> predicate,
      final Card[] result) {
    return findInHand(hand, predicate, INCORRECT_LIMIT, result);
  }

  private boolean isStraightFlush(final Card[] hand, final List<Integer> meaningfulCards,
      final int suitIndex) {
    final Card[] straightFlushCards = new Card[MAX_HAND_CARDS];
    findInHand(hand, (card -> meaningfulCards.contains(card.getValue().getValue())),
        straightFlushCards);
    byte straightFlushCheck = 0;
    for (final Card card : straightFlushCards) {
      if (card.getSuit().getValue() == suitIndex + 1) {
        ++straightFlushCheck;
      }
    }
    return straightFlushCheck == MAX_HAND_CARDS;
  }

  private Card findHighestCard(final Card[] hand) {
    final int aceValue = CardValue.ACE.getValue();
    Card highest = null;
    int highestValue = 0;

    for (final Card card : hand) {
      if (card == null) {
        continue;
      }
      final int cardValue = card.getValue().getValue();

      if (CardValue.ACE.equals(card.getValue())) {
        highest = card;
        highestValue = aceValue;
        continue;
      }

      if (highestValue != aceValue && cardValue > highestValue) {
        highest = card;
        highestValue = cardValue;
      }
    }

    return highest;
  }

  public HandOutcome calculate(final Card[] holeCards, final Card[] table) {
    final byte[] counts = new byte[CardValue.values().length];
    final byte[] suits = new byte[Suit.values().length];

    final Card[] hand = Stream
        .concat(Arrays.stream(holeCards), Arrays.stream(table))
        .toArray(Card[]::new);

    final HandOutcome handOutcome = new HandOutcome();

    for (final Card card : hand) {
      ++counts[card.getValue().getValue() - 1];
      ++suits[card.getSuit().getValue() - 1];
    }

    HandName outcome = HandName.HIGH_CARD;
    byte straightCount = 0;
    List<Integer> meaningful = new ArrayList<>(MAX_HAND_CARDS);

    for (byte i = 0; i < counts.length; i++) {
      final byte count = counts[i];

      if (count == 1 && i > 0 && counts[i - 1] == 1) {
        ++straightCount;
        if (!HandName.SET.equals(outcome)) {
          meaningful.add((int) i);
        }
      } else if (count == 2) {
        straightCount = 0;
        if (HandName.ONE_PAIR.equals(outcome)) {
          outcome = HandName.TWO_PAIRS;
        } else if (HandName.HIGH_CARD.equals(outcome)) {
          outcome = HandName.ONE_PAIR;
        } else if (HandName.SET.equals(outcome)) {
          outcome = HandName.FULL_HOUSE;
        }
        meaningful.add(i + 1);
      } else if (count == 3) {
        straightCount = 0;
        if (HandName.ONE_PAIR.equals(outcome)) {
          outcome = HandName.FULL_HOUSE;
        } else {
          outcome = HandName.SET;
        }
        meaningful.add(i + 1);
      } else if (count == 4) {
        straightCount = 0;
        outcome = HandName.QUADS;
        meaningful.add(i + 1);
      } else if (straightCount == 4 && counts[i - 1] == 1) {
        ++straightCount;
        if (!HandName.SET.equals(outcome)) {
          meaningful.add((int) i);
        }
      }
    }
    //  Ace-high straight.
    final boolean aceHighStraight = counts[CardValue.ACE.getValue() - 1] == 1
        && counts[CardValue.KING.getValue() - 1] == 1;
    if (straightCount == 3 && aceHighStraight) {
      straightCount += 2;
      meaningful.add(CardValue.ACE.getValue());
      meaningful.add(CardValue.KING.getValue());
    }
    if (straightCount >= MAX_HAND_CARDS) {
      outcome = HandName.STRAIGHT;
    }

    final Card highestCardInHand = this.findHighestCard(hand);
    if (meaningful.size() > MAX_HAND_CARDS) {
      meaningful = meaningful.subList(0, Math.min(MAX_HAND_CARDS, meaningful.size()));
    }
    final List<Integer> meaningfulFinal = meaningful;

    int flushSuit = 0;
    if (outcome.getValue() < HandName.SET.getValue() || HandName.STRAIGHT.equals(outcome)) {
      for (int suitIndex = 0; suitIndex < suits.length; suitIndex++) {
        final byte suit = suits[suitIndex];
        if (suit >= MAX_HAND_CARDS) {
          if (HandName.STRAIGHT.equals(outcome) && isStraightFlush(hand, meaningfulFinal,
              suitIndex)) {
            outcome = HandName.STRAIGHT_FLUSH;
          } else {
            outcome = HandName.FLUSH;
          }
          flushSuit = suitIndex + 1;
          break;
        }
      }

      if (HandName.STRAIGHT_FLUSH.equals(outcome) && highestCardInHand.getValue().getValue() ==
          CardValue.ACE.getValue()) {
        outcome = HandName.ROYAL_FLUSH;
      }
    }

    final Card[] meaningfulCards = new Card[MAX_HAND_CARDS];
    final int flushSuitToCheck = flushSuit;

    int foundCards = 0;
    switch (outcome) {
      case HIGH_CARD:
        foundCards = this.findInHand(hand,
            (card -> card.getValue().getValue() == highestCardInHand.getValue().getValue()),
            meaningfulCards);
        break;
      case ONE_PAIR:
      case TWO_PAIRS:
      case SET:
      case FULL_HOUSE:
      case QUADS:
      case STRAIGHT:
        foundCards = this.findInHand(hand,
            (card -> meaningfulFinal.contains(card.getValue().getValue())),
            meaningfulCards);
        break;
      case FLUSH:
        foundCards = this.findInHand(hand, card -> card.getSuit().getValue() == flushSuitToCheck,
            meaningfulCards);
        break;
      case STRAIGHT_FLUSH:
      case ROYAL_FLUSH:
        foundCards = this
            .findInHand(hand, card -> meaningfulFinal.contains(card.getValue().getValue())
                && card.getSuit().getValue() == flushSuitToCheck, meaningfulCards);
        break;
    }

    Card[] resultingTopCards = meaningfulCards;
    if (foundCards != MAX_HAND_CARDS) {
      if (HandName.HIGH_CARD.equals(outcome)) {
        resultingTopCards = hand;
        handOutcome.setHighestCard(highestCardInHand);
      } else {
        final int cardsToFind = MAX_HAND_CARDS - foundCards;
        final Card[] missingCards = new Card[cardsToFind];
        final int highest = this.findHighestCard(resultingTopCards).getValue().getValue();

        this.findInHand(hand,
            card -> CardValue.ACE.equals(card.getValue()) || card.getValue().getValue() > highest,
            cardsToFind, missingCards);
        resultingTopCards = Stream
            .concat(Arrays.stream(meaningfulCards), Arrays.stream(missingCards))
            .filter(Objects::nonNull)
            .toArray(Card[]::new);
        handOutcome.setHighestCard(this.findHighestCard(resultingTopCards));
      }
    }

    handOutcome.setTopCards(resultingTopCards);
    handOutcome.setHandName(outcome);
    return handOutcome;
  }
}
