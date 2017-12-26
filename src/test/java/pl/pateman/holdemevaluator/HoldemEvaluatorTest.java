package pl.pateman.holdemevaluator;

import org.junit.Test;

public class HoldemEvaluatorTest {

  @Test
  public void calculate() throws Exception {
    final Card[] tableCards = new Card[]{
        new Card(CardValue.SEVEN, Suit.SPADES),
        new Card(CardValue.EIGHT, Suit.HEARTS),
        new Card(CardValue.ACE, Suit.CLUBS),
        new Card(CardValue.TWO, Suit.HEARTS),
        new Card(CardValue.THREE, Suit.DIAMONDS)
    };
    final Card[] pairOfTwos = {new Card(CardValue.TWO, Suit.CLUBS),
        new Card(CardValue.TWO, Suit.SPADES)};

    final HoldemEvaluator evaluator = new HoldemEvaluator();

    evaluator.calculate(new Card[]{new Card(CardValue.SEVEN, Suit.HEARTS),
        new Card(CardValue.TWO, Suit.SPADES)}, tableCards);
    evaluator.calculate(pairOfTwos, tableCards);
    evaluator.calculate(pairOfTwos,
        new Card[]{new Card(CardValue.KING, Suit.SPADES), new Card(CardValue.KING, Suit.HEARTS),
            new Card(CardValue.KING, Suit.CLUBS)});
    evaluator.calculate(pairOfTwos,
        new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS), new Card(CardValue.KING, Suit.HEARTS),
            new Card(CardValue.KING, Suit.CLUBS)});
    evaluator.calculate(pairOfTwos, new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS)});
    evaluator.calculate(pairOfTwos,
        new Card[]{new Card(CardValue.TWO, Suit.DIAMONDS), new Card(CardValue.TWO, Suit.HEARTS)});
    //  High card.
    evaluator.calculate(new Card[]{}, tableCards);
    evaluator.calculate(
        new Card[]{new Card(CardValue.FOUR, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS)},
        tableCards);
    evaluator.calculate(
        new Card[]{new Card(CardValue.FOUR, Suit.HEARTS), new Card(CardValue.FIVE, Suit.CLUBS)},
        new Card[]{
            new Card(CardValue.SEVEN, Suit.SPADES),
            new Card(CardValue.EIGHT, Suit.HEARTS),
            new Card(CardValue.SIX, Suit.HEARTS)
        });
    evaluator.calculate(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.CLUBS)},
        new Card[]{new Card(CardValue.JACK, Suit.HEARTS), new Card(CardValue.QUEEN, Suit.CLUBS),
            new Card(CardValue.TEN, Suit.SPADES)});
    evaluator.calculate(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.KING, Suit.HEARTS), new Card(CardValue.QUEEN, Suit.DIAMONDS),
            new Card(CardValue.TEN, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.SIX, Suit.HEARTS)});
    evaluator.calculate(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.DIAMONDS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.SIX, Suit.HEARTS)});
    evaluator.calculate(
        new Card[]{new Card(CardValue.TWO, Suit.HEARTS), new Card(CardValue.FIVE, Suit.HEARTS)},
        new Card[]{new Card(CardValue.EIGHT, Suit.HEARTS), new Card(CardValue.FOUR, Suit.HEARTS),
            new Card(CardValue.THREE, Suit.HEARTS), new Card(CardValue.JACK, Suit.CLUBS),
            new Card(CardValue.SIX, Suit.HEARTS)});
    evaluator.calculate(
        new Card[]{new Card(CardValue.ACE, Suit.SPADES), new Card(CardValue.KING, Suit.SPADES)},
        new Card[]{new Card(CardValue.JACK, Suit.SPADES), new Card(CardValue.QUEEN, Suit.SPADES),
            new Card(CardValue.TEN, Suit.SPADES)});
  }

}