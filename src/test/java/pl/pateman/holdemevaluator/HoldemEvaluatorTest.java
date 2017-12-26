package pl.pateman.holdemevaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import pl.pateman.holdemevaluator.evaluator.HandOutcome;
import pl.pateman.holdemevaluator.evaluator.HoldemEvaluator;

public class HoldemEvaluatorTest {

  private Card[] tableCards;
  private Card[] pairOfTwos;
  private HoldemEvaluator evaluator;

  private void assertOutcomeHandName(final Card[] holeCards, final Card[] tableCards,
      final HandName expectedHandName) {
    final HandOutcome handOutcome = this.evaluator.calculate(holeCards, tableCards);
    assertEquals(handOutcome.getHandName(), expectedHandName);
  }

  private <T> boolean arrayContainsAll(final T[] arrayA, final T[] arrayB) {
    for (final T b : arrayB) {
      boolean aContainsB = false;
      for (final T a : arrayA) {
        if (a.equals(b)) {
          aContainsB = true;
          break;
        }
      }

      if (!aContainsB) {
        return false;
      }
    }
    return true;
  }

  private void assertOutcomeTopCards(final Card[] holeCards, final Card[] tableCards,
      final Card[] expectedTopCards) {
    final HandOutcome handOutcome = this.evaluator.calculate(holeCards, tableCards);
    final Card[] topCards = handOutcome.getTopCards();
    assertTrue(this.arrayContainsAll(topCards, expectedTopCards));
  }

  private void assertOutcomeHighestCard(final Card[] holeCards, final Card[] tableCards,
      final Card expectedHighestCard) {
    final HandOutcome handOutcome = this.evaluator.calculate(holeCards, tableCards);
    assertEquals(handOutcome.getHighestCard(), expectedHighestCard);
  }

  @Before
  public void setUp() throws Exception {
    this.tableCards = new Card[]{
        new Card(CardValue.SEVEN, Suit.SPADES),
        new Card(CardValue.EIGHT, Suit.HEARTS),
        new Card(CardValue.ACE, Suit.CLUBS),
        new Card(CardValue.TWO, Suit.HEARTS),
        new Card(CardValue.THREE, Suit.DIAMONDS)
    };
    this.pairOfTwos = new Card[]{new Card(CardValue.TWO, Suit.CLUBS),
        new Card(CardValue.TWO, Suit.SPADES)};
    this.evaluator = new HoldemEvaluator();
  }

