package pl.pateman.holdemevaluator.evaluator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import pl.pateman.holdemevaluator.Card;
import pl.pateman.holdemevaluator.CardValue;
import pl.pateman.holdemevaluator.HandName;
import pl.pateman.holdemevaluator.Suit;

public final class HoldemEvaluator {

  private static final int MAX_HAND_CARDS = 5;
  private static final List<Integer> ACE_HIGH_STRAIGHT_VALUES = Arrays.asList(
      CardValue.ACE.getValue(),
      CardValue.KING.getValue(),
      CardValue.QUEEN.getValue(),
      CardValue.JACK.getValue(),
      CardValue.TEN.getValue());
  private static final List<Integer> ACE_LOW_STRAIGHT_VALUES = Arrays.asList(
      CardValue.ACE.getValue(),
      CardValue.TWO.getValue(),
      CardValue.THREE.getValue(),
      CardValue.FOUR.getValue(),
      CardValue.FIVE.getValue());

  private int findInHand(final Card[] hand, final Predicate<Card> predicate, final Card[] result) {
    byte currentIndex = 0;
    int matches = 0;

    final Set<Card> dups = new HashSet<>(hand.length);
    for (final Card card : hand) {
      if (predicate.test(card) && !dups.contains(card)) {
        result[currentIndex++] = card;
        ++matches;
        dups.add(card);

        if (matches >= result.length) {
          break;
        }
      }
    }
    return matches;
  }

