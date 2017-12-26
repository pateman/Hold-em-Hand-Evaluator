package pl.pateman.holdemevaluator.parser;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pl.pateman.holdemevaluator.Card;
import pl.pateman.holdemevaluator.CardValue;
import pl.pateman.holdemevaluator.Suit;
import pl.pateman.holdemevaluator.TestUtil;
import pl.pateman.holdemevaluator.UnitTest;

@Category(UnitTest.class)
public class StringHandParserTest {

  private StringHandParser parser;

  private void assertArrayContainsAll(final Card[] arrayToTest, final Card[] expectedContents) {
    assertTrue(TestUtil.arrayContainsAll(arrayToTest, expectedContents));
  }

  @Before
  public void setUp() throws Exception {
    this.parser = new StringHandParser();
  }

  @Test
  public void parseValid() throws Exception {
    this.assertArrayContainsAll(this.parser.parse("2S JD"),
        new Card[]{new Card(CardValue.TWO, Suit.SPADES), new Card(CardValue.JACK, Suit.DIAMONDS)});
    this.assertArrayContainsAll(this.parser.parse("2SJD"),
        new Card[]{new Card(CardValue.TWO, Suit.SPADES), new Card(CardValue.JACK, Suit.DIAMONDS)});
    this.assertArrayContainsAll(this.parser.parse(""), new Card[]{});
    this.assertArrayContainsAll(this.parser.parse("  "), new Card[]{});
    this.assertArrayContainsAll(this.parser.parse("  2S      KD   "),
        new Card[]{new Card(CardValue.TWO, Suit.SPADES), new Card(CardValue.KING, Suit.DIAMONDS)});
    this.assertArrayContainsAll(this.parser.parse("A♠ K♥ 9♣ T♣ 7♠"),
        new Card[]{new Card(CardValue.ACE, Suit.SPADES),
            new Card(CardValue.KING, Suit.HEARTS), new Card(CardValue.TEN, Suit.CLUBS),
            new Card(CardValue.NINE, Suit.CLUBS),
            new Card(CardValue.SEVEN, Suit.SPADES)});
    this.assertArrayContainsAll(this.parser.parse("A♠ K♡9♣ T♣ 7♠"),
        new Card[]{new Card(CardValue.ACE, Suit.SPADES),
            new Card(CardValue.KING, Suit.HEARTS), new Card(CardValue.TEN, Suit.CLUBS),
            new Card(CardValue.NINE, Suit.CLUBS),
            new Card(CardValue.SEVEN, Suit.SPADES)});
  }

  @Test(expected = StringHandParserException.class)
  public void parseNull() throws Exception {
    this.parser.parse(null);
  }

  @Test(expected = StringHandParserException.class)
  public void parseInvalidValue() throws Exception {
    this.parser.parse("   10H QS");
  }

  @Test(expected = StringHandParserException.class)
  public void parseInvalidValueUnicode() throws Exception {
    this.parser.parse(" †H QS");
  }

  @Test(expected = StringHandParserException.class)
  public void parseInvalidSuit() throws Exception {
    this.parser.parse("   TH QB");
  }

  @Test(expected = StringHandParserException.class)
  public void parseInvalidSuitUnicode() throws Exception {
    this.parser.parse("   TH Q‿");
  }

  @Test(expected = StringHandParserException.class)
  public void parseIncorrectOrderOfValueAndSuit() throws Exception {
    this.parser.parse("   QS    TH   ♦K");
  }

  @Test(expected = StringHandParserException.class)
  public void parseIncorrectUnicodeCharacter() throws Exception {
    this.parser.parse("\uD83C\uDCD3 KH");
  }
}