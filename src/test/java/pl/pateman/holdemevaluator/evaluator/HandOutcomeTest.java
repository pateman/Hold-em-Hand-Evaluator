package pl.pateman.holdemevaluator.evaluator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import pl.pateman.holdemevaluator.Card;
import pl.pateman.holdemevaluator.IntegrationTest;
import pl.pateman.holdemevaluator.parser.StringHandParser;
import pl.pateman.holdemevaluator.parser.StringHandParserException;

@Category(IntegrationTest.class)
public class HandOutcomeTest {

  private HoldemEvaluator evaluator;
  private StringHandParser handParser;

  private void assertHandComparison(final String handA, final String handB, final String tableCards,
      final boolean whetherHandAWins, final boolean draw) throws StringHandParserException {
    final Card[] parsedHandA = this.handParser.parse(handA);
    final Card[] parsedHandB = this.handParser.parse(handB);
    final Card[] parsedTableCards = this.handParser.parse(tableCards);

    final HandOutcome outcomeA = this.evaluator.calculate(parsedHandA, parsedTableCards);
    final HandOutcome outcomeB = this.evaluator.calculate(parsedHandB, parsedTableCards);
    final int compareTo = outcomeA.compareTo(outcomeB);
    if (whetherHandAWins) {
      assertTrue(compareTo >= 1);
    } else {
      if (draw) {
        assertEquals(compareTo, 0);
      } else {
        assertTrue(compareTo <= -1);
      }
    }
  }

  @Before
  public void setUp() throws Exception {
    this.evaluator = new HoldemEvaluator();
    this.handParser = new StringHandParser();
  }

  @Test
  public void testHandNameComparison() throws Exception {
    //  High card vs high card.
    this.assertHandComparison("AS KH", "2D 3C", "9H 6C TD JC 7H", true, false);
    //  High card vs one pair.
    this.assertHandComparison("AS KH", "2D 2C", "9H 6C TD JC 7H", false, false);
    //  One pair vs one pair.
    this.assertHandComparison("2D 2C", "KH 4H", "9H 6C TD JC KC", false, false);
    //  One pair vs one pair.
    this.assertHandComparison("KD 4S", "KH 4H", "9H 6C TD JC KC", false, true);
    //  One pair vs two pair.
    this.assertHandComparison("KD 4S", "KH TH", "9H 6C TD JC KC", false, false);
    //  Two pair vs two pair.
    this.assertHandComparison("KD TS", "KH 6H", "9H 6C TD JC KC", true, false);
    //  Two pair vs two pair.
    this.assertHandComparison("KD TS", "KH TH", "9H 6C TD JC KC", false, true);
    //  Set vs one pair.
    this.assertHandComparison("KD KS", "KH 4H", "9H 6C TD JC KC", true, false);
    //  Set vs two pair.
    this.assertHandComparison("KD KS", "KH TH", "9H 6C TD JC KC", true, false);
    //  Set vs set.
    this.assertHandComparison("KD KS", "6H 6H", "9H 6C TD JC KC", true, false);
    //  Set vs set.
    this.assertHandComparison("KD KS", "AH AD", "9H 6C TD AC KC", false, false);
    //  Flush vs set.
    this.assertHandComparison("2D 6D", "AH AD", "9D 6D TD AC KC", true, false);
    //  Flush vs flush.
    this.assertHandComparison("2D 6D", "3D AD", "9D 6D TD AC KC", false, false);
    //  Flush vs full house.
    this.assertHandComparison("2D 6D", "AH AD", "9D 6D KD AC KC", false, false);
    //  Full house vs quads.
    this.assertHandComparison("KS KH", "AH AD", "9D AS KD AC 9C", false, false);
    //  Quads vs quads.
    this.assertHandComparison("KS KH", "AH AD", "9D AS KD AC KC", false, false);
    //  Straight flush vs quads.
    this.assertHandComparison("KS QS", "AH AD", "TS AS JS AC KC", true, false);
    //  Royal flush vs straight flush.
    this.assertHandComparison("AS KS", "8S 9S", "TS QS JS AC KC", true, false);
  }

  @Test
  public void testStraightComparison() throws Exception {
    
  }
}