  private boolean isStraightFlush(final EvaluatorOutcome evaluatorOutcome, final int suitIndex) {
    final Card[] straightFlushCards = new Card[MAX_HAND_CARDS];
    this.findInHand(evaluatorOutcome.getHand(),
        card -> evaluatorOutcome.getMeaningfulCardValues().contains(card.getValue().getValue()),
        straightFlushCards);
    byte straightFlushCheck = 0;
    for (final Card card : straightFlushCards) {
      if (card == null) {
        continue;
      }
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

  private void findNHighestCardsExcluding(final Card[] hand, final Card[] excluding,
      final Card[] result) {
    final Card[] handWithoutExcludedCards = Arrays
        .stream(hand)
        .filter(c -> {
          for (final Card anExcluding : excluding) {
            if (anExcluding == null) {
              continue;
            }
            if (anExcluding.equals(c)) {
              return false;
            }
          }
          return true;
        })
        .toArray(Card[]::new);

    int smallestCard = 0;
    int pointer = 0;

    for (final Card card : handWithoutExcludedCards) {
      final int value = card.getValue().getValue();
      if (value == CardValue.ACE.getValue()) {
        result[(pointer++) % result.length] = card;
      } else if (value > smallestCard) {
        smallestCard = value;
        result[(pointer++) % result.length] = card;
      }
    }
  }

  private int findMeaningfulCardsInHand(final EvaluatorOutcome evaluatorOutcome,
      final Card[] result) {
    final Card[] hand = evaluatorOutcome.getHand();
    final Card highestCardInHand = evaluatorOutcome.getHighestCardInHand();
    final Collection<Integer> meaningfulCardValues = evaluatorOutcome.getMeaningfulCardValues();
    final int flushSuitToCheck = evaluatorOutcome.getFlushSuit();

    int foundCards = 0;
    switch (evaluatorOutcome.getOutcome()) {
      case HIGH_CARD:
        foundCards = this.findInHand(hand,
            (card -> card.getValue().getValue() == highestCardInHand.getValue().getValue()),
            result);
        break;
      case ONE_PAIR:
      case TWO_PAIRS:
      case SET:
      case FULL_HOUSE:
      case QUADS:
      case STRAIGHT:
        foundCards = this.findInHand(hand,
            (card -> meaningfulCardValues.contains(card.getValue().getValue())),
            result);
        break;
      case FLUSH:
        foundCards = this.findInHand(hand, card -> card.getSuit().getValue() == flushSuitToCheck,
            result);
        break;
      case STRAIGHT_FLUSH:
      case ROYAL_FLUSH:
        foundCards = this
            .findInHand(hand, card -> meaningfulCardValues.contains(card.getValue().getValue())
                && card.getSuit().getValue() == flushSuitToCheck, result);
        break;
    }
    return foundCards;
  }

  private FlushInfo determineFlush(final EvaluatorOutcome evaluatorOutcome) {
    HandName outcome = evaluatorOutcome.getOutcome();
    int flushSuit = FlushInfo.NO_FLUSH;

    if (outcome.getValue() < HandName.SET.getValue() || HandName.STRAIGHT.equals(outcome)) {
      final byte[] suitCounts = evaluatorOutcome.getSuitCounts();
      for (int suitIndex = 0; suitIndex < suitCounts.length; suitIndex++) {
        final byte suit = suitCounts[suitIndex];
        if (suit >= MAX_HAND_CARDS) {
          if (HandName.STRAIGHT.equals(outcome) && this
              .isStraightFlush(evaluatorOutcome, suitIndex)) {
            outcome = HandName.STRAIGHT_FLUSH;
          } else {
            outcome = HandName.FLUSH;
          }
          flushSuit = suitIndex + 1;
          break;
        }
      }

      //  Check for a royal flush.
      if (HandName.STRAIGHT_FLUSH.equals(outcome)
          && evaluatorOutcome.getHighestCardInHand().getValue().equals(CardValue.ACE)) {
        final byte[] cardValueCounts = evaluatorOutcome.getCardValueCounts();
        if (cardValueCounts[CardValue.KING.getValue() - 1] >= 1 &&
            cardValueCounts[CardValue.QUEEN.getValue() - 1] >= 1 &&
            cardValueCounts[CardValue.JACK.getValue() - 1] >= 1 &&
            cardValueCounts[CardValue.TEN.getValue() - 1] >= 1) {
          outcome = HandName.ROYAL_FLUSH;
        }
      }
    }

    return new FlushInfo(flushSuit, outcome);
  }

  private byte checkStraight(final EvaluatorOutcome evaluatorOutcome) {
    final byte[] cardValueCounts = evaluatorOutcome.getCardValueCounts();

    byte straightCount = 0;
    final List<Integer> straightValueCards = new ArrayList<>(MAX_HAND_CARDS);
    for (int i = 0; i < cardValueCounts.length - 1; i++) {
      final byte count = cardValueCounts[i];
      if (count >= 1) {
        if (cardValueCounts[i + 1] >= 1) {
          ++straightCount;
          straightValueCards.add(i + 1);
        } else if (straightCount != (MAX_HAND_CARDS - 1)) {
          straightCount = 0;
        } else {
          ++straightCount;
          straightValueCards.add(i + 1);
        }
      } else {
        straightCount = 0;
      }

      if (straightCount >= MAX_HAND_CARDS) {
        break;
      }
    }

    if (straightCount != MAX_HAND_CARDS) {
      //  Edge cases: ace-high straight and ace-low straight respectively.
      if (cardValueCounts[CardValue.ACE.getValue() - 1] >= 1
          && cardValueCounts[CardValue.KING.getValue() - 1] >= 1 &&
          cardValueCounts[CardValue.QUEEN.getValue() - 1] >= 1 &&
          cardValueCounts[CardValue.JACK.getValue() - 1] >= 1 &&
          cardValueCounts[CardValue.TEN.getValue() - 1] >= 1) {
        straightValueCards.addAll(ACE_HIGH_STRAIGHT_VALUES);
        straightCount = MAX_HAND_CARDS;
      } else if (cardValueCounts[CardValue.ACE.getValue() - 1] >= 1
          && cardValueCounts[CardValue.TWO.getValue() - 1] >= 1 &&
          cardValueCounts[CardValue.THREE.getValue() - 1] >= 1 &&
          cardValueCounts[CardValue.FOUR.getValue() - 1] >= 1 &&
          cardValueCounts[CardValue.FIVE.getValue() - 1] >= 1) {
        straightValueCards.addAll(ACE_LOW_STRAIGHT_VALUES);
        straightCount = MAX_HAND_CARDS;
      }
    }

    if (straightCount == MAX_HAND_CARDS) {
      evaluatorOutcome.getMeaningfulCardValues().clear();
      evaluatorOutcome.getMeaningfulCardValues().addAll(straightValueCards);
    }

    return straightCount;
  }

  private EvaluatorOutcome determineOutcome(final Card[] hand, final byte[] cardValueCounts,
      final byte[] suitCounts, final List<Integer> meaningfulCardValues) {

    final EvaluatorOutcome evaluatorOutcome = new EvaluatorOutcome(hand, cardValueCounts,
        suitCounts, meaningfulCardValues);
    evaluatorOutcome.setHighestCardInHand(this.findHighestCard(hand));

    evaluatorOutcome.setOutcome(HandName.HIGH_CARD);
    for (byte i = 0; i < cardValueCounts.length; i++) {
      final byte count = cardValueCounts[i];

      if (count == 2) {
        if (HandName.ONE_PAIR.equals(evaluatorOutcome.getOutcome())) {
          evaluatorOutcome.setOutcome(HandName.TWO_PAIRS);
        } else if (HandName.HIGH_CARD.equals(evaluatorOutcome.getOutcome())) {
          evaluatorOutcome.setOutcome(HandName.ONE_PAIR);
        } else if (HandName.SET.equals(evaluatorOutcome.getOutcome())) {
          evaluatorOutcome.setOutcome(HandName.FULL_HOUSE);
        }
        meaningfulCardValues.add(i + 1);
      } else if (count == 3) {
        if (HandName.ONE_PAIR.equals(evaluatorOutcome.getOutcome())) {
          evaluatorOutcome.setOutcome(HandName.FULL_HOUSE);
        } else {
          evaluatorOutcome.setOutcome(HandName.SET);
        }
        meaningfulCardValues.add(i + 1);
      } else if (count == 4) {
        evaluatorOutcome.setOutcome(HandName.QUADS);
        meaningfulCardValues.add(i + 1);
      }
    }

    if (evaluatorOutcome.getOutcome().getValue() <= HandName.SET.getValue()) {
      //  Edge cases: ace-high straight and ace-low straight.
      final byte straightCount = this.checkStraight(evaluatorOutcome);

      //  Check if there's a straight.
      if (straightCount >= MAX_HAND_CARDS) {
        evaluatorOutcome.setOutcome(HandName.STRAIGHT);
      }
    }

    //  Check if there's a flush/straight flush/royal flush.
    final FlushInfo flushInfo = this.determineFlush(evaluatorOutcome);
    if (flushInfo.isFlush()) {
      evaluatorOutcome.setFlushSuit(flushInfo.getFlushSuit());
      evaluatorOutcome.setOutcome(flushInfo.getOutcome());
    }

    //  Make sure that the number of meaningful cards does not exceed the limit.
    final List<Integer> meaningful = evaluatorOutcome.getMeaningfulCardValues();
    if (meaningful.size() > MAX_HAND_CARDS) {
      evaluatorOutcome.setMeaningfulCardValues(
          meaningful.subList(0, Math.min(MAX_HAND_CARDS, meaningful.size())));
    }

    return evaluatorOutcome;
  }

  private void correctHighestCardForAceLowStraight(final EvaluatorOutcome evaluatorOutcome,
      final HandOutcome handOutcome) {
    if (!HandName.STRAIGHT.equals(evaluatorOutcome.getOutcome()) && !HandName.STRAIGHT_FLUSH
        .equals(evaluatorOutcome.getOutcome())) {
      return;
    }

    final byte[] cardValueCounts = evaluatorOutcome.getCardValueCounts();
    if (cardValueCounts[CardValue.ACE.getValue() - 1] != 1
        || cardValueCounts[CardValue.FIVE.getValue() - 1] != 1) {
      return;
    }

    final Card[] five = new Card[1];
    this.findInHand(handOutcome.getTopCards(), card -> CardValue.FIVE.equals(card.getValue()),
        five);
    handOutcome.setHighestCard(five[0]);
  }

  private void findTopCardsAndSetHighestCard(final EvaluatorOutcome evaluatorOutcome,
      final HandOutcome handOutcome) {
    final Card[] result = new Card[MAX_HAND_CARDS];
    final int foundCards = this.findMeaningfulCardsInHand(evaluatorOutcome, result);

    Card[] resultingTopCards = result;
    if (foundCards != MAX_HAND_CARDS) {
      if (HandName.HIGH_CARD.equals(evaluatorOutcome.getOutcome())) {
        resultingTopCards = evaluatorOutcome.getHand();
        handOutcome.setHighestCard(evaluatorOutcome.getHighestCardInHand());
      } else {
        final int cardsToFind = MAX_HAND_CARDS - foundCards;
        final Card[] missingCards = new Card[cardsToFind];

        this.findNHighestCardsExcluding(evaluatorOutcome.getHand(), result, missingCards);
        resultingTopCards = Stream
            .concat(Arrays.stream(result), Arrays.stream(missingCards))
            .filter(Objects::nonNull)
            .toArray(Card[]::new);
        handOutcome.setHighestCard(this.findHighestCard(resultingTopCards));
      }
    } else {
      handOutcome.setHighestCard(this.findHighestCard(resultingTopCards));
    }

    handOutcome.setTopCards(resultingTopCards);
    this.correctHighestCardForAceLowStraight(evaluatorOutcome, handOutcome);
  }

  public HandOutcome calculate(final Card[] holeCards, final Card[] table) {
    final byte[] counts = new byte[CardValue.values().length];
    final byte[] suits = new byte[Suit.values().length];

    final Card[] hand = Stream
        .concat(Arrays.stream(holeCards), Arrays.stream(table))
        .toArray(Card[]::new);

    for (final Card card : hand) {
      ++counts[card.getValue().getValue() - 1];
      ++suits[card.getSuit().getValue() - 1];
    }

    final EvaluatorOutcome evaluatorOutcome = this
        .determineOutcome(hand, counts, suits, new ArrayList<>(MAX_HAND_CARDS));

    final HandOutcome handOutcome = new HandOutcome();
    handOutcome.setHandName(evaluatorOutcome.getOutcome());
    this.findTopCardsAndSetHighestCard(evaluatorOutcome, handOutcome);
    return handOutcome;
  }

}
