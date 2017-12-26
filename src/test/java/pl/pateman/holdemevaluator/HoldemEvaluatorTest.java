package pl.pateman.holdemevaluator;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

public class HoldemEvaluatorTest {

  private Card[] tableCards;
  private Card[] pairOfTwos;
  private HoldemEvaluator evaluator;

  private void assertOutcomeHandName(final Card[] holeCards, final Card[] tableCards,
      final HandName expectedHandName) {
    final HandOutcome handOutcome = this.evaluator.calculate(holeCards, tableCards);
    assertEquals(handOutcome.getHandName(), expectedHandName);
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
    //  AS KS JS QS TS - royal flush.
    this.assertOutcomeHandName(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.SPADES)},
        new Card[]{new Card(CardValue.JACK, Suit.SPADES), new Card(CardValue.QUEEN, Suit.SPADES),
            new Card(CardValue.TEN, Suit.SPADES)}, HandName.ROYAL_FLUSH);
  }

}