  @Test
  public void testOutcomeHandName() throws Exception {
    //  2C 2S QC TD 3S AH - one pair.
    this.assertOutcomeHandName(this.pairOfTwos,
        new Card[]{new Card(CardValue.QUEEN, Suit.CLUBS), new Card(CardValue.TEN, Suit.DIAMONDS),
            new Card(CardValue.THREE, Suit.SPADES), new Card(CardValue.ACE, Suit.HEARTS)},
        HandName.ONE_PAIR);
    //  7H 2S 7S 8H AC 2H 3D - two pairs.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.SEVEN, Suit.HEARTS),
            new Card(CardValue.TWO, Suit.SPADES)}, this.tableCards, HandName.TWO_PAIRS);
    //  2C 2S 7S 8H AC 2H 3D - set.
    this.assertOutcomeHandName(this.pairOfTwos, this.tableCards, HandName.SET);
    //  2C 2S KS KH KC - full house.
    this.assertOutcomeHandName(this.pairOfTwos,
        new Card[]{new Card(CardValue.KING, Suit.SPADES), new Card(CardValue.KING, Suit.HEARTS),
            new Card(CardValue.KING, Suit.CLUBS)}, HandName.FULL_HOUSE);
    //  2C 2S 2D KH KC - full house.
    this.assertOutcomeHandName(this.pairOfTwos,
        new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS), new Card(CardValue.KING, Suit.HEARTS),
            new Card(CardValue.KING, Suit.CLUBS)}, HandName.FULL_HOUSE);
    //  2C 2S 2D - set.
    this.assertOutcomeHandName(this.pairOfTwos, new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS)},
        HandName.SET);
    //  2C 2S 2D 2H - quads.
    this.assertOutcomeHandName(this.pairOfTwos,
        new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS), new Card(CardValue.TWO, Suit.HEARTS)},
        HandName.QUADS);
    //  High card.
    this.assertOutcomeHandName(new Card[]{}, this.tableCards, HandName.HIGH_CARD);
    //  4H 5C 7S 8H AC 2H 3D - straight.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.FOUR, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS)},
        this.tableCards, HandName.STRAIGHT);
    //  4H 5C 7S 8H 6H - straight.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.FOUR, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS)},
        new Card[]{
            new Card(CardValue.SEVEN, Suit.SPADES),
            new Card(CardValue.EIGHT, Suit.HEARTS),
            new Card(CardValue.SIX, Suit.HEARTS)
        }, HandName.STRAIGHT);
    //  AS KC JH QC TS - straight.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS)},
        new Card[]{new Card(CardValue.JACK, Suit.HEARTS), new Card(CardValue.QUEEN, Suit.CLUBS),
            new Card(CardValue.TEN, Suit.SPADES)}, HandName.STRAIGHT);
    //  AS KC 2H 5C 3S KD 4S - straight.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS)},
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS),
            new Card(CardValue.THREE, Suit.SPADES), new Card(CardValue.KING, Suit.DIAMONDS),
            new Card(CardValue.FOUR, Suit.SPADES)}, HandName.STRAIGHT);
    //  2H 5H KH QD TH JC 6H - flush.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.KING, Suit.HEARTS), new Card(CardValue.QUEEN, Suit.DIAMONDS),
            new Card(CardValue.TEN, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.SIX, Suit.HEARTS)}, HandName.FLUSH);
    //  2H 5H 8H 4D 3H JC 6H - flush.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.DIAMONDS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.SIX, Suit.HEARTS)}, HandName.FLUSH);
    //  2H 5H 8H 4H 3H JC 6H - straight flush.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.SIX, Suit.HEARTS)}, HandName.STRAIGHT_FLUSH);
    //  2H 5H 8H 4H 3H JC AH - straight flush.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.ACE, Suit.HEARTS)}, HandName.STRAIGHT_FLUSH);
    //  AS KS JS QS TS - royal flush.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.SPADES)},
        new Card[]{new Card(CardValue.JACK, Suit.SPADES), new Card(CardValue.QUEEN, Suit.SPADES),
            new Card(CardValue.TEN, Suit.SPADES)}, HandName.ROYAL_FLUSH);
  }

  @Test
  public void testOutcomeTopCards() throws Exception {
    //  2C 2S 7S 8H AC 2H 3D
    this.assertOutcomeTopCards(this.pairOfTwos, this.tableCards,
        new Card[]{new Card(CardValue.ACE, Suit.CLUBS), new Card(CardValue.TWO, Suit.CLUBS),
            new Card(CardValue.TWO, Suit.SPADES), new Card(CardValue.TWO, Suit.HEARTS),
            new Card(CardValue.EIGHT, Suit.HEARTS)});
    //  High card.
    this.assertOutcomeTopCards(new Card[]{}, this.tableCards, this.tableCards);
    //  2C 2S 2D 2H 3H AS
    this.assertOutcomeTopCards(this.pairOfTwos,
        new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS), new Card(CardValue.TWO, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.ACE, Suit.SPADES)},
        new Card[]{new Card(CardValue.TWO, Suit.CLUBS), new Card(CardValue.TWO, Suit.SPADES),
            new Card(CardValue.TWO, Suit.DIAMONDS), new Card(CardValue.TWO, Suit.HEARTS),
            new Card(CardValue.ACE, Suit.SPADES)});
    //  4H 5C 7S 8H 6H KD
    this.assertOutcomeTopCards(
        new Card[]{new Card(CardValue.FOUR, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS)},
        new Card[]{
            new Card(CardValue.SEVEN, Suit.SPADES),
            new Card(CardValue.EIGHT, Suit.HEARTS),
            new Card(CardValue.SIX, Suit.HEARTS),
            new Card(CardValue.KING, Suit.DIAMONDS)
        }, new Card[]{new Card(CardValue.FOUR, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS),
            new Card(CardValue.SEVEN, Suit.SPADES),
            new Card(CardValue.EIGHT, Suit.HEARTS),
            new Card(CardValue.SIX, Suit.HEARTS)});
    //  AS KC 2H 5C 3S KD 4S
    this.assertOutcomeTopCards(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS)},
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS),
            new Card(CardValue.THREE, Suit.SPADES), new Card(CardValue.KING, Suit.DIAMONDS),
            new Card(CardValue.FOUR, Suit.SPADES)},
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.TWO, Suit.HEARTS),
            new Card(CardValue.FIVE, Suit.CLUBS), new Card(CardValue.THREE, Suit.SPADES),
            new Card(CardValue.FOUR, Suit.SPADES)});
    //  AS KC JH QC TS AD 5S
    this.assertOutcomeTopCards(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS)},
        new Card[]{new Card(CardValue.JACK, Suit.HEARTS), new Card(CardValue.QUEEN, Suit.CLUBS),
            new Card(CardValue.TEN, Suit.SPADES), new Card(CardValue.ACE, Suit.DIAMONDS),
            new Card(CardValue.FIVE, Suit.SPADES)},
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS),
            new Card(CardValue.JACK, Suit.HEARTS), new Card(CardValue.QUEEN, Suit.CLUBS),
            new Card(CardValue.TEN, Suit.SPADES)});
    //  2H 5H 8H 4H 3H JC 6H
    this.assertOutcomeTopCards(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.SIX, Suit.HEARTS)},
        new Card[]{new Card(CardValue.SIX, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.TWO, Suit.HEARTS),
            new Card(CardValue.FIVE, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS)});
    //  2H 5H 8H 4H 3H JC AH
    this.assertOutcomeTopCards(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.ACE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.ACE, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.TWO, Suit.HEARTS),
            new Card(CardValue.FIVE, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS)});
    //  AS KS JS QS TS 2H
    this.assertOutcomeTopCards(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.SPADES)},
        new Card[]{new Card(CardValue.JACK, Suit.SPADES), new Card(CardValue.QUEEN, Suit.SPADES),
            new Card(CardValue.TEN, Suit.SPADES), new Card(CardValue.TWO, Suit.HEARTS)},
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.SPADES),
            new Card(CardValue.JACK, Suit.SPADES), new Card(CardValue.QUEEN, Suit.SPADES),
            new Card(CardValue.TEN, Suit.SPADES)});
  }

  @Test
  public void testOutcomeHighestCard() throws Exception {
    //  2C 2S 7S 8H AC 2H 3D
    this.assertOutcomeHighestCard(this.pairOfTwos, this.tableCards,
        new Card(CardValue.ACE, Suit.CLUBS));
    //  High card.
    this.assertOutcomeHighestCard(new Card[]{}, this.tableCards,
        new Card(CardValue.ACE, Suit.CLUBS));
    //  2C 2S 2D 2H 3H AS
    this.assertOutcomeHighestCard(this.pairOfTwos,
        new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS), new Card(CardValue.TWO, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.ACE, Suit.SPADES)},
        new Card(CardValue.ACE, Suit.SPADES));
    //  4H 5C 7S 8H 6H KD
    this.assertOutcomeHighestCard(
        new Card[]{new Card(CardValue.FOUR, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS)},
        new Card[]{
            new Card(CardValue.SEVEN, Suit.SPADES),
            new Card(CardValue.EIGHT, Suit.HEARTS),
            new Card(CardValue.SIX, Suit.HEARTS),
            new Card(CardValue.KING, Suit.DIAMONDS)
        }, new Card(CardValue.EIGHT, Suit.HEARTS));
    //  AS KC 2H 5C 3S KD 4S
    this.assertOutcomeHighestCard(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS)},
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS),
            new Card(CardValue.THREE, Suit.SPADES), new Card(CardValue.KING, Suit.DIAMONDS),
            new Card(CardValue.FOUR, Suit.SPADES)}, new Card(CardValue.FIVE, Suit.CLUBS));
    //  AS KC JH QC TS AD 5S
    this.assertOutcomeHighestCard(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS)},
        new Card[]{new Card(CardValue.JACK, Suit.HEARTS), new Card(CardValue.QUEEN, Suit.CLUBS),
            new Card(CardValue.TEN, Suit.SPADES), new Card(CardValue.ACE, Suit.DIAMONDS),
            new Card(CardValue.FIVE, Suit.SPADES)}, new Card(CardValue.ACE, Suit.SPADES));
    //  2H 5H 8H 4H 3H JC AH
    this.assertOutcomeHighestCard(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.ACE, Suit.HEARTS)}, new Card(CardValue.FIVE, Suit.HEARTS));
    //  AS KS JS QS TS 2H
    this.assertOutcomeHighestCard(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.SPADES)},
        new Card[]{new Card(CardValue.JACK, Suit.SPADES), new Card(CardValue.QUEEN, Suit.SPADES),
            new Card(CardValue.TEN, Suit.SPADES), new Card(CardValue.TWO, Suit.HEARTS)},
        new Card(CardValue.ACE, Suit.SPADES));
  }
}